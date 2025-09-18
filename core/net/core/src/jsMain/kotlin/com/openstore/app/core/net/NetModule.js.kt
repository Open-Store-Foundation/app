package com.openstore.app.core.net

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.JsClient
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import com.openstore.app.core.common.lazyUnsafe

actual class NetModule actual constructor(
    netConfig: NetConfig,
) {
    actual val clientEngineFactory: HttpClientEngineFactory<*> by lazyUnsafe { JsClient() }
    actual val networkProvider: NetworkProviderLauncher by lazyUnsafe { PlatformNetworkProvider() }
    actual val logger: NetLogger = NetLogger(Logger.DEFAULT)

    init {
        networkProvider.launch()
        logger.isEnabled(netConfig.isLogging)
    }
}
