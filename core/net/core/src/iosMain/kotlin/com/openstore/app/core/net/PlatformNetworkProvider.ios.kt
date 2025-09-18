package com.openstore.app.core.net

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class PlatformNetworkProvider : NetworkProviderLauncher {
    override val stateFlow: StateFlow<NetworkStatus> = MutableStateFlow(NetworkStatus.Available)
    override fun isConnected(): Boolean = true
    override fun status(): NetworkStatus = NetworkStatus.Available
    override fun launch() {}
}
