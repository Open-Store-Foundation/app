package com.openstore.app.core.os

import kotlinx.browser.window

actual object Os {
    actual fun platform(): Platform {
        val protocol = window.location.protocol
        val platform = when {
            // Standard Web Protocols
            protocol.startsWith("http") -> Platform.Web
            // Extension Protocols
            protocol.startsWith("chrome-extension") -> Platform.Extension
            else -> throw IllegalStateException("Unknown platform: $protocol")
        }
        return platform
    }
}