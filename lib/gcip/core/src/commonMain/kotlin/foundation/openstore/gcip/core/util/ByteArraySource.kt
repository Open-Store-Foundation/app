package foundation.openstore.gcip.core.util

import kotlinx.io.Buffer
import kotlinx.io.RawSource

internal class ByteArraySource(private val data: ByteArray) : RawSource {
    private var position = 0

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        if (position >= data.size) return -1L
        if (byteCount == 0L) return 0L
        val bytesToRead = minOf(byteCount, (data.size - position).toLong()).toInt()
        sink.write(data, position, position + bytesToRead)
        position += bytesToRead
        return bytesToRead.toLong()
    }

    override fun close() {}
}
