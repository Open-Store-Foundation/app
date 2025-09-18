package com.openstore.app.core.os

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

@SuppressLint("StaticFieldLeak")
object Android {

    @Volatile
    private lateinit var appContext: Context
    val context get() = appContext

    internal fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun providerId(): String {
        return "${context.packageName}.provider"
    }

    fun privateFiles(): File {
        return File(context.cacheDir, "/private/file_paths")
    }

    fun sharedFiles(): File {
        return File(context.cacheDir, "/shared/shared_paths")
    }

    fun sharedUri(file: File): Uri {
        return FileProvider.getUriForFile(context, providerId(), file)
    }

    fun pubKey(context: Context): ByteArray {
        return context.assets.open("pub")
            .use {
                ByteArray(it.available())
                    .apply { it.read(this) }
            }
    }
}
