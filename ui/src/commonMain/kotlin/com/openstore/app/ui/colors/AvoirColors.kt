package com.openstore.app.ui.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.openstore.app.ui.AppTheme
import com.openstore.app.ui.LocalAppTheme

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3375BB), 
    onPrimary = Color(0xFFFFFFFF), 
    
    primaryContainer = Color(0xFFD3E4FF), 
    onPrimaryContainer = Color(0xFF001C38),
    
    secondary = Color(0xFF545F70), 
    onSecondary = Color(0xFFFFFFFF), 
    secondaryContainer = Color(0xFFD7E3F8), 
    onSecondaryContainer = Color(0xFF101C2B),
    
    tertiary = Color(0xFF6C5677), 
    onTertiary = Color(0xFFFFFFFF), 
    tertiaryContainer = Color(0xFFF5D9FF), 
    onTertiaryContainer = Color(0xFF261431), 

    error = Color(0xFFBA1A1A), 
    onError = Color(0xFFFFFFFF), 
    errorContainer = Color(0xFFFFDAD6), 
    onErrorContainer = Color(0xFF410002), 

    background = Color(0xFFFDFCFF), 
    onBackground = Color(0xFF1A1C1E), 

    surface = Color(0xFFFDFCFF), 
    onSurface = Color(0xFF1A1C1E), 

    surfaceVariant = Color(0xFFDFE2EB), 
    onSurfaceVariant = Color(0xFF43474E), 

    surfaceContainer = Color(0xFFECE7EE),

    outline = Color(0xFF73777F), 
    outlineVariant = Color(0xFFDBDCE5), 

    scrim = Color(0x99000000), 

    
    inverseSurface = Color(0xFF1B1B1C), 
    inverseOnSurface = Color(0xFFEAEAEA), 
    inversePrimary = Color(0xFF48FF91), 

    surfaceTint = Color(0xFF0500FF) 
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA2C9FF), 
    onPrimary = Color(0xFF00315B), 
    
    primaryContainer = Color(0xFF004881), 
    onPrimaryContainer = Color(0xFFD3E4FF),
    
    secondary = Color(0xFFBBC7DB), 
    onSecondary = Color(0xFF263141), 
    secondaryContainer = Color(0xFF3C4758), 
    onSecondaryContainer = Color(0xFFD7E3F8),
    
    tertiary = Color(0xFFD8BDE3), 
    onTertiary = Color(0xFF3C2947), 
    tertiaryContainer = Color(0xFF543F5E), 
    onTertiaryContainer = Color(0xFFF5D9FF), 

    error = Color(0xFFFFB4AB), 
    onError = Color(0xFF690005), 
    errorContainer = Color(0xFF93000A), 
    onErrorContainer = Color(0xFFFFB4AB), 

    background = Color(0xFF1A1C1E), 
    onBackground = Color(0xFFE3E2E6), 

    surface = Color(0xFF1A1C1E), 
    onSurface = Color(0xFFE3E2E6), 

    surfaceVariant = Color(0xFF43474E), 
    onSurfaceVariant = Color(0xFFC3C6CF), 

    surfaceContainer = Color(0xFF262A2D),

    outline = Color(0xFF8D9199), 
    outlineVariant = Color(0xFF434C5A), 

    scrim = Color(0x99000000), 

    inverseSurface = Color(0xFFFFFFFF), 
    inverseOnSurface = Color(0xFF252525), 
    inversePrimary = Color(0xFF0500FF), 

    surfaceTint = Color(0xFF48FF91) 
)


object CustomColors {
    val AchievementDark = Color(0xFFFFEB3B)
    val AchievementLight = Color(0xFFFF9800)
}

val ColorScheme.achievementColor: Color
    @Composable
    @ReadOnlyComposable
    get() {
        return when (LocalAppTheme.current) {
            AppTheme.Dark -> CustomColors.AchievementDark
            AppTheme.Light -> CustomColors.AchievementLight
        }
    }



interface AvoirColorSet {
    val containerColor: Color
    val primaryColor: Color
    val secondaryColor: Color
    val accentColor: Color
    val surfaceColor: Color
    val onSurfaceColor: Color
    val textColor: Color
}

object AvoirColorSets {
    object Pink : AvoirColorSet {
        override val containerColor = Color(0xFFFADCCC)
        override val primaryColor = Color(0xFF673010)
        override val secondaryColor = Color(0xFF694C3D)
        override val accentColor = Color(0xFFFF5E00)
        override val surfaceColor = Color(0xFFE9C8B4)
        override val onSurfaceColor = Color(0xFFBA907A)
        override val textColor = Color(0xFF77665C)
    }

    object Blue : AvoirColorSet {
        override val containerColor = Color(0xFFDBE3FD)
        override val primaryColor = Color(0xFF1C3563)
        override val secondaryColor = Color(0xFF1B4186)
        override val accentColor = Color(0xFF005BFF)
        override val surfaceColor = Color(0xFFB8CCF0)
        override val onSurfaceColor = Color(0xFF4A6BA6)
        override val textColor = Color(0xFF324A75)
    }

    object Green : AvoirColorSet {
        override val containerColor = Color(0xFFEEF1E5)
        override val primaryColor = Color(0xFF3C4132)
        override val secondaryColor = Color(0xFF596446)
        override val accentColor = Color(0xFFAAFF00)
        override val surfaceColor = Color(0xFFD8DFCC)
        override val onSurfaceColor = Color(0xFF7D8175)
        override val textColor = Color(0xFF636958)
    }

    object Purple : AvoirColorSet {
        override val containerColor = Color(0xFFE6DEF6)
        override val primaryColor = Color(0xFF391F6D)
        override val secondaryColor = Color(0xFF61439F)
        override val accentColor = Color(0xFF0019FF)
        override val surfaceColor = Color(0xFFD1C5E9)
        override val onSurfaceColor = Color(0xFF8169B2)
        override val textColor = Color(0xFF636958)
    }

    object Yellow : AvoirColorSet {
        override val containerColor = Color(0xFFF9ECE0)
        override val primaryColor = Color(0xFF2E1125)
        override val secondaryColor = Color(0xFFFADCCC)
        override val accentColor = Color(0xFFFADCCC)
        override val surfaceColor = Color(0xFFFADCCC)
        override val onSurfaceColor = Color(0xFFFADCCC)
        override val textColor = Color(0xFF636958)
    }
}

interface AvoirColorSetProvider {

    @get:Composable
    @get:ReadOnlyComposable
    val set: AvoirColorSet

    val containerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = set.containerColor

    val primaryColor: Color
        @Composable
        @ReadOnlyComposable
        get() = set.primaryColor

    val secondaryColor: Color
        @Composable
        @ReadOnlyComposable
        get() = set.secondaryColor

    val accentColor: Color
        @Composable
        @ReadOnlyComposable
        get() = set.accentColor

    val surfaceColor: Color
        @Composable
        @ReadOnlyComposable
        get() = set.surfaceColor

    val onSurfaceColor: Color
        @Composable
        @ReadOnlyComposable
        get() = set.onSurfaceColor

    val textColor: Color
        @Composable
        @ReadOnlyComposable
        get() = set.textColor
}

object AvoirColorSetProviders {

    object Pink : AvoirColorSetProvider {
        override val set: AvoirColorSet
            @Composable
            @ReadOnlyComposable
            get() = AvoirColorSets.Pink
    }

    object Blue : AvoirColorSetProvider {
        override val set: AvoirColorSet
            @Composable
            @ReadOnlyComposable
            get() = AvoirColorSets.Blue
    }

    object Green : AvoirColorSetProvider {
        override val set: AvoirColorSet
            @Composable
            @ReadOnlyComposable
            get() = AvoirColorSets.Green
    }

    object Purple : AvoirColorSetProvider {
        override val set: AvoirColorSet
            @Composable
            @ReadOnlyComposable
            get() = AvoirColorSets.Purple
    }

    object Yellow : AvoirColorSetProvider {
        override val set: AvoirColorSet
            @Composable
            @ReadOnlyComposable
            get() = AvoirColorSets.Yellow
    }
}
