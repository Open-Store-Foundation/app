package com.openstore.app.screens.report


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.Router
import com.openstore.app.core.common.isEmail
import com.openstore.app.data.NewReport
import com.openstore.app.data.report.ReportCategoryId
import com.openstore.app.data.report.ReportService
import com.openstore.app.data.report.ReportSubcategoryId
import com.openstore.app.data.report.toRString
import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.cells.AvoirTextFieldCell
import com.openstore.app.ui.cells.TextCell
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.lifecycle.OnPause
import com.openstore.app.ui.lifecycle.OnResume
import com.openstore.app.ui.workround.rememberTextFieldValue
import foundation.openstore.kitten.android.withViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.stringResource

sealed interface ReportSubmitEvent {
    object Submit : ReportSubmitEvent
    object Error : ReportSubmitEvent
}

sealed interface ReportSubmitAction : MviAction {
    class Submit(val email: String, val description: String?) : ReportSubmitAction
}

data class ReportSubmitState(
    val isLoading: Boolean,
) : MviState

class ReportSubmitViewState(
    val isLoading: MviProperty<Boolean>,
) : MviViewState

class ReportSubmitFeature(
    val objAddress: String,
    val categoryId: ReportCategoryId,
    val subcategoryId: ReportSubcategoryId? = null,
    private val reportService: ReportService,
) : MviFeature<ReportSubmitAction, ReportSubmitState, ReportSubmitViewState>(
    initState = ReportSubmitState(isLoading = false)
) {

    private val relay = MviRelay<ReportSubmitEvent>()
    val events = relay.events

    override fun createViewState(): ReportSubmitViewState {
        return buildViewState {
            ReportSubmitViewState(
                mviProperty { it.isLoading }
            )
        }
    }

    override suspend fun executeAction(action: ReportSubmitAction) {
        when (action) {
            is ReportSubmitAction.Submit -> {
                try {
                    setState { copy(isLoading = true) }
                    reportService.createReport(
                        NewReport(
                            assetAddress = objAddress,
                            categoryId = categoryId.id,
                            subcategoryId = subcategoryId?.id,
                            email = action.email,
                            description = action.description
                        )
                    )

                    relay.emit(ReportSubmitEvent.Submit)
                } catch (e: Throwable) {
                    L.e(e)
                    setState { copy(isLoading = false) }
                    relay.emit(ReportSubmitEvent.Error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSubmitScreen(
    navigator: NavHostController,
    onSubmitted: () -> Unit
) {
    val feature = StoreInjector.withViewModel {
        val data = it.state.toRoute<Router.ReportSubmit>()
        provideReportSummaryFeature(data)
    }

    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var email by rememberTextFieldValue()
    var description by rememberTextFieldValue()
    var isEmailValid by remember { mutableStateOf(true) }

    OnResume {
        focusRequester.requestFocus()
    }

    OnPause {
        focusRequester.freeFocus()
    }

    OnCreate {
        feature.events.onEach { event ->
            when (event) {
                ReportSubmitEvent.Submit -> onSubmitted()
                ReportSubmitEvent.Error -> snackbarHost.showSnackbar("Error submitting report. Please try again later.")
            }
        }.launchIn(scope)
    }

    val onNext = {
        if (!email.text.isEmail()) {
            isEmailValid = false
        } else {
            keyboardController?.hide()
            feature.sendAction(
                ReportSubmitAction.Submit(
                    email.text, description.text.ifBlank { null }
                )
            )
        }
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                onNavigateUp = { navigator.navigateUp() },
                title = "Send report"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) {
        val isLoading by feature.state.isLoading.observeSafeState()

        when {
            isLoading -> AvoirLoaderScreen()
            else -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(vertical = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TextCell(
                        title = "Category",
                        maxLinesTitle = 2,

                        subtitle = stringResource(feature.categoryId.toRString()),
                        maxLinesSubtitle = 2,
                    )

                    feature.subcategoryId?.let {
                        TextCell(
                            title = "Subcategory",
                            maxLinesTitle = 2,

                            subtitle = stringResource(it.toRString()),
                            maxLinesSubtitle = 2,
                        )
                    }

                    AvoirTextFieldCell(
                        modifier = Modifier.focusRequester(focusRequester),
                        value = email,
                        label = "Email (required)",
                        supportingText = {
                            if (isEmailValid) {
                                Text(text = "We'll use this to contact you about your report")
                            } else {
                                Text(text = "Please enter a valid email")
                            }
                        },
                        onValueChange = { text ->
                            email = text
                            isEmailValid = true
                        },
                        isError = !isEmailValid,
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                        ),
                    )

                    AvoirTextFieldCell(
                        value = description,
                        label = "Description",
                        supportingText = { Text(text = "Please describe the issue in detail. Include steps to reproduce if applicable.") },
                        minLines = 3,
                        maxLines = 3,
                        singleLine = false,
                        onValueChange = { text -> description = text },
                        keyboardActions = KeyboardActions(
                            onSend = { onNext() },
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                        ),
                    )

                    Spacer(Modifier.weight(1f))

                    AvoirButton(
                        title = "Send",
                        enabled = email.text.isNotBlank(),
                        onClick = onNext,
                    )
                }
            }
        }
    }
}