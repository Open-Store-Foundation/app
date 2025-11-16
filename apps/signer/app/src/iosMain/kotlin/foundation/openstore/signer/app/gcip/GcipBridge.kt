package foundation.openstore.signer.app.gcip

import androidx.compose.ui.window.ComposeUIViewController
import foundation.openstore.gcip.platform.GcipDataBundle
import foundation.openstore.gcip.platform.utils.toByteArray
import foundation.openstore.gcip.platform.utils.toNSData
import foundation.openstore.signer.app.screens.gcip.GcipApp
import platform.Foundation.NSData
import platform.UIKit.UIViewController

fun createGcipViewController(
    requestData: NSData,
    caller: String?,
    onError: (NSData?) -> Unit,
    onSuccess: (NSData) -> Unit
): UIViewController {
    return ComposeUIViewController {
        GcipApp(
            provideData = { GcipDataBundle(requestData.toByteArray(), caller) },
            isFullScreen = true,
            onError = { onError(it?.toNSData()) },
            onConfirmed = { onSuccess(it.toNSData()) }
        )
    }
}
