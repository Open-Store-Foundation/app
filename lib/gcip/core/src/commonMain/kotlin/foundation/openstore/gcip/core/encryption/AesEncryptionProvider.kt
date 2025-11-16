package foundation.openstore.gcip.core.encryption

interface AesEncryptionProvider {

    companion object {
        const val IV_GCM_SIZE = 12 // bytes
    }

    fun generateIv(): ByteArray
    suspend fun generateAes(): ByteArray
    suspend fun encryptAes(data: ByteArray, key: ByteArray, iv: ByteArray, aad: ByteArray?): ByteArray
    suspend fun decryptAes(data: ByteArray, key: ByteArray, iv: ByteArray, aad: ByteArray?): ByteArray
}