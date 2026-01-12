package com.BrewApp.dailyquoteapp.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.BrewApp.dailyquoteapp.mainui.FavoritesScreen
import com.BrewApp.dailyquoteapp.mainui.HomeScreen
import com.BrewApp.dailyquoteapp.ui.theme.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home
            composable(Screens.Home.route) {
                HomeScreen()
            }

            // Favorites
            composable(Screens.Favorites.route) {
                FavoritesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Discover (Placeholder)
            composable(Screens.Discover.route) {
                Surface(modifier = Modifier.padding(24.dp)) { Text("Discover Screen") }
            }

            // Profile (Placeholder)
            composable(Screens.Profile.route) {
                Surface(modifier = Modifier.padding(24.dp)) { Text("Profile Screen") }
            }
        }
    }
}

@Composable
fun AppBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define items for the bottom bar
    val items = listOf(
        NavigationItem(Screens.Home.route, "Home", Icons.Filled.Home),
        NavigationItem(Screens.Discover.route, "Discover", Icons.Filled.Search),
        NavigationItem(Screens.Favorites.route, "Favorites", Icons.Filled.Favorite),
        NavigationItem(Screens.Profile.route, "Profile", Icons.Filled.Person)
    )

    NavigationBar(
        containerColor = SurfaceLight.copy(alpha = 0.9f),
        tonalElevation = 0.dp,
        modifier = Modifier.border(width = 1.dp, color = BorderLight)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(Screens.Home.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
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

// Helper data class for the bottom bar logic
private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)