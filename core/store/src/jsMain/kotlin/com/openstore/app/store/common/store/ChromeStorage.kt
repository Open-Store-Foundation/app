package com.openstore.app.store.common.store

import kotlin.js.Promise

external interface ChromeStorage {
    fun set(items: dynamic): Promise<Unit>
    fun get(key: String): Promise<dynamic>
    fun remove(key: String): Promise<Unit>
    fun clear(): Promise<Unit>
}

fun ChromeStorage.storeItem(key: String, value: String) {
    val item = js("{}")
    item[key] = value
    set(item)
}

fun ChromeStorage.getItem(key: String): Promise<String?> {
    return get(key).then<String?> {
        it[key] as? String
    }.catch {
        null
    }
}
