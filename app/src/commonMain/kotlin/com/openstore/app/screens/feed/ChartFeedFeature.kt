package com.openstore.app.screens.feed

import com.openstore.app.Router
import com.openstore.app.data.db.ResponseRepo
import com.openstore.app.data.store.ChartFeedInteractor
import com.openstore.app.data.store.ChartFeedStatus
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.paging.MviPaging
import com.openstore.app.paging.PagingConfig
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface ChartFeedAction : MviAction {
    data object Refresh : ChartFeedAction
    data object Retry : ChartFeedAction
}

data class ChartFeedState(
    val status: ChartFeedStatus?
) : MviState

class ChartFeedViewState(
    val status: MviProperty<ChartFeedStatus?>
) : MviViewState

class ChartFeedFeature(
    chartFeed: ChartFeedInteractor,
    respRepo: ResponseRepo,
) : MviFeature<ChartFeedAction, ChartFeedState, ChartFeedViewState>(
    initState = ChartFeedState(status = null)
) {

    private val loader = ChartFeedLoader(
        data = Router.Objects(),
        chartFeed = chartFeed,
        responseRepo = respRepo,
    )

    val paging = MviPaging(
        config = PagingConfig(
            initPage = 20,
            pageSize = 20,
            prefetchSize = 5,
            stateScope = stateScope
        ),
        loader = loader,
    )

    init {
        chartFeed.observer.onEach { status ->
            setState { copy(status = status) }
        }.launchIn(bgScope)

        paging.preload()
    }

    override fun createViewState(): ChartFeedViewState {
        return buildViewState {
            ChartFeedViewState(mviProperty { it.status })
        }
    }

    override suspend fun executeAction(action: ChartFeedAction) {
        when (action) {
            is ChartFeedAction.Refresh -> {
                mainScope.launch {
                    paging.refresh()
                }
            }
            is ChartFeedAction.Retry -> {
                mainScope.launch {
                    paging.recover()
                }
            }
        }
    }
}
