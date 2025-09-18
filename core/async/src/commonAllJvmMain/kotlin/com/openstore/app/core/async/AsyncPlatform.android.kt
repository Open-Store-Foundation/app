package com.openstore.app.core.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher

actual object AsyncPlatform {

    actual val Io: CoroutineDispatcher = Dispatchers.IO

    actual fun createDispatcherPool(name: String): CoroutineDispatcher {
        return createDefaultPool(name)
            .asCoroutineDispatcher()
    }
}