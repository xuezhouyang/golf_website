package com.flumenis.sms2email.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

/**
 * Theme Manager for PostaFide v2.0
 *
 * Supports:
 * - Light/Dark/Auto modes
 * - Dynamic colors (Android 12+)
 * - Custom accent colors
 * - Persistent theme selection
 */
class ThemeManager(private val context: Context) {

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val ACCENT_COLOR = stringPreferencesKey("accent_color")
        private val USE_DYNAMIC_COLOR = stringPreferencesKey("use_dynamic_color")
    }

    /**
     * Theme mode flow
     */
    val themeModeFlow: Flow<ThemeMode> = context.themeDataStore.data.map { prefs ->
        when (prefs[THEME_MODE]) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.AUTO
        }
    }

    /**
     * Accent color flow
     */
    val accentColorFlow: Flow<AccentColor> = context.themeDataStore.data.map { prefs ->
        when (prefs[ACCENT_COLOR]) {
            "GREEN" -> AccentColor.GREEN
            "BLUE" -> AccentColor.BLUE
            "PURPLE" -> AccentColor.PURPLE
            "ORANGE" -> AccentColor.ORANGE
            else -> AccentColor.TEAL
        }
    }

    /**
     * Dynamic color enabled flow
     */
    val useDynamicColorFlow: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[USE_DYNAMIC_COLOR] != "false" // Default to true
    }

    /**
     * Set theme mode
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { prefs ->
            prefs[THEME_MODE] = mode.name
        }
    }

    /**
     * Set accent color
     */
    suspend fun setAccentColor(color: AccentColor) {
        context.themeDataStore.edit { prefs ->
            prefs[ACCENT_COLOR] = color.name
        }
    }

    /**
     * Toggle dynamic color
     */
    suspend fun setUseDynamicColor(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[USE_DYNAMIC_COLOR] = enabled.toString()
        }
    }
}

/**
 * Theme modes
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    AUTO
}

/**
 * Accent colors
 */
enum class AccentColor {
    TEAL,
    GREEN,
    BLUE,
    PURPLE,
    ORANGE
}
