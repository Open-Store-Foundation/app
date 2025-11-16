package com.openstore.app.core.root

actual class DeviceRootProvider {
    actual fun isRooted(): Boolean {
        return false
    }
}
