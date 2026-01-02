package foundation.openstore.gcip.core.data

import foundation.openstore.gcip.core.GcipScheme

interface GcipDeviceProvider {

    companion object Companion {
        fun empty(scheme: GcipScheme): GcipDeviceProvider {
            return object : GcipDeviceProvider {
                override fun isDeviceSecure(): Boolean {
                    return true
                }

                override fun devicePlatform(): GcipScheme {
                    return scheme
                }
            }
        }
    }

    fun isDeviceSecure(): Boolean
    fun devicePlatform(): GcipScheme
}