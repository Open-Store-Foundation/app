package com.openstore.app.log.targets.console

import com.openstore.app.log.L
import com.openstore.app.log.L.LogType.d
import com.openstore.app.log.L.LogType.e
import com.openstore.app.log.L.LogType.i
import com.openstore.app.log.L.LogType.v
import com.openstore.app.log.L.LogType.w
import com.openstore.app.log.LogTarget

actual open class ConsoleLogTarget : LogTarget {
    actual override fun log(
        type: L.LogType,
        tag: String?,
        msg: String?
    ) {
        when (type) {
            v -> console.log("[${tag}]", msg ?: "EMPTY MESSAGE")
            d -> console.log("[${tag}]", msg ?: "EMPTY MESSAGE")
            i -> console.info("[${tag}]", msg ?: "EMPTY MESSAGE")
            w -> console.warn("[${tag}]", msg ?: "EMPTY MESSAGE")
            e -> console.error("[${tag}]", msg ?: "EMPTY MESSAGE")
        }
    }
}