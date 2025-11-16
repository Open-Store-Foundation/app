package foundation.openstore.gcip.platform

import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipErrorContext
import foundation.openstore.gcip.core.util.GcipResult
import platform.Foundation.NSExtensionItem
import platform.Foundation.NSItemProvider
import platform.Foundation.NSData
import platform.Foundation.NSExtensionContext
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("GcipNsExtensionHandler", exact = true)
class GcipNsExtensionHandler {

    fun handleExtensionContext(
        context: NSExtensionContext,
        onSuccess: (NSData) -> Unit,
        onError: (GcipErrorContext) -> Unit
    ) {
        handleExtensionItems(
            extensionItems = context.inputItems,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun handleExtensionItems(
        extensionItems: List<*>,
        onSuccess: (NSData) -> Unit,
        onError: (GcipErrorContext) -> Unit
    ) {
        for (item in extensionItems) {
            if (item !is NSExtensionItem) {
                continue
            }

            val attachments = item.attachments
                ?: continue

            for (provider in attachments) {
                if (provider !is NSItemProvider) {
                    continue
                }

                if (provider.hasItemConformingToTypeIdentifier(GcipConfig.Data)) {
                    provider.loadItemForTypeIdentifier(GcipConfig.Data, options = null) { loadedItem, error ->
                        if (error != null) {
                            onError(GcipResult.ctx(GcipStatus.InvalidBlock))
                        } else {
                            val data = loadedItem as? NSData
                            if (data != null) {
                                onSuccess(data)
                            } else {
                                onError(GcipResult.ctx(GcipStatus.InvalidBlock))
                            }
                        }
                    }

                    return
                }
            }
        }
    }

    fun createExtensionItem(data: NSData): NSExtensionItem {
        val returnProvider = NSItemProvider(
            item = data,
            typeIdentifier = GcipConfig.Data
        )

        val returnItem = NSExtensionItem()
        returnItem.attachments = listOf(returnProvider)

        return returnItem
    }
}
