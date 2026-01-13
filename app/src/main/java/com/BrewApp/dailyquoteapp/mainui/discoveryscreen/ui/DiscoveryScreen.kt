package com.BrewApp.dailyquoteapp.mainui.discoveryscreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.BrewApp.dailyquoteapp.data.model.SupabaseQuote
import com.BrewApp.dailyquoteapp.mainui.discoveryscreen.viewmodel.DiscoveryViewModel
import com.BrewApp.dailyquoteapp.mainui.favouritescreen.viewmodel.CollectionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel = viewModel(),
    collectionsViewModel: CollectionsViewModel = viewModel()
) {
    val quotes by viewModel.quotes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val focusManager = LocalFocusManager.current
    val pullToRefreshState = rememberPullToRefreshState()

    // --- State for Add to Collection Dialog ---
    var showCollectionDialog by remember { mutableStateOf(false) }
    var quoteToAddToCollection by remember { mutableStateOf<SupabaseQuote?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // FIXED
            .statusBarsPadding()
    ) {
        // --- 1. Header: Search & Filter ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .shadow(1.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape) // FIXED
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape) // FIXED
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // FIXED
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface), // FIXED
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(text = "Find inspiration...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 16.sp) // FIXED
                            }
                            innerTextField()
                        }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(1.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape) // FIXED
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape) // FIXED
                    .clickable { /* Filter logic */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onSurface) // FIXED
            }
        }

        // --- 2. Category Chips ---
        val categories = listOf("Motivation", "Love", "Success", "Wisdom", "Humor")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface) // FIXED
                        .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50)) // FIXED
                        .clickable { viewModel.onCategorySelected(category) }
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // FIXED
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // --- 3. Main Feed ---
        Box(modifier = Modifier.weight(1f)) {
            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { viewModel.refresh() },
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                if (quotes.isEmpty() && !isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(64.dp)) // FIXED
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "No quotes found", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) // FIXED
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp, start = 20.dp, end = 20.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(quotes, key = { it.id }) { quote ->
                            DiscoveryQuoteCard(
                                quote = quote,
                                onToggleLike = { viewModel.toggleQuoteLike(quote) },
                                onAddToCollection = {
                                    quoteToAddToCollection = quote
                                    collectionsViewModel.loadCollections() // Fetch collections when dialog opens
                                    showCollectionDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Add to Collection Dialog (UI UPDATED) ---
    if (showCollectionDialog && quoteToAddToCollection != null) {
        val collections by collectionsViewModel.collections.collectAsState()
        var isCreating by remember { mutableStateOf(false) }
        var newCollectionName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCollectionDialog = false },
            containerColor = MaterialTheme.colorScheme.surface, // FIXED
            shape = RoundedCornerShape(24.dp),
            titleContentColor = MaterialTheme.colorScheme.onSurface,   // FIXED
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,  // FIXED
            title = {
                Text(
                    text = if (isCreating) "New Collection" else "Save to Collection",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface // FIXED
                )
            },
            text = {
                if (isCreating) {
                    Column {
                        Text("Give your collection a name:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) // FIXED
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCollectionName,
                            onValueChange = { newCollectionName = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface, // FIXED
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface, // FIXED
                                cursorColor = MaterialTheme.colorScheme.primary, // FIXED
                                focusedBorderColor = MaterialTheme.colorScheme.primary, // FIXED
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline, // FIXED
                                focusedLabelColor = MaterialTheme.colorScheme.primary, // FIXED
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant // FIXED
                            )
                        )
                    }
                } else {
                    if (collections.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Text("No custom collections yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) // FIXED
                        }
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(collections) { collection ->
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = collection.name,
                                            color = MaterialTheme.colorScheme.onSurface, // FIXED
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Default.Folder,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary // FIXED
                                        )
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                    modifier = Modifier
                                        .clickable {
                                            collectionsViewModel.addQuoteToCollection(
                                                collection,
                                                quoteToAddToCollection!!.text,
                                                quoteToAddToCollection!!.author
                                            )
                                            showCollectionDialog = false
                                        }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) // FIXED
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (isCreating) {
                    Button(
                        onClick = {
                            if (newCollectionName.isNotBlank()) {
                                collectionsViewModel.createCollection(newCollectionName)
                                isCreating = false
                                newCollectionName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // FIXED
                    ) { Text("Create", color = MaterialTheme.colorScheme.onPrimary) }
                } else {
                    TextButton(onClick = { isCreating = true }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary) // FIXED
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create New", color = MaterialTheme.colorScheme.primary) // FIXED
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (isCreating) isCreating = false else showCollectionDialog = false
                }) { Text(if (isCreating) "Cancel" else "Close", color = MaterialTheme.colorScheme.onSurfaceVariant) } // FIXED
            }
        )
    }
}

@Composable
fun DiscoveryQuoteCard(
    quote: SupabaseQuote,
    onToggleLike: () -> Unit,
    onAddToCollection: () -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // FIXED
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "“", fontSize = 64.sp, fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // FIXED
                    modifier = Modifier.offset(x = (-8).dp, y = (-24).dp)
                )
                Text(
                    text = quote.text, fontSize = 20.sp, fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, lineHeight = 28.sp, // FIXED
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(0.dp)) // FIXED
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) // FIXED
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = quote.author, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant) // FIXED
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // 1. LIKE
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (quote.isLiked) Color(0xFFE25555) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), // FIXED
                        modifier = Modifier.size(22.dp).clickable { onToggleLike() }
                    )
                    // 2. ADD TO COLLECTION
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = "Add to Collection",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), // FIXED
                        modifier = Modifier.size(22.dp).clickable { onAddToCollection() }
                    )
                    // 3. SHARE
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), // FIXED
                        modifier = Modifier.size(22.dp).clickable {
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, "${quote.text} - ${quote.author}")
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Quote"))
                        }
                    )
                }
            }
        }
    }
}