package com.BrewApp.dailyquoteapp.mainui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.BrewApp.dailyquoteapp.ui.theme.*

@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit
) {
    // Dummy Data
    val favoriteQuotes = listOf(
        DailyQuote("The only way to do great work is to love what you do.", "Steve Jobs"),
        DailyQuote("Simplicity is the ultimate sophistication.", "Leonardo da Vinci"),
        DailyQuote("Stay hungry, stay foolish.", "Steve Jobs"),
        DailyQuote("Be the change that you wish to see in the world.", "Mahatma Gandhi"),
        DailyQuote("To live is the rarest thing in the world. Most people exist, that is all.", "Oscar Wilde")
    )

    Scaffold(
        containerColor = BackgroundLightBlue, // The light pastel blue
        topBar = {
            FavoritesTopBar(onBack = onNavigateBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp)
        ) {
            items(favoriteQuotes) { quote ->
                FavoriteQuoteCard(quote)
            }
        }
    }
}

@Composable
fun FavoritesTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Title
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )

        // More Button
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = "More",
                tint = TextPrimary
            )
        }
    }
}

@Composable
fun FavoriteQuoteCard(quote: DailyQuote) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight // White card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            // Delete Icon (Top Right)
            IconButton(
                onClick = { /* Delete logic */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Quote Icon
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = PrimaryBlue.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                )

                // Text
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.headlineMedium, // Playfair font
                    color = TextPrimary
                )

                // Divider and Author
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(1.dp)
                            .background(BorderLight)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = quote.author.uppercase(),
                        style = MaterialTheme.typography.labelMedium, // Inter font
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}