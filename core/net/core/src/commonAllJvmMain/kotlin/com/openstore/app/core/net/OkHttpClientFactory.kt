package com.openstore.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionPool
import okhttp3.Dispatcher

internal object OkHttpClientFactory : HttpClientEngineFactory<OkHttpConfig> {

    private const val MAX_IDLE_CONNECTIONS = 20
    private const val IDLE_TIMEOUT = 5L

    private const val CONNECTION_TIMEOUT = 30L

    private val connectionPool = ConnectionPool(MAX_IDLE_CONNECTIONS, IDLE_TIMEOUT, TimeUnit.SECONDS)
    private val dispatcher = Dispatcher()

    override fun create(block: OkHttpConfig.() -> Unit): HttpClientEngine {
        val defaultHttpConfig = OkHttpConfig().apply {
            config {
                connectionPool(connectionPool)
                dispatcher(this@OkHttpClientFactory.dispatcher)

                readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            }
        }

        return OkHttpEngine(defaultHttpConfig.apply(block))
    }
}
