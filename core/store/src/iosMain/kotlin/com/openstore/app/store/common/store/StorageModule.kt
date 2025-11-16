package com.openstore.app.store.common.store

import com.russhwolf.settings.NSUserDefaultsSettings
import platform.Foundation.NSUserDefaults

actual class StorageModule(private val appGroupId: String? = null) {
    actual val keyValueFactory: PlatformKeyValueFactory = object : PlatformKeyValueFactory {
        override fun create(name: String?): KeyValueStorage {
            val settings = if (appGroupId != null) {
                val sharedDefaults = NSUserDefaults(suiteName = appGroupId)
                NSUserDefaultsSettings(sharedDefaults)
            } else {
                NSUserDefaultsSettings.Factory().create(name)
            }
            return KeyValueStorageWrapper(settings)
        }
    }
}
