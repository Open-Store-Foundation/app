package foundation.openstore.gcip.encryption

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.random.CryptographyRandom
import foundation.openstore.gcip.core.encryption.AesEncryptionProvider

class AesProviderDefault : AesEncryptionProvider {

    private val provider = CryptographyProvider.Default

    override suspend fun generateAes(): ByteArray {
        val key = provider.get(AES.GCM)
            .keyGenerator(AES.Key.Size.B256)
            .generateKey()

        return key.encodeToByteArray(AES.Key.Format.RAW)
    }

    override fun generateIv(): ByteArray {
        return CryptographyRandom.nextBytes(AesEncryptionProvider.IV_GCM_SIZE)
    }

    @OptIn(DelicateCryptographyApi::class)
    override suspend fun encryptAes(data: ByteArray, key: ByteArray, iv: ByteArray, aad: ByteArray?): ByteArray {
        val aesKey = provider.get(AES.GCM)
            .keyDecoder()
            .decodeFromByteArray(AES.Key.Format.RAW, key)

        val cipher = aesKey.cipher()
        val ciphertext = cipher.encryptWithIv(iv, data, aad)

        return ciphertext
    }

    @OptIn(DelicateCryptographyApi::class)
    override suspend fun decryptAes(data: ByteArray, key: ByteArray, iv: ByteArray, aad: ByteArray?): ByteArray {
        val aesKey = provider.get(AES.GCM)
            .keyDecoder()
            .decodeFromByteArray(AES.Key.Format.RAW, key)

        val cipher = aesKey.cipher()


        return cipher.decryptWithIv(iv, data, aad)
    }
}
