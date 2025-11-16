package foundation.openstore.gcip.platform

import android.content.Intent
import foundation.openstore.gcip.core.GcipConfig

actual class GcipDataBundle(
    private val intent: Intent,
    private val caller: String?,
) {
    actual fun getData(): ByteArray? {
        return intent.getByteArrayExtra(GcipConfig.Data)
    }

    actual fun getCaller(): String? {
        return caller
    }
}
