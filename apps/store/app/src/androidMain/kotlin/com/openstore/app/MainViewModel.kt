package com.openstore.app

import com.openstore.app.MainEvents.OpenObject
import com.openstore.app.MainEvents.RequestInstallPermission
import com.openstore.app.MainEvents.StartInstallerService
import com.openstore.app.data.Asset
import com.openstore.app.data.installation.InstallationRequestRepo
import com.openstore.app.data.installation.InstallerAction
import com.openstore.app.data.settings.SettingTheme
import com.openstore.app.data.settings.SettingsRepo
import com.openstore.app.mvi.AsyncViewModel
import com.openstore.app.mvi.MviRelay
import com.openstore.app.ui.AppTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

sealed interface MainEvents {
    data object RequestInstallPermission : MainEvents
    data object StartInstallerService : MainEvents

    data class OpenObject(val obj: Asset) : MainEvents
    data class DeleteObject(val obj: Asset) : MainEvents
}

sealed interface MainAction {
    object Launch : MainAction
}

class MainViewModel(
    private val settingsRepo: SettingsRepo,
    installationRepo: InstallationRequestRepo,
) : AsyncViewModel() {

    private val relay = MviRelay<MainEvents>()
    val events = relay.events

    init {
        installationRepo.platformInstallerActions.onEach { event ->
            val event = when (event) {
                is InstallerAction.Delete -> {
                    MainEvents.DeleteObject(event.obj)
                }
                is InstallerAction.InstallerSetup -> {
                    RequestInstallPermission
                }
                is InstallerAction.InstallerLaunch -> {
                    StartInstallerService
                }
                is InstallerAction.Open -> {
                    OpenObject(event.obj)
                }
            }

            relay.emit(event)
        }.launchIn(stateScope)
    }

    fun sendAction(action: MainAction) {
        stateScope.launch {
            when (action) {
                MainAction.Launch -> {
                    // use later
                }
            }
        }
    }

    fun appTheme(): AppTheme? {
        return when (runBlocking { settingsRepo.getTheme() }) {
            SettingTheme.Light -> AppTheme.Light
            SettingTheme.Dark -> AppTheme.Dark
            else -> null
        }
    }
}
