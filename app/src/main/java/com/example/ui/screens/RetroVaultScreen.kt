package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.GamePlayerActivity
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.theme.*

data class RetroGameItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val engineBadge: String,
  val license: String,
  val playUrl: String,
  val accentColor: Color,
  val emoji: String,
  val isOffline: Boolean = false
)

@Composable
fun RetroVaultScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val retroGames = listOf(
    RetroGameItem(
      id = "retro-nes-emulator",
      title = "8-Bit Retro NES Container",
      subtitle = "Integrated CRT Arcade Emulator with Virtual D-Pad & A/B buttons",
      engineBadge = "WASM / 8-BIT EMULATOR",
      license = "Open Source / Homebrew",
      playUrl = "file:///android_asset/games/retro_nes/index.html",
      accentColor = CandyMint,
      emoji = "🕹️",
      isOffline = true
    ),
    RetroGameItem(
      id = "pacman-canvas",
      title = "Pac-Man Canvas",
      subtitle = "Authentic retro arcade maze runner with ghost AI pathfinding",
      engineBadge = "HTML5 CANVAS",
      license = "MIT License",
      playUrl = "https://platzh1rsch.github.io/pacman-canvas/",
      accentColor = CandyLemon,
      emoji = "👾"
    ),
    RetroGameItem(
      id = "hextris",
      title = "Hextris Fast Hexagon",
      subtitle = "Addictive fast-paced hexagonal puzzle game with neon physics",
      engineBadge = "WEBGL / CANVAS",
      license = "GPL-3.0",
      playUrl = "https://hextris.github.io/hextris/",
      accentColor = CandyCyan,
      emoji = "⬢"
    ),
    RetroGameItem(
      id = "clumsy-bird",
      title = "Clumsy Bird",
      subtitle = "MelonJS canvas port of the iconic flapping airborne obstacle runner",
      engineBadge = "MELONJS ENGINE",
      license = "MIT License",
      playUrl = "https://ellisonleao.github.io/clumsy-bird/",
      accentColor = CandyWatermelon,
      emoji = "🐦"
    ),
    RetroGameItem(
      id = "canvas-tetris",
      title = "Canvas Retro Tetris",
      subtitle = "Classic falling tetrominoes with authentic sound, levels, and DAS controls",
      engineBadge = "REACT / CANVAS",
      license = "MIT License",
      playUrl = "https://chvin.github.io/react-tetris/",
      accentColor = CandyGrape,
      emoji = "🧱"
    ),
    RetroGameItem(
      id = "tower-blocks",
      title = "Tower Blocks 3D",
      subtitle = "Three.js geometric tower balance stacking game with dynamic lighting",
      engineBadge = "THREE.JS / WEBGL",
      license = "MIT License",
      playUrl = "https://iamkun.github.io/tower_game/",
      accentColor = CandyTangerine,
      emoji = "🏗️"
    ),
    RetroGameItem(
      id = "cyber-neon-pong",
      title = "Neon Cyber Pong",
      subtitle = "Responsive touch paddle vs predictive AI with synthetic audio",
      engineBadge = "OFFLINE CANVAS",
      license = "Native Built-in",
      playUrl = "file:///android_asset/games/pong/index.html",
      accentColor = CandyCyan,
      emoji = "🏓",
      isOffline = true
    ),
    RetroGameItem(
      id = "snake-retro-offline",
      title = "Retro Pixel Snake",
      subtitle = "Classic snake game with touch d-pad, swipe controls, and fruit bonus",
      engineBadge = "OFFLINE CANVAS",
      license = "Native Built-in",
      playUrl = "file:///android_asset/games/snake/index.html",
      accentColor = CandyMint,
      emoji = "🐍",
      isOffline = true
    ),
    RetroGameItem(
      id = "memory-matrix-offline",
      title = "Pattern Memory Matrix",
      subtitle = "Cognitive brain grid test memorizing randomized light patterns",
      engineBadge = "OFFLINE CANVAS",
      license = "Native Built-in",
      playUrl = "file:///android_asset/games/memory_matrix/index.html",
      accentColor = CandyTangerine,
      emoji = "🧠",
      isOffline = true
    )
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
      .systemBarsPadding()
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(VaultSurfaceElevated)
          .testTag("retro_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "Retro & Classic Vault",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp
        )
        Text(
          text = "WASM • WebAssembly • Emulator • GitHub Pages",
          color = CandyMint,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .testTag("retro_vault_list"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1638)),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CandyMint.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(CandyMint, CandyCyan))),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = "Preserved Classic Games",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Iconic open-source titles running in hardware-accelerated sandboxed containers without ads or redirects.",
                color = VaultTextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      items(retroGames, key = { it.id }) { game ->
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
            .clickable {
              soundEngine.playSnap()
              hapticEngine.vibrateSuccess()
              GamePlayerActivity.launch(
                context = context,
                gameId = game.id,
                title = game.title,
                url = game.playUrl,
                isOffline = game.isOffline
              )
            }
            .testTag("retro_card_${game.id}")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Left Emoji/Icon Box
            Box(
              modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF150F28))
                .border(1.5.dp, game.accentColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(game.emoji, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Center details
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = game.title,
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp
                )
                if (game.isOffline) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(CandyMint.copy(alpha = 0.2f))
                      .padding(horizontal = 5.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "OFFLINE",
                      color = CandyMint,
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Black
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = game.subtitle,
                color = VaultTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2
              )

              Spacer(modifier = Modifier.height(6.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = game.engineBadge,
                  color = game.accentColor,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 9.sp
                )
                Text(
                  text = "•",
                  color = VaultTextMuted,
                  fontSize = 9.sp
                )
                Text(
                  text = game.license,
                  color = VaultTextMuted,
                  fontSize = 9.sp
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Play Button
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(game.accentColor),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}
