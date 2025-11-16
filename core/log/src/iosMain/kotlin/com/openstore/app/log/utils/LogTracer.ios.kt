package com.openstore.app.log.utils

import platform.Foundation.NSThread
import kotlin.experimental.ExperimentalNativeApi

actual class LogTracer {

    @OptIn(ExperimentalNativeApi::class)
    actual fun trace(className: String): LogTrace? {
        val stackTrace = Throwable().getStackTrace()
        val trace = findCallerTrace(stackTrace, className)
            ?: return null

        val threadName = NSThread.currentThread.name ?: "main"
        return LogTrace(threadName, trace.className, trace.methodName, trace.lineNumber)
    }

    private fun findCallerTrace(stackTrace: Array<String>, targetClassName: String): ParsedStackFrame? {
        var found = false
        for (frame in stackTrace) {
            val parsed = parseStackFrame(frame)
            if (parsed != null) {
                if (parsed.className == targetClassName) {
                    found = true
                }
                if (found && parsed.className != targetClassName) {
                    return parsed
                }
            }
        }
        return null
    }

    private fun parseStackFrame(frame: String): ParsedStackFrame? {
        val kfunMatch = Regex("""kfun:([^#]+)#([^(]+)\(""").find(frame)
        if (kfunMatch != null) {
            val fullClassName = kfunMatch.groupValues[1]
            val methodName = kfunMatch.groupValues[2]
            return ParsedStackFrame(fullClassName, methodName, 0)
        }

        val atMatch = Regex("""at (\d+)\s+\S+\s+\S+\s+kfun:([^#]+)#([^(]+)""").find(frame)
        if (atMatch != null) {
            val fullClassName = atMatch.groupValues[2]
            val methodName = atMatch.groupValues[3]
            return ParsedStackFrame(fullClassName, methodName, 0)
        }

        val simpleMatch = Regex("""([a-zA-Z_][\w.]*\.[a-zA-Z_][\w.]*)\.(\w+)\(""").find(frame)
        if (simpleMatch != null) {
            val className = simpleMatch.groupValues[1]
            val methodName = simpleMatch.groupValues[2]
            return ParsedStackFrame(className, methodName, 0)
        }

        return null
    }

    private data class ParsedStackFrame(
        val className: String,
        val methodName: String,
        val lineNumber: Int
    )
}