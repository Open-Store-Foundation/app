package com.openstore.app.installer.utils

import com.appmattus.crypto.Algorithm
import com.openstore.app.core.common.toLower0xHex
import com.openstore.app.core.os.Android
import com.openstore.app.log.L
import io.ktor.utils.io.InternalAPI
import java.io.File

object ApkFileDestination {

    const val APK_NAME = "base.apk"

    // TODO change path??
    fun getSrcFor(address: String): File {
        return File(Android.privateFiles(), "/${address}/$APK_NAME")
    }

    fun checkHash(src: File, checksum: String): Boolean {
        val hash = getFileHash(src)
            ?: return false

        val isEq = hash.equals(checksum, ignoreCase = true)

        if (!isEq) {
            L.e("Hash mismatch: ${src.absolutePath} - $hash != $checksum")
        }

        return isEq
    }

    @OptIn(InternalAPI::class)
    private fun getFileHash(src: File): String? {
        val blake = Algorithm.Blake3().createDigest()

        val result = runCatching {
            src.readInto { bytes, size ->
                blake.update(bytes, 0, size)
            }
        }

        if (result.isFailure) {
            L.e("Failed to hash file: $src", result.exceptionOrNull())
            return null
        }

        return blake.digest().toLower0xHex()
    }
}

