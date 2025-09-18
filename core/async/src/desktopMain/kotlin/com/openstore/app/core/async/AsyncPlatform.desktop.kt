package com.openstore.app.core.async.core.async

import com.openstore.app.core.async.Async.STATE_DIFF_THREAD_NAME
import com.openstore.app.core.async.Async.STATE_THREAD_NAME

actual object ThreadChecker {
    actual fun isStateThread(): Boolean {
        return Thread.currentThread().name
            .startsWith(STATE_THREAD_NAME)
    }

    actual fun isStateDiffThread(): Boolean {
        return Thread.currentThread().name
            .startsWith(STATE_DIFF_THREAD_NAME)
    }

    actual fun isMainThread(): Boolean {
        val currentThread = Thread.currentThread()
        return "main" == currentThread.name
    }
}