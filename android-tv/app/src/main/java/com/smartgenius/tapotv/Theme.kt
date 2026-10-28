package com.smartgenius.tapotv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val BG = Color(0xFF0F1115)
val SURFACE = Color(0xFF181B22)
val SURFACE_HI = Color(0xFF262B36)
val FG = Color(0xFFE8EAF0)
val MUTED = Color(0xFF8B92A5)
val WARN = Color(0xFFFFB300)

val ACCENTS: List<Pair<Int, Long>> = listOf(
    R.string.accent_blue to 0xFF4F8CFF,
    R.string.accent_green to 0xFF3DDC84,
    R.string.accent_orange to 0xFFFF9800,
    R.string.accent_pink to 0xFFFF5C8A,
    R.string.accent_purple to 0xFFB388FF,
)

fun accentColor(index: Int): Color = Color(ACCENTS[index.coerceIn(ACCENTS.indices)].second)

@Composable
fun TvTheme(accentIndex: Int, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = accentColor(accentIndex),
            onPrimary = BG,
            background = BG,
            onBackground = FG,
            surface = SURFACE,
            onSurface = FG,
            surfaceVariant = SURFACE_HI,
            onSurfaceVariant = FG,
            outline = MUTED,
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = BG, contentColor = FG, content = content)
    }
}

/** Botón con un borde de foco bien visible para navegar con el control remoto. */
@Composable
fun TvButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    enabled: Boolean = true,
) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(24.dp)
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier
            .alpha(if (enabled) 1f else 0.35f)
            .onFocusChanged { focused = it.isFocused }
            .clip(shape)
            .background(if (primary) scheme.primary else SURFACE_HI)
            .border(if (focused) 3.dp else 1.dp, if (focused) Color.White else MUTED.copy(alpha = 0.5f), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 26.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = if (primary) scheme.onPrimary else scheme.onSurface,
        )
    }
}

/** Tarjeta enfocable (para los recuadros de cámara). */
@Composable
fun FocusCard(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .onFocusChanged { focused = it.isFocused }
            .clip(shape)
            .background(SURFACE)
            .border(
                if (focused) 4.dp else 1.dp,
                if (focused) MaterialTheme.colorScheme.primary else SURFACE_HI,
                shape,
            )
            .clickable(onClick = onClick),
        content = content,
    )
}
