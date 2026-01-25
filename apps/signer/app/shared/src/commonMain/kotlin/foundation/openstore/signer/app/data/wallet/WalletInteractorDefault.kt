package foundation.openstore.signer.app.data.wallet

import com.openstore.app.core.async.Relay
import com.openstore.app.core.common.use
import com.openstore.core.signing.Cryptography
import foundation.openstore.gcip.core.Blockchain
import foundation.openstore.gcip.core.Challenge
import foundation.openstore.gcip.core.CredentialRequest
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.Credential
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.gcip.core.Transport
import foundation.openstore.gcip.core.Wallet
import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.encryption.KeyPair
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.ExchangeKey
import foundation.openstore.gcip.core.Meta
import foundation.openstore.gcip.core.transport.GcipConnectionType
import foundation.openstore.gcip.core.transport.GcipCredentialType
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.transport.GcipTransportType
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.toUrlBase64Fmt
import foundation.openstore.gcip.transform.GcipTransformer
import foundation.openstore.signer.app.data.Emoji
import foundation.openstore.signer.app.data.dao.ConnectionCredentialDao
import foundation.openstore.signer.app.data.dao.ConnectionCredentialEntity
import foundation.openstore.signer.app.data.dao.ConnectionDao
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import foundation.openstore.signer.app.data.dao.LocalCredential
import foundation.openstore.signer.app.data.dao.CredentialDao
import foundation.openstore.signer.app.data.dao.CredentialEntity
import foundation.openstore.signer.app.data.dao.ExchangeDao
import foundation.openstore.signer.app.data.dao.ExchangeSession
import foundation.openstore.signer.app.data.dao.SigningDao
import foundation.openstore.signer.app.data.dao.SigningEntity
import foundation.openstore.signer.app.data.dao.SigningTarget
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.data.dao.WalletDao
import foundation.openstore.signer.app.data.dao.WalletEntity
import foundation.openstore.signer.app.data.dao.WalletWithConnections
import foundation.openstore.signer.app.data.mnemonic.EntropySize
import foundation.openstore.signer.app.data.mnemonic.Mnemonic
import foundation.openstore.signer.app.data.mnemonic.MnemonicRepository
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.utils.currentTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class WalletInteractorDefault(
    private val connectionCredentialDao: ConnectionCredentialDao,
    private val exchangerDao: ExchangeDao,
    private val connectionDao: ConnectionDao,
    private val credentialDao: CredentialDao,
    private val walletDao: WalletDao,
    private val signingDao: SigningDao,
    private val mnemonicRepository: MnemonicRepository,
    private val encryptionCoder: GcipEncryptionCoder,
) : WalletInteractor {

    private val wallets = Relay<GcipId>()

    override val onWalletChanged: Flow<WalletWithConnections> get() {
        return wallets.events
            .mapNotNull { walletId ->
                walletDao.findWithConnectionCount(walletId)
            }
    }

    private val baseDerivations: Set<String> by lazy {
        Blockchain.entries.map { it.derivationPath }.toSet()
    }

    override suspend fun createMnemonic(): Mnemonic {
        return mnemonicRepository.create(EntropySize.B128)
    }

    override suspend fun createWallet(name: String, store: SecureStore): WalletEntity {
        val mnemonic = createMnemonic()
        return mnemonic.use { mnemonic ->
            internalImport(name, mnemonic, isVerified = false, store)
                .also { wallets.emit(it.id) }
        }
    }

    override suspend fun importWallet(
        name: String,
        mnemonic: Mnemonic,
        store: SecureStore,
        isVerified: Boolean,
    ): WalletEntity {
        return internalImport(name, mnemonic, isVerified = isVerified, store)
            .also { wallets.emit(it.id) }
    }

    override suspend fun verify(walletId: GcipId) {
        walletDao.verify(walletId, true)
        wallets.emit(walletId)
    }

    private suspend fun internalImport(
        name: String,
        mnemonic: Mnemonic,
        isVerified: Boolean,
        store: SecureStore,
    ): WalletEntity {
        val wallet = store.encryptMnemonic(mnemonic).use { encryptedMnemonic ->
            WalletEntity(
                id = GcipId.generate(),
                initials = Emoji.get(name),
                name = name,
                encryptedMnemonic = encryptedMnemonic,
                isVerified = isVerified,
                createdAt = currentTime(),
            ).also { walletDao.insert(it) }
        }

        val credentials = mnemonicRepository.seed(mnemonic).use { seed ->
            Blockchain.entries.map { chain ->
                Cryptography.PrivateKey.fromPath(seed = seed, algorithm = chain.curve, path = chain.derivationPath)
                   .use { pk ->
                        CredentialEntity(
                            id = GcipId.generate(),
                            walletId = wallet.id,
                            algorithm = chain.curve,
                            derivationPath = chain.derivationPath,
                            payload = Cryptography.pubKey(pk, chain.curve).toUrlBase64Fmt(),
                            type = GcipCredentialType.PublicKey
                        )
                    }
            }
        }

        credentialDao.insertAll(credentials)

        return wallet
    }

    override suspend fun hasWallets(): Boolean {
        return walletDao.count() > 0
    }

    override suspend fun updateWallet(wallet: WalletEntity) {
        walletDao.insert(wallet)
        wallets.emit(wallet.id)
    }

    override suspend fun getWallets(): List<WalletEntity> {
        return walletDao.findAll()
    }

    override suspend fun getWallestWithConnections(): List<WalletWithConnections> {
        return walletDao.findAllWithConnectionCount()
    }

    override suspend fun getMnemonic(walletId: GcipId, store: SecureStore): Mnemonic {
        val wallet = walletDao.findById(walletId) ?: throw IllegalStateException("Wallet not found")
        return store.decryptMnemonic(wallet.encryptedMnemonic)
    }

    override suspend fun findTransactionsBy(walletId: GcipId, limit: Int?): List<Transaction> {
        return when (limit) {
            null -> signingDao.findAllTransactions(walletId)
            else -> signingDao.findTransactions(walletId, limit)
        }
    }

    override suspend fun findConnectionsBy(walletId: GcipId, limit: Int?): List<ConnectionEntity> {
        return when (limit) {
            null -> connectionDao.findAllByWalletId(walletId)
            else -> connectionDao.findByWalletId(walletId, limit)
        }
    }

    override suspend fun findBaseCredentialsBy(walletId: GcipId): Map<Blockchain, LocalCredential> {
        val blockchains = Blockchain.entries
            .associateBy { it.derivationPath }

        val derivations = blockchains.keys

        return credentialDao.findAllByWallet(walletId, derivations)
            .associateBy { blockchains[it.derivationPath]!! } // TODO
    }

    override suspend fun findCredentialBy(
        walletId: GcipId,
        credentialId: GcipId
    ): LocalCredential? {
        return credentialDao.findByIdAndWallet(credentialId, walletId)
    }

    override suspend fun findCredentialsByConnection(connectionId: GcipId): List<LocalCredential> {
        return connectionCredentialDao.findCredentialsByConnection(connectionId)
    }

    override suspend fun hasCredentialAccess(connectionId: GcipId, credentialId: GcipId): Boolean {
        return connectionCredentialDao.hasAccess(connectionId, credentialId)
    }

    override suspend fun findWalletBy(id: GcipId): WalletEntity? {
        return walletDao.findById(id)
    }

    override suspend fun findConnectionBy(id: GcipId): ConnectionEntity? {
        return connectionDao.findById(id)
    }

    override suspend fun deleteWallet(walletId: GcipId) {
        walletDao.delete(walletId) // cascade
    }

    override suspend fun exchange(request: SignerRequest.Exchange): GcipResult<CommonResponse.Exchange> {
        return GcipResult.ok(
            CommonResponse.Exchange(
                block = request.block,
                encryption = handshake(
                    encryption = request.encryption,
                    transport = request.transport,
                    meta = request.meta,
                    connectionId = null,
                ),
                meta = null,
            )
        )
    }

    override suspend fun connect(
        request: SignerRequest.Connect,
        store: SecureStore,
        wallet: WalletEntity,
    ): GcipResult<CommonResponse.Connect> {
        val connection = ConnectionEntity(
            id = GcipId.generate(),
            serviceName = request.clientData.name,
            serviceOrigin = request.clientData.origin,
            callerId = request.callerData.id,
            callerScheme = request.callerData.scheme?.value,
            callerSignature = request.callerData.signature,
            walletId = wallet.id,
            meta = request.meta?.payload,
            createdAt = currentTime(),
        )
        connectionDao.insert(connection)

        val resultCredentials = createNewCredentials(store, wallet, request.derivations)
        createCredentialBindings(connection.id, resultCredentials)

        wallets.emit(wallet.id)
        var clientData = CommonResponse.Connect.ConnectData(
            connectionId = connection.id,
            signerData = request.signerData,
            wallets = listOf(
                Wallet(
                    namespace = wallet.name,
                    credentials = resultCredentials.map { it.toCredentialResponse() }
                )
            ),
            connectionType = GcipConnectionType.Device,
        )

        val connect = when (val encryption = request.encryption) {
            is Encryption.Handshake.Request -> {
                CommonResponse.Connect.Handshake(
                    block = request.block,
                    encryption = handshake(
                        encryption = encryption,
                        transport = Transport.Internal,
                        connectionId = connection.id,
                        meta = request.meta,
                    ),
                    data = clientData,
                )
            }

            is Encryption.Session -> {
                val exchange = exchangerDao.findExchangeById(encryption.eid)
                    ?: return GcipResult.err(GcipStatus.UnknownSession)

                if (exchange.connectionId != null) {
                    return GcipResult.err(GcipStatus.UnknownSession)
                }

                if (exchange.transport != GcipTransportType.Internal) {
                    clientData = clientData.copy(connectionType = GcipConnectionType.CrossDevice)
                }

                exchangerDao.setConnection(connection.id, connection.id) // TODO cleanup on disconnect

                CommonResponse.Connect.Session(
                    block = request.block,
                    encryption = encryption,
                    data = clientData,
                )
            }
        }

        return GcipResult.ok(connect)
    }

    private suspend fun handshake(
        encryption: Encryption.Handshake.Request,
        transport: Transport,
        connectionId: GcipId?,
        meta: Meta?,
    ): Encryption.Handshake.Response {
        val eid = GcipId.generate()
        val key = encryptionCoder.generateExchangePair(encryption.key.algo)
        val sessionKey = KeyPair(key.pk, encryption.key.payload).use { key ->
            encryptionCoder.generateSessionKey(eid, key)
        }

        val exchange = ExchangeSession(
            eid = eid,
            connectionId = connectionId,
            sessionKey = sessionKey,
            transport = transport.toGcipTransportType(),
            transportData = transport.peerData,
            meta = meta?.payload,
            createdAt = currentTime(),
        )

        exchangerDao.save(exchange)

        return Encryption.Handshake.Response(
            eid = eid,
            key = ExchangeKey(payload = key.pub, algo = key.algo),
        )
    }

    override suspend fun disconnect(connectionId: GcipId): Boolean {
        val connection = connectionDao.findById(connectionId) ?: return false
        val deleted = connectionDao.delete(connectionId) > 0
        if (deleted) {
            connectionCredentialDao.deleteOrphanedCredentials(connection.walletId, baseDerivations)
            wallets.emit(connection.walletId)
        }
        return deleted
    }

    override suspend fun sign(
        request: SignerRequest.Sign,
        store: SecureStore,
        wallet: WalletEntity,
        connection: ConnectionEntity,
        credential: LocalCredential,
    ): GcipResult<CommonResponse.Sign> {
        val signingData = request.challenge.transformData()
        val derivationType = credential.derivationType

        val signature = store.decryptMnemonic(wallet.encryptedMnemonic).use { mnemonic ->
            mnemonicRepository.seed(mnemonic).use { seed ->
                Cryptography.privateKey(derivationType, seed).use { pk ->
                    Cryptography.sign(pk, data = signingData, algorithm = credential.algorithm).use {
                        it
                    }
                }
            }
        }


        val entity = SigningEntity(
            sequenceId = null,
            signingId = GcipId.generate(),
            blockId = request.blockId,
            challenge = request.challenge.displayData,
            connectionId = request.connectionId,
            credentialId = request.credentialId,
            walletId = wallet.id,
            method = SigningTarget.Default,
            challengeTransforms = request.challenge.transforms,
            challengeFormat = request.challenge.format,
            meta = request.meta?.payload,
            createdAt = currentTime(),
        )

        signingDao.insert(entity)

        return GcipResult.ok(
            CommonResponse.Sign(
                block = request.block,
                encryption = request.encryption,
                signingId = entity.signingId,
                signature = signature,
            )
        )
    }
    override suspend fun extend(
        request: SignerRequest.Extend,
        store: SecureStore,
        wallet: WalletEntity,
        connection: ConnectionEntity
    ): GcipResult<CommonResponse.Extend> {
        val resultCredentials = createNewCredentials(store, wallet, request.derivations)
        createCredentialBindings(connection.id, resultCredentials)

        return GcipResult.ok(
            CommonResponse.Extend(
                block = request.block,
                encryption = request.encryption,
                connectionId = request.connectionId,
                wallets = listOf(
                    Wallet(credentials = resultCredentials.map { it.toCredentialResponse() })
                )
            )
        )
    }

    private suspend fun createNewCredentials(
        store: SecureStore,
        wallet: WalletEntity,
        requests: List<CredentialRequest>,
    ): MutableList<CredentialProof> {
        val resultCredentials = mutableListOf<CredentialProof>()
        val newEntities = mutableListOf<CredentialEntity>()

        val credentials = credentialDao.findAllByWallet(wallet.id)
            .associateBy { it.derivationType.displayKey }

        store.decryptMnemonic(wallet.encryptedMnemonic).use { mnemonic ->
            mnemonicRepository.seed(mnemonic).use { seed ->
                for (req in requests) {
                    for (type in req.credentials) {
                        Cryptography.privateKey(type, seed).use { privateKey ->
                            val pubKey = Cryptography.pubKey(privateKey, type.algo)
                                .toUrlBase64Fmt()

                            val entity = credentials.getOrElse(type.displayKey) {
                                LocalCredential(
                                    CredentialEntity(
                                        id = GcipId.generate(),
                                        walletId = wallet.id,
                                        algorithm = type.algo,
                                        payload = pubKey,
                                        derivationPath = type.derivation,
                                        type = req.type
                                    )
                                ).also { newEntities.add(it.entity) }
                            }

                            resultCredentials.add(
                                CredentialProof(
                                    entity = entity,
                                    pubKey = entity.payload
                                )
                            )
                        }
                    }
                }
            }
        }

        credentialDao.insertAll(newEntities)
        return resultCredentials
    }

    private suspend fun createCredentialBindings(
        connectionId: GcipId,
        credentials: List<CredentialProof>
    ) {
        val bindings = credentials.map { proof ->
            ConnectionCredentialEntity(
                connectionId = connectionId,
                credentialId = proof.entity.id
            )
        }
        connectionCredentialDao.insertAll(bindings)
    }

    private class CredentialProof(
        val entity: LocalCredential,
        val pubKey: String,
    ) {
        fun toCredentialResponse(): Credential {
            return Credential(
                id = entity.id,
                pubkey = pubKey,
                derivation = entity.derivationType
            )
        }
    }

    private fun Challenge.transformData(): ByteArray {
        var result = rawData
        for (transform in transforms) {
            result = GcipTransformer.hash(result, transform)
        }
        return result
    }
}
