package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val JeevixColorScheme = lightColorScheme(
  primary = JeevixRed,
  onPrimary = JeevixWhite,
  primaryContainer = JeevixRedLight,
  onPrimaryContainer = JeevixRed,
  secondary = JeevixRed,
  onSecondary = JeevixWhite,
  background = JeevixWhite,
  onBackground = JeevixTextDark,
  surface = JeevixWhite,
  onSurface = JeevixTextDark,
  surfaceVariant = JeevixOffWhite,
  onSurfaceVariant = JeevixTextMuted,
  outline = JeevixBorder,
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = JeevixColorScheme,
    typography = Typography,
    content = content
  )
}

