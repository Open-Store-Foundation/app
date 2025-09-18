package com.openstore.app.core.net

import io.ktor.http.parseUrl

fun String?.isUrl(): Boolean {
    if (this == null) return false
    val result = runCatching { parseUrl(this) }
    val url = result.getOrNull()
    return this.isNotBlank()
        && url != null
        && url.host.isNotBlank()
        && url.protocolOrNull != null
}