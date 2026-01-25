package com.openstore.app.data.settings

import com.openstore.app.store.common.store.KeyValueStorage

enum class SettingTheme {
    System, Dark, Light
}

class SettingsRepo(
    val store: KeyValueStorage,
) {

    companion object {
        const val KEY_THEME = "isDarkTheme"
        const val KEY_UPDATE = "isNotifyUpdate"
    }

    suspend fun getTheme(): SettingTheme {
        val isDarkTheme = store.getBooleanOrNull(KEY_THEME)
        return when {
            isDarkTheme == null -> SettingTheme.System
            isDarkTheme -> SettingTheme.Dark
            else -> SettingTheme.Light
        }
    }

    suspend fun setTheme(theme: SettingTheme) {
        when (theme) {
            SettingTheme.System -> store.remove(KEY_THEME)
            SettingTheme.Dark -> store.putBoolean(KEY_THEME, true)
            SettingTheme.Light -> store.putBoolean(KEY_THEME, false)
        }
    }

    suspend fun isNotifyUpdate(appAddress: String): Boolean {
        val isNotifyUpdate = store.getBooleanOrNull(getNotifyKey(appAddress))
        return isNotifyUpdate
            ?: true
    }

    suspend fun setNotifyUpdate(appAddress: String, isNotify: Boolean) {
        store.putBoolean(getNotifyKey(appAddress), isNotify)
    }

    private fun getNotifyKey(appAddress: String): String {
        return "$KEY_UPDATE:${appAddress}"
    }
}
