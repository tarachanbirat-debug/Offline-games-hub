package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GameVaultRepository(
  private val dao: GameVaultDao,
  context: Context
) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("OFFLINE_VAULT_DATA", Context.MODE_PRIVATE)

  val allGames: Flow<List<GameItem>> = dao.getAllGames()

  suspend fun initializeDefaultGamesIfEmpty() = withContext(Dispatchers.IO) {
    val defaultGames = listOf(
      GameItem(
        id = "water_sort",
        title = "Water Sort Color",
        description = "Pour and separate vibrant liquids into clean glass tubes. Test your logic with 4 liquid layers.",
        category = "Puzzle",
        genre = "Liquid Puzzle",
        technology = "Native 2.5D Canvas",
        engine = "WaterEngine",
        version = "2.4.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.watersort",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "TAP TUBE A → TAP TUBE B",
        difficulty = "MEDIUM",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Liquid Dynamics",
        highScore = 0,
        totalPlays = 14,
        stars = 5,
        isFavorite = true,
        lastPlayed = System.currentTimeMillis() - 120000,
        candyColorHex = 0xFFFF2E63 // Coral Pink
      ),
      GameItem(
        id = "game_2048",
        title = "2048 Master",
        description = "Slide and merge glossy numbered tiles across the 4x4 board to reach the golden 2048 crown tile.",
        category = "Puzzle",
        genre = "Number Puzzle",
        technology = "Native Matrix",
        engine = "NumberEngine",
        version = "1.9.2",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.game2048",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "SWIPE OR CHUNKY D-PAD",
        difficulty = "MEDIUM",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "MIT Open Source",
        highScore = 1420,
        totalPlays = 32,
        stars = 5,
        isFavorite = true,
        lastPlayed = System.currentTimeMillis() - 60000,
        candyColorHex = 0xFFFF8000 // Tangerine
      ),
      GameItem(
        id = "mini_ludo",
        title = "Ludo Championship",
        description = "Roll the 3D dice, move your colored tokens, capture opponent tokens, and race to Home!",
        category = "Board",
        genre = "Classic Board",
        technology = "Native 2.5D Board",
        engine = "LudoEngine",
        version = "1.8.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.miniludo",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "TAP DICE TO ROLL & MOVE",
        difficulty = "MEDIUM",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Classic Board Game",
        highScore = 2,
        totalPlays = 19,
        stars = 5,
        isFavorite = true,
        lastPlayed = System.currentTimeMillis() - 1500000,
        candyColorHex = 0xFFA855F7 // Purple
      ),
      GameItem(
        id = "snake",
        title = "Snake Deluxe",
        description = "Classic retro arcade snake with smooth grid movement, juicy apples, and progressive speed.",
        category = "Arcade",
        genre = "Retro Arcade",
        technology = "Native 2D Canvas",
        engine = "SnakeEngine",
        version = "2.1.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.snake",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "SWIPE OR CHUNKY D-PAD",
        difficulty = "EASY",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Arcade Classic",
        highScore = 48,
        totalPlays = 25,
        stars = 5,
        isFavorite = true,
        lastPlayed = System.currentTimeMillis() - 300000,
        candyColorHex = 0xFF00E676 // Mint Green
      ),
      GameItem(
        id = "pong",
        title = "Neon Cyber Pong",
        description = "Responsive touch paddle vs predictive AI with synthetic audio and 60-120 FPS rendering.",
        category = "Arcade",
        genre = "Retro Table Tennis",
        technology = "Native Jetpack Compose Canvas",
        engine = "PongEngine",
        version = "2.0.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.pong",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "DRAG PADDLE LEFT / RIGHT",
        difficulty = "MEDIUM",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Atari Classic",
        highScore = 15,
        totalPlays = 34,
        stars = 5,
        isFavorite = true,
        lastPlayed = System.currentTimeMillis() - 200000,
        candyColorHex = 0xFF00D2FF // Cyber Cyan
      ),
      GameItem(
        id = "word_guess",
        title = "Word Guess",
        description = "Guess the hidden 5-letter word in 6 attempts! Color-coded green and yellow feedback tiles.",
        category = "Puzzle",
        genre = "Word Puzzle",
        technology = "Native Word Engine",
        engine = "WordEngine",
        version = "1.0.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.wordguess",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "TOUCH KEYBOARD",
        difficulty = "MEDIUM",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Wordle Concept",
        highScore = 450,
        totalPlays = 22,
        stars = 5,
        isFavorite = true,
        lastPlayed = System.currentTimeMillis() - 450000,
        candyColorHex = 0xFFFFD000 // Gold / Yellow
      ),
      GameItem(
        id = "dot_box",
        title = "Dot & Box",
        description = "Connect dots with lines to close squares and claim territory against the smart AI!",
        category = "Board",
        genre = "Strategy Board",
        technology = "Native Vector Grid",
        engine = "DotBoxEngine",
        version = "1.0.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.dotbox",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "TAP BETWEEN DOTS",
        difficulty = "MEDIUM",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Classic Pen & Paper",
        highScore = 300,
        totalPlays = 16,
        stars = 5,
        isFavorite = false,
        lastPlayed = System.currentTimeMillis() - 600000,
        candyColorHex = 0xFF00E5FF // Cyan
      ),
      GameItem(
        id = "highway_racer",
        title = "Highway Racer 3D",
        description = "High-speed 3-lane road racing with oncoming traffic avoidance, coin rushes, and screen-shake.",
        category = "Action",
        genre = "Endless Racer",
        technology = "Native 2.5D Canvas",
        engine = "VelocityEngine",
        version = "3.0.1",
        license = "Apache-2.0",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.highwayracer",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "TAP LANES OR DRAG CAR",
        difficulty = "HARD",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Speed Arcade",
        highScore = 650,
        totalPlays = 19,
        stars = 5,
        isFavorite = false,
        lastPlayed = System.currentTimeMillis() - 800000,
        candyColorHex = 0xFF29B6F6 // Sky Blue
      ),
      GameItem(
        id = "tic_tac_toe",
        title = "Tic-Tac-Toe AI",
        description = "Chunky 3D board with animated glowing X/O tokens and unbeatable Minimax AI!",
        category = "Board",
        genre = "Classic Strategy",
        technology = "Native 2D/3D Board",
        engine = "Minimax AI",
        version = "2.0.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.tictactoe",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "TAP GRID CELLS",
        difficulty = "EXPERT",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Minimax AI Engine",
        highScore = 6,
        totalPlays = 28,
        stars = 5,
        isFavorite = false,
        lastPlayed = System.currentTimeMillis() - 2500000,
        candyColorHex = 0xFFFFD000 // Lemon Gold
      ),
      GameItem(
        id = "brick_breaker",
        title = "Neon Brick Breaker",
        description = "Breakout arcade action with responsive paddle deflection, glowing bricks, and particle bursts.",
        category = "Arcade",
        genre = "Arcade Breakout",
        technology = "Native Canvas Physics",
        engine = "BreakoutEngine",
        version = "1.5.0",
        license = "MIT",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.brickbreaker",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "DRAG PADDLE LEFT / RIGHT",
        difficulty = "MEDIUM",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Classic Breakout",
        highScore = 320,
        totalPlays = 12,
        stars = 5,
        isFavorite = false,
        lastPlayed = System.currentTimeMillis() - 3600000,
        candyColorHex = 0xFFFF5252 // Coral Red
      ),
      GameItem(
        id = "memory_match",
        title = "Candy Memory Match",
        description = "Flip 3D arcade cards, match candy pairs, and build up your memory combo streak!",
        category = "Reflex",
        genre = "Memory Match",
        technology = "Native Animated Grid",
        engine = "MemoryEngine",
        version = "1.2.0",
        license = "CC0",
        licenseStatus = "OFFLINE READY",
        source = "Native Built-in",
        entryPoint = "com.example.game.memorymatch",
        offlineMode = true,
        onlineMode = false,
        dependencies = "Zero External CDN",
        controls = "TAP CARDS TO FLIP",
        difficulty = "EASY",
        orientation = "PORTRAIT",
        author = "Game Station",
        credits = "Match Puzzle",
        highScore = 240,
        totalPlays = 15,
        stars = 5,
        isFavorite = false,
        lastPlayed = System.currentTimeMillis() - 4000000,
        candyColorHex = 0xFF00BFA5 // Emerald
      )
    )
    dao.insertGames(defaultGames)
  }

  suspend fun recordGamePlay(gameId: String, score: Int = 0) = withContext(Dispatchers.IO) {
    val now = System.currentTimeMillis()
    dao.recordPlay(gameId, now)
    if (score > 0) {
      dao.updateHighScore(gameId, score, now)
    }
  }

  suspend fun toggleFavorite(gameId: String, current: Boolean) = withContext(Dispatchers.IO) {
    dao.setFavorite(gameId, !current)
  }

  suspend fun updateDifficulty(gameId: String, newDiff: String) = withContext(Dispatchers.IO) {
    val game = dao.getGameById(gameId) ?: return@withContext
    dao.updateGame(game.copy(difficulty = newDiff))
  }

  suspend fun addNewGame(game: GameItem) = withContext(Dispatchers.IO) {
    dao.insertGame(game)
  }

  fun getSoundEnabled(): Boolean = prefs.getBoolean("SOUND_ENABLED", true)
  fun setSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean("SOUND_ENABLED", enabled).apply()

  fun getHapticsEnabled(): Boolean = prefs.getBoolean("HAPTICS_ENABLED", true)
  fun setHapticsEnabled(enabled: Boolean) = prefs.edit().putBoolean("HAPTICS_ENABLED", enabled).apply()

  fun getSelectedThemeId(): String = prefs.getString("SELECTED_THEME_ID", "pop_2d") ?: "pop_2d"
  fun setSelectedThemeId(themeId: String) = prefs.edit().putString("SELECTED_THEME_ID", themeId).apply()
}
