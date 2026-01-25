package foundation.openstore.signer.app

import android.app.Application
import android.content.Context
import com.openstore.app.core.async.Async
import com.openstore.app.core.config.BuildConfig
import com.openstore.app.log.L
import com.openstore.app.log.LogFile
import com.openstore.app.log.LoggerConfig
import com.openstore.app.log.targets.console.ConsoleLogTarget
import com.openstore.app.log.utils.LogArchiver
import com.openstore.app.log.utils.LogTracer
import com.openstore.app.mvi.Mvi
import foundation.openstore.signer.app.di.SignerInjection
import java.io.File
import kotlin.use

class AppDelegate : Application() {

    override fun onCreate() {
        BuildConfig.setIsDebug(foundation.openstore.signer.app.BuildConfig.DEBUG)
        Mvi.init(Mvi.Config(useThreadCheck = BuildConfig.isDebug))

        super.onCreate()

        initLogger(this)
        SignerInjection.init(this)
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
                        pubKeyProvider = { pubKey(context) }
                    ),
                ),
                targets = listOf(ConsoleLogTarget()),
            )
        }
    }

    fun pubKey(context: Context): ByteArray {
        return context.assets.open("pub")
            .use {
                ByteArray(it.available())
                    .apply { it.read(this) }
            }
    }
}
