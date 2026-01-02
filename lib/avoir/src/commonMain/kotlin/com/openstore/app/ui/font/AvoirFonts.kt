package com.openstore.app.ui.font

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import foundation.openstore.avoir.generated.resources.Res
import foundation.openstore.avoir.generated.resources.open_sans_bold
import foundation.openstore.avoir.generated.resources.open_sans_light
import foundation.openstore.avoir.generated.resources.open_sans_medium
import foundation.openstore.avoir.generated.resources.open_sans_regular
import foundation.openstore.avoir.generated.resources.open_sans_semibold
import org.jetbrains.compose.resources.Font

object AvoirFonts {
    private val DefaultTypography = Typography()

    @Composable
    fun OpenSans(): FontFamily {
        return FontFamily(
            Font(Res.font.open_sans_light, FontWeight.Light),
            Font(Res.font.open_sans_regular, FontWeight.Normal),
            Font(Res.font.open_sans_medium, FontWeight.Medium),
            Font(Res.font.open_sans_semibold, FontWeight.SemiBold),
            Font(Res.font.open_sans_bold, FontWeight.Bold),
        )
    }

    @Composable
    fun DefaultTextStyle(): TextStyle {
        return TextStyle.Default.copy(fontFamily = OpenSans())
    }

    @Composable
    fun AvoirTypography(): Typography {
        val openSans = OpenSans()

        return Typography(
            displayLarge = DefaultTypography.displayLarge.copy(fontFamily = openSans),
            displayMedium = DefaultTypography.displayMedium.copy(fontFamily = openSans),
            displaySmall = DefaultTypography.displaySmall.copy(fontFamily = openSans),

            headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = openSans),
            headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = openSans),
            headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = openSans),

            titleLarge = DefaultTypography.titleLarge.copy(fontFamily = openSans, fontSize = 20.sp),
            titleMedium = DefaultTypography.titleMedium.copy(fontFamily = openSans),
            titleSmall = DefaultTypography.titleSmall.copy(fontFamily = openSans),

            bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = openSans),
            bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = openSans),
            bodySmall = DefaultTypography.bodySmall.copy(fontFamily = openSans),

            labelLarge = DefaultTypography.labelLarge.copy(fontFamily = openSans),
            labelMedium = DefaultTypography.labelMedium.copy(fontFamily = openSans),
            labelSmall = DefaultTypography.labelSmall.copy(fontFamily = openSans)
        )
    }
}
