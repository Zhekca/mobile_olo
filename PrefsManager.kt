package com.example.glitchstore

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveUserId(id: Int) {
        prefs.edit().putInt("user_id", id).apply()
    }
    fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }
    fun clearUser() {
        prefs.edit().remove("user_id").apply()
    }

    fun getThemeState(): Boolean {
        return prefs.getBoolean("isDarkTheme", false)
    }
    fun saveThemeState(isDark: Boolean) {
        prefs.edit().putBoolean("isDarkTheme", isDark).apply()
    }

    fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean("notif_enabled", false)
    }
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notif_enabled", enabled).apply()
    }
}