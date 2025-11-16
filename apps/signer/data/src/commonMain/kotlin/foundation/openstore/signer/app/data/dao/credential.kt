package foundation.openstore.signer.app.data.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import com.openstore.app.core.common.lazyUnsafe
import com.openstore.app.core.common.toLower0xHex
import foundation.openstore.gcip.core.Algorithm
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.transport.GcipCredentialType
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.util.fromUrlBase64
import kotlin.getValue

class LocalCredential(
    val entity: CredentialEntity,
) {
    val id: GcipId get() = entity.id
    val algorithm: Algorithm get() = entity.algorithm
    val walletId: GcipId get() = entity.walletId
    val derivationPath: String? get() = entity.derivationPath
    val type: GcipCredentialType get() = entity.type
    val payload: String get() = entity.payload

    val payloadRaw: ByteArray by lazyUnsafe {
        entity.payload.fromUrlBase64()
    }

    val payloadHex: String by lazyUnsafe {
        payloadRaw.toLower0xHex()
    }

    val derivationType: Derivation by lazyUnsafe {
        Derivation.from(entity.algorithm, entity.derivationPath)
    }
}

interface CredentialDao {
    suspend fun findBy(id: GcipId): LocalCredential?
    suspend fun findByIdAndWallet(id: GcipId, walletId: GcipId): LocalCredential?
    suspend fun findAllByWallet(walletId: GcipId, derivations: Set<String>): List<LocalCredential>
    suspend fun findAllByWallet(walletId: GcipId): List<LocalCredential>
    suspend fun insertAll(entity: List<CredentialEntity>)
    suspend fun insert(entity: CredentialEntity)
    suspend fun deleteByWallet(id: GcipId)
}

class CredentialDaoImpl(private val db: SignerDatabase) : CredentialDao {

    private val queries = db.credentialQueries

    override suspend fun findBy(id: GcipId): LocalCredential? {
        return queries.findBy(id)
            .awaitAsOneOrNull()
            ?.let { LocalCredential(it) }
    }

    override suspend fun findByIdAndWallet(id: GcipId, walletId: GcipId): LocalCredential? {
        return queries.findByIdAndWallet(id, walletId)
            .awaitAsOneOrNull()
            ?.let { LocalCredential(it) }
    }

    override suspend fun findAllByWallet(walletId: GcipId, derivations: Set<String>): List<LocalCredential> {
        return if (derivations.isEmpty()) {
            queries.findAllByWallet(walletId)
                .awaitAsList()
                .map { LocalCredential(it) }
        } else {
            queries.findAllByWalletWithDerivations(walletId,derivations)
                .awaitAsList()
                .map { LocalCredential(it) }
        }
    }

    override suspend fun findAllByWallet(walletId: GcipId): List<LocalCredential> {
        return queries.findAllByWallet(walletId)
            .awaitAsList()
            .map { LocalCredential(it) }
    }

    override suspend fun insertAll(entity: List<CredentialEntity>) {
        db.transaction {
            entity.forEach { queries.insert(it) }
        }
    }

    override suspend fun insert(entity: CredentialEntity) {
        queries.insert(entity)
    }

    override suspend fun deleteByWallet(id: GcipId) {
        queries.deleteByWallet(id)
    }
}
