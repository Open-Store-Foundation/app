package foundation.openstore.gcip.core.builder

import foundation.openstore.gcip.core.Algorithm
import foundation.openstore.gcip.core.ClientRequest
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.core.Transport
import foundation.openstore.gcip.core.Wallet
import foundation.openstore.gcip.core.transport.CoseKey
import foundation.openstore.gcip.core.transport.GcipChallenge
import foundation.openstore.gcip.core.transport.GcipClientData
import foundation.openstore.gcip.core.transport.GcipConnectRequest
import foundation.openstore.gcip.core.transport.GcipConnectResponse
import foundation.openstore.gcip.core.transport.GcipCredentialRequest
import foundation.openstore.gcip.core.transport.GcipCredentialResponse
import foundation.openstore.gcip.core.transport.GcipCredentialType
import foundation.openstore.gcip.core.transport.GcipDerivationRequest
import foundation.openstore.gcip.core.transport.GcipDerivationResponse
import foundation.openstore.gcip.core.transport.GcipDisconnectRequest
import foundation.openstore.gcip.core.transport.GcipDisconnectResponse
import foundation.openstore.gcip.core.transport.GcipExchangeRequest
import foundation.openstore.gcip.core.transport.GcipExchangeResponse
import foundation.openstore.gcip.core.transport.GcipExtendRequest
import foundation.openstore.gcip.core.transport.GcipExtendResponse
import foundation.openstore.gcip.core.transport.GcipRequestType
import foundation.openstore.gcip.core.transport.GcipResponseType
import foundation.openstore.gcip.core.transport.GcipSignRequest
import foundation.openstore.gcip.core.transport.GcipSignResponse
import foundation.openstore.gcip.core.transport.GcipSignerData
import foundation.openstore.gcip.core.transport.GcipSigningAlgorithm
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.transport.GcipTransport
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrError
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray

interface GcipBuilder {
    fun createRequest(request: ClientRequest): GcipRequestType
    fun createResponse(response: CommonResponse): GcipResult<GcipResponseType>
}

class GcipBuilderDefault : GcipBuilder {
    override fun createRequest(request: ClientRequest): GcipRequestType {
        return when (request) {
            is ClientRequest.Exchange -> GcipExchangeRequest(
                transport = request.data.transport.toGcipTransport(),
                meta = request.data.meta?.payload,
            )

            is ClientRequest.Connect -> GcipConnectRequest(
                clientData = GcipClientData(
                    name = request.data.clientData.name.take(GcipConfig.MAX_NAME_LENGTH),
                    origin = request.data.clientData.origin
                ),
                credRequests = request.data.credentials.map { derivation ->
                    GcipCredentialRequest(
                        type = GcipCredentialType.PublicKey,
                        derivations = listOf(derivation.toDerivationRequest())
                    )
                },
                transport = request.data.transport.toGcipTransport(),
                meta = request.data.meta?.payload,
            )

            is ClientRequest.Extend -> GcipExtendRequest(
                connectionId = request.data.connectionId,
                credentials = request.data.credentials.map { derivation ->
                    GcipCredentialRequest(
                        type = GcipCredentialType.PublicKey,
                        derivations = listOf(derivation.toDerivationRequest())
                    )
                },
                meta = request.data.meta?.payload,
            )

            is ClientRequest.Sign -> GcipSignRequest(
                connectionId = request.data.connectionId,
                credId = request.data.credentialId,
                challenge = request.data.challenge.let {
                    GcipChallenge(
                        payload = it.rawData,
                        format = it.format,
                        transforms = it.transforms
                    )
                },
                meta = request.data.meta?.payload,
            )

            is ClientRequest.Disconnect -> GcipDisconnectRequest(
                connectionId = request.data.connectionId,
                meta = request.data.meta?.payload,
            )
        }
    }

    override fun createResponse(response: CommonResponse): GcipResult<GcipResponseType> {
        val data = when (response) {
            is CommonResponse.Exchange -> GcipExchangeResponse()
            is CommonResponse.Connect -> response.data.let { data ->
                GcipConnectResponse(
                    connectionId = data.connectionId,
                    connectionType = response.data.connectionType,
                    signerData = GcipSignerData(
                        name = data.signerData.name,
                        scheme = data.signerData.scheme.value,
                        id = data.signerData.id
                    ),
                    credentials = data.wallets.map { wallet ->
                        wallet.toCredentialResponse()
                            .getOrError { return it }
                    },
                )
            }
            is CommonResponse.Extend -> GcipExtendResponse(
                credentials = response.wallets.map { wallet ->
                    wallet.toCredentialResponse()
                        .getOrError { return it }
                },
                connectionId = response.connectionId,
            )
            is CommonResponse.Sign -> GcipSignResponse(
                signingId = response.signingId,
                sig = response.signature
            )
            is CommonResponse.Disconnect -> GcipDisconnectResponse(
                connectionId = response.connectionId
            )
        }

        return GcipResult.ok(data)
    }
}

private fun Transport.toGcipTransport(): GcipTransport {
    return when (this) {
        Transport.Internal -> GcipTransport(
            type = toGcipTransportType(),
            params = null,
        )
        else -> throw IllegalArgumentException("Unsupported transport type")
    }
}

private fun Wallet.toCredentialResponse(): GcipResult<GcipCredentialResponse> {
    val derivations = credentials.map { credential ->
        val curve = credential.toCoseKey()
            ?: return GcipResult.err(GcipStatus.InvalidFormat)

        GcipDerivationResponse(
            credId = credential.id,
            payload = Cbor.CoseCompliant.encodeToByteArray(curve),
            params = credential.derivation.toGcipDerivationRequest()
        )
    }

    return GcipResult.ok(
        GcipCredentialResponse(
            type = GcipCredentialType.PublicKey,
            namespace = namespace,
            derivations = derivations
        )
    )
}

private fun Derivation.toGcipDerivationRequest(): GcipDerivationRequest {
    return when (this) {
        is Derivation.Algo -> GcipDerivationRequest(alg = algo.toSigningAlgorithm())
        is Derivation.Blob -> GcipDerivationRequest(alg = algo.toSigningAlgorithm(), der = value)
        is Derivation.Path -> GcipDerivationRequest(alg = algo.toSigningAlgorithm(), derPath = path)
    }
}

private fun Derivation.toDerivationRequest(): GcipDerivationRequest {
    val signingAlg = algo.toSigningAlgorithm()
    return when (this) {
        is Derivation.Algo -> GcipDerivationRequest(
            alg = signingAlg,
            der = null,
            derPath = null
        )
        is Derivation.Path -> GcipDerivationRequest(
            alg = signingAlg,
            der = null,
            derPath = path
        )
        is Derivation.Blob -> GcipDerivationRequest(
            alg = signingAlg,
            der = value,
            derPath = null
        )
    }
}

private fun Algorithm.toSigningAlgorithm(): GcipSigningAlgorithm {
    return when (this) {
        Algorithm.Es256Secp256k1 -> GcipSigningAlgorithm.EcSecp256K1
        Algorithm.Es256Secp256r1 -> GcipSigningAlgorithm.EcN256
        Algorithm.EddsaEd25519 -> GcipSigningAlgorithm.Ed25519
    }
}
