package com.openstore.app.installer.utils

import java.io.File
import java.io.FileInputStream

inline fun File.readInto(crossinline applier: (ByteArray, Int) -> Unit) {
    val buffer = ByteArray(4 * 1024)

//    this.readChannel().readBuffer().use { channel ->
//        var bytesRead: Int
//        while (channel.readAvailable(buffer).also { bytesRead = it } != -1) {
//            applier(buffer, bytesRead)
//        }
//    }

    FileInputStream(this).use { srcStream ->
        var bytesRead: Int

        while (srcStream.read(buffer).also { bytesRead = it } >= 0) {
            applier(buffer, bytesRead)
        }
    }
}
