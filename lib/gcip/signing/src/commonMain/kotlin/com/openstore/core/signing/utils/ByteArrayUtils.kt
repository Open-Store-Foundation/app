package com.openstore.core.signing.utils

internal inline fun <T> ByteArray.use(block: (ByteArray) -> T): T {
    return try {
        block(this)
    } finally {
        fillZeros()
    }
}

internal fun ByteArray.fillZeros() = fill(0)
internal fun CharArray.fillZeros() = fill('\u0000')

internal fun CharArray.toByteArray(): ByteArray {
    var byteCount = 0
    for (char in this) {
        val code = char.code
        byteCount += when {
            code <= 0x7F -> 1
            code <= 0x7FF -> 2
            code in 0xD800..0xDFFF -> 0
            else -> 3
        }
    }

    val result = ByteArray(byteCount)
    var pos = 0

    for (char in this) {
        val code = char.code
        when {
            code <= 0x7F -> {
                result[pos++] = code.toByte()
            }
            code <= 0x7FF -> {
                result[pos++] = (0xC0 or (code shr 6)).toByte()
                result[pos++] = (0x80 or (code and 0x3F)).toByte()
            }
            code in 0xD800..0xDFFF -> {}
            else -> {
                result[pos++] = (0xE0 or (code shr 12)).toByte()
                result[pos++] = (0x80 or ((code shr 6) and 0x3F)).toByte()
                result[pos++] = (0x80 or (code and 0x3F)).toByte()
            }
        }
    }

    return result
}

