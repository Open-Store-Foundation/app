package com.openstore.app.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.paging.PagingStage
import com.openstore.app.paging.rememberPaging
import com.openstore.app.paging.rememberStage
import com.openstore.app.data.Asset
import com.openstore.app.log.L
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.categories.AvoirSearchToolbar
import com.openstore.app.screens.cells.ObjCell
import com.openstore.app.ui.cells.AvoirErrorCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.component.AvoirEmptyScreen
import com.openstore.app.ui.lifecycle.OnPause
import com.openstore.app.ui.lifecycle.OnResume
import com.openstore.app.ui.list.errorItem
import com.openstore.app.ui.list.loadingItem
import com.openstore.app.ui.text.emptyTextValue
import com.openstore.app.ui.workround.rememberTextFieldValue
import foundation.openstore.kitten.android.withViewModel

@Composable
fun SearchScreen(
    navigator: NavHostController,
    onObject: (Asset) -> Unit,
) {
    val feature = StoreInjector.withViewModel { provideSearchFeature() }

    var value by rememberTextFieldValue()
    val focus = remember { FocusRequester() }

    OnResume {
        focus.requestFocus()
    }

    OnPause {
        focus.freeFocus()
    }

    AvoirScaffold(
        topBar = {
            AvoirSearchToolbar(
                value = value,
                onValueChange = {
                    value = it
                    feature.sendAction(SearchAction.Search(it.text))
                },
                onClear = {
                    value = emptyTextValue()
                    feature.sendAction(SearchAction.Search(value.text))
                },
                onNavigateUp = { navigator.navigateUp() },
                focus = focus,
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            val paging = feature.paging.rememberPaging()
            val stage = feature.paging.rememberStage()

            if (paging.isEmpty()) {
                when {
                    stage == PagingStage.End && value.text.isNotEmpty() -> AvoirEmptyScreen()
                    stage == PagingStage.None || stage == PagingStage.End -> Unit
                    stage == PagingStage.Error -> AvoirErrorCell { feature.sendAction(SearchAction.Retry) }
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
                            is SearchCell.Obj -> {
                                ObjCell(item.target, onClick = { onObject(item.target) })
                            }
                        }
                    }

                    when (stage) {
                        PagingStage.Error -> errorItem { feature.sendAction(SearchAction.Retry) }
                        PagingStage.Loading -> loadingItem()
                        else -> Unit
                    }
                }
            }
        }
    }
}

