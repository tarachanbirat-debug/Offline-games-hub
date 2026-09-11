package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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

private fun Context.findActivity(): Activity? {
  var current = this
  while (current is ContextWrapper) {
    if (current is Activity) return current
    current = current.baseContext
  }
  return null
}

// SECTION 1.2: Compact BottomNavigation bar (height fixed at 52dp, icon size 18dp, label size 10sp)
@Composable
private fun VaultBottomNavigation(
  currentTab: VaultTab,
  onTabSelected: (VaultTab) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .height(52.dp)
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
      .padding(horizontal = 8.dp, vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    VaultTab.entries.forEach { tab ->
      val isSelected = (currentTab == tab)
      val tintColor = when (tab) {
        VaultTab.OFFLINE_VAULT -> CandyMint
        VaultTab.CLOUD_ARCADE -> CandyLemon
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(if (isSelected) tintColor.copy(alpha = 0.18f) else Color.Transparent)
          .border(
            if (isSelected) 1.dp else 0.dp,
            if (isSelected) tintColor.copy(alpha = 0.6f) else Color.Transparent,
            RoundedCornerShape(16.dp)
          )
          .clickable { onTabSelected(tab) }
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .testTag("nav_tab_${tab.name.lowercase()}")
      ) {
        Icon(
          imageVector = tab.icon,
          contentDescription = tab.title,
          tint = if (isSelected) tintColor else VaultTextMuted,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = tab.title,
          color = if (isSelected) Color.White else VaultTextMuted,
          fontWeight = FontWeight.Medium,
          fontSize = 10.sp
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameVaultApp(initialIsOnline: Boolean = true, switchToOffline: Boolean = false) {
  val context = LocalContext.current
  val activity = remember(context) { context.findActivity() }
  val scope = rememberCoroutineScope()
  val configuration = LocalConfiguration.current
  val orientation = configuration.orientation

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

  // SECTION 1.1: Detect orientation and active game state
  var isGameActive by remember { mutableStateOf(false) }
  val hideChrome = (orientation == Configuration.ORIENTATION_LANDSCAPE) || isGameActive || (activeGame != null)

  // SECTION 1.3: Window Insets & Immersive Mode
  DisposableEffect(hideChrome) {
    val window = activity?.window
    if (window != null) {
      val insetsController = WindowInsetsControllerCompat(window, window.decorView)
      if (hideChrome) {
        insetsController.systemBarsBehavior =
          WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
      } else {
        insetsController.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
      }
    }
    onDispose {
      val window = activity?.window
      if (window != null) {
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
      }
    }
  }

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
    isGameActive = false
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
            // SECTION 1.2: Scaffold Configuration (zero padding when hideChrome == true)
            Scaffold(
              containerColor = MaterialTheme.colorScheme.background,
              contentWindowInsets = if (hideChrome) WindowInsets(0, 0, 0, 0) else WindowInsets.statusBars,
              topBar = {
                if (!hideChrome) {
                  TopAppBar(
                    title = {
                      Text(
                        text = "GAME VAULT",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground
                      )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                      containerColor = MaterialTheme.colorScheme.background
                    ),
                    actions = {
                      IconButton(onClick = {
                        val nextSound = !soundEngine.isMuted
                        soundEngine.isMuted = nextSound
                        lofiEngine.isMuted = nextSound
                        scope.launch { repository.setSoundEnabled(!nextSound) }
                      }) {
                        Icon(
                          imageVector = if (soundEngine.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                          contentDescription = "Toggle Audio",
                          tint = MaterialTheme.colorScheme.primary,
                          modifier = Modifier.size(20.dp)
                        )
                      }
                    }
                  )
                }
              },
              bottomBar = {
                if (!hideChrome) {
                  VaultBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                      currentTab = tab
                      soundEngine.playTap()
                      hapticEngine.vibrateTap()
                    }
                  )
                }
              }
            ) { innerPadding ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(if (hideChrome) PaddingValues(0.dp) else innerPadding)
              ) {
                when (currentTab) {
                  VaultTab.OFFLINE_VAULT -> {
                    OfflineVaultScreen(
                      currentThemeId = selectedThemeId,
                      onThemeSelected = { newThemeId ->
                        selectedThemeId = newThemeId
                        repository.setSelectedThemeId(newThemeId)
                        soundEngine.playSnap()
                        hapticEngine.vibrateTap()
                      },
                      onGamePlayingStateChanged = { isPlaying ->
                        isGameActive = isPlaying
                      }
                    )
                  }
                  VaultTab.CLOUD_ARCADE -> {
                    CloudArcadeScreen(
                      onGameActiveChanged = { isPlaying ->
                        isGameActive = isPlaying
                      }
                    )
                  }
                }
              }
            }
          }
        }
      }

      // SECTION 1.4: Unobtrusive Floating Control when hideChrome == true
      if (hideChrome) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .zIndex(999f),
          contentAlignment = Alignment.TopEnd
        ) {
          IconButton(
            onClick = {
              isGameActive = false
              activeGame = null
              activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            },
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.65f))
              .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Game HUD",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
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

    // Global Floating Audio Mood Controller
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 72.dp, end = 16.dp),
      contentAlignment = Alignment.BottomEnd
    ) {
      // Audio engine status
    }

    // Interactive Particle Physics Engine Overlay
    ParticleOverlay(
      particleSystem = particleSystem,
      modifier = Modifier.fillMaxSize()
    )
  }
}
