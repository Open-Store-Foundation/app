package com.openstore.app.store.common.store

import com.russhwolf.settings.PreferencesSettings

actual class StorageModule {
    actual val keyValueFactory: PlatformKeyValueFactory = object : PlatformKeyValueFactory {
        override fun create(name: String?): KeyValueStorage {
            return KeyValueStorageWrapper(PreferencesSettings.Factory().create(name))
        }
    }
}
