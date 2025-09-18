package com.openstore.app.di

import com.openstore.app.MainViewModel
import com.openstore.app.data.settings.SettingsRepo
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector

interface MainComponent : Component {
    fun provideMainViewModel(): MainViewModel
}

object ActivityInjector : Injector<MainComponent>()