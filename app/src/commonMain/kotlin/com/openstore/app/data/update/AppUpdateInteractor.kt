package com.openstore.app.data.update

import com.openstore.app.data.sources.AppChainService
import com.openstore.app.installer.MutableInstallationMetaRepo

interface AppUpdateInteractor {
    suspend fun hasNewVersion(appAddress: String, packageName: String): Boolean
}

class AppUpdateInteractorDefault(
    private val appChainService: AppChainService,
    private val installationRepo: MutableInstallationMetaRepo,
) : AppUpdateInteractor {

    override suspend fun hasNewVersion(appAddress: String, packageName: String): Boolean {
        val lastVersion = appChainService.getLastVersionInStore(appAddress)
        if (lastVersion == null) {
            return false
        }

        val isOutdated = installationRepo.isAppOutdated(appAddress, lastVersion, packageName)
        return isOutdated
    }
}
