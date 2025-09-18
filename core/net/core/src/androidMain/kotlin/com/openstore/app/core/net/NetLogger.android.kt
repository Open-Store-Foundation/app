package com.openstore.app.core.net

import android.util.Log
import io.ktor.client.plugins.logging.Logger

actual class NetLogger : Logger {

    private var isEnabled = false

    override fun log(message: String) {
        if (isEnabled) {
            Log.d("WalletKit", message)
        }
    }

    internal actual fun isEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
}
