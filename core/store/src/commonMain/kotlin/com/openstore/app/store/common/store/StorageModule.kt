package com.openstore.app.store.common.store

expect class StorageModule {
    val keyValueFactory: PlatformKeyValueFactory
}
