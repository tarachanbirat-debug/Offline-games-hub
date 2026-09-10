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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.model.GameItem
import com.example.ui.components.GameCard
import com.example.ui.components.GameCardArtwork
import com.example.ui.theme.*

@Composable
fun GameHubScreen(
  games: List<GameItem>,
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onGamePlay: (GameItem) -> Unit,
  onGameCardClick: (GameItem) -> Unit,
  onToggleFavorite: (GameItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("ALL") }
  var offlineOnlyFilter by remember { mutableStateOf(false) }

  val categories = listOf("ALL", "PUZZLE", "ARCADE", "RACING", "BOARD", "REFLEX", "FAVORITES")

  val filteredGames = remember(games, searchQuery, selectedCategory, offlineOnlyFilter) {
    games.filter { game ->
      val matchesSearch = searchQuery.isBlank() ||
        game.title.contains(searchQuery, ignoreCase = true) ||
        game.description.contains(searchQuery, ignoreCase = true) ||
        game.genre.contains(searchQuery, ignoreCase = true)

      val matchesCategory = when (selectedCategory) {
        "ALL" -> true
        "FAVORITES" -> game.isFavorite
        else -> game.category.equals(selectedCategory, ignoreCase = true)
      }

      val matchesOffline = !offlineOnlyFilter || game.offlineMode

      matchesSearch && matchesCategory && matchesOffline
    }
  }

  val featuredGame = remember(games) {
    games.firstOrNull { it.id == "water_sort" } ?: games.firstOrNull()
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
    // 1. Vault Brand Header & Live Stats Bar (Part 12)
    item(span = { GridItemSpan(maxLineSpan) }) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "GAME VAULT STUDIO",
              color = CandyLemon,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              letterSpacing = 1.4.sp
            )
            Text(
              text = "Personal Arcade Hub",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
          }

          // Total Games Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(VaultSurfaceHighlight)
              .border(1.dp, VaultBorderGlow, RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = "${games.size} VAULT GAMES",
              color = CandyMint,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 10.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Platform Stats Bar (Total Games, Offline Ready, Total Plays, Favorites)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VaultSurfaceElevated)
            .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          StatsMetric(label = "OFFLINE READY", value = "${games.count { it.offlineMode }}", color = CandyMint)
          StatsMetric(label = "TOTAL PLAYS", value = "${games.sumOf { it.totalPlays }}", color = CandyCyan)
          StatsMetric(label = "FAVORITES", value = "${games.count { it.isFavorite }}", color = CandyWatermelon)
          StatsMetric(label = "ENGINES", value = "8 NATIVE", color = CandyLemon)
        }
      }
    }

    // 2. Hero Banner (Part 11)
    if (featuredGame != null && searchQuery.isBlank() && selectedCategory == "ALL") {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  CandyWatermelon.copy(alpha = 0.35f),
                  VaultSurfaceElevated,
                  Color(0xFF281845)
                )
              )
            )
            .border(1.5.dp, CandyWatermelon.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
            .clickable { onGamePlay(featuredGame) }
            .padding(14.dp)
            .testTag("hero_featured_banner")
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Text Details
            Column(
              modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(CandyWatermelon)
                  .padding(horizontal = 7.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "FEATURED FLAGSHIP",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Text(
                text = featuredGame.title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp)
              )

              Text(
                text = featuredGame.description,
                color = VaultTextSecondary,
                fontSize = 11.sp,
                maxLines = 2,
                lineHeight = 14.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
              )

              // PLAY NOW BUTTON
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(CandyMint)
                  .clickable { onGamePlay(featuredGame) }
                  .padding(horizontal = 14.dp, vertical = 6.dp)
                  .testTag("hero_play_btn")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("PLAY NOW", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
              }
            }

            // Hero Artwork
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(18.dp))
                .background(VaultCardDark.copy(alpha = 0.6f))
            ) {
              GameCardArtwork(gameId = featuredGame.id, modifier = Modifier.fillMaxSize())
            }
          }
        }
      }
    }

    // 3. Search Bar
    item(span = { GridItemSpan(maxLineSpan) }) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorderGlow, RoundedCornerShape(18.dp))
          .padding(horizontal = 14.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = VaultTextSecondary)
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search 10+ Vault games...", color = VaultTextMuted, fontSize = 13.sp) },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("hub_search_input")
        )
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Close, contentDescription = "Clear", tint = VaultTextMuted)
          }
        }
      }
    }

    // 4. Category & Filter Chips Scrollable Row
    item(span = { GridItemSpan(maxLineSpan) }) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Offline Filter Toggle Chip
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (offlineOnlyFilter) CandyMint else VaultSurfaceElevated)
            .border(1.dp, CandyMint, RoundedCornerShape(14.dp))
            .clickable {
              offlineOnlyFilter = !offlineOnlyFilter
              soundEngine.playTap()
              hapticEngine.vibrateTap()
            }
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("hub_filter_offline")
        ) {
          Text(
            text = "⚡ OFFLINE ONLY",
            color = if (offlineOnlyFilter) Color.Black else CandyMint,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp
          )
        }

        // Category Pills
        categories.forEach { cat ->
          val isSelected = (selectedCategory == cat)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) CandyCyan else VaultSurfaceElevated)
              .border(1.dp, if (isSelected) Color.White else VaultBorder, RoundedCornerShape(14.dp))
              .clickable {
                selectedCategory = cat
                soundEngine.playTap()
                hapticEngine.vibrateTap()
              }
              .padding(horizontal = 12.dp, vertical = 7.dp)
              .testTag("hub_cat_$cat")
          ) {
            Text(
              text = cat,
              color = if (isSelected) Color.Black else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 5. Game Cards Grid (Candy Cards)
    items(filteredGames, key = { it.id }) { game ->
      GameCard(
        game = game,
        onPlayClick = { onGamePlay(game) },
        onCardClick = { onGameCardClick(game) },
        onFavoriteClick = { onToggleFavorite(game) }
      )
    }
  }
}

@Composable
private fun StatsMetric(label: String, value: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(label, color = VaultTextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
  }
}
