package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.GamePlayerActivity
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.model.OfflineGamesCatalog

@Composable
fun OfflineVaultHomeScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onOpenSettings: () -> Unit = {},
  onOpenOnlineArcade: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val games = OfflineGamesCatalog.games

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0B0F19))
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Top Header & Green Pill Badge ("100% Offline • No Wi-Fi Needed")
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Offline Vault",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF10B981).copy(alpha = 0.2f))
            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "100% Offline • No Wi-Fi Needed",
            color = Color(0xFF10B981),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      IconButton(
        onClick = {
          soundEngine.playTap()
          hapticEngine.vibrateTap()
          onOpenSettings()
        },
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(Color(0xFF1E293B))
          .border(1.dp, Color(0xFF334155), CircleShape)
      ) {
        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Modern 2-Column Grid of Premium Game Cards
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier
        .fillMaxSize()
        .weight(1f),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(bottom = 96.dp)
    ) {
      items(games) { game ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              soundEngine.playSnap()
              hapticEngine.vibrateSuccess()
              GamePlayerActivity.launch(
                context = context,
                gameId = game.id,
                title = game.title,
                url = game.assetUrl,
                isOffline = true
              )
            },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
          ) {
            // Visual Poster Banner with Rich Colorful Gradient & Emoji
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                  Brush.linearGradient(
                    colors = listOf(game.accentColor.copy(alpha = 0.8f), Color(0xFF0F172A))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = game.emoji,
                fontSize = 42.sp
              )
              
              // Genre Tag top-right
              Box(
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(8.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.Black.copy(alpha = 0.5f))
                  .padding(horizontal = 6.dp, vertical = 3.dp)
              ) {
                Text(
                  text = game.genre,
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Game Details & CTA
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = game.title,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = game.description,
                  color = Color(0xFF94A3B8),
                  fontSize = 11.sp,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Glowing "PLAY NOW ▶" CTA Button
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    Brush.horizontalGradient(
                      colors = listOf(Color(0xFFFF6B6B), Color(0xFFFE8C00))
                    )
                  )
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "PLAY NOW",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp,
                  letterSpacing = 0.5.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
