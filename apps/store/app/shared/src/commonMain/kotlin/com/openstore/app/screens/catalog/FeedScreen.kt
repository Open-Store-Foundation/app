package com.openstore.app.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.openstore.app.common.strings.RString
import com.openstore.app.data.CategoryId
import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.Asset
import com.openstore.app.data.TitleType
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.cells.BannersCell
import com.openstore.app.screens.cells.CarouselCell
import com.openstore.app.screens.cells.CategoryCell
import com.openstore.app.screens.cells.HighlightCell
import com.openstore.app.screens.cells.ObjCell
import com.openstore.app.screens.cells.TitleTypeCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.component.AvoirErrorScreen
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.icons.CommonIcons
import com.openstore.app.ui.icons.Search
import foundation.openstore.kitten.android.withViewModel
import foundation.openstore.app.generated.resources.Sync
import foundation.openstore.app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    typeId: ObjTypeId,
    onSeeMore: (objTypeId: ObjTypeId, expandType: TitleType) -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    onObject: (Asset) -> Unit,
    onCategory: (CategoryId) -> Unit,
) {
    val feature = StoreInjector.withViewModel { provideFeedFeature() }
    val feed by feature.state.feed.observeSafeState()
    val isRefreshing by feature.state.isRefreshing.observeSafeState()

//    var periodIndex by remember {
//        mutableStateOf(0)
//    }

    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(initialHeightOffsetLimit = 0f),
    )

    AvoirScaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            Column {
                AvoirToolbar(
                    title = {
                        if (isRefreshing) {
                            Text(stringResource(RString.Sync))
                        } else {
                            Text(stringResource(RString.app_name))
                        }
                    },
                    navigationIcon = {
                        Spacer(Modifier.width(4.dp))

                        IconButton(onClick = {
                            onSettings.invoke()
                        }) {
                            DefaultItemIcon(
                                vector = Icons.Rounded.AccountCircle,
                                size = 28.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = {
                                onSearch.invoke()
                            }) {
                                DefaultItemIcon(
                                    CommonIcons.Search,
                                    size = 26.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    },
                    scrollBehavior = scroll,
                )

                // TODO v3 when there will be games
//                PrimaryTabRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    selectedTabIndex = periodIndex,
//                ) {
//                    val isAppsSelected = periodIndex == 0
//                    val isGamesSelected = periodIndex == 1
//                    Tab(selected = isAppsSelected, onClick = { periodIndex = 0 }, text = {
//                        Text(
//                            "Apps",
//                            style = MaterialTheme.typography.titleSmall,
//                            fontWeight = when {
//                                isAppsSelected -> FontWeight.SemiBold
//                                else -> FontWeight.Medium
//                            },
//                            color = when {
//                                isAppsSelected -> MaterialTheme.colorScheme.primary
//                                else -> MaterialTheme.colorScheme.onSurfaceVariant
//                            }
//                        )
//                    })
//
//                    Tab(selected = isGamesSelected, onClick = { periodIndex = 1 }, text = {
//                        Text(
//                            text = "Games",
//                            style = MaterialTheme.typography.titleSmall,
//                            fontWeight = when {
//                                isGamesSelected -> FontWeight.SemiBold
//                                else -> FontWeight.Medium
//                            },
//                            color = when {
//                                isGamesSelected -> MaterialTheme.colorScheme.primary
//                                else -> MaterialTheme.colorScheme.onSurfaceVariant
//                            }
//                        )
//                    })
//                }
            }
        },
        content = {
            Column(
                modifier = Modifier.padding(it)
                    .fillMaxWidth()
            ) {
                val isLoading by feature.state.isLoading.observeSafeState()
                val isError by feature.state.isError.observeSafeState()

                when {
                    isError -> AvoirErrorScreen { feature.sendAction(FeedAction.Init) }
                    isLoading -> AvoirLoaderScreen()
                    else -> {
//                        PullToRefreshBox(
//                            isRefreshing = isRefreshing,
//                            onRefresh = { feature.sendAction(FeedAction.Refresh) },
//                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 15.dp),
                            ) {
                                items(
                                    items = feed,
                                    contentType = { obj -> obj.type },
                                    key = { obj -> obj.id }
                                ) { cell ->
                                    when (cell) {
                                        is FeedCell.Header -> TitleTypeCell(
                                            cell.title,
                                            onSeeAll = { type -> onSeeMore(typeId, type) }
                                        )
                                        is FeedCell.Banner -> BannersCell(
                                            cell = cell,
                                            onObject = onObject
                                        )
                                        is FeedCell.Obj -> ObjCell(
                                            target = cell.target,
                                            onClick = { onObject(cell.target) })

                                        is FeedCell.Highlight -> HighlightCell(
                                            cell = cell,
                                            onClick = { onObject(cell.target) }
                                        )

                                        is FeedCell.Cat -> CategoryCell(
                                            target = cell.category,
                                            onClick = { onCategory(cell.category) })

                                        is FeedCell.Carousel -> CarouselCell(
                                            cell = cell,
                                            onClick = onObject
                                        )
                                    }
                                }
                            }
//                        }
                    }
                }
            }
        }
    )
}
