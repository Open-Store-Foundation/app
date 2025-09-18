package com.openstore.app.core.net

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class NodeJsNetworkProvider : NetworkProviderLauncher {

    private val state: MutableStateFlow<NetworkStatus> = MutableStateFlow(NetworkStatus.Available)
    override val stateFlow: StateFlow<NetworkStatus> = state

    override fun isConnected(): Boolean = true
    override fun status(): NetworkStatus = NetworkStatus.Available
    override fun launch() {}
}
