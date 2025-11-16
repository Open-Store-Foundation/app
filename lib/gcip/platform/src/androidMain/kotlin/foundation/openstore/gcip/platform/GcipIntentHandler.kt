package foundation.openstore.gcip.platform

import android.app.Activity
import android.content.Intent
import foundation.openstore.gcip.core.GcipConfig

class GcipIntentHandler {

    fun createRequestIntent(result: ByteArray, caller: String? = null): Intent {
        return Intent.createChooser(
            Intent(GcipConfig.ActionRequest).apply {
                caller?.let {
                    setPackage(it)
                }

                putExtra(
                    GcipConfig.Data,
                    result
                )
            },
            null,
        )
    }

    fun createIntent(result: ByteArray): Intent {
        return Intent().apply {
            putExtra(
                GcipConfig.Data,
                result
            )
        }
    }

    fun handleIntent(activity: Activity): GcipDataBundle {
        return GcipDataBundle(activity.intent, activity.callingPackage)
    }
}