package com.BrewApp.dailyquoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.BrewApp.dailyquoteapp.navigation.AppNavGraph
import com.BrewApp.dailyquoteapp.ui.theme.DailyQuoteAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyQuoteAppTheme {
                // The Navigation Graph handles the Scaffold and Routing
                AppNavGraph()
            }
        }
    }
}