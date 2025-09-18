package com.openstore.app.log.targets.console

import com.openstore.app.log.L
import com.openstore.app.log.LogTarget

expect open class ConsoleLogTarget : LogTarget {
    override fun log(type: L.LogType, tag: String?, msg: String?)
}
