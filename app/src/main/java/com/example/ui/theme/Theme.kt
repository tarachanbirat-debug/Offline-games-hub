package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VaultColorScheme = darkColorScheme(
  primary = CandyGrape,
  onPrimary = Color.White,
  primaryContainer = VaultSurfaceElevated,
  onPrimaryContainer = CandyCyan,
  secondary = CandyMint,
  onSecondary = Color.Black,
  secondaryContainer = VaultSurfaceHighlight,
  onSecondaryContainer = Color.White,
  tertiary = CandyWatermelon,
  onTertiary = Color.White,
  background = VaultBackground,
  onBackground = VaultTextPrimary,
  surface = VaultSurface,
  onSurface = VaultTextPrimary,
  surfaceVariant = VaultSurfaceElevated,
  onSurfaceVariant = VaultTextSecondary,
  outline = VaultBorderGlow
)

@Composable
fun GameVaultTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = VaultColorScheme,
    typography = Typography,
    content = content
  )
}
