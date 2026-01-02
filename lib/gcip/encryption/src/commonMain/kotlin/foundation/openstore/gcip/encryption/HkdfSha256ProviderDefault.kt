package foundation.openstore.gcip.encryption

import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.HKDF
import dev.whyoleg.cryptography.algorithms.SHA256
import foundation.openstore.gcip.core.encryption.HkdfSha256Provider

class HkdfSha256ProviderDefault : HkdfSha256Provider {

    private val provider = CryptographyProvider.Default

    override suspend fun compute(data: ByteArray, salt: ByteArray, info: ByteArray): ByteArray {
        return provider.get(HKDF)
            .secretDerivation(digest = SHA256, outputSize = 256.bits, salt = salt, info = info)
            .deriveSecretToByteArray(data)
    }
}
