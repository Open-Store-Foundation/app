package com.openstore.app.installer

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.widget.Toast
import com.openstore.app.installer.core.ScopedBroadcastReceiver
import com.openstore.app.log.L

class InstallationReceiver : ScopedBroadcastReceiver() {
    
    companion object {
        const val INSTALL_ACTION = "com.openstore.app.installer.INSTALL_COMMIT"
        const val UNINSTALL_ACTION = "com.openstore.app.installer.UNINSTALL_ACTION"
    }

    override fun onHandleReceive(context: Context, intent: Intent) {
        val controller = InstallerInjector.injectWith(scope) { provideServiceController() }

        when (intent.action) {
            INSTALL_ACTION -> handleInstallAction(context, intent, controller)
            UNINSTALL_ACTION -> handleUninstallAction(context, intent, controller)
            else -> return
        }
    }

    private fun handleInstallAction(context: Context, intent: Intent, controller: ServiceController) {
        val sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1)
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
        val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)

        L.d("Received install result for session $sessionId: status=$status, message=$message")

        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                handlePendingUserAction(context, intent)
            }
            PackageInstaller.STATUS_SUCCESS -> {
                L.i("Session $sessionId: INSTALL SUCCEEDED!")
                if (sessionId != -1) {
                    InstallationSessionStorage.finishInstallationSession(sessionId)?.let {
                        L.i("Session $sessionId finished!")
                        controller.onInstallSuccess(it)
                    }
                } else {
                    L.w("Session $sessionId not found!")
                }
            }
            else -> {
                showError(context, status)
                L.e("Session $sessionId: INSTALL FAILED! Status: $status, Message: $message")
                if (sessionId != -1) {
                    InstallationSessionStorage.finishInstallationSession(sessionId)?.let {
                        controller.onInstallError(it)
                    }
                }
            }
        }
    }

    fun handleUninstallAction(context: Context, intent: Intent, controller: ServiceController) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)
        val packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)

        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                handlePendingUserAction(context, intent)
            }
            PackageInstaller.STATUS_SUCCESS -> {
                L.d("Uninstall successful $packageName")

                if (packageName != null) {
                    controller.onDeleteSuccessful(packageName)
                }
            }
            else -> {
                showError(context, status)
                L.d("Unrecognized status received $packageName: $status")
            }
        }
    }

    private fun showError(context: Context, status: Int) {
        when (status) {
            PackageInstaller.STATUS_FAILURE_ABORTED -> {
                Toast.makeText(context, "Error: Installation aborted!", Toast.LENGTH_SHORT)
                    .show()
            }
            PackageInstaller.STATUS_FAILURE_BLOCKED -> {
                Toast.makeText(context, "Error: Installation blocked by device!", Toast.LENGTH_SHORT)
                    .show()
            }
            PackageInstaller.STATUS_FAILURE_CONFLICT -> {
                Toast.makeText(context, "Error: Conflict with already installed app!", Toast.LENGTH_SHORT)
                    .show()
            }
            PackageInstaller.STATUS_FAILURE_INVALID -> {
                Toast.makeText(context, "Error: Installing APK is invalid!", Toast.LENGTH_SHORT)
                    .show()
            }
            PackageInstaller.STATUS_FAILURE_STORAGE -> {
                Toast.makeText(context, "Error: Insufficient storage!", Toast.LENGTH_SHORT)
                    .show()
            }
            PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> {
                Toast.makeText(context, "Error: App incompatible with device!", Toast.LENGTH_SHORT)
                    .show()
            }
            PackageInstaller.STATUS_FAILURE_TIMEOUT -> {
                Toast.makeText(context, "Error: Installation timed out!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handlePendingUserAction(context: Context, intent: Intent) {
        L.i("Session requires user action.")

        // This is returned when the user is presented with the confirmation dialog.
        // You can get the intent to launch the dialog from the broadcast.
        val confirmationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_INTENT)
        }

        if (confirmationIntent != null) {
            // It's recommended to start this with FLAG_ACTIVITY_NEW_TASK
            confirmationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(confirmationIntent)
        }
    }
}
