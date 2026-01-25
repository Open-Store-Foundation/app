package com.openstore.app.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

private const val TransactionDuration = 100

@Composable
fun AvoirNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(
                animationSpec = tween(TransactionDuration, easing = LinearEasing)
            ) + slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(TransactionDuration, easing = LinearEasing),
            )
        },
        exitTransition = { ExitTransition.None },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(TransactionDuration, easing = LinearEasing)
            ) + slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(TransactionDuration, easing = LinearEasing),
            )
        },
        popEnterTransition = { EnterTransition.None },
        builder = builder,
    )
}


private const val DialogAnimationDuration = 300

@Composable
fun AvoirDialogNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    DialogAnimationDuration,
                    delayMillis = DialogAnimationDuration,
                    easing = LinearEasing
                )
            ) + expandVertically(
                animationSpec = tween(
                    DialogAnimationDuration,
                    delayMillis = DialogAnimationDuration,
                    easing = LinearEasing
                ),
                expandFrom = Alignment.Bottom
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    DialogAnimationDuration,
                    easing = LinearEasing
                )
            ) + shrinkVertically(
                animationSpec = tween(
                    DialogAnimationDuration,
                    easing = LinearEasing
                ),
                shrinkTowards = Alignment.Bottom
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(
                    DialogAnimationDuration,
                    easing = LinearEasing
                )
            ) + shrinkVertically(
                animationSpec = tween(
                    DialogAnimationDuration,
                    easing = LinearEasing
                ),
                shrinkTowards = Alignment.Bottom
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    DialogAnimationDuration,
                    delayMillis = DialogAnimationDuration,
                    easing = LinearEasing
                )
            ) + expandVertically(
                animationSpec = tween(
                    DialogAnimationDuration,
                    delayMillis = DialogAnimationDuration,
                    easing = LinearEasing
                ),
                expandFrom = Alignment.Bottom
            )
        },
        builder = builder,
    )
}
