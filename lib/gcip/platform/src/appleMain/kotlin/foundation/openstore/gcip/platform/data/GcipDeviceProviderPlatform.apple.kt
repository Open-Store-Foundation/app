package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.transport.GcipPlatform
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomMac
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.UIUserInterfaceIdiomPhone

actual class GcipDeviceProviderPlatform(
    private val platform: GcipScheme? = null,
) : foundation.openstore.gcip.core.data.GcipDeviceProvider {

    actual override fun isDeviceSecure(): Boolean {
        return true
    }

    actual override fun devicePlatform(): GcipScheme {
        val result = when (UIDevice.currentDevice.userInterfaceIdiom) {
            UIUserInterfaceIdiomPad,
            UIUserInterfaceIdiomPhone -> GcipPlatform.Ios
            UIUserInterfaceIdiomMac -> GcipPlatform.Macos
            else -> throw IllegalStateException("Unknown platform")
        }

        return result.toScheme()
    }
}