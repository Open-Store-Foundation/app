package foundation.openstore.gcip.platform.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build

class SignatureProvider(
    private val context: Context,
) {

    fun getSignatures(packageName: String): List<Signature> {
        val packageManager = context.packageManager
        val packageInfo = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw SecurityException("Package not found: $packageName", e)
        }

        val signatures = parseSignatures(packageInfo)
        if (signatures.isEmpty()) {
            throw SecurityException("No signatures found for package: $packageName")
        }

        return signatures
    }

    private fun parseSignatures(info: PackageInfo): List<Signature> {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signers = info.signingInfo
                    ?: return emptyList()

                if (signers.hasMultipleSigners()) {
                    signers.apkContentsSigners.toList()
                } else {
                    signers.signingCertificateHistory.toList()
                }
            } else {
                @Suppress("DEPRECATION")
                info.signatures?.toList() ?: emptyList()
            }
        } catch (e: Exception) {
            throw SecurityException("Error retrieving signatures", e)
        }
    }
}