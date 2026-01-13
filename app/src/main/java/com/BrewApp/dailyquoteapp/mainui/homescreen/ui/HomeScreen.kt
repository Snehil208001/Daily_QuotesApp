package com.BrewApp.dailyquoteapp.mainui.homescreen.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.BrewApp.dailyquoteapp.mainui.homescreen.viewmodel.HomeViewModel
import com.BrewApp.dailyquoteapp.util.QuoteStyle
import com.BrewApp.dailyquoteapp.util.ShareUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current

    // Observe Data from ViewModel
    val currentQuote by viewModel.currentQuote.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // State for Share Dialog
    var showShareDialog by remember { mutableStateOf(false) }

    // Date Logic
    val currentDate = remember { LocalDate.now() }
    val dayOfWeek = currentDate.format(DateTimeFormatter.ofPattern("EEEE"))
    val monthDay = currentDate.format(DateTimeFormatter.ofPattern("MMM dd"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // FIXED
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // FIXED
                    letterSpacing = 2.sp
                )
                Text(
                    text = monthDay,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // FIXED
                )
            }
        }

        // 2. Main Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Text(
                    text = "“",
                    fontSize = 120.sp,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), // FIXED
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-10).dp, y = (-40).dp)
                )

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
                        color = MaterialTheme.colorScheme.onSurface // FIXED
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) // FIXED
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "— ${currentQuote.author}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // FIXED
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // 3. Actions Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            VerticalActionButton(
                icon = Icons.Outlined.Refresh,
                label = "New Quote",
                isPrimary = false,
                onClick = { viewModel.getNextQuote() }
            )

            VerticalActionButton(
                icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                label = if (isFavorite) "Saved" else "Save",
                isPrimary = true,
                onClick = { viewModel.toggleFavorite() }
            )

            VerticalActionButton(
                icon = Icons.Outlined.IosShare,
                label = "Share",
                isPrimary = false,
                onClick = { showShareDialog = true } // Open Dialog
            )
        }
    }

    // --- Share Dialog ---
    if (showShareDialog) {
        ShareQuoteDialog(
            quote = currentQuote,
            onDismiss = { showShareDialog = false },
            onShareText = { ShareUtils.shareText(context, currentQuote) },
            onSaveImage = { bitmap -> ShareUtils.saveImageToGallery(context, bitmap) },
            onShareImage = { bitmap -> ShareUtils.shareImage(context, bitmap) }
        )
    }
}

@Composable
fun ShareQuoteDialog(
    quote: com.BrewApp.dailyquoteapp.data.model.Quote,
    onDismiss: () -> Unit,
    onShareText: () -> Unit,
    onSaveImage: (Bitmap) -> Unit,
    onShareImage: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    var selectedStyle by remember { mutableStateOf(QuoteStyle.CLASSIC) }

    // Generate Preview Bitmap whenever style or quote changes
    val previewBitmap = remember(selectedStyle, quote) {
        ShareUtils.generateQuoteBitmap(context, quote, selectedStyle)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // FIXED
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Share Quote", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) // FIXED
                Spacer(modifier = Modifier.height(16.dp))

                // Preview Image
                Image(
                    bitmap = previewBitmap.asImageBitmap(),
                    contentDescription = "Quote Card Preview",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Style Selector
                Text("Select Style", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) // FIXED
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuoteStyle.values().forEach { style ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when (style) {
                                        QuoteStyle.CLASSIC -> Color.White
                                        QuoteStyle.MODERN -> Color(0xFF1E293B)
                                        QuoteStyle.ARTISTIC -> Color(0xFFFFFDD0)
                                    }
                                )
                                .border(
                                    width = if (selectedStyle == style) 3.dp else 1.dp,
                                    color = if (selectedStyle == style) MaterialTheme.colorScheme.primary else Color.Gray, // FIXED
                                    shape = CircleShape
                                )
                                .clickable { selectedStyle = style }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = onShareText, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)) { // FIXED
                        Text("Text")
                    }
                    Button(onClick = { onSaveImage(previewBitmap) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)) { // FIXED
                        Text("Save")
                    }
                    Button(onClick = { onShareImage(previewBitmap) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { // FIXED
                        Text("Share")
                    }
                }
            }
        }
    }
}

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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (isPrimary) 64.dp else 56.dp)
                .shadow(
                    elevation = if (isPrimary) 10.dp else 2.dp,
                    shape = CircleShape,
                    spotColor = if (isPrimary) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.1f)
                )
                .clip(CircleShape)
                .background(if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface) // FIXED
                .then(
                    if (!isPrimary) Modifier.border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape) else Modifier // FIXED
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // FIXED
                modifier = Modifier.size(if (isPrimary) 28.dp else 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPrimary) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) // FIXED
        )
    }
}