package foundation.openstore.signer.app

import platform.Foundation.NSBundle

internal object IosBundleConfig {

    private const val EXTENSION_PREFIX = ".extension"

    fun normalizeBundleId(bundleId: String): String {
        var normalized = bundleId
        if (normalized.endsWith(EXTENSION_PREFIX)) {
            normalized = normalized.removeSuffix(EXTENSION_PREFIX)
        }

        return normalized
    }

    fun getCurrentBundleId(): String {
        return NSBundle.mainBundle.bundleIdentifier
            ?: throw IllegalStateException("App Group Id not found")
    }

    fun getNormalizedCurrentBundleId(): String {
        return normalizeBundleId(getCurrentBundleId())
    }

    fun getAppGroupId(bundleId: String): String {
        val normalized = normalizeBundleId(bundleId)

        return "group.$normalized"
    }
}
