package com.openstore.app.mvi

import androidx.annotation.AnyThread
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.internal.Stateful
import com.openstore.app.mvi.thread.MviThread
import com.openstore.app.mvi.thread.StateThread

abstract class MviFeatureDelegate<A : MviAction, S : MviState>(
    private val feature: MviFeature<in A, S, *>
) : Stateful<S> {

    protected val mainScope = feature.scopes.main
    protected val bgScope = feature.scopes.bg

    protected abstract suspend fun executeAction(action: A)

    @StateThread
    override fun Stateful<S>.obtainState(): S {
        return feature.internalObtainState()
    }

    @StateThread
    override fun <T : S> Stateful<S>.obtainSpecificState(): T? {
        return feature.internalOnState()
    }

    @StateThread
    suspend fun execute(action: A) {
        MviThread.State.require()
        executeAction(action)
    }

    @AnyThread
    override fun Stateful<S>.setState(mapper: @StateThread S.() -> S) {
        feature.internalSetState(mapper)
    }

    @AnyThread
    override fun <State : S> Stateful<S>.setState(
        failure: @StateThread (() -> Unit)?,
        mapper: @StateThread State.() -> S,
    ) {
        feature.internalSetState(failure, mapper)
    }
}
