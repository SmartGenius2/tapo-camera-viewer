package com.smartgenius.tapotv

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Locale

private sealed interface Screen {
    data object Home : Screen
    data class Fullscreen(val index: Int) : Screen
    data object Settings : Screen
    data class Form(val camera: Camera?, val back: Screen) : Screen
}

class MainActivity : ComponentActivity() {
    /** Aplica el idioma elegido en la app; si es "" se usa el del sistema. */
    override fun attachBaseContext(base: Context) {
        val lang = base.getSharedPreferences("settings", MODE_PRIVATE).getString("language", "") ?: ""
        if (lang.isEmpty()) {
            super.attachBaseContext(base)
            return
        }
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration).apply { setLocale(locale) }
        super.attachBaseContext(base.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Evita que el protector de pantalla del sistema se active mientras se ven las cámaras.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val cameraStore = CameraStore(this)
        val settingsStore = SettingsStore(this)
        setContent { App(cameraStore, settingsStore, onLanguageChanged = ::recreate) }
    }
}

@Composable
private fun App(cameraStore: CameraStore, settingsStore: SettingsStore, onLanguageChanged: () -> Unit) {
    val cameras = remember { mutableStateListOf<Camera>().apply { addAll(cameraStore.load()) } }
    var settings by remember { mutableStateOf(settingsStore.load()) }
    var screen by remember {
        mutableStateOf<Screen>(if (settingsStore.consumeReopenSettings()) Screen.Settings else Screen.Home)
    }

    fun persist() = cameraStore.save(cameras.toList())

    fun move(cam: Camera, delta: Int) {
        val i = cameras.indexOfFirst { it.id == cam.id }
        val j = i + delta
        if (i < 0 || j !in cameras.indices) return
        val tmp = cameras[i]
        cameras[i] = cameras[j]
        cameras[j] = tmp
        persist()
    }

    TvTheme(settings.accent) {
        when (val s = screen) {
            Screen.Home -> HomeScreen(
                cameras = cameras,
                settings = settings,
                onOpen = { screen = Screen.Fullscreen(it) },
                onSettings = { screen = Screen.Settings },
                onAdd = { screen = Screen.Form(null, Screen.Home) },
            )

            is Screen.Fullscreen -> if (cameras.isEmpty()) {
                screen = Screen.Home
            } else {
                FullscreenScreen(
                    cameras = cameras.toList(),
                    startIndex = s.index,
                    settings = settings,
                    onExit = { screen = Screen.Home },
                )
            }

            Screen.Settings -> SettingsScreen(
                cameras = cameras,
                settings = settings,
                onSettings = {
                    val languageChanged = it.language != settings.language
                    settings = it
                    settingsStore.save(it)
                    if (languageChanged) {
                        settingsStore.markReopenSettings()
                        onLanguageChanged()
                    }
                },
                onAdd = { screen = Screen.Form(null, Screen.Settings) },
                onEdit = { screen = Screen.Form(it, Screen.Settings) },
                onMove = ::move,
                onBack = { screen = Screen.Home },
            )

            is Screen.Form -> CameraFormScreen(
                initial = s.camera,
                onSave = { cam ->
                    val i = cameras.indexOfFirst { it.id == cam.id }
                    if (i >= 0) cameras[i] = cam else cameras.add(cam)
                    persist()
                    screen = s.back
                },
                onDelete = { cam ->
                    cameras.removeAll { it.id == cam.id }
                    persist()
                    screen = s.back
                },
                onCancel = { screen = s.back },
            )
        }
    }
}
