package com.openstore.app.screens.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.Router
import com.openstore.app.data.report.ReportCategoryId
import com.openstore.app.data.report.ReportRepo
import com.openstore.app.data.report.toRString
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import com.openstore.app.ui.cells.TextCell
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import foundation.openstore.kitten.android.withViewModel
import org.jetbrains.compose.resources.stringResource

sealed interface ReportCategoryAction : MviAction {
    object Load : ReportCategoryAction
}

data class ReportCategoryState(
    val categories: List<ReportCategoryId>
) : MviState

class ReportCategoryViewState(
    val categories: MviProperty<List<ReportCategoryId>>
) : MviViewState

class ReportCategoryFeature(
    val objAddress: String,
    private val reportRepo: ReportRepo
) : MviFeature<ReportCategoryAction, ReportCategoryState, ReportCategoryViewState>(
    initState = ReportCategoryState(emptyList()),
    initAction = ReportCategoryAction.Load
) {

    override fun createViewState(): ReportCategoryViewState {
        return buildViewState {
            ReportCategoryViewState(mviProperty { it.categories })
        }
    }

    override suspend fun executeAction(action: ReportCategoryAction) {
        when (action) {
            is ReportCategoryAction.Load -> {
                setState { copy(categories = reportRepo.getCategories()) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCategoryScreen(
    navigator: NavHostController,
    onCategory: (String, ReportCategoryId) -> Unit,
    onSubmit: (String, ReportCategoryId) -> Unit,
) {
    val feature = StoreInjector.withViewModel {
        val data = it.state.toRoute<Router.ReportCategory>()
        provideReportCategoryFeature(data)
    }

    val categories by feature.state.categories.observeSafeState()

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                onNavigateUp = { navigator.navigateUp() },
                title = "Choose category"
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(top = 25.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                count = categories.size,
                key = { index -> categories[index].ordinal },
                contentType = { 0 }
            ) { index ->
                val item = categories[index]
                TextCell(
                    title = stringResource(item.toRString()),
                    onClick = {
                        when (item) {
                            ReportCategoryId.OTHER -> onSubmit(feature.objAddress, item)
                            else -> onCategory(feature.objAddress, item)
                        }
                    },
                    maxLinesTitle = 2,
                    minHeight = 70.dp,
                )
            }
        }
    }
}

