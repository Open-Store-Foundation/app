package com.openstore.app.store.common.store

import com.russhwolf.settings.NSUserDefaultsSettings

actual class StorageModule {
    actual val settingsFactory: PlatformSettingsFactory = object : PlatformSettingsFactory {
        override fun create(name: String?): AsyncSettings {
            return SettingsWrapper(
                NSUserDefaultsSettings.Factory().create(name),
            )
        }
    }
}
