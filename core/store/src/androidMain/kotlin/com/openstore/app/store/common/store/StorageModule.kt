package com.openstore.app.store.common.store

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings

actual class StorageModule(
    private val context: Context,
) {
    actual val keyValueFactory: PlatformKeyValueFactory = object : PlatformKeyValueFactory {
        override fun create(name: String?): KeyValueStorage {
            return KeyValueStorageWrapper(
                SharedPreferencesSettings.Factory(context)
                    .create(name),
            )
        }
    }
}
