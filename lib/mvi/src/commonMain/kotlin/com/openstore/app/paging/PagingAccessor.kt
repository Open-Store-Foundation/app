package com.openstore.app.paging

import androidx.annotation.MainThread
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.openstore.app.mvi.thread.MviThread
import com.openstore.app.paging.collection.MviPagingMutationProxy

data class PagingAccessor<Item>(
    val state: MviPagingMutationProxy<Item>,
    private val onScrollPosition: (Int) -> Unit
) {

    @MainThread
    fun size(): Int {
        MviThread.Main.require()
        return state.size()
    }

    @MainThread
    fun isEmpty(): Boolean {
        MviThread.Main.require()
        return state.size() == 0
    }

    @MainThread
    fun get(index: Int): Item? {
        MviThread.Main.require()
        return state.getByPosition(index)
    }

    @MainThread
    fun require(index: Int): Item {
        MviThread.Main.require()
        return get(index)!!
    }

    @MainThread
    fun scroll(index: Int): Item? {
        MviThread.Main.require()
        onScrollPosition.invoke(index)
        return state.getByPosition(index)
    }
}
