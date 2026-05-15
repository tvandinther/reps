package com.tvandinther.reps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Background = Color(0xFF060606)
val Surface = Color(0xFF0D0D0D)
val OnBackground = Color(0xFFFFFFFF)
val OnSurface = Color(0xFFE0E0E0)
val Ghost = Color(0x59FFFFFF) // ~35% white

private val DarkColors = darkColorScheme(
    background = Background,
    surface = Surface,
    onBackground = OnBackground,
    onSurface = OnSurface,
    primary = OnBackground,
    onPrimary = Background,
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF333333),
)

@Composable
fun RepsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
