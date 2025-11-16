package com.openstore.app.ui.navigation

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun androidx.navigation.NavHostController.navigateToBrowser(uri: String) {
    NSURL.URLWithString(uri)?.let { url ->
        UIApplication.sharedApplication.openURL(url)
    }
}