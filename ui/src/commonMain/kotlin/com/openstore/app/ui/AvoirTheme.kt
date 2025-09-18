package com.openstore.app.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.openstore.app.log.L
import com.openstore.app.ui.colors.DarkColorScheme
import com.openstore.app.ui.colors.LightColorScheme
import com.openstore.app.ui.font.AvoirFonts

@Composable
fun AvoirTheme(
    theme: AppTheme = rememberAppTheme().value,
    content: @Composable () -> Unit,
) {
    val colors = remember(theme) {
        when (theme) {
            AppTheme.Dark -> DarkColorScheme
            AppTheme.Light -> LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AvoirFonts.AvoirTypography(),
    ) {
        CompositionLocalProvider(LocalAppTheme provides theme) {
            CompositionLocalProvider(
                LocalTextStyle provides AvoirFonts.DefaultTextStyle(),
            ) {
                content()
            }
        }
    }
}

