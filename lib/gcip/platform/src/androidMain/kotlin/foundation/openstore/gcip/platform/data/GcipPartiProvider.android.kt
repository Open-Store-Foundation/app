package foundation.openstore.gcip.platform.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import foundation.openstore.gcip.core.CallerData
import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.encryption.HashingProvider
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.transport.GcipPlatform
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrError
import foundation.openstore.gcip.core.util.toUrlBase64Fmt
import foundation.openstore.gcip.platform.utils.SignatureProvider

actual class GcipPartiProviderPlatform(
    private val context: Context,
    private val signature: SignatureProvider,
    private val hashing: HashingProvider,
    private val isEnableOriginValidation: Boolean = true,
) : GcipPartiProvider {

    actual override fun prepareWalletInitialData(caller: String?): GcipResult<CallerData.Initial> {
        val data = prepareWalletRawData(caller)
            .getOrError { return it }

        return GcipResult.ok(
            CallerData.Initial(
                scheme = data.scheme,
                id = data.id,
                signature = data.signatures?.firstOrNull(),
            )
        )
    }

    @SuppressLint("UseKtx")
    actual override fun prepareWalletRawData(caller: String?): GcipResult<CallerData.Raw> {
        if (caller == null) {
            return GcipResult.ok(
                CallerData.Raw(GcipPlatform.Android.toScheme(), null, null)
            )
        }

        val fingers = getCallingAppCertFingerprints(caller)
            .getOrError { return it }

        return GcipResult.ok(
            CallerData.Raw(
                scheme = GcipPlatform.Android.toScheme(),
                id = caller,
                signatures = fingers
            )
        )
    }

    actual override fun prepareSignerData(): GcipResult<SignerData> {
        return GcipResult.ok(
            SignerData(
                name = context.applicationInfo.name.take(GcipConfig.MAX_NAME_LENGTH),
                scheme = GcipPlatform.Android.toScheme(),
                id = context.packageName,
            )
        )
    }

    actual override fun prepareWalletOrigin(suggestedOrigin: String, caller: String?): GcipResult<String> {
        if (!isEnableOriginValidation) {
            return GcipResult.ok(suggestedOrigin)
        }

        val uri = Uri.parse(suggestedOrigin)
        val scheme = uri.scheme?.lowercase()

        // origin - always HTTPS
        if (scheme != "https") {
            return GcipResult.err(GcipStatus.InvalidOrigin)
        }

        // 1. Check if WE (the wallet) claim this domain
        checkHttpOrigin(uri, context.packageName)
            .getOrError { return it }

        // 2. Check if the CALLER claims this domain
        if (caller.isNullOrBlank()) {
            return GcipResult.err(GcipStatus.UnknownCaller)
        }

        checkHttpOrigin(uri, caller)
            .getOrError { return it }

        return GcipResult.ok(suggestedOrigin)
    }

    private fun getCallingAppCertFingerprints(callingPackage: String): GcipResult<List<String>> {
        val signatures = try {
            signature.getSignatures(callingPackage)
        } catch (e: SecurityException) {
            return GcipResult.err(GcipStatus.UnknownError, err = e)
        }

        return GcipResult.ok(
            signatures
                .map { signature ->
                    hashing.sha256(signature.toByteArray())
                        .toUrlBase64Fmt()
                }
        )
    }

    private fun checkHttpOrigin(uri: Uri, packageName: String): GcipResult<String> {
        val host = uri.host
            ?: return GcipResult.err(GcipStatus.InvalidOrigin)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val domainVerificationManager = context.getSystemService(DomainVerificationManager::class.java)
                ?: return GcipResult.err(GcipStatus.PlatformApiError)


            val userState = try {
                domainVerificationManager.getDomainVerificationUserState(packageName)
            } catch (e: Exception) {
                return GcipResult.err(GcipStatus.UnknownCaller, err = e)
            } ?: return GcipResult.err(GcipStatus.InvalidOrigin)

            val domainState = userState.hostToStateMap[host]
            if (domainState != DomainVerificationUserState.DOMAIN_STATE_VERIFIED) {
                return GcipResult.err(GcipStatus.InvalidOrigin)
            }

            return GcipResult.ok(data = host)
        } else {
            // On older Androids, we check if the package handles the intent for this URI
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                `package` = packageName
            }

            context.packageManager.resolveActivity(intent, 0)
                ?: return GcipResult.err(GcipStatus.InvalidOrigin)

            return GcipResult.ok(data = host)
        }
    }
}
