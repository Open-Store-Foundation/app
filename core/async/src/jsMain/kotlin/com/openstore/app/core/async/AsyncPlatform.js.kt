package com.openstore.app.core.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object AsyncPlatform {
    actual val Io: CoroutineDispatcher = Dispatchers.Default
    actual fun createDispatcherPool(name: String): CoroutineDispatcher {
        return Dispatchers.Default
    }
}

actual object ThreadChecker {
    actual fun isMainThread(): Boolean {
        return true
    }

    actual fun isStateThread(): Boolean {
        return true
    }

    actual fun isStateDiffThread(): Boolean {
        return true
    }
}