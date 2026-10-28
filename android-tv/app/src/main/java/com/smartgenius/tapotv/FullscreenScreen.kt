package com.smartgenius.tapotv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay

@Composable
fun FullscreenScreen(
    cameras: List<Camera>,
    startIndex: Int,
    settings: AppSettings,
    onExit: () -> Unit,
) {
    BackHandler(onBack = onExit)

    val count = cameras.size
    var index by remember { mutableIntStateOf(startIndex.coerceIn(0, count - 1)) }
    var muted by remember { mutableStateOf(settings.fullscreenMuted) }
    var state by remember { mutableStateOf(PlayState.Connecting) }
    var hint by remember { mutableStateOf(true) }
    val focus = remember { FocusRequester() }

    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    LaunchedEffect(index) {
        state = PlayState.Connecting
        hint = true
        delay(4_000)
        hint = false
    }

    fun step(delta: Int) {
        if (count > 1) index = (index + delta + count) % count
    }

    val camera = cameras[index]

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focus)
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (e.key) {
                    Key.DirectionLeft -> { step(-1); true }
                    Key.DirectionRight -> { step(1); true }
                    Key.DirectionCenter, Key.Enter -> { muted = !muted; hint = true; true }
                    else -> false
                }
            }
            .focusable(),
    ) {
        key(camera.id) {
            CameraPlayer(
                url = camera.rtspUrl(camera.path),
                muted = muted,
                modifier = Modifier.fillMaxSize(),
                onState = { state = it },
            )
        }

        if (hint || state != PlayState.Live) {
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xAA000000))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    "${camera.name}  (${index + 1}/$count)",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Text(
                    when (state) {
                        PlayState.Connecting -> stringResource(R.string.status_connecting)
                        PlayState.Error -> stringResource(R.string.status_retrying)
                        PlayState.Live -> stringResource(if (muted) R.string.sound_off else R.string.sound_on)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
                Text(
                    stringResource(if (count > 1) R.string.hint_multi else R.string.hint_single),
                    style = MaterialTheme.typography.bodySmall,
                    color = MUTED,
                )
            }
        }
    }
}
