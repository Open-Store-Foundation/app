package com.openwallet.sample

import com.openstore.app.log.L
import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.platform.GcipHandlerFactoryPlatform
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSExtensionItem
import platform.Foundation.NSItemProvider
import platform.Foundation.NSItemProviderRepresentationVisibilityAll
import platform.UIKit.UIActivityType
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController
import foundation.openstore.gcip.platform.utils.toByteArray
import foundation.openstore.gcip.platform.utils.toNSData
import kotlin.collections.emptyList

@OptIn(BetaInteropApi::class)
class IosWalletPlatformDelegate(
    private val viewControllerProvider: () -> UIViewController
) {

    private val handler = GcipHandlerFactoryPlatform.extensionHandler()

    fun startIntent(data: ByteArray, onResult: (ByteArray?) -> Unit) {
        presentExtension(data.toNSData(), onResult)
    }

    private fun presentExtension(data: NSData, onResult: (ByteArray?) -> Unit) {
        val viewController = viewControllerProvider()
        
        val itemProvider = NSItemProvider()

        itemProvider.registerDataRepresentationForTypeIdentifier(
            typeIdentifier = GcipConfig.Data,
            visibility = NSItemProviderRepresentationVisibilityAll
        ) { completion ->
            completion?.invoke(data, null)
            null
        }

        val activityVC = UIActivityViewController(
            activityItems = listOf(itemProvider),
            applicationActivities = null
        )

        activityVC.completionWithItemsHandler = { activityType: UIActivityType?, completed: Boolean, returnedItems: List<*>?, error: NSError? ->
            L.d("completed: $completed, error: $error")
            handleExtensionCompletion(completed, returnedItems, error, onResult)
        }

        activityVC.popoverPresentationController?.apply {
            sourceView = viewController.view
            permittedArrowDirections = 0UL
        }

        viewController.presentViewController(activityVC, animated = true, completion = null)
    }

    private fun handleExtensionCompletion(
        completed: Boolean, 
        returnedItems: List<*>?,
        error: NSError?, 
        onResult: (ByteArray?) -> Unit
    ) {
        if (error != null) {
            onResult(null)
            return
        }

        if (!completed) {
            onResult(null)
            return
        }

        val items: List<*> = returnedItems.orEmpty()
        handler.handleExtensionItems(
            extensionItems = items,
            onError = {
                onResult(null)
            },
            onSuccess = {
                onResult(it.toByteArray())
            }
        )
    }
}

