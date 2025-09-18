package com.openstore.app.features.catalog.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.features.catalog.CatalogInjector
import foundation.openstore.kitten.android.withViewModel

@Composable
fun CatalogHomeScreen() {
    val navigator = rememberNavController()
    val feature = CatalogInjector.withViewModel { provideHomeFeature() }
    val data = feature.state.name.observeSafeState()

    Column {
        Text("Hello ${data.value}")
    }
}
