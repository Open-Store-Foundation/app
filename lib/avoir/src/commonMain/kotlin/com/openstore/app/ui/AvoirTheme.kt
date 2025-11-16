package com.openstore.app.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.openstore.app.ui.colors.FirestormDarkColorScheme
import com.openstore.app.ui.colors.FirestormLightColorScheme
import com.openstore.app.ui.font.AvoirFonts

@Composable
fun AvoirTheme(
    theme: AppTheme? = null,
    colors: AppColorScheme = AppColorScheme.Frozen,
    content: @Composable () -> Unit,
) {
    val appTheme = theme ?: rememberAppTheme().value

    val colors = remember(appTheme) {
        when (appTheme) {
            AppTheme.Dark -> colors.dark
            AppTheme.Light -> colors.light
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AvoirFonts.AvoirTypography(),
    ) {
        CompositionLocalProvider(
            LocalAppTheme provides appTheme,
            LocalTextStyle provides AvoirFonts.DefaultTextStyle(),
        ) {
            content()
        }
    }
}

