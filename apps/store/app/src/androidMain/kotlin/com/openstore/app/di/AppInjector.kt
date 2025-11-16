package com.openstore.app.di

import com.openstore.app.installer.ServiceController
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector

interface AppComponent : Component {
    val installerController: ServiceController
}

object AppInjector : Injector<AppComponent>()
