package com.openstore.app.core.common

val NotLeadingHexFormat by lazy {
    HexFormat {
        number {
            removeLeadingZeros = true
        }
    }
}

fun ByteArray.toLower0xHex(): String = "0x${this.toHexString()}"
fun ByteArray.toUpper0xHex(): String = "0x${this.toHexString(HexFormat.UpperCase)}"

private val HEX_CHARS_UPPER = "0123456789ABCDEF".toCharArray()
fun ByteArray.toFingerHex(): String {
    if (this.isEmpty()) {
        return ""
    }

    // Calculate capacity: N bytes = N * 2 hex chars + (N-1) colons = 3N - 1
    // Handle the case of a single byte (N=1) correctly (capacity should be 2)
    val capacity = if (this.size == 1) 2 else this.size * 3 - 1
    val sb = StringBuilder(capacity) // Pre-allocate

    this.forEachIndexed { index, byte ->
        // Get the integer value of the byte, ensuring it's treated as unsigned (0-255).
        val v = byte.toInt() and 0xFF

        // Append the high nibble (first hex character).
        sb.append(HEX_CHARS_UPPER[v ushr 4]) // `ushr` is unsigned right shift

        // Append the low nibble (second hex character).
        sb.append(HEX_CHARS_UPPER[v and 0x0F]) // `0x0F` masks the lower 4 bits

        // Append the colon separator if it's not the last byte.
        if (index < this.size - 1) {
            sb.append(':')
        }
    }

    return sb.toString()
}

fun Long.toInt256Hex0x(): String {
    return toString(16)
        .padStart(64, '0')
        .add0x()
}

fun Long.toNotLeadingHex0x(): String {
    return toHexString(NotLeadingHexFormat)
        .add0x()
}
