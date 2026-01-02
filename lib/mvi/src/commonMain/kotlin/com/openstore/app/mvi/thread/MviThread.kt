package com.openstore.app.mvi.thread

import androidx.annotation.AnyThread
import com.openstore.app.log.L
import com.openstore.app.mvi.Mvi
import com.openstore.app.core.async.Async

internal enum class MviThread {

    Main, State, Computation;

    @AnyThread
    fun require() {
        if (!Mvi.config().useThreadCheck) {
            return
        }

        if (!check()) {
            L.e(IllegalStateException("Call method expected on $name"))
            finish()
        }
    }

    @AnyThread
    fun check(): Boolean {
        return when (this) {
            Main -> Async.isMainThread()
            State -> Async.isStateThread()
            Computation -> Async.isStateDiffThread()
        }
        return true
    }

    @AnyThread
    fun finish() {
//        handler.postDelayed(
//            { exitProcess(1) },
//            1000L
//        )
    }
}

