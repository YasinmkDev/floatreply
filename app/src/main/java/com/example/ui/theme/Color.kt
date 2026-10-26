package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * FloatReply clean minimal palette — white surfaces, near-black ink, hairline
 * borders, ONE flat accent (LinkedIn blue). No gradients, no glow, no glass.
 *
 * The old cyberpunk names are kept as aliases so existing call sites keep
 * compiling; their values are now the light palette. New code should use the
 * semantic names (Background, Ink, Hairline, Accent, ...).
 */

// Semantic palette — use these in new code.
val Background = Color(0xFFFFFFFF)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceTint = Color(0xFFF9FAFB)
val FeedBackground = Color(0xFFF4F2EE)
val Ink = Color(0xFF111111)
val InkSecondary = Color(0xFF5E5E5E)
val InkMuted = Color(0xFF9CA3AF)
val Hairline = Color(0xFFE5E7EB)
val Accent = Color(0xFF0A66C2)
val AccentDark = Color(0xFF004182)
val AccentTint = Color(0xFFEAF3FC)
val Success = Color(0xFF16A34A)
val SuccessTint = Color(0xFFE6F4EA)
val Warning = Color(0xFFD97706)
val Error = Color(0xFFDC2626)

// Legacy aliases (cyberpunk names, light values). Kept so the existing
// screens compile unchanged; do not use these names in new code.
val CyanPrimary = Accent
val CyanPrimaryDark = AccentDark
val CyanGlow = Accent
val CyanContainer = AccentTint
val IndigoAccent = Accent
val IndigoDeep = AccentDark
val IndigoContainer = AccentTint
val DarkBackground = Background
val DarkSurface = SurfaceWhite
val DarkSurfaceCard = SurfaceWhite
val DarkSurfaceGlass = SurfaceWhite
val DarkSurfaceBorder = Hairline
val LinkedInBlue = Accent
val LinkedInLightBg = FeedBackground
val LinkedInDarkBg = FeedBackground
val LinkedInCardBg = SurfaceWhite
val LinkedInBorder = Hairline
val TextPrimary = Ink
val TextSecondary = InkSecondary
val TextMuted = InkMuted
val StatusGreen = Success
val FlameAmber = Warning
val FlameRed = Error
val ElectricViolet = Accent
