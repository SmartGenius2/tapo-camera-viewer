package com.smartgenius.tapotv

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PlayState { Connecting, Live, Error }

private const val RETRY_DELAY_MS = 3_000L

/** Reproduce un stream RTSP y reintenta solo si se cae. */
@OptIn(UnstableApi::class)
@Composable
fun CameraPlayer(
    url: String,
    muted: Boolean,
    modifier: Modifier = Modifier,
    onState: (PlayState) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val report by rememberUpdatedState(onState)

    val player = remember(url) {
        ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(500, 2_000, 250, 500)
                    .build()
            )
            .build()
            .apply {
                playWhenReady = true
                volume = if (muted) 0f else 1f
            }
    }

    LaunchedEffect(muted, player) { player.volume = if (muted) 0f else 1f }

    DisposableEffect(player) {
        var retryJob: Job? = null

        fun start() {
            val source = RtspMediaSource.Factory()
                .setForceUseRtpTcp(true)
                .setTimeoutMs(8_000)
                .createMediaSource(MediaItem.fromUri(url))
            player.setMediaSource(source)
            player.prepare()
        }

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> report(PlayState.Live)
                    Player.STATE_BUFFERING -> report(PlayState.Connecting)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                report(PlayState.Error)
                retryJob?.cancel()
                retryJob = scope.launch {
                    delay(RETRY_DELAY_MS)
                    start()
                }
            }
        }

        player.addListener(listener)
        start()

        onDispose {
            retryJob?.cancel()
            player.removeListener(listener)
            player.release()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                keepScreenOn = true
                isFocusable = false
                this.player = player
            }
        },
        update = { it.player = player },
    )
}
