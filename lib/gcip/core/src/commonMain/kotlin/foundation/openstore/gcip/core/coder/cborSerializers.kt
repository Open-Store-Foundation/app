package foundation.openstore.gcip.core.coder

import foundation.openstore.gcip.core.transport.GcipBinaryFormat
import foundation.openstore.gcip.core.transport.GcipConnectionType
import foundation.openstore.gcip.core.transport.GcipCredentialType
import foundation.openstore.gcip.core.transport.GcipMethod
import foundation.openstore.gcip.core.transport.GcipSigningAlgorithm
import foundation.openstore.gcip.core.transport.GcipTransformAlgorithm
import foundation.openstore.gcip.core.transport.GcipTransportType
import foundation.openstore.gcip.core.util.DerivationPath
import foundation.openstore.gcip.core.transport.GcipId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind

import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object GcipCborTable {

    object Common {
        object ClientData {
            const val NAME = 0x01L
            const val ORIGIN = 0x02L
        }

        object SignerData {
            const val NAME = 0x01L
            const val SCHEME = 0x02L
            const val ID = 0x03L
        }

        object Challenge {
            const val PAYLOAD = 0x01L
            const val FMT = 0x02L
            const val TRANSFORM = 0x03L
        }

        object Derivation {
            const val ALG = 0x01L
            const val DER = 0x02L
            const val DER_PATH = 0x03L
        }

        object CredentialRequest {
            const val TYPE = 0x01L
            const val PARAMS = 0x02L
        }

        object CredentialResponse {
            const val TYPE = 0x01L
            const val NAMESPACE = 0x02L
            const val DERIVATIONS = 0x03L
        }

        object KeyDerivation {
            const val CRED_ID = 0x01L
            const val PAYLOAD = 0x02L
            const val PARAMS = 0x03L
        }

        object Transport {
            const val TYPE = 0x01L
            const val PARAMS = 0x02L
        }

        object EncryptionMessage {
            const val EID = 0x01L
            const val DATA = 0x02L
            const val IV = 0x03L
            const val EXCHANGE_KEY = 0x04L
        }

        const val META_ID = 0x20L
    }

    object ConnectRequest {
        const val CLIENT_DATA = 0x01L
        const val CRED_REQUESTS = 0x02L
        const val TRANSPORT = 0x03L
        const val META = Common.META_ID
    }

    object ConnectResponse {
        const val CONNECTION_ID = 0x01L
        const val CONNECTION_TYPE = 0x02L
        const val SIGNER_DATA = 0x03L
        const val CREDENTIALS = 0x04L
        const val META = Common.META_ID
    }

    object ExtendRequest {
        const val CONNECTION_ID = 0x01L
        const val CREDENTIALS = 0x02L
        const val META = Common.META_ID
    }

    object ExtendResponse {
        const val CREDENTIALS = 0x01L
        const val CONNECTION_ID = 0x02L
        const val META = Common.META_ID
    }

    object SignRequest {
        const val CONNECTION_ID = 0x01L
        const val CRED_ID = 0x02L
        const val CHALLENGE = 0x03L
        const val META = Common.META_ID
    }

    object SignResponse {
        const val SIGNING_ID = 0x01L
        const val SIG = 0x02L
        const val META = Common.META_ID
    }

    object DisconnectRequest {
        const val CONNECTION_ID = 0x01L
        const val META = Common.META_ID
    }

    object DisconnectResponse {
        const val CONNECTION_ID = 0x01L
        const val META = Common.META_ID
    }

    object ExchangeRequest {
        const val TRANSPORT = 0x01L
        const val META = Common.META_ID
    }

    object ExchangeResponse {
        const val META = Common.META_ID
    }
}

object MethodSerializer : KSerializer<GcipMethod> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Method", PrimitiveKind.BYTE)
    override fun serialize(encoder: Encoder, value: GcipMethod) {
        encoder.encodeByte(value.code)
    }
    override fun deserialize(decoder: Decoder): GcipMethod {
        return GcipMethod.fromCode(decoder.decodeByte())
            ?: throw IllegalArgumentException("Unknown Method code")
    }
}

object ConnectionTypeSerializer : KSerializer<GcipConnectionType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ConnectionType", PrimitiveKind.BYTE)
    override fun serialize(encoder: Encoder, value: GcipConnectionType) {
        encoder.encodeByte(value.value)
    }
    override fun deserialize(decoder: Decoder): GcipConnectionType {
        return GcipConnectionType.from(decoder.decodeByte())
            ?: throw IllegalArgumentException("Unknown ConnectionType value")
    }
}

object RepresentationSerializer : KSerializer<GcipBinaryFormat> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Representation", PrimitiveKind.BYTE)
    override fun serialize(encoder: Encoder, value: GcipBinaryFormat) {
        encoder.encodeByte(value.value)
    }
    override fun deserialize(decoder: Decoder): GcipBinaryFormat {
        return GcipBinaryFormat.from(decoder.decodeByte())
            ?: throw IllegalArgumentException("Unknown Representation value")
    }
}

object SigningAlgorithmSerializer : KSerializer<GcipSigningAlgorithm> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SigningAlgorithm", PrimitiveKind.BYTE)
    override fun serialize(encoder: Encoder, value: GcipSigningAlgorithm) {
        encoder.encodeInt(value.value)
    }
    override fun deserialize(decoder: Decoder): GcipSigningAlgorithm {
        return GcipSigningAlgorithm.from(decoder.decodeInt())
            ?: throw IllegalArgumentException("Unknown SigningAlgorithm value")
    }
}

object TransformAlgorithmSerializer : KSerializer<GcipTransformAlgorithm> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TransformAlgorithm", PrimitiveKind.SHORT)
    override fun serialize(encoder: Encoder, value: GcipTransformAlgorithm) {
        encoder.encodeInt(value.value)
    }
    override fun deserialize(decoder: Decoder): GcipTransformAlgorithm {
        return GcipTransformAlgorithm.from(decoder.decodeInt())
            ?: throw IllegalArgumentException("Unknown TransformAlgorithm value")
    }
}

object GcipIdSerializer : KSerializer<GcipId> {
    private val delegate = ByteArraySerializer()
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: GcipId) {
        encoder.encodeSerializableValue(delegate, value.value)
    }

    override fun deserialize(decoder: Decoder): GcipId {
        return GcipId(decoder.decodeSerializableValue(delegate))
    }
}

object CredentialTypeSerializer : KSerializer<GcipCredentialType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CredentialType", PrimitiveKind.BYTE)
    override fun serialize(encoder: Encoder, value: GcipCredentialType) {
        encoder.encodeByte(value.code)
    }
    override fun deserialize(decoder: Decoder): GcipCredentialType {
        return GcipCredentialType.fromCode(decoder.decodeByte())
            ?: throw IllegalArgumentException("Unknown CredentialType code")
    }
}

object TransportTypeSerializer : KSerializer<GcipTransportType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TransportType", PrimitiveKind.BYTE)
    override fun serialize(encoder: Encoder, value: GcipTransportType) {
        return encoder.encodeByte(value.value)
    }
    override fun deserialize(decoder: Decoder): GcipTransportType {
        return GcipTransportType.from(decoder.decodeByte())
            ?: throw IllegalArgumentException("Unknown TransportType value")
    }
}

object DerPathSerializer : KSerializer<String> {
    private val delegateSerializer = ListSerializer(UInt.serializer())
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor
    override fun serialize(encoder: Encoder, value: String) {
        val result = DerivationPath.encode(value)
        encoder.encodeSerializableValue(delegateSerializer, result)
    }
    override fun deserialize(decoder: Decoder): String {
        val result = decoder.decodeSerializableValue(delegateSerializer)
        return DerivationPath.decode(result)
    }
}

