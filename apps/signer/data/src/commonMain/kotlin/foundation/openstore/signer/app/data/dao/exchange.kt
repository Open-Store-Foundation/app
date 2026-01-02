package foundation.openstore.signer.app.data.dao

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import foundation.openstore.gcip.core.transport.GcipTransportType
import foundation.openstore.gcip.core.transport.GcipId
import kotlin.time.Instant

class ExchangeSession(
    val eid: GcipId,
    val connectionId: GcipId?,

    val transport: GcipTransportType,
    val transportData: ByteArray,
    val sessionKey: ByteArray,
    val meta: ByteArray?,
    val createdAt: Instant,
)

interface ExchangeDao {
    suspend fun save(entity: ExchangeSession)
    suspend fun findExchangeById(eid: GcipId): ExchangeSession?
    suspend fun setConnection(eid: GcipId, connectionId: GcipId)
}

class ExchangeDaoImpl(db: SignerDatabase) : ExchangeDao {
    private val queries = db.exchangeQueries

    override suspend fun save(entity: ExchangeSession) {
        queries.insert(entity.toEntity())
    }

    override suspend fun findExchangeById(eid: GcipId): ExchangeSession? {
        return queries.findById(eid).awaitAsOneOrNull()?.toDomain()
    }

    override suspend fun setConnection(eid: GcipId, connectionId: GcipId) {
        queries.setConnection(connectionId, eid)
    }

    private fun ExchangeSession.toEntity(): ExchangeEntity {
        return ExchangeEntity(
            eid = eid,
            connectionId = connectionId,
            transport = transport,
            transportData = transportData,
            sessionKey = sessionKey,
            meta = meta,
            createdAt = createdAt,
        )
    }

    private fun ExchangeEntity.toDomain(): ExchangeSession {
        return ExchangeSession(
            eid = eid,
            connectionId = connectionId,
            transport = transport,
            transportData = transportData,
            sessionKey = sessionKey,
            meta = meta,
            createdAt = createdAt,
        )
    }
}
