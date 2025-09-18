package com.openstore.app.installer

sealed class InstallationEvent(
    val address: String
) {
    class Fetching(
        address: String
    ) : InstallationEvent(address)

    class FetchingFailed(
        address: String
    ) : InstallationEvent(address)

    data class Enqueued(
        val request: InstallationRequest,
    ) : InstallationEvent(request.address)

    data class DownloadProgress(
        val request: InstallationRequest,
        val progress: Int
    ) : InstallationEvent(request.address)

    data class DownloadFailed(
        val request: InstallationRequest,
        val e: Throwable
    ) : InstallationEvent(request.address)

    data class DownloadCanceled(
        val request: InstallationRequest
    ) : InstallationEvent(request.address)

    data class Installation(
        val request: InstallationRequest,
    ) : InstallationEvent(request.address)

    data class InstallationFailed(
        val request: InstallationRequest
    ) : InstallationEvent(request.address)

    data class InstallationFinished(
        val request: InstallationRequest,
    ) : InstallationEvent(request.address)
}

interface InstallationEventProducer {
    suspend fun produceUninstallEvent(packageName: String)
    suspend fun produceInstallationEvent(event: InstallationEvent)
}
