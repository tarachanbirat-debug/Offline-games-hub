package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class OfflineGame(
  val id: String,
  val title: String,
  val description: String,
  val category: String,
  val genre: String,
  val assetFolder: String,
  val emoji: String,
  val accentColor: Color,
  val badgeText: String = "Offline Ready • No Internet",
  val orientation: String = "PORTRAIT",
  val rating: Float = 4.9f
) {
  val assetUrl: String
    get() = "file:///android_asset/offline_games/$assetFolder/index.html"
}

object OfflineGamesCatalog {
  val games = listOf(
    OfflineGame(
      id = "2048",
      title = "2048 Classic",
      description = "Slide and merge numbered tiles to reach the legendary 2048 crown tile. Zero lag, silky smooth touch swipes.",
      category = "PUZZLE",
      genre = "Number Logic",
      assetFolder = "2048",
      emoji = "🔢",
      accentColor = CandyLemon,
      rating = 4.9f
    ),
    OfflineGame(
      id = "snake",
      title = "Snake Retro",
      description = "Classic retro arcade snake with neon fruit, responsive on-screen D-pad and swipe mechanics.",
      category = "ARCADE",
      genre = "Retro Arcade",
      assetFolder = "snake",
      emoji = "🐍",
      accentColor = CandyMint,
      rating = 4.8f
    ),
    OfflineGame(
      id = "tictactoe",
      title = "Tic-Tac-Toe AI",
      description = "Neon 3D grid with animated glowing X/O tokens and unbeatable local Minimax AI or 2-player pass-and-play.",
      category = "STRATEGY",
      genre = "Classic Strategy",
      assetFolder = "tictactoe",
      emoji = "❌⭕",
      accentColor = CandyCyan,
      rating = 4.7f
    ),
    OfflineGame(
      id = "pacman",
      title = "Pac-Man Canvas",
      description = "Authentic retro maze runner with dots, energizers, ghosts, and chiptune sound synthesis.",
      category = "ARCADE",
      genre = "Maze Action",
      assetFolder = "pacman",
      emoji = "👾",
      accentColor = CandyLemon,
      rating = 5.0f
    ),
    OfflineGame(
      id = "brick_breaker",
      title = "Brick Breaker / Pong",
      description = "Breakout neon arcade with responsive paddle deflection, glowing destructible bricks, and physics bounces.",
      category = "ARCADE",
      genre = "Breakout Arcade",
      assetFolder = "brick_breaker",
      emoji = "🧱",
      accentColor = CandySkyBlue,
      rating = 4.9f
    ),
    OfflineGame(
      id = "memory_matrix",
      title = "Memory Matrix",
      description = "Cognitive pattern sequence trainer. Memorize the glowing matrix tiles and repeat the flashing pattern.",
      category = "PUZZLE",
      genre = "Brain Trainer",
      assetFolder = "memory_matrix",
      emoji = "🧠",
      accentColor = CandyGrape,
      rating = 4.8f
    ),
    OfflineGame(
      id = "tetris",
      title = "Hextris / Tetris Clone",
      description = "Neon falling block puzzle with 7 tetrominoes, rotation, line-clear sparkles, and touch D-pad.",
      category = "PUZZLE",
      genre = "Block Puzzle",
      assetFolder = "tetris",
      emoji = "🟦",
      accentColor = CandyWatermelon,
      rating = 4.9f
    ),
    OfflineGame(
      id = "pong",
      title = "Cyber Pong 2D",
      description = "High-velocity table tennis with paddle spin physics, sound effects, and smart computer opponent.",
      category = "ARCADE",
      genre = "Table Tennis",
      assetFolder = "pong",
      emoji = "🏓",
      accentColor = CandyTangerine,
      rating = 4.8f
    )
  )
}
