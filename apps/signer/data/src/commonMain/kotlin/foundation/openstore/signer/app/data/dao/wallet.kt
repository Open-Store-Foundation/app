package foundation.openstore.signer.app.data.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import foundation.openstore.gcip.core.transport.GcipId

data class WalletWithConnections(
    val wallet: WalletEntity,
    val count: Int,
)

interface WalletDao {
    suspend fun findById(id: GcipId): WalletEntity?
    suspend fun findByConnection(id: GcipId): WalletEntity?
    suspend fun insert(entity: WalletEntity)
    suspend fun delete(id: GcipId)
    suspend fun count(): Int
    suspend fun findAll(): List<WalletEntity>
    suspend fun verify(id: GcipId, isVerified: Boolean)
    suspend fun findAllWithConnectionCount(): List<WalletWithConnections>
    suspend fun findWithConnectionCount(id: GcipId): WalletWithConnections?
}

class WalletDaoImpl(private val db: SignerDatabase) : WalletDao {
    private val queries = db.walletQueries
    private val connectionQueries = db.connectionQueries

    override suspend fun findById(id: GcipId): WalletEntity? {
        return queries.findById(id).awaitAsOneOrNull()
    }

    override suspend fun findByConnection(id: GcipId): WalletEntity? {
        // SQLDelight doesn't have a direct join query in Wallet.sq for this yet, 
        // but we can query Connection then Wallet, or add a join query.
        // For now, mirroring logic: find connection -> get walletId -> find wallet.
        val connection = connectionQueries.findById(id).awaitAsOneOrNull() ?: return null
        return findById(connection.walletId)
    }

    override suspend fun insert(entity: WalletEntity) {
        queries.insert(entity)
    }

    override suspend fun delete(id: GcipId) {
        queries.delete(id)
    }

    override suspend fun count(): Int {
        return queries.count().awaitAsOne().toInt()
    }

    override suspend fun findAll(): List<WalletEntity> {
        return queries.findAll().awaitAsList()
    }

    override suspend fun verify(id: GcipId, isVerified: Boolean) {
        queries.verify(isVerified, id)
    }

    override suspend fun findAllWithConnectionCount(): List<WalletWithConnections> {
        // Ideal: New SQL query with GROUP BY.
        // Current: Loop (inefficient but matches typical migration step 1).
        // Let's optimize slightly: Fetch all wallets, fetch all connections, group in memory?
        // OR: just loop as per request to "wrap".
        val wallets = findAll()
        return wallets.map { wallet ->
            // We need a count query in ConnectionDao, but we don't have it exposed in .sq yet
            // Let's just fetch all by wallet and count.
            val connections = connectionQueries.findAllByWalletId(wallet.id).awaitAsList()
            WalletWithConnections(wallet, connections.size)
        }
    }

    override suspend fun findWithConnectionCount(id: GcipId): WalletWithConnections? {
        val wallet = findById(id) ?: return null
        val connections = connectionQueries.findAllByWalletId(id).awaitAsList()
        return WalletWithConnections(wallet, connections.size)
    }
}
