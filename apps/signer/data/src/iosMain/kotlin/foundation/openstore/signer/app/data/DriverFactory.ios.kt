package foundation.openstore.signer.app.data

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import foundation.openstore.signer.app.data.dao.SignerDatabase

actual class DriverFactory(
    private val name: String,
    private val path: String,
) {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SignerDatabase.Schema.synchronous(),
            name = name,
            onConfiguration = {
                it.copy(
                    extendedConfig = it.extendedConfig.copy(
                        foreignKeyConstraints = true,
                        basePath = path,
                    )
                )
            }
        )
    }
}
