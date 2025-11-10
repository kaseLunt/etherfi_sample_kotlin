package com.babel.etherfiportfoliotracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
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

// Define custom color properties
data class CustomColors(
    val appBackground: Color = Color.Unspecified,
    val cardBackground: Color = Color.Unspecified,
    val cardBackgroundLight: Color = Color.Unspecified,
    val cardBorder: Color = Color.Unspecified,
    val textLavender: Color = Color.Unspecified,
    val textAccent: Color = Color.Unspecified,
    val purpleArrow: Color = Color.Unspecified,
    val gradientStart: Color = Color.Unspecified,
    val gradientMiddle: Color = Color.Unspecified,
    val gradientEnd: Color = Color.Unspecified,
    val inputGradientStart: Color = Color.Unspecified,
    val inputGradientEnd: Color = Color.Unspecified,
    val cardScreenBackground: Color = Color.Unspecified,
    val cardScreenDivider: Color = Color.Unspecified
)

// Create the specific dark theme colors
private val DarkCustomColors = CustomColors(
    appBackground = AppBrandPurpleDark,
    cardBackground = AppBrandPurpleMedium,
    cardBackgroundLight = AppBrandPurpleLight,
    cardBorder = AppBrandPurpleBorder,
    textLavender = AppBrandLavenderText,
    textAccent = AppBrandLavenderAccent,
    purpleArrow = AppBrandPurpleArrow,
    gradientStart = AppBrandGradientStart,
    gradientMiddle = AppBrandGradientMiddle,
    gradientEnd = AppBrandGradientEnd,
    inputGradientStart = AppBrandInputGradientStart,
    inputGradientEnd = AppBrandInputGradientEnd,
    cardScreenBackground = CardScreenDarkBackground,
    cardScreenDivider = CardScreenDivider
)

// Create a CompositionLocal
internal val LocalCustomColors = staticCompositionLocalOf { CustomColors() }

@Composable
fun EtherFiPortfolioTrackerTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> EtherFiColorScheme
    }

    val customColors = DarkCustomColors

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object AppTheme {
    val colors: CustomColors
        @Composable
        get() = LocalCustomColors.current
}