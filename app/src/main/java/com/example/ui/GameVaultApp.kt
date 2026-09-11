package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.audio.VaultLofiEngine
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.GamePlayerActivity
import com.example.data.GameVaultDatabase
import com.example.data.GameVaultRepository
import com.example.games.brickbreaker.BrickBreakerScreen
import com.example.games.dotbox.DotBoxScreen
import com.example.games.game2048.Game2048Screen
import com.example.games.highwayracer.HighwayRacerScreen
import com.example.games.memorymatch.MemoryMatchScreen
import com.example.games.miniludo.MiniLudoScreen
import com.example.games.snake.SnakeScreen
import com.example.games.tictactoe.TicTacToeScreen
import com.example.games.watersort.WaterSortScreen
import com.example.games.wordguess.WordGuessScreen
import com.example.model.GameItem
import com.example.ui.components.GameDetailModal
import com.example.ui.particles.ParticleOverlay
import com.example.ui.particles.ParticleSystem
import com.example.ui.screens.CloudArcadeScreen
import com.example.ui.screens.OfflineVaultScreen
import com.example.ui.screens.WebGameScreen
import com.example.ui.screens.WelcomeScreen
import com.example.network.NetworkChecker
import com.example.ui.screens.games.PongScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class VaultTab(val title: String, val icon: ImageVector) {
  OFFLINE_VAULT("Offline Vault", Icons.Default.SportsEsports),
  CLOUD_ARCADE("Cloud Arcade", Icons.Default.Cloud)
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
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            VaultSurfaceHighlight.copy(alpha = 0.92f),
            VaultSurfaceElevated.copy(alpha = 0.98f)
          )
        )
      )
      .border(1.2.dp, VaultBorderGlow, RoundedCornerShape(24.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    VaultTab.entries.forEach { tab ->
      val isSelected = (currentTab == tab)
      val tintColor = when (tab) {
        VaultTab.OFFLINE_VAULT -> CandyMint
        VaultTab.CLOUD_ARCADE -> CandyLemon
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(if (isSelected) tintColor.copy(alpha = 0.18f) else Color.Transparent)
          .border(
            if (isSelected) 1.dp else 0.dp,
            if (isSelected) tintColor.copy(alpha = 0.6f) else Color.Transparent,
            RoundedCornerShape(16.dp)
          )
          .clickable { onTabSelected(tab) }
          .padding(horizontal = 32.dp, vertical = 8.dp)
          .testTag("nav_tab_${tab.name.lowercase()}")
      ) {
        Icon(
          imageVector = tab.icon,
          contentDescription = tab.title,
          tint = if (isSelected) tintColor else VaultTextMuted,
          modifier = Modifier.size(24.dp)
        )
        Text(
          text = tab.title,
          color = if (isSelected) Color.White else VaultTextMuted,
          fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
          fontSize = 11.sp,
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}

@Composable
fun GameVaultApp(initialIsOnline: Boolean = true, switchToOffline: Boolean = false) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val db = remember { GameVaultDatabase.getDatabase(context) }
  val repository = remember { GameVaultRepository(db.gameDao(), context) }
  val soundEngine = remember { VaultSoundEngine(context) }
  val hapticEngine = remember { VaultHapticEngine(context) }
  val lofiEngine = remember { VaultLofiEngine(context) }
  val particleSystem = remember { ParticleSystem() }

  // Initialize sound / haptic settings from repo
  LaunchedEffect(Unit) {
    val soundEnabled = repository.getSoundEnabled()
    soundEngine.isMuted = !soundEnabled
    lofiEngine.isMuted = !soundEnabled
    hapticEngine.isMuted = !repository.getHapticsEnabled()
    repository.initializeDefaultGamesIfEmpty()
  }

  val games by repository.allGames.collectAsState(initial = emptyList())

  var selectedThemeId by remember { mutableStateOf(repository.getSelectedThemeId()) }
  var showSplash by rememberSaveable { mutableStateOf(true) }
  var currentTab by remember { mutableStateOf(if (switchToOffline || !initialIsOnline) VaultTab.OFFLINE_VAULT else VaultTab.CLOUD_ARCADE) }
  var activeGame by remember { mutableStateOf<GameItem?>(null) }
  var previewGame by remember { mutableStateOf<GameItem?>(null) }

  // Ensure lo-fi stops when leaving composable
  DisposableEffect(Unit) {
    onDispose {
      lofiEngine.stopLofi()
    }
  }

  fun launchGame(game: GameItem) {
    soundEngine.playSnap()
    hapticEngine.vibrateMove()
    scope.launch {
      repository.recordGamePlay(game.id)
    }
    val mood = when (game.id) {
      "highway_racer", "snake", "brick_breaker", "pong" -> VaultLofiEngine.LofiMood.CYBER_SYNTH
      "tictactoe", "2048", "game_2048", "water_sort", "word_guess", "memory_match" -> VaultLofiEngine.LofiMood.CHILL_BEATS
      else -> VaultLofiEngine.LofiMood.RETRO_CHILL
    }
    lofiEngine.startLofi(mood)

    if (game.entryPoint.isNotBlank() && (game.entryPoint.startsWith("file://") || game.entryPoint.startsWith("http"))) {
      GamePlayerActivity.launch(
        context = context,
        gameId = game.id,
        title = game.title,
        url = game.entryPoint,
        isOffline = game.offlineMode
      )
    } else {
      activeGame = game
    }
  }

  fun closeGame() {
    lofiEngine.stopLofi()
    soundEngine.playPop()
    hapticEngine.vibrateTap()
    activeGame = null
  }

  GameVaultTheme(presetId = selectedThemeId) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (showSplash) {
        WelcomeScreen(
          soundEngine = soundEngine,
          hapticEngine = hapticEngine,
          onStartPlaying = {
            soundEngine.playSnap()
            hapticEngine.vibrateSuccess()
            showSplash = false
            val isOnline = NetworkChecker.isOnline(context)
            currentTab = if (isOnline) VaultTab.CLOUD_ARCADE else VaultTab.OFFLINE_VAULT
          }
        )
      } else {
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
              "word_guess" -> {
                WordGuessScreen(
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
              "dot_box" -> {
                DotBoxScreen(
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
              "pong" -> {
                PongScreen(
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
                WebGameScreen(
                  game = game,
                  soundEngine = soundEngine,
                  hapticEngine = hapticEngine,
                  onBack = { closeGame() }
                )
              }
            }
          } else {
            // Vault Main Navigation Content (2 tabs)
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
                  VaultTab.OFFLINE_VAULT -> {
                    OfflineVaultScreen()
                  }
                  VaultTab.CLOUD_ARCADE -> {
                    CloudArcadeScreen()
                  }
                }
              }
            }
          }
        }
      }
    }

    // Game Detail & Preview Modal
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

    // Top-Level Juice / Particle Overlay
    ParticleOverlay(
      particleSystem = particleSystem,
      modifier = Modifier.fillMaxSize()
    )
  }
}
