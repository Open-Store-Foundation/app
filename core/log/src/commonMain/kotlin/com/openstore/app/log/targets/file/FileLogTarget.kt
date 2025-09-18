package com.openstore.app.log.targets.file

import com.openstore.app.log.L
import com.openstore.app.log.LogFile
import com.openstore.app.log.LogTarget
import com.openstore.app.log.LoggerConfig

expect open class FileLogTarget: LogTarget {
    override fun prepare(config: LoggerConfig)
    override fun release()
    override fun log(type: L.LogType, tag: String?, msg: String?)
    override fun files(): List<LogFile>
}
