@file:OptIn(ExperimentalSerializationApi::class)

package com.openstore.app.core.net.json_rpc

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class JsonRpcRequest(
    val method: String,
    val params: JsonElement? = null,
    @EncodeDefault val id: Int = 0,
) {
    @EncodeDefault
    val jsonrpc: String = "2.0"
}
