package com.openstore.app.log.utils

import com.openstore.app.log.LogFile

actual class LogArchiver {
    actual fun archive(files: List<LogFile>): Boolean {
        return true
    }
}