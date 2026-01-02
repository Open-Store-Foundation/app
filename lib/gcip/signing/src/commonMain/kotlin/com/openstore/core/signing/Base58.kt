package com.openstore.core.signing

object Base58 {
    const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    private val ALPHABET_ARRAY = ALPHABET.toCharArray()
    private val INDEXES = IntArray(128) { -1 }

    init {
        for (i in ALPHABET.indices) {
            INDEXES[ALPHABET[i].code] = i
        }
    }

    fun encode(input: ByteArray): String {
        if (input.isEmpty()) return ""
        var inputCopy = input.copyOf()
        // Count leading zeros
        var zeros = 0
        while (zeros < inputCopy.size && inputCopy[zeros] == 0.toByte()) {
            zeros++
        }
        // Convert to base58
        inputCopy = inputCopy.copyOfRange(zeros, inputCopy.size)
        val size = inputCopy.size
        val b58 = CharArray(size * 2) // Upper bound
        var b58sz = 0

        var startAt = 0
        while (startAt < inputCopy.size) {
            val mod = divmod256(inputCopy, startAt)
            if (inputCopy[startAt] == 0.toByte()) {
                startAt++
            }
            b58[b58sz++] = ALPHABET_ARRAY[mod.toInt()]
        }

        while (zeros-- > 0) {
            b58[b58sz++] = ALPHABET_ARRAY[0]
        }
        return b58.concatToString(0, 0 + b58sz).reversed()
    }

    fun encodeCheck(data: ByteArray): String {
        val hash1 = Cryptography.Hash.sha256(data)
        val hash2 = Cryptography.Hash.sha256(hash1)
        val checksum = hash2.copyOfRange(0, 4)
        return encode(data + checksum)
    }

    private fun divmod256(number: ByteArray, startAt: Int): Byte {
        var remainder = 0
        for (i in startAt until number.size) {
            val digit = number[i].toInt() and 0xFF
            val temp = remainder * 256 + digit
            number[i] = (temp / 58).toByte()
            remainder = temp % 58
        }
        return remainder.toByte()
    }
}
