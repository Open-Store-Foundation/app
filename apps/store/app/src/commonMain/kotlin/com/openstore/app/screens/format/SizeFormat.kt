package com.openstore.app.screens.format

/**
 * Formats a size in bytes into a human-readable string with units (KB, MB, GB, etc.).
 *
 * Uses base 1024 for calculations (like operating systems often do, KiB, MiB, GiB)
 * but displays the common SI-like units (KB, MB, GB).
 *
 * Formats to one decimal place if the number is not whole after conversion,
 * otherwise shows the whole number.
 *
 * Examples:
 * ```
 * formatBytes(0)       // "0 B"
 * formatBytes(100)     // "100 B"
 * formatBytes(1024)    // "1 KB"
 * formatBytes(1536)    // "1.5 KB"  (1024 * 1.5)
 * formatBytes(12345)   // "12.1 KB" (12345 / 1024 = 12.055...)
 * formatBytes(1048576) // "1 MB"    (1024 * 1024)
 * formatBytes(1258291) // "1.2 MB"  (approx 1.2 * 1024 * 1024)
 * formatBytes(128974848) // "123 MB" (123 * 1024 * 1024)
 * ```
 *
 * @param bytes The size in bytes. Handles non-negative values. Negative values are treated as 0.
 * @return A formatted string representing the size (e.g., "1.5 KB", "123 MB").
 */
fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"

    val base = 1024.0
    val units = listOf("B", "KB", "MB", "GB", "TB", "PB", "EB")

    var value = bytes.toDouble()
    var unitIndex = 0

    // Find the largest unit where the value is >= 1.0
    // Stop before exceeding the units list
    while (value >= base && unitIndex < units.size - 1) {
        value /= base
        unitIndex++
    }

    // Use the platform-specific formatter to get 1 decimal place
    // This handles rounding and ensures '.' as the separator for the next step
    val formattedValue = if (value < 100) {
        (value * 10).toLong() / 10f
    } else {
        value.toInt().toFloat()
    }

    val finalFormattedValue = formattedValue.toString().removeSuffix(".0")

    return "$finalFormattedValue ${units[unitIndex]}"
}
