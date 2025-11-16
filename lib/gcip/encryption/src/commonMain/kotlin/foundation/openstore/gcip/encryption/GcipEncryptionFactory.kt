package foundation.openstore.gcip.encryption

import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.coder.GcipEncryptionCoderDefault

object GcipEncryptionFactory {
    fun create(delegate: GcipEncryptionCoder.Delegate): GcipEncryptionCoder {
        return GcipEncryptionCoderDefault(
            ecdhProvider = EcdhProviderDefault(),
            aesProvider = AesProviderDefault(),
            hfdfSha256Provider = HkdfSha256ProviderDefault(),
            delegate = delegate,
        )
    }
}