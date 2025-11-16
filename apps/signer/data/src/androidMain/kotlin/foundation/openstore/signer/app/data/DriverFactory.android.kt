package foundation.openstore.signer.app.data

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import foundation.openstore.signer.app.data.dao.SignerDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SignerDatabase.Schema.synchronous(),
            context = context,
            name = "open-wallet.db",
            useNoBackupDirectory = true,
        )
    }
}
