package com.openstore.app.core.async

import com.openstore.app.core.async.Async.STATE_DIFF_THREAD_NAME
import com.openstore.app.core.async.Async.STATE_THREAD_NAME
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.newFixedThreadPoolContext
import platform.Foundation.NSThread

@OptIn(DelicateCoroutinesApi::class)
actual object AsyncPlatform {
    actual val Io: CoroutineDispatcher = Dispatchers.IO

    actual fun createDispatcherPool(name: String): CoroutineDispatcher {
        return newFixedThreadPoolContext(nThreads = 1, name = name)
    }
}

actual object ThreadChecker {
    actual fun isMainThread(): Boolean {
        return NSThread.currentThread.isMainThread
    }

    actual fun isStateThread(): Boolean {
//        return NSThread.currentThread.name?.startsWith(STATE_THREAD_NAME)
//            ?: return false // TODO
        return true;
    }

    actual fun isStateDiffThread(): Boolean {
//        return NSThread.currentThread.name?.startsWith(STATE_DIFF_THREAD_NAME)
//            ?: return false // TODO
        return true
    }
}