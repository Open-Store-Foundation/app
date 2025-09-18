package com.openstore.app.core.common

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger


private val ADDRESS_REGEX = Regex("0x[a-fA-F0-9]{40}")
fun String.isEvmAddress(): Boolean {
    return this.matches(ADDRESS_REGEX)
}

private val EMAIL_REGEX = Regex(pattern = "[a-zA-Z0-9+._%\\-]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+")
fun String.isEmail(): Boolean {
    return this.isNotBlank() && EMAIL_REGEX.matches(this)
}

const val HexPrefix = "0x"

fun String.remove0x(): String = if (containsHexPrefix()) {
    substring(2)
} else {
    this
}

fun String.add0x(): String =
    if (startsWith(HexPrefix)) {
        this
    } else {
        "$HexPrefix$this"
    }

fun String.hexToBigInteger(default: BigInteger = BigInteger.ZERO): BigInteger {
    return try {
        remove0x().toBigInteger(16)
    } catch (e: NumberFormatException) {
        default
    }
}

fun String.containsHexPrefix(): Boolean =
    this.length > 1 && this[0] == '0' && this[1] == 'x'

fun String.isHexEncoded(): Boolean {
    val regex = "^0x[0-9A-Fa-f]*$".toRegex()

    if (!this.containsHexPrefix()) {
        return false
    }

    if (!regex.matches(this)) {
        return false
    }

    return true
}
