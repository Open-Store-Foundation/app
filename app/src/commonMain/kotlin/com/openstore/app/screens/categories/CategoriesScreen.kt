package com.openstore.app.screens.categories

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.Router
import com.openstore.app.ui.components.AvoirEditText
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.data.CategoryId
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.component.AvoirDividerCustom
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.cells.CategoryCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.component.AvoirEmptyScreen
import com.openstore.app.ui.component.DefaultToolbarHeight
import com.openstore.app.ui.text.emptyTextValue
import com.openstore.app.ui.workround.rememberTextFieldValue
import foundation.openstore.kitten.android.withViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvoirSearchToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onNavigateUp: () -> Unit,
    onClear: () -> Unit,
    focus: FocusRequester = remember { FocusRequester() },
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    Column {
        Row(
            modifier = Modifier.windowInsetsPadding(windowInsets)
                .clipToBounds()
                .fillMaxWidth()
                .height(DefaultToolbarHeight)
                .padding(vertical = 5.dp)
        ) {
            IconButton(onClick = onNavigateUp) {
                DefaultItemIcon(
                    vector = Icons.AutoMirrored.Default.ArrowBack,
                )
            }

            AvoirEditText(
                modifier = Modifier
                    .focusable()
                    .focusRequester(focus)
                    .fillMaxWidth(),
                value = value,
                placeholder = { Text("Search by name or address") },
                onValueChange = onValueChange,
                trailingIcon = {
                    IconButton(onClear) {
                        DefaultItemIcon(
                            vector = Icons.Default.Close,
                        )
                    }
                }
            )
        }

        AvoirDividerCustom(Modifier.fillMaxWidth())
    }
}


@Composable
fun CategoriesScreen(
    navigator: NavHostController,
    onCategory: (CategoryId) -> Unit,
) {
    val feature = StoreInjector.withViewModel {
        val data = it.state.toRoute<Router.Categories>()
        provideCategoriesFeature(data)
    }

    var value by rememberTextFieldValue()
    val focus = remember { FocusRequester() }

    AvoirScaffold(
        topBar = {
            AvoirSearchToolbar(
                value = value,
                onValueChange = {
                    value = it
                    feature.sendAction(CategoriesAction.Query(value.text))
                },
                onClear = {
                    value = emptyTextValue()
                    feature.sendAction(CategoriesAction.Query(value.text))
                },
                onNavigateUp = {
                    navigator.navigateUp()
                },
                focus = focus,
            )
        }
    ) {
        val data by feature.state.categories.observeSafeState()
        val safeData = data

        when {
            safeData == null -> AvoirLoaderScreen()
            safeData.isEmpty() -> AvoirEmptyScreen()
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(it),
                    contentPadding = PaddingValues(top = 15.dp),
                ) {
                    items(
                        count = safeData.size,
                        key = { index -> safeData[index].id },
                        contentType = { 0 }
                    ) { index ->
                        val item = safeData[index]
                        CategoryCell(item, onClick = { onCategory(item) })
                    }
                }
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        focus.requestFocus()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        focus.freeFocus()
    }
}