package foundation.openstore.signer.app.di

import foundation.openstore.signer.app.IosApp
import foundation.openstore.signer.app.MainViewModel
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponent
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponentDefault
import foundation.openstore.signer.app.screens.gcip.GcipInjector
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten

object SignerInjection {
    fun init(config: IosApp.Config) {
        Kitten.init(
            registry = SignerComponentRegistry(config)
        ) {
            create { dataCmp }

            register(ActivityInjector) { mainCmp }
            register(GcipInjector) { gcipFeatureCmp }
            register(SignerInjector) { walletCmp }
        }
    }
}

class SignerComponentRegistry(
    private val config: IosApp.Config
) : ComponentRegistry() {

    val storageCmp: StorageComponent by singleton {
        ModulesComponentDefault(appGroupId = config.groupId)
    }

    val gcipCmp: GcipComponent by singleton {
        GcipComponentDefault(
            storageCmp = storageCmp,
            normalizedBundleId = config.bundleId
        )
    }

    val dataCmp: DataComponent by singleton {
        DataComponentDefault(storageCmp = storageCmp, gcipCmp = gcipCmp)
    }

    val walletCmp: SignerComponent by shared {
        SignerComponentDefault(dataCmp)
    }

    val gcipFeatureCmp: GcipFeatureComponent by shared {
        GcipFeatureComponentDefault(dataCmp, gcipCmp)
    }

    val mainCmp: MainComponent by shared<MainComponent> {
        object : MainComponent {
            override fun provideMainViewModel(): MainViewModel {
                return MainViewModel()
            }
        }
    }
}






