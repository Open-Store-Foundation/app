package com.openstore.app.installer.handlers

import com.openstore.app.installer.InstallationRequest
import com.openstore.app.installer.QueueStage
import com.openstore.app.installer.utils.ApkFileDestination
import com.openstore.app.log.L
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.util.cio.use
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.writeAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer

class FileDownloadManager(
    private val observeScope: CoroutineScope,
    private val client: HttpClient,
    private val delegate: Delegate,
) {

    interface Delegate {
        suspend fun onStageUpdated(stage: QueueStage.Progress)
        fun isCancelled(): Boolean
    }

    private val buffer = ByteBuffer.allocate(32 * 1024)

    suspend fun downloadFile(request: InstallationRequest): Result<File> {
        return runCatching { innerDownloadFile(request) }
            .onFailure { error ->
                L.e("Downloading failed: ${request.name}", error)
            }
    }

    private suspend fun innerDownloadFile(request: InstallationRequest): File {
        buffer.clear()

        val destinationFile = prepareFile(request)
            ?: throw IllegalStateException("Can't create file for: ${request.name}")

        var observerJob: Job? = null

        try {
            val result = client.get(request.artifactUrls.random())

            if (!result.status.isSuccess()) {
                throw IllegalStateException("Http status error: ${result.status}")
            }

            val fileSize = when {
                request.size > 0 -> request.size
                else -> {
                    val length = result.headers[HttpHeaders.ContentLength]
                    length?.toLongOrNull() ?: 0L
                }
            }

            val channel = result.bodyAsChannel()
            var totalRead = 0

            if (fileSize > 0) {
                observerJob = observeScope.launch {
                    while (isActive) {
                        delay(3_000)

                        if (!isActive || delegate.isCancelled()) {
                            break
                        }

                        val progress = (totalRead * 100 / fileSize).toInt()
                        delegate.onStageUpdated(
                            QueueStage.Progress(
                                request,
                                progress
                            )
                        )
                    }
                }
            } else {
                delegate.onStageUpdated(
                    QueueStage.Progress(
                        request,
                        progress = -1,
                    )
                )
            }

            var bytesRead = 0
            destinationFile.writeChannel().use {
                while (channel.readAvailable(buffer).also { bytesRead = it } != -1) {
                    if (!observeScope.isActive || delegate.isCancelled()) {
                        throw kotlin.coroutines.cancellation.CancellationException()
                    }

                    buffer.flip()
                    writeAvailable(buffer)
                    buffer.clear()

                    totalRead += bytesRead
                }
            }
        } catch (e: Throwable) {
            runCatching {
                destinationFile.delete()
            }

            throw e
        } finally {
            runCatching {
                observerJob?.cancelAndJoin()
            }
        }

        return destinationFile
    }

    private fun prepareFile(request: InstallationRequest): File? {
        val destinationFile = ApkFileDestination.getSrcFor(request.address)

        val parent = destinationFile.parentFile
        if (parent != null && !parent.exists()) {
            parent.mkdirs()
        }

        if (destinationFile.exists()) {
            destinationFile.delete()
        }

        if (!destinationFile.createNewFile()) {
            return null
        }

        return destinationFile
    }
}