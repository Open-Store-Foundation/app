package foundation.openstore.signer.app.di

import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten

object SignerInjection {
    fun init() {
        Kitten.init(
            registry = SignerRegistry()
        ) {
            register(SignerInjector) { walletCmp }
        }
    }
}

class SignerRegistry : ComponentRegistry() {

    val mdlCmp: StorageComponent by singleton {
        WebStorageComponent()
    }

    val dataCmp: DataComponent by singleton {
        DataComponentDefault(storageCmp = mdlCmp)
    }

    val walletCmp: SignerComponent by shared {
        SignerComponentDefault(dataCmp)
    }
}
