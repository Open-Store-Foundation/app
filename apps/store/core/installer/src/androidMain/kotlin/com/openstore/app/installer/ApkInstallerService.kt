package com.openstore.app.installer

import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.openstore.app.log.L
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import foundation.openstore.kitten.android.withLifecycle

class ApkInstallerService : LifecycleService() {

    companion object {
        const val ACTION_START_QUEUE = "com.openstore.app.action.START_DOWNLOAD"
        const val ACTION_STOP_QUEUE = "com.openstore.app.action.STOP_DOWNLOAD"
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var notificationManager: InstallationNotificationManager
    private lateinit var notificationResultManager: InstallationRequestNotificationManager
    private lateinit var controller: ServiceController

    override fun onCreate() {
        super.onCreate()

        controller = InstallerInjector.withLifecycle { provideServiceController() }

        notificationManager = InstallationNotificationManager(
            context = this,
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager,
            packageManager = packageManager
        )

        notificationResultManager = InstallationRequestNotificationManager(
            context = this,
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager,
            packageManager = packageManager
        )

        notificationManager.createNotificationChannel()
        notificationResultManager.createNotificationChannel()

        controller.events.onEach { event ->
            when (event) {
                is ServiceEvents.DownloadPrepare -> notificationManager.updateNotificationProgress(QueueStage.Starting)
                is ServiceEvents.WaitingInstallation -> notificationManager.updateNotificationProgress(QueueStage.WaitingInstallation)
                is ServiceEvents.DownloadProgress -> notificationManager.updateNotificationProgress(event.progress)
                is ServiceEvents.InstallationError -> notificationResultManager.showDownloadErrorNotification(event.info)
                is ServiceEvents.InstallationFinished -> notificationResultManager.showApkInstalledNotification(event.request)
                is ServiceEvents.Finished -> stopService()
            }
        }.launchIn(serviceScope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (controller.isStarted()) {
            return START_NOT_STICKY
        }

        when (intent?.action) {
            ACTION_START_QUEUE -> {
                val isEmpty = controller.isEmpty()

                if (isEmpty) {
                    stopSelf()
                    return START_NOT_STICKY
                }

                controller.runQueueLoop()

                try {
                    ServiceCompat.startForeground(
                        this,
                        notificationManager.notificationId(),
                        notificationManager.installationNotification(QueueStage.Starting),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                        } else {
                            0
                        }
                    )
                } catch (e: Throwable) {
                    L.e("startForeground", e)
                }
            }

            ACTION_STOP_QUEUE -> {
                // TODO implement queue clean
                stopService()
            }

            else -> Unit
        }

        return START_NOT_STICKY
    }

    private fun stopService() {
        serviceJob.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}
