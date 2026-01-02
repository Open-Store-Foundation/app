package foundation.openstore.signer.app

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

object IosApp {

    class Config(
        val groupId: String,
        val bundleId: String,
    )

    val appGroupId: String by lazy {
        IosBundleConfig.getAppGroupId(IosBundleConfig.getCurrentBundleId())
    }

    val bundleId: String by lazy {
        IosBundleConfig.getNormalizedCurrentBundleId()
    }

    fun initialize() {
        BuildConfig.setIsDebug(true)
        initLogger()

        Mvi.init(Mvi.Config(useThreadCheck = true))

        SignerInjection.init(
            Config(
                groupId = appGroupId,
                bundleId = bundleId,
            )
        )
    }

    private fun initLogger() {
        if (BuildConfig.isDebug) {
            L.initialize(
                config = LoggerConfig(
                    logsDir = LogFile(""),
                    sharedDir = LogFile(""),
                    scope = Async.globalScope(),
                    tracer = LogTracer(),
                    archiver = LogArchiver(),
                ),
                targets = listOf(ConsoleLogTarget()),
            )
        }
    }
}

