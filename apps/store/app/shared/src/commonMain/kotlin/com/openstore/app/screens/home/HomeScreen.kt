package com.openstore.app.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import foundation.openstore.kitten.android.withStatelessViewModel
import foundation.openstore.kitten.android.withViewModel

@Composable
fun HomeScreen() {
    val navigator = rememberNavController()

    val feature = StoreInjector.withStatelessViewModel { provideHomeFeature() }
    val feed by feature.state.name.observeSafeState()
}
