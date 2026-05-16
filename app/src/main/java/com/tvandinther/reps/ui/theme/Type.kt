package com.tvandinther.reps.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tvandinther.reps.R

// ── Font families ─────────────────────────────────────────────────────────────

val BarlowCondensedFamily = FontFamily(
    Font(R.font.barlow_condensed_regular, FontWeight.Normal),
    Font(R.font.barlow_condensed_bold,    FontWeight.Bold),
    Font(R.font.barlow_condensed_black,   FontWeight.Black),
)

val SpectralFamily = FontFamily(
    Font(R.font.spectral_bold,      FontWeight.Bold),
    Font(R.font.spectral_extrabold, FontWeight.ExtraBold),
)

// ── Named text styles (use these directly in composables) ─────────────────────

/** Screen titles — 28sp, Black (900), tracking 0.04 em, ALL CAPS */
val StyleH1 = TextStyle(
    fontFamily   = BarlowCondensedFamily,
    fontWeight   = FontWeight.Black,
    fontSize     = 28.sp,
    letterSpacing = 1.12.sp,   // 0.04 em × 28 sp
    lineHeight   = 28.sp,
)

/** List item name — 18sp, Bold (700), tracking 0.03 em, ALL CAPS */
val StyleH2 = TextStyle(
    fontFamily   = BarlowCondensedFamily,
    fontWeight   = FontWeight.Bold,
    fontSize     = 18.sp,
    letterSpacing = 0.54.sp,   // 0.03 em × 18 sp
)

/** Section group header — 13sp, Bold (700), tracking 0.2 em, ALL CAPS */
val StyleH3 = TextStyle(
    fontFamily   = BarlowCondensedFamily,
    fontWeight   = FontWeight.Bold,
    fontSize     = 13.sp,
    letterSpacing = 2.6.sp,    // 0.2 em × 13 sp
)

/** Eyebrow (section dividers) — 9sp, Bold (700), tracking 0.35 em, ALL CAPS */
val StyleEyebrow = TextStyle(
    fontFamily   = BarlowCondensedFamily,
    fontWeight   = FontWeight.Bold,
    fontSize     = 9.sp,
    letterSpacing = 3.15.sp,   // 0.35 em × 9 sp
)

/** Unit labels — 8sp, Bold (700), tracking 0.2 em, ALL CAPS */
val StyleLabel = TextStyle(
    fontFamily   = BarlowCondensedFamily,
    fontWeight   = FontWeight.Bold,
    fontSize     = 8.sp,
    letterSpacing = 1.6.sp,    // 0.2 em × 8 sp
)

/** Secondary body copy — 10sp, Normal (400), tracking 0.1 em, uppercase */
val StyleBody = TextStyle(
    fontFamily   = BarlowCondensedFamily,
    fontWeight   = FontWeight.Normal,
    fontSize     = 10.sp,
    letterSpacing = 1.0.sp,    // 0.1 em × 10 sp
)

/** Buttons — 15sp, Bold (700), tracking 0.1 em, ALL CAPS */
val StyleButton = TextStyle(
    fontFamily   = BarlowCondensedFamily,
    fontWeight   = FontWeight.Bold,
    fontSize     = 15.sp,
    letterSpacing = 1.5.sp,    // 0.1 em × 15 sp
)

/** Navigation tab label — 9sp, Bold (700), tracking 0.35 em, ALL CAPS */
val StyleTab = StyleEyebrow

/** Large set values (Spectral) — 22sp, Bold (700) */
val StyleDataL = TextStyle(
    fontFamily = SpectralFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 22.sp,
)

/** Medium data (RPE chip, Spectral) — 16sp, Bold (700) */
val StyleDataM = TextStyle(
    fontFamily = SpectralFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 16.sp,
)

/** Small Spectral data — 13sp, Bold (700) */
val StyleDataS = TextStyle(
    fontFamily = SpectralFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 13.sp,
)

// ── Material3 Typography (Barlow Condensed everywhere) ───────────────────────

val RepsTypography = Typography(
    displayLarge = StyleH1.copy(fontSize = 57.sp, letterSpacing = 2.28.sp),
    displayMedium = StyleH1.copy(fontSize = 45.sp, letterSpacing = 1.8.sp),
    displaySmall = StyleH1.copy(fontSize = 36.sp, letterSpacing = 1.44.sp),
    headlineLarge = StyleH1,
    headlineMedium = StyleH2.copy(fontSize = 20.sp, letterSpacing = 0.6.sp),
    headlineSmall = StyleH2,
    titleLarge = TextStyle(
        fontFamily   = BarlowCondensedFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 22.sp,
        letterSpacing = 0.66.sp,
    ),
    titleMedium = StyleH2,
    titleSmall = StyleH3,
    bodyLarge = StyleBody.copy(fontSize = 16.sp, letterSpacing = 1.6.sp),
    bodyMedium = StyleBody.copy(fontSize = 14.sp, letterSpacing = 1.4.sp),
    bodySmall = StyleBody,
    labelLarge = StyleButton,
    labelMedium = StyleLabel.copy(fontSize = 10.sp, letterSpacing = 2.0.sp),
    labelSmall = StyleLabel,
)
