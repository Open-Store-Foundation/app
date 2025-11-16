package foundation.openstore.signer.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.AppColorScheme
import com.openstore.app.ui.AvoirTheme
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.navigation.AvoirNavHost
import com.openstore.app.ui.setAppTheme
import com.openstore.app.ui.systemAppTheme
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.kitten.android.withStatelessViewModel
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.connections.ConnectionsScreen
import foundation.openstore.signer.app.screens.create.CreateWalletScreen
import foundation.openstore.signer.app.screens.details.WalletDetailsScreen
import foundation.openstore.signer.app.screens.home.HomeScreen
import foundation.openstore.signer.app.screens.import.ImportWalletScreen
import foundation.openstore.signer.app.screens.list.WalletListScreen
import foundation.openstore.signer.app.screens.mnemonic.verify.VerifyMnemonicScreen
import foundation.openstore.signer.app.screens.mnemonic.viewer.MnemonicViewerMode
import foundation.openstore.signer.app.screens.mnemonic.viewer.MnemonicViewerScreen
import foundation.openstore.signer.app.screens.settings.SettingsScreen
import foundation.openstore.signer.app.screens.transactions.TransactionsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

sealed interface Router {
    @Serializable
    data object Home : Router

    @Serializable
    data object CreateWallet : Router

    @Serializable
    data object ImportWallet : Router

    @Serializable
    data class MnemonicCreate(val name: String) : Router

    @Serializable
    data class MnemonicVerify(val pendingId: String) : Router

    @Serializable
    data class MnemonicViewer(val walletId: String) : Router

    @Serializable
    data object WalletList : Router

    @Serializable
    data class WalletDetails(val walletId: String) : Router

    @Serializable
    data class Connections(val walletId: String) : Router

    @Serializable
    data class Transactions(val walletId: String) : Router

    @Serializable
    data object Settings : Router
}

@Composable
fun App(
    navigator: NavHostController,
) {
    val scope = rememberCoroutineScope()
    val feature = SignerInjector.withStatelessViewModel { provideMainViewModel() }

    val systemTheme = systemAppTheme()

    OnCreate {
        scope.launch(Dispatchers.Main.immediate) {
            setAppTheme(feature.appTheme() ?: systemTheme)
        }
    }

    val hasWallets by feature.state.hasWallets.observeSafeState()
    AvoirTheme(colors = AppColorScheme.Firestorm) {
        Surface {
            hasWallets?.let {
                Router(
                    navigator = navigator,
                    startRoute = when (it) {
                        true -> Router.WalletList
                        false -> Router.Home
                    }
                )
            }
        }
    }
}

@Composable
private fun Router(navigator: NavHostController, startRoute: Router) {
    AvoirNavHost(
        navController = navigator,
        startDestination = startRoute,
    ) {
        composable<Router.Home> {
            HomeScreen(
                onCreateNew = {
                    navigator.navigate(Router.CreateWallet)
                },
                onImportExisting = {
                    navigator.navigate(Router.ImportWallet)
                },
            )
        }

        composable<Router.CreateWallet> {
            CreateWalletScreen(
                navigator = navigator,
                onNext = { name ->
                    navigator.navigate(Router.MnemonicCreate(name))
                }
            )
        }

        composable<Router.ImportWallet> {
            ImportWalletScreen(
                navigator = navigator,
                onImported = {
                    navigator.navigate(Router.WalletList) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Router.MnemonicCreate> {
            val data = it.toRoute<Router.MnemonicCreate>()

            MnemonicViewerScreen(
                navigator = navigator,
                mode = MnemonicViewerMode.Create(data.name),
                onVerify = { id -> navigator.navigate(Router.MnemonicVerify(id)) },
            )
        }

        composable<Router.MnemonicVerify> {
            VerifyMnemonicScreen(
                navigator = navigator,
                onSuccess = { isRefresh ->
                    navigator.navigate(Router.WalletList) {
                        popUpTo(0) { inclusive = isRefresh }
                    }
                }
            )
        }

        composable<Router.MnemonicViewer> {
            val data = it.toRoute<Router.MnemonicViewer>()
            MnemonicViewerScreen(
                navigator = navigator,
                mode = MnemonicViewerMode.View(GcipId.from(data.walletId)),
                onVerify = { id -> navigator.navigate(Router.MnemonicVerify(id)) },
            )
        }

        composable<Router.WalletList> {
            WalletListScreen(
                onWalletSelected = {
                    navigator.navigate(Router.WalletDetails(it.id.fmt))
                },
                onCreateWallet = {
                    navigator.navigate(Router.CreateWallet)
                },
                onImportWallet = {
                    navigator.navigate(Router.ImportWallet)
                },
                onSettings = {
                    navigator.navigate(Router.Settings)
                }
            )
        }

        composable<Router.WalletDetails> {
            WalletDetailsScreen(
                navigator = navigator,
                onWalletDeleted = { hasWallets ->
                    when (hasWallets) {
                        true -> navigator.navigate(Router.WalletList) {
                            popUpTo(0) { inclusive = true }
                        }
                        false -> navigator.navigate(Router.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onTransactions = { walletId ->
                    navigator.navigate(Router.Transactions(walletId.fmt))
                },
                onMnemonic = { walletId ->
                    navigator.navigate(Router.MnemonicViewer(walletId.fmt))
                },
                onConnection = { connectionId ->
                    navigator.navigate(Router.Connections(connectionId.fmt))
                }
            )
        }

        composable<Router.Connections> {
            ConnectionsScreen(navigator = navigator)
        }

        composable<Router.Transactions> {
            TransactionsScreen(navigator = navigator)
        }

        composable<Router.Settings> {
            SettingsScreen(
                navigator = navigator,
                onSecurity = {
                    // TODO: Navigate to Security Settings
                }
            )
        }
    }
}