package com.openstore.app.core.net

import io.ktor.client.plugins.logging.Logger

actual class NetLogger : Logger {

    private var isEnabled = false

    override fun log(message: String) {
        if (isEnabled) {
            println("WalletKit: $message")
        }
    }

    internal actual fun isEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
}
