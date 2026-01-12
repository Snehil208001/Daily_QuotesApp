package com.BrewApp.dailyquoteapp.mainui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder // Added this import
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.BrewApp.dailyquoteapp.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data Model
data class DailyQuote(
    val text: String,
    val author: String
)

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // State
    var currentQuote by remember {
        mutableStateOf(DailyQuote("The only way to do great work is to love what you do.", "Steve Jobs"))
    }
    var isFavorite by remember { mutableStateOf(false) }

    // Date Logic (Java.time requires minSdk 26, or desugaring)
    val currentDate = remember { LocalDate.now() }
    val dayOfWeek = currentDate.format(DateTimeFormatter.ofPattern("EEEE")) // "Monday"
    val monthDay = currentDate.format(DateTimeFormatter.ofPattern("MMM dd")) // "Oct 24"

    Scaffold(
        containerColor = BackgroundCream,
        bottomBar = { ModernBottomNavBar() }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // 1. Top Bar / Date Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dayOfWeek.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = monthDay,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = { /* Settings */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = TextPrimary
                    )
                }
            }

            // 2. Main Content Area (Centered)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Decorative Quote Mark
                Text(
                    text = "“",
                    fontSize = 120.sp,
                    fontFamily = FontFamily.Serif,
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-10).dp, y = (-40).dp)
                )

                // Quote Content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentQuote.text,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Blue Separator Pill
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryBlue.copy(alpha = 0.3f))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "— ${currentQuote.author}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            // 3. Actions Bar (Refresh, Save, Share)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                // Refresh Action
                VerticalActionButton(
                    icon = Icons.Outlined.Refresh,
                    label = "New Quote",
                    isPrimary = false,
                    onClick = {
                        currentQuote = DailyQuote("Simplicity is the ultimate sophistication.", "Leonardo da Vinci")
                        isFavorite = false
                    }
                )

                // Save/Favorite Action (Primary - Big)
                VerticalActionButton(
                    // Corrected Icon logic: Use Icons.Filled.Favorite (solid) vs Icons.Filled.FavoriteBorder (outline)
                    icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    label = if (isFavorite) "Saved" else "Save",
                    isPrimary = true,
                    onClick = { isFavorite = !isFavorite }
                )

                // Share Action
                VerticalActionButton(
                    icon = Icons.Outlined.IosShare,
                    label = "Share",
                    isPrimary = false,
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "\"${currentQuote.text}\" - ${currentQuote.author}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                )
            }
        }
    }
}

/**
 * Custom Component for the Vertical Buttons (Icon on top, Text below)
 */
@Composable
fun VerticalActionButton(
    icon: ImageVector,
    label: String,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        // The Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (isPrimary) 64.dp else 56.dp) // Primary is slightly larger
                .shadow(
                    elevation = if (isPrimary) 10.dp else 2.dp,
                    shape = CircleShape,
                    spotColor = if (isPrimary) PrimaryBlue.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.1f)
                )
                .clip(CircleShape)
                .background(if (isPrimary) PrimaryBlue else SurfaceLight)
                .then(
                    if (!isPrimary) Modifier.border(1.dp, BorderLight, CircleShape) else Modifier
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrimary) Color.White else TextSecondary,
                modifier = Modifier.size(if (isPrimary) 28.dp else 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // The Label
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPrimary) TextPrimary else TextMuted
        )
    }
}

@Composable
fun ModernBottomNavBar() {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.5f), // Glassmorphism
        tonalElevation = 0.dp,
        modifier = Modifier.border(width = 1.dp, color = BorderLight)
    ) {
        val items = listOf(
            Triple("Today", Icons.Filled.Home, true),
            Triple("Topics", Icons.Filled.GridView, false),
            Triple("Saved", Icons.Filled.Bookmarks, false)
        )

        items.forEach { (label, icon, isSelected) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = isSelected,
                onClick = { /* TODO */ },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}