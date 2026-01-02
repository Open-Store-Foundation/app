package foundation.openstore.gcip.core.validator

import foundation.openstore.gcip.core.ClientRequest
import foundation.openstore.gcip.core.coder.GcipCborCoder
import foundation.openstore.gcip.core.transport.GcipEncryptionMessage
import foundation.openstore.gcip.core.transport.GcipMethod
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult

class GcipCommonValidator(
    private val cbor: GcipCborCoder,
) {
    fun validateResponseMethod(method: GcipMethod, data: ByteArray): GcipResult<GcipEncryptionMessage> {
        return when (method) {
            GcipMethod.ConnectResponse,
            GcipMethod.ExtendResponse,
            GcipMethod.SignResponse,
            GcipMethod.DisconnectResponse,
            GcipMethod.ExchangeResponse -> GcipResult.ok(cbor.decodeEncryptedMessage(data))
            else -> GcipResult.Companion.err(GcipStatus.InvalidMethod)
        }
    }

    fun validateRequestMethod(method: GcipMethod, data: ByteArray): GcipResult<GcipEncryptionMessage> {
        return when (method) {
            GcipMethod.ConnectRequest,
            GcipMethod.ExtendRequest,
            GcipMethod.SignRequest,
            GcipMethod.DisconnectRequest,
            GcipMethod.ExchangeRequest -> GcipResult.ok(cbor.decodeEncryptedMessage(data))
            else -> GcipResult.Companion.err(GcipStatus.InvalidMethod)
        }
    }

    fun requestToResponse(method: GcipMethod): GcipMethod {
        return when (method) {
            GcipMethod.ExchangeRequest -> GcipMethod.ExchangeResponse
            GcipMethod.ConnectRequest -> GcipMethod.ConnectResponse
            GcipMethod.ExtendRequest -> GcipMethod.ExtendResponse
            GcipMethod.SignRequest-> GcipMethod.SignResponse
            GcipMethod.DisconnectRequest -> GcipMethod.DisconnectResponse
            else -> method
        }
    }

    fun requestToMethod(request: ClientRequest): GcipMethod {
        return when (request) {
            is ClientRequest.Exchange -> GcipMethod.ExchangeRequest
            is ClientRequest.Connect -> GcipMethod.ConnectRequest
            is ClientRequest.Sign -> GcipMethod.SignRequest
            is ClientRequest.Extend -> GcipMethod.ExtendRequest
            is ClientRequest.Disconnect -> GcipMethod.DisconnectRequest
        }
    }
}