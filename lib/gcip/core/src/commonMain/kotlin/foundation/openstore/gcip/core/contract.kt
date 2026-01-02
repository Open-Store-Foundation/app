package foundation.openstore.gcip.core

import foundation.openstore.gcip.core.GcipConfig.MAX_SCHEME_LENGTH
import foundation.openstore.gcip.core.coder.GcipBlock
import foundation.openstore.gcip.core.transport.CoseCurve
import foundation.openstore.gcip.core.transport.CoseKey
import foundation.openstore.gcip.core.transport.GcipBinaryFormat
import foundation.openstore.gcip.core.transport.GcipConnectionType
import foundation.openstore.gcip.core.transport.GcipCredentialType
import foundation.openstore.gcip.core.transport.GcipPlatform
import foundation.openstore.gcip.core.transport.GcipMethod
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.transport.GcipTransformAlgorithm
import foundation.openstore.gcip.core.transport.GcipTransportType
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.util.fromUrlBase64
import foundation.openstore.gcip.core.util.toUrlBase64Fmt

class SignerBlock(
    val version: UByte,
    val status: GcipStatus,
    val nonce: Short,
    val method: GcipMethod,
    val data: ByteArray,
) {
    fun toGcip(): GcipBlock.Encode {
        return GcipBlock.Encode(
            version = version,
            status = status,
            nonce = nonce,
            method = method,
            data = data
        )
    }
}

class ExchangeKey(
    val payload: ByteArray,
    val algo: CoseCurve.Exchange = CoseCurve.Exchange.P256,
) {
    fun toCoseKey(): CoseKey? {
        return CoseKey.exchange(payload, algo)
    }
}


sealed interface Encryption {

    sealed interface Initiator : Encryption

    sealed interface Replier : Encryption {
        val eid: GcipId
        val key: ExchangeKey? get() = null
    }

    sealed interface Handshake : Encryption {
        class Request(
            val key: ExchangeKey,
        ) : Handshake, Initiator

        class Response(
            override val key: ExchangeKey,
            override val eid: GcipId,
        ) : Handshake, Replier
    }

    class Session(
        override val eid: GcipId,
    ) : Encryption, Initiator, Replier
}

interface HandshakeRequestEncryption {
    val encryption: Encryption.Handshake.Request?
}

interface SessionEncryption {
    val encryption: Encryption.Session
}

interface InitialEncryption {
    val encryption: Encryption.Initiator
}

interface HandshakeResponseEncryption {
    val encryption: Encryption.Handshake.Response
}

class Challenge(
    val rawData: ByteArray,
    val transforms: List<GcipTransformAlgorithm> = emptyList(),
    val format: GcipBinaryFormat? = null
) {
    val displayData by lazy {
        when (format) {
            GcipBinaryFormat.Utf8 -> rawData.decodeToString()
            GcipBinaryFormat.Base64Url -> rawData.toUrlBase64Fmt()
            GcipBinaryFormat.Hex,
            null -> "0x${rawData.toHexString(HexFormat.Default)}"
        }
    }
}

data class Wallet(
    val namespace: String? = null,
    val credentials: List<Credential>,
)

data class CredentialRequest(
    val type: GcipCredentialType,
    val credentials: List<Derivation>,
)

data class Credential(
    val id: GcipId,
    val pubkey: String,
    val derivation: Derivation,
) {
    fun toCoseKey(): CoseKey? {
        return CoseKey.sig(
            pubKey = pubkey.fromUrlBase64(),
            curve = derivation.algo.curve,
        )
    }
}

sealed class GcipScheme(
    val value: String
) {

    class Defined(val scheme: GcipPlatform) : GcipScheme(scheme.value)

    class Custom(value: String) : GcipScheme(value) {
        init {
            require(value.length <= MAX_SCHEME_LENGTH)
            require(value.all { it in 'a'..'z' })
        }
    }

    companion object {
        fun from(scheme: String): GcipScheme {
            return GcipPlatform.entries
                .firstOrNull { it.value == scheme }
                ?.let { Defined(it) }
                ?: Custom(scheme)
        }
    }
}

fun GcipPlatform.toScheme(): GcipScheme.Defined {
    return GcipScheme.Defined(this)
}

sealed interface ClientRequestData {
    class Exchange(
        val transport: Transport,
        val meta: Meta? = null,
    ) : ClientRequestData

    class Connect(
        val eid: GcipId?,
        val clientData: ClientData,
        val credentials: List<Derivation>,
        val transport: Transport,
        val meta: Meta? = null,
    ) : ClientRequestData

    class Extend(
        val eid: GcipId,
        val connectionId: GcipId,
        val credentials: List<Derivation>,
        val meta: Meta? = null,
    ) : ClientRequestData

    class Sign(
        val eid: GcipId,
        val connectionId: GcipId,
        val credentialId: GcipId,
        val challenge: Challenge,
        val meta: Meta? = null,
    ) : ClientRequestData

    class Disconnect(
        val eid: GcipId,
        val connectionId: GcipId,
        val meta: Meta? = null,
    ) : ClientRequestData
}

sealed interface ClientRequest {

    val encryption: Encryption
    val nonce: Short

    class Exchange(
        val data: ClientRequestData.Exchange,
        override val encryption: Encryption.Handshake.Request,
        override val nonce: Short,
    ) : ClientRequest, HandshakeRequestEncryption

    class Connect(
        val data: ClientRequestData.Connect,
        override val encryption: Encryption.Initiator,
        override val nonce: Short,
    ) : ClientRequest, InitialEncryption

    class Extend(
        val data: ClientRequestData.Extend,
        override val encryption: Encryption.Session,
        override val nonce: Short,
    ) : ClientRequest, SessionEncryption

    class Sign(
        val data: ClientRequestData.Sign,
        override val encryption: Encryption.Session,
        override val nonce: Short,
    ) : ClientRequest, SessionEncryption

    class Disconnect(
        val data: ClientRequestData.Disconnect,
        override val encryption: Encryption.Session,
        override val nonce: Short,
    ) : ClientRequest, SessionEncryption
}

class Meta(
    val payload: ByteArray
)

@Suppress("ArrayInDataClass")
sealed interface Transport {
    val peerData: ByteArray

    object Internal : Transport {
        override val peerData: ByteArray = byteArrayOf()
    }

    data class Nfc(override val peerData: ByteArray) : Transport
    data class Ble(override val peerData: ByteArray) : Transport
    data class Usb(override val peerData: ByteArray) : Transport

    fun toGcipTransportType(): GcipTransportType {
        return when (this) {
            is Ble -> GcipTransportType.Ble
            is Internal -> GcipTransportType.Internal
            is Nfc -> GcipTransportType.Nfc
            is Usb -> GcipTransportType.Usb
        }
    }
}

data class SignerData(
    val name: String,
    val scheme: GcipScheme,
    val id: String,
)

data class ClientData(
    val name: String,
    val origin: String,
)

sealed interface CallerData {

    class Initial(
        val scheme: GcipScheme?,
        val id: String?,
        val signature: String?
    )

    class Raw(
        val scheme: GcipScheme?,
        val id: String?,
        val signatures: List<String>?,
    )
}

sealed interface SignerRequest {

    val block: SignerBlock
    val meta: Meta?

    class Exchange(
        override val block: SignerBlock,
        override val encryption: Encryption.Handshake.Request,
        val transport: Transport,
        override val meta: Meta? = null,
    ) : SignerRequest, HandshakeRequestEncryption

    class Connect(
        override val block: SignerBlock,
        override val encryption: Encryption.Initiator,
        val transport: Transport,
        val signerData: SignerData,
        val clientData: ClientData,
        val derivations: List<CredentialRequest>,
        val callerData: CallerData.Initial,
        override val meta: Meta? = null,
    ) : SignerRequest, InitialEncryption

    class Extend(
        override val block: SignerBlock,
        override val encryption: Encryption.Session,
        val connectionId: GcipId,
        val derivations: List<CredentialRequest>,
        val callerData: CallerData.Raw,
        override val meta: Meta? = null,
    ) : SignerRequest, SessionEncryption

    class Sign(
        val blockId: String,
        override val block: SignerBlock,
        override val encryption: Encryption.Session,
        val connectionId: GcipId,
        val credentialId: GcipId,
        val challenge: Challenge,
        val callerData: CallerData.Raw,
        override val meta: Meta? = null,
    ) : SignerRequest, SessionEncryption

    class Disconnect(
        override val block: SignerBlock,
        override val encryption: Encryption.Session,
        val connectionId: GcipId,
        val callerData: CallerData.Raw,
        override val meta: Meta? = null,
    ) : SignerRequest, SessionEncryption
}

sealed interface CommonResponse {

    val block: SignerBlock
    val meta: Meta?
    val encryption: Encryption.Replier

    class Exchange(
        override val block: SignerBlock,
        override val encryption: Encryption.Handshake.Response,
        override val meta: Meta? = null,
    ) : CommonResponse, HandshakeResponseEncryption

    sealed interface Connect : CommonResponse {

        val data: ConnectData

        class Handshake(
            override val block: SignerBlock,
            override val data: ConnectData,
            override val encryption: Encryption.Handshake.Response,
            override val meta: Meta? = null,
        ) : Connect, HandshakeResponseEncryption

        class Session(
            override val block: SignerBlock,
            override val encryption: Encryption.Session,
            override val data: ConnectData,
            override val meta: Meta? = null,
        ) : Connect, SessionEncryption

        data class ConnectData(
            val connectionId: GcipId,
            val signerData: SignerData,
            val wallets: List<Wallet>,
            val connectionType: GcipConnectionType,
        )
    }

    class Extend(
        override val block: SignerBlock,
        override val encryption: Encryption.Session,
        val wallets: List<Wallet>,
        val connectionId: GcipId,
        override val meta: Meta? = null,
    ) : CommonResponse, SessionEncryption

    class Sign(
        override val block: SignerBlock,
        override val encryption: Encryption.Session,
        val signingId: GcipId,
        val signature: ByteArray,
        override val meta: Meta? = null,
    ) : CommonResponse, SessionEncryption

    class Disconnect(
        override val block: SignerBlock,
        override val encryption: Encryption.Session,
        val connectionId: GcipId,
        override val meta: Meta? = null,
    ) : CommonResponse, SessionEncryption
}
