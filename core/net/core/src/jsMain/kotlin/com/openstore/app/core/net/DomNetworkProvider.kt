package com.openstore.app.core.net

import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class DomNetworkProvider : NetworkProviderLauncher {

    private val state: MutableStateFlow<NetworkStatus> = MutableStateFlow(NetworkStatus.Connecting)
    override val stateFlow: StateFlow<NetworkStatus> = state

    override fun isConnected(): Boolean = window.navigator.onLine
    override fun status(): NetworkStatus {
        return if (window.navigator.onLine) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Connecting
        }
    }

    override fun launch() {
        state.tryEmit(status())

        window.addEventListener(
            "online",
            {
                state.tryEmit(NetworkStatus.Available)
            },
        )

        window.addEventListener(
            "offline",
            {
                state.tryEmit(NetworkStatus.Connecting)
            },
        )
    }
}
