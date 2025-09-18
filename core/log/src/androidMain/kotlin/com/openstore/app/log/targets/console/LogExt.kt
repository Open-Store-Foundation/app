package com.openstore.app.log.targets.console

import android.util.Log
import com.openstore.app.log.L
import com.openstore.app.log.L.LogType.d
import com.openstore.app.log.L.LogType.e
import com.openstore.app.log.L.LogType.i
import com.openstore.app.log.L.LogType.v
import com.openstore.app.log.L.LogType.w

fun L.LogType.toLog(): Int {
    return when (this) {
        v -> Log.VERBOSE
        d -> Log.DEBUG
        i -> Log.INFO
        w -> Log.WARN
        e -> Log.ERROR
    }
}
