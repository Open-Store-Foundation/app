package com.openstore.app.log.targets.file.engine

import android.util.Log
import com.openstore.app.log.LogFile
import com.openstore.app.log.LoggerConfig
import com.openstore.app.log.utils.FileManager
import java.io.File

abstract class FileWritable {

    internal val fileManager = FileManager
    protected val operationsLock = Any()

    fun write(msg: String) {
        try {
            writeImpl(msg)
        } catch (th: Throwable) {
            Log.e("FileWritable", "File writable error", th)
        }
    }

    protected abstract fun writeImpl(msg: String)

    abstract fun onInit(config: LoggerConfig)
    abstract fun onRelease()

    abstract fun getFiles(): List<LogFile>
    abstract fun canWrite(): Boolean
}
