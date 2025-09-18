package com.openstore.app.data.store

import com.openstore.app.core.net.json_rpc.util.ResponseResult
import com.openstore.app.core.net.json_rpc.util.apiBodyOrError
import com.openstore.app.data.Category
import com.openstore.app.data.Feed
import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.PlatformId
import com.openstore.app.data.db.ResponseRepo
import com.openstore.app.store.common.store.KeyValueStorage
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.readRawBytes
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import kotlin.time.TimedValue

interface StoreService {
    suspend fun loadCategories(): List<Category>
    suspend fun restoreFeed(typeId: ObjTypeId): Feed?
    suspend fun loadFeed(typeId: ObjTypeId): Feed
}

class StoreServiceDefault(
    private val platformId: PlatformId,
    private val host: String,
    private val client: HttpClient,
    private val cache: ResponseRepo,
) : StoreService {
    override suspend fun loadCategories(): List<Category> {
        val result = client.get(host) {
            url {
                appendPathSegments("/store/categories")
            }
        }
        return result.apiBodyOrError()
    }

    override suspend fun restoreFeed(typeId: ObjTypeId): Feed? {
        return cache.restoreFeed(typeId)
    }

    override suspend fun loadFeed(
        typeId: ObjTypeId,
    ): Feed {
        val result = client.get(host) {
            url {
                appendPathSegments("/store/feed")
            }

            parameter("platform", platformId)
            parameter("type", typeId)
        }

        val data = result.readRawBytes()

        return cache.saveFeed(typeId, data)
    }
}
