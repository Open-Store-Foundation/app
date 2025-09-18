@file:OptIn(ExperimentalTypeInference::class)

package com.openstore.app.core.common

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

val BigInteger.isZero: Boolean get() = this == BigInteger.ZERO
val BigDecimal.isZero: Boolean get() = compareTo(BigDecimal.ZERO) == 0

val BigInteger.isZeroOrLess: Boolean get() = this <= BigInteger.ZERO
val BigDecimal.isZeroOrLess: Boolean get() = this <= BigDecimal.ZERO

val BigInteger?.orZero: BigInteger get() = this ?: BigInteger.ZERO
val BigDecimal?.orZero: BigDecimal get() = this ?: BigDecimal.ZERO


fun BigInteger.toBigDecimal(decimalMode: DecimalMode? = null): BigDecimal =
    BigDecimal.fromBigInteger(this, decimalMode = decimalMode)

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfBigInteger")
fun <T> Iterable<T>.sumOf(selector: (T) -> BigInteger): BigInteger =
    fold(BigInteger.ZERO) { acc, t -> acc.plus(selector(t)) }

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfBigDecimal")
fun <T> Iterable<T>.sumOf(selector: (T) -> BigDecimal): BigDecimal =
    fold(BigDecimal.ZERO) { acc, t -> acc.plus(selector(t)) }

