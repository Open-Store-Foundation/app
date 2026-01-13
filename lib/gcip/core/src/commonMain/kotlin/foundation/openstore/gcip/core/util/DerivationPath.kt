package foundation.openstore.gcip.core.util

import foundation.openstore.gcip.core.Algorithm

object DerivationPath {

    private const val MAX_UINT = 4294967295UL
    const val MAX_HARDENED = 2147483647UL
    const val HARDENED_MASK = 0x80000000u

    private const val PREFIX = "m/"
    private const val SEPARATOR = '/'
    private const val HARDENED = '\''
    private const val MAX_INDEX = 4_294_967_295L

    fun isHardened(index: UInt): Boolean {
        return (index and HARDENED_MASK) != 0u
    }

    fun toSafeHardened(algorithm: Algorithm, index: UInt): UInt? {
        return if (algorithm.isRequireHardened && !isHardened(index)) {
            return null
        } else {
            index
        }
    }

    fun toHardened(index: UInt): UInt {
        return index or HARDENED_MASK
    }

    fun parsePath(path: String): List<UInt> {
        if (path == "m") return emptyList()
        require(path.startsWith("m/"))

        return path.substringAfter("m/")
            .split("/")
            .map { comp ->
                if (comp.endsWith("'")) {
                    val num = comp.dropLast(1).toUInt()
                    num or HARDENED_MASK
                } else {
                    comp.toUInt()
                }
            }
    }

    fun encode(value: String): List<UInt> {
        if (value == "m" || value.isEmpty()) {
            throw IllegalArgumentException("Invalid derPath format: $value. Expected BIP32 path component.")
        }

        val startIndex = if (value.startsWith("m/")) 2 else if (value.startsWith("m")) 1 else 0
        val result = ArrayList<UInt>(6)
        var i = startIndex

        while (i < value.length) {
            val partStart = i
            while (i < value.length && value[i] != '/') {
                i++
            }
            val partEnd = i
            if (partEnd > partStart) {
                result.add(encodeUnit(value, partStart, partEnd))
            }
            if (i < value.length) {
                i++
            }
        }

        return result
    }

    fun decode(value: List<UInt>): String {
        if (value.isEmpty()) return "m"

        val sb = StringBuilder("m/")
        for (i in value.indices) {
            if (i > 0) sb.append('/')
            decodeUnit(value[i], sb)
        }
        return sb.toString()
    }

    fun validate(path: String, isRequireHardened: Boolean): Boolean {
        if (path.length < 11 || !path.startsWith(PREFIX)) {
            return false
        }

        var segmentCount = 0
        var currentVal: Long = 0
        var hasDigits = false
        var isHardened = false

        for (i in 2 until path.length) {
            when (val char = path[i]) {
                SEPARATOR -> {
                    if (!hasDigits) return false
                    if (isHardened && path[i - 1] != HARDENED) {
                        return false
                    }

                    if (isRequireHardened && !isHardened) {
                        return false
                    }

                    segmentCount++
                    currentVal = 0
                    hasDigits = false
                    isHardened = false
                }
                HARDENED -> {
                    if (isHardened) {
                        return false
                    }
                    if (!hasDigits) {
                        return false
                    }
                    isHardened = true
                }
                in '0'..'9' -> {
                    if (isHardened) {
                        return false
                    }
                    val digit = (char - '0').toLong()

                    if (currentVal > MAX_INDEX / 10) return false
                    currentVal = currentVal * 10 + digit
                    if (currentVal > MAX_INDEX) return false

                    hasDigits = true
                }
                else -> return false 
            }
        }

        if (!hasDigits) return false 
        if (isRequireHardened && !isHardened) return false
        segmentCount++

        return segmentCount < 6
    }

    private fun encodeUnit(value: String, start: Int, end: Int): UInt {
        val lastChar = end - 1
        val isHardened = value[lastChar] == '\''
        val numEnd = if (isHardened) lastChar else end

        val numValue = parseUInt(value, start, numEnd)

        if (isHardened && numValue > MAX_HARDENED) {
            throw IllegalArgumentException("Index too large for hardening: ${value.substring(start, end)}")
        }
        if (numValue > MAX_UINT) {
            throw IllegalArgumentException("Index too large: ${value.substring(start, end)}")
        }

        val index = numValue.toUInt()
        return if (isHardened) {
            index or HARDENED_MASK
        } else {
            index
        }
    }

    private fun decodeUnit(value: UInt, sb: StringBuilder) {
        val isHardened = (value and HARDENED_MASK) != 0u
        val index = value and 0x7FFFFFFFu

        sb.append(index)
        if (isHardened) {
            sb.append('\'')
        }
    }

    private fun parseUInt(value: String, start: Int, end: Int): ULong {
        if (start >= end) {
            throw IllegalArgumentException("Invalid number format: ${value.substring(start, end)}")
        }

        var result = 0UL
        for (i in start until end) {
            val digit = value[i] - '0'
            result = result * 10UL + digit.toULong()
            if (result > MAX_UINT) {
                throw IllegalArgumentException("Number too large: ${value.substring(start, end)}")
            }
        }
        return result
    }
}
