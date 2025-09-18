package com.openstore.app.core.net

import io.ktor.client.engine.HttpClientEngineFactory

expect class NetModule(
    netConfig: NetConfig,
) {
    val clientEngineFactory: HttpClientEngineFactory<*>
    val networkProvider: NetworkProviderLauncher
    val logger: NetLogger
}
