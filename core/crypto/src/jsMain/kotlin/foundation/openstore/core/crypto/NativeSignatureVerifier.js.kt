package foundation.openstore.core.crypto

actual class NativeSignatureVerifier : SignatureVerifier {

    actual override fun decodeCertificate(raw: ByteArray): Result<X509> = runCatching {
        X509()
    }

    actual override fun verify(
        cert: X509,
        data: ByteArray,
        signature: ByteArray
    ): Boolean {
        return true
    }
}