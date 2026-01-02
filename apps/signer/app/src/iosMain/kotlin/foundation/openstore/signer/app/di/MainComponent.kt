package foundation.openstore.signer.app.di

import foundation.openstore.signer.app.MainViewModel
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector

interface MainComponent : Component {
    fun provideMainViewModel(): MainViewModel
}

object ActivityInjector : Injector<MainComponent>()
