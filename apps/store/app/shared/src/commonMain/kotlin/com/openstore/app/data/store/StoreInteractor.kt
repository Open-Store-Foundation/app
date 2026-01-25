package com.openstore.app.data.store

import com.openstore.app.data.CategoryId
import com.openstore.app.data.Feed
import com.openstore.app.data.Asset
import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.db.ObjectDao
import com.openstore.app.screens.catalog.FeedCell

interface StoreInteractor {
    suspend fun restoreFeed(typeId: ObjTypeId): Result<List<FeedCell>?>
    suspend fun loadFeed(typeId: ObjTypeId): Result<List<FeedCell>>
}

class StoreInteractorDefault(
    private val storeService: StoreService,
    private val objDao: ObjectDao,
) : StoreInteractor {

    override suspend fun restoreFeed(typeId: ObjTypeId): Result<List<FeedCell>?> {
        return runCatching {
            val result = storeService.restoreFeed(typeId)
                ?: return@runCatching null

            val (cells, _) = getCellsAndObjs(result)

            cells
        }
    }

    override suspend fun loadFeed(typeId: ObjTypeId): Result<List<FeedCell>> {
        return runCatching {
            val result = storeService.loadFeed(typeId)
            val (cells, objs) = getCellsAndObjs(result)

            objDao.insertAll(objs)

            cells
        }
    }

    private fun getCellsAndObjs(feed: Feed): Pair<List<FeedCell>, List<Asset>> {
        val result = mutableListOf<FeedCell>()

        val objs = mutableListOf<Asset>()

        feed.sections.forEach {
            when (it) {
                is Feed.Section.Header -> {
                    result.add(FeedCell.Header(it.title))
                }
                is Feed.Section.Banner -> {
                    objs.addAll(it.assets)

                    val covers = it.assets.zip(it.covers)
                        .map { (obj, cover) -> FeedCell.CoveredObj(obj, cover) }

                    result.add(FeedCell.Banner(covers))
                }
                is Feed.Section.Highlight -> {
                    result.add(FeedCell.Highlight("Our choice", it.target, it.covers))
                    objs.add(it.target)
                }
                is Feed.Section.HList -> {
                    if (it.title != null) {
                        result.add(FeedCell.Header(it.title))
                    }

                    val covers = it.assets.zip(it.covers)
                        .map { (obj, cover) -> FeedCell.CoveredObj(obj, cover) }

                    result.add(FeedCell.Carousel(covers))
                    objs.addAll(it.assets)
                }
                is Feed.Section.Categories -> {
                    if (it.title != null) {
                        result.add(FeedCell.Header(it.title))
                    }

                    for (category in it.categories) {
                        result.add(FeedCell.Cat(CategoryId.requireById(category)))
                    }
                }
                is Feed.Section.VList -> {
                    if (it.title != null) {
                        result.add(FeedCell.Header(it.title))
                    }

                    for (obj in it.assets) {
                        result.add(FeedCell.Obj(obj))
                    }

                    objs.addAll(it.assets)
                }
            }
        }

        return result to objs
    }
}