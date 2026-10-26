package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = Accent,
  onPrimary = Color.White,
  primaryContainer = AccentTint,
  onPrimaryContainer = AccentDark,
  secondary = Accent,
  onSecondary = Color.White,
  secondaryContainer = AccentTint,
  onSecondaryContainer = AccentDark,
  tertiary = Accent,
  onTertiary = Color.White,
  background = Background,
  onBackground = Ink,
  surface = SurfaceWhite,
  onSurface = Ink,
  surfaceVariant = SurfaceTint,
  onSurfaceVariant = InkSecondary,
  outline = Hairline,
  error = Error,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LightColorScheme,
    typography = Typography,
    content = content
  )
}
