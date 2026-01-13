package com.BrewApp.dailyquoteapp.mainui.favouritescreen.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.BrewApp.dailyquoteapp.data.db.FavoriteQuote
import com.BrewApp.dailyquoteapp.data.model.QuoteCollection
import com.BrewApp.dailyquoteapp.mainui.favouritescreen.viewmodel.CollectionsViewModel
import com.BrewApp.dailyquoteapp.mainui.favouritescreen.viewmodel.FavouriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    onBackClick: () -> Unit,
    onItemClick: (FavoriteQuote) -> Unit,
    viewModel: FavouriteViewModel = viewModel(),
    collectionsViewModel: CollectionsViewModel = viewModel()
) {
    // --- States ---
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    // Local State for Tabs (0 = Favorites, 1 = Collections)
    var selectedTab by remember { mutableIntStateOf(0) }
    // Local State for Collection Navigation (null = List, Object = Details)
    var selectedCollection by remember { mutableStateOf<QuoteCollection?>(null) }

    // Dialog State
    var showCreateDialog by remember { mutableStateOf(false) }

    // --- FIX: Reload Collections when switching to Tab 1 ---
    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            collectionsViewModel.loadCollections()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // FIXED
            .statusBarsPadding()
    ) {
        // --- Header ---
        FavoritesHeader(
            onBackClick = {
                if (selectedCollection != null) selectedCollection = null
                else onBackClick()
            },
            title = if (selectedCollection != null) selectedCollection!!.name else "Library"
        )

        if (selectedCollection == null) {
            // --- Tabs (Only show on main library screen) ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary, // FIXED
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary // FIXED
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Favorites") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Collections") })
            }
        }

        // --- Main Content with Pull-to-Refresh ---
        Box(modifier = Modifier.weight(1f)) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.refresh()
                    collectionsViewModel.loadCollections() // Also refresh collections
                },
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                Crossfade(targetState = selectedCollection, label = "Navigation") { collection ->
                    if (collection != null) {
                        // Show Collection Details
                        CollectionDetailView(
                            collection = collection,
                            viewModel = collectionsViewModel
                        )
                    } else {
                        // Show Tabs
                        when (selectedTab) {
                            0 -> FavoritesListView(viewModel)
                            1 -> CollectionsListView(
                                viewModel = collectionsViewModel,
                                onCollectionClick = { selectedCollection = it },
                                onCreateClick = { showCreateDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Collection Dialog
    if (showCreateDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                collectionsViewModel.createCollection(name)
                showCreateDialog = false
            }
        )
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun FavoritesListView(viewModel: FavouriteViewModel) {
    val favorites by viewModel.favorites.collectAsState()

    if (favorites.isEmpty()) {
        EmptyStateView(message = "No Favorites Yet", subMessage = "Save quotes to see them here.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(favorites, key = { it.id }) { quote ->
                FavoriteQuoteCard(
                    text = quote.text,
                    author = quote.author,
                    onDelete = { viewModel.removeFavorite(quote) }
                )
            }
        }
    }
}

@Composable
fun CollectionsListView(
    viewModel: CollectionsViewModel,
    onCollectionClick: (QuoteCollection) -> Unit,
    onCreateClick: () -> Unit
) {
    val collections by viewModel.collections.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (collections.isEmpty()) {
            EmptyStateView(message = "No Collections", subMessage = "Create a collection to organize your quotes.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(collections) { collection ->
                    CollectionCard(collection, onClick = { onCollectionClick(collection) })
                }
            }
        }

        // FAB to create collection
        FloatingActionButton(
            onClick = onCreateClick,
            containerColor = MaterialTheme.colorScheme.primary, // FIXED
            contentColor = MaterialTheme.colorScheme.onPrimary, // FIXED
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, "Create Collection")
        }
    }
}

@Composable
fun CollectionDetailView(
    collection: QuoteCollection,
    viewModel: CollectionsViewModel
) {
    val items by viewModel.collectionItems.collectAsState()

    LaunchedEffect(collection.id) {
        viewModel.loadQuotesForCollection(collection.id)
    }

    if (items.isEmpty()) {
        EmptyStateView(message = "Empty Collection", subMessage = "Add quotes to this collection.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                FavoriteQuoteCard(
                    text = item.text,
                    author = item.author,
                    onDelete = { viewModel.removeQuoteFromCollection(item.id, collection.id) }
                )
            }
        }
    }
}

// --- UI COMPONENTS ---

@Composable
fun FavoritesHeader(onBackClick: () -> Unit, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface) // FIXED
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onSurface, // FIXED
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface // FIXED
        )
    }
}

@Composable
fun FavoriteQuoteCard(text: String, author: String, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // FIXED
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 12.dp, y = (-12).dp)
            ) {
                Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(20.dp)) // FIXED
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(Icons.Rounded.FormatQuote, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), modifier = Modifier.size(32.dp)) // FIXED
                Text(text = "\"$text\"", fontFamily = FontFamily.Serif, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface) // FIXED
                Text(text = author, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) // FIXED
            }
        }
    }
}

@Composable
fun CollectionCard(collection: QuoteCollection, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // FIXED
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.FolderOpen, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) // FIXED
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = collection.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center) // FIXED
        }
    }
}

@Composable
fun CreateCollectionDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Collection") },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Name") }) },
        confirmButton = { Button(onClick = { if(text.isNotBlank()) onConfirm(text) }) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EmptyStateView(message: String, subMessage: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.FormatQuote, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), modifier = Modifier.size(80.dp)) // FIXED
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) // FIXED
        Text(text = subMessage, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp)) // FIXED
    }
}