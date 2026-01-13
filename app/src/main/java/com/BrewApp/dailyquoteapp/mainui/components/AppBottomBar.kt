package com.BrewApp.dailyquoteapp.mainui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.BrewApp.dailyquoteapp.navigation.Screens

@Composable
fun AppBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define items for the bottom bar
    val items = listOf(
        NavigationItem(Screens.Home.route, "Home", Icons.Filled.Home),
        NavigationItem(Screens.Discovery.route, "Discovery", Icons.Filled.Search),
        NavigationItem(Screens.Favorites.route, "Favorites", Icons.Filled.Favorite),
        NavigationItem(Screens.Profile.route, "Profile", Icons.Filled.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), // FIXED
        tonalElevation = 0.dp,
        modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)) // FIXED
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
                            popUpTo(navController.graph.findStartDestination().id) {
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
                    selectedIconColor = MaterialTheme.colorScheme.primary, // FIXED
                    selectedTextColor = MaterialTheme.colorScheme.primary, // FIXED
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // FIXED
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // FIXED
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