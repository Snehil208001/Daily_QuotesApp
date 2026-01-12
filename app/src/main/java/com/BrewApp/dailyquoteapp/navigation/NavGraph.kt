package com.BrewApp.dailyquoteapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.BrewApp.dailyquoteapp.mainui.FavouriteScreen
import com.BrewApp.dailyquoteapp.mainui.HomeScreen
import com.BrewApp.dailyquoteapp.mainui.components.AppBottomNavBar

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
                FavouriteScreen(
                    onBackClick = { navController.popBackStack() },
                    onItemClick = { item ->
                        // TODO: Handle item click (e.g., navigate to details)
                    }
                )
            }
        }
    }
}