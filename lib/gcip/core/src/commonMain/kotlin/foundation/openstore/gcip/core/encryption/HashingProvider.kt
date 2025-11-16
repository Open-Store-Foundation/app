package foundation.openstore.gcip.core.encryption

interface HashingProvider {
    fun sha256(data: ByteArray): ByteArray
}
