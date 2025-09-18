package com.openstore.app.core.net.json_rpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

@Serializable
data class JsonRpcResponse(
    val id: Int,
    val result: JsonElement = JsonNull,
    val error: Error?,
) {
    @Serializable
    data class Error(
        val code: Int,
        val message: String,
    )
}
