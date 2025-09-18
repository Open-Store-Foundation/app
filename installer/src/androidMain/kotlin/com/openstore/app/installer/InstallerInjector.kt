package com.openstore.app.installer

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector

interface InstallerComponent : Component {
    fun provideServiceController(): ServiceController
}

object InstallerInjector : Injector<InstallerComponent>()
