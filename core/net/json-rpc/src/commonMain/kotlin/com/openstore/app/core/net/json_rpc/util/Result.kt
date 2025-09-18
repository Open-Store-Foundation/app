package com.openstore.app.core.net.json_rpc.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ResponseResult<T>(
    @SerialName("data")
    val data: T
)
