package com.openstore.app.log

import com.openstore.app.log.utils.LogArchiver
import com.openstore.app.log.utils.LogTracer
import kotlinx.coroutines.CoroutineScope

data class LoggerConfig(
    val logsDir: LogFile,
    val sharedDir: LogFile,
    val tracer: LogTracer,
    val archiver: LogArchiver,
    val scope: CoroutineScope,
    val archiveName: String = DEFAULT_LOG_ARCHIVE,
    val maxLength: Int = LOGGER_ENTRY_MAX_LEN
) {
    companion object {
        private const val DEFAULT_LOG_ARCHIVE = "OS.logup.zip"
        const val LOGGER_ENTRY_MAX_LEN = 6 * 1024
    }

    fun outputArchive(): LogFile {
        return LogFile(logsDir.toPath(), archiveName)
    }

    fun outputPublicArchive(): LogFile {
        return LogFile(sharedDir.toPath(), archiveName)
    }
}
