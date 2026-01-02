package foundation.openstore.gcip.platform

import android.content.Context
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.encryption.HashingProvider
import foundation.openstore.gcip.platform.data.GcipDeviceProviderPlatform
import foundation.openstore.gcip.platform.data.GcipPartiProviderPlatform
import foundation.openstore.gcip.platform.utils.SignatureProvider

actual object GcipHandlerFactoryPlatform : DefaultFactoryPlatform {

    fun intentHandler(): GcipIntentHandler {
        return GcipIntentHandler()
    }

    fun platformPariProvider(
        context: Context,
        hashing: HashingProvider,
        isEnableOriginValidation: Boolean = true,
    ): GcipPartiProvider {
        return GcipPartiProviderPlatform(
            context = context,
            signature = SignatureProvider(context),
            hashing = hashing,
            isEnableOriginValidation = isEnableOriginValidation,
        )
    }
}