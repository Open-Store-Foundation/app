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
data class GcipConnectRequest(
    @SerialName("clientData")
    @CborLabel(GcipCborTable.ConnectRequest.CLIENT_DATA)
    val clientData: GcipClientData,

    @SerialName("credentials")
    @CborLabel(GcipCborTable.ConnectRequest.CRED_REQUESTS)
    val credRequests: List<GcipCredentialRequest>,

    @SerialName("transport")
    @CborLabel(GcipCborTable.ConnectRequest.TRANSPORT)
    val transport: GcipTransport,

    @SerialName("meta")
    @CborLabel(GcipCborTable.ConnectRequest.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipRequestType

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GcipConnectResponse(
    @SerialName("connectionId")
    @CborLabel(GcipCborTable.ConnectResponse.CONNECTION_ID)
    @ByteString
    val connectionId: GcipId,

    @SerialName("connectionType")
    @CborLabel(GcipCborTable.ConnectResponse.CONNECTION_TYPE)
    val connectionType: GcipConnectionType,

    @SerialName("signerData")
    @CborLabel(GcipCborTable.ConnectResponse.SIGNER_DATA)
    val signerData: GcipSignerData,

    @SerialName("credentials")
    @CborLabel(GcipCborTable.ConnectResponse.CREDENTIALS)
    val credentials: List<GcipCredentialResponse>,

    @SerialName("meta")
    @CborLabel(GcipCborTable.ConnectResponse.META)
    @ByteString
    val meta: ByteArray? = null,
) : GcipResponseType

