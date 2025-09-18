package com.openstore.app.store.common.store

import kotlinx.coroutines.await

class WebExtensionChromeSettings(
    private val chromeStorage: ChromeStorage,
    private val name: String,
) : KeyValueStorage {

    private fun buildKey(key: String): String = "$name-$key"

    override suspend fun clear() {
        chromeStorage.clear().await()
    }

    override suspend fun remove(key: String) {
        chromeStorage.remove(buildKey(key)).await()
    }

    override suspend fun hasKey(key: String): Boolean {
        return chromeStorage.get(buildKey(key)).await() != null
    }

    override suspend fun putInt(key: String, value: Int) {
        chromeStorage.storeItem(buildKey(key), value.toString())
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int {
        return chromeStorage.getItem(buildKey(key)).await()?.toInt() ?: defaultValue
    }

    override suspend fun getIntOrNull(key: String): Int? {
        return chromeStorage.getItem(buildKey(key)).await()?.toIntOrNull()
    }

    override suspend fun putLong(key: String, value: Long) {
        chromeStorage.storeItem(buildKey(key), value.toString())
    }

    override suspend fun getLong(key: String, defaultValue: Long): Long {
        return chromeStorage.getItem(buildKey(key)).await()?.toLong() ?: defaultValue
    }

    override suspend fun getLongOrNull(key: String): Long? {
        return chromeStorage.getItem(buildKey(key)).await()?.toLongOrNull()
    }

    override suspend fun putString(key: String, value: String) {
        chromeStorage.storeItem(buildKey(key), value)
    }

    override suspend fun getString(key: String, defaultValue: String): String {
        return chromeStorage.getItem(buildKey(key)).await() ?: defaultValue
    }

    override suspend fun getStringOrNull(key: String): String? {
        return chromeStorage.getItem(buildKey(key)).await()
    }

    override suspend fun putFloat(key: String, value: Float) {
        chromeStorage.storeItem(buildKey(key), value.toString())
    }

    override suspend fun getFloat(key: String, defaultValue: Float): Float {
        return chromeStorage.getItem(buildKey(key)).await()?.toFloat() ?: defaultValue
    }

    override suspend fun getFloatOrNull(key: String): Float? {
        return chromeStorage.getItem(buildKey(key)).await()?.toFloatOrNull()
    }

    override suspend fun putDouble(key: String, value: Double) {
        chromeStorage.storeItem(buildKey(key), value.toString())
    }

    override suspend fun getDouble(key: String, defaultValue: Double): Double {
        return chromeStorage.getItem(buildKey(key)).await()?.toDouble() ?: defaultValue
    }

    override suspend fun getDoubleOrNull(key: String): Double? {
        return chromeStorage.getItem(buildKey(key)).await()?.toDoubleOrNull()
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        chromeStorage.storeItem(buildKey(key), value.toString())
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return chromeStorage.getItem(buildKey(key)).await()?.toBoolean() ?: defaultValue
    }

    override suspend fun getBooleanOrNull(key: String): Boolean? {
        return chromeStorage.getItem(buildKey(key)).await()?.toBooleanStrictOrNull()
    }
}
