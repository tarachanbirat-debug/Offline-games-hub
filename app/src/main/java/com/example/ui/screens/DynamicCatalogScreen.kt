package com.example.ui.screens

import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.GamePlayerActivity
import com.example.api.GamePixGameDto
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.data.GameCatalogState
import com.example.data.GamePixCatalogRepository
import com.example.network.NetworkChecker
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DynamicCatalogScreen(
  repository: GamePixCatalogRepository,
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val catalogState by repository.catalogState.collectAsState()

  // Reactive Network Status
  val isOnline by NetworkChecker.observeNetwork(context).collectAsState(initial = NetworkChecker.isOnline(context))

  var selectedCategory by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }

  val categories = listOf("All", "Arcade", "Action", "Puzzle", "Sports")

  LaunchedEffect(isOnline) {
    if (isOnline) {
      repository.loadGames()
    }
  }

  val gamesList = remember(catalogState, selectedCategory, searchQuery) {
    repository.filterGames(selectedCategory, searchQuery)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
      .systemBarsPadding()
  ) {
    // 1. Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(VaultSurfaceElevated)
            .testTag("catalog_back_btn")
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
            text = "Online Arcade",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
          )
          Text(
            text = "Dynamic 100+ GamePix Catalog",
            color = CandyCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      IconButton(
        onClick = {
          soundEngine.playTap()
          hapticEngine.vibrateTap()
          scope.launch { repository.loadGames(selectedCategory, forceRefresh = true) }
        },
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(VaultSurfaceElevated)
          .testTag("catalog_refresh_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Refresh Feed",
          tint = Color.White
        )
      }
    }

    // 2. Network Guard Check: If offline, show the clean requested fallback
    if (!isOnline && gamesList.isEmpty()) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, VaultBorderGlow, RoundedCornerShape(24.dp))
        ) {
          Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CandyWatermelon.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = CandyWatermelon,
                modifier = Modifier.size(32.dp)
              )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
              text = "No Internet Connection",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Connect to internet to access 100+ cloud games",
              color = VaultTextSecondary,
              fontSize = 13.sp,
              textAlign = TextAlign.Center,
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
              onClick = onBack,
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = CandyMint),
              modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Back to Offline Vault",
                  color = Color.Black,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp
                )
              }
            }
          }
        }
      }
    } else {
      // 3. High Performance 2-Column Catalog Grid
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .testTag("catalog_grid"),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Search Bar Header
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
              placeholder = { Text("Search 100+ cloud games...", color = VaultTextMuted, fontSize = 13.sp) },
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
                .testTag("catalog_search_input")
            )
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = VaultTextMuted)
              }
            }
          }
        }

        // Category Tabs Header ([All], [Arcade], [Action], [Puzzle], [Sports])
        item(span = { GridItemSpan(maxLineSpan) }) {
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
                  .clip(RoundedCornerShape(16.dp))
                  .background(if (isSelected) CandyCyan else VaultSurfaceElevated)
                  .border(
                    1.dp,
                    if (isSelected) Color.White else VaultBorder,
                    RoundedCornerShape(16.dp)
                  )
                  .clickable {
                    soundEngine.playTap()
                    hapticEngine.vibrateTap()
                    selectedCategory = cat
                  }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
                  .testTag("catalog_tab_$cat")
              ) {
                Text(
                  text = cat.uppercase(),
                  color = if (isSelected) Color.Black else Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }
            }
          }
        }

        // Count Indicator
        item(span = { GridItemSpan(maxLineSpan) }) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "SHOWING ${gamesList.size} CLOUD GAMES",
              color = VaultTextSecondary,
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            if (catalogState is GameCatalogState.Loading) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CandyCyan, strokeWidth = 2.dp)
            }
          }
        }

        // 2-Column Game Cards
        items(gamesList, key = { it.id }) { game ->
          CatalogGameCard(
            game = game,
            onClick = {
              soundEngine.playSnap()
              hapticEngine.vibrateSuccess()
              GamePlayerActivity.launch(
                context = context,
                gameId = game.id,
                title = game.title,
                url = game.getEffectivePlayUrl(),
                isOffline = false
              )
            }
          )
        }
      }
    }
  }
}

@Composable
private fun CatalogGameCard(
  game: GamePixGameDto,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .testTag("catalog_card_${game.id}")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Thumbnail with Category Tag & Rating Overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp)
          .background(Color(0xFF1E1638))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(game.getEffectiveThumbnail())
            .crossfade(true)
            .build(),
          contentDescription = game.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient vignette
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xAA000000))
              )
            )
        )

        // Category Tag
        Box(
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.TopStart)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xCC000000))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text(
            text = game.getNormalizedCategory(),
            color = CandyCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
          )
        }

        // Quality rating badge
        Box(
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.BottomEnd)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xCC000000))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "★ ${String.format(Locale.US, "%.1f", game.quality ?: 4.9)}",
            color = VaultGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Metadata & Play Button
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp)
      ) {
        Text(
          text = game.title,
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = game.description?.ifBlank { "Instant HTML5 play" } ?: "Instant HTML5 play",
          color = VaultTextMuted,
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = onClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = CandyCyan),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text(
            text = "PLAY",
            color = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
