package foundation.openstore.signer.app.di

import com.openstore.app.store.common.store.PlatformKeyValueFactory
import com.openstore.app.store.common.store.StorageModule
import foundation.openstore.signer.app.data.DriverFactory
import foundation.openstore.signer.app.data.createDatabase
import foundation.openstore.signer.app.data.dao.AppDatabase
import foundation.openstore.kitten.api.deps.depLazy
import platform.Foundation.NSFileManager

class ModulesComponentDefault(
    private val appGroupId: String,
) : StorageComponent {

    private val storageModule: StorageModule by depLazy {
        StorageModule(appGroupId = appGroupId)
    }

    override val appDatabase: AppDatabase by depLazy {
        AppDatabase(
            createDatabase(
                DriverFactory(
                    name = "signer.db",
                    path = sharedContainerDirectory(appGroupId)
                )
            )
        )
    }

    override val keyValueFactory: PlatformKeyValueFactory by depLazy {
        storageModule.keyValueFactory
    }

    private fun sharedContainerDirectory(
        appGroupId: String,
    ): String {
        val containerUrl = NSFileManager.defaultManager.containerURLForSecurityApplicationGroupIdentifier(appGroupId)
        return requireNotNull(containerUrl?.path) { "App Group '$appGroupId' not configured" }
    }

}


