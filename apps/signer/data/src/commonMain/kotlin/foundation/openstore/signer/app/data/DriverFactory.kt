package foundation.openstore.signer.app.data

import app.cash.sqldelight.db.SqlDriver
import foundation.openstore.signer.app.data.adapters.AlgorithmAdapter
import foundation.openstore.signer.app.data.adapters.BinaryFormatAdapter
import foundation.openstore.signer.app.data.adapters.CredentialTypeAdapter
import foundation.openstore.signer.app.data.adapters.GcipIdAdapter
import foundation.openstore.signer.app.data.adapters.GcipTransportTypeAdapter
import foundation.openstore.signer.app.data.adapters.InstantAdapter
import foundation.openstore.signer.app.data.adapters.SigningTargetAdapter
import foundation.openstore.signer.app.data.adapters.TransformAlgorithmListAdapter
import foundation.openstore.signer.app.data.dao.ConnectionCredentialEntity
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import foundation.openstore.signer.app.data.dao.CredentialEntity
import foundation.openstore.signer.app.data.dao.ExchangeEntity
import foundation.openstore.signer.app.data.dao.SignerDatabase
import foundation.openstore.signer.app.data.dao.SigningEntity
import foundation.openstore.signer.app.data.dao.WalletEntity

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): SignerDatabase {
    val driver = driverFactory.createDriver()
    return SignerDatabase(
        driver,
        ConnectionEntityAdapter = ConnectionEntity.Adapter(
            createdAtAdapter = InstantAdapter,
            idAdapter = GcipIdAdapter,
            walletIdAdapter = GcipIdAdapter,
        ),
        CredentialEntityAdapter = CredentialEntity.Adapter(
            algorithmAdapter = AlgorithmAdapter,
            typeAdapter = CredentialTypeAdapter,
            idAdapter = GcipIdAdapter,
            walletIdAdapter = GcipIdAdapter,
        ),
        SigningEntityAdapter = SigningEntity.Adapter(
            challengeFormatAdapter = BinaryFormatAdapter,
            challengeTransformsAdapter = TransformAlgorithmListAdapter,
            methodAdapter = SigningTargetAdapter,
            createdAtAdapter = InstantAdapter,
            walletIdAdapter = GcipIdAdapter,
            signingIdAdapter = GcipIdAdapter,
            connectionIdAdapter = GcipIdAdapter,
            credentialIdAdapter = GcipIdAdapter,
        ),
        ExchangeEntityAdapter = ExchangeEntity.Adapter(
            transportAdapter = GcipTransportTypeAdapter,
            eidAdapter = GcipIdAdapter,
            connectionIdAdapter = GcipIdAdapter,
            createdAtAdapter = InstantAdapter,
        ),
        ConnectionCredentialEntityAdapter = ConnectionCredentialEntity.Adapter(
            connectionIdAdapter = GcipIdAdapter,
            credentialIdAdapter = GcipIdAdapter
        ),
        WalletEntityAdapter = WalletEntity.Adapter(
            idAdapter = GcipIdAdapter,
            createdAtAdapter = InstantAdapter,
        )
    )
}
