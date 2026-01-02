@file:Suppress("ArrayInDataClass")

package foundation.openstore.gcip.core.transport

import foundation.openstore.gcip.core.coder.DerPathSerializer
import foundation.openstore.gcip.core.coder.GcipCborTable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import kotlinx.serialization.cbor.CborLabel

@Serializable
data class GcipClientData(
    @SerialName("name")
    @CborLabel(GcipCborTable.Common.ClientData.NAME)
    val name: String,

    @SerialName("origin")
    @CborLabel(GcipCborTable.Common.ClientData.ORIGIN)
    val origin: String,
)

@Serializable
data class GcipSignerData(
    @SerialName("name")
    @CborLabel(GcipCborTable.Common.SignerData.NAME)
    val name: String,

    @SerialName("scheme")
    @CborLabel(GcipCborTable.Common.SignerData.SCHEME)
    val scheme: String,

    @SerialName("id")
    @CborLabel(GcipCborTable.Common.SignerData.ID)
    val id: String,
)

@Serializable
data class GcipChallenge(
    @SerialName("payload")
    @CborLabel(GcipCborTable.Common.Challenge.PAYLOAD)
    @ByteString
    val payload: ByteArray,

    @SerialName("fmt")
    @CborLabel(GcipCborTable.Common.Challenge.FMT)
    val format: GcipBinaryFormat? = null,

    @SerialName("transform")
    @CborLabel(GcipCborTable.Common.Challenge.TRANSFORM)
    val transforms: List<GcipTransformAlgorithm>? = null
)

@Serializable
data class GcipCredentialRequest(
    @SerialName("type")
    @CborLabel(GcipCborTable.Common.CredentialRequest.TYPE)
    val type: GcipCredentialType,

    @SerialName("derivations")
    @CborLabel(GcipCborTable.Common.CredentialRequest.PARAMS)
    val derivations: List<GcipDerivationRequest>
)

@Serializable
data class GcipDerivationRequest(
    @SerialName("alg")
    @CborLabel(GcipCborTable.Common.Derivation.ALG)
    val alg: GcipSigningAlgorithm,

    @SerialName("der")
    @CborLabel(GcipCborTable.Common.Derivation.DER)
    @ByteString
    val der: ByteArray? = null,

    @SerialName("derPath")
    @CborLabel(GcipCborTable.Common.Derivation.DER_PATH)
    @Serializable(with = DerPathSerializer::class)
    val derPath: String? = null
)

@Serializable
data class GcipCredentialResponse(
    @SerialName("type")
    @CborLabel(GcipCborTable.Common.CredentialResponse.TYPE)
    val type: GcipCredentialType,

    @SerialName("derivations")
    @CborLabel(GcipCborTable.Common.CredentialResponse.DERIVATIONS)
    val derivations: List<GcipDerivationResponse>,

    @SerialName("namespace")
    @CborLabel(GcipCborTable.Common.CredentialResponse.NAMESPACE)
    val namespace: String? = null,
)

@Serializable
data class GcipDerivationResponse(
    @SerialName("credId")
    @CborLabel(GcipCborTable.Common.KeyDerivation.CRED_ID)
    @ByteString
    val credId: GcipId,

    @SerialName("payload")
    @CborLabel(GcipCborTable.Common.KeyDerivation.PAYLOAD)
    @ByteString
    val payload: ByteArray,

    @SerialName("params")
    @CborLabel(GcipCborTable.Common.KeyDerivation.PARAMS)
    val params: GcipDerivationRequest,
)


@Serializable
class GcipTransport(
    @SerialName("type")
    @CborLabel(GcipCborTable.Common.Transport.TYPE)
    val type: GcipTransportType,

    @SerialName("params")
    @CborLabel(GcipCborTable.Common.Transport.PARAMS)
    @ByteString
    val params: ByteArray? = null
)
