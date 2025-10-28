package com.openstore.app.installer.handlers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import com.openstore.app.installer.ArtifactValidationStatus
import com.openstore.app.installer.InstallationMetaRepo
import com.openstore.app.installer.InstallationReceiver
import com.openstore.app.installer.InstallationRequest
import com.openstore.app.installer.InstallationSessionStorage
import com.openstore.app.installer.utils.ApkFileDestination
import com.openstore.app.installer.utils.readInto
import com.openstore.app.log.L
import java.io.File

// TODO throw to reason
class ApkInstallationManager(
    private val context: Context,
    private val metaRepo: InstallationMetaRepo
) {

    fun install(request: InstallationRequest): Result<Unit> {
        return runCatching {
            val srcFile = ApkFileDestination.getSrcFor(request.address)
            val validationStatus = metaRepo.validateArtifact(srcFile.canonicalPath, request)
            if (validationStatus != ArtifactValidationStatus.Valid) {
                throw IllegalStateException("Artifact is not valid: $validationStatus")
            }

            internalInstall(srcFile, request)
        }.onFailure { error ->
            L.e("Installation failed: ${request.name}", error)
        }
    }

    private fun internalInstall(srcFile: File, request: InstallationRequest) {
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
            setAppPackageName(request.packageName)
            setAppLabel(request.name)
            setSize(srcFile.length())
        }

        val installer = context.packageManager.packageInstaller

        L.d("Creating session for: ${request.name}")
        val sessionId = installer.createSession(params)
        L.d("Session created: ${request.name}")

        val totalSize = srcFile.length().toFloat()
        var totalBytes = 0

        installer.openSession(sessionId).use { session ->
            L.d("Start coping: ${request.name}")
            session.openWrite(ApkFileDestination.APK_NAME, 0, srcFile.length()).use { outputStream ->
                srcFile.readInto { bytes, size ->
                    outputStream.write(bytes, 0, size)

                    totalBytes += size

                    if (totalSize > 0) {
                        session.setStagingProgress(totalBytes / totalSize)
                    }
                }

                session.fsync(outputStream)
            }
            L.d("Coping finished: ${request.name}")

            val intent = Intent(context, InstallationReceiver::class.java).apply {
                action = InstallationReceiver.Companion.INSTALL_ACTION
                putExtra(PackageInstaller.EXTRA_SESSION_ID, sessionId)
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context.applicationContext,
                sessionId,
                intent,
                pendingIntentFlags
            )

            InstallationSessionStorage.startInstallationSession(sessionId, request)

            session.commit(pendingIntent.intentSender)
            L.d("Sent installation request: ${request.name}")
        }

        runCatching {
            if (srcFile.exists()) {
                srcFile.delete()
            }
        }
    }

    fun cleanUp(request: InstallationRequest) {
        val file = ApkFileDestination.getSrcFor(request.address)
        if (file.exists()) {
            file.delete()
        }
    }
}