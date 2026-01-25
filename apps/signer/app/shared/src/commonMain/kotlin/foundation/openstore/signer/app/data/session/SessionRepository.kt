package foundation.openstore.signer.app.data.session

import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.dao.ExchangeDao

class SessionRepository(
    private val exchangeDao: ExchangeDao,
) {
    suspend fun findSessionEncryptionKey(eid: GcipId): ByteArray? {
        val session = exchangeDao.findExchangeById(eid)
            ?: return null

        return session.sessionKey
    }
}
