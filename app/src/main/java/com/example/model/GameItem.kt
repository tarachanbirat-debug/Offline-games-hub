package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "games")
data class GameItem(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val category: String, // "Puzzle", "Board", "Arcade", "Racing", "Reflex", "Strategy", "Casual"
  val genre: String,
  val technology: String, // "Canvas / 2D", "WebGL", "Native Engine", "Phaser 3", "Godot"
  val engine: String,
  val version: String,
  val license: String, // "MIT", "Apache-2.0", "BSD-3-Clause", "CC0"
  val licenseStatus: String, // "LICENSE VERIFIED", "LICENSE REVIEW REQUIRED", "LICENSE NOT SUITABLE"
  val source: String,
  val entryPoint: String,
  val offlineMode: Boolean,
  val onlineMode: Boolean,
  val dependencies: String,
  val controls: String, // "TAP", "SWIPE", "DRAG", "BUTTONS"
  val difficulty: String, // "EASY", "MEDIUM", "HARD", "EXPERT"
  val orientation: String, // "PORTRAIT", "LANDSCAPE", "ANY"
  val author: String,
  val credits: String,
  val highScore: Int = 0,
  val totalPlays: Int = 0,
  val stars: Int = 5,
  val isFavorite: Boolean = false,
  val lastPlayed: Long = 0L,
  val candyColorHex: Long = 0xFFFF3366 // Vibrant card candy tint
) : Serializable

enum class GameDifficulty(val label: String) {
  EASY("EASY"),
  MEDIUM("MEDIUM"),
  HARD("HARD"),
  EXPERT("EXPERT")
}
