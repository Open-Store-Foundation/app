package foundation.openstore.gcip.core.encryption

interface HkdfSha256Provider {
    suspend fun compute(data: ByteArray, salt: ByteArray, info: ByteArray): ByteArray
}
