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
import com.openstore.app.installer.utils.ApkFileDestination
import com.openstore.app.log.L
import com.openstore.app.store.common.store.KeyValueStorage
import java.io.File
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

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
    private val context: Context,
    private val storage: InstallationMetaRepoStorage,
) : MutableInstallationMetaRepo {

    companion object {
        private const val APP_METADATA_ADDRESS = "com.openstore.app.metadata.address"
    }

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

    override fun validateArtifact(artifactFile: String, request: InstallationRequest): ArtifactValidationStatus {
        val file = File(artifactFile)
        L.d("Validating artifact: file=$artifactFile, package=${request.packageName}, version=${request.version}, versionName=${request.versionName}, checksum=${request.checksum}, fingerprints=${request.contractFingerprints.size}")
        if (!file.exists()) {
            L.e("Artifact file not found: $artifactFile")
            return ArtifactValidationStatus.FileNotFound
        }

        // TODO
        if (!ApkFileDestination.checkHash(file, request.checksum)) {
            L.e("Invalid checksum for artifact: expected=${request.checksum}")
            return ArtifactValidationStatus.InvalidChecksum
        }

        val contractFingerprints = request.contractFingerprints.toSet()
        L.d("Prepared contract fingerprints: count=${contractFingerprints.size}")
        val newInfo = getPackageInfo(file)
            ?: run {
                L.e("Package info for artifact not found")
                return ArtifactValidationStatus.PackageInfoNotFound
            }

        if (newInfo.packageName != request.packageName) {
            L.e("Package name mismatch: expected=${request.packageName}, actual=${newInfo.packageName}")
            return ArtifactValidationStatus.PackageNameMismatch
        }

        if (newInfo.versionCodeCompat != request.version) {
            L.e("Version code mismatch: expected=${request.version}, actual=${newInfo.versionCodeCompat}")
            return ArtifactValidationStatus.VersionCodeMismatch
        }

        if (newInfo.versionName != null && request.versionName != null) {
            if (newInfo.versionName != request.versionName) {
                L.e("Version name mismatch: expected=${request.versionName}, actual=${newInfo.versionName}")
                return ArtifactValidationStatus.VersionNameMismatch
            }
        }

        val isArtifactFingerValid = checkArtifactFingerprints(newInfo, contractFingerprints)
        if (!isArtifactFingerValid) {
            L.e("Artifact fingerprint mismatch for ${newInfo.packageName}")
            return ArtifactValidationStatus.FingerprintMismatch
        }
        L.d("Artifact fingerprints validated for ${newInfo.packageName}")

        val oldInfo = getPackageInfo(request.packageName)
        if (oldInfo == null) {
            L.e("No installed package found for ${request.packageName}; skipping update signature check")
            return ArtifactValidationStatus.Valid
        }

        val isUpdateValid = checkUpdate(oldInfo, contractFingerprints)
        if (!isUpdateValid) {
            L.e("Update signature mismatch for installed package ${oldInfo.packageName}")
            return ArtifactValidationStatus.UpdateSignatureMismatch
        }

        L.d("Artifact validation succeeded for ${newInfo.packageName}")
        return ArtifactValidationStatus.Valid
    }

    private fun checkArtifactFingerprints(
        packageInfo: PackageInfo,
        fingerprints: Set<String>,
    ): Boolean {
        val fileFingerprints = getSignatureFingerprints(packageInfo)

        if (fileFingerprints.isEmpty()) {
            return false
        }

        if (!fingerprints.containsAll(fileFingerprints)) {
            return false
        }

        return true
    }

    private fun checkUpdate(packageInfo: PackageInfo, contractFingerprints: Set<String>): Boolean {
        val existingSignatures = getSignatureFingerprints(packageInfo)
        return checkAppSignatures(existingSignatures, contractFingerprints)
    }

    private fun checkAppSignatures(signatures: Set<String>, contractFingerprints: Set<String>): Boolean {
        try {
            if (signatures.isEmpty()) {
                return false
            }

            return signatures.containsAll(contractFingerprints)
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

    private val PackageInfo.versionCodeCompat: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                longVersionCode
            } else {
                versionCode.toLong()
            }
        }

    private fun getSignatureFingerprints(packageInfo: PackageInfo): Set<String> {
        val signatures = getSignatures(packageInfo)
        val hasher = Algorithm.SHA_256.createDigest()

        val certs = signatures.map {
            hasher.digest(it.toByteArray())
                .toFingerHex()
        }

        return certs.toSet()
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

    private fun getX509Certs(signatures: List<Signature>): List<X509Certificate> {
        val certFactory = CertificateFactory.getInstance("X.509")

        return try {
            signatures.map { signature ->
                signature.toByteArray().inputStream().use {
                    certFactory.generateCertificate(it) as X509Certificate
                }
            }
        } catch (e: Throwable) {
            L.e("Can't decode certificate for certificate", e)
            return emptyList()
        }
    }


    private fun getPackageInfo(file: File): PackageInfo? {
        val info = try {
            packageManager.getPackageArchiveInfo(file.canonicalPath, defaultFlags())
        } catch (e: Throwable) {
            L.e("Can't get package info", e)
            return null
        }

        return info
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
