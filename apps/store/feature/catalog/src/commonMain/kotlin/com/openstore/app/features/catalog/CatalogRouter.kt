package com.openstore.app.features.catalog

import kotlinx.serialization.Serializable

sealed interface CatalogRouter {
    @Serializable
    data object Home : CatalogRouter
}
