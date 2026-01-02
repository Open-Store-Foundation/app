package foundation.openstore.signer.app.di

import com.openstore.app.store.common.store.PlatformKeyValueFactory
import foundation.openstore.kitten.api.Component
import foundation.openstore.signer.app.data.dao.AppDatabase

interface StorageComponent : Component {
    val keyValueFactory: PlatformKeyValueFactory
    val appDatabase: AppDatabase
}
