package com.example.games.wordguess

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.components.UniversalGameHeader
import com.example.ui.particles.ParticleSystem
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun WordGuessScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  highScore: Int,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  val wordList = remember {
    listOf(
      "APPLE", "TIGER", "PIZZA", "SMILE", "WATER", "CANDY", "SHINE", "BRAIN",
      "LUCKY", "SMART", "SUPER", "PLANT", "CLOUD", "DANCE", "TRAIN", "FLAME",
      "MUSIC", "QUEST", "ROBOT", "OCEAN", "SPACE", "MAGIC", "POWER", "BEACH"
    )
  }

  var targetWord by remember { mutableStateOf(wordList.random()) }
  val guesses = remember { mutableStateListOf<String>() }
  var currentInput by remember { mutableStateOf("") }
  var isGameOver by remember { mutableStateOf(false) }
  var isWon by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }
  var bestScore by remember { mutableIntStateOf(highScore) }

  fun restartGame() {
    targetWord = wordList.random()
    guesses.clear()
    currentInput = ""
    isGameOver = false
    isWon = false
    soundEngine.playPop()
  }

  fun submitGuess() {
    if (currentInput.length != 5 || isGameOver) return

    val guess = currentInput.uppercase()
    guesses.add(guess)
    currentInput = ""

    if (guess == targetWord) {
      isWon = true
      isGameOver = true
      val points = (7 - guesses.size) * 100
      score += points
      if (score > bestScore) {
        bestScore = score
        onScoreUpdated(bestScore)
      }
      soundEngine.playVictory()
      hapticEngine.vibrateSuccess()
      particleSystem.spawnVictoryConfetti(500f, 400f, 100)
    } else if (guesses.size >= 6) {
      isGameOver = true
      soundEngine.playGameOver()
      hapticEngine.vibrateCrash()
    } else {
      soundEngine.playSnap()
      hapticEngine.vibrateMove()
    }
  }

  fun onKeyPress(key: String) {
    if (isGameOver) return
    if (key == "ENTER") {
      submitGuess()
    } else if (key == "DEL") {
      if (currentInput.isNotEmpty()) {
        currentInput = currentInput.dropLast(1)
        soundEngine.playTap()
        hapticEngine.vibrateTap()
      }
    } else if (currentInput.length < 5) {
      currentInput += key
      soundEngine.playTap()
      hapticEngine.vibrateTap()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "Word Guess",
      score = score,
      bestScore = bestScore,
      difficulty = difficulty,
      soundEnabled = !soundEngine.isMuted,
      hapticsEnabled = !hapticEngine.isMuted,
      onBack = onBack,
      onReset = { restartGame() },
      onToggleSound = { soundEngine.isMuted = !soundEngine.isMuted },
      onToggleHaptics = { hapticEngine.isMuted = !hapticEngine.isMuted }
    )

    // Status Banner
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isWon) {
        Text("🎉 BRILLIANT! YOU GUESSED IT: $targetWord", color = CandyMint, fontWeight = FontWeight.Black, fontSize = 13.sp)
      } else if (isGameOver) {
        Text("WORD WAS: $targetWord • TAP RESET", color = CandyWatermelon, fontWeight = FontWeight.Black, fontSize = 13.sp)
      } else {
        Text("GUESS THE 5-LETTER WORD (TRY ${guesses.size + 1}/6)", color = VaultTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }

    // 6 Rows of 5 Letter Tiles
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 28.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      for (row in 0 until 6) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          for (col in 0 until 5) {
            val letter: Char? = when {
              row < guesses.size -> guesses[row].getOrNull(col)
              row == guesses.size -> currentInput.getOrNull(col)
              else -> null
            }

            val tileColor = when {
              row < guesses.size -> {
                val guessedChar = guesses[row][col]
                if (guessedChar == targetWord[col]) {
                  CandyMint // Correct spot
                } else if (targetWord.contains(guessedChar)) {
                  CandyLemon // Wrong spot
                } else {
                  VaultSurfaceHighlight // Not in word
                }
              }
              letter != null -> VaultSurfaceElevated
              else -> VaultCardDark
            }

            val animColor by animateColorAsState(targetValue = tileColor, animationSpec = tween(200), label = "tile_bg")

            Box(
              modifier = Modifier
                .size(48.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(animColor)
                .border(
                  1.5.dp,
                  if (letter != null && row == guesses.size) CandyCyan else VaultBorder,
                  RoundedCornerShape(10.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = letter?.toString() ?: "",
                color = if (tileColor == CandyLemon) Color.Black else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
              )
            }
          }
        }
      }
    }

    // Virtual Keyboard
    val keyboardRows = listOf(
      listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
      listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
      listOf("ENTER", "Z", "X", "C", "V", "B", "N", "M", "DEL")
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 6.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      keyboardRows.forEach { rowKeys ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          rowKeys.forEach { key ->
            val isAction = key == "ENTER" || key == "DEL"
            val keyWidth = if (isAction) 52.dp else 32.dp

            Box(
              modifier = Modifier
                .height(44.dp)
                .width(keyWidth)
                .padding(2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAction) CandyCyan.copy(alpha = 0.25f) else VaultSurfaceElevated)
                .border(1.dp, VaultBorder, RoundedCornerShape(8.dp))
                .clickable { onKeyPress(key) }
                .testTag("key_$key"),
              contentAlignment = Alignment.Center
            ) {
              if (key == "DEL") {
                Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(16.dp))
              } else {
                Text(
                  text = key,
                  color = if (isAction) CandyCyan else Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = if (isAction) 10.sp else 13.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
