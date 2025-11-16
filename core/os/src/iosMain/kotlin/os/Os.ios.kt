package com.openstore.app.core.os

import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomMac
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.UIUserInterfaceIdiomPhone

actual object Os {
    actual fun platform(): Platform {
        return when (UIDevice.currentDevice.userInterfaceIdiom) {
            UIUserInterfaceIdiomPhone -> Platform.IOS
            UIUserInterfaceIdiomPad -> Platform.IPad
            UIUserInterfaceIdiomMac -> Platform.MacOS
            else -> throw IllegalStateException("Unknown platform")
        }
    }
}