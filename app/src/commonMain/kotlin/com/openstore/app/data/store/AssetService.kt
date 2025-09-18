package com.openstore.app.data.store

import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.Asset
import com.openstore.app.data.PlatformId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import com.openstore.app.core.net.json_rpc.util.apiBodyOrError
import com.openstore.app.data.db.ResponseRepo
import io.ktor.client.statement.readRawBytes

interface AssetService {
    suspend fun findObject(
        address: String,
    ): Asset

    suspend fun findObject(
        id: Long,
    ): Asset

    suspend fun loadChart(
        size: Int,
        offset: Int,
        typeId: ObjTypeId? = null,
        categoryId: Int? = null,
    ): List<Asset>

    suspend fun search(
        query: String,
        size: Int,
        offset: Int,
        typeId: ObjTypeId? = null,
        categoryId: Int? = null,
    ): List<Asset>
}

class AssetServiceDefault(
    private val platformId: PlatformId,
    private val host: String,
    private val client: HttpClient,
    private val cache: ResponseRepo,
) : AssetService {

    override suspend fun findObject(address: String): Asset {
        val result = client.get(host) {
            url {
                appendPathSegments("/asset/address/$address")
            }
        }

        return result.apiBodyOrError()
    }

    override suspend fun findObject(id: Long): Asset {
        val result = client.get(host) {
            url {
                appendPathSegments("/asset/id/$id")
            }
        }

        return result.apiBodyOrError()
    }

    override suspend fun loadChart(
        size: Int,
        offset: Int,
        typeId: ObjTypeId?,
        categoryId: Int?,
    ): List<Asset> {
        if (offset > 60) {
            return emptyList()
        }

        require(size in 20..100) { "Size must be between 20 and 100" }

        val result = client.get(host) {
            url {
                appendPathSegments("/asset/chart")
            }

            parameter("size", size)
            parameter("offset", offset)

            parameter("platform", platformId)

            typeId?.let { parameter("type", it) }
            categoryId?.let { parameter("category_id", it) }
        }

        if (offset == 0 && typeId == null && categoryId == null) {
            val body = result.readRawBytes()
            return cache.saveChart(typeId, body)
        } else {
            return result.apiBodyOrError()
        }
    }

    override suspend fun search(
        query: String,
        size: Int,
        offset: Int,
        typeId: ObjTypeId?,
        categoryId: Int?,
    ): List<Asset> {
        if (query.length < 3) {
            return emptyList()
        }

        require(size in 20..100) { "Size must be between 20 and 100" }
        val result = client.get(host) {
            url {
                appendPathSegments("/asset/search")
            }

            parameter("platform", platformId)
            parameter("type", typeId)
            parameter("content", query)
            parameter("size", size)
            parameter("offset", offset)

            categoryId?.let { parameter("category_id", it) }
        }

        return result.apiBodyOrError()
    }
}
