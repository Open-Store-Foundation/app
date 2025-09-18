package com.openstore.app.data.db

import com.openstore.app.core.net.json_rpc.util.ResponseResult
import com.openstore.app.data.Feed
import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.Asset
import com.openstore.app.store.common.store.KeyValueStorage
import kotlinx.serialization.json.Json

// TODO v2 consider to move to Ktor plugin
interface ResponseRepo {
    suspend fun restoreFeed(typeId: ObjTypeId): Feed?
    suspend fun saveFeed(typeId: ObjTypeId, result: ByteArray): Feed

    suspend fun restoreChart(typeId: ObjTypeId?): List<Asset>?
    suspend fun saveChart(typeId: ObjTypeId?, result: ByteArray): List<Asset>
}

class ResponseRepoKeyValue(
    private val cache: KeyValueStorage,
    private val json: Json,
) : ResponseRepo {

    companion object {
        private const val FEED_KEY = "feed"
        private const val CHART_KEY = "chart"
    }

    override suspend fun restoreFeed(typeId: ObjTypeId): Feed? {
        val body = cache.getStringOrNull(feedKey(typeId))
            ?: return null

        val feed = json.decodeFromString<ResponseResult<Feed>>(body)

        return feed.data
    }

    override suspend fun saveFeed(typeId: ObjTypeId, result: ByteArray): Feed {
        val body = result.decodeToString()
        val feed = json.decodeFromString<ResponseResult<Feed>>(body)

        cache.putString(feedKey(typeId), body)

        return feed.data
    }

    private fun feedKey(typeId: ObjTypeId): String {
        return "$FEED_KEY:${typeId.id}"
    }

    override suspend fun restoreChart(typeId: ObjTypeId?): List<Asset>? {
        val body = cache.getStringOrNull(chartKey(typeId))
            ?: return null

        val feed = json.decodeFromString<ResponseResult<List<Asset>>>(body)

        return feed.data
    }

    override suspend fun saveChart(
        typeId: ObjTypeId?,
        result: ByteArray
    ): List<Asset> {
        val body = result.decodeToString()
        val chart = json.decodeFromString<ResponseResult<List<Asset>>>(body)

        cache.putString(chartKey(typeId), body)

        return chart.data
    }

    private fun chartKey(typeId: ObjTypeId?): String {
        return "$CHART_KEY:${typeId?.id}"
    }
}