package com.openstore.app.data.store

import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.Asset
import com.openstore.app.data.db.ObjectDao
import com.openstore.app.data.db.ResponseRepo

interface ObjectRepo {

    suspend fun findObject(address: String): Result<Asset>

    suspend fun findObject(id: Long): Result<Asset>

    suspend fun loadChart(
        size: Int,
        offset: Int,
        typeId: ObjTypeId? = null,
        categoryId: Int? = null,
    ): Result<List<Asset>>

    suspend fun search(
        query: String,
        size: Int,
        offset: Int,
        typeId: ObjTypeId? = null,
        categoryId: Int? = null,
    ): Result<List<Asset>>
}

class ObjectRepoDefault(
    private val objectDao: ObjectDao,
    private val objectService: AssetService,
) : ObjectRepo {

    override suspend fun findObject(address: String): Result<Asset> {
        return runCatching {
            val cached = objectDao.findObject(address)

            if (cached == null) {
                val remote = objectService.findObject(address)
                objectDao.insert(remote)
                remote
            } else {
                cached
            }
        }
    }

    override suspend fun findObject(id: Long): Result<Asset> {
        return runCatching {
            val cached = objectDao.findObject(id)

            if (cached == null) {
                val remote = objectService.findObject(id)
                objectDao.insert(remote)
                remote
            } else {
                cached
            }
        }
    }

    override suspend fun loadChart(
        size: Int,
        offset: Int,
        typeId: ObjTypeId?,
        categoryId: Int?
    ): Result<List<Asset>> {
        return runCatching {
            objectService.loadChart(size, offset, typeId, categoryId)
                .also {
                    runCatching { objectDao.insertAll(it) }
                }
        }
    }

    override suspend fun search(
        query: String,
        size: Int,
        offset: Int,
        typeId: ObjTypeId?,
        categoryId: Int?
    ): Result<List<Asset>> {
        return runCatching {
            objectService.search(query, size, offset, typeId, categoryId)
                .also {
                    runCatching { objectDao.insertAll(it) }
                }
        }
    }
}
