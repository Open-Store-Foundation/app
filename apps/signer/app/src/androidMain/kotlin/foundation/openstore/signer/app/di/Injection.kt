package foundation.openstore.signer.app.di

import android.app.Application
import com.openstore.app.core.root.DeviceRootProvider
import foundation.openstore.signer.app.MainViewModel
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponent
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponentDefault
import foundation.openstore.signer.app.screens.gcip.GcipInjector
import org.openwallet.kitten.core.ComponentProvider
import org.openwallet.kitten.core.Kitten

object SignerInjection {
    fun init(app: Application) {
        Kitten.init(
            provider = SignerComponentProvider(app)
        ) { deps ->
            create { deps.dataCmp }

            register(ActivityInjector) { deps.mainCmp }
            register(SignerInjector) { deps.walletCmp }
            register(GcipInjector) { deps.gcipFeatureCmp }
        }
    }
}

class SignerComponentProvider(
    private val app: Application
) : ComponentProvider() {

    val stgCmp: StorageComponent
        get() = singleOwner {
            ModulesComponentDefault(app)
        }

    val gcipCmp: GcipComponent
        get() = singleOwner {
            GcipComponentDefault(app = app, storageCmp = stgCmp)
        }

    val dataCmp: DataComponent
        get() = singleOwner {
            DataComponentDefault(
                storageCmp = stgCmp,
                gcipCmp = gcipCmp,
            )
        }

    val gcipFeatureCmp: GcipFeatureComponent
        get() = multiOwner {
            GcipFeatureComponentDefault(dataCmp, gcipCmp)
        }

    val walletCmp: SignerComponent
        get() = multiOwner {
            SignerComponentDefault(dataCmp)
        }

    val mainCmp: MainComponent
        get() = multiOwner<MainComponent> {
            object : MainComponent {
                override fun provideMainViewModel(): MainViewModel = MainViewModel(settingsRepo = dataCmp.walletSettingsRepository)
            }
        }
}
