package com.openstore.app.log

import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
object L {

    @Suppress("EnumEntryName")
    enum class LogType {
        v, d, i, w, e;
    }

    @Volatile
    private lateinit var config: LoggerConfig

    @Volatile
    private lateinit var targets: List<LogTarget>

    private val isInit = AtomicBoolean(false)
    private val lock = Mutex()

    private val logStringBuilder by lazy {
        StringBuilder()
    }

    fun initialize(config: LoggerConfig, targets: List<LogTarget>) {
        if (isInit.compareAndSet(false, true)) {
            this.config = config
            this.targets = targets

            targets.forEach { it.prepare(config) }
        }
    }

    fun setTargets(newTargets: List<LogTarget>) {
        if (!isInit.load()) {
            return
        }

        config.scope.launch {
            lock.withLock {
                targets.forEach { it.release() }
                targets = newTargets
                targets.forEach { it.prepare(config) }
            }
        }
    }

    fun hasTargets(): Boolean {
        return try {
            targets.isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }

    fun hasLogs(): Boolean {
        if (!isInit.load()) {
            return false
        }

        return targets.map { it.files() }
            .flatten()
            .isNotEmpty()
    }

    fun capture(onComplete: (LogFile?) -> Unit) {
        if (!isInit.load()) {
            return
        }

        config.scope.launch {
            lock.withLock {
                val files = targets.map { it.files() }
                    .flatten()

                val outputPub = config.outputPublicArchive()

                val archiver = config.archiver

                if (!archiver.archive(files)) {
                    onComplete(null)
                    return@withLock
                }

                onComplete(outputPub)
            }
        }
    }

    fun v(e: Throwable, vararg o: Any) = logEx(LogType.v, e, *o)
    fun v(vararg o: Any) = log(LogType.v, *o)

    fun d(e: Throwable, vararg o: Any) = logEx(LogType.d, e, *o)
    fun d(vararg o: Any) = log(LogType.d, *o)

    fun i(e: Throwable, vararg o: Any) = logEx(LogType.i, e, *o)
    fun i(vararg o: Any) = log(LogType.i, *o)

    fun w(e: Throwable, vararg o: Any) = logEx(LogType.w, e, *o)
    fun w(vararg o: Any) = log(LogType.w, *o)

    fun e(e: Throwable, vararg o: Any) = logEx(LogType.e, e, *o)
    fun e(vararg o: Any?) = log(LogType.e, *o)
    fun e(e: Throwable) = logEx(LogType.e, e)

    private fun log(logType: LogType, vararg o: Any?) {
        logEx(logType, null, *o)
    }

    private fun logEx(
        logType: LogType,
        e: Throwable?,
        vararg o: Any?,
        loggerClassName: String = L::class.simpleName.orEmpty()
    ) {
        if (!isInit.load()) {
//            val msg = o.joinToString(" | ") { it.toString() }
//            Log.println(
//                logType.toLog(),
//                "L", "Log before init L!"
//                    + "\nMessage: " + msg
//                    + "\nError: " + e?.stackTraceToString()
//            )
            return
        }

        if (targets.isEmpty()) {
            return
        }

        val element = config.tracer.trace(loggerClassName)

        config.scope.launch {
            lock.withLock {
                logExSync(
                    logType = logType,
                    e = e,
                    className = element?.className ?: loggerClassName,
                    o = o,
                    threadName = element?.thread ?: "unknown",
                    methodName = element?.methodName ?: "unknown",
                    lineNumber = element?.lineNumber ?: 0
                )
            }
        }
    }

    private fun logExSync(
        logType: LogType,
        e: Throwable?,
        className: String,
        threadName: String,
        methodName: String,
        lineNumber: Int,
        vararg o: Any?,
    ) {
        logStringBuilder
            .clear()
            .append("[$threadName] $methodName:$lineNumber ")

        for (obj in o) {
            val maxLength = config.maxLength
            val data = if (obj is CharSequence && obj.length > maxLength) {
                obj.substring(0, maxLength)
            } else {
                obj
            }
            logStringBuilder.append(data).append(" ")

            if (logStringBuilder.length >= maxLength) {
                // strip long input data.
                logStringBuilder.append(" ...(strip long data, more then $maxLength bytes) ")
                break
            }
        }

        val substringWithSimpleClassName = className.substringAfterLast(".")

        var tag = className
        if (substringWithSimpleClassName != className) {
            tag = substringWithSimpleClassName
        }

        val msg = logStringBuilder.toString()
        if (e == null) {
            targets.forEach { target ->
                target.log(logType, tag, msg)
            }
        } else {
            targets.forEach { target ->
                target.log(logType, tag, msg, e)
            }
        }
    }
}
