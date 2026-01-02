package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.CallerData
import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.transport.GcipPlatform
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrError
import platform.Foundation.NSURL
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("GcipPartiProviderPlatform", exact = true)
actual class GcipPartiProviderPlatform(
    private val normalizedBundleId: String
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

    actual override fun prepareWalletRawData(caller: String?): GcipResult<CallerData.Raw> {
        if (caller == null) {
            return GcipResult.ok(
                CallerData.Raw(GcipPlatform.Ios.toScheme(), null, null) // TODO Scheme
            )
        }

        return GcipResult.ok(
            CallerData.Raw(
                scheme = GcipPlatform.Ios.toScheme(),
                id = caller,
                signatures = null
            )
        )
    }

    actual override fun prepareSignerData(): GcipResult<SignerData> {
        return GcipResult.ok(
            SignerData(
                name = normalizedBundleId,
                scheme = GcipPlatform.Ios.toScheme(),
                id = normalizedBundleId,
            )
        )
    }

    actual override fun prepareWalletOrigin(suggestedOrigin: String, caller: String?): GcipResult<String> {
        val url = NSURL.URLWithString(suggestedOrigin)
            ?: return GcipResult.err(GcipStatus.InvalidOrigin)

        val scheme = url.scheme?.lowercase()

        // origin - always HTTPS
        if (scheme != "https") {
            return GcipResult.err(GcipStatus.InvalidOrigin)
        }

        url.host ?: return GcipResult.err(GcipStatus.InvalidOrigin)

        // On iOS we can't easily verify if the caller owns the domain without AASA checks which are OS level.
        // We accept valid HTTPS URLs.
        return GcipResult.ok(data = suggestedOrigin)
    }
}