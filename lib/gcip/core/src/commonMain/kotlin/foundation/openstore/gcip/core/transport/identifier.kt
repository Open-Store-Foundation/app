package foundation.openstore.gcip.core.transport

import foundation.openstore.gcip.core.util.fromUrlBase64
import foundation.openstore.gcip.core.util.toUrlBase64Fmt
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class GcipId(val value: ByteArray) {

    companion object {
        fun generate(): GcipId {
            return GcipId(Identifier.generateBytesUuid())
        }

        fun from(value: String): GcipId {
            return GcipId(Identifier.decode(value))
        }
    }

    init {
        require(value.size == 16) { "eid must be 16 bytes" }
    }

    val fmt: String by lazy(LazyThreadSafetyMode.NONE) {
        Identifier.encode(value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || this::class != other::class) {
            return false
        }

        other as GcipId

        return fmt == other.fmt
    }

    override fun hashCode(): Int {
        return fmt.hashCode()
    }
}

fun ByteArray.safeId(): GcipId {
    return when {
        size >= 16 -> GcipId(this.copyOf(16))
        else -> GcipId(ByteArray(16).also { copyInto(it) })
    }
}

object Identifier {

    const val BYTES_SIZE = 16

    @OptIn(ExperimentalUuidApi::class)
    fun generateBytesUuid(): ByteArray {
        return Uuid.random()
            .toByteArray()
    }

    @OptIn(ExperimentalUuidApi::class)
    fun generateId(): String {
        return encode(generateBytesUuid())
    }

    @OptIn(ExperimentalUuidApi::class)
    fun encode(data: ByteArray): String {
        return data.toUrlBase64Fmt()
    }

    fun decode(data: String): ByteArray {
        return data.fromUrlBase64()
    }
}
