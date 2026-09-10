package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameItem
import com.example.ui.theme.*

@Composable
fun GameDetailModal(
  game: GameItem,
  onDismiss: () -> Unit,
  onPlay: () -> Unit,
  onChangeDifficulty: (String) -> Unit
) {
  val candyColor = Color(game.candyColorHex)

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(28.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(
              VaultSurfaceElevated,
              VaultSurface,
              VaultBackground
            )
          )
        )
        .border(1.5.dp, candyColor.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
        .padding(18.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Row with Close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Offline/Online tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (game.offlineMode) CandyMint.copy(alpha = 0.2f) else CandySkyBlue.copy(alpha = 0.2f))
              .border(1.dp, if (game.offlineMode) CandyMint else CandySkyBlue, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = if (game.offlineMode) "⚡ OFFLINE READY" else "🌐 ONLINE RUNTIME",
              color = if (game.offlineMode) CandyMint else CandySkyBlue,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(VaultSurfaceHighlight)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }

        // Preview Artwork
        Box(
          modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(VaultCardDark)
            .border(1.dp, candyColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        ) {
          GameCardArtwork(gameId = game.id, modifier = Modifier.fillMaxSize())
        }

        // Title
        Text(
          text = game.title,
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          modifier = Modifier.padding(top = 2.dp)
        )

        // Category & Tech Subtitle
        Text(
          text = "${game.category} • ${game.technology} • ${game.license}",
          color = candyColor,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        // Description
        Text(
          text = game.description,
          color = VaultTextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        // Specs Grid
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VaultCardDark)
            .padding(vertical = 8.dp, horizontal = 12.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CONTROLS", color = VaultTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(game.controls, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("BEST SCORE", color = VaultTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("${game.highScore}", color = CandyLemon, fontSize = 11.sp, fontWeight = FontWeight.Black)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PLAYS", color = VaultTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("${game.totalPlays}", color = CandyMint, fontSize = 11.sp, fontWeight = FontWeight.Black)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Difficulty Selector (Actually modifies gameplay!)
        Text("SELECT DIFFICULTY", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("EASY", "MEDIUM", "HARD", "EXPERT").forEach { diff ->
            val isSelected = (game.difficulty == diff)
            val pillColor = when (diff) {
              "EASY" -> CandyMint
              "MEDIUM" -> CandyLemon
              "HARD" -> CandyTangerine
              "EXPERT" -> CandyWatermelon
              else -> CandyCyan
            }
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) pillColor else VaultCardDark)
                .border(1.dp, if (isSelected) Color.White else pillColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .clickable { onChangeDifficulty(diff) }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = diff,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big PLAY NOW Action Button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
              Brush.horizontalGradient(
                listOf(candyColor, candyColor.copy(alpha = 0.85f))
              )
            )
            .clickable(onClick = onPlay)
            .padding(vertical = 12.dp)
            .testTag("modal_play_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "PLAY NOW",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 15.sp,
              letterSpacing = 1.sp
            )
          }
        }
      }
    }
  }
}
