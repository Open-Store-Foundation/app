package foundation.openstore.gcip.transform

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.RIPEMD160
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.algorithms.SHA3_256
import dev.whyoleg.cryptography.algorithms.SHA512
import foundation.openstore.gcip.core.transport.GcipTransformAlgorithm
import foundation.openstore.gcip.transform.algorithms.Ripemd160
import org.kotlincrypto.hash.blake2.BLAKE2b
import org.kotlincrypto.hash.sha3.Keccak256

object GcipTransformer {

    private val cryptography = CryptographyProvider.Default

    @OptIn(DelicateCryptographyApi::class)
    fun hash(data: ByteArray, algorithm: GcipTransformAlgorithm): ByteArray {
        val algorithm = when (algorithm) {
            GcipTransformAlgorithm.Sha256 -> SHA256
            GcipTransformAlgorithm.Sha512 -> SHA512
            GcipTransformAlgorithm.Sha512_256 -> SHA512
            GcipTransformAlgorithm.Sha3_256 -> SHA3_256
            GcipTransformAlgorithm.Ripemd160 -> return Ripemd160().digest(data)
            GcipTransformAlgorithm.Keccak256 -> return Keccak256().digest(data)
            GcipTransformAlgorithm.Blake2b_224 -> return BLAKE2b(224).digest(data)
            GcipTransformAlgorithm.Blake2b_256 -> return BLAKE2b(256).digest(data)
        }

        val hasher = cryptography.get(algorithm)
            .hasher()

        return hasher.hashBlocking(data)
    }


//    fun hmacSha256(pass: ByteArray, data: ByteArray): ByteArray {
//       return cryptography.get(HMAC)
//           .keyDecoder(SHA256)
//           .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, pass)
//           .signatureGenerator()
//           .generateSignatureBlocking(data)
//    }
//
//    fun hmacSha512(pass: ByteArray, data: ByteArray): ByteArray {
//        return cryptography.get(HMAC)
//            .keyDecoder(SHA256)
//            .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, pass)
//            .signatureGenerator()
//            .generateSignatureBlocking(data)
//    }
}
