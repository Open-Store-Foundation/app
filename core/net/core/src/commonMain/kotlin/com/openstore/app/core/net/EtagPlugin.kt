package com.openstore.app.core.net

import com.openstore.app.log.L
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.DefaultHttpResponse
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.request
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey
import io.ktor.util.cio.use
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import io.ktor.util.pipeline.PipelinePhase
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.build
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.pool.ByteArrayPool
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray

interface EtagStorage {
    suspend fun getEtag(url: String): String?
    suspend fun setEtag(url: String, etag: String)

    suspend fun getBody(url: String): String?
    suspend fun setBody(url: String, body: String)
}

class EtagConfig {
    var storage: EtagStorage? = null
}

// TODO add request filter
class EtagPlugin internal constructor(
    private val storage: EtagStorage?,
) {

    companion object Plugin : HttpClientPlugin<EtagConfig, EtagPlugin> {

        override val key: AttributeKey<EtagPlugin> = AttributeKey("EtagPlugin")

        override fun prepare(block: EtagConfig.() -> Unit): EtagPlugin {
            val config = EtagConfig()
                .apply(block)

            return EtagPlugin(config.storage)
        }

        @OptIn(InternalAPI::class)
        override fun install(plugin: EtagPlugin, scope: HttpClient) {
            scope.sendPipeline.intercept(HttpSendPipeline.State) {
                requireNotNull(plugin.storage) { "Plugin storage shouldn't be null" }

                if (context.method == HttpMethod.Get) {
                    val url = context.url.toString()
                    val knownEtag = plugin.storage.getEtag(url)

                    if (knownEtag != null) {
                        context.headers.append(HttpHeaders.IfNoneMatch, knownEtag)
                    }
                }

                proceed()
            }

            val cacheResponsePhase = PipelinePhase("Cache")
            scope.receivePipeline.insertPhaseAfter(HttpReceivePipeline.State, cacheResponsePhase)
            scope.receivePipeline.intercept(cacheResponsePhase) { response ->
                requireNotNull(plugin.storage) { "Plugin storage shouldn't be null" }

                val storage = plugin.storage

                val request = response.request
                val url = request.url.toString()

                if (response.status == HttpStatusCode.NotModified) {
                    val cachedBody = storage.getBody(url)

                    if (cachedBody != null) {
                        L.d("ETag cache-hit for: $url")

                        finish()

                        val newResponseData = HttpResponseData(
                            statusCode = response.status,
                            requestTime = response.requestTime,
                            headers = Headers.build {
                                appendAll(response.headers)
                                append(HttpHeaders.ContentType, "application/json")
                            },
                            version = response.version,
                            body = ByteReadChannel(cachedBody.decodeBase64Bytes()),
                            callContext = response.coroutineContext
                        )

                        val newResponse = DefaultHttpResponse(request.call, newResponseData)

                        proceedWith(newResponse)
                        return@intercept
                    } else {
                        proceed()
                        return@intercept
                    }
                }

                val responseEtag = response.headers[HttpHeaders.ETag]
                if (request.method != HttpMethod.Get || !response.status.isSuccess() || responseEtag.isNullOrBlank()) {
                    proceed()
                    return@intercept
                }

                L.d("ETag saving cache for: $responseEtag - $url")
                val outputChannel = ByteChannel()

                CoroutineScope(response.coroutineContext).launch {
                    val body = BytePacketBuilder()
                    val buffer = ByteArrayPool.borrow()
                    val channel = response.rawContent

                    try {
                        outputChannel.use {
                            while (!channel.isClosedForRead) {
                                val bytesRead = channel.readAvailable(buffer)
                                if (bytesRead == -1) {
                                    // End of stream reached
                                    break
                                }

                                if (bytesRead > 0) {
                                    writeFully(buffer, 0, bytesRead)
                                    body.writeFully(buffer, 0, bytesRead)
                                    flush()
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    } finally {
                        ByteArrayPool.recycle(buffer)
                    }

                    storage.setEtag(url, responseEtag)
                    storage.setBody(url, body.build().readByteArray().encodeBase64())
                }

                finish()

                val newResponseData = HttpResponseData(
                    statusCode = response.status,
                    requestTime = response.requestTime,
                    headers = Headers.build {
                        appendAll(response.headers)
                    },
                    version = response.version,
                    body = outputChannel,
                    callContext = response.coroutineContext
                )

                val newResponse = DefaultHttpResponse(request.call, newResponseData)

                proceedWith(newResponse)

                return@intercept
            }
        }
    }
}
