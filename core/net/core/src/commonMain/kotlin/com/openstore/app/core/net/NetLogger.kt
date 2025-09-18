package com.openstore.app.core.net

import io.ktor.client.plugins.logging.Logger

expect class NetLogger : Logger {
    internal fun isEnabled(enabled: Boolean)
}
