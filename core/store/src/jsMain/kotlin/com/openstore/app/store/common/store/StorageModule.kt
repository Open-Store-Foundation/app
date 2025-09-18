package com.openstore.app.store.common.store

import com.russhwolf.settings.StorageSettings
import com.openstore.core.store.common.platform.JsPlatform

actual class StorageModule {
    actual val keyValueFactory: PlatformKeyValueFactory = object : PlatformKeyValueFactory {
        override fun create(name: String?): KeyValueStorage {
            return if (!JsPlatform.isDom) {
                val chromeStorage = js("self.chrome.storage.local").unsafeCast<ChromeStorage>()
                WebExtensionChromeSettings(chromeStorage, name ?: "")
            } else {
                WebLocalStorageSettings(StorageSettings(), name ?: "")
            }
        }
    }
}
