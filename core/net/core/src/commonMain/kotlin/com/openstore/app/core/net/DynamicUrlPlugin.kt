package com.openstore.app.core.net

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.url
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.util.AttributeKey
import io.ktor.utils.io.InternalAPI

interface DynamicUrlProvider {
    suspend fun getPlaceholder(): String
    suspend fun getHost(): String
}

class DynamicUrlConfig {
    var provider: DynamicUrlProvider? = null
}

class DynamicUrlPlugin internal constructor(
    private val provider: DynamicUrlProvider
) {

    companion object Plugin : HttpClientPlugin<DynamicUrlConfig, DynamicUrlPlugin> {

        override val key: AttributeKey<DynamicUrlPlugin> = AttributeKey("DynamicUrlPlugin")

        override fun prepare(block: DynamicUrlConfig.() -> Unit): DynamicUrlPlugin {
            val config = DynamicUrlConfig()
                .apply(block)

            val provider = config.provider
            requireNotNull(provider) { "Plugin storage shouldn't be null" }

            return DynamicUrlPlugin(provider)
        }

        @OptIn(InternalAPI::class)
        override fun install(plugin: DynamicUrlPlugin, scope: HttpClient) {
            scope.sendPipeline.intercept(HttpSendPipeline.State) {
                val newUrl = context.url.buildString()
                    .replace(
                        plugin.provider.getPlaceholder(),
                        plugin.provider.getHost()
                    )


                context.url.encodedParameters.clear()
                context.url.parameters.clear()

                context.url(newUrl)

                proceed()
            }
        }
    }
}
