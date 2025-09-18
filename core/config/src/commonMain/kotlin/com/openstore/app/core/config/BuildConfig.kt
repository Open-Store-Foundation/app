package com.openstore.app.core.config

object BuildConfig {
    var isDebug: Boolean = true
        private set

    fun setIsDebug(debug: Boolean) { isDebug = debug }
}
