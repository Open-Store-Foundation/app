package foundation.openstore.signer.app.screens.gcip

import foundation.openstore.gcip.platform.GcipDataBundle
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector
import foundation.openstore.signer.app.di.DataComponent
import foundation.openstore.signer.app.di.GcipComponent

interface GcipFeatureComponent : Component {
    fun provideGcipFeature(bundle: GcipDataBundle): GcipViewModel
}

class GcipFeatureComponentDefault(
    private val dataCmp: DataComponent,
    private val gcipCmp: GcipComponent,
) : GcipFeatureComponent {

    override fun provideGcipFeature(bundle: GcipDataBundle): GcipViewModel {
        return GcipViewModel(
            bundle = bundle,
            settingsRepository = dataCmp.walletSettingsRepository,
            walletInteractor = dataCmp.walletInteractor,
            comparator = gcipCmp.originComparator,
            signerHandler = gcipCmp.signerHandler,
        )
    }
}

object GcipInjector : Injector<GcipFeatureComponent>()
