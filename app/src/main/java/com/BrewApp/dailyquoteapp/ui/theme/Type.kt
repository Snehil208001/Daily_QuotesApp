package com.BrewApp.dailyquoteapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.BrewApp.dailyquoteapp.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val InterFont = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider)
)

val PlayfairFont = FontFamily(
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = provider)
)

// Set of Material typography styles to start with
val Typography = Typography(
    // Large Titles (Playfair)
    headlineLarge = TextStyle(
        fontFamily = PlayfairFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // Quotes (Playfair)
    headlineMedium = TextStyle(
        fontFamily = PlayfairFont,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp, // ~1.35rem
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // UI Elements (Inter)
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Authors / Small Caps
    labelMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Tab Labels
    labelSmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp, // Tailwind text-[10px]
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)