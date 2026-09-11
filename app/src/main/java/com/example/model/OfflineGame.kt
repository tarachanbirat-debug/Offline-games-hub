package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class OfflineGame(
  val id: String,
  val title: String,
  val description: String,
  val category: String,
  val genre: String,
  val url: String,
  val emoji: String,
  val accentColor: Color,
  val badgeText: String = "Offline Ready • No Internet",
  val orientation: String = "PORTRAIT",
  val rating: Float = 4.9f
) {
  val assetUrl: String
    get() = url
  val isLandscape: Boolean
    get() = orientation.equals("LANDSCAPE", ignoreCase = true)
}

object OfflineGamesCatalog {
  val games = listOf(
    OfflineGame(
      id = "fruit_ninja",
      title = "Fruit Ninja",
      description = "Real blade trail, flying fruits & splash sounds with juicy arcade action.",
      category = "ACTION",
      genre = "Blade Action",
      url = "https://verma-anushka.github.io/Fruit-Ninja/",
      emoji = "🍉",
      accentColor = CandyWatermelon,
      orientation = "LANDSCAPE",
      rating = 4.9f
    ),
    OfflineGame(
      id = "clumsy_bird",
      title = "Clumsy Bird",
      description = "Official melonJS Flappy Bird with full sprites and smooth touch physics.",
      category = "TAP",
      genre = "Action Flier",
      url = "https://ellisonleao.github.io/clumsy-bird/",
      emoji = "🐤",
      accentColor = CandyMint,
      orientation = "PORTRAIT",
      rating = 4.8f
    ),
    OfflineGame(
      id = "2048",
      title = "2048 Deluxe",
      description = "Gabriele Cirulli's original animated engine with smooth tile merging.",
      category = "PUZZLE",
      genre = "Number Logic",
      url = "https://gabrielecirulli.github.io/2048/",
      emoji = "2048",
      accentColor = CandyCyan,
      orientation = "PORTRAIT",
      rating = 4.9f
    ),
    OfflineGame(
      id = "hextris",
      title = "Neon Hextris",
      description = "Neon hexagon puzzle with synth audio and fast-paced rotation.",
      category = "PUZZLE",
      genre = "Hex Arcade",
      url = "https://hextris.github.io/hextris/",
      emoji = "🔷",
      accentColor = CandyLemon,
      orientation = "PORTRAIT",
      rating = 4.9f
    ),
    OfflineGame(
      id = "pacman",
      title = "Pac-Man Classic",
      description = "Canvas maze with retro ghosts, dots, energizers, and audio.",
      category = "RETRO",
      genre = "Maze Arcade",
      url = "https://platzh1rsch.github.io/pacman-canvas/",
      emoji = "🟡",
      accentColor = CandyGrape,
      orientation = "LANDSCAPE",
      rating = 4.9f
    ),
    OfflineGame(
      id = "tower_game",
      title = "Tower Master",
      description = "3D isometric block stacking game with precision physics.",
      category = "SKILL",
      genre = "Stacking",
      url = "https://iamkun.github.io/tower_game/",
      emoji = "🗼",
      accentColor = CandySkyBlue,
      orientation = "PORTRAIT",
      rating = 4.8f
    ),
    OfflineGame(
      id = "snake_game",
      title = "Snake Game Retro",
      description = "Classic retro snake game with crisp controls and score tracking.",
      category = "RETRO",
      genre = "Retro Arcade",
      url = "https://eperezcosano.github.io/snake-game/",
      emoji = "🐍",
      accentColor = CandyLemon,
      orientation = "PORTRAIT",
      rating = 4.9f
    ),
    OfflineGame(
      id = "breakout",
      title = "Breakout DX-Ball",
      description = "Canvas brick breaker with paddle physics and power-ups.",
      category = "ARCADE",
      genre = "Brick Breaker",
      url = "https://bmorelli25.github.io/Breakout-Game-JavaScript/",
      emoji = "🧱",
      accentColor = CandyWatermelon,
      orientation = "LANDSCAPE",
      rating = 4.9f
    ),
    OfflineGame(
      id = "webgl_maze",
      title = "WebGL Maze 3D",
      description = "Full 3D first-person labyrinth rendered with WebGL.",
      category = "ACTION",
      genre = "3D Maze",
      url = "https://mrdoob.github.io/three.js/examples/webgl_geometry_cube.html",
      emoji = "🧊",
      accentColor = CandyMint,
      orientation = "LANDSCAPE",
      rating = 4.9f
    ),
    OfflineGame(
      id = "connect_four",
      title = "Connect 4 AI",
      description = "Full board strategy game with a smart bot opponent.",
      category = "BOARD",
      genre = "Board Strategy",
      url = "https://kenrick95.github.io/c4/",
      emoji = "🔴",
      accentColor = CandyCyan,
      orientation = "PORTRAIT",
      rating = 4.8f
    )
  )
}
