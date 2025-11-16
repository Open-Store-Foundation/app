package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.data.GcipDeviceProvider
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.transport.GcipPlatform

actual class GcipDeviceProviderPlatform(
    private val platform: GcipScheme? = null,
) : GcipDeviceProvider {
    actual override fun isDeviceSecure(): Boolean {
        return true
    }

    actual override fun devicePlatform(): GcipScheme {
        return platform
            ?: getPlatform()
            ?: throw IllegalStateException("Platform not defined")
    }

    private fun getPlatform(): GcipScheme? {
        val os = System.getProperty("os.name").lowercase()
        val result =  when {
            os.contains("win") -> GcipPlatform.Windows
            os.contains("mac") -> GcipPlatform.Macos
            os.contains("nix") || os.contains("nux") || os.contains("aix") -> GcipPlatform.Linux
            else -> null
        }

        return result?.toScheme()
    }
}
