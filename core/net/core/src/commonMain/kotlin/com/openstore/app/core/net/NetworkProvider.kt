package com.openstore.app.core.net

import kotlinx.coroutines.flow.StateFlow

interface NetworkProvider {
    val stateFlow: StateFlow<NetworkStatus>
    fun isConnected(): Boolean
    fun status(): NetworkStatus
}

interface NetworkProviderLauncher : NetworkProvider {
    fun launch()
}
