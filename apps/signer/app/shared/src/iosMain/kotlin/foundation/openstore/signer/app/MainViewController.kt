package foundation.openstore.signer.app

import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.compose.rememberNavController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App(
        navigator = rememberNavController()
    )
}
