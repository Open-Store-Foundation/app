package com.openstore.app.core.common

inline fun <T> ByteArray.use(block: (ByteArray) -> T): T {
    return try {
        block(this)
    } finally {
        fillZeros()
    }
}

fun ByteArray.fillZeros() = fill(0)

fun ByteArray.toCharArray(): CharArray {
    var charCount = 0
    var i = 0
    while (i < size) {
        val byte = this[i].toInt() and 0xFF
        when {
            byte <= 0x7F -> {
                charCount++
                i++
            }
            byte and 0xE0 == 0xC0 -> {
                charCount++
                i += 2
            }
            byte and 0xF0 == 0xE0 -> {
                charCount++
                i += 3
            }
            byte and 0xF8 == 0xF0 -> {
                charCount += 2
                i += 4
            }
            else -> {
                charCount++
                i++
            }
        }
    }

    val result = CharArray(charCount)
    var pos = 0
    i = 0

    while (i < size) {
        val byte = this[i].toInt() and 0xFF
        when {
            byte <= 0x7F -> {
                result[pos++] = byte.toChar()
                i++
            }
            byte and 0xE0 == 0xC0 && i + 1 < size -> {
                val b2 = this[i + 1].toInt() and 0x3F
                val code = ((byte and 0x1F) shl 6) or b2
                result[pos++] = code.toChar()
                i += 2
            }
            byte and 0xF0 == 0xE0 && i + 2 < size -> {
                val b2 = this[i + 1].toInt() and 0x3F
                val b3 = this[i + 2].toInt() and 0x3F
                val code = ((byte and 0x0F) shl 12) or (b2 shl 6) or b3
                result[pos++] = code.toChar()
                i += 3
            }
            byte and 0xF8 == 0xF0 && i + 3 < size -> {
                val b2 = this[i + 1].toInt() and 0x3F
                val b3 = this[i + 2].toInt() and 0x3F
                val b4 = this[i + 3].toInt() and 0x3F
                val codePoint = ((byte and 0x07) shl 18) or (b2 shl 12) or (b3 shl 6) or b4
                val adjusted = codePoint - 0x10000
                result[pos++] = (0xD800 or (adjusted shr 10)).toChar()
                result[pos++] = (0xDC00 or (adjusted and 0x3FF)).toChar()
                i += 4
            }
            else -> {
                result[pos++] = '\uFFFD'
                i++
            }
        }
    }

    return result
}
