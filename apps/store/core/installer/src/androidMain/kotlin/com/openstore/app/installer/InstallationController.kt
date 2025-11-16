package com.openstore.app.installer

import com.openstore.app.core.async.AsyncController
import com.openstore.app.core.async.Relay
import com.openstore.app.installer.ServiceEvents.DownloadProgress
import com.openstore.app.installer.ServiceEvents.InstallationError
import com.openstore.app.installer.ServiceEvents.InstallationFinished
import com.openstore.app.installer.handlers.ApkInstallationManager
import com.openstore.app.installer.handlers.FileDownloadManager
import com.openstore.app.log.L
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference

sealed interface ServiceEvents {
    data object DownloadPrepare : ServiceEvents

    data class DownloadProgress(
        val progress: QueueStage.Progress
    ) : ServiceEvents

    object WaitingInstallation : ServiceEvents

    data class InstallationError(
        val info: InstallationRequest
    ) : ServiceEvents

    data class InstallationFinished(
        val request: InstallationRequest
    ) : ServiceEvents

    object Finished : ServiceEvents
}

class ServiceController(
    httpClient: HttpClient,

    private val queue: InstallationRequestQueue,
    private val installationManager: ApkInstallationManager,
    private val eventProvider: InstallationEventProducer,
) : AsyncController() {

    companion object {
        private const val IDLE_TIMEOUT = 5_000L
    }

    private val activeRequest = AtomicReference<InstallationRequest?>(null)
    private val isStarted = AtomicBoolean(false)
    private val relay = Relay<ServiceEvents>()
    val events = relay.events

    private val downloadManager = FileDownloadManager(
        observeScope = bgScope,
        client = httpClient,
        delegate = object : FileDownloadManager.Delegate {
            override suspend fun onStageUpdated(stage: QueueStage.Progress) {
                executionScope.launch {
                    val active = activeRequest.load()
                    if (active == null || stage.info != active) {
                        return@launch
                    }

                    eventProvider.produceInstallationEvent(
                        InstallationEvent.DownloadProgress(stage.info, stage.progress)
                    )

                    relay.emit(DownloadProgress(stage))
                }
            }

            override fun isCancelled(): Boolean {
                return activeRequest.load() == null
            }
        }
    )

    init {
        L.d("Init NATIVE service controller")
        queue.inQueueEvents.onEach { event ->
            executionScope.launch {
                when (event) {
                    is InQueueEvents.Cancel -> {
                        val request = activeRequest.load()
                        if (request.isSameAddress(event.request.address)) {
                            activeRequest.store(null)
                        }
                    }
                    is InQueueEvents.CleanUp -> {
                        installationManager.cleanUp(event.request)
                    }
                    is InQueueEvents.NativeInstall -> {
                        L.d("Handle NATIVE request: ${event.request.name}")
                        installationManager.install(event.request).getOrElse { error ->
                            eventProvider.produceInstallationEvent(InstallationEvent.InstallationFailed(event.request))
                            relay.emit(InstallationError(event.request)) // TODO specify reason
                        }
                    }
                }
            }
        }.launchIn(bgScope)
    }

    fun isEmpty(): Boolean {
        return runBlocking { queue.isQueueEmpty() }
    }

    fun isStarted(): Boolean {
        return isStarted.load()
    }

    fun runQueueLoop(): Boolean {
        if (!isStarted.compareAndSet(false, true)) {
            return false
        }

        executionScope.launch {
            relay.emit(ServiceEvents.DownloadPrepare)

            while (true) {
                var request = queue.nextRequest()
                if (request == null) {
                    delay(IDLE_TIMEOUT)

                    request = queue.nextRequest()
                    if (request == null) {
                        if (!queue.isNativeQueueEmpty()) {
                            relay.emit(ServiceEvents.WaitingInstallation)
                            continue
                        }

                        isStarted.compareAndSet(true, false)
                        relay.emit(ServiceEvents.Finished)
                        break
                    }
                }

                try {
                    activeRequest.store(request)
                    executeRequest(request)
                } finally {
                    activeRequest.store(null)
                }
            }
        }

        return true
    }

    private suspend fun executeRequest(request: InstallationRequest) {
        downloadManager.downloadFile(request).getOrElse { error ->
            eventProvider.produceInstallationEvent(InstallationEvent.DownloadFailed(request, error))
            relay.emit(InstallationError(request))
            return
        }

        if (activeRequest.load() == null) {
            eventProvider.produceInstallationEvent(InstallationEvent.DownloadCanceled(request))
            return
        }

        eventProvider.produceInstallationEvent(InstallationEvent.Installation(request))
    }

    fun onInstallSuccess(request: InstallationRequest) {
        executionScope.launch {
            relay.emit(InstallationFinished(request))
            eventProvider.produceInstallationEvent(
                InstallationEvent.InstallationFinished(request)
            )
        }
    }

    fun onInstallError(request: InstallationRequest) {
        executionScope.launch {
            relay.emit(InstallationError(request))
            eventProvider.produceInstallationEvent(
                InstallationEvent.InstallationFailed(request)
            )
        }
    }

    fun onDeleteSuccessful(packageName: String) {
        executionScope.launch {
            eventProvider.produceUninstallEvent(packageName)
        }
    }
}
