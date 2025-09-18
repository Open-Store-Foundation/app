package com.openstore.app.mvi.contract.internal

import androidx.annotation.AnyThread
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.thread.StateThread

interface Stateful<S : MviState> {

    @StateThread
    fun Stateful<S>.obtainState(): S

    @StateThread
    fun <T : S> Stateful<S>.obtainSpecificState() : T?

    @AnyThread
    fun Stateful<S>.setState(
        mapper: @StateThread S.() -> S
    )

    @AnyThread
    fun <State : S> Stateful<S>.setState(
        failure: @StateThread (() -> Unit)? = null,
        mapper: @StateThread State.() -> S,
    )
}
