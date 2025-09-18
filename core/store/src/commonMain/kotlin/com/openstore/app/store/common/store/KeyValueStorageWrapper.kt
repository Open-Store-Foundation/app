package com.openstore.app.store.common.store

import com.russhwolf.settings.Settings

class KeyValueStorageWrapper(private val delegate: Settings) : KeyValueStorage {

    override suspend fun clear() = delegate.clear()
    override suspend fun remove(key: String) = delegate.remove(key)
    override suspend fun hasKey(key: String): Boolean = delegate.hasKey(key)
    override suspend fun putInt(key: String, value: Int) = delegate.putInt(key, value)
    override suspend fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(key, defaultValue)
    override suspend fun getIntOrNull(key: String): Int? = delegate.getIntOrNull(key)
    override suspend fun putLong(key: String, value: Long) = delegate.putLong(key, value)
    override suspend fun getLong(key: String, defaultValue: Long): Long = delegate.getLong(key, defaultValue)
    override suspend fun getLongOrNull(key: String): Long? = delegate.getLongOrNull(key)
    override suspend fun putString(key: String, value: String) = delegate.putString(key, value)
    override suspend fun getString(key: String, defaultValue: String): String = delegate.getString(key, defaultValue)
    override suspend fun getStringOrNull(key: String): String? = delegate.getStringOrNull(key)
    override suspend fun putFloat(key: String, value: Float) = delegate.putFloat(key, value)
    override suspend fun getFloat(key: String, defaultValue: Float): Float = delegate.getFloat(key, defaultValue)
    override suspend fun getFloatOrNull(key: String): Float? = delegate.getFloatOrNull(key)
    override suspend fun putDouble(key: String, value: Double) = delegate.putDouble(key, value)
    override suspend fun getDouble(key: String, defaultValue: Double): Double = delegate.getDouble(key, defaultValue)
    override suspend fun getDoubleOrNull(key: String): Double? = delegate.getDoubleOrNull(key)
    override suspend fun putBoolean(key: String, value: Boolean) = delegate.putBoolean(key, value)
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate.getBoolean(key, defaultValue)

    override suspend fun getBooleanOrNull(key: String): Boolean? = delegate.getBooleanOrNull(key)
}
