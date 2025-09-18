package com.openstore.app.core.os

import kotlinx.browser.window

object Js {

    val isDom: Boolean get() {
        return jsTypeOf(window) != "undefined"
    }

    val isNodeJs: Boolean get() {
        return runCatching { js("process.release.name") }
            .getOrNull() == "node"
    }
}
