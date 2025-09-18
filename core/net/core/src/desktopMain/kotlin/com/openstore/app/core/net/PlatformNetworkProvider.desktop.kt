package com.openstore.app.core.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.openstore.app.core.net.NetworkProviderLauncher
import com.openstore.app.core.net.NetworkStatus
import java.net.InetAddress

actual class PlatformNetworkProvider : NetworkProviderLauncher {

    private val coroutineJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + coroutineJob)

    private val isNetworkConnected = MutableStateFlow(initialNetworkStatus())
    override val stateFlow: StateFlow<NetworkStatus> get() = isNetworkConnected

    override fun launch() {
        coroutineScope.launch {
            while (isActive) {
                val currentStatus = status()
                if (currentStatus != isNetworkConnected.value) {
                    isNetworkConnected.emit(currentStatus)
                }
                delay(CONNECTIVITY_CHECK_INTERVAL_MILLISECONDS)
            }
        }
    }

    override fun isConnected(): Boolean {
        return try {
            InetAddress.getByName("8.8.8.8").isReachable(5000)
        } catch (e: Exception) {
            false
        }
    }

    override fun status(): NetworkStatus {
        return if (isConnected()) NetworkStatus.Available else NetworkStatus.Connecting
    }

    private fun initialNetworkStatus(): NetworkStatus {
        return if (isConnected()) NetworkStatus.Available else NetworkStatus.Connecting
    }

    companion object {
        const val CONNECTIVITY_CHECK_INTERVAL_MILLISECONDS: Long = 30_000
    }
}
