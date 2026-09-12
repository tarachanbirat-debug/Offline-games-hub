package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun UniversalGameHeader(
  title: String,
  score: Int,
  bestScore: Int,
  difficulty: String,
  soundEnabled: Boolean,
  hapticsEnabled: Boolean,
  onBack: () -> Unit,
  onReset: () -> Unit,
  onToggleSound: () -> Unit,
  onToggleHaptics: () -> Unit,
  onDifficultyClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .clip(RoundedCornerShape(22.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            VaultSurfaceHighlight.copy(alpha = 0.85f),
            VaultSurfaceElevated.copy(alpha = 0.95f)
          )
        )
      )
      .border(1.dp, VaultBorderGlow.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Back Button
    IconButton(
      onClick = onBack,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(VaultSurfaceHighlight)
        .testTag("game_back_button")
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back to Vault",
        tint = Color.White
      )
    }

    // Title & Difficulty Pill
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = 6.dp)
    ) {
      Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        maxLines = 1
      )
      if (difficulty.isNotEmpty()) {
        val diffColor = when (difficulty) {
          "EASY" -> CandyMint
          "MEDIUM" -> CandyLemon
          "HARD" -> CandyTangerine
          "EXPERT" -> CandyWatermelon
          else -> CandyCyan
        }
        Box(
          modifier = Modifier
            .padding(top = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(diffColor.copy(alpha = 0.2f))
            .border(0.8.dp, diffColor, RoundedCornerShape(8.dp))
            .clickable(enabled = onDifficultyClick != null) { onDifficultyClick?.invoke() }
            .padding(horizontal = 6.dp, vertical = 1.dp)
        ) {
          Text(
            text = difficulty,
            color = diffColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
          )
        }
      }
    }

    // Score Badges
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Score Box
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(VaultCardDark.copy(alpha = 0.7f))
          .border(0.8.dp, VaultBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = "SCORE",
          color = VaultTextSecondary,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "$score",
          color = CandyLemon,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
      }

      // Best Score Box
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(VaultCardDark.copy(alpha = 0.7f))
          .border(0.8.dp, VaultBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = "BEST",
          color = VaultTextSecondary,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "$bestScore",
          color = CandyMint,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
      }

      // Reset
      IconButton(
        onClick = onReset,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(VaultSurfaceHighlight)
          .testTag("game_reset_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Restart Game",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }

      // Sound Toggle
      IconButton(
        onClick = onToggleSound,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (soundEnabled) VaultSurfaceHighlight else VaultCardDark)
          .testTag("game_sound_toggle")
      ) {
        Icon(
          imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
          contentDescription = "Toggle Sound",
          tint = if (soundEnabled) CandyCyan else VaultTextMuted,
          modifier = Modifier.size(18.dp)
        )
      }

      // Haptics Toggle
      IconButton(
        onClick = onToggleHaptics,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (hapticsEnabled) VaultSurfaceHighlight else VaultCardDark)
          .testTag("game_haptic_toggle")
      ) {
        Icon(
          imageVector = Icons.Default.Vibration,
          contentDescription = "Toggle Haptics",
          tint = if (hapticsEnabled) CandyWatermelon else VaultTextMuted,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
