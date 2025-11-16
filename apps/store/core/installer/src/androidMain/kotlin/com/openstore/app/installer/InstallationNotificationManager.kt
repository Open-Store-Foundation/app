package com.openstore.app.installer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.toColorInt
import foundation.openstore.appapps.store.core.installer.R

class InstallationNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val packageManager: PackageManager,
) {

    companion object {
        private const val NOTIFICATION_MAIN_GROUP_ID = "com.openstore.app.installer.main"

        private const val NOTIFICATION_CHANNEL_ID = "com.openstore.app.installer"
        private const val NOTIFICATION_CHANNEL_NAME = "Open Store"
        private const val NOTIFICATION_ID = 1
    }

    fun notificationId(): Int {
        return NOTIFICATION_ID
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun installationNotification(stage: QueueStage): Notification {
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (stage) {
            is QueueStage.Progress -> "Downloading ${stage.info.name}"
            is QueueStage.Starting -> "Starting download..."
            is QueueStage.WaitingInstallation -> "App installation..."
        }

        val progress = when (stage) {
            is QueueStage.Progress -> stage.progress
            is QueueStage.Starting -> 0
            is QueueStage.WaitingInstallation -> 0
        }

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setGroup(NOTIFICATION_MAIN_GROUP_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.animated_download)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setColorized(true)
            .setColor("#A2C9FF".toColorInt())

        if (progress > 0) {
            builder.setProgress(100, progress, false)
        } else {
            builder.setProgress(0, 0, true)
        }

        return builder.build()
    }

    fun updateNotificationProgress(stage: QueueStage) {
        val notification = installationNotification(stage)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

class InstallationRequestNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val packageManager: PackageManager,
) {

    companion object {
        private const val NOTIFICATION_RESULT_GROUP_ID = "com.openstore.app.installer.result"
        private const val NOTIFICATION_CHANNEL_ID = "com.openstore.app.installer"
        private const val NOTIFICATION_CHANNEL_NAME = "Open Store"
    }

    private var notificationIdPtr = 1001

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDownloadErrorNotification(request: InstallationRequest) {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setGroup(NOTIFICATION_RESULT_GROUP_ID)
            .setContentTitle("${request.name} installation is failed!")
            .setSmallIcon(R.drawable.ic_error)
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(++notificationIdPtr, notification)
    }

    fun showApkInstalledNotification(request: InstallationRequest) {
        val id = ++notificationIdPtr

        val intent = packageManager.getLaunchIntentForPackage(request.packageName)

        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setGroup(NOTIFICATION_RESULT_GROUP_ID)
            .setContentTitle("${request.name} is installed!")
            .setSmallIcon(R.drawable.ic_check)
            .setContentIntent(pendingIntent)
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
