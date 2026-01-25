package foundation.openstore.signer.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.compose.rememberNavController
import foundation.openstore.signer.app.di.SignerInjection
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    SignerInjection.init()

    onWasmReady {
        ComposeViewport {
            val navigator = rememberNavController()
            App(navigator)
        }
    }
}
