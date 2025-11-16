package com.openstore.app.installer

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.openstore.app.log.L
import androidx.core.net.toUri

class InstallationRouter(
    private val activity: Activity,
) {

    private val packageManager = activity.packageManager

    fun canRequestPackageInstalls(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun startInstallerService(): Boolean {
        try {
            val intent = Intent(activity, ApkInstallerService::class.java).apply {
                action = ApkInstallerService.ACTION_START_QUEUE
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(intent)
            } else {
                activity.startService(intent)
            }
            return true
        } catch (e: Throwable) {
            L.e("startInstallerService", e)
            return false
        }
    }

    fun startSettingsActivity(result: ActivityResultLauncher<Intent>): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = "package:${activity.packageName}".toUri()
                }

                result.launch(intent)
            }

            return true
        } catch (e: Throwable) {
            L.e("startSettingsActivity", e)
            return false
        }
    }

    fun startApplication(packageName: String): Boolean {
        try {
            val intent = activity.packageManager.getLaunchIntentForPackage(packageName)

            if (intent == null) {
                return false
            }
            activity.startActivity(intent)
            return true
        } catch (e: Throwable) {
            L.e("startObject", e)
            return false
        }
    }
}