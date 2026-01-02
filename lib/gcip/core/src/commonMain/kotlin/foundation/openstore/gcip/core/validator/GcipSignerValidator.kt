package foundation.openstore.gcip.core.validator

import foundation.openstore.gcip.core.Algorithm
import foundation.openstore.gcip.core.Challenge
import foundation.openstore.gcip.core.ClientData
import foundation.openstore.gcip.core.CredentialRequest
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.core.Meta
import foundation.openstore.gcip.core.SignerBlock
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.gcip.core.Transport
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.transport.GcipChallenge
import foundation.openstore.gcip.core.transport.GcipConnectRequest
import foundation.openstore.gcip.core.transport.GcipCredentialRequest
import foundation.openstore.gcip.core.transport.GcipDerivationRequest
import foundation.openstore.gcip.core.transport.GcipDisconnectRequest
import foundation.openstore.gcip.core.transport.GcipExchangeRequest
import foundation.openstore.gcip.core.transport.GcipExtendRequest
import foundation.openstore.gcip.core.transport.GcipSignRequest
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.transport.GcipTransportType
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrError

interface GcipSignerValidator {

    fun validate(
        block: SignerBlock,
        request: GcipExchangeRequest,
        encryption: Encryption.Handshake.Request,
        caller: String?
    ): GcipResult<SignerRequest.Exchange>

    fun validate(
        block: SignerBlock,
        request: GcipConnectRequest,
        encryption: Encryption.Initiator,
        caller: String?
    ): GcipResult<SignerRequest.Connect>

    fun validate(
        block: SignerBlock,
        request: GcipExtendRequest,
        encryption: Encryption.Session,
        caller: String?
    ): GcipResult<SignerRequest.Extend>

    fun validate(
        blockId: String,
        block: SignerBlock,
        request: GcipSignRequest,
        encryption: Encryption.Session,
        caller: String?
    ): GcipResult<SignerRequest.Sign>

    fun validate(
        block: SignerBlock,
        request: GcipDisconnectRequest,
        encryption: Encryption.Session,
        caller: String?
    ): GcipResult<SignerRequest.Disconnect>
}

class GcipSignerValidatorDefault(
    private val partiProvider: GcipPartiProvider
) : GcipSignerValidator {

    override fun validate(
        block: SignerBlock,
        request: GcipExchangeRequest,
        encryption: Encryption.Handshake.Request,
        caller: String?
    ): GcipResult<SignerRequest.Exchange> {
        return GcipResult.ok(
            SignerRequest.Exchange(
                block = block,
                encryption = encryption,
                transport = when (request.transport.type) {
                    GcipTransportType.Internal -> Transport.Internal
                    else -> return GcipResult.err(GcipStatus.UnsupportedTransport)
                }
            )
        )
    }

    override fun validate(
        block: SignerBlock,
        request: GcipConnectRequest,
        encryption: Encryption.Initiator,
        caller: String?
    ): GcipResult<SignerRequest.Connect> {
        val clientName = request.clientData.name
        if (clientName.isBlank()) {
            return GcipResult.err(GcipStatus.InvalidClientData)
        }

        if (clientName.length > GcipConfig.MAX_NAME_LENGTH) {
            return GcipResult.err(GcipStatus.InvalidClientData)
        }

        val origin = request.clientData.origin
        val validatedOrigin = partiProvider.prepareWalletOrigin(origin, caller)
            .getOrError { return it }

        if (request.credRequests.isEmpty()) {
            return GcipResult.err(GcipStatus.MissingCredentialParams)
        }

        val clientData = partiProvider.prepareWalletInitialData(caller)
            .getOrError { return it }
        
        val signerData = partiProvider.prepareSignerData()
            .getOrError { return it }

        val derivations = request.credRequests.map { credential ->
            credential.toCredentialRequest()
                .getOrError { return it }
        }

        return GcipResult.ok(
            SignerRequest.Connect(
                block = block,
                encryption = encryption,
                derivations = derivations,
                signerData = signerData,
                clientData = ClientData(
                    origin = validatedOrigin,
                    name = clientName,
                ),
                meta = request.meta?.let(::Meta),
                callerData = clientData,
                transport = when (request.transport.type) {
                    GcipTransportType.Internal -> Transport.Internal
                    else -> return GcipResult.err(GcipStatus.UnsupportedTransport)
                }
            )
        )
    }

    override fun validate(
        block: SignerBlock,
        request: GcipExtendRequest,
        encryption: Encryption.Session,
        caller: String?
    ): GcipResult<SignerRequest.Extend> {
        if (request.credentials.isEmpty()) {
            return GcipResult.err(GcipStatus.MissingCredentialParams)
        }

        val derivations = request.credentials.map { credential ->
            credential.toCredentialRequest()
                .getOrError { return it }
        }

        val clientData = partiProvider.prepareWalletRawData(caller)
            .getOrError { return it }

        return GcipResult.ok(
            SignerRequest.Extend(
                block = block,
                encryption = encryption,
                connectionId = request.connectionId,
                derivations = derivations,
                callerData = clientData,
                meta = request.meta?.let(::Meta),
            )
        )
    }

    override fun validate(
        blockId: String,
        block: SignerBlock,
        request: GcipSignRequest,
        encryption: Encryption.Session,
        caller: String?
    ): GcipResult<SignerRequest.Sign> {
        if (request.challenge.payload.isEmpty()) {
            return GcipResult.err(GcipStatus.MissingChallenge)
        }

        val clientData = partiProvider.prepareWalletRawData(caller)
            .getOrError { return it }

        return GcipResult.ok(
            SignerRequest.Sign(
                blockId = blockId,
                block = block,
                challenge = request.challenge.toChallenge(),
                encryption = encryption,
                credentialId = request.credId,
                connectionId = request.connectionId,
                callerData = clientData,
                meta = request.meta?.let(::Meta),
            )
        )
    }

    override fun validate(
        block: SignerBlock,
        request: GcipDisconnectRequest,
        encryption: Encryption.Session,
        caller: String?
    ): GcipResult<SignerRequest.Disconnect> {
        val clientData = partiProvider.prepareWalletRawData(caller)
            .getOrError { return it }

        return GcipResult.ok(
            SignerRequest.Disconnect(
                block = block,
                connectionId = request.connectionId,
                encryption = encryption,
                callerData = clientData,
                meta = request.meta?.let(::Meta),
            )
        )
    }

}

private fun GcipDerivationRequest.toDerivationType(): GcipResult<Derivation> {
    val algorithm = Algorithm.from(this.alg.value)
        ?: return GcipResult.err(GcipStatus.UnknownCredentialParam)

    val derPath = derPath
    val der = der

    val derivation = when {
        derPath != null && der != null -> return GcipResult.err(GcipStatus.InvalidFormat)
        derPath != null -> Derivation.Path(algorithm, derPath)
        der != null -> Derivation.Blob(algorithm, der)
        else -> Derivation.Algo(algorithm)
    }

    return GcipResult.ok(derivation)
}

private fun GcipChallenge.toChallenge(): Challenge {
    return Challenge(
        rawData = payload,
        transforms = transforms ?: emptyList(),
        format = format
    )
}

private fun GcipCredentialRequest.toCredentialRequest(): GcipResult<CredentialRequest> {
    return GcipResult.ok(
        CredentialRequest(
            type = type,
            credentials = derivations.map { derivation ->
                derivation.toDerivationType()
                    .getOrError { return it }
            }
        )
    )
}
