package com.openstore.app.core.net

// TODO v3 extend to connections(add Connecting etc)
sealed class NetworkStatus {
    data object Available : NetworkStatus()
    data object Connecting : NetworkStatus()
}
