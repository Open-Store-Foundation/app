package com.openstore.app.data.installation

import com.openstore.app.core.async.Relay
import com.openstore.app.data.Artifact
import com.openstore.app.data.Asset
import com.openstore.app.data.sources.AppChainService
import com.openstore.app.data.store.ObjectRepo
import com.openstore.app.installer.InQueueEvents
import com.openstore.app.installer.InstallationEvent
import com.openstore.app.installer.InstallationEventProducer
import com.openstore.app.installer.InstallationMetaRepo
import com.openstore.app.installer.InstallationRequest
import com.openstore.app.installer.InstallationRequestQueue
import com.openstore.app.installer.InstallationStatus
import com.openstore.app.installer.InstallationStatusRepo
import com.openstore.app.installer.InstalledObjectMeta
import com.openstore.app.installer.MutableInstallationMetaRepo
import com.openstore.app.installer.isSameAddress
import com.openstore.app.log.L
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed interface InstallerAction {
    data object InstallerSetup : InstallerAction
    data object InstallerLaunch : InstallerAction
    data class Delete(val obj: Asset) : InstallerAction
    data class Open(val obj: Asset) : InstallerAction
}

data class FetchingRequest(
    val asset: Asset,
    val artifact: Artifact,
)

data class InstalledAsset(
    val asset: Asset,
    val hasNewVersion: Boolean? = null,
)

enum class InstallationQueueStatus {
    InQueueDownload,
    Downloading,
    InQueueInstall,
    Installation
}

sealed interface DeleteEvent {
    data class ObjectDeleted(
        val packageName: String
    ) : DeleteEvent
}

interface InstallationRequestRepo : InstallationEventProducer, InstallationStatusRepo {
    val platformInstallerActions: Flow<InstallerAction>
    val installationEvents: Flow<InstallationEvent>
    val deleteEvents: Flow<DeleteEvent>

    suspend fun fetchInstallationRequest(request: FetchingRequest)
    suspend fun getInstalledAssets(): List<InstalledAsset>
    suspend fun setupInstaller()
    suspend fun enqueueRequest(info: InstallationRequest): Boolean
    suspend fun dequeueRequest(obj: Asset)

    suspend fun openObject(obj: Asset)
    suspend fun deleteObject(obj: Asset)
}

class InstallationRepoDefault(
    private val objectRepo: ObjectRepo,
    private val appChainService: AppChainService,
    private val installationValidator: InstallationValidator,
    private val installationMetaRepo: MutableInstallationMetaRepo,
) : InstallationRequestRepo, InstallationRequestQueue {

    // States
    private val mutex = Mutex()

    // Fetching
    private val fetchingQueue = LinkedHashMap<String, FetchingRequest>()

    // Download
    private val downloadQueue = LinkedHashMap<String, InstallationRequest>()
    private var downloadActiveRequest: InstallationRequest? = null
    private var activeStatus: InstallationStatus? = null

    private val installationQueue = LinkedHashMap<String, InstallationRequest>()
    private var installationActiveRequest: InstallationRequest? = null

    // Relays
    private val platformInstallerRelay = Relay<InstallerAction>()
    override val platformInstallerActions = platformInstallerRelay.events

    private val installationRelay = Relay<InstallationEvent>()
    override val installationEvents = installationRelay.events

    private val inQueueRelay = Relay<InQueueEvents>()
    override val inQueueEvents = inQueueRelay.events

    private val deleteEventRelay = Relay<DeleteEvent>() // rename
    override val deleteEvents = deleteEventRelay.events

    // TODO v2
    suspend fun getInstallingQueue() {
        mutex.withLock {
            val statuses = buildMap {
                if (downloadQueue.values.isNotEmpty()) {
                    put(InstallationQueueStatus.InQueueDownload, downloadQueue.values)
                }

                downloadActiveRequest?.let {
                    put(InstallationQueueStatus.Downloading, listOf(it))
                }

                if (installationQueue.values.isNotEmpty()) {
                    put(InstallationQueueStatus.InQueueInstall, installationQueue.values)
                }

                installationActiveRequest?.let {
                    put(InstallationQueueStatus.Installation, listOf(it))
                }
            }
        }
    }

    //
    // Common
    //
    override suspend fun fetchInstallationRequest(request: FetchingRequest) {
        val (asset: Asset, artifact: Artifact) = request

        if (!installationMetaRepo.canRequestPackageInstalls()) {
            L.w("Can't request package installs")
            setupInstaller()
            return
        }

        L.d("Fetching installation request for ${artifact.refId}")
        mutex.withLock {
            fetchingQueue.put(request.asset.address, request)
            installationRelay.emit(InstallationEvent.Fetching(request.asset.address))
        }

        val validationResult = runCatching {
            val result = installationValidator.validate(request)

            if (result !is InstallationValidationResult.Data) {
                return@runCatching null
            }

            result
        }.onFailure(L::e).getOrNull()

        if (validationResult == null) {
            L.d("Asset metadata is not valid ${artifact.refId}")
            installationRelay.emit(InstallationEvent.FetchingFailed(request.asset.address)) // TODO
            return
        }

        val sources = runCatching {
            // Checking sources
            appChainService.getArtifactSources(asset.address, artifact)
                .ifEmpty {
                    L.e("No c_sources not found for ${artifact.refId}")
                    appChainService.getArtifactLink(asset, artifact)
                        ?.let { listOf(it) }
                }
        }.onFailure(L::e).getOrNull()

        if (sources == null || sources.isEmpty()) {
            L.d("No d_sources found for ${artifact.refId}")
            installationRelay.emit(InstallationEvent.FetchingFailed(request.asset.address)) // TODO
            return
        }

        L.d("Found several artifact sources: $sources")

        val info = InstallationRequest(
            address = asset.address,
            name = asset.name,
            packageName = asset.packageName,
            version = artifact.versionCode,
            versionName = artifact.versionName,
            size = artifact.size,
            checksum = artifact.checksum,
            artifactUrls = sources,
            contractFingerprints = validationResult.fingerprints
        )

        enqueueRequest(info)
    }

    override suspend fun getInstalledAssets(): List<InstalledAsset> {
        return installationMetaRepo.getInstalledObjectsMetas()
            .mapNotNull { meta ->
                objectRepo.findObject(meta.address)
                    .getOrNull()
            }
            .map { asset -> InstalledAsset(asset) }
    }

    override suspend fun getInstallationStatus(
        address: String,
        packageName: String,
        version: Long
    ): InstallationStatus {
        mutex.withLock {
            if (fetchingQueue.contains(address)) {
                return InstallationStatus.Fetching
            }

            val request = downloadActiveRequest
            if (request.isSameAddress(address)) {
                return activeStatus ?: InstallationStatus.Starting
            }

            val installationRequest = installationQueue.get(packageName)
            if (installationRequest?.address == address || installationActiveRequest?.address == address) {
                return InstallationStatus.Installing
            }

            val downloadRequest = downloadQueue.get(packageName)
            if (downloadRequest?.address == address) {
                return InstallationStatus.InQueue
            }

            if (downloadActiveRequest?.address == address) {
                return InstallationStatus.Downloading
            }
        }

        return installationMetaRepo.getInstallationStatus(address, packageName, version)
    }

    override suspend fun openObject(obj: Asset) {
        platformInstallerRelay.emit(InstallerAction.Open(obj))
    }

    //
    // Pre Install
    //
    override suspend fun setupInstaller() {
        platformInstallerRelay.emit(InstallerAction.InstallerSetup)
    }

    override suspend fun enqueueRequest(info: InstallationRequest): Boolean {
        val old = mutex.withLock {
            fetchingQueue.remove(info.address)
            downloadQueue.put(info.packageName, info)
        }

        if (old == null) {
            installationRelay.emit(InstallationEvent.Enqueued(info))
            platformInstallerRelay.emit(InstallerAction.InstallerLaunch)
        }

        return true
    }

    //
    // Install Start
    //
    override suspend fun isQueueEmpty(): Boolean {
        return mutex.withLock {
            downloadQueue.isEmpty()
        }
    }

    override suspend fun nextRequest(): InstallationRequest? {
        return mutex.withLock {
            val key = downloadQueue.keys.firstOrNull()
                ?: return null

            val item = downloadQueue.remove(key)

            if (item != null) {
                downloadActiveRequest = item
                activeStatus = InstallationStatus.Starting
            }

            item
        }
    }

    override suspend fun isNativeQueueEmpty(): Boolean {
        return mutex.withLock {
            installationQueue.isEmpty() && installationActiveRequest == null
        }
    }

    fun addNativeRequest(request: InstallationRequest) {
        installationQueue[request.packageName] = request

        if (installationActiveRequest == null) {
            installationActiveRequest = request
            L.d("Distribute NATIVE installation: ${request.name}")
            inQueueRelay.emit(InQueueEvents.NativeInstall(request))
        } else {
            installationQueue[request.packageName] = request
        }
    }

    fun proceedNextNativeRequest(request: InstallationRequest) {
        installationQueue.remove(request.packageName)
        installationActiveRequest = null

        val key = installationQueue.keys.firstOrNull()
            ?: return

        val item = installationQueue.remove(key)

        if (item != null) {
            installationActiveRequest = item
            L.d("Distribute NATIVE installation: ${request.name}")
            inQueueRelay.emit(InQueueEvents.NativeInstall(item))
        }
    }

    //
    // Download / Local Install
    //
    override suspend fun produceInstallationEvent(event: InstallationEvent) {
        mutex.withLock {
            if (downloadActiveRequest.isSameAddress(event.address)) {
                activeStatus = when (event) {
                    is InstallationEvent.DownloadProgress -> InstallationStatus.Downloading
                    is InstallationEvent.Installation -> InstallationStatus.Installing
                    is InstallationEvent.DownloadCanceled -> null
                    is InstallationEvent.InstallationFailed -> null
                    is InstallationEvent.DownloadFailed -> null
                    is InstallationEvent.InstallationFinished -> null
                    else -> activeStatus
                }

                downloadActiveRequest = when (event) {
                    is InstallationEvent.DownloadCanceled -> null
                    is InstallationEvent.InstallationFailed -> null
                    is InstallationEvent.DownloadFailed -> null
                    is InstallationEvent.InstallationFinished -> null
                    else -> downloadActiveRequest
                }
            }

            if (event is InstallationEvent.InstallationFinished) {
                installationMetaRepo.addInstalledAddress(event.request.packageName, event.request.address)
            }

            when (event) {
                is InstallationEvent.Installation -> addNativeRequest(event.request)
                is InstallationEvent.InstallationFinished -> proceedNextNativeRequest(event.request)
                is InstallationEvent.InstallationFailed -> proceedNextNativeRequest(event.request)
                else -> null
            }

            val terminatedRequest = when (event) {
                is InstallationEvent.InstallationFinished -> event.request
                is InstallationEvent.DownloadFailed -> event.request
                is InstallationEvent.InstallationFailed -> event.request
                is InstallationEvent.DownloadCanceled -> event.request
                else -> null
            }

            if (terminatedRequest != null) {
                inQueueRelay.emit(InQueueEvents.CleanUp(terminatedRequest))
            }
        }

        L.d("Distribute installation event: ${event::class.simpleName}")
        installationRelay.emit(event)
    }

    //
    // Cancel Install
    //
    override suspend fun dequeueRequest(obj: Asset) {
        mutex.withLock {
            val request = downloadActiveRequest
            if (request.isSameAddress(obj.address)) {
                inQueueRelay.emit(InQueueEvents.Cancel(request))
            }

            downloadQueue.remove(obj.packageName)
        }
    }

    //
    // Delete
    //
    override suspend fun deleteObject(obj: Asset) {
        platformInstallerRelay.emit(InstallerAction.Delete(obj))
    }

    override suspend fun produceUninstallEvent(packageName: String) {
        installationMetaRepo.deleteInstalledAddress(packageName)
        deleteEventRelay.emit(DeleteEvent.ObjectDeleted(packageName))
    }
}
