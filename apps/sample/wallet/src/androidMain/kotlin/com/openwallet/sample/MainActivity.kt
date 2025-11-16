package com.openwallet.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.openstore.app.ui.AppTheme
import com.openstore.app.ui.AvoirTheme
import com.openwallet.sample.di.WalletInjection
import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.platform.GcipHandlerFactoryPlatform

class MainActivity : ComponentActivity() {

    private val handler by lazy {
        GcipHandlerFactoryPlatform.intentHandler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WalletInjection.init(application)

        setContent {
            val view = WindowCompat.getInsetsController(window, window.decorView)
            view.isAppearanceLightStatusBars = true

            var responseData by remember { mutableStateOf<ByteArray?>(null) }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                responseData = result.data?.getByteArrayExtra(GcipConfig.Data)
            }


            AvoirTheme {
                WalletApp(
                    responseData = responseData,
                    onResponseConsumed = { responseData = null },
                    startIntent = { bytes ->
                        launcher.launch(handler.createRequestIntent(bytes, null))
                    },
                    onMessage = { msg ->
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }
}
