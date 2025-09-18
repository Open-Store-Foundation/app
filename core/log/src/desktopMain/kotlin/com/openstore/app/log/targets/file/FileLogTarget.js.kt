package com.openstore.app.log.targets.file

import com.openstore.app.log.L
import com.openstore.app.log.LogFile
import com.openstore.app.log.LogTarget
import com.openstore.app.log.LoggerConfig

actual open class FileLogTarget : LogTarget {
    actual override fun prepare(config: LoggerConfig) {

    }

    actual override fun release() {
    }

    actual override fun log(
        type: L.LogType,
        tag: String?,
        msg: String?
    ) {

    }

    actual override fun files(): List<LogFile> {
        return emptyList()
    }
}