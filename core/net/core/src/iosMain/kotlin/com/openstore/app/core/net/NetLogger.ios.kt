package com.openstore.app.core.net

import io.ktor.client.plugins.logging.Logger

actual class NetLogger(
    private val delegate: Logger,
) : Logger {
    private var isEnabled = false

    override fun log(message: String) {
        if (isEnabled) {
            delegate.log(message)
        }
    }

    internal actual fun isEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
}
