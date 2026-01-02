package com.openstore.app.mvi.props

import com.openstore.app.mvi.thread.ComputationThread
import com.openstore.app.mvi.thread.MainThread
import kotlinx.coroutines.flow.StateFlow

interface MviProperty<T> {
    fun MviProperty<T>.data(): StateFlow<T>
}

interface MviMutableProperty<T> : MviProperty<T> {
    @MainThread
    fun setValue(newState: T)

    @ComputationThread
    fun isChanged(oldState: T, state: T): Boolean
}
