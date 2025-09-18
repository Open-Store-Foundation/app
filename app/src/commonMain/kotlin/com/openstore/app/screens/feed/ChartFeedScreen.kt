package com.openstore.app.screens.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openstore.app.common.strings.RString
import com.openstore.app.data.Asset
import com.openstore.app.data.store.ChartFeedStatus
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.paging.PagingStage
import com.openstore.app.paging.rememberPaging
import com.openstore.app.paging.rememberStage
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.cells.ObjCell
import com.openstore.app.screens.list.ObjListCell
import com.openstore.app.ui.cells.AvoirErrorCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.component.AvoirAppBar
import com.openstore.app.ui.component.AvoirEmptyScreen
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.icons.CommonIcons
import com.openstore.app.ui.icons.Search
import com.openstore.app.ui.list.errorItem
import com.openstore.app.ui.list.loadingItem
import foundation.openstore.kitten.android.withViewModel
import openstore.core.strings.generated.resources.Connecting
import openstore.core.strings.generated.resources.Sync
import openstore.core.strings.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartFeedScreen(
    onObject: (Asset) -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
) {
    val feature = StoreInjector.withViewModel {
        provideChartFeedFeature()
    }

    val status by feature.state.status.observeSafeState()

    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(initialHeightOffsetLimit = 0f),
    )

    AvoirScaffold(
        topBar = {
            AvoirAppBar(
                title = when (status) {
                    ChartFeedStatus.Syncing -> stringResource(RString.Sync)
                    ChartFeedStatus.Connecting -> stringResource(RString.Connecting)
                    ChartFeedStatus.Ready -> stringResource(RString.app_name)
                    null -> ""
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = {
                            onSettings.invoke()
                        }) {
                            DefaultItemIcon(
                                vector = Icons.Filled.Settings,
                                size = 24.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        IconButton(onClick = {
                            onSearch.invoke()
                        }) {
                            DefaultItemIcon(
                                CommonIcons.Search,
                                size = 22.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
                scrollBehavior = scroll,
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            val paging = feature.paging.rememberPaging()
            val stage = feature.paging.rememberStage()

            if (paging.isEmpty()) {
                when {
                    stage == PagingStage.End -> AvoirEmptyScreen()
                    stage == PagingStage.Error -> AvoirErrorCell { feature.sendAction(ChartFeedAction.Retry) }
                    else -> AvoirLoaderScreen()
                }
            } else {
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
                        PagingStage.Error -> errorItem { feature.sendAction(ChartFeedAction.Retry) }
                        PagingStage.Loading -> loadingItem()
                        else -> Unit
                    }
                }
            }
        }
    }
}
