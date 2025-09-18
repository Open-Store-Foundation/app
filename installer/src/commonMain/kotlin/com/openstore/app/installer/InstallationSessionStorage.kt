package com.openstore.app.installer

import com.openstore.app.log.L
import io.ktor.util.collections.ConcurrentMap

internal object InstallationSessionStorage {

    private val installationSessions = ConcurrentMap<Int, InstallationRequest>()

    internal fun startInstallationSession(id: Int, request: InstallationRequest) {
        installationSessions[id] = request
    }

    internal fun finishInstallationSession(id: Int): InstallationRequest? {
        val request = installationSessions.remove(id)

        if (request == null) {
            L.w("Session $id finished, but request not found")
        }

        return request
    }
}