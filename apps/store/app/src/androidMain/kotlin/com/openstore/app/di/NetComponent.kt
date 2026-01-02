package com.openstore.app.di

import android.app.Application
import com.openstore.app.core.net.DynamicUrlPlugin
import com.openstore.app.core.net.DynamicUrlProvider
import com.openstore.app.data.db.EtagKey
import com.openstore.app.data.db.EtagValue
import com.openstore.app.json.BigDecimalSerializer
import com.openstore.app.json.BigIntegerSerializer
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import com.openstore.app.core.net.EtagPlugin
import com.openstore.app.core.net.EtagStorage
import com.openstore.app.core.net.NetConfig
import com.openstore.app.core.net.NetModule
import com.openstore.app.core.net.NetworkProvider
import com.openstore.app.core.net.NetworkProviderLauncher
import com.openstore.app.data.node.AppNodes
import com.openstore.app.data.node.CustomNodeType
import com.openstore.app.data.node.NodeRepo
import com.openstore.app.data.node.NodeRepoStorage
import com.openstore.app.di.DataComponentDefault.Companion.NODES_STORAGE
import foundation.openstore.kitten.api.Component
import io.ktor.client.HttpClientConfig
import org.openwallet.kitten.core.depLazy

interface NetComponent : Component {
    val baseGfHost: String
    val baseEthHost: String
    val baseClientHost: String
    val baseStatHost: String

    val nodeRepo: NodeRepo
    val json: Json

    val networkProvider: NetworkProvider

    fun clientHttpClient(): HttpClient
    fun bscHttpClient(): HttpClient
    fun greenfieldHttpClient(): HttpClient
    fun emptyHttpClient(): HttpClient
}

class NetComponentDefault(
    private val app: Application,
    private val appNodes: AppNodes,
    private val modules: StorageComponent,
) : NetComponent {

    companion object {
        const val NODE_PLACEHOLDER = "https://placeholder.net"
    }

    private val etagDao by depLazy {
        modules.appDatabase.getEtagDao()
    }

    private val etagStorage by depLazy {
        object : EtagStorage {
            override suspend fun getEtag(url: String): String? {
                return etagDao.getKey(url)?.etag
            }

            override suspend fun setEtag(url: String, etag: String) {
                etagDao.insert(EtagKey(url, etag))
            }

            override suspend fun getBody(url: String): String? {
                return etagDao.getValue(url)?.body
            }

            override suspend fun setBody(url: String, body: String) {
                etagDao.insert(EtagValue(url, body = body))
            }
        }
    }

    override val nodeRepo: NodeRepo by depLazy {
        NodeRepoStorage(
            defaultNodes = appNodes,
            storage = modules.keyValueFactory.create(NODES_STORAGE)
        )
    }

    private val netModule by depLazy {
        NetModule(
            context = app,
            netConfig = NetConfig(
                isLogging = true
            )
        )
    }

    override val baseEthHost by depLazy {
        NODE_PLACEHOLDER
    }

    override val baseGfHost by depLazy {
        NODE_PLACEHOLDER
    }

    override val baseClientHost by depLazy {
        "${NODE_PLACEHOLDER}/v1"
    }

    override val baseStatHost by depLazy {
        NODE_PLACEHOLDER
    }

    override val json by depLazy {
        Json {
            explicitNulls = false
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
            encodeDefaults = true
            coerceInputValues = true
            serializersModule += SerializersModule {
                contextual(BigIntegerSerializer)
                contextual(BigDecimalSerializer)
            }
        }
    }

    override val networkProvider by depLazy {
        netModule.networkProvider
    }

    override fun clientHttpClient(): HttpClient {
        return jsonHttpClient {
            install(DynamicUrlPlugin) {
                provider = object : DynamicUrlProvider {
                    override suspend fun getPlaceholder(): String {
                        return NODE_PLACEHOLDER
                    }

                    override suspend fun getHost(): String {
                        return nodeRepo.getNode(CustomNodeType.API)
                    }
                }
            }

            install(EtagPlugin) {
                storage = etagStorage
            }
        }
    }

    override fun bscHttpClient(): HttpClient {
        return jsonHttpClient {
            install(DynamicUrlPlugin) {
                provider = object : DynamicUrlProvider {
                    override suspend fun getPlaceholder(): String {
                        return NODE_PLACEHOLDER
                    }

                    override suspend fun getHost(): String {
                        return nodeRepo.getNode(CustomNodeType.BSC)
                    }
                }
            }
        }
    }

    override fun greenfieldHttpClient(): HttpClient {
        return jsonHttpClient {
            install(DynamicUrlPlugin) {
                provider = object : DynamicUrlProvider {
                    override suspend fun getPlaceholder(): String {
                        return NODE_PLACEHOLDER
                    }

                    override suspend fun getHost(): String {
                        return nodeRepo.getNode(CustomNodeType.GREENFIELD)
                    }
                }
            }
        }
    }

    private fun jsonHttpClient(
        config: HttpClientConfig<*>.() -> Unit
    ): HttpClient {
        val client = HttpClient(netModule.clientEngineFactory) {
            config()

            install(Logging) {
                logger = netModule.logger
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(json, ContentType.Any)
            }
        }

        // retry
        client.plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value !in 100..399) {
                execute(request)
            } else {
                originalCall
            }
        }

        return client
    }

    override fun emptyHttpClient(): HttpClient {
        return HttpClient(netModule.clientEngineFactory)
    }
}