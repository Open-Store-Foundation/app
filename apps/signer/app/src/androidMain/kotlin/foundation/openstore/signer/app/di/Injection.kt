package foundation.openstore.signer.app.di

import android.app.Application
import foundation.openstore.signer.app.MainViewModel
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponent
import foundation.openstore.signer.app.screens.gcip.GcipFeatureComponentDefault
import foundation.openstore.signer.app.screens.gcip.GcipInjector
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten

object SignerInjection {
    fun init(app: Application) {
        Kitten.init(
            registry = SignerComponentRegistry(app)
        ) {
            create { dataCmp }

            register(ActivityInjector) { mainCmp }
            register(SignerInjector) { signerCmp }
            register(GcipInjector) { gcipFeatureCmp }
        }
    }
}

class SignerComponentRegistry(
    private val app: Application
) : ComponentRegistry() {

    val stgCmp: StorageComponent by singleton {
        ModulesComponentDefault(app)
    }

    val gcipCmp: GcipComponent by singleton {
        GcipComponentDefault(app = app, storageCmp = stgCmp)
    }

    val dataCmp: DataComponent by singleton {
        DataComponentDefault(
            storageCmp = stgCmp,
            gcipCmp = gcipCmp,
        )
    }

    val gcipFeatureCmp: GcipFeatureComponent by shared {
        GcipFeatureComponentDefault(dataCmp, gcipCmp)
    }

    val signerCmp: SignerComponent by shared {
        SignerComponentDefault(dataCmp, gcipCmp)
    }

    val mainCmp: MainComponent by shared<MainComponent> {
        object : MainComponent {
            override fun provideMainViewModel(): MainViewModel =
                MainViewModel(settingsRepo = dataCmp.walletSettingsRepository)
        }
    }
}
