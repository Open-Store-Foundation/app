package foundation.openstore.core.crypto

import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

actual class NativeSignatureVerifier(
    private val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
) : SignatureVerifier {

    actual override fun decodeCertificate(raw: ByteArray): Result<X509> = runCatching {
        raw.inputStream().use {
            val cert = certificateFactory.generateCertificate(it) as X509Certificate
            X509(cert)
        }
    }

    actual override fun verify(
        cert: X509,
        data: ByteArray,
        signature: ByteArray
    ): Boolean {
        val isValid = runCatching {
            val sig = Signature.getInstance(cert.certificate.sigAlgName)
            sig.initVerify(cert.certificate)
            sig.update(data)
            sig.verify(signature)
        }

        return isValid.getOrDefault(false)
    }
}