package com.openstore.app

import android.app.Application
import android.content.Context
import com.openstore.app.core.async.Async
import com.openstore.app.core.config.BuildConfig
import com.openstore.app.core.os.Android
import com.openstore.app.log.L
import com.openstore.app.log.LogFile
import com.openstore.app.log.LoggerConfig
import com.openstore.app.log.targets.console.ConsoleLogTarget
import com.openstore.app.log.utils.LogArchiver
import com.openstore.app.log.utils.LogTracer
import com.openstore.app.mvi.Mvi
import com.openstore.app.di.OpenStoreInjection
import com.openstore.app.installer.utils.ApkFileDestination
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class AppDelegate : Application() {

    override fun onCreate() {
        BuildConfig.setIsDebug(com.openstore.app.BuildConfig.DEBUG)
        Mvi.init(Mvi.Config(useThreadCheck = BuildConfig.isDebug))

        super.onCreate()

        initLogger(this)
        OpenStoreInjection.init(this)
    }

    // TODO setup logs in Settings
    private fun initLogger(context: Context) {
        if (BuildConfig.isDebug) {
            L.initialize(
                config = LoggerConfig(
                    logsDir = LogFile(""),
                    sharedDir = LogFile(""),
                    scope = Async.globalScope(),
                    tracer = LogTracer(),
                    archiver = LogArchiver(
                        output = File(""),
                        outputPub = File(""),
                        pubKeyProvider = { Android.pubKey(context) }
                    ),
                ),
                targets = listOf(ConsoleLogTarget()),
            )
        }
    }
}
