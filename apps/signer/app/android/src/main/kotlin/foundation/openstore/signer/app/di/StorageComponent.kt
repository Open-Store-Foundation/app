package foundation.openstore.signer.app.di

import android.app.Application
import com.openstore.app.store.common.store.StorageModule
import foundation.openstore.signer.app.data.DriverFactory
import foundation.openstore.signer.app.data.createDatabase
import foundation.openstore.signer.app.data.dao.AppDatabase
import foundation.openstore.kitten.api.deps.depLazy

class ModulesComponentDefault(
    private val app: Application
) : StorageComponent {

    private val storageModule by depLazy {
        StorageModule(app)
    }

    override val appDatabase by depLazy {
        AppDatabase(
            createDatabase(
                DriverFactory(app)
            )
        )
    }

    override val keyValueFactory by depLazy {
        storageModule.keyValueFactory
    }
}
