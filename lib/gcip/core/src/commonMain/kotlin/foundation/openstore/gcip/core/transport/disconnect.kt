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
data class GcipDisconnectRequest(
    @SerialName("connectionId")
    @CborLabel(GcipCborTable.DisconnectRequest.CONNECTION_ID)
    @ByteString
    val connectionId: GcipId,

    @SerialName("meta")
    @CborLabel(GcipCborTable.DisconnectRequest.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipRequestType

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GcipDisconnectResponse(
    @SerialName("connectionId")
    @CborLabel(GcipCborTable.DisconnectResponse.CONNECTION_ID)
    @ByteString
    val connectionId: GcipId,

    @SerialName("meta")
    @CborLabel(GcipCborTable.DisconnectResponse.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipResponseType
