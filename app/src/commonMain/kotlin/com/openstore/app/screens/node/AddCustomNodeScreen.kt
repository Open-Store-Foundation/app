package com.openstore.app.screens.node

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.Router
import com.openstore.app.core.net.isUrl
import com.openstore.app.data.node.CustomNodeType
import com.openstore.app.data.node.NodeRepo
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import com.openstore.app.ui.cells.AvoirTextFieldCell
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.OutlineAvoirButton
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.lifecycle.OnPause
import com.openstore.app.ui.lifecycle.OnResume
import com.openstore.app.ui.workround.rememberTextFieldValue
import foundation.openstore.kitten.android.withViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

sealed interface AddCustomNodeEvent {
    object Saved : AddCustomNodeEvent
}

sealed interface AddCustomNodeAction : MviAction {
    data class SetCustomNode(
        val url: String? = null
    ) : AddCustomNodeAction
}

data class AddCustomNodeState(
    val currentLink: String?
) : MviState

class AddCustomNodeViewState(
    val currentLink: MviProperty<String?>
) : MviViewState

class AddCustomNodeFeature(
    private val type: CustomNodeType,
    private val nodeRepo: NodeRepo
) : MviFeature<AddCustomNodeAction, AddCustomNodeState, AddCustomNodeViewState>(
    initState = AddCustomNodeState(runBlocking { nodeRepo.restoreNode(type) })
) {

    private val relay = MviRelay<AddCustomNodeEvent>()
    val events = relay.events

    override fun createViewState(): AddCustomNodeViewState {
        return buildViewState {
            AddCustomNodeViewState(mviProperty { it.currentLink })
        }
    }

    override suspend fun executeAction(action: AddCustomNodeAction) {
        when (action) {
            is AddCustomNodeAction.SetCustomNode -> {
                nodeRepo.setNode(type, action.url)
                relay.emit(AddCustomNodeEvent.Saved)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomNodeScreen(
    navigator: NavHostController
) {
    val feature = StoreInjector.withViewModel {
        val data = it.state.toRoute<Router.AddCustomNode>()
        provideAddCustomNodeFeature(data.type)
    }

    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val currentLink by feature.state.currentLink.observeSafeState()
    var nodeUrl by rememberTextFieldValue { currentLink }
    var isUrlValid by remember(currentLink) { mutableStateOf(currentLink.isUrl()) }

    val onNext = {
        if (!nodeUrl.text.isUrl()) {
            isUrlValid = false
        } else {
            keyboardController?.hide()
            feature.sendAction(AddCustomNodeAction.SetCustomNode(nodeUrl.text))
        }
    }

    OnResume {
        focusRequester.requestFocus()
    }

    OnPause {
        focusRequester.freeFocus()
    }

    OnCreate {
        feature.events.onEach { event ->
            when (event) {
                is AddCustomNodeEvent.Saved -> {
                    navigator.navigateUp()
                }
            }
        }.launchIn(scope)
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                onNavigateUp = { navigator.navigateUp() },
                title = "Nodes"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) {
        Column(
            Modifier.fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            AvoirTextFieldCell(
                value = nodeUrl,
                isError = !isUrlValid,
                onValueChange = {
                    nodeUrl = it
                    isUrlValid = it.text.isUrl()
                },
                label = "Node Url",
                supportingText = {
                    if (isUrlValid) {
                        Text("HTTPS link to selected node type")
                    } else {
                        Text("HTTPS link is invalid")
                    }
                },
                keyboardActions = KeyboardActions(
                    onSend = { onNext() },
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                ),
                singleLine = false,
                maxLines = 3,
                minLines = 1,
            )

            Spacer(Modifier.weight(1f))

            Row(
                Modifier.padding(horizontal = 19.dp),
                horizontalArrangement = Arrangement.spacedBy(19.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlineAvoirButton(
                    modifier = Modifier.weight(1f),
                    withPaddings = false,
                    title = "Default",
                ) {
                    feature.sendAction(AddCustomNodeAction.SetCustomNode(null))
                }

                AvoirButton(
                    modifier = Modifier.weight(1f),
                    withPaddings = false,
                    enabled = nodeUrl.text.isNotBlank(),
                    title = "Save",
                ) {
                    feature.sendAction(AddCustomNodeAction.SetCustomNode(nodeUrl.text))
                }
            }
        }
    }
}
