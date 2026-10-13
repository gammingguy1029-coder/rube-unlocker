package com.rubecoder.rubeaurachecker.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The app is dark-only by design: a black canvas with a neon red accent.
 * ponytail: no light scheme or dynamic color; add a `lightColorScheme` variant if a light mode is ever requested.
 */
val RubeAuraColors: ColorScheme = darkColorScheme(
    primary = NeonRed,
    onPrimary = PureBlack,
    secondary = NeonAmber,
    onSecondary = PureBlack,
    background = PureBlack,
    onBackground = NeonWhite,
    surface = SurfaceNearBlack,
    onSurface = NeonWhite,
    surfaceVariant = SurfaceDeep,
    onSurfaceVariant = TextMuted,
    error = NeonRed,
    onError = PureBlack,
    outline = NeonRedFaint,
    outlineVariant = Hairline,
)

/** Neon red, the single accent that runs through the whole UI. */
val NeonRed = androidx.compose.ui.graphics.Color(0xFFFF1744)

/** A warmer neon used only for the 999 PERFECT AURA state. */
val NeonAmber = androidx.compose.ui.graphics.Color(0xFFFFB300)

val PureBlack = androidx.compose.ui.graphics.Color(0xFF000000)

val NeonWhite = androidx.compose.ui.graphics.Color(0xFFF2F2F2)

val SurfaceNearBlack = androidx.compose.ui.graphics.Color(0xFF0B0B0D)

val SurfaceDeep = androidx.compose.ui.graphics.Color(0xFF141417)

val TextMuted = androidx.compose.ui.graphics.Color(0xFFB0B0B8)

val NeonRedFaint = androidx.compose.ui.graphics.Color(0x40FF1744)

val Hairline = androidx.compose.ui.graphics.Color(0x1FFFFFFF)

val RubeAuraTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 48.sp, letterSpacing = 2.sp),
    displayMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 36.sp, letterSpacing = 1.5.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 1.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, letterSpacing = 0.5.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp),
)

/** Corner radii shared by every card and field in the app. */
val RubeAuraShapes = androidx.compose.material3.Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun RubeAuraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RubeAuraColors,
        typography = RubeAuraTypography,
        shapes = RubeAuraShapes,
        content = content,
    )
}