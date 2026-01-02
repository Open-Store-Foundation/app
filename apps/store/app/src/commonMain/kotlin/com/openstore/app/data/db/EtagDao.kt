package com.openstore.app.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(
    tableName = "etag_key",
)
class EtagKey(
    @PrimaryKey
    var url: String,
    var etag: String,
)

@Entity(
    tableName = "etag_value",
)
class EtagValue(
    @PrimaryKey
    var etag: String,
    var body: String,
)

@Dao
interface EtagDao {

    @Insert
    fun insert(key: EtagKey)

    @Insert
    fun insert(key: EtagValue)

    @Query("SELECT * FROM etag_key WHERE url = :url")
    fun getKey(url: String): EtagKey?

    @Query("SELECT * FROM etag_value WHERE etag = :etag")
    fun getValue(etag: String): EtagValue?
}