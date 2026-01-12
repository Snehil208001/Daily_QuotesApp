package com.BrewApp.dailyquoteapp.navigation


sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Favorites : Screens("favorites")
    object Discover : Screens("discover")
    object Profile : Screens("profile")
}