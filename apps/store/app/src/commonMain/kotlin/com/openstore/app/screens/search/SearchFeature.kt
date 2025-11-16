package com.openstore.app.screens.search

import com.openstore.app.core.common.isEvmAddress
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviSubject
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.paging.ItemCell
import com.openstore.app.paging.MviPaging
import com.openstore.app.paging.PagingConfig
import com.openstore.app.paging.PagingLoader
import com.openstore.app.paging.PagingResult
import com.openstore.app.paging.PagingStage
import com.openstore.app.paging.PagingState
import com.openstore.app.data.Asset
import com.openstore.app.data.sources.AppChainService
import com.openstore.app.data.store.ObjectRepo
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

sealed interface SearchAction : MviAction {
    data class Search(val query: String) : SearchAction
    data object Retry : SearchAction
}

class SearchState : MviState
class SearchViewState : MviViewState

@OptIn(ExperimentalAtomicApi::class)
class SearchFeature(
    private val objRepo: ObjectRepo,
    private val appChainService: AppChainService,
) : MviFeature<SearchAction, SearchState, SearchViewState>(initState = SearchState()) {

    private val consumer = MviSubject<String>()
    private val query = AtomicReference("")

    val paging = MviPaging(
        config = PagingConfig(
            initPage = 20,
            pageSize = 20,
            prefetchSize = 5,
            stateScope = stateScope
        ),
        loader = SearchPagingLoader(
            objRepo = objRepo,
            appChainService = appChainService,
            queryProvider = { query.load() },
        )
    )

    init {
        consumer.events
            .debounce(400L)
            .distinctUntilChanged()
            .mapLatest {
                query.store(it)
                paging.refresh()
            }
            .launchIn(mainScope)

        paging.preload()
    }

    override fun createViewState(): SearchViewState {
        return buildViewState {
            SearchViewState()
        }
    }

    override suspend fun executeAction(action: SearchAction) {
        when (action) {
            is SearchAction.Search -> {
                consumer.emit(action.query)
            }

            is SearchAction.Retry -> {
                paging.recover()
            }
        }
    }
}

sealed class SearchCell(
    override val type: String,
    override val id: Long
) : ItemCell<Long> {

    companion object {
        private const val OBJ = "Object"
    }

    class Obj(val target: Asset) : SearchCell(OBJ, target.id)
}

class SearchPagingLoader(
    private val appChainService: AppChainService,
    private val objRepo: ObjectRepo,
    private val queryProvider: () -> String
) : PagingLoader<Int, SearchCell> {

    override suspend fun loadPage(
        config: PagingConfig<Int, SearchCell>,
        stage: PagingStage,
        state: PagingState<Int>
    ): PagingResult<Int, SearchCell> {
        val offset = state.nextKey ?: 0
        val limit = if (offset > 0) config.pageSize else config.initPage
        val query = queryProvider.invoke()

        if (query.length < 3) {
            return PagingResult.Page(emptyList(), nextKey = null)
        }

        return search(query, offset, limit)
    }

    private suspend fun search(
        query: String,
        offset: Int,
        limit: Int,
    ): PagingResult<Int, SearchCell> {
        try {
            if (query.isEvmAddress()) {
                val obj = appChainService.findAsset(query).getOrNull()
                    ?.let { listOf(SearchCell.Obj(it)) }
                    ?: emptyList()

                return PagingResult.Page(obj, nextKey = null)
            } else {
                val result = objRepo.search(query, limit, offset) // TODO when many apps pass type
                    .getOrElse { emptyList() } // TODO v2 Handle
                    .map { SearchCell.Obj(it) }

                val nextKey = if (limit > result.size) null else offset + result.size

                return PagingResult.Page(
                    data = result,
                    nextKey = nextKey,
                )
            }
        } catch (e: Throwable) {
            return PagingResult.Error(error = e)
        }
    }
}