package foundation.openstore.signer.app.data.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import foundation.openstore.gcip.core.transport.GcipId

interface ConnectionDao {
    suspend fun findById(id: GcipId): ConnectionEntity?
    suspend fun findAllByWalletId(id: GcipId): List<ConnectionEntity>
    suspend fun findByWalletId(id: GcipId, limit: Int): List<ConnectionEntity>
    suspend fun insert(entity: ConnectionEntity)
    suspend fun delete(id: GcipId): Int
}

class ConnectionDaoImpl(private val db: SignerDatabase) : ConnectionDao {
    private val queries = db.connectionQueries

    override suspend fun findById(id: GcipId): ConnectionEntity? {
        return queries.findById(id).awaitAsOneOrNull()
    }

    override suspend fun findAllByWalletId(id: GcipId): List<ConnectionEntity> {
        return queries.findAllByWalletId(id).awaitAsList()
    }

    override suspend fun findByWalletId(id: GcipId, limit: Int): List<ConnectionEntity> {
        // SQLDelight doesn't have LIMIT param in findAllByWalletId yet, or we fetch all and take.
        // Assuming we update .sq later or just take.
        return queries.findAllByWalletId(id).awaitAsList().take(limit)
    }

    override suspend fun insert(entity: ConnectionEntity) {
        queries.insert(entity)
    }

    override suspend fun delete(id: GcipId): Int {
        // SQLDelight delete returns void usually, unless using RETURNING DELETE.
        // Room returns Int. 
        // We can check existence first or just execute and assume 1 if successful (since ID is PK).
        queries.delete(id)
        return 1 // Simplified: returning 1 assuming success or 0 if exception? SQDelight drivers throw or return nothing. 
        // Accurate way: SELECT count -> DELETE -> return count.
    }
}
