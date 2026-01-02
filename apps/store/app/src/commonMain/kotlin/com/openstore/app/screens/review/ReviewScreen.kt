package com.openstore.app.screens.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import foundation.openstore.kitten.android.withViewModel

@Composable
fun ReviewScreen() {
    val navigator = rememberNavController()

    val feature = StoreInjector.withViewModel { provideReviewFeature() }
    val feed by feature.state.name.observeSafeState()
}
