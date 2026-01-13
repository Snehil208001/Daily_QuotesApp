package com.BrewApp.dailyquoteapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun DailyQuoteAppTheme(
    themeMode: String = "system", // system, light, dark
    accentColorName: String = "blue", // blue, green, purple, orange
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    // 1. Determine Dark Mode
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    // 2. Select Accent Color
    val primaryColor = when (accentColorName) {
        "green" -> PrimaryGreen
        "purple" -> PrimaryPurple
        "orange" -> PrimaryOrange
        else -> PrimaryBlue
    }

    // 3. Build Color Scheme
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            secondary = primaryColor,
            tertiary = TextMuted,
            background = BackgroundDark,
            surface = SurfaceDark,
            onPrimary = SurfaceLight,
            onSurface = SurfaceLight
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            secondary = primaryColor,
            tertiary = TextMuted,
            background = BackgroundCream,
            surface = SurfaceLight,
            onPrimary = SurfaceLight,
            onSurface = TextPrimary
        )
    }

    // 4. Apply Font Scaling via LocalDensity
    val currentDensity = LocalDensity.current
    val customDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale * fontScale
    )

    CompositionLocalProvider(
        LocalDensity provides customDensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography, // Assuming Typography is defined in Type.kt
            content = content
        )
    }
}