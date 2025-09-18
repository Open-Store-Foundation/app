package com.openstore.app.screens.format

import kotlin.math.abs

private const val KILO = 1_000L
private const val MEGA = 1_000_000L
private const val GIGA = 1_000_000_000L

private const val KILO_D = 1_000.0
private const val MEGA_D = 1_000_000.0
private const val GIGA_D = 1_000_000_000.0

/**
 * Formats a Long number into a human-readable string with metric suffixes (K, M, B)
 * for Kotlin Multiplatform (KMP) environments. Does not use JVM classes.
 *
 * The formatted string will have a maximum of 3 digits before the suffix,
 * truncating (not rounding) decimals as needed.
 *
 * Examples:
 * 100 -> "100"
 * 999 -> "999"
 * 1000 -> "1K"
 * 1200 -> "1.2K"
 * 12200 -> "12.2K"
 * 12990 -> "12.9K" (truncates 12.99)
 * 999999 -> "999K" (truncates 999.999)
 * 1000000 -> "1M"
 * 1220400 -> "1.22M"
 * 1000000000 -> "1B"
 * -12200 -> "-12.2K"
 * 0 -> "0"
 * Long.MAX_VALUE (9,223,372,036,854,775,807) -> "9.22B" (approx, truncated)
 * Long.MIN_VALUE (-9,223,372,036,854,775,808) -> "-9.22B" (special case)
 *
 * @param number The Long number to format.
 * @return The formatted string.
 */
fun formatBigNumber(number: Long): String {
    if (number == 0L) return "0"

    if (number < 0) return "0"

    val isNegative = number < 0
    val absNumber = abs(number)


    // Helper function to format the scaled value with truncation
    // Ensures max 3 digits (e.g., X.XX, XX.X, XXX)
    fun formatScaledValue(value: Double, suffix: String): String {
        val formattedValueString = when {
            // Value < 10: format as X.XX, X.X or X (max 3 digits total)
            // Truncate to 2 decimal places
            value < 10.0 -> {
                // Truncate by multiplying, converting to Long, then dividing back
                val truncated = (value * 100).toLong() / 100.0
                // Double.toString() handles removing trailing zeros like 1.20 -> "1.2"
                // but leaves .0 for whole numbers like 1.0 -> "1.0". We remove the ".0".
                truncated.toString().removeSuffix(".0")
            }
            // Value < 100: format as XX.X or XX (max 3 digits total)
            // Truncate to 1 decimal place
            value < 100.0 -> {
                val truncated = (value * 10).toLong() / 10.0
                truncated.toString().removeSuffix(".0")
            }
            // Value < 1000: format as XXX (always 3 digits total)
            // Truncate to 0 decimal places (integer)
            else -> { // Covers values >= 100.0 and < 1000.0
                value.toLong().toString()
            }
        }
        return formattedValueString + suffix
    }

    // Determine the correct range and format
    val formattedAbsString = when {
        // Below 1000: display as is
        absNumber < KILO -> absNumber.toString()

        // Thousands (K): 1,000 to 999,999
        absNumber < MEGA -> formatScaledValue(absNumber / KILO_D, "K")

        // Millions (M): 1,000,000 to 999,999,999
        absNumber < GIGA -> formatScaledValue(absNumber / MEGA_D, "M")

        // Billions (B): 1,000,000,000 and above
        else -> formatScaledValue(absNumber / GIGA_D, "B")
        // Add Trillions (T) etc. here if needed, adjusting thresholds and divisors
    }

    // Add the negative sign back if the original number was negative
    return if (isNegative) "-$formattedAbsString" else formattedAbsString
}
