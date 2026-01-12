package com.BrewApp.dailyquoteapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Define the Color Schemes using your new colors from Color.kt
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryBlue,
    tertiary = TextMuted,
    background = BackgroundDark,
    surface = SurfaceDark,         // Fixed: mapped to SurfaceDark
    onPrimary = SurfaceLight,      // Fixed: mapped to SurfaceLight (White text on Blue)
    onSurface = SurfaceLight       // Fixed: mapped to SurfaceLight (White text on Dark bg)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryBlue,
    tertiary = TextMuted,
    background = BackgroundCream,  // Fixed: mapped to BackgroundCream
    surface = SurfaceLight,        // Fixed: mapped to SurfaceLight
    onPrimary = SurfaceLight,      // Fixed: mapped to SurfaceLight (White text on Blue)
    onSurface = TextPrimary        // Fixed: mapped to TextPrimary (Dark text on Light bg)
)

@Composable
fun DailyQuoteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Changed to false to force our custom colors
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}