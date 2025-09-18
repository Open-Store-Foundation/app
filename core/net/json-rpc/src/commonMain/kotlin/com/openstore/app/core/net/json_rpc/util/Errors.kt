package com.openstore.app.core.net.json_rpc.util

sealed class RpcError(
    message: String?,
    cause: Throwable? = null,
) : Throwable(message, cause) {

    class ErrorResponse(
        val code: Int,
        override val message: String,
        cause: Throwable? = null
    ) : RpcError(message, cause)
}
