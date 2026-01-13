package foundation.openstore.signer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.openstore.app.ui.AppTheme
import com.openstore.app.ui.rememberAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val viewModel = ActivityInjector.withViewModel { provideMainViewModel() }
        enableEdgeToEdge()

        setContent {
            val view = LocalView.current

            val theme by rememberAppTheme()
            LaunchedEffect(theme) {
                val windowInsetsController = WindowCompat.getInsetsController(window, view)
                windowInsetsController.isAppearanceLightStatusBars = theme == AppTheme.Light
            }

            App(
                navigator = rememberNavController()
            )
        }
    }
}
