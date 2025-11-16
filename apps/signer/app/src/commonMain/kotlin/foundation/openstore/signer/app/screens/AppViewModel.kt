package foundation.openstore.signer.app.screens

import com.openstore.app.mvi.AsyncViewModel
import com.openstore.app.ui.AppTheme
import foundation.openstore.signer.app.data.settings.SettingTheme
import foundation.openstore.signer.app.data.settings.WalletSettingsRepository
import foundation.openstore.signer.app.data.wallet.WalletInteractor


import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty

sealed interface AppAction : MviAction {
    data object Init : AppAction
}

data class AppState(
    val hasWallets: Boolean? = null
) : MviState

class AppViewState(
    val hasWallets: MviProperty<Boolean?>
) : MviViewState

class AppViewModel(
    private val walletInteractor: WalletInteractor,
    private val settingsRepo: WalletSettingsRepository
) : MviFeature<AppAction, AppState, AppViewState>(
    initState = AppState(),
    initAction = AppAction.Init,
) {

    override fun createViewState(): AppViewState {
        return buildViewState {
            AppViewState(mviProperty { it.hasWallets })
        }
    }

    suspend fun appTheme(): AppTheme? {
        return when (settingsRepo.getTheme()) {
            SettingTheme.Dark -> AppTheme.Dark
            SettingTheme.Light -> AppTheme.Light
            SettingTheme.System -> null
        }
    }

    override suspend fun executeAction(action: AppAction) {
        when (action) {
            AppAction.Init -> {
                val hasWallets = walletInteractor.hasWallets()
                setState { copy(hasWallets = hasWallets) }
            }
        }
    }
}
