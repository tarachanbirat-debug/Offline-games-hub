package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun GameVaultTheme(
  presetId: String = "pop_2d",
  content: @Composable () -> Unit
) {
  val preset = VaultThemeManager.presets.find { it.id == presetId } ?: VaultThemeManager.presets.first()

  val colorScheme = darkColorScheme(
    primary = preset.primaryAccent,
    onPrimary = Color.White,
    primaryContainer = preset.surfaceElevatedColor,
    onPrimaryContainer = preset.primaryAccent,
    secondary = preset.secondaryAccent,
    onSecondary = Color.Black,
    secondaryContainer = preset.surfaceHighlightColor,
    onSecondaryContainer = Color.White,
    tertiary = preset.primaryAccent,
    onTertiary = Color.White,
    background = preset.backgroundColor,
    onBackground = preset.textColor,
    surface = preset.surfaceColor,
    onSurface = preset.textColor,
    surfaceVariant = preset.surfaceElevatedColor,
    onSurfaceVariant = preset.textMutedColor,
    outline = preset.surfaceHighlightColor
  )

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
