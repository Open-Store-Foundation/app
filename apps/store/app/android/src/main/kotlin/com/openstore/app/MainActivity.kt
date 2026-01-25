package com.openstore.app

import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.openstore.app.core.config.BuildConfig
import com.openstore.app.di.ActivityInjector
import com.openstore.app.installer.handlers.ApkUninstallManager
import com.openstore.app.installer.InstallationRouter
import com.openstore.app.mvi.OnceOnly
import com.openstore.app.screens.update.StoreUpdateDialog
import com.openstore.app.ui.AppTheme
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.rememberAppTheme
import com.openstore.app.ui.setAppTheme
import com.openstore.app.ui.systemAppTheme
import foundation.openstore.kitten.android.withViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val view = LocalView.current
            val viewModel = ActivityInjector.withViewModel { provideMainViewModel() }

            val scope = rememberCoroutineScope()
            val installationManager = remember { ApkUninstallManager(this) }
            val installationRouter = remember { InstallationRouter(this) }
            val contract = remember { ActivityResultContracts.StartActivityForResult() }
            val navigator: NavHostController = rememberNavController()

            val settingsResult = rememberLauncherForActivityResult(
                contract = contract,
                onResult = {
                    if (installationRouter.canRequestPackageInstalls()) {
                        Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Install permission denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )

            val notificationState = rememberNotificationState {
                viewModel.sendAction(MainAction.Launch)
            }

            val systemTheme = systemAppTheme()
            OnceOnly {
                setAppTheme(viewModel.appTheme() ?: systemTheme)

                scope.launch {
                    if (notificationState != null && notificationState.shouldRequest) {
                        notificationState.launchPermissionRequest()
                    } else {
                        viewModel.sendAction(MainAction.Launch)
                    }
                }
            }

            val theme by rememberAppTheme()
            LaunchedEffect(theme) {
                val windowInsetsController = WindowCompat.getInsetsController(window, view)
                windowInsetsController.isAppearanceLightStatusBars = theme == AppTheme.Light
            }

            OnCreate {
                viewModel.events.onEach { event ->
                    when (event) {
                        is MainEvents.RequestInstallPermission -> {
                           if (!installationRouter.startSettingsActivity(settingsResult)) {
                               Toast.makeText(this@MainActivity, "Install permission denied!", Toast.LENGTH_SHORT)
                                   .show()
                           }
                        }
                        is MainEvents.StartInstallerService -> {
                            if (!installationRouter.startInstallerService()) {
                                Toast.makeText(this@MainActivity, "Can't install application!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        is MainEvents.OpenObject -> {
                            if (!installationRouter.startApplication(event.obj.packageName)) {
                                Toast.makeText(this@MainActivity, "Can't start application!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        is MainEvents.DeleteObject -> {
                            if (!installationManager.uninstall(event.obj.packageName)) {
                                Toast.makeText(this@MainActivity, "Can't uninstall application!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }.launchIn(scope)
            }

            App(navigator = navigator)
        }
    }
}
