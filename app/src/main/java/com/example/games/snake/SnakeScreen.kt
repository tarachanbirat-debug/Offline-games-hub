package com.example.games.snake

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.components.UniversalGameHeader
import com.example.ui.particles.ParticleSystem
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.random.Random

data class SnakePoint(val x: Int, val y: Int)

enum class SnakeDir { UP, DOWN, LEFT, RIGHT }

@Composable
fun SnakeScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  highScore: Int,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  val gridCols = 18
  val gridRows = 18

  var snake by remember {
    mutableStateOf(
      listOf(
        SnakePoint(9, 9),
        SnakePoint(8, 9),
        SnakePoint(7, 9)
      )
    )
  }
  var direction by remember { mutableStateOf(SnakeDir.RIGHT) }
  var nextDirection by remember { mutableStateOf(SnakeDir.RIGHT) }
  var food by remember { mutableStateOf(SnakePoint(14, 9)) }
  var score by remember { mutableIntStateOf(0) }
  var bestScore by remember { mutableIntStateOf(highScore) }
  var isGameOver by remember { mutableStateOf(false) }

  val baseSpeedMs = when (difficulty) {
    "EASY" -> 160L
    "MEDIUM" -> 125L
    "HARD" -> 95L
    else -> 75L // EXPERT
  }

  fun spawnFood(s: List<SnakePoint>): SnakePoint {
    val empty = mutableListOf<SnakePoint>()
    for (x in 0 until gridCols) {
      for (y in 0 until gridRows) {
        val pt = SnakePoint(x, y)
        if (!s.contains(pt)) empty.add(pt)
      }
    }
    return if (empty.isNotEmpty()) empty.random() else SnakePoint(0, 0)
  }

  fun restartGame() {
    snake = listOf(SnakePoint(9, 9), SnakePoint(8, 9), SnakePoint(7, 9))
    direction = SnakeDir.RIGHT
    nextDirection = SnakeDir.RIGHT
    score = 0
    isGameOver = false
    food = spawnFood(snake)
    soundEngine.playPop()
  }

  // Game Loop
  LaunchedEffect(isGameOver, difficulty) {
    while (isActive && !isGameOver) {
      val speed = (baseSpeedMs - (score * 2L)).coerceAtLeast(50L)
      delay(speed)

      direction = nextDirection
      val head = snake.first()
      val newHead = when (direction) {
        SnakeDir.UP -> SnakePoint(head.x, head.y - 1)
        SnakeDir.DOWN -> SnakePoint(head.x, head.y + 1)
        SnakeDir.LEFT -> SnakePoint(head.x - 1, head.y)
        SnakeDir.RIGHT -> SnakePoint(head.x + 1, head.y)
      }

      // Check collision with walls
      if (newHead.x < 0 || newHead.x >= gridCols || newHead.y < 0 || newHead.y >= gridRows) {
        isGameOver = true
        soundEngine.playGameOver()
        hapticEngine.vibrateCrash()
        particleSystem.spawnImpactDust(500f, 500f, 35)
        break
      }

      // Check collision with self
      if (snake.contains(newHead)) {
        isGameOver = true
        soundEngine.playGameOver()
        hapticEngine.vibrateCrash()
        particleSystem.spawnImpactDust(500f, 500f, 35)
        break
      }

      val newSnake = mutableListOf(newHead)
      if (newHead == food) {
        // Ate food!
        newSnake.addAll(snake)
        score += 10
        if (score > bestScore) {
          bestScore = score
          onScoreUpdated(score)
        }
        food = spawnFood(newSnake)
        soundEngine.playScore()
        hapticEngine.vibrateTap()
        particleSystem.spawnScoreSparkles(500f, 400f, 20)
      } else {
        newSnake.addAll(snake.dropLast(1))
      }
      snake = newSnake
    }
  }

  fun setDir(newDir: SnakeDir) {
    if (isGameOver) return
    val isOpposite = (direction == SnakeDir.UP && newDir == SnakeDir.DOWN) ||
                     (direction == SnakeDir.DOWN && newDir == SnakeDir.UP) ||
                     (direction == SnakeDir.LEFT && newDir == SnakeDir.RIGHT) ||
                     (direction == SnakeDir.RIGHT && newDir == SnakeDir.LEFT)
    if (!isOpposite) {
      nextDirection = newDir
      hapticEngine.vibrateMove()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "Snake Deluxe",
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

    if (isGameOver) {
      Text(
        text = "💀 SNAKE CRASHED! TAP RESET TO TRY AGAIN",
        color = CandyWatermelon,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp,
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(vertical = 4.dp)
      )
    }

    // Grid Arena with Swipe Gesture
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      var dragX by remember { mutableFloatStateOf(0f) }
      var dragY by remember { mutableFloatStateOf(0f) }

      Box(
        modifier = Modifier
          .size(330.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(Color(0xFF142422))
          .border(2.dp, CandyMint.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
          .padding(6.dp)
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = {
                dragX = 0f
                dragY = 0f
              },
              onDrag = { change, dragAmount ->
                change.consume()
                dragX += dragAmount.x
                dragY += dragAmount.y
              },
              onDragEnd = {
                if (abs(dragX) > abs(dragY)) {
                  if (dragX > 30f) setDir(SnakeDir.RIGHT)
                  else if (dragX < -30f) setDir(SnakeDir.LEFT)
                } else {
                  if (dragY > 30f) setDir(SnakeDir.DOWN)
                  else if (dragY < -30f) setDir(SnakeDir.UP)
                }
              }
            )
          }
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val cellW = size.width / gridCols
          val cellH = size.height / gridRows

          // Draw Food (Pulsing Apple)
          val fx = food.x * cellW
          val fy = food.y * cellH
          drawCircle(
            brush = Brush.radialGradient(listOf(CandyWatermelon, Color(0xFFB3003B))),
            radius = cellW * 0.46f,
            center = Offset(fx + cellW / 2, fy + cellH / 2)
          )
          // Leaf
          drawCircle(
            color = CandyMint,
            radius = cellW * 0.16f,
            center = Offset(fx + cellW * 0.7f, fy + cellH * 0.2f)
          )

          // Draw Snake Segments
          for (i in snake.indices) {
            val seg = snake[i]
            val sx = seg.x * cellW
            val sy = seg.y * cellH
            val isHead = (i == 0)

            if (isHead) {
              drawRoundRect(
                brush = Brush.verticalGradient(listOf(CandyMint, Color(0xFF00B377))),
                topLeft = Offset(sx + 1f, sy + 1f),
                size = Size(cellW - 2f, cellH - 2f),
                cornerRadius = CornerRadius(6f, 6f)
              )
              // Eyes on head
              val eyeRadius = cellW * 0.14f
              val eyeOffset = when (direction) {
                SnakeDir.RIGHT -> Offset(sx + cellW * 0.75f, sy + cellH * 0.35f)
                SnakeDir.LEFT -> Offset(sx + cellW * 0.25f, sy + cellH * 0.35f)
                SnakeDir.UP -> Offset(sx + cellW * 0.35f, sy + cellH * 0.25f)
                SnakeDir.DOWN -> Offset(sx + cellW * 0.35f, sy + cellH * 0.75f)
              }
              drawCircle(Color.White, eyeRadius, eyeOffset)
              drawCircle(Color.Black, eyeRadius * 0.5f, eyeOffset)
            } else {
              drawRoundRect(
                color = CandyMint.copy(alpha = 0.85f - (i * 0.015f).coerceAtMost(0.4f)),
                topLeft = Offset(sx + 2f, sy + 2f),
                size = Size(cellW - 4f, cellH - 4f),
                cornerRadius = CornerRadius(4f, 4f)
              )
            }
          }
        }
      }
    }

    // Chunky D-Pad Controls
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DPadArrow(Icons.Default.KeyboardArrowUp) { setDir(SnakeDir.UP) }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
          DPadArrow(Icons.Default.KeyboardArrowLeft) { setDir(SnakeDir.LEFT) }
          DPadArrow(Icons.Default.KeyboardArrowDown) { setDir(SnakeDir.DOWN) }
          DPadArrow(Icons.Default.KeyboardArrowRight) { setDir(SnakeDir.RIGHT) }
        }
      }
    }
  }
}

@Composable
private fun DPadArrow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit
) {
  IconButton(
    onClick = onClick,
    modifier = Modifier
      .size(46.dp)
      .clip(CircleShape)
      .background(VaultSurfaceHighlight)
      .border(1.dp, CandyMint.copy(alpha = 0.6f), CircleShape)
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = CandyMint)
  }
}
