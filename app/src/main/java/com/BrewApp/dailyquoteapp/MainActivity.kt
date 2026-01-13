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
import com.BrewApp.dailyquoteapp.data.auth.AuthManager
import com.BrewApp.dailyquoteapp.data.auth.SupabaseClient
import com.BrewApp.dailyquoteapp.navigation.AppNavGraph
import com.BrewApp.dailyquoteapp.navigation.Screens
import com.BrewApp.dailyquoteapp.ui.theme.DailyQuoteAppTheme
import io.github.jan.supabase.gotrue.handleDeeplinks
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Handle Deep Links (This parses the URL and logs the user in automatically)
        SupabaseClient.client.handleDeeplinks(intent)

        val authManager = AuthManager()

        setContent {
            DailyQuoteAppTheme {
                // State to hold the decision and check completion
                var startDestination by remember { mutableStateOf(Screens.Login.route) }
                var isAuthChecked by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    // 2. Check if this launch was triggered by a Password Reset Link
                    val data = intent?.data
                    // Supabase sends tokens in the fragment (e.g., #access_token=...&type=recovery)
                    val isRecovery = data?.toString()?.contains("type=recovery") == true

                    if (isRecovery) {
                        // Go straight to New Password Screen
                        startDestination = Screens.NewPassword.route
                    } else if (authManager.isUserLoggedIn()) {
                        // Normal auto-login
                        startDestination = Screens.Home.route
                    }

                    isAuthChecked = true
                }

                if (isAuthChecked) {
                    // Once checked, show the app with the correct start screen
                    AppNavGraph(startDestination = startDestination)
                } else {
                    // Minimal Splash/Loading screen while checking auth
                    Box(modifier = Modifier.fillMaxSize().background(Color.White))
                }
            }
        }
    }
}