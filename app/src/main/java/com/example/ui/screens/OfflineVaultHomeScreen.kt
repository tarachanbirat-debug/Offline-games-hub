package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.GamePlayerActivity
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.model.OfflineGame
import com.example.model.OfflineGamesCatalog
import com.example.ui.theme.*

@Composable
fun OfflineVaultHomeScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onOpenSettings: () -> Unit = {},
  onOpenOnlineArcade: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("ALL") }

  val categories = listOf("ALL", "ARCADE", "PUZZLE", "STRATEGY")

  val filteredGames = remember(searchQuery, selectedCategory) {
    OfflineGamesCatalog.games.filter { game ->
      val matchesSearch = searchQuery.isBlank() ||
        game.title.contains(searchQuery, ignoreCase = true) ||
        game.description.contains(searchQuery, ignoreCase = true) ||
        game.genre.contains(searchQuery, ignoreCase = true)

      val matchesCategory = (selectedCategory == "ALL") ||
        game.category.equals(selectedCategory, ignoreCase = true)

      matchesSearch && matchesCategory
    }
  }

  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 160.dp),
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
      .padding(horizontal = 14.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // 1. Top Header & User Status
    item(span = { GridItemSpan(maxLineSpan) }) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: User Profile Pill
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(VaultSurfaceElevated)
              .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
              .clickable {
                soundEngine.playTap()
                hapticEngine.vibrateTap()
                onOpenSettings()
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(CandyMint, CandyCyan))),
              contentAlignment = Alignment.Center
            ) {
              Text("⚡", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Offline Vault",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "● ZERO LATENCY",
                color = CandyMint,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 9.sp
              )
            }
          }

          // Right: Star Coins & Settings
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(VaultSurfaceElevated)
                .border(1.dp, VaultGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "OFFLINE",
                  color = VaultGold,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = {
                soundEngine.playTap()
                hapticEngine.vibrateTap()
                onOpenSettings()
              },
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(VaultSurfaceElevated)
                .border(1.dp, VaultBorder, CircleShape)
            ) {
              Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Offline Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  CandyMint.copy(alpha = 0.25f),
                  VaultSurfaceElevated,
                  CandyCyan.copy(alpha = 0.25f)
                )
              )
            )
            .border(1.dp, CandyMint.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CandyMint)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "100% OFFLINE ARCADE",
                  color = CandyMint,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  letterSpacing = 1.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Zero Latency • No Internet Required",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
              )
              Text(
                text = "Touch-optimized HTML5 Canvas & WebGL local bundles",
                color = VaultTextSecondary,
                fontSize = 11.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CandyMint)
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "${OfflineGamesCatalog.games.size} READY",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(VaultSurfaceElevated)
            .border(1.dp, VaultBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Search, contentDescription = null, tint = VaultTextSecondary)
          Spacer(modifier = Modifier.width(8.dp))
          TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search offline games...", color = VaultTextMuted, fontSize = 13.sp) },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              disabledContainerColor = Color.Transparent,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
          )
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = VaultTextMuted)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          categories.forEach { cat ->
            val isSelected = (selectedCategory.equals(cat, ignoreCase = true))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) CandyMint else VaultSurfaceElevated)
                .border(
                  1.dp,
                  if (isSelected) Color.White else VaultBorder,
                  RoundedCornerShape(14.dp)
                )
                .clickable {
                  soundEngine.playTap()
                  hapticEngine.vibrateTap()
                  selectedCategory = cat
                }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("offline_tab_$cat")
            ) {
              Text(
                text = cat,
                color = if (isSelected) Color.Black else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // 2. Offline Game Cards
    items(filteredGames, key = { it.id }) { game ->
      OfflineGameGridCard(
        game = game,
        onClick = {
          soundEngine.playSnap()
          hapticEngine.vibrateSuccess()
          GamePlayerActivity.launch(
            context = context,
            gameId = game.id,
            title = game.title,
            url = game.assetUrl,
            isOffline = true
          )
        }
      )
    }
  }
}

@Composable
fun OfflineGameGridCard(
  game: OfflineGame,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
    modifier = modifier
      .fillMaxWidth()
      .border(1.2.dp, VaultBorder, RoundedCornerShape(22.dp))
      .clickable { onClick() }
      .testTag("offline_game_card_${game.id}")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // 1. Artwork & Header with Emoji Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp)
          .background(
            Brush.verticalGradient(
              colors = listOf(
                game.accentColor.copy(alpha = 0.35f),
                Color(0xFF1E1638)
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = game.emoji,
          fontSize = 44.sp
        )

        // Rating Badge (Top Right)
        Box(
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xCC000000))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "★ ${game.rating}",
            color = VaultGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 2. Body Details
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
      ) {
        // Distinct "Offline Ready • No Internet" Green Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CandyMint.copy(alpha = 0.16f))
            .border(1.dp, CandyMint.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(CandyMint)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Offline Ready • No Internet",
              color = CandyMint,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
          text = game.title,
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Genre / Category
        Text(
          text = "${game.category} • ${game.genre}",
          color = CandyCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Description
        Text(
          text = game.description,
          color = VaultTextMuted,
          fontSize = 11.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Instant Play Button
        Button(
          onClick = onClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = game.accentColor),
          contentPadding = PaddingValues(0.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "PLAY INSTANTLY",
              color = Color.Black,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}
