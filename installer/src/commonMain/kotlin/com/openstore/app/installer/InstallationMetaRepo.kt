package com.openstore.app.installer

enum class InstallationStatus {
    Uninstalled,
    InstalledAnother,
    InstalledActual,
    InstalledOutdated,
    Fetching,
    InQueue,
    Starting,
    Downloading,
    Installing;

    val isNone: Boolean get() = this == Uninstalled
            || this == InstalledAnother
            || this == InstalledActual
            || this == InstalledOutdated

    val isInstalling: Boolean get() =  this == Installing
            || this == Starting
            || this == Downloading
            || this == Fetching
            || this == InQueue
            || this == Downloading

    val isIntermediate: Boolean get() = isInstalling && this != Downloading

    val isDangerous: Boolean get() = this == InstalledAnother
}

data class InstalledObjectMeta(
    val address: String,
    val packageName: String,
)

interface InstallationMetaRepo {
    suspend fun canRequestPackageInstalls(): Boolean
    suspend fun getInstallationStatus(
        address: String,
        packageName: String,
        version: Long
    ): InstallationStatus

    suspend fun getInstalledObjectsMetas(): List<InstalledObjectMeta>
    suspend fun getInstalledObjectsMeta(packageName: String): InstalledObjectMeta?
}

interface MutableInstallationMetaRepo : InstallationMetaRepo {
    suspend fun isAppOutdated(address: String, version: Long, packageName: String? = null): Boolean
    suspend fun addInstalledAddress(packageName: String, address: String)
    suspend fun deleteInstalledAddress(packageName: String)
}
