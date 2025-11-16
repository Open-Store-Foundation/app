package foundation.openstore.gcip.core.coder

import foundation.openstore.gcip.core.encryption.AesEncryptionProvider
import foundation.openstore.gcip.core.encryption.EcdhEncryptionProvider
import foundation.openstore.gcip.core.encryption.HkdfSha256Provider
import foundation.openstore.gcip.core.encryption.KeyPair
import foundation.openstore.gcip.core.transport.CoseCurve
import foundation.openstore.gcip.core.transport.GcipEncryptionAlgorithm
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.util.GcipResult

interface GcipEncryptionCoder {

    interface Delegate {
        suspend fun getHandshakePrivateKey(pubKey: ByteArray): ByteArray? = null
        suspend fun getSessionKey(eid: GcipId): ByteArray?
    }

    fun generateIv(
        algo: GcipEncryptionAlgorithm = GcipEncryptionAlgorithm.Aes256Gcm,
    ): ByteArray

    fun generateAad(
        signature: ByteArray,
        eid: GcipId,
    ): ByteArray

    suspend fun generateExchangePair(
        algo: CoseCurve.Exchange = CoseCurve.Exchange.P256,
    ): KeyPair

    suspend fun generateSessionKey(
        eid: GcipId,
        keyPair: KeyPair,
        algo: CoseCurve.Exchange = CoseCurve.Exchange.P256,
    ): ByteArray

    suspend fun encrypt(
        eid: GcipId,
        iv: ByteArray,
        aad: ByteArray,
        data: ByteArray,
        algo: GcipEncryptionAlgorithm = GcipEncryptionAlgorithm.Aes256Gcm,
    ): GcipResult<ByteArray>

    suspend fun decrypt(
        eid: GcipId,
        iv: ByteArray,
        aad: ByteArray,
        data: ByteArray,
        algo: GcipEncryptionAlgorithm = GcipEncryptionAlgorithm.Aes256Gcm,
    ): GcipResult<ByteArray>

    suspend fun decrypt(
        eid: GcipId,
        iv: ByteArray,
        aad: ByteArray,
        data: ByteArray,
        ekey: ByteArray?,
        algo: GcipEncryptionAlgorithm = GcipEncryptionAlgorithm.Aes256Gcm,
    ): GcipResult<ByteArray>
}


class GcipEncryptionCoderDefault(
    private val ecdhProvider: EcdhEncryptionProvider,
    private val aesProvider: AesEncryptionProvider,
    private val hfdfSha256Provider: HkdfSha256Provider,
    private val delegate: GcipEncryptionCoder.Delegate,
) : GcipEncryptionCoder {

    companion object Companion {
        private val HkdfInfo =  "GCIP/1/Session/AES-256-GCM".encodeToByteArray()
    }

    override fun generateAad(
        signature: ByteArray,
        eid: GcipId,
    ): ByteArray {
        return signature + eid.value
    }

    override fun generateIv(
        algo: GcipEncryptionAlgorithm
    ): ByteArray {
        return aesProvider.generateIv()
    }

    override suspend fun generateExchangePair(algo: CoseCurve.Exchange): KeyPair {
        return when (algo) {
            CoseCurve.Exchange.P256 -> ecdhProvider.generatePair()
            CoseCurve.Exchange.X25519 -> TODO()
        }
    }

    override suspend fun generateSessionKey(
        eid: GcipId,
        keyPair: KeyPair,
        algo: CoseCurve.Exchange,
    ): ByteArray {
        val shared = when (algo) {
            CoseCurve.Exchange.P256 -> ecdhProvider.generateSharedSecret(
                eid = eid,
                privateKey = keyPair.pk,
                publicKey = keyPair.pub,
            )
            CoseCurve.Exchange.X25519 -> TODO()
        }

        return hfdfSha256Provider.compute(data = shared, salt = eid.value, info = HkdfInfo)
    }

    override suspend fun encrypt(
        eid: GcipId,
        iv: ByteArray,
        aad: ByteArray,
        data: ByteArray,
        algo: GcipEncryptionAlgorithm,
    ): GcipResult<ByteArray> {
        val sessionKey = delegate.getSessionKey(eid)
            ?: return GcipResult.err(GcipStatus.UnknownSession)

        return when (algo) {
            GcipEncryptionAlgorithm.Aes256Gcm -> {
                val result = try {
                    aesProvider.encryptAes(
                        data = data,
                        key = sessionKey,
                        aad = aad,
                        iv = iv,
                    )
                } catch (e: Throwable) {
                    return GcipResult.err(GcipStatus.EncryptionError, err = e)
                }

                GcipResult.ok(result)
            }
        }
    }

    override suspend fun decrypt(
        eid: GcipId,
        iv: ByteArray,
        aad: ByteArray,
        data: ByteArray,
        algo: GcipEncryptionAlgorithm,
    ): GcipResult<ByteArray> {
        return decrypt(eid, iv, aad, data, ekey = null, algo)
    }

    override suspend fun decrypt(
        eid: GcipId,
        iv: ByteArray,
        aad: ByteArray,
        data: ByteArray,
        ekey: ByteArray?,
        algo: GcipEncryptionAlgorithm,
    ): GcipResult<ByteArray> {
        val sessionKey = if (ekey != null) {
            delegate.getHandshakePrivateKey(pubKey = ekey)
                ?.let { pk -> generateSessionKey(eid = eid, keyPair = KeyPair(pk = pk, pub = ekey)) }
                ?: return GcipResult.err(GcipStatus.UnknownSession)
        } else {
            delegate.getSessionKey(eid)
                ?: return GcipResult.err(GcipStatus.UnknownSession)
        }

        return when (algo) {
            GcipEncryptionAlgorithm.Aes256Gcm -> {
                if (iv.size != AesEncryptionProvider.IV_GCM_SIZE) {
                    return GcipResult.err(GcipStatus.InvalidFormat)
                }

                val result = try {
                    aesProvider.decryptAes(data = data, key = sessionKey, iv = iv, aad = aad)
                } catch (e: Throwable) {
                    return GcipResult.err(GcipStatus.EncryptionError, err = e)
                }

                GcipResult.ok(result)
            }
        }
    }
}
