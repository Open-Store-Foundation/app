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
data class GcipSignRequest(
    @SerialName("connectionId")
    @CborLabel(GcipCborTable.SignRequest.CONNECTION_ID)
    @ByteString
    val connectionId: GcipId,

    @SerialName("credId")
    @CborLabel(GcipCborTable.SignRequest.CRED_ID)
    @ByteString
    val credId: GcipId,

    @SerialName("challenge")
    @CborLabel(GcipCborTable.SignRequest.CHALLENGE)
    val challenge: GcipChallenge,

    @SerialName("meta")
    @CborLabel(GcipCborTable.SignRequest.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipRequestType

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GcipSignResponse(
    @SerialName("signingId")
    @CborLabel(GcipCborTable.SignResponse.SIGNING_ID)
    @ByteString
    val signingId: GcipId,

    @SerialName("sig")
    @CborLabel(GcipCborTable.SignResponse.SIG)
    @ByteString
    val sig: ByteArray,

    @SerialName("meta")
    @CborLabel(GcipCborTable.SignResponse.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipResponseType
