package foundation.openstore.signer.app.data.settings

import com.openstore.app.store.common.store.PlatformKeyValueFactory

interface WalletSettingsRepository {
    suspend fun getTheme(): SettingTheme
    suspend fun setTheme(theme: SettingTheme)
}

class WalletSettingsRepositoryImpl(
    private val keyValueFactory: PlatformKeyValueFactory
) : WalletSettingsRepository {

    private val store by lazy { keyValueFactory.create("wallet_settings") }

    companion object {
        const val KEY_THEME = "isDarkTheme"
    }

    override suspend fun getTheme(): SettingTheme {
        val isDarkTheme = store.getBooleanOrNull(KEY_THEME)
        return when {
            isDarkTheme == null -> SettingTheme.System
            isDarkTheme -> SettingTheme.Dark
            else -> SettingTheme.Light
        }
    }

    override suspend fun setTheme(theme: SettingTheme) {
        when (theme) {
            SettingTheme.System -> store.remove(KEY_THEME)
            SettingTheme.Dark -> store.putBoolean(KEY_THEME, true)
            SettingTheme.Light -> store.putBoolean(KEY_THEME, false)
        }
    }
}



