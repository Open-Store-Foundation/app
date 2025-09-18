package com.openstore.app.features.catalog

import com.openstore.app.features.catalog.screens.home.CatalogHomeFeature
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector

interface CatalogComponent : Component {
    fun provideHomeFeature(): CatalogHomeFeature
}

object CatalogInjector : Injector<CatalogComponent>()
