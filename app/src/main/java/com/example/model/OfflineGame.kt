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
      id = "hextris",
      title = "Hextris Neon",
      description = "Fast-paced hexagon puzzle inspired by Tetris with rotating neon blocks and zero lag.",
      category = "PUZZLE",
      genre = "Hex Arcade",
      assetFolder = "hextris",
      emoji = "🔷",
      accentColor = CandyLemon,
      rating = 4.9f
    ),
    OfflineGame(
      id = "clumsy_bird",
      title = "Clumsy Bird",
      description = "Flap your wings through obstacle pipes with smooth parallax background and touch physics.",
      category = "ARCADE",
      genre = "Action Flier",
      assetFolder = "clumsy_bird",
      emoji = "🐦",
      accentColor = CandyMint,
      rating = 4.8f
    ),
    OfflineGame(
      id = "pacman",
      title = "Retro Pac-Man",
      description = "Classic arcade maze runner with dots, energizers, ghosts, and chiptune sound synthesis.",
      category = "ARCADE",
      genre = "Maze Arcade",
      assetFolder = "pacman",
      emoji = "👾",
      accentColor = CandyWatermelon,
      rating = 5.0f
    ),
    OfflineGame(
      id = "2048",
      title = "2048 Deluxe",
      description = "Juicy animated tile merging puzzle with high score save and silky smooth touch swipes.",
      category = "PUZZLE",
      genre = "Number Logic",
      assetFolder = "2048",
      emoji = "🔢",
      accentColor = CandyCyan,
      rating = 4.9f
    ),
    OfflineGame(
      id = "space_shooter",
      title = "Galaxy Shooter",
      description = "Vibrant 2D space fighter with particle bursts, laser shooting, and boss waves.",
      category = "ARCADE",
      genre = "Space Combat",
      assetFolder = "space_shooter",
      emoji = "🚀",
      accentColor = CandySkyBlue,
      rating = 4.9f
    ),
    OfflineGame(
      id = "block_puzzle",
      title = "Block Puzzle Blast",
      description = "Color block drop mechanics with satisfying line explosions and high score tracker.",
      category = "PUZZLE",
      genre = "Block Drop",
      assetFolder = "block_puzzle",
      emoji = "🟦",
      accentColor = CandyGrape,
      rating = 4.8f
    )
  )
}
