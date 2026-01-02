package foundation.openstore.gcip.platform

actual class GcipDataBundle {
    actual fun getData(): ByteArray? {
        return byteArrayOf()
    }

    actual fun getCaller(): String? {
        return null
    }
}
