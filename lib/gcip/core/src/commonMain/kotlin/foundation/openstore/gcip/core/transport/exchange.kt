@file:Suppress("ArrayInDataClass")

package foundation.openstore.gcip.core.transport

import foundation.openstore.gcip.core.coder.GcipCborTable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import kotlinx.serialization.cbor.CborLabel

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GcipExchangeRequest(
    @SerialName("transport")
    @CborLabel(GcipCborTable.ExchangeRequest.TRANSPORT)
    val transport: GcipTransport,

    @SerialName("meta")
    @CborLabel(GcipCborTable.ExchangeRequest.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipRequestType

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GcipExchangeResponse(
    @SerialName("meta")
    @CborLabel(GcipCborTable.ExchangeResponse.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipResponseType
