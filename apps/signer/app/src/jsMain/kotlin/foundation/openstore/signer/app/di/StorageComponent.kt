package foundation.openstore.signer.app.di

import com.openstore.app.store.common.store.StorageModule
import foundation.openstore.signer.app.data.DriverFactory
import foundation.openstore.signer.app.data.createDatabase
import foundation.openstore.signer.app.data.dao.AppDatabase
import org.openwallet.kitten.core.depLazy

class WebStorageComponent : StorageComponent {

    private val storageModule by depLazy {
        StorageModule()
    }

    override val keyValueFactory by depLazy {
        storageModule.keyValueFactory
    }

    override val appDatabase: AppDatabase by depLazy {
        AppDatabase(
            createDatabase(
                DriverFactory(
                    js("new Worker(new URL('@cashapp/sqldelight-sqljs-worker/sqljs.worker.js', import.meta.url))")
                )
            )
        )
    }
}
