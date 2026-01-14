package foundation.openstore.gcip.core.transport

import kotlinx.serialization.Serializable
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@Serializable
enum class GcipMethod(val value: String, val code: Byte) {
    ExchangeRequest("gcip.exchange.request", 0),
    ExchangeResponse("gcip.exchange.response", -128),

    ConnectRequest("gcip.connect.request", 1),
    ConnectResponse("gcip.connect.response", -127),

    ExtendRequest("gcip.extend.request", 2),
    ExtendResponse("gcip.extend.response", -126),

    SignRequest("gcip.sign.request", 3),
    SignResponse("gcip.sign.response", -125),

    DisconnectRequest("gcip.disconnect.request", 4),
    DisconnectResponse("gcip.disconnect.response", -124),
    ;

    val isRequest get() = code >= 0
    val isResponse get() = code < 0

    companion object Companion {
        fun from(value: String): GcipMethod? = entries.firstOrNull { it.value == value }
        fun fromCode(code: Byte): GcipMethod? = entries.firstOrNull { it.code == code }
    }
}

@Serializable
sealed interface GcipRequestType
@Serializable
sealed interface GcipResponseType

@Serializable
enum class GcipConnectionType(val value: Byte) {
    Device(0),
    CrossDevice(1),
    ;
    
    companion object Companion {
        fun from(value: Byte): GcipConnectionType? = entries.firstOrNull { it.value == value }
    }
}

@Serializable
enum class GcipTransportType(val value: Byte) {
    Internal(0),
    Usb(1),
    Nfc(2),
    Ble(3),
    ;

    companion object Companion {
        fun from(value: Byte): GcipTransportType? = entries.firstOrNull { it.value == value }
    }
}

@Serializable
enum class GcipBinaryFormat(val value: Byte) {
    Hex(0),
    Utf8(1),
    Base64Url(2),
    ;
    
    companion object Companion {
        fun from(value: Byte): GcipBinaryFormat? = entries.firstOrNull { it.value == value }
    }
}

@Serializable
enum class GcipSigningAlgorithm(val value: Int) {
    EcN256(CoseId.EsSecp256r1),
    Ed25519(CoseId.EdDsa),
    EcSecp256K1(CoseId.EcSecp256k1),
    ;
    
    companion object Companion {
        fun from(value: Int): GcipSigningAlgorithm? = entries.firstOrNull { it.value == value }
    }
}

@Suppress("EnumEntryName")
@Serializable
enum class GcipTransformAlgorithm(val value: Int) {
    Sha256(CoseId.Sha256),
    Sha512(CoseId.Sha512),
    Sha512_256(CoseId.Sha512_256),
    Keccak256(CoseId.Keccak256),
    Sha3_256(CoseId.Sha3_256),
    Ripemd160(CoseId.Ripemd160),
    Blake2b_224(CoseId.Blake2b_224),
    Blake2b_256(CoseId.Blake2b_256),
    ;

    companion object Companion {
        fun from(value: Int): GcipTransformAlgorithm? = entries.firstOrNull { it.value == value }
    }
}

enum class GcipEncryptionAlgorithm {
    Aes256Gcm;
}

@Serializable
enum class GcipCredentialType( val code: Byte) {
    PublicKey(0),
    ;

    companion object Companion {
        fun fromCode(code: Byte): GcipCredentialType? = entries.firstOrNull { it.code == code }
    }
}

enum class GcipPlatform(
    val value: String,
) {
    Android("a"),
    Ios("i"), // including iPad, iPhone, WatchOS etc.
    Windows("w"),
    Macos("m"),
    Linux("l"),

    ChromeExtension("ce"),
    Web("web"),
    ;
}

@OptIn(ExperimentalObjCName::class)
@ObjCName("GcipStatus", exact = true)
enum class GcipStatus(val value: Int, val description: String) {
    // Environment
    Success(0, "Success"),
    UnknownError(1, "Unknown Error: An unexpected error occurred"),
    UnsafeDevice(2, "Unsafe Device: The device environment is considered unsafe (e.g., rooted)"),
    PlatformApiError(3, "Should Retry: A temporary issue occurred; the client should retry the request"),
    UnknownCaller(4, "Unknown Client: The client identifier is unknown or invalid"),
    UserCanceled(5, "User Canceled: The user declined the request"),
    TooManyRequests(6, "Too Many Requests: Each application can send only one sign request at the same time"),

    // Block
    InvalidBlock(32, "Invalid Block: The block structure is malformed or too short"),
    UnsupportedVersion(33, "Unsupported Version: The protocol version is not supported"),
    UnknownMethod(34, "Unknown Method: The method code is not recognized"),
    UnknownStatus(35, "Unknown Status: The status code is not recognized"),

    // Body
    EncryptionError(64, "Encryption Error: Failed to decrypt or encrypt the message"),
    InvalidFormat(65, "Invalid Format: The request format is invalid or missing required fields"),
    InvalidMethod(66, "Invalid Method: The requested method is not supported or invalid"),
    UnknownSession(67, "Unknown Exchange: The exchange ID is not recognized"),
    UnsupportedTransport(68, "Unsupported Transport: The requested transport method is not supported"),
    MissingChallenge(69, "Missing Challenge: The challenge field is missing when required"),
    UnknownRepresentation(70, "Unknown Representation: The requested repr type is not supported"),
    UnknownTransform(71, "Unknown Transform: The requested transform type is not supported"),
    InvalidClientData(72, "Invalid Client Data: The provided client data structure is invalid"),
    UnsupportedAlgorithm(73, "Unsupported Algorithm: The requested algorithm is not supported"),

    MissingOrigin(128, "Missing Origin: The clientData.origin is missing"),
    InvalidOrigin(129, "Invalid Origin: The origin format is invalid"),
    MissingCredentialParams(130, "Missing Credential Params: Required parameters for credential generation are missing"),
    UnknownCredentialParam(131, "Unknown Credential Param: A provided credential parameter is unknown or unsupported"),
    UnverifiedOrigin(132, "Unverified Origin: The origin could not be cryptographically verified"),

    MissingCredentials(160, "Missing Allowed Credentials: No credentials provided for signing"),
    UnknownConnection(161, "Unknown Connection: The connectionId is invalid or expired"),
    UnknownCredential(162, "Unknown Credential: The requested credId was not found"),
    MismatchOrigin(163, "Mismatch Origin: The origin provided in clientData does not match the transport source"),
    ;

    companion object Companion {
        fun from(value: Int): GcipStatus? = entries.firstOrNull { it.value == value }
    }

    val isSuccess: Boolean get() {
        return value == Success.value
    }

    val isError: Boolean get() {
        return value > Success.value
    }

    val isEnvError: Boolean get() {
        return value > Success.value && value < EncryptionError.value
    }

    val isCommonError: Boolean get() {
        return value > Success.value && value < EncryptionError.value
    }

    val isBodyError: Boolean get() {
        return value > EncryptionError.value
    }
}
