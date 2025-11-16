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
data class GcipExtendRequest(
    @SerialName("connectionId")
    @CborLabel(GcipCborTable.ExtendRequest.CONNECTION_ID)
    @ByteString
    val connectionId: GcipId,

    @SerialName("credentials")
    @CborLabel(GcipCborTable.ExtendRequest.CREDENTIALS)
    val credentials: List<GcipCredentialRequest>,

    @SerialName("meta")
    @CborLabel(GcipCborTable.ExtendRequest.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipRequestType

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GcipExtendResponse(
    @SerialName("credentials")
    @CborLabel(GcipCborTable.ExtendResponse.CREDENTIALS)
    val credentials: List<GcipCredentialResponse>,

    @SerialName("connectionId")
    @CborLabel(GcipCborTable.ExtendResponse.CONNECTION_ID)
    @ByteString
    val connectionId: GcipId,

    @SerialName("meta")
    @CborLabel(GcipCborTable.ExtendResponse.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipResponseType
