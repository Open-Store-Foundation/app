package com.openstore.app.screens.details

import com.openstore.app.DataState
import com.openstore.app.data.Artifact
import com.openstore.app.data.Asset
import com.openstore.app.data.ObjectId
import com.openstore.app.data.TrackId
import com.openstore.app.data.artifact.ArtifactService
import com.openstore.app.data.installation.DeleteEvent
import com.openstore.app.data.installation.FetchingRequest
import com.openstore.app.data.installation.InstallationRequestRepo
import com.openstore.app.data.settings.SettingsRepo
import com.openstore.app.data.sources.AppChainService
import com.openstore.app.data.store.ObjectRepo
import com.openstore.app.installer.InstallationEvent
import com.openstore.app.installer.InstallationRequest
import com.openstore.app.installer.InstallationStatus
import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface ObjDetailsAction : MviAction {
    object Refresh : ObjDetailsAction

    data class Delete(
        val obj: Asset
    ) : ObjDetailsAction

    data class Open(
        val obj: Asset
    ) : ObjDetailsAction

    data class Install(
        val obj: Asset,
        val artifact: Artifact,
    ) : ObjDetailsAction

    data class Cancel(
        val obj: Asset,
    ) : ObjDetailsAction

    data class SetNotification(
        val address: String,
        val isNotify: Boolean
    ) : ObjDetailsAction
}

sealed interface ObjDetailsEvents {
    object SourcesNotFound : ObjDetailsEvents
}

data class ObjDetailsState(
    val obj: Asset? = null,
    val progress: Int = 0,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isNotifyUpdate: Boolean? = null,
    val status: InstallationStatus? = null,
    val artifact: DataState<Artifact> = DataState.loading(),
) : MviState

class ObjDetailsViewState(
    val obj: MviProperty<Asset?>,
    val artifact: MviProperty<DataState<Artifact>>,
    val status: MviProperty<InstallationStatus?>,
    val progress: MviProperty<Int>,
    val isLoading: MviProperty<Boolean>,
    val isError: MviProperty<Boolean>,
    val isNotifyUpdate: MviProperty<Boolean?>,
) : MviViewState

class ObjDetailsFeature(
    val data: ObjectId,
    private val objRepo: ObjectRepo,
    private val settingsRepo: SettingsRepo,
    private val artifactService: ArtifactService,
    private val appChainService: AppChainService,
    private val requestRepo: InstallationRequestRepo,
) : MviFeature<ObjDetailsAction, ObjDetailsState, ObjDetailsViewState>(
    initState = ObjDetailsState(),
    initAction = ObjDetailsAction.Refresh,
) {

    private val relay = MviRelay<ObjDetailsEvents>()
    val events = relay.events


    init {
        requestRepo.deleteEvents.onEach { event ->
            L.d("Handle delete event: ${event::class.simpleName}")

            stateScope.launch {
                val state = obtainState()
                if (state.obj == null) {
                    L.w("Where is no any obj in state, skip event.")
                    return@launch
                }

                when (event) {
                    is DeleteEvent.ObjectDeleted -> {
                        reloadStatus(state.obj)
                    }
                }
            }
        }.launchIn(bgScope)

        requestRepo.installationEvents.onEach { event ->
            L.d("Handle installation event: ${event::class.simpleName}")

            stateScope.launch {
                val state = obtainState()

                if (state.obj?.address != event.address) {
                    return@launch
                }

                when (event) {
                    is InstallationEvent.Fetching -> setState {
                        copy(status = InstallationStatus.Fetching)
                    }
                    is InstallationEvent.FetchingFailed -> {
                        relay.emit(ObjDetailsEvents.SourcesNotFound)
                        reloadStatus(state.obj)
                    }
                    is InstallationEvent.Enqueued -> setState {
                        copy(progress = 0, status = InstallationStatus.InQueue)
                    }
                    is InstallationEvent.DownloadProgress -> setState {
                        copy(progress = event.progress, status = InstallationStatus.Downloading)
                    }
                    is InstallationEvent.Installation -> setState {
                        copy(progress = 100, status = InstallationStatus.Installing)
                    }
                    is InstallationEvent.DownloadCanceled,
                    is InstallationEvent.DownloadFailed,
                    is InstallationEvent.InstallationFailed,
                    is InstallationEvent.InstallationFinished -> reloadStatus(state.obj)
                }
            }
        }.launchIn(bgScope)
    }

    override fun createViewState(): ObjDetailsViewState {
        return buildViewState {
            ObjDetailsViewState(
                mviProperty { it.obj },
                mviProperty { it.artifact },
                mviProperty { it.status },
                mviProperty { it.progress },
                mviProperty { it.isLoading },
                mviProperty { it.isError },
                mviProperty { it.isNotifyUpdate },
            )
        }
    }

    override suspend fun executeAction(action: ObjDetailsAction) {
        when (action) {
            is ObjDetailsAction.Refresh -> {
                reloadData()
            }
            is ObjDetailsAction.Install -> {
                handleInstallationEvent(action)
            }
            is ObjDetailsAction.Cancel -> {
                requestRepo.dequeueRequest(action.obj)
            }
            is ObjDetailsAction.Open -> {
                requestRepo.openObject(action.obj)
            }
            is ObjDetailsAction.Delete -> {
                requestRepo.deleteObject(action.obj)
            }
            is ObjDetailsAction.SetNotification -> {
                settingsRepo.setNotifyUpdate(action.address, action.isNotify)
                setState { copy(isNotifyUpdate = action.isNotify) }
            }
        }
    }

    private suspend fun handleInstallationEvent(action: ObjDetailsAction.Install) {
        val req = FetchingRequest(action.obj, action.artifact)
        requestRepo.fetchInstallationRequest(req)
    }

    private fun reloadData() {
        stateScope.launch {
            setState { copy(isLoading = true) }

             when (data) {
                is ObjectId.Id -> {
                    val obj = objRepo.findObject(data.id).getOrNull()
                    val isNotifyUpdate = obj?.let { settingsRepo.isNotifyUpdate(it.address) }
                    setState { copy(obj = obj, isNotifyUpdate = isNotifyUpdate, isLoading = false, isError = obj == null) }

                    if (obj == null) {
                        return@launch
                    }

                    reloadStatus(obj)
                }
                is ObjectId.Address -> {
                    val isNotifyUpdate = settingsRepo.isNotifyUpdate(data.address)
                    val data = appChainService.collectObjectData(data.address).getOrNull()
                    setState { copy(obj = data?.obj, isLoading = false, isNotifyUpdate = isNotifyUpdate, isError = data == null) }

                    if (data == null) {
                        return@launch
                    }

                    reloadStatus(data.obj, data.artifact)
                }
            }
        }
    }

    private fun reloadStatus(obj: Asset, artifact: Artifact? = null) {
        stateScope.launch {
            setState { copy(artifact = DataState.loading()) }

            val state = obtainState()
            val stateArtifact = state.artifact.data
            val artifact = if (stateArtifact == null) {
                val artifact = when (data) {
                    is ObjectId.Id -> artifactService.getArtifact(
                        objectId = data.id,
                        trackId = TrackId.RELEASE.id,
                    ).getOrNull()

                    is ObjectId.Address -> artifact
                }

                if (artifact == null) {
                    setState { copy(artifact = DataState.error(), isLoading = false) }
                    return@launch
                }

                artifact
            } else {
                stateArtifact
            }

            val status = runCatching {
                requestRepo.getInstallationStatus(
                    address = obj.address,
                    packageName = obj.packageName,
                    version = artifact.versionCode
                )
            }.getOrNull()

            if (status == null) {
                setState { copy(artifact = DataState.error(), isLoading = false) }
                return@launch
            }

            setState { copy(artifact = DataState.data(artifact), status = status, isLoading = false) }
        }
    }
}
