package com.smartgenius.tapotv

import android.content.Context

data class AppSettings(
    val title: String = "Tapo TV Viewer",
    val description: String? = null, // null = texto por defecto del idioma
    val language: String = "", // "" = idioma del sistema
    val accent: Int = 0,
    val columns: Int = 0, // 0 = automático
    val gridHd: Boolean = false,
    val showNames: Boolean = true,
    val showClock: Boolean = true,
    val fullscreenMuted: Boolean = false,
)

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun load(): AppSettings {
        val d = AppSettings()
        return AppSettings(
            title = prefs.getString("title", d.title) ?: d.title,
            description = prefs.getString("description", null),
            language = prefs.getString("language", "") ?: "",
            accent = prefs.getInt("accent", d.accent),
            columns = prefs.getInt("columns", d.columns),
            gridHd = prefs.getBoolean("gridHd", d.gridHd),
            showNames = prefs.getBoolean("showNames", d.showNames),
            showClock = prefs.getBoolean("showClock", d.showClock),
            fullscreenMuted = prefs.getBoolean("fullscreenMuted", d.fullscreenMuted),
        )
    }

    /** Tras cambiar de idioma la actividad se recrea; esto permite volver a Ajustes en vez de a Inicio. */
    fun markReopenSettings() = prefs.edit().putBoolean("reopenSettings", true).apply()

    fun consumeReopenSettings(): Boolean {
        val v = prefs.getBoolean("reopenSettings", false)
        if (v) prefs.edit().remove("reopenSettings").apply()
        return v
    }

    fun save(s: AppSettings) {
        prefs.edit()
            .putString("title", s.title)
            .putString("description", s.description)
            .putString("language", s.language)
            .putInt("accent", s.accent)
            .putInt("columns", s.columns)
            .putBoolean("gridHd", s.gridHd)
            .putBoolean("showNames", s.showNames)
            .putBoolean("showClock", s.showClock)
            .putBoolean("fullscreenMuted", s.fullscreenMuted)
            .apply()
    }
}
