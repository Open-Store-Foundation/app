package foundation.openstore.signer.app.data.wallet

import foundation.openstore.gcip.core.Blockchain
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import foundation.openstore.signer.app.data.dao.LocalCredential
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.data.dao.WalletEntity
import foundation.openstore.signer.app.data.dao.WalletWithConnections
import foundation.openstore.signer.app.data.mnemonic.Mnemonic
import foundation.openstore.signer.app.data.passcode.SecureStore
import kotlinx.coroutines.flow.Flow

interface WalletInteractor {

    val onWalletChanged: Flow<WalletWithConnections>

    suspend fun getWallets(): List<WalletEntity>
    suspend fun getWallestWithConnections(): List<WalletWithConnections>
    suspend fun findWalletBy(id: GcipId): WalletEntity?
    suspend fun hasWallets(): Boolean
    suspend fun updateWallet(wallet: WalletEntity)

    suspend fun findConnectionBy(id: GcipId): ConnectionEntity?
    suspend fun findTransactionsBy(walletId: GcipId, limit: Int? = null): List<Transaction>
    suspend fun findConnectionsBy(walletId: GcipId, limit: Int? = null): List<ConnectionEntity>
    suspend fun findBaseCredentialsBy(walletId: GcipId): Map<Blockchain, LocalCredential>
    suspend fun findCredentialBy(walletId: GcipId, credentialId: GcipId): LocalCredential?
    suspend fun findCredentialsByConnection(connectionId: GcipId): List<LocalCredential>
    suspend fun hasCredentialAccess(connectionId: GcipId, credentialId: GcipId): Boolean
    suspend fun deleteWallet(walletId: GcipId)
    suspend fun disconnect(connectionId: GcipId): Boolean

    suspend fun createMnemonic(): Mnemonic
    suspend fun createWallet(name: String, store: SecureStore): WalletEntity
    suspend fun importWallet(name: String, mnemonic: Mnemonic, store: SecureStore, isVerified: Boolean): WalletEntity
    suspend fun verify(walletId: GcipId)
    suspend fun getMnemonic(walletId: GcipId, store: SecureStore): Mnemonic

    suspend fun exchange(
        request: SignerRequest.Exchange,
    ): GcipResult<CommonResponse.Exchange>

    suspend fun connect(
        request: SignerRequest.Connect,
        store: SecureStore,
        wallet: WalletEntity,
    ): GcipResult<CommonResponse.Connect>

    suspend fun sign(
        request: SignerRequest.Sign,
        store: SecureStore,
        wallet: WalletEntity,
        connection: ConnectionEntity,
        credential: LocalCredential,
    ): GcipResult<CommonResponse.Sign>

    suspend fun extend(
        request: SignerRequest.Extend,
        store: SecureStore,
        wallet: WalletEntity,
        connection: ConnectionEntity,
    ): GcipResult<CommonResponse.Extend>
}
