package foundation.openstore.gcip.core.validator

import foundation.openstore.gcip.core.Algorithm
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.Credential
import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.Meta
import foundation.openstore.gcip.core.SignerBlock
import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.Wallet
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.transport.CoseKey
import foundation.openstore.gcip.core.transport.GcipConnectResponse
import foundation.openstore.gcip.core.transport.GcipCredentialResponse
import foundation.openstore.gcip.core.transport.GcipDerivationResponse
import foundation.openstore.gcip.core.transport.GcipDisconnectResponse
import foundation.openstore.gcip.core.transport.GcipExchangeResponse
import foundation.openstore.gcip.core.transport.GcipExtendResponse
import foundation.openstore.gcip.core.transport.GcipSignResponse
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrError
import foundation.openstore.gcip.core.util.toUrlBase64Fmt
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray

interface GcipWalletValidator {

    fun validate(
        block: SignerBlock,
        response: GcipExchangeResponse,
        encryption: Encryption.Handshake.Response
    ): GcipResult<CommonResponse.Exchange>

    fun validate(
        block: SignerBlock,
        response: GcipConnectResponse,
        encryption: Encryption.Handshake.Response
    ): GcipResult<CommonResponse.Connect>

    fun validate(
        block: SignerBlock,
        response: GcipExtendResponse,
        encryption: Encryption.Session,
    ): GcipResult<CommonResponse.Extend>

    fun validate(
        block: SignerBlock,
        response: GcipSignResponse,
        encryption: Encryption.Session,
    ): GcipResult<CommonResponse.Sign>

    fun validate(
        block: SignerBlock,
        response: GcipDisconnectResponse,
        encryption: Encryption.Session,
    ): GcipResult<CommonResponse.Disconnect>
}

class GcipWalletValidatorDefault : GcipWalletValidator {

    override fun validate(
        block: SignerBlock,
        response: GcipExchangeResponse,
        encryption: Encryption.Handshake.Response
    ): GcipResult<CommonResponse.Exchange> {
        return GcipResult.ok(
            CommonResponse.Exchange(
                block = block,
                encryption = encryption,
                meta = response.meta?.let(::Meta),
            )
        )
    }

    override fun validate(
        block: SignerBlock,
        response: GcipConnectResponse,
        encryption: Encryption.Handshake.Response
    ): GcipResult<CommonResponse.Connect> {
        return GcipResult.ok(
            CommonResponse.Connect.Handshake(
                block = block,
                data = response.toConnectData().getOrError { return it },
                encryption = encryption,
                meta = response.meta?.let(::Meta),
            )
        )
    }

    override fun validate(
        block: SignerBlock,
        response: GcipExtendResponse,
        encryption: Encryption.Session,
    ): GcipResult<CommonResponse.Extend> {
        return GcipResult.ok(
            CommonResponse.Extend(
                block = block,
                wallets = response.credentials.map { cred ->
                    cred.toGcipWallet().getOrError { return it }
                },
                encryption = encryption,
                connectionId = response.connectionId,
                meta = response.meta?.let(::Meta),
            )
        )
    }

    override fun validate(
        block: SignerBlock,
        response: GcipSignResponse,
        encryption: Encryption.Session,
    ): GcipResult<CommonResponse.Sign> {
        return GcipResult.ok(
            CommonResponse.Sign(
                block = block,
                signingId = response.signingId,
                signature = response.sig,
                encryption = encryption,
                meta = response.meta?.let(::Meta),
            )
        )
    }

    override fun validate(
        block: SignerBlock,
        response: GcipDisconnectResponse,
        encryption: Encryption.Session,
    ): GcipResult<CommonResponse.Disconnect> {
        return GcipResult.ok(
            CommonResponse.Disconnect(
                block = block,
                connectionId = response.connectionId,
                encryption = encryption,
                meta = response.meta?.let(::Meta),
            )
        )
    }

}

private fun GcipCredentialResponse.toGcipWallet(): GcipResult<Wallet> {
    return GcipResult.ok(
        Wallet(
            namespace = namespace,
            credentials = derivations.map { der ->
                der.toGcipCredential()
                    .getOrError { return it }
            }
        )
    )
}

private fun GcipDerivationResponse.toGcipCredential(): GcipResult<Credential> {
    val algorithm = Algorithm.from(params.alg.value)
        ?: return GcipResult.err(GcipStatus.UnsupportedAlgorithm)

    if (params.der != null && params.derPath != null) {
        return GcipResult.err(GcipStatus.InvalidFormat)
    }

    val pubkey = Cbor.CoseCompliant.decodeFromByteArray<CoseKey>(payload)
        .toRawPublicKey()
        ?.toUrlBase64Fmt() // TODO
        ?: return GcipResult.err(GcipStatus.InvalidFormat)

    return GcipResult.ok(
        Credential(
            id = credId,
            pubkey = pubkey,
            derivation = when {
                params.derPath != null -> Derivation.Path(algorithm, params.derPath)
                params.der != null -> Derivation.Blob(algorithm, params.der)
                else -> Derivation.Algo(algorithm)
            },
        )
    )
}

private fun GcipConnectResponse.toConnectData(): GcipResult<CommonResponse.Connect.ConnectData> {
    val response = this

    val signer = SignerData(
        name = response.signerData.name,
        scheme = GcipScheme.from(response.signerData.scheme),
        id = response.signerData.id
    )

    return GcipResult.ok(
        CommonResponse.Connect.ConnectData(
            connectionId = response.connectionId,
            signerData = signer,
            wallets = response.credentials.map { cred ->
                cred.toGcipWallet().getOrError { return it }
            },
            connectionType = response.connectionType,
        )
    )
}
