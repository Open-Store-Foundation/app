package foundation.openstore.core.crypto

expect class NativeSignatureVerifier : SignatureVerifier {
    override fun decodeCertificate(raw: ByteArray): Result<X509>
    override fun verify(cert: X509, data: ByteArray, signature: ByteArray): Boolean
}