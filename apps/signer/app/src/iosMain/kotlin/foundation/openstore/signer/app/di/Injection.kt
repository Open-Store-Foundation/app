package foundation.openstore.signer.app.di

import foundation.openstore.signer.app.IosApp
import foundation.openstore.signer.app.MainViewModel
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponent
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponentDefault
import foundation.openstore.signer.app.screens.gcip.GcipInjector
import org.openwallet.kitten.core.ComponentProvider
import org.openwallet.kitten.core.Kitten

object SignerInjection {
    fun init(config: IosApp.Config) {
        Kitten.init(
            provider = SignerComponentProvider(config)
        ) { deps ->
            create { deps.dataCmp }

            register(ActivityInjector) { deps.mainCmp }
            register(GcipInjector) { deps.gcipFeatureCmp }
            register(SignerInjector) { deps.walletCmp }
        }
    }
}

class SignerComponentProvider(
    private val config: IosApp.Config
) : ComponentProvider() {

    val storageCmp: StorageComponent
        get() = singleOwner {
            ModulesComponentDefault(appGroupId = config.groupId)
        }

    val gcipCmp: GcipComponent
        get() = singleOwner {
            GcipComponentDefault(
                storageCmp = storageCmp,
                normalizedBundleId = config.bundleId
            )
        }

    val dataCmp: DataComponent
        get() = singleOwner {
            DataComponentDefault(storageCmp = storageCmp, gcipCmp = gcipCmp)
        }

    val walletCmp: SignerComponent
        get() = multiOwner {
            SignerComponentDefault(dataCmp)
        }

    val gcipFeatureCmp: GcipFeatureComponent
        get() = multiOwner {
            GcipFeatureComponentDefault(dataCmp, gcipCmp)
        }

    val mainCmp: MainComponent
        get() = multiOwner<MainComponent> {
            object : MainComponent {
                override fun provideMainViewModel(): MainViewModel {
                    return MainViewModel()
                }
            }
        }
}






