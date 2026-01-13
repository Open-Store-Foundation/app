package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import kotlinx.browser.window

import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.CallerData
import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.transport.GcipPlatform
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.util.getOrError

actual class GcipPartiProviderPlatform(
    private val name: String,
    private val origin: String,
) : GcipPartiProvider {

    actual override fun prepareWalletInitialData(caller: String?): GcipResult<CallerData.Initial> {
         val data = prepareWalletRawData(caller)
             .getOrError { return it }

        return GcipResult.ok(
            CallerData.Initial(
                scheme = data.scheme,
                id = data.id,
                signature = data.signatures?.firstOrNull()
            )
        )
    }

    actual override fun prepareWalletRawData(caller: String?): GcipResult<CallerData.Raw> {
        // Platform scheme
        val scheme = try {
            GcipDeviceProviderPlatform().devicePlatform()
        } catch (e: Throwable) {
            GcipPlatform.Web.toScheme()
        }

        if (caller == null) {
            return GcipResult.ok(
                CallerData.Raw(scheme, null, null)
            )
        }

        return GcipResult.ok(
            CallerData.Raw(
                scheme = scheme,
                id = caller,
                signatures = null // No signatures in Web
            )
        )
    }

    actual override fun prepareWalletOrigin(suggestedOrigin: String, caller: String?): GcipResult<String> {
         val url = try {
            js("new URL(suggestedOrigin)")
        } catch (e: dynamic) {
            return GcipResult.err(GcipStatus.InvalidOrigin)
        }

        val protocol = url.protocol as? String
        val scheme = protocol?.removeSuffix(":")?.lowercase()

        if (scheme != "https" && scheme != "http" && scheme != "chrome-extension") {
             return GcipResult.err(GcipStatus.InvalidOrigin)
        }

        return GcipResult.ok(suggestedOrigin)
    }

    actual override fun prepareSignerData(): GcipResult<SignerData> {
         // Current environment info
         val currentOrigin = try {
             window.location.origin
         } catch(e: Throwable) {
             ""
         }

         val identifier = origin.ifBlank { currentOrigin }

        val scheme = try {
            GcipDeviceProviderPlatform().devicePlatform()
        } catch (e: Throwable) {
            GcipPlatform.Web.toScheme()
        }

        return GcipResult.ok(
            SignerData(
                name = name.take(foundation.openstore.gcip.core.GcipConfig.MAX_NAME_LENGTH),
                scheme = scheme,
                id = identifier
            )
        )
    }
}