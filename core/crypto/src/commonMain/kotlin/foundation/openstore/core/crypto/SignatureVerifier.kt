package foundation.openstore.core.crypto

interface SignatureVerifier  {
    fun decodeCertificate(raw: ByteArray): Result<X509>
    fun verify(cert: X509, data: ByteArray, signature: ByteArray): Boolean
}
