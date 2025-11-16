package foundation.openstore.gcip.core.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.Base64.PaddingOption

private val UrlSafe: Base64 = Base64.UrlSafe.withPadding(PaddingOption.ABSENT)

fun String.fromUrlBase64(): ByteArray {
    return UrlSafe.decode(this)
}

fun ByteArray.toUrlBase64Fmt(): String {
    return UrlSafe.encode(this)
}
