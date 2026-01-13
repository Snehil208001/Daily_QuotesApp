package com.BrewApp.dailyquoteapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.BrewApp.dailyquoteapp.data.model.Quote
import java.time.LocalDate

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("daily_quote_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_QUOTE_TEXT = "daily_quote_text"
        private const val KEY_QUOTE_AUTHOR = "daily_quote_author"
        private const val KEY_QUOTE_DATE = "daily_quote_date"

        private const val KEY_NOTIF_ENABLED = "notif_enabled"
        private const val KEY_NOTIF_HOUR = "notif_hour"
        private const val KEY_NOTIF_MINUTE = "notif_minute"

        // New Personalization Keys
        const val KEY_THEME_MODE = "app_theme_mode" // "system", "light", "dark"
        const val KEY_ACCENT_COLOR = "app_accent_color" // "blue", "green", "purple", "orange"
        const val KEY_FONT_SCALE = "app_font_scale" // Float (e.g., 0.8f to 1.4f)
    }

    // --- Daily Quote Logic (Existing) ---
    fun saveDailyQuote(quote: Quote) {
        val today = LocalDate.now().toString()
        prefs.edit()
            .putString(KEY_QUOTE_TEXT, quote.text)
            .putString(KEY_QUOTE_AUTHOR, quote.author)
            .putString(KEY_QUOTE_DATE, today)
            .apply()
    }

    fun getDailyQuote(): Quote? {
        val text = prefs.getString(KEY_QUOTE_TEXT, null)
        val author = prefs.getString(KEY_QUOTE_AUTHOR, null)
        if (text != null && author != null) {
            return Quote(text, author)
        }
        return null
    }

    fun getLastQuoteDate(): String? = prefs.getString(KEY_QUOTE_DATE, null)

    fun isQuoteFromToday(): Boolean {
        val lastDate = getLastQuoteDate()
        val today = LocalDate.now().toString()
        return lastDate == today
    }

    // --- Notification Settings (Existing) ---
    fun setNotificationsEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()
    fun areNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_NOTIF_ENABLED, false)

    fun setNotificationTime(hour: Int, minute: Int) {
        prefs.edit().putInt(KEY_NOTIF_HOUR, hour).putInt(KEY_NOTIF_MINUTE, minute).apply()
    }

    fun getNotificationTime(): Pair<Int, Int> {
        val hour = prefs.getInt(KEY_NOTIF_HOUR, 8)
        val minute = prefs.getInt(KEY_NOTIF_MINUTE, 0)
        return Pair(hour, minute)
    }

    // --- New Personalization Settings ---

    fun setThemeMode(mode: String) = prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "system") ?: "system"

    fun setAccentColor(color: String) = prefs.edit().putString(KEY_ACCENT_COLOR, color).apply()
    fun getAccentColor(): String = prefs.getString(KEY_ACCENT_COLOR, "blue") ?: "blue"

    fun setFontScale(scale: Float) = prefs.edit().putFloat(KEY_FONT_SCALE, scale).apply()
    fun getFontScale(): Float = prefs.getFloat(KEY_FONT_SCALE, 1.0f)

    // Helper to register listener for reactive updates
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}