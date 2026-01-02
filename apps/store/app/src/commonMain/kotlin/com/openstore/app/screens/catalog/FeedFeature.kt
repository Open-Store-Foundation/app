package com.openstore.app.screens.catalog

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.data.CategoryId
import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.Asset
import com.openstore.app.data.TitleType
import com.openstore.app.data.store.StoreInteractor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

sealed interface FeedAction : MviAction {
    data object Init : FeedAction
    data object Refresh : FeedAction
}

data class FeedState(
    val feed: List<FeedCell> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isRefreshing: Boolean = false,
) : MviState

class FeedViewState(
    val feed: MviProperty<List<FeedCell>>,
    val isLoading: MviProperty<Boolean>,
    val isError: MviProperty<Boolean>,
    val isRefreshing: MviProperty<Boolean>,
) : MviViewState

sealed class FeedCell(
    val type: String,
    val id: String,
) {
    companion object {
        private const val TITLE = "Title"
        private const val HIGHLIGHT = "Highlight"
        private const val OBJ = "Obj"
        private const val BANNER = "Banner"
        private const val CAROUSEL = "Carousel"
        private const val CATEGORY = "Category"
    }

    class CoveredObj(val target: Asset, val cover: String)

    class Header(val title: TitleType) : FeedCell(TITLE, title.toString())
    class Highlight(val title: String, val target: Asset, val covers: List<String>) : FeedCell(HIGHLIGHT, "high_${target.id}")
    class Obj(val target: Asset) : FeedCell(OBJ, "${target.id}")
    class Banner(val items: List<CoveredObj>) : FeedCell(BANNER, "-2")
    class Carousel(val items: List<CoveredObj>) : FeedCell(CAROUSEL, "-1")
    class Cat(val category: CategoryId) : FeedCell(CATEGORY, "cat_${category.id}")
}

class FeedFeature(
    private val interactor: StoreInteractor,
) : MviFeature<FeedAction, FeedState, FeedViewState>(
    initState = FeedState(),
    initAction = FeedAction.Init,
) {

    override fun createViewState(): FeedViewState {
        return buildViewState {
            FeedViewState(
                mviProperty { it.feed },
                mviProperty { it.isLoading },
                mviProperty { it.isError },
                mviProperty { it.isRefreshing },
            )
        }
    }

    override suspend fun executeAction(action: FeedAction) {
        when (action) {
            FeedAction.Refresh,
            FeedAction.Init -> {
                setState {
                    copy(
                        isLoading = action == FeedAction.Init,
                        isRefreshing = action == FeedAction.Refresh,
                        isError = false,
                    )
                }

                bgScope.launch {
                    val cache = interactor.restoreFeed(
                        typeId = ObjTypeId.APP,
                    ).getOrNull()

                    val job = async {
                        interactor.loadFeed(
                            typeId = ObjTypeId.APP,
                        )
                    }

                    if (cache != null) {
                        setState {
                            copy(feed = cache, isRefreshing = true, isLoading = false)
                        }
                    }

                    val data = job.await()

                    setState {
                        when (val feed = data.getOrNull()) {
                            null -> copy(
                                isLoading = false,
                                isRefreshing = false,
                                isError = true,
                            )
                            else -> copy(
                                feed = feed,
                                isLoading = false,
                                isRefreshing = false,
                                isError = false
                            )
                        }
                    }
                }
            }
        }
    }
}