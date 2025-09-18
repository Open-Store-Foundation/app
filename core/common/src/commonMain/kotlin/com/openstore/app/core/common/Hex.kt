package com.openstore.app.core.common

private val HEX_CHARS_UPPER = "0123456789ABCDEF".toCharArray()

/**
 * Converts a ByteArray to its uppercase hexadecimal string representation.
 *
 * This implementation is optimized for performance by:
 * 1. Pre-allocating the exact size CharArray needed for the result.
 * 2. Using a direct lookup table (HEX_CHARS_UPPER) for hex characters.
 * 3. Employing efficient bitwise operations for nibble extraction.
 *
 * Ideal for converting cryptographic hash results (like SHA-256 fingerprints)
 * where performance can be important.
 *
 * @receiver The ByteArray to convert.
 * @return The uppercase hexadecimal string representation of the ByteArray.
 *         Returns an empty string if the input ByteArray is empty.
 */
fun ByteArray.toUpper0xHex(): String {
    if (this.isEmpty()) return ""

    // Each byte converts to two hex characters.
    val hexChars = CharArray(this.size * 2)

    // Loop through each byte in the ByteArray.
    for (i in this.indices) {
        // Get the integer value of the byte, masking with 0xFF to ensure
        // it's treated as an unsigned value (0-255) for the shifts.
        val v = this[i].toInt() and 0xFF

        // Calculate the index for the high nibble (first hex character)
        // using unsigned right shift, then look up the character.
        hexChars[i * 2] = HEX_CHARS_UPPER[v ushr 4] // `ushr` is unsigned right shift

        // Calculate the index for the low nibble (second hex character)
        // using a bitwise AND mask, then look up the character.
        hexChars[i * 2 + 1] = HEX_CHARS_UPPER[v and 0x0F] // `0x0F` masks the lower 4 bits
    }

    // Create the final String from the CharArray. This is efficient.
    return "0x${hexChars.concatToString()}"
}

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
