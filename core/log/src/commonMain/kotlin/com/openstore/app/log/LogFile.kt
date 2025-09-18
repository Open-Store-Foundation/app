package com.openstore.app.log

class LogFile(private vararg val paths: String) {
    fun toPath(): String {
        return paths.joinToString("/")
    }
}
