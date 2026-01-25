package foundation.openstore.signer.app.screens.gcip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openstore.app.log.L
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.AppColorScheme
import com.openstore.app.ui.AvoirTheme
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.lifecycle.OnResume
import com.openstore.app.ui.navigation.AvoirDialogNavHost
import com.openstore.app.ui.navigation.AvoirNavHost
import com.openstore.app.ui.setAppTheme
import com.openstore.app.ui.systemAppTheme
import com.openstore.app.ui.workround.rememberAvoirSheetState
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.gcip.platform.GcipDataBundle
import foundation.openstore.kitten.android.withStatelessViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private const val SHEET_HIDE_TARGET = 0.3f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GcipApp(
    isFullScreen: Boolean,
    provideData: () -> GcipDataBundle,
    onError: (ByteArray?) -> Unit,
    onConfirmed: (ByteArray) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val feature = GcipInjector.withStatelessViewModel {
        provideGcipFeature(provideData())
    }

    val dismissAction = {
        feature.sendAction(GcipAction.Disconnect)
    }

    val systemTheme = systemAppTheme()
    OnCreate {
        scope.launch(Dispatchers.Main.immediate) {
            setAppTheme(feature.appTheme() ?: systemTheme)
        }
    }

    val navigator = rememberNavController()
    OnResume {
        feature.events.onEach { event ->
            when (event) {
                is GcipEvent.Error -> {
                    L.e(event.e)
                    onError(event.result)
                }
                is GcipEvent.Response -> onConfirmed(event.result)
                is GcipEvent.Selected -> navigator.navigate(GcipRouter.Confirm)
                is GcipEvent.ChangeWallet -> navigator.navigateUp()
            }
        }.launchIn(scope)
    }

    AvoirTheme(colors = AppColorScheme.Firestorm) {
        when {
            isFullScreen -> {
                AvoirScaffold(
                    topBar = {}
                ) {
                    Column(Modifier.padding(it)) {
                        Spacer(Modifier.height(24.dp))
                        GcipContent(
                            navigator = navigator,
                            feature = feature,
                            isFullScreen = isFullScreen,
                            onDismissRequest = dismissAction
                        )
                    }
                }
            }
            else -> {
                val window = LocalWindowInfo.current
                var stateHolder: SheetState? = null

                val sheetState = rememberAvoirSheetState(
                    skipPartiallyExpanded = true,
                    confirmValueChange = {
                        val state = stateHolder
                        if (it == Hidden && state != null) {
                            val offset = state.requireOffset() / window.containerSize.height
                            offset > SHEET_HIDE_TARGET
                        } else {
                            true
                        }
                    }
                )

                SideEffect {
                    stateHolder = sheetState
                }

                ModalBottomSheet(
                    onDismissRequest = dismissAction,
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    GcipContent(
                        navigator = navigator,
                        feature = feature,
                        isFullScreen = isFullScreen,
                        onDismissRequest = dismissAction
                    )
                }
            }
        }
    }
}

interface GcipRouter {

    @Serializable
    object Select : GcipRouter

    @Serializable
    object Confirm: GcipRouter
}

@Composable
private fun GcipContent(
    feature: GcipViewModel,
    navigator: NavHostController,
    isFullScreen: Boolean,
    onDismissRequest: () -> Unit
) {
    val content: NavGraphBuilder.(SignerRequest) -> Unit = { request ->
        composable<GcipRouter.Select> {
            if (request is SignerRequest.Connect) {
                WalletSelectorScreen(
                    request = request,
                    isFullScreen = isFullScreen,
                    isLoading = feature.state.isLoading,
                    wallets = feature.state.wallets,
                    onWalletSelected = { feature.sendAction(GcipAction.SelectWallet(it)) }
                )
            }
        }

        composable<GcipRouter.Confirm> {
            ConfirmationScreen(
                request = request,
                isFullScreen = isFullScreen,
                wallet = feature.state.selectedWallet,
                connection = feature.state.connection,
                credential = feature.state.credential,
                isLoading = feature.state.isLoading,
                onChangeWallet = { feature.sendAction(GcipAction.ChangeWallet) },
                onCancel = onDismissRequest,
                onConfirm = { feature.sendAction(GcipAction.Confirm(it)) }
            )
        }
    }

    val request by feature.state.request.observeSafeState()
    val req = request
    when {
        req == null -> {
            AvoirLoaderScreen()
        }
        isFullScreen -> {
            AvoirNavHost(
                navController = navigator,
                startDestination = when (request) {
                    is SignerRequest.Connect -> GcipRouter.Select
                    else -> GcipRouter.Confirm
                },
                builder = { content(req) },
            )
        }
        else -> {
            AvoirDialogNavHost(
                navController = navigator,
                startDestination = when (request) {
                    is SignerRequest.Connect -> GcipRouter.Select
                    else -> GcipRouter.Confirm
                },
                builder = { content(req) },
            )
        }
    }
}
