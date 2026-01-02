@file:Suppress("ConstPropertyName")

package foundation.openstore.gcip.core

object GcipConfig {
    val ActualVersion = 1.toUByte()
    val MinVersion = 1.toUByte()

    const val MAX_NAME_LENGTH = 15
    const val MAX_SCHEME_LENGTH = 10

    const val ActionAnnounce = "gcip.action.announce"
    const val ActionRequest = "gcip.action.request"
    const val Data = "gcip.data.block"
}
