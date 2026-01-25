package com.openstore.app.screens.catalog

import com.openstore.app.Router
import com.openstore.app.data.db.ResponseRepo
import com.openstore.app.data.store.ChartFeedInteractor
import com.openstore.app.log.L
import com.openstore.app.paging.PagingConfig
import com.openstore.app.paging.PagingLoader
import com.openstore.app.paging.PagingResult
import com.openstore.app.paging.PagingStage
import com.openstore.app.paging.PagingState
import com.openstore.app.screens.list.ObjListCell

class ChartFeedLoader(
    private val data: Router.Objects,
    private val chartFeed: ChartFeedInteractor,
    private val responseRepo: ResponseRepo,
) : PagingLoader<Int, ObjListCell>{

    override suspend fun loadCache(config: PagingConfig<Int, ObjListCell>): List<ObjListCell>? {
        val result = responseRepo.restoreChart(data.typeId)
            ?.map { ObjListCell.Obj(it) }

        return result
    }

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
            val result = chartFeed.loadChart(limit, offset)
                .getOrThrow()
                .map { ObjListCell.Obj(it) }

            val nextKey = if (limit > result.size) null else offset + result.size

            return PagingResult.Page(
                data = result,
                nextKey = nextKey,
            )
        } catch (e: Throwable) {
            L.e(e)
            return PagingResult.Error(error = e)
        }
    }
}