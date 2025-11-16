package com.openstore.core.signing

object Bech32 {
    private const val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"

    // For SegWit (converts program to 5-bit and prepends version)
    fun encodeSegwit(hrp: String, witnessVersion: Int, program: ByteArray): String {
        val data = convertBits(program, 8, 5, true)
        val versionByte = witnessVersion.toByte()
        val data5bit = byteArrayOf(versionByte) + data
        return encode5Bit(hrp, data5bit)
    }

    // Generic (converts 8-bit data to 5-bit)
    fun encode(hrp: String, data: ByteArray): String {
        val data5bit = convertBits(data, 8, 5, true)
        return encode5Bit(hrp, data5bit)
    }

    // Low level (expects 5-bit data)
    fun encode5Bit(hrp: String, data5bit: ByteArray): String {
        val checksum = createChecksum(hrp, data5bit)
        val combined = data5bit + checksum
        val sb = StringBuilder()
        sb.append(hrp)
        sb.append("1")
        for (b in combined) {
            sb.append(CHARSET[b.toInt()])
        }
        return sb.toString()
    }

    private fun createChecksum(hrp: String, values: ByteArray): ByteArray {
        val hrpExpanded = expandHrp(hrp)
        val enc = hrpExpanded + values + ByteArray(6)
        val mod = polymod(enc) xor 1
        val checksum = ByteArray(6)
        for (i in 0 until 6) {
            checksum[i] = ((mod ushr (5 * (5 - i))) and 31).toByte()
        }
        return checksum
    }

    private fun expandHrp(hrp: String): ByteArray {
        val len = hrp.length
        val ret = ByteArray(len * 2 + 1)
        for (i in 0 until len) {
            val c = hrp[i].code
            ret[i] = (c ushr 5).toByte()
            ret[i + len + 1] = (c and 31).toByte()
        }
        ret[len] = 0
        return ret
    }

    private fun polymod(values: ByteArray): Int {
        var chk = 1
        for (v in values) {
            val b = chk ushr 25
            chk = ((chk and 0x1ffffff) shl 5) xor (v.toInt() and 0xff)
            for (i in 0 until 5) {
                if (((b ushr i) and 1) != 0) {
                    chk = chk xor GEN[i]
                }
            }
        }
        return chk
    }

    private val GEN = intArrayOf(0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3)

    private fun convertBits(
        data: ByteArray,
        fromBits: Int,
        toBits: Int,
        pad: Boolean
    ): ByteArray {
        var acc = 0
        var bits = 0
        val maxv = (1 shl toBits) - 1
        val out = mutableListOf<Byte>()

        for (b in data) {
            val value = b.toInt() and 0xFF
            if (value shr fromBits != 0) {
                throw IllegalArgumentException("Invalid data range")
            }

            acc = (acc shl fromBits) or value
            bits += fromBits

            while (bits >= toBits) {
                bits -= toBits
                out.add(((acc ushr bits) and maxv).toByte())
            }
        }

        if (pad) {
            if (bits > 0) {
                out.add(((acc shl (toBits - bits)) and maxv).toByte())
            }
        } else {
            if (bits >= fromBits || ((acc shl (toBits - bits)) and maxv) != 0) {
                throw IllegalArgumentException("Invalid data (non-zero padding)")
            }
        }

        return out.toByteArray()
    }
}
