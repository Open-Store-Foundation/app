package com.openstore.app.log.targets.console

import com.openstore.app.log.L
import com.openstore.app.log.L.LogType.d
import com.openstore.app.log.L.LogType.e
import com.openstore.app.log.L.LogType.i
import com.openstore.app.log.L.LogType.v
import com.openstore.app.log.L.LogType.w
import java.util.logging.Level

fun L.LogType.toLog(): Level {
    return when (this) {
        v -> Level.INFO
        d -> Level.ALL
        i -> Level.INFO
        w -> Level.WARNING
        e -> Level.SEVERE
    }
}
