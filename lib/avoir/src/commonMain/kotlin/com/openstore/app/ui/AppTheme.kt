package com.openstore.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.openstore.app.ui.colors.DarkColorScheme
import com.openstore.app.ui.colors.FirestormDarkColorScheme
import com.openstore.app.ui.colors.FirestormLightColorScheme
import com.openstore.app.ui.colors.LightColorScheme

enum class AppColorScheme(
    val light: ColorScheme,
    val dark: ColorScheme,
) {
    Frozen(LightColorScheme, DarkColorScheme),
    Firestorm(FirestormLightColorScheme, FirestormDarkColorScheme),
    ;
}

enum class AppTheme {
    Light,
    Dark,
}

private val globalTheme = mutableStateOf(AppTheme.Light)

fun setAppTheme(theme: AppTheme) {
    globalTheme.value = theme
}

@Composable
@ReadOnlyComposable
fun systemAppTheme(): AppTheme {
    val isSystemDark = isSystemInDarkTheme()
    return if (isSystemDark) AppTheme.Dark else AppTheme.Light
}

@Composable
fun rememberAppTheme(): State<AppTheme> {
    return remember { globalTheme }
}

// Main Additional
var LocalAppTheme = staticCompositionLocalOf { AppTheme.Light }
