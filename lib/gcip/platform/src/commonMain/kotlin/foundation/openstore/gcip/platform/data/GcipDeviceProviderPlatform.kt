package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.data.GcipDeviceProvider

expect class GcipDeviceProviderPlatform : GcipDeviceProvider {
    override fun isDeviceSecure(): Boolean
    override fun devicePlatform(): GcipScheme
}
