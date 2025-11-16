package foundation.openstore.signer.app.data.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import com.openstore.app.core.common.lazyUnsafe
import foundation.openstore.gcip.core.transport.GcipId

data class Transaction(
    val signing: SigningEntity,
    val connection: ConnectionEntity,
    val credential: LocalCredential?,
)

val SigningEntity.reqSequenceId: Long get() = sequenceId!!

enum class SigningTarget(val code: Int) {
    Default(0),
    Connection(1),
    Extension(2);


    val display: String by lazyUnsafe {
        when (this) {
            Connection -> "Connection"
            Extension -> "Extension"
            Default -> "Signing"
        }
    }

    companion object {
        fun from(value: Int): SigningTarget? = entries.firstOrNull { it.code == value }
    }
}

interface SigningDao {
    suspend fun insert(entity: SigningEntity)
    suspend fun findAllByWallet(walletId: GcipId): List<SigningEntity>
    suspend fun getMaxSequenceId(): Int?
    suspend fun findTransactions(walletId: GcipId, limit: Int): List<Transaction>
    suspend fun findAllTransactions(walletId: GcipId): List<Transaction>
}

class SigningDaoImpl(db: SignerDatabase) : SigningDao {

    private val queries = db.signingQueries

    override suspend fun insert(entity: SigningEntity) {
        queries.insert(entity)
    }

    override suspend fun findAllByWallet(walletId: GcipId): List<SigningEntity> {
        return queries.findAllByWallet(walletId).awaitAsList()
    }

    override suspend fun getMaxSequenceId(): Int? {
        return queries.getMaxSequenceId().awaitAsOneOrNull()?.MAX?.toInt()
    }

    override suspend fun findTransactions(walletId: GcipId, limit: Int): List<Transaction> {
        return queries.findAllSignings(walletId)
            .awaitAsList()
            .take(limit) // TODO
            .map { row ->
                row.toTransaction()
            }
    }

    override suspend fun findAllTransactions(walletId: GcipId): List<Transaction> {
        return queries.findAllSignings(walletId)
            .awaitAsList()
            .map { row ->
                row.toTransaction()
            }
    }

    private fun FindAllSignings.toTransaction(): Transaction {
        return Transaction(
            signing = SigningEntity(
                signingId = signingId,
                sequenceId = sequenceId,
                connectionId = connectionId,
                walletId = walletId,
                credentialId = credentialId,
                challenge = challenge,
                challengeTransforms = challengeTransforms,
                challengeFormat = challengeFormat,
                method = method,
                blockId = blockId,
                meta = meta,
                createdAt = createdAt,
            ),
            connection = ConnectionEntity(
                id = id,
                serviceName = serviceName,
                serviceOrigin = serviceOrigin,
                callerId = callerId,
                callerScheme = callerScheme,
                callerSignature = callerSignature,
                meta = meta_,
                walletId = walletId_,
                createdAt = createdAt_,
            ),
            credential = if (id_ != null) {
                LocalCredential(
                    CredentialEntity(
                        id = id_,
                        algorithm = algorithm!!,
                        walletId = walletId__!!,
                        derivationPath = derivationPath,
                        type = type!!,
                        payload = payload!!,
                    )
                )
            } else {
                null
            }
        )
    }
}
