package com.openstore.app.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.AppTheme
import com.openstore.app.ui.AvoirTheme
import com.openstore.app.ui.component.AvoirSurface

@Composable
fun ThemePreview(
    isUseTheme: Boolean = true,
    contentFirst: @Composable () -> Unit,
) = ThemePreview(listOf(contentFirst), isUseTheme)

@Composable
fun ThemePreview(
    contents: List<@Composable () -> Unit>,
    isUseTheme: Boolean = true,
) {
    LazyColumn {
        for (content in contents) {
            item {
                Column {
                    if (isUseTheme) {
                        LightSurface {
                            content()
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    DarkSurface {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkSurface(
    content: @Composable () -> Unit
) {
    AvoirTheme(theme = AppTheme.Dark) {
        AvoirSurface {
            content()
        }
    }
}

@Composable
private fun LightSurface(
    content: @Composable () -> Unit
) {
    AvoirTheme(theme = AppTheme.Light) {
        AvoirSurface {
            content()
        }
    }
}