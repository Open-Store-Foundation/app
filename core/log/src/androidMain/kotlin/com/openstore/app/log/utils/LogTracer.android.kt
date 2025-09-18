package com.openstore.app.log.utils

actual class LogTracer {
    actual fun trace(className: String): LogTrace? {
        val thread = Thread.currentThread()
        val trace = trace(thread, className)
            ?: return null

        return LogTrace(thread.name, trace.className, trace.methodName, trace.lineNumber)
    }

    private fun trace(thread: Thread, className: String): StackTraceElement? {
        val e = thread.stackTrace
        var found = false
        for (s in e) {
            if (s.className == className) {
                found = true
            }
            if (found && s.className != className) {
                return s
            }
        }
        return null
    }
}