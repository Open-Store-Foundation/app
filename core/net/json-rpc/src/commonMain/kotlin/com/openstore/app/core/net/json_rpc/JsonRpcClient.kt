package com.openstore.app.core.net.json_rpc

import com.openstore.app.core.net.json_rpc.util.RpcError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonElement

class JsonRpcClient(
    private val url: String,
    private val client: HttpClient,
) {

    suspend fun <Response> send(
        request: JsonRpcRequest,
        headers: Map<String, String> = emptyMap(),
        decodeFromJsonElement: (JsonElement) -> Response,
    ): Response {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)

            headers {
                headers.forEach { (key, value) -> append(key, value) }
            }
        }

        return parseJsonRpcResponse(
            response = response.bodyOrError<JsonRpcResponse>(),
            decodeFromJsonElement = decodeFromJsonElement
        )
    }

    private fun <Response> parseJsonRpcResponse(
        response: JsonRpcResponse,
        decodeFromJsonElement: (JsonElement) -> Response,
    ): Response =
        if (response.error != null) {
            throw RpcError.ErrorResponse(response.error.code, response.error.message)
        } else {
            decodeFromJsonElement(response.result)
        }

    private suspend inline fun <reified T> HttpResponse.bodyOrError(): T {
        return if (status.isSuccess()) {
            body()
        } else {
            throw RpcError.ErrorResponse(status.value, status.description)
        }
    }
}
