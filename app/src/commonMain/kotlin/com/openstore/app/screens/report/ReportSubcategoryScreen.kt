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
import com.openstore.app.data.report.ReportSubcategoryId
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

sealed interface ReportSubcategoryAction : MviAction {
    object Load : ReportSubcategoryAction
}

data class ReportSubcategoryState(
    val categories: List<ReportSubcategoryId>
) : MviState

class ReportSubcategoryViewState(
    val categories: MviProperty<List<ReportSubcategoryId>>
) : MviViewState

class ReportSubcategoryFeature(
    val objAddress: String,
    val categoryId: ReportCategoryId,
    private val reportRepo: ReportRepo
) : MviFeature<ReportSubcategoryAction, ReportSubcategoryState, ReportSubcategoryViewState>(
    initState = ReportSubcategoryState(emptyList()),
    initAction = ReportSubcategoryAction.Load
) {

    override fun createViewState(): ReportSubcategoryViewState {
        return buildViewState {
            ReportSubcategoryViewState(mviProperty { it.categories })
        }
    }

    override suspend fun executeAction(action: ReportSubcategoryAction) {
        when (action) {
            is ReportSubcategoryAction.Load -> {
                setState { copy(categories = reportRepo.getSubcategories(categoryId)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSubcategoryScreen(
    navigator: NavHostController,
    onSubcategory: (String, ReportCategoryId, ReportSubcategoryId?) -> Unit
) {
    val feature = StoreInjector.withViewModel {
        val data = it.state.toRoute<Router.ReportSubcategory>()
        provideReportSubcategoryFeature(data)
    }

    val subcategories by feature.state.categories.observeSafeState()

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                onNavigateUp = { navigator.navigateUp() },
                title = "Choose subcategory"
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
                count = subcategories.size,
                key = { index -> subcategories[index].ordinal },
                contentType = { 0 }
            ) { index ->
                val item = subcategories[index]

                TextCell(
                    title = stringResource(item.toRString()),
                    onClick = { onSubcategory(feature.objAddress, feature.categoryId, item) },
                    maxLinesTitle = 2,
                    minHeight = 70.dp,
                )
            }
        }
    }
}