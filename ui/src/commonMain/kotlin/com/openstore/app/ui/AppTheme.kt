package com.openstore.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

enum class AppTheme {
    Light,
    Dark,
}

private val globalTheme = mutableStateOf(AppTheme.Light)

fun setAppTheme(theme: AppTheme) {
    globalTheme.value = theme
}

@Composable
fun systemAppTheme(): AppTheme {
    val isSystemDark = isSystemInDarkTheme()
    return if (isSystemDark) AppTheme.Dark else AppTheme.Light
}

@Composable
fun rememberAppTheme(): State<AppTheme> {
    return remember { globalTheme }
}

// Main Additional
var LocalAppTheme = compositionLocalOf { AppTheme.Light }
