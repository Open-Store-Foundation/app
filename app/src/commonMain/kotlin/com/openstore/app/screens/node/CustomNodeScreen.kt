package com.openstore.app.screens.node


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.openstore.app.data.node.AppNodes
import com.openstore.app.data.node.CustomNodeType
import com.openstore.app.data.node.NodeRepo
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.cells.TextCell
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.lifecycle.OnResume
import foundation.openstore.kitten.android.withViewModel

sealed interface CustomNodeAction : MviAction {
    object Refresh : CustomNodeAction
}

data class CustomNodeState(
    val nodes: AppNodes? = null
) : MviState

class CustomNodeViewState(
    val nodes: MviProperty<AppNodes?>
) : MviViewState

class CustomNodeFeature(
    private val nodeRepo: NodeRepo
) : MviFeature<CustomNodeAction, CustomNodeState, CustomNodeViewState>(
    initState = CustomNodeState(),
) {
    override fun createViewState(): CustomNodeViewState {
        return buildViewState {
            CustomNodeViewState(mviProperty { it.nodes })
        }
    }

    override suspend fun executeAction(action: CustomNodeAction) {
        when (action) {
            CustomNodeAction.Refresh -> {
                val nodes = nodeRepo.restoreAppNodes()
                setState { copy(nodes = nodes) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomNodeScreen(
    navigator: NavHostController,
    onAddCustom: (CustomNodeType) -> Unit,
) {
    val feature = StoreInjector.withViewModel { provideCustomNodeFeature() }
    val nodes by feature.state.nodes.observeSafeState()
    val safeNodes = nodes

    OnCreate {
        feature.sendAction(CustomNodeAction.Refresh)
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                onNavigateUp = { navigator.navigateUp() },
                title = "Nodes",
            )
        }
    ) {
        Column(
            Modifier.padding(it)
                .padding(vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(19.dp)
        ) {
            if (safeNodes != null) {
                Column {
                    SmallTitleCell("Binance Smart Chain")
                    TextCell(
                        title = getTitle(safeNodes.isBscDefault),
                        subtitle = getUrl(safeNodes.bscUrl),
                        subtitleColor = MaterialTheme.colorScheme.tertiary,
                        onClick = { onAddCustom(CustomNodeType.BSC) },
                        maxLinesSubtitle = 2,
                    )
                }

                Column {
                    SmallTitleCell("Binance Greenfield")
                    TextCell(
                        title = getTitle(safeNodes.isGreenfieldDefault),
                        subtitle = getUrl(safeNodes.greenfieldUrl),
                        subtitleColor = MaterialTheme.colorScheme.tertiary,
                        onClick = { onAddCustom(CustomNodeType.GREENFIELD) },
                        maxLinesSubtitle = 2,
                    )
                }

                Column {
                    SmallTitleCell("API client")
                    TextCell(
                        title = getTitle(safeNodes.isApiDefault),
                        subtitle = getUrl(safeNodes.apiUrl),
                        subtitleColor = MaterialTheme.colorScheme.tertiary,
                        onClick = { onAddCustom(CustomNodeType.API) },
                        maxLinesSubtitle = 2,
                    )
                }
            }
        }
    }
}

@Composable
fun getTitle(isDefault: Boolean): String {
    return when (isDefault) {
        true -> "Default"
        false -> "Custom"
    }
}

@Composable
fun getUrl(customUrl: String?): String {
    return when (customUrl == null) {
        true -> "Hidden"
        false -> customUrl
    }
}