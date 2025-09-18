package com.openstore.app.core.net.json_rpc.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*

suspend inline fun <reified T> HttpResponse.bodyOrError(errorKey: String = "message"): T {
    return if (status.isSuccess() || status == HttpStatusCode.NotModified) {
        try {
            body<T>()
        } catch (e: JsonConvertException) {
            throw RpcError.ErrorResponse(status.value, errorMessage(errorKey), e)
        }
    } else {
        throw RpcError.ErrorResponse(status.value, errorMessage(errorKey))
    }
}

suspend inline fun <reified T> HttpResponse.apiBodyOrError(errorKey: String = "message"): T {
    return if (status.isSuccess() || status == HttpStatusCode.NotModified) {
        try {
            body<ResponseResult<T>>().data
        } catch (e: JsonConvertException) {
            throw RpcError.ErrorResponse(status.value, errorMessage(errorKey), e)
        }
    } else {
        throw RpcError.ErrorResponse(status.value, errorMessage(errorKey))
    }
}

suspend inline fun HttpResponse.errorMessage(errorKey: String = "message"): String =
    bodyAsText().getJsonField(errorKey).trim()

fun String.getJsonField(name: String): String {
    val jsonBody = runCatching { Json.decodeFromString<JsonElement>(this) }
        .getOrNull()

    val jsonElement = jsonBody?.getJsonFieldRecursively(name)

    return (jsonElement as? JsonPrimitive)
        ?.contentOrNull
        ?: jsonElement.toString()
}

private fun JsonElement.getJsonFieldRecursively(fieldName: String): JsonElement? {
    if (this is JsonObject) {
        if (this.containsKey(fieldName)) {
            return this[fieldName]
        } else {
            for (value in this.values) {
                val result = value.getJsonFieldRecursively(fieldName)
                if (result != null) {
                    return result
                }
            }
        }
    } else if (this is JsonArray) {
        for (item in this) {
            val result = item.getJsonFieldRecursively(fieldName)
            if (result != null) {
                return result
            }
        }
    }
    return null
}

suspend fun <T> retryIfFails(
    times: Int = 3,
    initialDelay: Long = 400,
    maxDelay: Long = 1600,
    factor: Int = 400,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (t: RpcError) {
            println(t)
        }
        delay(currentDelay)
        currentDelay = (currentDelay + factor).coerceAtMost(maxDelay)
    }
    return block()
}

