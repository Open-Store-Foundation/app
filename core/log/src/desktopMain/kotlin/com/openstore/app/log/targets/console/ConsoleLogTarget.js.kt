package com.openstore.app.log.targets.console

import com.openstore.app.log.L
import com.openstore.app.log.L.LogType.d
import com.openstore.app.log.L.LogType.e
import com.openstore.app.log.L.LogType.i
import com.openstore.app.log.L.LogType.v
import com.openstore.app.log.L.LogType.w
import com.openstore.app.log.LogTarget
import java.util.logging.Logger

actual open class ConsoleLogTarget : LogTarget {
    actual override fun log(
        type: L.LogType,
        tag: String?,
        msg: String?
    ) {
        Logger.getGlobal().log(type.toLog(), tag, msg)
    }
}