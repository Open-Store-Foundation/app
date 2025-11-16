package foundation.openstore.gcip.core.transport

import foundation.openstore.gcip.core.ExchangeKey
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.cbor.ByteString
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.cbor.CborLabel
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

internal object CoseHeader {
    const val KTY = 1L
    const val KID = 2L
    const val ALG = 3L
    const val CRV = -1L
    const val X = -2L
    const val Y = -3L
}

enum class CoseKeyType(val value: Int) {
    Okp(1),
    Ec2(2);

    companion object {
        fun from(value: Int): CoseKeyType? = entries.find { it.value == value }
    }
}

@Suppress("ConstPropertyName")
object CoseId {
    // Exchange
    const val EcdhEsHkdf256 = -25

    // Sig
    const val EsSecp256r1 = -7
    const val EcSecp256k1 = -47
    const val EdDsa = -8

    // Curve
    const val CurveP256 = 1
    const val CurveX25519 = 4
    const val CurveEd25519 = 6
    const val CurveSecp256k1 = 8

    // Transform
    const val Sha256 = -16
    const val Sha512 = -43
    const val Sha512_256 = -100
    const val Keccak256 = -101
    const val Sha3_256 = -102
    const val Ripemd160 = -103
    const val Blake2b_224 = -104
    const val Blake2b_256 = -105
}

sealed interface CoseAlgorithm {

    val value: Int

    enum class Exchange(override val value: Int) : CoseAlgorithm {
        EcdhEsHkdf256(CoseId.EcdhEsHkdf256),
        ;
    }

    enum class Sig(override val value: Int) : CoseAlgorithm {
        Es256(CoseId.EsSecp256r1),
        EdDsa(CoseId.EdDsa),
        Es256K(CoseId.EcSecp256k1)
    }

    companion object {
        fun exchange(value: Int): Exchange? = Exchange.entries.find { it.value == value }
        fun sig(value: Int): Sig? = Sig.entries.find { it.value == value }
        fun from(alg: Int): CoseAlgorithm? = exchange(alg) ?: sig(alg)
    }
}

sealed interface CoseCurve {
    val value: Int

    enum class Exchange(override val value: Int) : CoseCurve {
        P256(CoseId.CurveP256),
        X25519(CoseId.CurveX25519),
        ;
    }

    enum class Sig(override val value: Int) : CoseCurve {
        Secp256r1(CoseId.CurveP256),
        Ed25519(CoseId.CurveEd25519),
        Secp256k1(CoseId.CurveSecp256k1),
        ;
    }

    companion object {
        fun exchange(value: Int): Exchange? = Exchange.entries.find { it.value == value }
        fun sig(value: Int): Sig? = Sig.entries.find { it.value == value }
        fun from(alg: CoseAlgorithm): CoseCurve? {
            return when (alg) {
                is CoseAlgorithm.Exchange -> exchange(alg.value)
                is CoseAlgorithm.Sig -> sig(alg.value)
            }
        }
    }
}

@Serializable
class CoseKey(
    @CborLabel(CoseHeader.KTY)
    val kty: Int,

    @CborLabel(CoseHeader.KID)
    @ByteString
    val kid: ByteArray? = null,

    @CborLabel(CoseHeader.ALG)
    val alg: Int,

    @CborLabel(CoseHeader.CRV)
    val crv: Int,

    @CborLabel(CoseHeader.X)
    @ByteString
    val x: ByteArray,

    @CborLabel(CoseHeader.Y)
    @ByteString
    val y: ByteArray? = null
) {
    fun toExchangeKey(): ExchangeKey? {
        val data = when (kty) {
            CoseKeyType.Okp.value -> x
            CoseKeyType.Ec2.value -> {
                if (y == null) {
                    return null
                }

                val output = ByteArray(1 + x.size + y.size)
                output[0] = 0x04
                x.copyInto(output, 1)
                y.copyInto(output, 1 + x.size)

                output
            }
            else -> return null
        }

        return ExchangeKey(data, CoseCurve.exchange(crv)!!) // TODO
    }

    fun toRawPublicKey(): ByteArray? {
        return when (kty) {
            CoseKeyType.Okp.value -> x
            CoseKeyType.Ec2.value -> {
                if (y == null) {
                    return null
                }

                val output = ByteArray(1 + x.size + y.size)
                output[0] = 0x04
                x.copyInto(output, 1)
                y.copyInto(output, 1 + x.size)

                output
            }
            else -> null
        }
    }

    companion object {
        fun exchange(
            pubKey: ByteArray,
            curve: CoseCurve.Exchange,
        ): CoseKey? {
            return when (curve) {
                CoseCurve.Exchange.P256 -> ecdsa(pubKey, curve, alg = CoseAlgorithm.Exchange.EcdhEsHkdf256)
                CoseCurve.Exchange.X25519 -> eddsa(pubKey, curve, alg = CoseAlgorithm.Exchange.EcdhEsHkdf256)
            }
        }

        fun sig(
            pubKey: ByteArray,
            curve: CoseCurve.Sig,
            alg: CoseAlgorithm.Sig? = null
        ): CoseKey? {
            return when (curve) {
                CoseCurve.Sig.Secp256r1 -> eddsa(pubKey, curve, alg = CoseAlgorithm.Sig.Es256)
                CoseCurve.Sig.Ed25519 -> eddsa(pubKey, curve, alg = alg ?: CoseAlgorithm.Sig.EdDsa)
                CoseCurve.Sig.Secp256k1 -> ecdsa(pubKey, curve, alg = CoseAlgorithm.Sig.Es256K)
            }
        }

        private fun eddsa(pubKey: ByteArray, curve: CoseCurve, alg: CoseAlgorithm): CoseKey? {
            if (pubKey.size != 32) {
                return null
            }

            return CoseKey(
                kty = CoseKeyType.Okp.value,
                crv = curve.value,
                x = pubKey,
                alg = alg.value,
                y = null
            )
        }

        private fun ecdsa(pubKey: ByteArray, curve: CoseCurve, alg: CoseAlgorithm): CoseKey? {
            val (x, y) = when (pubKey.size) {
                65 -> {
                    if (pubKey[0] != 0x04.toByte()) {
                        return null
                    }

                    pubKey.copyOfRange(1, 33) to pubKey.copyOfRange(33, 65)
                }
                64 -> {
                    pubKey.copyOfRange(0, 32) to pubKey.copyOfRange(32, 64)
                }
                else -> return null
            }

            return CoseKey(
                kty = CoseKeyType.Ec2.value,
                crv = curve.value,
                x = x,
                y = y,
                alg = alg.value,
            )
        }
    }
}