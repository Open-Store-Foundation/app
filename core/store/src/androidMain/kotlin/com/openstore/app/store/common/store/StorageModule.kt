package com.openstore.app.store.common.store

import com.russhwolf.settings.SharedPreferencesSettings
import com.openstore.app.core.os.Android

actual class StorageModule {
    actual val keyValueFactory: PlatformKeyValueFactory = object : PlatformKeyValueFactory {
        override fun create(name: String?): KeyValueStorage {
            return KeyValueStorageWrapper(
                SharedPreferencesSettings.Factory(Android.context).create(name),
            )
        }
    }
}
