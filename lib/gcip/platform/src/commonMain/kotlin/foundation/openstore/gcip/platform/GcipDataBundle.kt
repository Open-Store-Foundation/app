package foundation.openstore.gcip.platform

expect class GcipDataBundle {
    fun getData(): ByteArray?
    fun getCaller(): String?
}