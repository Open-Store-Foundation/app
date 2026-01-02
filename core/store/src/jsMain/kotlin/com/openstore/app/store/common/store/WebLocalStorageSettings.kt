package com.openstore.app.store.common.store

import kotlinx.browser.localStorage
import org.w3c.dom.Storage

@Suppress("NOTHING_TO_INLINE")
internal inline operator fun Storage.get(key: String): String? = getItem(key)

@Suppress("NOTHING_TO_INLINE")
internal inline operator fun Storage.set(key: String, value: String) = setItem(key, value)

class WebLocalStorageSettings(
    private val delegate: Storage = localStorage,
    private val name: String
) : KeyValueStorage {

    private fun buildKey(key: String): String {
        return if (name.isEmpty()) {
            key
        } else {
            "$name-$key"
        }
    }

    override suspend fun clear() = delegate.clear()
    override suspend fun remove(key: String) = delegate.removeItem(buildKey(key))
    override suspend fun hasKey(key: String): Boolean = delegate[buildKey(key)] != null

    override suspend fun putInt(key: String, value: Int) {
        delegate[buildKey(key)] = value.toString()
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int =
        delegate[buildKey(key)]?.toIntOrNull() ?: defaultValue

    override suspend fun getIntOrNull(key: String): Int? =
        delegate[buildKey(key)]?.toIntOrNull()

    override suspend fun putLong(key: String, value: Long) {
        delegate[buildKey(key)] = value.toString()
    }

    override suspend fun getLong(key: String, defaultValue: Long): Long =
        delegate[buildKey(key)]?.toLongOrNull() ?: defaultValue

    override suspend fun getLongOrNull(key: String): Long? =
        delegate[buildKey(key)]?.toLongOrNull()

    override suspend fun putString(key: String, value: String) {
        delegate[buildKey(key)] = value
    }

    override suspend fun getString(key: String, defaultValue: String): String =
        delegate[buildKey(key)] ?: defaultValue

    override suspend fun getStringOrNull(key: String): String? =
        delegate[buildKey(key)]

    override suspend fun putFloat(key: String, value: Float) {
        delegate[buildKey(key)] = value.toString()
    }

    override suspend fun getFloat(key: String, defaultValue: Float): Float =
        delegate[buildKey(key)]?.toFloatOrNull() ?: defaultValue

    override suspend fun getFloatOrNull(key: String): Float? =
        delegate[buildKey(key)]?.toFloatOrNull()

    override suspend fun putDouble(key: String, value: Double) {
        delegate[buildKey(key)] = value.toString()
    }

    override suspend fun getDouble(key: String, defaultValue: Double): Double =
        delegate[buildKey(key)]?.toDoubleOrNull() ?: defaultValue

    override suspend fun getDoubleOrNull(key: String): Double? =
        delegate[buildKey(key)]?.toDoubleOrNull()

    override suspend fun putBoolean(key: String, value: Boolean) {
        delegate[buildKey(key)] = value.toString()
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate[buildKey(key)]?.toBooleanStrictOrNull() ?: defaultValue

    override suspend fun getBooleanOrNull(key: String): Boolean? =
        delegate[buildKey(key)]?.toBooleanStrictOrNull()
}
