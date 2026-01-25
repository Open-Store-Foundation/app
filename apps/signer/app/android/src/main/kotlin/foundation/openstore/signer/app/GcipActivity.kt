package foundation.openstore.signer.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.openstore.app.mvi.OnceOnly
import com.openstore.app.ui.setAppTheme
import com.openstore.app.ui.systemAppTheme
import foundation.openstore.gcip.platform.GcipHandlerFactoryPlatform
import foundation.openstore.signer.app.screens.gcip.GcipApp

class GcipActivity : ComponentActivity() {

    val intentHandler by lazy {
        GcipHandlerFactoryPlatform.intentHandler()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val systemTheme = systemAppTheme()

            OnceOnly {
                setAppTheme(systemTheme)
            }

            GcipApp(
                isFullScreen = false,
                provideData = {
                    intentHandler.handleIntent(this)
                },
                onConfirmed = {
                    val intent = intentHandler.createIntent(it)
                    setResult(RESULT_OK, intent)
                    finish()
                },
                onError = {
                    val intent = it?.let { intentHandler.createIntent(it) }
                    setResult(RESULT_CANCELED, intent)
                    finish()
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setResult(RESULT_CANCELED)
        finish()
//  TODO pass intent       handleException(GcipStatusCode.TOO_MANY_REQUESTS, null)
    }
}