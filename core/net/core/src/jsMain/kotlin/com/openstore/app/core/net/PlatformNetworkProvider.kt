package com.openstore.app.core.net

import kotlinx.coroutines.flow.StateFlow
import com.openstore.app.core.os.Js

actual class PlatformNetworkProvider : NetworkProviderLauncher {

    private val provider: NetworkProviderLauncher by lazy {
        when {
            Js.isDom -> DomNetworkProvider()
            Js.isNodeJs -> NodeJsNetworkProvider()
            else -> ServiceWorkerNetworkProvider()
        }
    }

    override val stateFlow: StateFlow<NetworkStatus> = provider.stateFlow

    override fun isConnected(): Boolean = provider.isConnected()
    override fun status(): NetworkStatus = provider.status()
    override fun launch() = provider.launch()
}