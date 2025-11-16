package com.openstore.app.core.net

import com.openstore.app.OkHttpClientFactory
import io.ktor.client.engine.HttpClientEngineFactory
import com.openstore.app.core.common.lazyUnsafe
import com.openstore.app.core.net.NetConfig
import com.openstore.app.core.net.NetworkProvider
import com.openstore.app.core.net.NetworkProviderLauncher
import com.openstore.app.core.net.PlatformNetworkProvider

actual class NetModule(
    netConfig: NetConfig,
) {
    actual val clientEngineFactory: HttpClientEngineFactory<*> by lazyUnsafe { OkHttpClientFactory }
    actual val networkProvider: NetworkProviderLauncher by lazyUnsafe { PlatformNetworkProvider() }
    actual val logger: NetLogger by lazyUnsafe { NetLogger() }

    init {
        networkProvider.launch()
        logger.isEnabled(netConfig.isLogging)
    }
}
