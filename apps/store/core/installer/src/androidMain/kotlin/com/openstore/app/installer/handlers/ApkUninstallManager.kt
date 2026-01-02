package com.openstore.app.installer.handlers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import com.openstore.app.installer.InstallationReceiver
import com.openstore.app.log.L

class ApkUninstallManager(
    private val context: Context
) {

    fun uninstall(packageName: String): Boolean {
        val packageInstaller = context.packageManager.packageInstaller

        val result = runCatching {
            val intent = Intent(context, InstallationReceiver::class.java).apply {
                action = InstallationReceiver.Companion.UNINSTALL_ACTION
                putExtra(PackageInstaller.EXTRA_PACKAGE_NAME, packageName)
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                packageName.hashCode(),
                intent,
                pendingIntentFlags
            )

            packageInstaller.uninstall(packageName, pendingIntent.intentSender)
        }.onFailure { error ->
            L.e("Delete failed: $packageName", error)
        }

        return result.isSuccess
    }
}