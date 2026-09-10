package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.data.GameVaultDatabase
import com.example.data.GameVaultRepository
import com.example.discovery.VaultDiscoveryEngine
import com.example.games.brickbreaker.BrickBreakerScreen
import com.example.games.game2048.Game2048Screen
import com.example.games.highwayracer.HighwayRacerScreen
import com.example.games.memorymatch.MemoryMatchScreen
import com.example.games.miniludo.MiniLudoScreen
import com.example.games.snake.SnakeScreen
import com.example.games.tictactoe.TicTacToeScreen
import com.example.games.watersort.WaterSortScreen
import com.example.model.GameItem
import com.example.ui.components.GameDetailModal
import com.example.ui.particles.ParticleOverlay
import com.example.ui.particles.ParticleSystem
import com.example.ui.screens.DiscoveryScreen
import com.example.ui.screens.GameHubScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WebGameScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class VaultTab(val title: String, val icon: ImageVector) {
  HUB("GAMES", Icons.Default.Games),
  DISCOVERY("DISCOVER", Icons.Default.Build),
  SETTINGS("VAULT", Icons.Default.Settings)
}

@Composable
fun GameVaultApp() {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val db = remember { GameVaultDatabase.getDatabase(context) }
  val repository = remember { GameVaultRepository(db.gameDao(), context) }
  val soundEngine = remember { VaultSoundEngine(context) }
  val hapticEngine = remember { VaultHapticEngine(context) }
  val discoveryEngine = remember { VaultDiscoveryEngine() }
  val particleSystem = remember { ParticleSystem() }

  // Initialize sound / haptic settings from repo
  LaunchedEffect(Unit) {
    soundEngine.isMuted = !repository.getSoundEnabled()
    hapticEngine.isMuted = !repository.getHapticsEnabled()
    repository.initializeDefaultGamesIfEmpty()
  }

  val games by repository.allGames.collectAsState(initial = emptyList())

  var currentTab by remember { mutableStateOf(VaultTab.HUB) }
  var activeGame by remember { mutableStateOf<GameItem?>(null) }
  var previewGame by remember { mutableStateOf<GameItem?>(null) }

  fun launchGame(game: GameItem) {
    soundEngine.playSnap()
    hapticEngine.vibrateMove()
    scope.launch {
      repository.recordGamePlay(game.id)
    }
    activeGame = game
  }

  fun closeGame() {
    soundEngine.playPop()
    hapticEngine.vibrateTap()
    activeGame = null
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    // Top Level Screens: Active Game vs Hub Navigation
    AnimatedContent(
      targetState = activeGame,
      transitionSpec = {
        fadeIn(animationSpec = tween(220)) togetherWith
        fadeOut(animationSpec = tween(220))
      },
      label = "game_launch_transition"
    ) { game ->
      if (game != null) {
        // Active Game Screen
        when (game.id) {
          "tictactoe", "tic_tac_toe" -> {
            TicTacToeScreen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          "game_2048", "2048" -> {
            Game2048Screen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              highScore = game.highScore,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          "snake" -> {
            SnakeScreen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              highScore = game.highScore,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          "water_sort" -> {
            WaterSortScreen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          "highway_racer" -> {
            HighwayRacerScreen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              highScore = game.highScore,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          "mini_ludo" -> {
            MiniLudoScreen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          "brick_breaker" -> {
            BrickBreakerScreen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              highScore = game.highScore,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          "memory_match" -> {
            MemoryMatchScreen(
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              particleSystem = particleSystem,
              difficulty = game.difficulty,
              highScore = game.highScore,
              onBack = { closeGame() },
              onScoreUpdated = { score ->
                scope.launch { repository.recordGamePlay(game.id, score) }
              }
            )
          }
          else -> {
            // Web / Sandboxed Container
            WebGameScreen(
              game = game,
              soundEngine = soundEngine,
              hapticEngine = hapticEngine,
              onBack = { closeGame() }
            )
          }
        }
      } else {
        // Vault Main Navigation Content
        Scaffold(
          containerColor = VaultBackground,
          contentWindowInsets = WindowInsets.statusBars,
          bottomBar = {
            VaultBottomNavigation(
              currentTab = currentTab,
              onTabSelected = { tab ->
                currentTab = tab
                soundEngine.playTap()
                hapticEngine.vibrateTap()
              }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (currentTab) {
              VaultTab.HUB -> {
                GameHubScreen(
                  games = games,
                  soundEngine = soundEngine,
                  hapticEngine = hapticEngine,
                  onGamePlay = { gameToPlay -> launchGame(gameToPlay) },
                  onGameCardClick = { clickedGame -> previewGame = clickedGame },
                  onToggleFavorite = { favGame ->
                    scope.launch {
                      repository.toggleFavorite(favGame.id, favGame.isFavorite)
                    }
                    soundEngine.playTap()
                    hapticEngine.vibrateTap()
                  }
                )
              }
              VaultTab.DISCOVERY -> {
                DiscoveryScreen(
                  discoveryEngine = discoveryEngine,
                  soundEngine = soundEngine,
                  hapticEngine = hapticEngine,
                  onImportSuccess = { newGame ->
                    scope.launch {
                      repository.addNewGame(newGame)
                    }
                    currentTab = VaultTab.HUB
                  }
                )
              }
              VaultTab.SETTINGS -> {
                SettingsScreen(
                  repository = repository,
                  soundEngine = soundEngine,
                  hapticEngine = hapticEngine,
                  games = games
                )
              }
            }
          }
        }
      }
    }

    // Game Detail & Preview Modal (Part 19)
    previewGame?.let { game ->
      GameDetailModal(
        game = game,
        onDismiss = { previewGame = null },
        onPlay = {
          previewGame = null
          launchGame(game)
        },
        onChangeDifficulty = { newDiff ->
          scope.launch {
            repository.updateDifficulty(game.id, newDiff)
          }
          previewGame = game.copy(difficulty = newDiff)
          soundEngine.playSnap()
          hapticEngine.vibrateTap()
        }
      )
    }

    // Top-Level Juice / Particle Overlay (Confetti, Sparkles, Dust)
    ParticleOverlay(
      particleSystem = particleSystem,
      modifier = Modifier.fillMaxSize()
    )
  }
}

@Composable
private fun VaultBottomNavigation(
  currentTab: VaultTab,
  onTabSelected: (VaultTab) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 24.dp, vertical = 8.dp)
      .clip(RoundedCornerShape(26.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            VaultSurfaceHighlight.copy(alpha = 0.92f),
            VaultSurfaceElevated.copy(alpha = 0.98f)
          )
        )
      )
      .border(1.2.dp, VaultBorderGlow, RoundedCornerShape(26.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically
  ) {
    VaultTab.values().forEach { tab ->
      val isSelected = (currentTab == tab)
      val tintColor = when (tab) {
        VaultTab.HUB -> CandyMint
        VaultTab.DISCOVERY -> CandyCyan
        VaultTab.SETTINGS -> CandyLemon
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clip(RoundedCornerShape(18.dp))
          .background(if (isSelected) tintColor.copy(alpha = 0.18f) else Color.Transparent)
          .border(
            if (isSelected) 1.dp else 0.dp,
            if (isSelected) tintColor.copy(alpha = 0.6f) else Color.Transparent,
            RoundedCornerShape(18.dp)
          )
          .clickable { onTabSelected(tab) }
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .testTag("nav_tab_${tab.name.lowercase()}")
      ) {
        Icon(
          imageVector = tab.icon,
          contentDescription = tab.title,
          tint = if (isSelected) tintColor else VaultTextMuted,
          modifier = Modifier.size(22.dp)
        )
        Text(
          text = tab.title,
          color = if (isSelected) Color.White else VaultTextMuted,
          fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
          fontSize = 10.sp,
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}
