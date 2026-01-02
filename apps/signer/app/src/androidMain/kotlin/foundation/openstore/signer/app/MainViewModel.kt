package foundation.openstore.signer.app

import com.openstore.app.mvi.AsyncViewModel
import com.openstore.app.ui.AppTheme
import foundation.openstore.signer.app.data.settings.SettingTheme
import foundation.openstore.signer.app.data.settings.WalletSettingsRepository
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import kotlinx.coroutines.runBlocking

class MainViewModel(
    private val settingsRepo: WalletSettingsRepository
) : AsyncViewModel() {

    fun appTheme(): AppTheme? {
        return when (runBlocking { settingsRepo.getTheme() }) {
            SettingTheme.Dark -> AppTheme.Dark
            SettingTheme.Light -> AppTheme.Light
            SettingTheme.System -> null
        }
    }
}
