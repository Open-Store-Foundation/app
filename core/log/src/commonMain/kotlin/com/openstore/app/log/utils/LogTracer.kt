package com.openstore.app.log.utils

data class LogTrace(
    val thread: String,
    val className: String,
    val methodName: String,
    val lineNumber: Int,
)

expect class LogTracer {
    fun trace(className: String): LogTrace?
}