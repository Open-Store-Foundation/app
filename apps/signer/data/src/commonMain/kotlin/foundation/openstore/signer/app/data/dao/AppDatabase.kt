package foundation.openstore.signer.app.data.dao

class AppDatabase(private val database: SignerDatabase) {
    fun connectionCredentialDao(): ConnectionCredentialDao = ConnectionCredentialDaoImpl(database)
    fun connectionDao(): ConnectionDao = ConnectionDaoImpl(database)
    fun credentialDao(): CredentialDao = CredentialDaoImpl(database)
    fun signingDao(): SigningDao = SigningDaoImpl(database)
    fun exchangeDao(): ExchangeDao = ExchangeDaoImpl(database)
    fun walletDao(): WalletDao = WalletDaoImpl(database)
}
