package foundation.openstore.signer.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import foundation.openstore.signer.app.data.dao.SignerDatabase
import org.w3c.dom.Worker

actual class DriverFactory(private val worker: Worker) {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(worker)
            .also { SignerDatabase.Schema.create(it) } // TODO await
    }
}
