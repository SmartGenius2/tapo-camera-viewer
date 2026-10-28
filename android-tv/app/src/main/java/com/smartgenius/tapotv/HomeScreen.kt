package com.smartgenius.tapotv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LIVE = Color(0xFF3DDC84)
private val CONNECTING = Color(0xFFFFB300)
private val ERROR = Color(0xFFFF5252)

@Composable
fun HomeScreen(
    cameras: List<Camera>,
    settings: AppSettings,
    onOpen: (Int) -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit,
) {
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(cameras.isEmpty()) { runCatching { firstFocus.requestFocus() } }

    Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 28.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(settings.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                val description = settings.description ?: stringResource(R.string.default_description)
                if (description.isNotBlank()) {
                    Text(description, style = MaterialTheme.typography.bodyLarge, color = MUTED)
                }
            }
            if (settings.showClock) {
                Clock()
                Spacer(Modifier.width(24.dp))
            }
            TvButton(stringResource(R.string.settings), onSettings, primary = false)
        }
        Spacer(Modifier.height(20.dp))

        if (cameras.isEmpty()) {
            Column(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(stringResource(R.string.no_cameras), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.no_cameras_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MUTED,
                )
                Spacer(Modifier.height(24.dp))
                TvButton(stringResource(R.string.add_camera), onAdd, Modifier.focusRequester(firstFocus))
            }
        } else {
            CameraGrid(cameras, settings, firstFocus, onOpen, Modifier.weight(1f))
        }
    }
}

private fun columnsFor(count: Int, setting: Int): Int = when {
    setting in 1..4 -> setting
    count <= 1 -> 1
    count <= 4 -> 2
    else -> 3
}

@Composable
private fun CameraGrid(
    cameras: List<Camera>,
    settings: AppSettings,
    firstFocus: FocusRequester,
    onOpen: (Int) -> Unit,
    modifier: Modifier,
) {
    val cols = columnsFor(cameras.size, settings.columns)
    val rows = (cameras.size + cols - 1) / cols
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(rows) { r ->
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(cols) { c ->
                    val i = r * cols + c
                    if (i < cameras.size) {
                        CameraTile(
                            camera = cameras[i],
                            settings = settings,
                            onOpen = { onOpen(i) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .then(if (i == 0) Modifier.focusRequester(firstFocus) else Modifier),
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraTile(camera: Camera, settings: AppSettings, onOpen: () -> Unit, modifier: Modifier) {
    var state by remember { mutableStateOf(PlayState.Connecting) }
    val stream = if (settings.gridHd) camera.path else camera.subPath
    val connecting = stringResource(R.string.status_connecting)
    val noSignal = stringResource(R.string.status_no_signal)

    FocusCard(onClick = onOpen, modifier = modifier) {
        CameraPlayer(
            url = camera.rtspUrl(stream),
            muted = true,
            modifier = Modifier.fillMaxSize(),
            onState = { state = it },
        )
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .clip(CircleShape)
                .background(Color(0xAA000000))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        when (state) {
                            PlayState.Live -> LIVE
                            PlayState.Connecting -> CONNECTING
                            PlayState.Error -> ERROR
                        }
                    )
            )
            if (settings.showNames || state != PlayState.Live) {
                Spacer(Modifier.width(8.dp))
                Text(
                    buildString {
                        if (settings.showNames) append(camera.name)
                        if (state == PlayState.Connecting) append(if (isEmpty()) connecting else " · $connecting")
                        if (state == PlayState.Error) append(if (isEmpty()) noSignal else " · $noSignal")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun Clock() {
    var now by remember { mutableStateOf(timeText()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = timeText()
            delay(15_000)
        }
    }
    Text(now, style = MaterialTheme.typography.titleLarge, color = MUTED)
}

private fun timeText(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
