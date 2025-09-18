package com.openstore.app.core.net

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.w3c.workers.ServiceWorkerGlobalScope

external val globalThis: ServiceWorkerGlobalScope

internal class ServiceWorkerNetworkProvider : NetworkProviderLauncher {

    private val state: MutableStateFlow<NetworkStatus> = MutableStateFlow(NetworkStatus.Connecting)
    override val stateFlow: StateFlow<NetworkStatus> = state

    override fun isConnected(): Boolean = globalThis.navigator.onLine
    override fun status(): NetworkStatus {
        return if (globalThis.navigator.onLine) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Connecting
        }
    }

    override fun launch() {
        state.tryEmit(status())

        globalThis.addEventListener(
            "online",
            {
                state.tryEmit(NetworkStatus.Available)
            },
        )

        globalThis.addEventListener(
            "offline",
            {
                state.tryEmit(NetworkStatus.Connecting)
            },
        )
    }
}
