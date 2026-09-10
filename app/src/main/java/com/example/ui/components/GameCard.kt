package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameItem
import com.example.ui.theme.*

@Composable
fun GameCard(
  game: GameItem,
  onPlayClick: () -> Unit,
  onCardClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.96f else 1.0f,
    animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
    label = "card_compression"
  )

  val cardCandyColor = Color(game.candyColorHex)

  Column(
    modifier = modifier
      .scale(scale)
      .fillMaxWidth()
      .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = cardCandyColor.copy(alpha = 0.35f))
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            VaultSurfaceElevated,
            VaultSurface,
            VaultCardDark
          )
        )
      )
      .border(1.5.dp, cardCandyColor.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onCardClick
      )
      .testTag("game_card_${game.id}")
  ) {
    // 70-85% Visual Artwork Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(175.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              cardCandyColor.copy(alpha = 0.22f),
              VaultSurface.copy(alpha = 0.9f)
            )
          )
        )
    ) {
      // Procedural 2.5D Artwork
      GameCardArtwork(
        gameId = game.id,
        modifier = Modifier.fillMaxSize()
      )

      // Top Floating Badges
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Offline / Online Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (game.offlineMode) CandyMint.copy(alpha = 0.22f) else CandySkyBlue.copy(alpha = 0.22f)
            )
            .border(
              1.dp,
              if (game.offlineMode) CandyMint else CandySkyBlue,
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
          Text(
            text = if (game.offlineMode) "⚡ OFFLINE" else "🌐 ONLINE",
            color = if (game.offlineMode) CandyMint else CandySkyBlue,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }

        // Favorite Toggle Button
        IconButton(
          onClick = onFavoriteClick,
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(VaultCardDark.copy(alpha = 0.7f))
            .testTag("fav_btn_${game.id}")
        ) {
          Icon(
            imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (game.isFavorite) CandyWatermelon else Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Bottom Category Pill inside Artwork
      Box(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(8.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(VaultCardDark.copy(alpha = 0.8f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "${game.category.uppercase()} • ${game.difficulty}",
          color = VaultTextSecondary,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Card Details Section
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
      // Title
      Text(
        text = game.title,
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      // Short Description
      Text(
        text = game.description,
        color = VaultTextSecondary,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
      )

      // Bottom Row: Stars / High Score & Action PLAY Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // High Score / Star info
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = VaultGold,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = if (game.highScore > 0) "BEST: ${game.highScore}" else "NEW",
            color = if (game.highScore > 0) CandyLemon else VaultTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Chunky 2.5D PLAY Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  cardCandyColor,
                  cardCandyColor.copy(alpha = 0.8f)
                )
              )
            )
            .clickable(onClick = onPlayClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("play_btn_${game.id}"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "PLAY",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              letterSpacing = 0.8.sp
            )
          }
        }
      }
    }
  }
}
