package dev.sergiosabater.rickmortypedia.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat
import dev.sergiosabater.rickmortypedia.R

private val LightColorScheme = lightColorScheme(
    // Main
    primary = PortalGreen,
    onPrimary = Color.White,
    primaryContainer = PortalGreenLight,
    onPrimaryContainer = TextPrimaryLight,

    // Secondary
    secondary = SpacePurple,
    onSecondary = Color.White,
    secondaryContainer = SpacePurpleLight,
    onSecondaryContainer = TextPrimaryLight,

    // Tertiary
    tertiary = LabGreen,
    onTertiary = Color.White,
    tertiaryContainer = LabGreenVariant,
    onTertiaryContainer = TextPrimaryLight,

    // Backgrounds and Surfaces
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondaryLight,

    // Utilities
    error = ErrorColor,
    onError = Color.White,
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF5F5F5)
)

private val DarkColorScheme = darkColorScheme(
    // Main - Green Neon Portal
    primary = PortalGreenNeon,
    onPrimary = SpaceBlack,
    primaryContainer = PortalGreenDark,
    onPrimaryContainer = TextPrimaryDark,

    // Secondary - Purple Neon
    secondary = NeonPurple,
    onSecondary = SpaceBlack,
    secondaryContainer = NeonPurpleDark,
    onSecondaryContainer = TextPrimaryDark,

    // Tertiary
    tertiary = LabGreen,
    onTertiary = SpaceBlack,
    tertiaryContainer = LabGreenVariant,
    onTertiaryContainer = TextPrimaryDark,

    // Backgrounds and Surfaces
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondaryDark,

    // Utilities
    error = ErrorColor,
    onError = Color.White,
    outline = Color(0xFF2A2F4A),
    outlineVariant = Color(0xFF1F2437)
)

val RickAndMortyFontFamily = FontFamily(
    Font(R.font.rick_and_morty)
)

@Composable
fun RickMortyPediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to maintain Rick and Morty theme identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()

            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}