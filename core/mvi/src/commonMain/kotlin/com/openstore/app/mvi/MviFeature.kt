package com.openstore.app.mvi

import androidx.annotation.AnyThread
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.openstore.app.log.L
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.contract.internal.Stateful
import com.openstore.app.mvi.thread.MainThread
import com.openstore.app.mvi.thread.MviThread
import com.openstore.app.mvi.thread.StateThread
import com.openstore.app.core.config.BuildConfig
import com.openstore.app.core.async.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.openstore.app.core.common.TestOnly
import kotlin.concurrent.Volatile

@Stable
abstract class MviFeature<A : MviAction, S : MviState, VS : MviViewState>(
    initState: S,
    initAction: A? = null
) : AsyncViewModel(), Stateful<S> {

    // Compute
    private val stateDiffDispatcher = Async.stateDiffDispatchers()
    private val stateDiffScope = CoroutineScope(commonScope + stateDiffDispatcher)

    private val stateConsumer = MutableSharedFlow<S>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    @Volatile
    private var innerState = initState
    val state: VS get() = viewState

    @get:TestOnly
    val stateFlow get() = _stateFlow
    private var _stateFlow: Flow<S>

    private lateinit var binding: MviBinder<S>
    private val viewState: VS

    init {
        @Suppress("LeakingThis")
        viewState = createViewState()

        _stateFlow = stateConsumer
            .onEach { state ->
                try {
                    binding.update(state)
                } catch (e: Throwable) {
                    L.e(e)

                    if (BuildConfig.isDebug) {
                        MviThread.Computation.finish()
                    }
                }
            }
            .also {
                it.launchIn(stateDiffScope)
            }

        if (initAction != null) {
            sendAction(initAction)
        }
    }

    @AnyThread
    fun sendAction(action: A) {
        stateScope.launch {
            try {
                executeAction(action)
            } catch (e: Throwable) {
                L.e(e)
            }
        }
    }

    @StateThread
    protected abstract fun createViewState(): VS

    @StateThread
    protected abstract suspend fun executeAction(action: A)

    @MainThread
    protected fun buildViewState(filler: @MainThread MviBinder.Builder<S>.() -> VS): VS {
        val builder = MviBinder.Builder(innerState, viewModelScope)
        val state = builder.filler()
        binding = builder.build()
        return state
    }

    @StateThread
    internal fun internalObtainState(): S {
        MviThread.State.require()
        return innerState
    }

    @StateThread
    override fun Stateful<S>.obtainState(): S {
        MviThread.State.require()
        return innerState
    }

    @AnyThread
    internal fun internalSetState(mapper: @StateThread S.() -> S) {
        setState(mapper)
    }

    @AnyThread
    override fun Stateful<S>.setState(mapper: @StateThread S.() -> S) {
        if (MviThread.State.check()) {
            val newValue = innerState.mapper()
            updateState(newValue)
        } else {
            stateScope.launch {
                val newValue = innerState.mapper()
                updateState(newValue)
            }
        }
    }

    @AnyThread
    internal fun <State : S> internalSetState(
        failure: @StateThread (() -> Unit)? = null,
        mapper: @StateThread State.() -> S,
    ) {
        setState(failure, mapper)
    }

    @AnyThread
    override fun <State : S> Stateful<S>.setState(
        failure: @StateThread (() -> Unit)?,
        mapper: @StateThread State.() -> S,
    ) {
        if (MviThread.State.check()) {
            innerSetState(failure, mapper)
        } else {
            stateScope.launch {
                innerSetState(failure, mapper)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @AnyThread
    private fun <State : S> Stateful<S>.innerSetState(
        failure: @StateThread (() -> Unit)?,
        mapper: @StateThread State.() -> S,
    ) {
        val currentValue = obtainState()
        val state = currentValue as? State
        if (state != null) {
            val newValue = currentValue.mapper()
            updateState(newValue)
        } else {
            failure?.invoke()
        }
    }

    @StateThread
    internal fun <T : S> internalOnState() : T? {
        return obtainSpecificState()
    }

    @Suppress("UNCHECKED_CAST")
    @StateThread
    override fun <T : S> Stateful<S>.obtainSpecificState() : T? {
        return obtainState() as? T
    }

    @StateThread
    private fun <ES : S> updateState(newState: ES) {
        MviThread.State.require()
        innerState = newState
        stateConsumer.tryEmit(newState)
    }
}
