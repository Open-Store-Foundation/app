package foundation.openstore.gcip.core.handler

import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.data.GcipDeviceProvider
import foundation.openstore.gcip.core.coder.GcipCborCoderDefault
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.encryption.HashingProvider
import foundation.openstore.gcip.core.validator.GcipSignerValidatorDefault

object GcipHandlerFactory {

    fun defaultSignerHandler(
        parti: GcipPartiProvider,
        coder: GcipEncryptionCoder,
        hasher: HashingProvider,
        device: GcipDeviceProvider,
    ): GcipSignerHandler {
        return GcipSignerHandler(
            cbor = GcipCborCoderDefault(),
            validator = GcipSignerValidatorDefault(
                partiProvider = parti,
            ),
            device = device,
            hasher = hasher,
            encryptor = coder,
        )
    }

    fun defaultClientHandler(
        encryption: GcipEncryptionCoder
    ): GcipWalletHandler {
        return GcipWalletHandler(
            cbor = GcipCborCoderDefault(),
            encryptor = encryption,
        )
    }
}