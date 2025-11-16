package foundation.openstore.gcip.core.transport

import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.coder.GcipCborCoderDefault
import foundation.openstore.gcip.core.util.getOrCtx
import foundation.openstore.gcip.encryption.EcdhProviderDefault
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GcipEncryptionTest {

    private val store = mutableMapOf<String, ByteArray>()
    private val cbor = GcipCborCoderDefault()

    private val encryption = GcipEncryptionCoderDefault.create(
        object : GcipEncryptionCoder.Delegate {
            override suspend fun getSessionKey(eid: ByteArray): ByteArray? {
                val keys = store[Identifier.encode(eid)]
                return keys
            }
        }
    )

    @BeforeTest
    fun before() {
        store.clear()
    }

    @Test
    fun testSession() = runTest {
        val eid = Identifier.generateBytesUuid()
        val ecdh = EcdhProviderDefault()

        val ecdh1 = ecdh.generatePair()
        val ecdh2 = ecdh.generatePair()

        val sumetric = ecdh.generateSharedSecret(eid, ecdh1.pk, ecdh2.pub)
        val sumetric2 = ecdh.generateSharedSecret(eid, ecdh2.pk, ecdh1.pub)

        assertTrue(sumetric.contentEquals(sumetric2))
    }

    @Test
    fun testHandshake() = runTest {
        val kpClient = encryption.generateExchangePair()
        val kpWallet = encryption.generateExchangePair()

        val eid = Identifier.generateBytesUuid()
        val sessionKey = encryption.generateSessionKey(eid, KeyPair(kpClient.pk, kpWallet.pub))
        store[Identifier.encode(eid)] = sessionKey

        // Sender
        val randomData = "Test Handshake"
        val aad = byteArrayOf(1,2,3)
        val result = encryption.encrypt(
            eid,
            aad,
            randomData.encodeToByteArray()
        ).getOrCtx {
            throw IllegalStateException("Error")
        }
        val message = cbor.encodeEncryptedMessage(eid, params = EncryptionParams(result))

        // Receiver
        val encryptedMessage = cbor.decodeEncryptedMessage(message)
        val decrypt = encryption.decrypt(
            eid = encryptedMessage.eid!!,
            data = encryptedMessage.data,
            iv = encryptedMessage.iv!!,
            aad = aad,
        ).getOrCtx {
            throw IllegalStateException("Error")
        }

        assertEquals(randomData, decrypt.decodeToString())
    }
}
