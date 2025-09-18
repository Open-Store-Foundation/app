package com.openstore.app.paging

import com.openstore.app.mvi.thread.BgThread
import com.openstore.app.mvi.thread.StateThread

interface PagingLoader<Id, Value> {

    companion object {
        const val NO_KEY = -1
    }

    @StateThread
    suspend fun loadCache(
        config: PagingConfig<Id, Value>,
    ): List<Value>? {
        return null
    }

    @BgThread
    suspend fun loadPage(
        config: PagingConfig<Id, Value>,
        stage: PagingStage,
        state: PagingState<Id>
    ): PagingResult<Id, Value>
}
