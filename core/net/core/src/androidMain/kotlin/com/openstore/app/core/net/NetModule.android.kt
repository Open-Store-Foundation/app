package com.openstore.app.core.net

import com.openstore.app.OkHttpClientFactory
import io.ktor.client.engine.HttpClientEngineFactory
import com.openstore.app.core.common.lazyUnsafe
import com.openstore.app.core.os.Android

actual class NetModule actual constructor(
    netConfig: NetConfig,
) {
    actual val clientEngineFactory: HttpClientEngineFactory<*> by lazyUnsafe { OkHttpClientFactory }
    actual val networkProvider: NetworkProviderLauncher by lazyUnsafe { PlatformNetworkProvider(Android.context) }
    actual val logger: NetLogger by lazyUnsafe { NetLogger() }

    init {
        networkProvider.launch()
        logger.isEnabled(netConfig.isLogging)
    }
}
