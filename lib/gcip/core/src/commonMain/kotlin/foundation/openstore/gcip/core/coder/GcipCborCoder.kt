package foundation.openstore.gcip.core.coder

import foundation.openstore.gcip.core.ClientRequest
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.builder.GcipBuilder
import foundation.openstore.gcip.core.builder.GcipBuilderDefault
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.transport.GcipConnectRequest
import foundation.openstore.gcip.core.transport.GcipConnectResponse
import foundation.openstore.gcip.core.transport.GcipDisconnectRequest
import foundation.openstore.gcip.core.transport.GcipDisconnectResponse
import foundation.openstore.gcip.core.transport.GcipEncryptionMessage
import foundation.openstore.gcip.core.transport.GcipExchangeRequest
import foundation.openstore.gcip.core.transport.GcipExchangeResponse
import foundation.openstore.gcip.core.transport.GcipExtendRequest
import foundation.openstore.gcip.core.transport.GcipExtendResponse
import foundation.openstore.gcip.core.transport.GcipMethod
import foundation.openstore.gcip.core.transport.GcipRequestType
import foundation.openstore.gcip.core.transport.GcipResponseType
import foundation.openstore.gcip.core.transport.GcipSignRequest
import foundation.openstore.gcip.core.transport.GcipSignResponse
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrError
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

interface GcipCborCoder {
    fun encodeClientRequest(request: ClientRequest): ByteArray
    fun decodeSignerRequest(method: GcipMethod, rawData: ByteArray): GcipResult<GcipRequestType>

    fun encodeEncryptedMessage(rawData: ByteArray, iv: ByteArray, encryption: Encryption): GcipResult<ByteArray>
    fun decodeEncryptedMessage(message: ByteArray): GcipEncryptionMessage

    fun encodeResponse(response: CommonResponse): GcipResult<ByteArray>
    fun decodeResponse(method: GcipMethod, rawData: ByteArray): GcipResult<GcipResponseType>
}


@OptIn(ExperimentalSerializationApi::class)
class GcipCborCoderDefault(
    private val builder: GcipBuilder = GcipBuilderDefault()
) : GcipCborCoder {

    private val cbor = Cbor {
        ignoreUnknownKeys = true
        encodeDefaults = true
        preferCborLabelsOverNames = true
        serializersModule = SerializersModule {
            contextual(MethodSerializer)
            contextual(ConnectionTypeSerializer)
            contextual(RepresentationSerializer)
            contextual(SigningAlgorithmSerializer)
            contextual(TransformAlgorithmSerializer)
            contextual(CredentialTypeSerializer)
            contextual(TransportTypeSerializer)
        }
    }

    override fun encodeClientRequest(request: ClientRequest): ByteArray {
        return when (val request = builder.createRequest(request)) {
            is GcipConnectRequest -> cbor.encodeToByteArray(request)
            is GcipDisconnectRequest -> cbor.encodeToByteArray(request)
            is GcipExchangeRequest -> cbor.encodeToByteArray(request)
            is GcipExtendRequest -> cbor.encodeToByteArray(request)
            is GcipSignRequest -> cbor.encodeToByteArray(request)
        }
    }

    override fun decodeSignerRequest(method: GcipMethod, rawData: ByteArray): GcipResult<GcipRequestType> {
        return when (method) {
            GcipMethod.ExchangeRequest -> {
                try {
                    val request = cbor.decodeFromByteArray<GcipExchangeRequest>(rawData)
                    GcipResult.ok(request)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.ConnectRequest -> {
                try {
                    val request = cbor.decodeFromByteArray<GcipConnectRequest>( rawData)
                    GcipResult.ok(request)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.ExtendRequest -> {
                try {
                    val request = cbor.decodeFromByteArray<GcipExtendRequest>( rawData)
                    GcipResult.ok(request)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.SignRequest -> {
                try {
                    val request = cbor.decodeFromByteArray<GcipSignRequest>( rawData)
                    GcipResult.ok(request)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.DisconnectRequest -> {
                try {
                    val request = cbor.decodeFromByteArray<GcipDisconnectRequest>( rawData)
                    GcipResult.ok(request)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            else -> GcipResult.err(GcipStatus.InvalidMethod)
        }
    }

    override fun encodeEncryptedMessage(rawData: ByteArray, iv: ByteArray, encryption: Encryption): GcipResult<ByteArray> {
        val message = when (encryption) {
            is Encryption.Session -> GcipEncryptionMessage(
                eid = encryption.eid,
                iv = iv,
                data = rawData,
                exchangeKey = null,
            )
            is Encryption.Handshake.Request -> {
                val key = encryption.key.toCoseKey() ?: return GcipResult.err(GcipStatus.InvalidFormat)
                GcipEncryptionMessage(
                    eid = null,
                    iv = null,
                    data = rawData,
                    exchangeKey = Cbor.CoseCompliant.encodeToByteArray(key),
                )
            }
            is Encryption.Handshake.Response -> {
                val key = encryption.key.toCoseKey() ?: return GcipResult.err(GcipStatus.InvalidFormat)
                GcipEncryptionMessage(
                    eid = encryption.eid,
                    iv = iv,
                    data = rawData,
                    exchangeKey = Cbor.CoseCompliant.encodeToByteArray(key),
                )
            }
        }

        return GcipResult.ok(cbor.encodeToByteArray(message))
    }

    override fun decodeEncryptedMessage(message: ByteArray): GcipEncryptionMessage {
        return cbor.decodeFromByteArray<GcipEncryptionMessage>(message)
    }

    override fun encodeResponse(response: CommonResponse): GcipResult<ByteArray> {
        val response = builder.createResponse(response)
            .getOrError { return it }

        val result = when (response) { // TODO
            is GcipConnectResponse -> cbor.encodeToByteArray(response)
            is GcipDisconnectResponse -> cbor.encodeToByteArray(response)
            is GcipExchangeResponse -> cbor.encodeToByteArray(response)
            is GcipExtendResponse -> cbor.encodeToByteArray(response)
            is GcipSignResponse -> cbor.encodeToByteArray(response)
        }

        return GcipResult.ok(result)
    }

    override fun decodeResponse(method: GcipMethod, rawData: ByteArray): GcipResult<GcipResponseType> {
        return when (method) {
            GcipMethod.ExchangeResponse -> {
                try {
                    val res = cbor.decodeFromByteArray<GcipExchangeResponse>( rawData)
                    GcipResult.ok(res)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.ConnectResponse -> {
                try {
                    val res = cbor.decodeFromByteArray<GcipConnectResponse>( rawData)
                    GcipResult.ok(res)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.ExtendResponse -> {
                try {
                    val res = cbor.decodeFromByteArray<GcipExtendResponse>( rawData)
                    GcipResult.ok(res)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.SignResponse -> {
                try {
                    val res = cbor.decodeFromByteArray<GcipSignResponse>( rawData)
                    GcipResult.ok(res)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            GcipMethod.DisconnectResponse -> {
                try {
                    val res = cbor.decodeFromByteArray<GcipDisconnectResponse>( rawData)
                    GcipResult.ok(res)
                } catch (e: Throwable) {
                    GcipResult.err(GcipStatus.InvalidFormat, err = e)
                }
            }
            else -> GcipResult.err(GcipStatus.InvalidMethod)
        }
    }
}
