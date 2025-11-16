package com.openstore.app.core.net

import android.content.Context
import com.openstore.app.OkHttpClientFactory
import io.ktor.client.engine.HttpClientEngineFactory
import com.openstore.app.core.common.lazyUnsafe

actual class NetModule (
    private val context: Context,
    netConfig: NetConfig,
) {
    actual val clientEngineFactory: HttpClientEngineFactory<*> by lazyUnsafe { OkHttpClientFactory }
    actual val networkProvider: NetworkProviderLauncher by lazyUnsafe { PlatformNetworkProvider(context) }
    actual val logger: NetLogger by lazyUnsafe { NetLogger() }

    init {
        networkProvider.launch()
        logger.isEnabled(netConfig.isLogging)
    }
}
