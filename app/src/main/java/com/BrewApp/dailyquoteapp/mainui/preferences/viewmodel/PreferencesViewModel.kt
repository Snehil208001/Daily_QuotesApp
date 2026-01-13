package com.BrewApp.dailyquoteapp.mainui.preferences.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.BrewApp.dailyquoteapp.data.auth.AuthManager
import com.BrewApp.dailyquoteapp.data.local.PreferencesManager
import com.BrewApp.dailyquoteapp.util.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val authManager = AuthManager() // For Sync
    private val context = application.applicationContext

    // Notification State
    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _notificationTime = MutableStateFlow(Pair(8, 0))
    val notificationTime: StateFlow<Pair<Int, Int>> = _notificationTime.asStateFlow()

    // Personalization State
    private val _themeMode = MutableStateFlow("system") // system, light, dark
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _accentColor = MutableStateFlow("blue") // blue, green, purple, orange
    val accentColor: StateFlow<String> = _accentColor.asStateFlow()

    private val _fontScale = MutableStateFlow(1.0f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    // Listener to react to changes (important for MainActivity to update theme instantly)
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PreferencesManager.KEY_THEME_MODE -> _themeMode.value = prefs.getThemeMode()
            PreferencesManager.KEY_ACCENT_COLOR -> _accentColor.value = prefs.getAccentColor()
            PreferencesManager.KEY_FONT_SCALE -> _fontScale.value = prefs.getFontScale()
            // Notifications...
            "notif_enabled" -> _notificationsEnabled.value = prefs.areNotificationsEnabled()
            "notif_hour", "notif_minute" -> _notificationTime.value = prefs.getNotificationTime()
        }
    }

    init {
        loadSettings()
        prefs.registerListener(prefsListener)
        // Try to fetch cloud settings on init if user is logged in
        syncFromCloud()
    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterListener(prefsListener)
    }

    private fun loadSettings() {
        _notificationsEnabled.value = prefs.areNotificationsEnabled()
        _notificationTime.value = prefs.getNotificationTime()
        _themeMode.value = prefs.getThemeMode()
        _accentColor.value = prefs.getAccentColor()
        _fontScale.value = prefs.getFontScale()
    }

    private fun syncFromCloud() {
        viewModelScope.launch {
            if (authManager.isUserLoggedIn()) {
                val (cloudTheme, cloudAccent, cloudScale) = authManager.fetchUserPreferences()
                if (cloudTheme != null) updateThemeMode(cloudTheme, sync = false)
                if (cloudAccent != null) updateAccentColor(cloudAccent, sync = false)
                if (cloudScale != null) updateFontScale(cloudScale, sync = false)
            }
        }
    }

    // --- Actions ---

    fun toggleNotifications(enabled: Boolean) {
        prefs.setNotificationsEnabled(enabled)
        if (enabled) NotificationScheduler.scheduleDailyNotification(context)
        else NotificationScheduler.cancelNotification(context)
    }

    fun updateTime(hour: Int, minute: Int) {
        prefs.setNotificationTime(hour, minute)
        if (_notificationsEnabled.value) NotificationScheduler.scheduleDailyNotification(context)
    }

    fun updateThemeMode(mode: String, sync: Boolean = true) {
        prefs.setThemeMode(mode)
        if (sync) triggerCloudSync()
    }

    fun updateAccentColor(color: String, sync: Boolean = true) {
        prefs.setAccentColor(color)
        if (sync) triggerCloudSync()
    }

    fun updateFontScale(scale: Float, sync: Boolean = true) {
        prefs.setFontScale(scale)
        if (sync) triggerCloudSync()
    }

    private fun triggerCloudSync() {
        viewModelScope.launch {
            authManager.syncUserPreferences(
                prefs.getThemeMode(),
                prefs.getAccentColor(),
                prefs.getFontScale()
            )
        }
    }
}