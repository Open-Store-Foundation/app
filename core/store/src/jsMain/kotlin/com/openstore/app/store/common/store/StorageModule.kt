package com.openstore.app.store.common.store

import com.openstore.app.core.os.Js

actual class StorageModule {
    actual val keyValueFactory: PlatformKeyValueFactory = object : PlatformKeyValueFactory {
        override fun create(name: String?): KeyValueStorage {
            return if (!Js.isDom) {
                val chromeStorage = js("self.chrome.storage.local").unsafeCast<ChromeStorage>()
                WebExtensionChromeSettings(chromeStorage, name ?: "")
            } else {
                WebLocalStorageSettings(name = name ?: "")
            }
        }
    }
}
