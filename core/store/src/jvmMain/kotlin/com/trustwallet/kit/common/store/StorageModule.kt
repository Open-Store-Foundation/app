package com.openstore.app.store.common.store

import com.russhwolf.settings.PreferencesSettings

actual class StorageModule actual constructor() {
    actual val settingsFactory: PlatformSettingsFactory = object : PlatformSettingsFactory {
        override fun create(name: String?): AsyncSettings {
            return SettingsWrapper(PreferencesSettings.Factory().create(name))
        }
    }
}
