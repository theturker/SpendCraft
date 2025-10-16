package com.alperen.spendcraft

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Theme modes matching iOS behavior
 */
enum class ThemeMode {
    SYSTEM,  // Follow system theme (iOS default)
    LIGHT,   // Always light theme
    DARK     // Always dark theme
}

/**
 * Theme Helper - iOS parity
 * Manages app theme preference with System/Light/Dark options
 */
object ThemeHelper {
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    
    /**
     * Get current theme mode preference
     * Default: SYSTEM (iOS default behavior)
     */
    fun getThemeMode(context: Context): Flow<ThemeMode> {
        return context.dataStore.data.map { preferences ->
            val themeModeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(themeModeString)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }
    }
    
    /**
     * Set theme mode preference
     */
    suspend fun setThemeMode(context: Context, themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }
    
    /**
     * Get localized display name for theme mode
     */
    fun getThemeModeDisplayName(themeMode: ThemeMode): String {
        return when (themeMode) {
            ThemeMode.SYSTEM -> "Sistem"
            ThemeMode.LIGHT -> "Açık"
            ThemeMode.DARK -> "Koyu"
        }
    }
}

