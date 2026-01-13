package com.BrewApp.dailyquoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.BrewApp.dailyquoteapp.data.auth.AuthManager
import com.BrewApp.dailyquoteapp.data.auth.SupabaseClient
import com.BrewApp.dailyquoteapp.mainui.preferences.viewmodel.PreferencesViewModel
import com.BrewApp.dailyquoteapp.navigation.AppNavGraph
import com.BrewApp.dailyquoteapp.navigation.Screens
import com.BrewApp.dailyquoteapp.ui.theme.DailyQuoteAppTheme
import io.github.jan.supabase.gotrue.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Handle Deep Links
        SupabaseClient.client.handleDeeplinks(intent)
        val authManager = AuthManager()

        setContent {
            // Get ViewModel to observe Theme/Accent/Font preferences
            val prefsViewModel: PreferencesViewModel = viewModel()
            val themeMode by prefsViewModel.themeMode.collectAsState()
            val accentColor by prefsViewModel.accentColor.collectAsState()
            val fontScale by prefsViewModel.fontScale.collectAsState()

            // Apply Theme dynamically
            DailyQuoteAppTheme(
                themeMode = themeMode,
                accentColorName = accentColor,
                fontScale = fontScale
            ) {
                // State to hold the decision and check completion
                var startDestination by remember { mutableStateOf(Screens.Login.route) }
                var isAuthChecked by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val data = intent?.data
                    val isRecovery = data?.toString()?.contains("type=recovery") == true

                    if (isRecovery) {
                        startDestination = Screens.NewPassword.route
                    } else if (authManager.isUserLoggedIn()) {
                        startDestination = Screens.Home.route
                    }
                    isAuthChecked = true
                }

                if (isAuthChecked) {
                    // Pass the existing viewModel to the graph if possible, or let screens get their own
                    // Since PreferencesViewModel uses SharedPreferences, separate instances will stay in sync.
                    AppNavGraph(startDestination = startDestination)
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.White))
                }
            }
        }
    }
}