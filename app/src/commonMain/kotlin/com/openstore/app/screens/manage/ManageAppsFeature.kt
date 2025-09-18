package com.openstore.app.screens.manage


import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.data.installation.InstallationRequestRepo
import com.openstore.app.data.installation.InstalledAsset
import com.openstore.app.data.update.AppUpdateInteractor
import com.openstore.app.log.L
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

sealed interface ManageAppsAction : MviAction {
    object Init : ManageAppsAction
    object Refresh : ManageAppsAction
}

data class ManageAppsState(
    val isLoading: Boolean,
    val isRefresh: Boolean,
    val assets: List<InstalledAsset>
) : MviState

class ManageAppsViewState(
    val isLoading: MviProperty<Boolean>,
    val isRefresh: MviProperty<Boolean>,
    val assets: MviProperty<List<InstalledAsset>>
) : MviViewState

class ManageAppsFeature(
    private val installationRepo: InstallationRequestRepo,
    private val appUpdate: AppUpdateInteractor,
) : MviFeature<ManageAppsAction, ManageAppsState, ManageAppsViewState>(
    initState = ManageAppsState(true, false, emptyList()),
    initAction = ManageAppsAction.Init
) {

    override fun createViewState(): ManageAppsViewState {
        return buildViewState {
            ManageAppsViewState(
                mviProperty { it.isLoading },
                mviProperty { it.isRefresh },
                mviProperty { it.assets }
            )
        }
    }

    override suspend fun executeAction(action: ManageAppsAction) {
        when (action) {
            is ManageAppsAction.Init,
            is ManageAppsAction.Refresh -> {
                setState {
                    copy(
                        isLoading = action is ManageAppsAction.Init,
                        isRefresh = action is ManageAppsAction.Refresh
                    )
                }

                try {
                    val installed = installationRepo.getInstalledAssets()

                    setState {
                        copy(
                            assets = installed,
                            isLoading = false,
                            isRefresh = false,
                        )
                    }

                    // TODO to bg
                    val statuses = installed.map { install ->
                        val hasUpdate = runCatching {
                            appUpdate.hasNewVersion(
                                appAddress = install.asset.address,
                                packageName = install.asset.packageName
                            )
                        }

                        install.copy(
                            hasNewVersion = true // hasUpdate.getOrDefault(false)
                        )
                    }

                    setState {
                        copy(assets = statuses)
                    }
                } catch (e: Throwable) {
                    L.e("Failed to get installed objects", e)

                    setState {
                        copy(
                            assets = emptyList(),
                            isLoading = false,
                            isRefresh = false,
                        )
                    }
                }
            }
        }
    }
}