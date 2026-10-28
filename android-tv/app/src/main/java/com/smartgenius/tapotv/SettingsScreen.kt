package com.smartgenius.tapotv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

private enum class Tab(val label: Int) {
    Cameras(R.string.tab_cameras),
    Look(R.string.tab_look),
    About(R.string.tab_about),
}

@Composable
fun SettingsScreen(
    cameras: List<Camera>,
    settings: AppSettings,
    onSettings: (AppSettings) -> Unit,
    onAdd: () -> Unit,
    onEdit: (Camera) -> Unit,
    onMove: (Camera, Int) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    var tab by remember { mutableStateOf(Tab.Cameras) }

    Row(
        Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(Modifier.width(280.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Tab.entries.forEach {
                TvButton(stringResource(it.label), { tab = it }, Modifier.fillMaxWidth(), primary = tab == it)
            }
            Spacer(Modifier.weight(1f))
            TvButton(stringResource(R.string.back), onBack, Modifier.fillMaxWidth(), primary = false)
        }

        Column(
            Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (tab) {
                Tab.Cameras -> CamerasTab(cameras, onAdd, onEdit, onMove)
                Tab.Look -> LookTab(settings, onSettings)
                Tab.About -> AboutTab(cameras.size)
            }
        }
    }
}

@Composable
private fun CamerasTab(
    cameras: List<Camera>,
    onAdd: () -> Unit,
    onEdit: (Camera) -> Unit,
    onMove: (Camera, Int) -> Unit,
) {
    Text(stringResource(R.string.cameras_title, cameras.size), style = MaterialTheme.typography.titleLarge)
    if (cameras.size >= 4) {
        Text(
            stringResource(R.string.cameras_warning, cameras.size),
            style = MaterialTheme.typography.bodyLarge,
            color = WARN,
        )
    }
    cameras.forEachIndexed { i, cam ->
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(cam.name, style = MaterialTheme.typography.titleMedium)
                Text("${cam.host}:${cam.port}", style = MaterialTheme.typography.bodyMedium, color = MUTED)
            }
            TvButton(stringResource(R.string.move_up), { onMove(cam, -1) }, primary = false, enabled = i > 0)
            TvButton(stringResource(R.string.move_down), { onMove(cam, 1) }, primary = false, enabled = i < cameras.lastIndex)
            TvButton(stringResource(R.string.edit), { onEdit(cam) }, primary = false)
        }
    }
    TvButton(stringResource(R.string.add_camera), onAdd)
}

@Composable
private fun LookTab(s: AppSettings, onChange: (AppSettings) -> Unit) {
    Text(stringResource(R.string.look_title), style = MaterialTheme.typography.titleLarge)

    OutlinedTextField(
        value = s.title,
        onValueChange = { onChange(s.copy(title = it)) },
        label = { Text(stringResource(R.string.field_app_title)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = s.description ?: stringResource(R.string.default_description),
        onValueChange = { onChange(s.copy(description = it)) },
        label = { Text(stringResource(R.string.field_app_description)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Label(stringResource(R.string.language_label))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TvButton(stringResource(R.string.language_system), { onChange(s.copy(language = "")) }, primary = s.language == "")
        TvButton("Español", { onChange(s.copy(language = "es")) }, primary = s.language == "es")
        TvButton("English", { onChange(s.copy(language = "en")) }, primary = s.language == "en")
    }

    Label(stringResource(R.string.accent_label))
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        ACCENTS.forEachIndexed { i, (_, value) ->
            ColorDot(Color(value), selected = s.accent == i) { onChange(s.copy(accent = i)) }
        }
        Text(stringResource(ACCENTS[s.accent.coerceIn(ACCENTS.indices)].first), color = MUTED)
    }

    Label(stringResource(R.string.columns_label))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(0, 1, 2, 3, 4).forEach { n ->
            TvButton(
                if (n == 0) stringResource(R.string.columns_auto) else n.toString(),
                { onChange(s.copy(columns = n)) },
                primary = s.columns == n,
            )
        }
    }

    Label(stringResource(R.string.quality_label))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TvButton(stringResource(R.string.quality_low), { onChange(s.copy(gridHd = false)) }, primary = !s.gridHd)
        TvButton(stringResource(R.string.quality_high), { onChange(s.copy(gridHd = true)) }, primary = s.gridHd)
    }

    ToggleRow(stringResource(R.string.toggle_names), s.showNames) { onChange(s.copy(showNames = it)) }
    ToggleRow(stringResource(R.string.toggle_clock), s.showClock) { onChange(s.copy(showClock = it)) }
    ToggleRow(stringResource(R.string.toggle_mute_fullscreen), s.fullscreenMuted) { onChange(s.copy(fullscreenMuted = it)) }

    Text(
        stringResource(R.string.launcher_note),
        style = MaterialTheme.typography.bodyMedium,
        color = MUTED,
    )
}

@Composable
private fun AboutTab(count: Int) {
    val context = LocalContext.current
    val version = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: ""
    }
    Text(stringResource(R.string.about_title), style = MaterialTheme.typography.titleLarge)
    Text(stringResource(R.string.about_version, version), style = MaterialTheme.typography.titleMedium)
    Text(stringResource(R.string.about_desc), style = MaterialTheme.typography.bodyLarge, color = MUTED)
    Text(stringResource(R.string.about_count, count), style = MaterialTheme.typography.bodyLarge)
    Text(stringResource(R.string.about_privacy), style = MaterialTheme.typography.bodyLarge, color = MUTED)
}

@Composable
private fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        TvButton(
            stringResource(if (checked) R.string.yes else R.string.no),
            { onChange(!checked) },
            primary = checked,
        )
    }
}

@Composable
private fun ColorDot(color: Color, selected: Boolean, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Box(
        Modifier
            .onFocusChanged { focused = it.isFocused }
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                if (focused) 4.dp else if (selected) 3.dp else 0.dp,
                if (focused) Color.White else FG,
                CircleShape,
            )
            .clickable(onClick = onClick),
    )
}
