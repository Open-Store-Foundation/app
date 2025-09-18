package com.openstore.app.store.common.store

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

class WebLocalStorageSettings(
    private val delegate: Settings = StorageSettings(),
    private val name: String,
) : KeyValueStorage {

    private fun buildKey(key: String): String = "$name-$key"

    override suspend fun clear() = delegate.clear()
    override suspend fun remove(key: String) = delegate.remove(buildKey(key))
    override suspend fun hasKey(key: String): Boolean = delegate.hasKey(buildKey(key))
    override suspend fun putInt(key: String, value: Int) = delegate.putInt(buildKey(key), value)
    override suspend fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(buildKey(key), defaultValue)
    override suspend fun getIntOrNull(key: String): Int? = delegate.getIntOrNull(buildKey(key))
    override suspend fun putLong(key: String, value: Long) = delegate.putLong(buildKey(key), value)
    override suspend fun getLong(key: String, defaultValue: Long): Long = delegate.getLong(buildKey(key), defaultValue)
    override suspend fun getLongOrNull(key: String): Long? = delegate.getLongOrNull(buildKey(key))
    override suspend fun putString(key: String, value: String) = delegate.putString(buildKey(key), value)
    override suspend fun getString(key: String, defaultValue: String): String =
        delegate.getString(buildKey(key), defaultValue)

    override suspend fun getStringOrNull(key: String): String? = delegate.getStringOrNull(buildKey(key))
    override suspend fun putFloat(key: String, value: Float) = delegate.putFloat(buildKey(key), value)
    override suspend fun getFloat(key: String, defaultValue: Float): Float =
        delegate.getFloat(buildKey(key), defaultValue)

    override suspend fun getFloatOrNull(key: String): Float? = delegate.getFloatOrNull(buildKey(key))
    override suspend fun putDouble(key: String, value: Double) = delegate.putDouble(buildKey(key), value)
    override suspend fun getDouble(key: String, defaultValue: Double): Double =
        delegate.getDouble(buildKey(key), defaultValue)

    override suspend fun getDoubleOrNull(key: String): Double? = delegate.getDoubleOrNull(buildKey(key))
    override suspend fun putBoolean(key: String, value: Boolean) = delegate.putBoolean(buildKey(key), value)
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate.getBoolean(buildKey(key), defaultValue)

    override suspend fun getBooleanOrNull(key: String): Boolean? = delegate.getBooleanOrNull(buildKey(key))
}
