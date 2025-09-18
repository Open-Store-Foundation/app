package com.openstore.app.screens.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.paging.PagingStage
import com.openstore.app.paging.rememberPaging
import com.openstore.app.paging.rememberStage
import com.openstore.app.Router
import com.openstore.app.data.Asset
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.cells.ObjCell
import com.openstore.app.ui.cells.AvoirErrorCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.component.AvoirEmptyScreen
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.list.errorItem
import com.openstore.app.ui.list.loadingItem
import org.jetbrains.compose.resources.stringResource
import foundation.openstore.kitten.android.withViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjListScreen(
    navigator: NavHostController,
    onObject: (Asset) -> Unit,
) {
    val feature = StoreInjector.withViewModel {
        val data = it.state.toRoute<Router.Objects>()
        provideObjListFeature(data)
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = when (feature.data.categoryId) {
                    null -> "Top chart"
                    else -> "Top in ${stringResource(feature.data.categoryId.displayRes())}"
                },
                onNavigateUp = {
                    navigator.navigateUp()
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            val state = rememberPullToRefreshState()
            val paging = feature.paging.rememberPaging()
            val stage = feature.paging.rememberStage()

            if (paging.isEmpty()) {
                when {
                    stage == PagingStage.End -> AvoirEmptyScreen()
                    stage == PagingStage.Error -> AvoirErrorCell { feature.sendAction(ObjListAction.Retry) }
                    else -> AvoirLoaderScreen()
                }
            } else {
                PullToRefreshBox(
                    state = state,
                    isRefreshing = stage == PagingStage.Refresh,
                    onRefresh = { feature.sendAction(ObjListAction.Refresh) },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 15.dp),
                    ) {
                        items(
                            count = paging.size(),
                            key = { index -> paging.require(index).id },
                            contentType = { index -> paging.require(index).type }
                        ) { index ->
                            val item = paging.scroll(index) ?: return@items

                            when (item) {
                                is ObjListCell.Obj -> {
                                    ObjCell(item.target, onClick = { onObject(item.target) })
                                }
                            }
                        }

                        when (stage) {
                            PagingStage.Error -> errorItem { feature.sendAction(ObjListAction.Retry) }
                            PagingStage.Loading -> loadingItem()
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}
