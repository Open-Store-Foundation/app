package com.openstore.app.log

interface LogTarget {

    fun log(type: L.LogType, tag: String?, msg: String?)

    fun log(type: L.LogType, tag: String?, msg: String?, tr: Throwable?) {
        log(type, tag, msg + '\n'.toString() + tr?.stackTraceToString())
    }

    fun files(): List<LogFile> { return emptyList() }

    fun prepare(config: LoggerConfig) {}

    fun release() {}
}
