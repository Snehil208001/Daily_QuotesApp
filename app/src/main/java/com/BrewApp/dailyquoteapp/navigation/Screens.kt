package com.BrewApp.dailyquoteapp.navigation

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object SignUp : Screens("signup")
    object Home : Screens("home")
    object Discovery : Screens("discovery")
    object Favorites : Screens("favorites")
    object Profile : Screens("profile")
    // NEW
    object NewPassword : Screens("new_password")
}