package foundation.openstore.gcip.platform

actual class GcipDataBundle(
    val data: ByteArray?,
    val caller: String?
) {
    actual fun getData(): ByteArray? {
        return data
    }

    actual fun getCaller(): String? {
        return caller
    }
}




