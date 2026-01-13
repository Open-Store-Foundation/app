package foundation.openstore.gcip.core.transport

import foundation.openstore.gcip.core.Blockchain
import foundation.openstore.gcip.core.ClientData
import foundation.openstore.gcip.core.ClientRequest
import foundation.openstore.gcip.core.ClientRequestData
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.ExchangeKey
import foundation.openstore.gcip.core.Credential
import foundation.openstore.gcip.core.SignerBlock
import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.Transport
import foundation.openstore.gcip.core.Wallet
import foundation.openstore.gcip.core.coder.GcipCborCoderDefault
import foundation.openstore.gcip.core.coder.GcipEncryptionCoderDefault
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.util.getOrCtx
import foundation.openstore.gcip.core.util.getOrNull
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GcipSerializationTest {

    private val parser = GcipCborCoderDefault()

    @Test
    fun testConnectRequest() {
        val request = ClientRequest.Connect(
            data = ClientRequestData.Connect(
                clientData = ClientData(
                    name = "Simple Wallet",
                    origin = "https://example.com"
                ),
                credentials = listOf(
                    Blockchain.Bitcoin.toDerivationType()
                ),
                eid = null,
                transport = Transport.Internal,
            ),
            encryption = Encryption.Handshake.Request(ExchangeKey(byteArrayOf(1))),
            nonce = 1u
        )
        val encoded = parser.encodeClientRequest(request)
        val decoded = parser.decodeSignerRequest(GcipMethod.ConnectRequest, encoded)
            .getOrCtx {
                println(it.error)
                it.e?.printStackTrace()
                throw it.e ?: RuntimeException("Unknown error")
            }

        println(decoded)
        assertTrue(decoded is GcipConnectRequest)
        assertEquals(request.data.credentials.size, decoded.credRequests.size)
        assertEquals(request.data.clientData.origin, decoded.clientData.origin)
        assertEquals(request.data.clientData.name, decoded.clientData.name)
    }

    @Test
    fun testCoseKeySerialization() {
        val key = CoseKey(
            alg = CoseAlgorithm.Sig.Es256.value,
            kty = CoseKeyType.Ec2.value,
            crv = CoseCurve.Sig.Secp256r1.value,
            x = ByteArray(32) { 1 },
            y = ByteArray(32) { 2 },
            kid = ByteArray(16) { 3 }
        )

        val encoded = Cbor.CoseCompliant.encodeToByteArray( key)
        val decoded = Cbor.CoseCompliant.decodeFromByteArray<CoseKey>(encoded)

        assertEquals(key.alg, decoded.alg)
        assertEquals(key.kty, decoded.kty)
        assertEquals(key.crv, decoded.crv)
    }

    @Test
    fun testConnectResponse() {
        val response = CommonResponse.Connect.Handshake(
            block = SignerBlock(
                version = 1.toUByte(),
                status = GcipStatus.Success,
                nonce = 1u,
                method = GcipMethod.ConnectResponse,
                data = byteArrayOf()
            ),
            data = CommonResponse.Connect.ConnectData(
                connectionId = GcipId.generate(),
                signerData = SignerData(name = "Signer", scheme = GcipPlatform.Android.toScheme(), id = "com.example.app"),
                wallets = listOf(
                    Wallet(
                        namespace = "Test",
                        credentials = listOf(
                            Credential(
                                id = GcipId.generate(),
                                pubkey = "BGsX0fLhLEJH-lzm5WOkQPJ3A32BLeszoPShOUXYiMJk_jQuL-Gn-bjufrSnwPnhYrzjNXazFezsu2QGg3v1H1U",
                                derivation = Blockchain.Bitcoin.toDerivationType()
                            )
                        ),
                    )
                ),
                connectionType = GcipConnectionType.Device
            ),
            encryption = Encryption.Handshake.Response(
                eid = GcipId.generate(),
                key = ExchangeKey(byteArrayOf(1))
            ),
            meta = null
        )



        val encoded = parser.encodeResponse(response).getOrNull()!!
        println(encoded)
       val decoded = parser.decodeResponse(GcipMethod.ConnectResponse, encoded)
           .getOrNull()!!

        println(decoded)
        assertTrue(decoded is GcipConnectResponse)
    }

//    @Test
//    fun testDerivationPathEncoder() {
//        val encode = DerivationPath.encode(Blockchain.Bitcoin.derivationPath)
//        val decode = DerivationPath.decode(encode)
//        assertEquals(Blockchain.Bitcoin.derivationPath, decode)
//    }
}
