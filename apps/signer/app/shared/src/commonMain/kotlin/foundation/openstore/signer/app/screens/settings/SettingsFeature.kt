package foundation.openstore.signer.app.screens.settings

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.signer.app.data.settings.SettingTheme
import foundation.openstore.signer.app.data.settings.WalletSettingsRepository
import kotlinx.coroutines.launch

sealed interface SettingsAction : MviAction {
    data class SetTheme(val theme: SettingTheme) : SettingsAction
}

data class SettingsState(
    val theme: SettingTheme
) : MviState

class SettingsViewState(
    val theme: MviProperty<SettingTheme>
) : MviViewState

class SettingsFeature(
    private val settingsRepo: WalletSettingsRepository
) : MviFeature<SettingsAction, SettingsState, SettingsViewState>(
    initState = SettingsState(
        theme = SettingTheme.System
    )
) {

    init {
        stateScope.launch {
            val theme = settingsRepo.getTheme()
            setState { copy(theme = theme) }
        }
    }

    override fun createViewState(): SettingsViewState {
        return buildViewState {
            SettingsViewState(mviProperty { it.theme })
        }
    }

    override suspend fun executeAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SetTheme -> {
                settingsRepo.setTheme(action.theme)
                setState { copy(theme = action.theme) }
            }
        }
    }
}
