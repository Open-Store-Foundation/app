package foundation.openstore.gcip.transform.algorithms

/**
 * Kotlin Multiplatform implementation of RIPEMD-160.
 *
 * Usage:
 * val hashBytes = Ripemd160().digest(inputBytes)
 */
internal class Ripemd160 {
    // 160-bit state (5 x 32-bit words)
    private val state = IntArray(5)

    // Buffer for the current 64-byte block
    private val buffer = ByteArray(64)
    private var bufferOffset = 0

    // Total processed bytes count
    private var byteCount = 0L

    init {
        reset()
    }

    /**
     * Resets the hash state to initial values.
     */
    fun reset() {
        state[0] = 0x67452301
        state[1] = -0x10325477 // 0xEFCDAB89
        state[2] = -0x67452302 // 0x98BADCFE
        state[3] = 0x10325476
        state[4] = -0x3C2D1E10 // 0xC3D2E1F0
        bufferOffset = 0
        byteCount = 0L
        buffer.fill(0)
    }

    /**
     * Updates the hash with the given input data.
     */
    fun update(input: ByteArray) {
        update(input, 0, input.size)
    }

    fun update(input: ByteArray, offset: Int, length: Int) {
        var i = 0
        val limit = length

        while (i < limit) {
            val space = 64 - bufferOffset
            val remaining = limit - i

            if (remaining >= space) {
                // Fill the buffer
                input.copyInto(buffer, bufferOffset, offset + i, offset + i + space)
                processBlock(buffer)
                byteCount += 64
                bufferOffset = 0
                i += space
            } else {
                // Buffer remaining data
                input.copyInto(buffer, bufferOffset, offset + i, offset + i + remaining)
                bufferOffset += remaining
                i += remaining
            }
        }
    }

    /**
     * Finalizes the hash and returns the 20-byte digest.
     * The instance is reset after this call.
     */
    fun digest(): ByteArray {
        val finalCount = byteCount + bufferOffset

        // Padding: append '1' bit (0x80 byte)
        buffer[bufferOffset] = 0x80.toByte()
        bufferOffset++

        // If not enough space for 64-bit length (8 bytes), pad with zeros and process
        if (bufferOffset > 56) {
            while (bufferOffset < 64) {
                buffer[bufferOffset++] = 0
            }
            processBlock(buffer)
            bufferOffset = 0
        }

        // Pad with zeros up to 56 bytes
        while (bufferOffset < 56) {
            buffer[bufferOffset++] = 0
        }

        // Append total length in bits as 64-bit Little Endian integer
        val bitLength = finalCount * 8
        val lenBytes = ByteArray(8)
        for (i in 0 until 8) {
            lenBytes[i] = (bitLength ushr (i * 8)).toByte()
        }
        lenBytes.copyInto(buffer, 56)

        processBlock(buffer)

        // Encode state to Little Endian bytes
        val result = ByteArray(20)
        for (i in 0 until 5) {
            val word = state[i]
            result[i * 4] = word.toByte()
            result[i * 4 + 1] = (word ushr 8).toByte()
            result[i * 4 + 2] = (word ushr 16).toByte()
            result[i * 4 + 3] = (word ushr 24).toByte()
        }

        reset()
        return result
    }

    /**
     * Helper to compute digest directly from a byte array.
     */
    fun digest(input: ByteArray): ByteArray {
        update(input)
        return digest()
    }

    // --- Core Logic ---

    private fun processBlock(block: ByteArray) {
        // Decode block into 16 Words (Little Endian)
        val X = IntArray(16)
        for (i in 0 until 16) {
            X[i] = (block[i * 4].toInt() and 0xFF) or
                    ((block[i * 4 + 1].toInt() and 0xFF) shl 8) or
                    ((block[i * 4 + 2].toInt() and 0xFF) shl 16) or
                    ((block[i * 4 + 3].toInt() and 0xFF) shl 24)
        }

        // Initialize working variables
        var al = state[0]; var bl = state[1]; var cl = state[2]; var dl = state[3]; var el = state[4]
        var ar = state[0]; var br = state[1]; var cr = state[2]; var dr = state[3]; var er = state[4]
        var t: Int

        // 5 Rounds of 16 steps = 80 steps
        for (j in 0 until 80) {
            // Left Line
            // T = rol(s, A + f(B, C, D) + X[r] + K) + E
            val funcL = f(j / 16, bl, cl, dl)
            t = al + funcL + X[RL[j]] + KL[j / 16]
            t = t.rotateLeft(SL[j]) + el

            al = el
            el = dl
            dl = cl.rotateLeft(10)
            cl = bl
            bl = t

            // Right Line
            // T = rol(s', A' + f'(B', C', D') + X[r'] + K') + E'
            // Note: Right line uses functions in reverse order: 4, 3, 2, 1, 0
            val funcR = f(4 - (j / 16), br, cr, dr)
            t = ar + funcR + X[RR[j]] + KR[j / 16]
            t = t.rotateLeft(SR[j]) + er

            ar = er
            er = dr
            dr = cr.rotateLeft(10)
            cr = br
            br = t
        }

        // Combine results
        val t0 = state[1] + cl + dr
        state[1] = state[2] + dl + er
        state[2] = state[3] + el + ar
        state[3] = state[4] + al + br
        state[4] = state[0] + bl + cr
        state[0] = t0
    }

    // Non-linear functions
    // 0: x ^ y ^ z
    // 1: (x & y) | (~x & z)
    // 2: (x | ~y) ^ z
    // 3: (x & z) | (y & ~z)
    // 4: x ^ (y | ~z)
    private fun f(round: Int, x: Int, y: Int, z: Int): Int {
        return when (round) {
            0 -> x xor y xor z
            1 -> (x and y) or (x.inv() and z)
            2 -> (x or y.inv()) xor z
            3 -> (x and z) or (y and z.inv())
            4 -> x xor (y or z.inv())
            else -> 0
        }
    }

    private fun Int.rotateLeft(bits: Int): Int {
        return (this shl bits) or (this ushr (32 - bits))
    }

    companion object {
        // Left line selection (r)
        private val RL = intArrayOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
            3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
            1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
            4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13
        )

        // Right line selection (r')
        private val RR = intArrayOf(
            5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
            6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
            15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
            8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
            12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
        )

        // Left line shifts (s)
        private val SL = intArrayOf(
            11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
            11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
            9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
        )

        // Right line shifts (s')
        private val SR = intArrayOf(
            8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
            9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
            9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
            15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
            8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
        )

        // Left line constants (K)
        private val KL = intArrayOf(
            0,
            0x5A827999,
            0x6ED9EBA1,
            -0x70E44324, // 0x8F1BBCDC
            -0x56AC02B2  // 0xA953FD4E
        )

        // Right line constants (K')
        private val KR = intArrayOf(
            0x50A28BE6,
            0x5C4DD124,
            0x6D703EF3,
            0x7A6D76E9,
            0
        )
    }
}