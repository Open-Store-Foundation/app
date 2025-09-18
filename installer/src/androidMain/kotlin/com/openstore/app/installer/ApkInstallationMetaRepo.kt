package com.openstore.app.installer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.appmattus.crypto.Algorithm
import com.openstore.app.core.common.toFingerHex
import com.openstore.app.core.os.Android
import com.openstore.app.log.L
import com.openstore.app.store.common.store.KeyValueStorage

class InstallationMetaRepoStorage(
    private val store: KeyValueStorage,
) {
    suspend fun getInstalledMeta(packageName: String): InstalledObjectMeta? {
        return store.getStringOrNull(packageName)?.let {
            InstalledObjectMeta(it, packageName)
        }
    }

    suspend fun addInstalledAddress(packageName: String, address: String) {
        store.putString(packageName, address)
    }

    suspend fun deleteInstalledAddress(packageName: String) {
        store.remove(packageName)
    }
}

class ApkInstallationMetaRepo(
    private val storage: InstallationMetaRepoStorage,
) : MutableInstallationMetaRepo {

    companion object {
        private const val APP_METADATA_ADDRESS = "com.openstore.app.metadata.address"
    }

    private val context: Context = Android.context
    private val packageManager = context.packageManager

    override suspend fun canRequestPackageInstalls(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    override suspend fun addInstalledAddress(packageName: String, address: String) {
        storage.addInstalledAddress(packageName, address)
    }

    override suspend fun deleteInstalledAddress(packageName: String) {
        storage.deleteInstalledAddress(packageName)
    }

    override suspend fun getInstalledObjectsMeta(packageName: String): InstalledObjectMeta? {
        val info = getPackageInfo(packageName)

        if (info == null) {
            storage.deleteInstalledAddress(packageName)
            return null
        }

        return getInstalledMetaBy(info)
    }

    override suspend fun isAppOutdated(address: String, version: Long, packageName: String?): Boolean {
        val packageName = packageName ?: context.packageName
        val status = getInstallationStatus(address, packageName, version)
        return status == InstallationStatus.InstalledOutdated
    }

    override suspend fun getInstallationStatus(
        address: String,
        packageName: String,
        version: Long,
    ): InstallationStatus {
        val info = getPackageInfo(packageName)

        if (info == null) {
            storage.deleteInstalledAddress(packageName)
            return InstallationStatus.Uninstalled
        }

        val meta = getInstalledMetaBy(info)
        if (meta != null && address != meta.address) {
            return InstallationStatus.InstalledAnother
        }

        val apkVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }

        if (apkVersion > version) {
            return InstallationStatus.InstalledOutdated
        }

        return InstallationStatus.InstalledActual
    }

    override suspend fun getInstalledObjectsMetas(): List<InstalledObjectMeta> {
        return getAppsInstalledBy(context.packageName)
    }

    // TODO for new versions and for new apps
    fun checkSignatures(packageName: String, sha256: Set<String>): Boolean {
        val info = getPackageInfo(packageName)
            ?: return false

        val signatures = getSignatures(info)

        // check versions
        // parse apk / check that apk is apk / check hash / check signatures

        return checkAppSignatures(signatures, sha256)
    }

    private fun checkAppSignatures(signatures: List<Signature>, sha256: Set<String>): Boolean {
        try {
            if (signatures.isEmpty()) {
                return false
            }

            val hasher = Algorithm.SHA_256.createDigest()
            val signFingers = signatures.map {
                hasher.digest(it.toByteArray())
                    .toFingerHex()
            }

            return signFingers.all {
                sha256.contains(it)
            }
        } catch (e: Throwable) {
            L.e("Error during hash calculation", e)
        }

        return false
    }

    private suspend fun getAppsInstalledBy(installerPackage: String?): List<InstalledObjectMeta> {
        val installedApps = mutableListOf<InstalledObjectMeta>()

        val packages = getPackagesInfo()
        for (packageInfo in packages) {
            try {
                val installerPackageName: String? = getInstaller(packageInfo.packageName)

                if (installerPackage == installerPackageName) {
                    L.d("Found package for Open Store installer: ${packageInfo.packageName}")
                    val meta = getInstalledMetaBy(packageInfo)
                    if (meta != null) {
                        installedApps.add(meta)
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                L.w("Package name not found during check: ${packageInfo.packageName}", e)
            } catch (e: SecurityException) {
                L.w("SecurityException checking installer for ${packageInfo.packageName}", e)
            } catch (e: Exception) {
                L.e("Error checking installer for ${packageInfo.packageName}", e)
            }
        }

        return installedApps
    }

    private suspend fun getInstalledMetaBy(packageInfo: PackageInfo): InstalledObjectMeta? {
        val address = packageInfo.metaInfoAddress
        if (address != null) {
            return InstalledObjectMeta(
                packageName = packageInfo.packageName,
                address = address
            )
        }

        return storage.getInstalledMeta(packageInfo.packageName)
    }

    private val PackageInfo.metaInfoAddress: String?
        get() {
            return applicationInfo?.metaData?.getString(APP_METADATA_ADDRESS)
        }

    private fun getSignatures(info: PackageInfo): List<Signature> {
        val signatures = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signers = info.signingInfo
                    ?: return emptyList()

                if (signers.hasMultipleSigners()) {
                    signers.apkContentsSigners
                } else {
                    signers.signingCertificateHistory
                }
            } else {
                @Suppress("DEPRECATION")
                info.signatures
                    ?: return emptyList()
            }
        } catch (e: Exception) {
            L.e("Error retrieving signatures", e)
            return emptyList()
        }

        return signatures.toList()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getPackagesInfo(): List<PackageInfo> {
        val infos = try {
            packageManager.getInstalledPackages(defaultFlags())
        } catch (e: Throwable) {
            L.e("Can't get packages info", e)
            return emptyList()
        }

        return infos
    }

    private fun getInstaller(packageName: String): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(packageName)
                .installingPackageName
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(packageName)
        }
    }

    private fun getPackageInfo(packageName: String): PackageInfo? {
        val info = try {
            packageManager.getPackageInfo(packageName, defaultFlags())
        } catch (e: Throwable) {
            L.e("Can't get package info", e)
            return null
        }

        return info
    }

    private fun defaultFlags(): Int {
        val certFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            PackageManager.GET_SIGNATURES
        }

        return certFlag or PackageManager.GET_META_DATA
    }
}
