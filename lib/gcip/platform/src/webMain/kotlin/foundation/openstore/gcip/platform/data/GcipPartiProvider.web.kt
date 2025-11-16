package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import kotlinx.browser.window

import foundation.openstore.gcip.core.GcipScheme

actual class GcipPartiProviderPlatform(
    private val origin: String
) : GcipPartiProvider {

    actual override fun prepareClientIdentifier(caller: String?): GcipResult<String?> {
        if (caller == null) return GcipResult.ok(null)
        // Web caller identifier? Maybe origin?
        return GcipResult.ok(GcipScheme.Defined.Web.sid(caller))
    }

    actual override fun prepareSignerIdentifier(): GcipResult<String> {
         // Current origin
         val currentOrigin = window.location.origin
         val identifier = if (origin.isNotBlank()) origin else currentOrigin
        return GcipResult.ok(GcipScheme.Defined.Web.sid(identifier))
    }

    actual override fun prepareClientOrigin(suggestedOrigin: String, caller: String?): GcipResult<String> {
         val url = try {
            js("new URL(suggestedOrigin)")
        } catch (e: dynamic) {
            return GcipResult.err(GcipStatus.InvalidOrigin)
        }

        val protocol = url.protocol as? String
        val scheme = protocol?.removeSuffix(":")?.lowercase()
        
        if (scheme != "https") {
             return GcipResult.err(GcipStatus.InvalidOrigin)
        }
        
        val host = url.host as? String ?: return GcipResult.err(GcipStatus.InvalidOrigin)

        return GcipResult.ok(suggestedOrigin)
    }
}