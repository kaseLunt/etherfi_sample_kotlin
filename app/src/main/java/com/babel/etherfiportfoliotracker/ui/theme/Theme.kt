package com.babel.etherfiportfoliotracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * EtherFi custom color scheme inspired by the ether.fi website.
 * Dark theme with gold/tan accents and cream text.
 */
private val EtherFiColorScheme = darkColorScheme(
    // Primary - Gold/Tan for main actions
    primary = EtherFiGold,
    onPrimary = EtherFiBlack,
    primaryContainer = EtherFiGoldDark,
    onPrimaryContainer = EtherFiCream,

    // Secondary - Cream for secondary actions
    secondary = EtherFiCream,
    onSecondary = EtherFiBlack,
    secondaryContainer = EtherFiDarkVariant,
    onSecondaryContainer = EtherFiCream,

    // Tertiary - Alternative accent
    tertiary = EtherFiGold,
    onTertiary = EtherFiBlack,
    tertiaryContainer = EtherFiDarkSurface,
    onTertiaryContainer = EtherFiGold,

    // Background & Surface
    background = EtherFiDarkBackground,
    onBackground = EtherFiCream,
    surface = EtherFiDarkSurface,
    onSurface = EtherFiCream,
    surfaceVariant = EtherFiDarkVariant,
    onSurfaceVariant = EtherFiGold,

    // Error
    error = EtherFiError,
    onError = EtherFiBlack,
    errorContainer = EtherFiError,
    onErrorContainer = EtherFiCream,

    // Outline
    outline = EtherFiGold,
    outlineVariant = EtherFiGoldDark,
)

@Composable
fun EtherFiPortfolioTrackerTheme(
    darkTheme: Boolean = true, // Always use dark theme to match EtherFi
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> EtherFiColorScheme // Always use our custom EtherFi theme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}