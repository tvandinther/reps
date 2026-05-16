package com.tvandinther.reps.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Background / Surface ──────────────────────────────────────────────────────
val ColorBackground     = Color(0xFF0C0C0C)
val ColorSurfaceDeep    = Color(0xFF060606)
val ColorSurfaceRaised  = Color(0xFF141414)
val ColorDivider        = Color(0xFF1A1A1A)
val ColorDividerSoft    = Color(0xFF131313)
val ColorBorder         = Color(0xFF242424)

// ── Signal (primary / accent) ─────────────────────────────────────────────────
val ColorSignal         = Color(0xFFD63A0A)   // primary orange
val ColorSignalDeep     = Color(0xFFA82C08)   // pressed
val ColorSignalDim      = Color(0xFF7A200A)   // dim
val ColorSignalShadowBg = Color(0xFF1A0A00)   // chip background
val ColorSignalEdge     = Color(0xFF3D1408)   // chip border

// ── Ink (text) ────────────────────────────────────────────────────────────────
val ColorInk    = Color(0xFFF0F0F0)   // primary text
val ColorInk2   = Color(0xFFE8E8E8)
val ColorInk3   = Color(0xFF888888)   // muted
val ColorInk4   = Color(0xFF444444)
val ColorInk5   = Color(0xFF333333)   // faint
val ColorInk6   = Color(0xFF222222)   // near-invisible

// ── Legacy aliases kept for backward compatibility ────────────────────────────
val Background  = ColorSurfaceDeep
val Surface     = ColorBackground
val OnBackground = ColorInk
val OnSurface   = ColorInk2
val Ghost       = Color(0x59FFFFFF)  // ~35% white

// ── Shapes ────────────────────────────────────────────────────────────────────
val RepsShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small      = RoundedCornerShape(0.dp),
    medium     = RoundedCornerShape(0.dp),
    large      = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)

// ── Color scheme ──────────────────────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    background          = ColorBackground,
    surface             = ColorSurfaceDeep,
    onBackground        = ColorInk,
    onSurface           = ColorInk2,
    primary             = ColorSignal,
    onPrimary           = ColorInk,
    primaryContainer    = ColorSignalShadowBg,
    onPrimaryContainer  = ColorSignal,
    secondary           = ColorInk3,
    onSecondary         = ColorBackground,
    surfaceVariant      = ColorSurfaceRaised,
    onSurfaceVariant    = ColorInk3,
    outline             = ColorDividerSoft,
    outlineVariant      = ColorBorder,
    error               = ColorSignal,
    onError             = ColorInk,
)

@Composable
fun RepsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        shapes      = RepsShapes,
        typography  = RepsTypography,
        content     = content,
    )
}
