package com.openstore.app.screens.list

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.paging.ItemCell
import com.openstore.app.paging.MviPaging
import com.openstore.app.paging.PagingConfig
import com.openstore.app.paging.PagingLoader
import com.openstore.app.paging.PagingResult
import com.openstore.app.paging.PagingStage
import com.openstore.app.paging.PagingState
import com.openstore.app.Router
import com.openstore.app.data.Asset
import com.openstore.app.data.store.ObjectRepo
import kotlinx.coroutines.launch

sealed interface ObjListAction : MviAction {
    data object Refresh : ObjListAction
    data object Retry : ObjListAction
}

data class ObjListState(
    val name: String
) : MviState

class ObjListViewState(
    val name: MviProperty<String>
) : MviViewState

class ObjListFeature(
    val data: Router.Objects,
    private val objRepo: ObjectRepo,
) : MviFeature<ObjListAction, ObjListState, ObjListViewState>(initState = ObjListState("")) {

    val paging = MviPaging(
        config = PagingConfig(
            initPage = 20,
            pageSize = 20,
            prefetchSize = 5,
            stateScope = stateScope
        ),
        loader = ObjectsPagingLoader(
            data = data,
            objRepo = objRepo,
        )
    )

    init {
        paging.preload()
    }

    override fun createViewState(): ObjListViewState {
        return buildViewState {
            ObjListViewState(mviProperty { it.name })
        }
    }

    override suspend fun executeAction(action: ObjListAction) {
        when (action) {
            is ObjListAction.Refresh -> {
                mainScope.launch {
                    paging.refresh()
                }
            }
            is ObjListAction.Retry -> {
                mainScope.launch {
                    paging.recover()
                }
            }
        }
    }
}

sealed class ObjListCell(
    override val type: String,
    override val id: Long
) : ItemCell<Long> {

    companion object {
        private const val OBJ = "Object"
    }

    class Obj(val target: Asset) : ObjListCell(OBJ, target.id)
}

class ObjectsPagingLoader(
    private val data: Router.Objects,
    private val objRepo: ObjectRepo,
) : PagingLoader<Int, ObjListCell> {

    override suspend fun loadPage(
        config: PagingConfig<Int, ObjListCell>,
        stage: PagingStage,
        state: PagingState<Int>
    ): PagingResult<Int, ObjListCell> {
        val offset = state.nextKey ?: 0
        val limit = if (offset > 0) config.pageSize else config.initPage

        return search(offset, limit)
    }

    private suspend fun search(
        offset: Int,
        limit: Int,
    ): PagingResult<Int, ObjListCell> {
        try {
            val result = objRepo.loadChart(limit, offset, data.typeId, data.categoryId?.id)
                .getOrElse { emptyList() } // TODO v2 handle
                .map { ObjListCell.Obj(it) }

            val nextKey = if (limit > result.size) null else offset + result.size

            return PagingResult.Page(
                data = result,
                nextKey = nextKey,
            )
        } catch (e: Throwable) {
            return PagingResult.Error(error = e)
        }
    }
}