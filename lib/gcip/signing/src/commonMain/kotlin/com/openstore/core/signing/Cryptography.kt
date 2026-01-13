package com.openstore.core.signing

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.openstore.core.signing.Cryptography.Curve.ecdsa
import com.openstore.core.signing.Cryptography.Curve.i32
import com.openstore.core.signing.Cryptography.Curve.wrapWithMaster
import com.openstore.core.signing.utils.fillZeros
import com.openstore.core.signing.utils.toByteArray
import com.openstore.core.signing.utils.use
import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyAlgorithmId
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.CryptographyProviderApi
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.Digest
import dev.whyoleg.cryptography.algorithms.EC
import dev.whyoleg.cryptography.algorithms.ECDSA
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.PBKDF2
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.algorithms.SHA512
import dev.whyoleg.cryptography.random.CryptographyRandom
import foundation.openstore.gcip.core.Algorithm
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.transport.GcipTransformAlgorithm
import foundation.openstore.gcip.core.util.DerivationPath
import foundation.openstore.gcip.transform.GcipTransformer

@Suppress("ArrayInDataClass")
data class AesGcmResult(
    val iv: ByteArray,
    val encryptedData: ByteArray
)

object Cryptography {

    object Hash {

        private val cryptography = CryptographyProvider.Default

        // insensitive
        fun ripemd160(data: ByteArray): ByteArray {
            return GcipTransformer.hash(data, GcipTransformAlgorithm.Ripemd160)
        }
        // insensitive
        fun blake2b224(data: ByteArray): ByteArray {
            return GcipTransformer.hash(data, GcipTransformAlgorithm.Blake2b_224)
        }
        // insensitive
        fun blake2b256(data: ByteArray): ByteArray {
            return GcipTransformer.hash(data, GcipTransformAlgorithm.Blake2b_256)
        }

        fun sha256(data: ByteArray): ByteArray {
            return GcipTransformer.hash(data, GcipTransformAlgorithm.Sha256)
        }

        fun sha512(data: ByteArray): ByteArray {
            return GcipTransformer.hash(data, GcipTransformAlgorithm.Sha512)
        }

        fun keccak256(data: ByteArray): ByteArray {
            return GcipTransformer.hash(data, GcipTransformAlgorithm.Keccak256)
        }

        fun hmacSha256(pass: ByteArray, data: ByteArray): ByteArray {
            return cryptography.get(HMAC)
                .keyDecoder(SHA256)
                .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, pass)
                .signatureGenerator()
                .generateSignatureBlocking(data)
        }

        fun hmacSha512(pass: ByteArray, data: ByteArray): ByteArray {
            return cryptography.get(HMAC)
                .keyDecoder(SHA512)
                .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, pass)
                .signatureGenerator()
                .generateSignatureBlocking(data)
        }

        suspend fun pbkdf2WithHmacSHA512(mnemonic: CharArray, salt: ByteArray): ByteArray {
            return pbkdf2WithHmacSHA512(mnemonic, salt, 2048, 512)
        }

        suspend fun pbkdf2WithHmacSHA512(
            password: CharArray,
            salt: ByteArray,
            iterations: Int,
            keyLengthBits: Int
        ): ByteArray {
            val pbkdf2 = CryptographyProvider.Default.get(PBKDF2)
            return password.toByteArray().use { pass ->
                pbkdf2.secretDerivation(
                    digest = SHA512,
                    iterations = iterations,
                    salt = salt,
                    outputSize = keyLengthBits.bits,
                ).deriveSecretToByteArray(pass)
            }
        }
    }

    val SECP256K1_N = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141".toBigInteger(16)
    val SECP256R1_N = "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551".toBigInteger(16)

    fun privateKey(derivation: Derivation, seed: ByteArray): ByteArray {
        return when (derivation) {
            is Derivation.Algo -> PrivateKey.fromAlgo(derivation.algo, seed)
            is Derivation.Path -> PrivateKey.fromPath(derivation.algo, seed, derivation.path)
            is Derivation.Blob -> PrivateKey.fromBlob(seed, derivation.value)
        }
    }

    fun pubKey(pk: ByteArray, algorithm: Algorithm): ByteArray {
        return when (algorithm) {
            Algorithm.EddsaEd25519 -> Ed25519.pubKey(pk)
            Algorithm.Es256Secp256k1 -> Secp256k1.pubKey(pk)
            Algorithm.Es256Secp256r1 -> Secp256r1.pubKey(pk)
        }
    }

    suspend fun sign(pk: ByteArray, data: ByteArray, algorithm: Algorithm): ByteArray {
        return when (algorithm) {
            Algorithm.EddsaEd25519 -> Ed25519.sign(pk, data)
            Algorithm.Es256Secp256k1 -> Secp256k1.sign(pk, data)
            Algorithm.Es256Secp256r1 -> Secp256r1.sign(pk, data)
        }
    }

    object PrivateKey {
        fun fromAlgo(algo: Algorithm, seed: ByteArray): ByteArray {
            return Hash.hmacSha256(
                pass = seed,
                data = byteArrayOf(algo.value.toByte())
            )
        }

        fun fromBlob(seed: ByteArray, blob: ByteArray): ByteArray {
            return Hash.hmacSha256(
                pass = seed,
                data = blob
            )
        }

        fun fromPath(algorithm: Algorithm, seed: ByteArray, path: String): ByteArray {
            val extended = when (algorithm) {
                Algorithm.EddsaEd25519 -> Ed25519.privateKey(algorithm, seed, path)
                Algorithm.Es256Secp256k1 -> Secp256k1.privateKey(algorithm, seed, path)
                Algorithm.Es256Secp256r1 -> Secp256r1.privateKey(algorithm, seed, path)
            }

            extended.chain.fillZeros()

            return extended.pk
        }
    }

    object Random {
        fun secureRandomBytes(size: Int): ByteArray {
            return CryptographyRandom.nextBytes(size)
        }
    }

    object Aes {

        private val aesGcm by lazy { CryptographyProvider.Default.get(AES.GCM) }

        @OptIn(DelicateCryptographyApi::class)
        suspend fun gcmEncrypt(key: ByteArray, data: ByteArray): AesGcmResult {
            val key = aesGcm.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, key)
            val iv = Random.secureRandomBytes(12)
            val result = key.cipher().encryptWithIv(iv, data)

            return AesGcmResult(iv, result)
        }

        @OptIn(DelicateCryptographyApi::class)
        suspend fun gcmDecrypt(key: ByteArray, iv: ByteArray, encryptedData: ByteArray): ByteArray {
            val key = aesGcm.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, key)
            return key.cipher().decryptWithIv(iv, encryptedData)
        }
    }

    object Secp256r1 {

        @OptIn(CryptographyProviderApi::class)
        private object None : CryptographyAlgorithmId<Digest>("NONE")
        private val ecdsa by lazy { CryptographyProvider.Default.get(ECDSA) }

        fun pubKey(priv32: ByteArray, compressed: Boolean = false): ByteArray {
            return byteArrayOf() // TODO https://github.com/whyoleg/cryptography-kotlin/issues/135
        }

        suspend fun sign(priv32: ByteArray, messageHash32: ByteArray): ByteArray {
            val curve = ecdsa.privateKeyDecoder(EC.Curve.P256)
            val privateKey = curve.decodeFromByteArray(EC.PrivateKey.Format.RAW, priv32)
            return privateKey.signatureGenerator(None, ECDSA.SignatureFormat.RAW)
                .generateSignature(messageHash32)
        }

        fun privateKey(
            algorithm: Algorithm,
            seed: ByteArray,
            path: String,
        ): ExtendedPrivateKey {
            return ecdsa(algorithm, seed, path)
        }
    }

    object Secp256k1 {
        fun pubKey(priv32: ByteArray, compressed: Boolean = false): ByteArray {
            if (compressed) {
                return compressPubKey(PlatformCryptography.Secp256k1.pubKey(priv32))
            } else {
                return PlatformCryptography.Secp256k1.pubKey(priv32)
            }
        }

        fun compressPubKey(publicKey: ByteArray): ByteArray {
            if (publicKey.size == 33) return publicKey
            if (publicKey.size == 65 && publicKey[0] == 0x04.toByte()) {
                val x = publicKey.copyOfRange(1, 33)
                val yLast = publicKey[64]
                val prefix = if ((yLast.toInt() and 1) == 0) 0x02 else 0x03
                return byteArrayOf(prefix.toByte()) + x
            }

            throw IllegalArgumentException("Invalid public key format for compression")
        }

        fun sign(priv32: ByteArray, messageHash32: ByteArray): ByteArray {
            return PlatformCryptography.Secp256k1.sign(priv32, messageHash32)
        }

        fun privateKey(
            algorithm: Algorithm,
            seed: ByteArray,
            path: String,
        ): ExtendedPrivateKey {
            return ecdsa(algorithm, seed, path)
        }
    }

    object Ed25519 {
        fun sign(seed32: ByteArray, message: ByteArray): ByteArray {
            return io.github.andreypfau.curve25519.ed25519.Ed25519.keyFromSeed(seed32)
                .sign(message)
        }

        fun pubKey(seed32: ByteArray): ByteArray {
            return io.github.andreypfau.curve25519.ed25519.Ed25519.keyFromSeed(seed32)
                .publicKey()
                .toByteArray()
        }

        fun privateKey(
            algorithm: Algorithm,
            seed: ByteArray,
            path: String,
        ): ExtendedPrivateKey {
            return wrapWithMaster(algorithm, seed, path) { index, key ->
                val idx = DerivationPath.toSafeHardened(algorithm, index)
                    ?: throw IllegalStateException("Invalid derivation index: $index")

                val data = byteArrayOf(0) + key.pk + i32(idx)
                val I = Hash.hmacSha512(key.chain, data)
                val IL = I.copyOfRange(0, 32)
                val IR = I.copyOfRange(32, 64)

                ExtendedPrivateKey(IL, IR)
            }
        }
    }

    private object Curve {

        fun wrapWithMaster(
            algorithm: Algorithm,
            seed: ByteArray,
            path: String,
            onPath: (UInt, ExtendedPrivateKey) -> ExtendedPrivateKey
        ): ExtendedPrivateKey {
            val master = masterPk(seed, algorithm.curveKey)
            val elems = DerivationPath.parsePath(path)

            var key = ExtendedPrivateKey(master.pk, master.chain)
            for (index in elems) {
                val newKey = onPath(index, key)

                key.pk.fillZeros()
                key.chain.fillZeros()

                key = newKey
            }

            return key
        }

        fun ecdsa(
            algorithm: Algorithm,
            seed: ByteArray,
            path: String,
        ): ExtendedPrivateKey {
            return wrapWithMaster(algorithm, seed, path) { index, key ->
                val hardened = DerivationPath.isHardened(index)

                val data = if (hardened) {
                    byteArrayOf(0) + key.pk + i32(index)
                } else {
                    val pubComp = if (algorithm == Algorithm.Es256Secp256k1) {
                        Secp256k1.pubKey(key.pk, compressed = true)
                    } else {
                        Secp256r1.pubKey(key.pk, compressed = true)
                    }
                    pubComp + i32(index)
                }

                val I = Hash.hmacSha512(key.chain, data)
                val IL = I.copyOfRange(0, 32)
                val IR = I.copyOfRange(32, 64)

                // BIP32: IL must be < n
                val n = if (algorithm == Algorithm.Es256Secp256k1) {
                    SECP256K1_N
                } else {
                    SECP256R1_N
                }

                val ilInt = BigInteger.fromByteArray(IL, Sign.POSITIVE)
                if (ilInt == BigInteger.ZERO || ilInt >= n) {
                    throw IllegalStateException("Invalid derived key: IL >= n")
                }

                // k_child = (IL + k_parent) mod n
                val kParent = BigInteger.fromByteArray(key.pk, Sign.POSITIVE)
                val kChild = (ilInt + kParent).mod(n)

                // convert back to 32 bytes
                val newPK = kChild.toByteArray().let { arr ->
                    if (arr.size == 32) arr
                    else if (arr.size < 32) ByteArray(32 - arr.size) + arr
                    else arr.copyOfRange(arr.size - 32, arr.size)
                }

                ExtendedPrivateKey(newPK, IR)
            }
        }

        fun i32(index: UInt): ByteArray {
            return byteArrayOf(
                (index shr 24).toByte(),
                (index shr 16).toByte(),
                (index shr 8).toByte(),
                index.toByte()
            )
        }

        private fun masterPk(seed: ByteArray, curveKey: String): ExtendedPrivateKey {
            val result = Hash.hmacSha512(
                pass = curveKey.encodeToByteArray(),
                data = seed,
            )

            return ExtendedPrivateKey.from(result)
        }
    }
}
