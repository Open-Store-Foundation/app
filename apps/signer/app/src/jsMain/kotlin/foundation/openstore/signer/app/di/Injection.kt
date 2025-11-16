package foundation.openstore.signer.app.di

import foundation.openstore.signer.app.screens.SignerInjector
import org.openwallet.kitten.core.ComponentProvider
import org.openwallet.kitten.core.Kitten

object SignerInjection {
    fun init() {
        Kitten.init(
            provider = SignerProvider()
        ) { deps ->
            register(SignerInjector) { deps.walletCmp }
        }
    }
}

class SignerProvider : ComponentProvider() {

    val mdlCmp: StorageComponent
        get() = singleOwner {
            WebStorageComponent()
        }

    val dataCmp: DataComponent
        get() = singleOwner {
            DataComponentDefault(storageCmp = mdlCmp)
        }

    val walletCmp: SignerComponent
        get() = multiOwner {
            SignerComponentDefault(dataCmp)
        }
}
