package foundation.openstore.gcip.core.coder

import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.core.transport.GcipMethod
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.ByteArraySource
import foundation.openstore.gcip.core.util.GcipResult
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import kotlinx.io.readUByte
import kotlinx.io.readUShort
import kotlinx.io.writeUByte
import kotlinx.io.writeUShort

sealed interface GcipBlock {

    val version: UByte
    val status: GcipStatus
    val nonce: UShort
    val method: GcipMethod?
    val data: ByteArray?

    data class Header(
        val version: UByte,
        val status: GcipStatus,
        val nonce: UShort,
        val method: GcipMethod?,
        val dataLength: Int,
    )

    class Decoded(
        val header: Header,
        override val data: ByteArray,
    ) : GcipBlock {
        override val version: UByte = header.version
        override val status: GcipStatus = header.status
        override val nonce: UShort = header.nonce
        override val method: GcipMethod = header.method!! // TODO
    }

    class Encode(
        override val version: UByte,
        override val status: GcipStatus,
        override val nonce: UShort,
        override val method: GcipMethod?,
        override val data: ByteArray?,
    ) : GcipBlock
}

object GcipCoder {

    private const val MIN_HEADER_SIZE = 5

    fun headerSignature(header: GcipBlock.Header): ByteArray {
        return Buffer()
            .apply { writeSignature(header) }
            .readByteArray()
    }

    fun encodeHeader(header: GcipBlock.Header): ByteArray {
        return Buffer().apply {
            writeSignature(header)

            header.method?.let { writeInt(header.dataLength) }
        }.readByteArray()
    }

    private fun Buffer.writeSignature(header: GcipBlock.Header) {
        writeUByte(header.version)
        writeUShort(header.status.value.toUShort())
        writeUShort(header.nonce)

        header.method?.let {
            writeByte(header.method.code)
        }
    }

    fun decodeBlock(bytes: ByteArray): GcipResult<GcipBlock.Decoded> {
        if (bytes.size < MIN_HEADER_SIZE) {
            return GcipResult.err(GcipStatus.InvalidBlock)
        }

        return try {
            decodeBlockInternal(bytes)
        } catch (e: Throwable) {
             GcipResult.err(GcipStatus.InvalidBlock, err = e)
        }
    }

    private fun decodeBlockInternal(bytes: ByteArray): GcipResult<GcipBlock.Decoded> {
        ByteArraySource(bytes)
            .buffered()
            .use { buf ->
                // version
                val version = buf.readUByte()

                // status
                val statusCode = buf.readUShort().toInt()
                var status = GcipStatus.from(statusCode) ?: GcipStatus.UnknownStatus
                val nonce = buf.readUShort()

                if (version < GcipConfig.MinVersion || version > GcipConfig.ActualVersion) {
                    status = GcipStatus.UnsupportedVersion
                }

                val header = GcipBlock.Header(
                    version = version,
                    status = status,
                    nonce = nonce,
                    method = null,
                    dataLength = 0,
                )

                if (status.isError) {
                    return GcipResult.err(
                        status,
                        GcipBlock.Decoded(
                            header = header,
                            data = byteArrayOf(),
                        )
                    )
                }

                // method
                val method = GcipMethod.fromCode(buf.readByte())
                if (method == null) {
                    val newHeader = header.copy(status = status)
                    return GcipResult.err(
                        status,
                        GcipBlock.Decoded(
                            header = newHeader,
                            data = byteArrayOf(),
                        )
                    )
                }

                // length
                val length = buf.readInt()

                // data
                val data = buf.readByteArray(length)

                return GcipResult.ok(
                    GcipBlock.Decoded(
                        header = header.copy(
                            method = method,
                            dataLength = length,
                        ),
                        data = data,
                    )
                )
            }
    }

    fun encodeBlock(header: GcipBlock.Header, data: ByteArray): ByteArray {
        val encodedHeader = encodeHeader(header)
        return encodedHeader + data
    }
}
