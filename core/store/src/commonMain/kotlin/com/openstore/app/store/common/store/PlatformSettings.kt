package com.openstore.app.store.common.store

interface KeyValueStorage {
    suspend fun clear()
    suspend fun remove(key: String)
    suspend fun hasKey(key: String): Boolean
    suspend fun putInt(key: String, value: Int)
    suspend fun getInt(key: String, defaultValue: Int): Int
    suspend fun getIntOrNull(key: String): Int?
    suspend fun putLong(key: String, value: Long)
    suspend fun getLong(key: String, defaultValue: Long): Long
    suspend fun getLongOrNull(key: String): Long?
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String, defaultValue: String): String
    suspend fun getStringOrNull(key: String): String?
    suspend fun putFloat(key: String, value: Float)
    suspend fun getFloat(key: String, defaultValue: Float): Float
    suspend fun getFloatOrNull(key: String): Float?
    suspend fun putDouble(key: String, value: Double)
    suspend fun getDouble(key: String, defaultValue: Double): Double
    suspend fun getDoubleOrNull(key: String): Double?
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean
    suspend fun getBooleanOrNull(key: String): Boolean?
}

interface PlatformKeyValueFactory {
    fun create(name: String?): KeyValueStorage
}
