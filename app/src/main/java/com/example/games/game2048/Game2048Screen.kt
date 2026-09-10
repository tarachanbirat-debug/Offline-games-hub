package com.example.games.game2048

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.components.UniversalGameHeader
import com.example.ui.particles.ParticleSystem
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun Game2048Screen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  highScore: Int,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  var grid by remember { mutableStateOf(Array(4) { IntArray(4) { 0 } }) }
  var score by remember { mutableIntStateOf(0) }
  var bestScore by remember { mutableIntStateOf(highScore) }
  var isGameOver by remember { mutableStateOf(false) }
  var hasWon2048 by remember { mutableStateOf(false) }

  fun spawnTile(g: Array<IntArray>, diff: String) {
    val empty = mutableListOf<Pair<Int, Int>>()
    for (r in 0 until 4) {
      for (c in 0 until 4) {
        if (g[r][c] == 0) empty.add(Pair(r, c))
      }
    }
    if (empty.isNotEmpty()) {
      val (r, c) = empty.random()
      val chanceOf4 = when (diff) {
        "EASY" -> 0.05f
        "MEDIUM" -> 0.15f
        "HARD" -> 0.35f
        else -> 0.55f // EXPERT spawns more 4s
      }
      g[r][c] = if (Random.nextFloat() < chanceOf4) 4 else 2
    }
  }

  fun checkGameOver(g: Array<IntArray>): Boolean {
    for (r in 0 until 4) {
      for (c in 0 until 4) {
        if (g[r][c] == 0) return false
        if (c < 3 && g[r][c] == g[r][c + 1]) return false
        if (r < 3 && g[r][c] == g[r + 1][c]) return false
      }
    }
    return true
  }

  fun restartGame() {
    val newGrid = Array(4) { IntArray(4) { 0 } }
    spawnTile(newGrid, difficulty)
    spawnTile(newGrid, difficulty)
    grid = newGrid
    score = 0
    isGameOver = false
    hasWon2048 = false
    soundEngine.playPop()
  }

  LaunchedEffect(Unit) {
    restartGame()
  }

  fun move(dir: Direction) {
    if (isGameOver) return

    var moved = false
    var pointsEarned = 0
    val newGrid = Array(4) { r -> grid[r].clone() }

    fun slideAndMerge(line: IntArray): Pair<IntArray, Int> {
      val filtered = line.filter { it != 0 }.toIntArray()
      val result = IntArray(4) { 0 }
      var targetIdx = 0
      var i = 0
      var linePoints = 0

      while (i < filtered.size) {
        if (i + 1 < filtered.size && filtered[i] == filtered[i + 1]) {
          val mergedVal = filtered[i] * 2
          result[targetIdx++] = mergedVal
          linePoints += mergedVal
          i += 2
        } else {
          result[targetIdx++] = filtered[i]
          i++
        }
      }
      return Pair(result, linePoints)
    }

    when (dir) {
      Direction.LEFT -> {
        for (r in 0 until 4) {
          val (newLine, p) = slideAndMerge(newGrid[r])
          pointsEarned += p
          if (!newGrid[r].contentEquals(newLine)) {
            newGrid[r] = newLine
            moved = true
          }
        }
      }
      Direction.RIGHT -> {
        for (r in 0 until 4) {
          val reversed = newGrid[r].reversedArray()
          val (newLine, p) = slideAndMerge(reversed)
          pointsEarned += p
          val normal = newLine.reversedArray()
          if (!newGrid[r].contentEquals(normal)) {
            newGrid[r] = normal
            moved = true
          }
        }
      }
      Direction.UP -> {
        for (c in 0 until 4) {
          val colArray = IntArray(4) { r -> newGrid[r][c] }
          val (newLine, p) = slideAndMerge(colArray)
          pointsEarned += p
          for (r in 0 until 4) {
            if (newGrid[r][c] != newLine[r]) {
              newGrid[r][c] = newLine[r]
              moved = true
            }
          }
        }
      }
      Direction.DOWN -> {
        for (c in 0 until 4) {
          val colArray = IntArray(4) { r -> newGrid[3 - r][c] }
          val (newLine, p) = slideAndMerge(colArray)
          pointsEarned += p
          for (r in 0 until 4) {
            if (newGrid[3 - r][c] != newLine[r]) {
              newGrid[3 - r][c] = newLine[r]
              moved = true
            }
          }
        }
      }
    }

    if (moved) {
      spawnTile(newGrid, difficulty)
      grid = newGrid
      score += pointsEarned
      if (score > bestScore) {
        bestScore = score
        onScoreUpdated(score)
      }

      if (pointsEarned > 0) {
        soundEngine.playScore()
        hapticEngine.vibrateTap()
        particleSystem.spawnScoreSparkles(500f, 400f, 15)
      } else {
        soundEngine.playSnap()
        hapticEngine.vibrateMove()
      }

      // Check for 2048 tile win
      if (!hasWon2048 && newGrid.any { row -> row.any { it >= 2048 } }) {
        hasWon2048 = true
        soundEngine.playVictory()
        hapticEngine.vibrateSuccess()
        particleSystem.spawnVictoryConfetti(500f, 400f, 100)
      }

      if (checkGameOver(newGrid)) {
        isGameOver = true
        soundEngine.playGameOver()
        hapticEngine.vibrateCrash()
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "2048 Master Edition",
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

    // Game Status or 2048 celebration
    if (hasWon2048) {
      Text(
        text = "👑 2048 REACHED! KEEP GOING!",
        color = CandyLemon,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(vertical = 4.dp)
      )
    } else if (isGameOver) {
      Text(
        text = "💀 GAME OVER - NO MOVES LEFT",
        color = CandyWatermelon,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(vertical = 4.dp)
      )
    }

    // 4x4 Board with Swipe Gesture Support
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      var dragTotalX by remember { mutableFloatStateOf(0f) }
      var dragTotalY by remember { mutableFloatStateOf(0f) }

      Box(
        modifier = Modifier
          .size(320.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(VaultSurfaceElevated)
          .border(2.dp, VaultBorderGlow, RoundedCornerShape(26.dp))
          .padding(10.dp)
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = {
                dragTotalX = 0f
                dragTotalY = 0f
              },
              onDrag = { change, dragAmount ->
                change.consume()
                dragTotalX += dragAmount.x
                dragTotalY += dragAmount.y
              },
              onDragEnd = {
                val threshold = 40f
                if (abs(dragTotalX) > abs(dragTotalY)) {
                  if (dragTotalX > threshold) move(Direction.RIGHT)
                  else if (dragTotalX < -threshold) move(Direction.LEFT)
                } else {
                  if (dragTotalY > threshold) move(Direction.DOWN)
                  else if (dragTotalY < -threshold) move(Direction.UP)
                }
              }
            )
          }
      ) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (r in 0 until 4) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              for (c in 0 until 4) {
                val value = grid[r][c]
                val tileColor = get2048TileColor(value)

                val tileScale by animateFloatAsState(
                  targetValue = if (value != 0) 1f else 0.95f,
                  animationSpec = tween(120),
                  label = "tile_scale"
                )

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .scale(tileScale)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tileColor)
                    .border(
                      1.dp,
                      if (value >= 128) CandyLemon else Color(0x22FFFFFF),
                      RoundedCornerShape(14.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (value > 0) {
                    Text(
                      text = "$value",
                      color = if (value in 2..4) Color(0xFF1E1638) else Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = if (value >= 1024) 17.sp else if (value >= 128) 20.sp else 23.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Chunky Arcade D-Pad for Touch Accessibility (Part 32)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DPadButton(icon = Icons.Default.KeyboardArrowUp, onClick = { move(Direction.UP) })
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
          DPadButton(icon = Icons.Default.KeyboardArrowLeft, onClick = { move(Direction.LEFT) })
          DPadButton(icon = Icons.Default.KeyboardArrowDown, onClick = { move(Direction.DOWN) })
          DPadButton(icon = Icons.Default.KeyboardArrowRight, onClick = { move(Direction.RIGHT) })
        }
      }
    }
  }
}

private enum class Direction { UP, DOWN, LEFT, RIGHT }

@Composable
private fun DPadButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit
) {
  IconButton(
    onClick = onClick,
    modifier = Modifier
      .size(46.dp)
      .clip(CircleShape)
      .background(VaultSurfaceHighlight)
      .border(1.dp, VaultBorderGlow, CircleShape)
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = Color.White)
  }
}

private fun get2048TileColor(value: Int): Color {
  return when (value) {
    0 -> Color(0xFF281E48)
    2 -> Color(0xFFEEE4DA)
    4 -> Color(0xFFEDE0C8)
    8 -> CandyTangerine
    16 -> Color(0xFFFF5722)
    32 -> CandyWatermelon
    64 -> Color(0xFFE91E63)
    128 -> CandyLemon
    256 -> CandyMint
    512 -> CandyCyan
    1024 -> CandyGrape
    2048 -> CandyGold
    else -> Color(0xFFFF007F)
  }
}

private val CandyGold = Color(0xFFFFD700)
