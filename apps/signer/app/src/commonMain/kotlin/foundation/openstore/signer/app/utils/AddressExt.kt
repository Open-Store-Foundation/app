package foundation.openstore.signer.app.utils

private const val START_LENGTH = 9
private const val END_LENGTH = 6

fun String.shrinkAddress(): String {
    if (length <= START_LENGTH + END_LENGTH) {
        return this
    }

    val start = substring(0, START_LENGTH)
    val end = substring(length - END_LENGTH, length)

    return "$start...$end"
}