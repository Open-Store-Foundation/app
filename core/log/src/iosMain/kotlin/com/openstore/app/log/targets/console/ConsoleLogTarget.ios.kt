package com.openstore.app.log.targets.console

import com.openstore.app.log.L
import com.openstore.app.log.LogTarget

actual open class ConsoleLogTarget : LogTarget {
    actual override fun log(
        type: L.LogType,
        tag: String?,
        msg: String?
    ) {
        println("[${type.name}] [${tag}]: ${msg ?: "EMPTY MESSAGE"}")
    }
}
