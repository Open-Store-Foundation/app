package com.openstore.app.screens.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.openstore.app.data.Asset
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.cells.ObjCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.component.AvoirButtonTiny
import com.openstore.app.ui.component.AvoirEmptyScreen
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirTinyLoader
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.DefaultSmallItemTitle
import foundation.openstore.kitten.android.withViewModel

// TODO loading state
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAppsScreen(
    navigator: NavHostController,
    onNavigateObject: (Asset) -> Unit,
) {
    val feature = StoreInjector.withViewModel { provideManageAppsFeature() }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                onNavigateUp = { navigator.navigateUp() },
                title = "Manage apps"
            )
        }
    ) {
        Column(
            Modifier.padding(it)
        ) {
            val apps by feature.state.assets.observeSafeState()
            val isRefresh by feature.state.isRefresh.observeSafeState()
            val isLoading by feature.state.isLoading.observeSafeState()
            val state = rememberPullToRefreshState()

            PullToRefreshBox(
                state = state,
                isRefreshing = isRefresh,
                onRefresh = { feature.sendAction(ManageAppsAction.Refresh) },
            ) {
                when {
                    isLoading -> {
                        AvoirLoaderScreen()
                    }
                    apps.isEmpty() -> {
                        AvoirEmptyScreen()
                    }
                    else -> {
                        LazyColumn(
                            Modifier.padding(vertical = 15.dp)
                                .fillMaxSize()
                        ) {
                            items(
                                apps,
                                key = { it.asset.address },
                                contentType = { Asset::class }
                            ) {
                                ObjCell(
                                    target = it.asset,
                                    valueContent = {
                                        when (it.hasNewVersion) {
                                            true -> DefaultSmallItemTitle(
                                                text = "Update",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            null -> AvoirTinyLoader()
                                            false -> Unit
                                        }
                                    }
                                ) { onNavigateObject(it.asset) }
                            }
                        }
                    }
                }
            }
        }
    }
}