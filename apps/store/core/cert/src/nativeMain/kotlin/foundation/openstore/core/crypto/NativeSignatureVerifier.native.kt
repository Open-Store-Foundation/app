package foundation.openstore.core.crypto

actual class NativeSignatureVerifier : foundation.openstore.core.crypto.SignatureVerifier {
    actual override fun decodeCertificate(raw: ByteArray): kotlin.Result<foundation.openstore.core.crypto.X509> {
        TODO("Not yet implemented")
    }

    actual override fun verify(
        cert: foundation.openstore.core.crypto.X509,
        data: ByteArray,
        signature: ByteArray
    ): Boolean {
        TODO("Not yet implemented")
    }
}