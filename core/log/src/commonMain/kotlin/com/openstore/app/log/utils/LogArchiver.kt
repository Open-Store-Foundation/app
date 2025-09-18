package com.openstore.app.log.utils

import com.openstore.app.log.LogFile

expect class LogArchiver {
    fun archive(files: List<LogFile>): Boolean
}
