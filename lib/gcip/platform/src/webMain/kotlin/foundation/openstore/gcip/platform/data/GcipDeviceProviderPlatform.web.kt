package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.data.GcipDeviceProvider
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.transport.GcipPlatform
import kotlinx.browser.window

actual class GcipDeviceProviderPlatform(
    private val platform: GcipScheme? = null,
) : GcipDeviceProvider {

    val isDom: Boolean get() {
        return jsTypeOf(window) != "undefined"
    }

    actual override fun isDeviceSecure(): Boolean {
        return true
    }

    actual override fun devicePlatform(): GcipScheme {
        return platform
            ?: getHostEnvironment()
    }

    fun getHostEnvironment(): GcipScheme {
        if (!isDom) {
            throw IllegalStateException("Not in browser")
        }

        val protocol = window.location.protocol
        val platform = when {
            // Standard Web Protocols
            protocol.startsWith("http") -> GcipPlatform.Web
            // Extension Protocols
            protocol.startsWith("chrome-extension") -> GcipPlatform.ChromeExtension
            else -> throw IllegalStateException("Unknown platform: $protocol")
        }

        return platform.toScheme()
    }
}
