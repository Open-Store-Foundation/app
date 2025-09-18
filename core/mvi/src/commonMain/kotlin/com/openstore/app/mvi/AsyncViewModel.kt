package com.openstore.app.mvi

import androidx.lifecycle.ViewModel
import com.openstore.app.log.L
import com.openstore.app.core.async.Async
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

abstract class AsyncViewModel : ViewModel() {

    class Scopes(
        val main: CoroutineScope,
        val bg: CoroutineScope,
        val state: CoroutineScope
    )

    @Suppress("MemberVisibilityCanBePrivate")
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        L.d("Coroutine Exception Handler", throwable)
    }

    private val job = SupervisorJob()

    @Suppress("MemberVisibilityCanBePrivate")
    protected val commonScope = job + exceptionHandler

    // Bg
    protected val bgScope = CoroutineScope(commonScope + Async.Io)

    // State
    private val stateDispatcher = Async.stateDispatcher()
    protected val stateScope = CoroutineScope(commonScope + stateDispatcher)

    // Main
    @Suppress("MemberVisibilityCanBePrivate")
    protected val mainScope = CoroutineScope(commonScope + Dispatchers.Main.immediate)

    // Utils
    internal val scopes = Scopes(mainScope, bgScope, stateScope)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    protected suspend inline fun <S : Any> MutableStateFlow<S>.postState(setter: S.() -> S): Boolean {
        return this.tryEmit(setter(value))
    }

    protected suspend inline fun <reified S : Any> MutableStateFlow<in S>.postOnExactState(
        failure: () -> Unit = { },
        setter: S.() -> S,
    ): Boolean {
        val currentValue = value
        if (currentValue is S) {
            val newState = setter(currentValue)
            return this.tryEmit(newState)
        } else {
            failure()
            return false
        }
    }
}
