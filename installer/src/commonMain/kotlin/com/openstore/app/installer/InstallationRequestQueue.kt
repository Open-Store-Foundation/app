package com.openstore.app.installer

import kotlinx.coroutines.flow.Flow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

data class InstallationRequest(
    val address: String,
    val name: String,
    val version: Long,
    val packageName: String,
    val artifactUrls: List<String>,
    val size: Long,
    val checksum: String,
)

interface InQueueEvents {
    data class NativeInstall(val request: InstallationRequest) : InQueueEvents
    data class Cancel(val request: InstallationRequest) : InQueueEvents
    data class CleanUp(val request: InstallationRequest) : InQueueEvents
}

@OptIn(ExperimentalContracts::class)
fun InstallationRequest?.isSameAddress(address: String): Boolean {
    contract {
        returns(true) implies (this@isSameAddress != null)
    }

    if (this == null) {
        return false
    }

    return this.address == address
}

sealed interface QueueStage {
    data object Starting : QueueStage
    data object WaitingInstallation : QueueStage
    data class Progress(val info: InstallationRequest, val progress: Int) : QueueStage
}

interface InstallationRequestQueue {
    val inQueueEvents: Flow<InQueueEvents>

    suspend fun isQueueEmpty(): Boolean
    suspend fun nextRequest(): InstallationRequest?

    suspend fun isNativeQueueEmpty(): Boolean
}
