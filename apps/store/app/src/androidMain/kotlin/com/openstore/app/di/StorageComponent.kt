package com.openstore.app.di

import android.app.Application
import com.openstore.app.data.db.AppDatabase
import com.openstore.app.db.getAppDatabase
import com.openstore.app.store.common.store.PlatformKeyValueFactory
import com.openstore.app.store.common.store.StorageModule
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.deps.depLazy

interface StorageComponent : Component {
    val keyValueFactory: PlatformKeyValueFactory
    val appDatabase: AppDatabase
}

class ModulesComponentDefault(
    private val app: Application
) : StorageComponent {

    private val storageModule by depLazy {
        StorageModule(app)
    }

    override val appDatabase by depLazy {
        getAppDatabase(app)
    }

    override val keyValueFactory by depLazy {
        storageModule.keyValueFactory
    }
}
