package foundation.openstore.gcip.core.transport

import foundation.openstore.gcip.core.coder.GcipCborTable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import kotlinx.serialization.cbor.CborLabel

@Serializable
class GcipEncryptionMessage(
    @SerialName("eid")
    @CborLabel(GcipCborTable.Common.EncryptionMessage.EID)
    @ByteString
    val eid: GcipId?,

    @SerialName("data")
    @CborLabel(GcipCborTable.Common.EncryptionMessage.DATA)
    @ByteString
    val data: ByteArray,

    @SerialName("iv")
    @CborLabel(GcipCborTable.Common.EncryptionMessage.IV)
    @ByteString
    val iv: ByteArray?,

    @SerialName("exchangeKey")
    @CborLabel(GcipCborTable.Common.EncryptionMessage.EXCHANGE_KEY)
    @ByteString
    val exchangeKey: ByteArray?,
)
