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
import androidx.compose.ui.draw.shadow
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
  initialCategory: String = "ALL",
  onOpenSettings: () -> Unit = {},
  onOpenOnline: () -> Unit = {},
  onGamePlay: (GameItem) -> Unit,
  onGameCardClick: (GameItem) -> Unit,
  onToggleFavorite: (GameItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(initialCategory) }

  // Sync if initialCategory changes externally (e.g. via navigation tab)
  LaunchedEffect(initialCategory) {
    selectedCategory = initialCategory
  }

  val categories = listOf("ALL", "PUZZLE", "BOARD", "ARCADE", "REFLEX", "FAVORITES")

  val filteredGames = remember(games, searchQuery, selectedCategory) {
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

      matchesSearch && matchesCategory
    }
  }

  val featuredGames = remember(games) {
    games.take(4)
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
    // 1. Top Profile & Action Bar (Matching Reference Screenshot unnamed.webp)
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
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(CandyWatermelon, CandyTangerine))),
              contentAlignment = Alignment.Center
            ) {
              Text("🎮", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Player 1",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "LVL 5 • NO WIFI",
                color = CandyMint,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 9.sp
              )
            }
          }

          // Right: Coin Counter & Settings Gear
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
                  text = "1,450",
                  color = VaultGold,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
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

        // Hero Slogan Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  CandySkyBlue.copy(alpha = 0.35f),
                  VaultSurfaceElevated,
                  CandyMint.copy(alpha = 0.25f)
                )
              )
            )
            .border(1.dp, VaultBorderGlow, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Unlimited Fun",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
              )
              Text(
                text = "Challenge Yourself Offline • 100% Free",
                color = VaultTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CandyMint)
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = "10 GAMES",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Online Games Portal Card (offlinegames.wshareit.com)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(
                  Color(0xFF0F2027),
                  Color(0xFF203A43),
                  Color(0xFF2C5364)
                )
              )
            )
            .border(1.5.dp, Brush.horizontalGradient(listOf(CandyCyan, CandyLemon)), RoundedCornerShape(20.dp))
            .clickable {
              soundEngine.playSnap()
              hapticEngine.vibrateSuccess()
              onOpenOnline()
            }
            .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Brush.radialGradient(listOf(CandyCyan, Color(0xFF0091EA)))),
                contentAlignment = Alignment.Center
              ) {
                Text("🌐", fontSize = 22.sp)
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "ONLINE ARCADE",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(CandyLemon)
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = "EMBEDDED",
                      color = Color.Black,
                      fontWeight = FontWeight.Black,
                      fontSize = 8.sp
                    )
                  }
                }
                Text(
                  text = "GameDistribution, Itch.io & 100+ Free Instant Games",
                  color = CandyMint,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(top = 1.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
              onClick = {
                soundEngine.playSnap()
                hapticEngine.vibrateSuccess()
                onOpenOnline()
              },
              colors = ButtonDefaults.buttonColors(containerColor = CandyCyan),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text("PLAY >", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
          }
        }
      }
    }

    // 2. Quick-Play Mini Featured Carousel (Save Me Out, Word Guess, Ludo, 2048)
    if (searchQuery.isBlank() && selectedCategory == "ALL") {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "POPULAR NOW",
            color = CandyLemon,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = 4.dp)
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            featuredGames.forEach { featGame ->
              val cardColor = Color(featGame.candyColorHex)
              Box(
                modifier = Modifier
                  .width(135.dp)
                  .height(145.dp)
                  .clip(RoundedCornerShape(18.dp))
                  .background(VaultSurfaceElevated)
                  .border(1.2.dp, cardColor.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                  .clickable { onGamePlay(featGame) }
                  .padding(8.dp)
              ) {
                Column(
                  modifier = Modifier.fillMaxSize(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.SpaceBetween
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(78.dp)
                      .clip(RoundedCornerShape(14.dp))
                  ) {
                    GameCardArtwork(gameId = featGame.id, modifier = Modifier.fillMaxSize())
                  }

                  Text(
                    text = featGame.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    maxLines = 1
                  )

                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(cardColor)
                      .padding(vertical = 3.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "PLAY NOW",
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp
                    )
                  }
                }
              }
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
          .border(1.dp, VaultBorder, RoundedCornerShape(18.dp))
          .padding(horizontal = 14.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = VaultTextSecondary)
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search games (e.g. Ludo, 2048, Snake)...", color = VaultTextMuted, fontSize = 13.sp) },
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

    // 4. Category Filter Chips
    item(span = { GridItemSpan(maxLineSpan) }) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        categories.forEach { cat ->
          val isSelected = (selectedCategory == cat)
          val chipIcon = when (cat) {
            "ALL" -> "🎮"
            "PUZZLE" -> "🧩"
            "BOARD" -> "🎲"
            "ARCADE" -> "🕹️"
            "REFLEX" -> "⚡"
            "FAVORITES" -> "⭐"
            else -> "🎯"
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) CandyCyan else VaultSurfaceElevated)
              .border(1.dp, if (isSelected) Color.White else VaultBorder, RoundedCornerShape(16.dp))
              .clickable {
                selectedCategory = cat
                soundEngine.playTap()
                hapticEngine.vibrateTap()
              }
              .padding(horizontal = 14.dp, vertical = 8.dp)
              .testTag("hub_cat_$cat")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = chipIcon, fontSize = 12.sp)
              Spacer(modifier = Modifier.width(5.dp))
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

    // 5. Game Cards Grid (Casual Game Tiles)
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
