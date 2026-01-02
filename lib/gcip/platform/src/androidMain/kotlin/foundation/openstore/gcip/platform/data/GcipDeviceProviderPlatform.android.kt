package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.data.GcipDeviceProvider
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.transport.GcipPlatform

actual class GcipDeviceProviderPlatform : GcipDeviceProvider {

    actual override fun isDeviceSecure(): Boolean {
        return true
    }

    actual override fun devicePlatform(): GcipScheme {
        return GcipPlatform.Android.toScheme()
    }
}
