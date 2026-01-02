package com.openstore.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openstore.app.core.common.Time
import com.openstore.app.data.Asset

@Dao
interface ObjectDao {

    companion object {
        private const val CACHE_LIFETIME = 1000 * 60 * 10 // 10 min TODO to config
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Asset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: List<Asset>)

    @Query("SELECT * FROM object WHERE id = :id AND createdAt > :cacheLifetimeTimestamp")
    suspend fun findObject(
        id: Long,
        cacheLifetimeTimestamp: Long = cacheLifetimeTimestamp()
    ): Asset?

    @Query("SELECT * FROM object WHERE address = :address AND createdAt > :cacheLifetimeTimestamp")
    suspend fun findObject(
        address: String,
        cacheLifetimeTimestamp: Long = cacheLifetimeTimestamp()
    ): Asset?

    fun cacheLifetimeTimestamp(): Long {
        return Time.nowMs() - CACHE_LIFETIME
    }
}
