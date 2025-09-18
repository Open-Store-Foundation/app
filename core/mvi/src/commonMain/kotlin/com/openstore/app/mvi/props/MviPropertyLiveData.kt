package com.openstore.app.mvi.props

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.openstore.app.mvi.ChangeStrategy
import com.openstore.app.mvi.thread.ComputationThread
import com.openstore.app.mvi.thread.MainThread
import com.openstore.app.mvi.thread.MviThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.openstore.app.core.common.TestOnly

class MviPropertyLiveData<T : Any?>(
    initialValue: T,
    private val strategy: ChangeStrategy<T> = ChangeStrategy.Value()
) : MviMutableProperty<T> {

    private val state by lazy { MutableStateFlow(initialValue) }
    val uiState: StateFlow<T> = state.asStateFlow()

    @MainThread
    override fun setValue(newState: T) {
        MviThread.Main.require()
        state.value = newState
    }

    @ComputationThread
    override fun isChanged(oldState: T, state: T): Boolean {
        MviThread.Computation.require()
        return strategy.isChanged(oldState, state)
    }

    @MainThread
    override fun MviProperty<T>.data(): StateFlow<T> {
        return uiState
    }
}

@Composable
@MainThread
fun <T : Any?> MviProperty<T>.observeSafeState(): State<T> {
    return data()
        .collectAsState()
}

@get:TestOnly
val <T> MviProperty<T>.accessor: T
    get() = data().value
