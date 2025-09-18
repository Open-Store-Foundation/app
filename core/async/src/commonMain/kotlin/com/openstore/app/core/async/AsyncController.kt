package com.openstore.app.core.async

import com.openstore.app.log.L
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class AsyncController {

    companion object {
        const val STATE_THREAD_NAME = "state-thread"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        L.d("Coroutine Exception Handler", throwable)
    }

    protected val job = SupervisorJob()

    @Suppress("MemberVisibilityCanBePrivate")
    protected val commonScope = job + exceptionHandler

    // Bg
    protected val bgScope = CoroutineScope(commonScope + Async.Io)
    protected val computationScope = CoroutineScope(commonScope + Async.Default)

    // State
    private val executionDispatcher = Async.createDispatcher(STATE_THREAD_NAME)
    protected val executionScope = CoroutineScope(commonScope + executionDispatcher)

    // Main
    @Suppress("MemberVisibilityCanBePrivate")
    protected val mainScope = CoroutineScope(commonScope + Dispatchers.Main.immediate)
}
