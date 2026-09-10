package com.example.games.brickbreaker

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.isActive
import kotlin.random.Random

data class Brick(
  val x: Float,
  val y: Float,
  val width: Float,
  val height: Float,
  val color: Color,
  var isAlive: Boolean = true
)

@Composable
fun BrickBreakerScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  highScore: Int,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  var score by remember { mutableIntStateOf(0) }
  var bestScore by remember { mutableIntStateOf(highScore) }
  var isGameOver by remember { mutableStateOf(false) }
  var isWon by remember { mutableStateOf(false) }

  var paddleX by remember { mutableFloatStateOf(0.5f) } // 0.0 to 1.0 (screen ratio)
  var ballX by remember { mutableFloatStateOf(0.5f) }
  var ballY by remember { mutableFloatStateOf(0.7f) }
  var ballVx by remember { mutableFloatStateOf(0.35f) }
  var ballVy by remember { mutableFloatStateOf(-0.45f) }

  val bricks = remember { mutableStateListOf<Brick>() }

  val speedMultiplier = when (difficulty) {
    "EASY" -> 0.85f
    "MEDIUM" -> 1.05f
    "HARD" -> 1.3f
    else -> 1.55f // EXPERT
  }

  fun initBricks(canvasW: Float, canvasH: Float) {
    bricks.clear()
    val cols = 5
    val rows = 4
    val pad = 8f
    val totalPad = (cols + 1) * pad
    val bWidth = (canvasW - totalPad) / cols
    val bHeight = 26f

    val rowColors = listOf(CandyWatermelon, CandyTangerine, CandyLemon, CandyMint)
    for (r in 0 until rows) {
      for (c in 0 until cols) {
        val bx = pad + c * (bWidth + pad)
        val by = 60f + r * (bHeight + pad)
        bricks.add(Brick(bx, by, bWidth, bHeight, rowColors[r]))
      }
    }
  }

  fun restartGame() {
    score = 0
    isGameOver = false
    isWon = false
    paddleX = 0.5f
    ballX = 0.5f
    ballY = 0.7f
    ballVx = (if (Random.nextBoolean()) 0.35f else -0.35f) * speedMultiplier
    ballVy = -0.45f * speedMultiplier
    bricks.forEach { it.isAlive = true }
    soundEngine.playPop()
  }

  // 60FPS Game Loop
  LaunchedEffect(isGameOver, isWon) {
    var lastTime = 0L
    while (isActive && !isGameOver && !isWon) {
      withInfiniteAnimationFrameMillis { now ->
        if (lastTime != 0L) {
          val dt = ((now - lastTime) / 1000f).coerceIn(0.001f, 0.05f)

          ballX += ballVx * dt
          ballY += ballVy * dt

          // Wall bounces
          if (ballX <= 0.03f) {
            ballX = 0.03f
            ballVx = -ballVx
            soundEngine.playSnap()
          } else if (ballX >= 0.97f) {
            ballX = 0.97f
            ballVx = -ballVx
            soundEngine.playSnap()
          }

          if (ballY <= 0.02f) {
            ballY = 0.02f
            ballVy = -ballVy
            soundEngine.playSnap()
          }

          // Bottom screen: Miss ball -> Game over
          if (ballY >= 0.98f) {
            isGameOver = true
            soundEngine.playGameOver()
            hapticEngine.vibrateCrash()
          }
        }
        lastTime = now
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "Neon Brick Breaker",
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

    if (isWon) {
      Text("🎉 ALL BRICKS CLEARED!", color = CandyMint, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 4.dp))
    } else if (isGameOver) {
      Text("💥 BALL LOST! TAP RESET", color = CandyWatermelon, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 4.dp))
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            change.consume()
            paddleX = (paddleX + dragAmount.x / size.width).coerceIn(0.12f, 0.88f)
          }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        if (bricks.isEmpty()) {
          initBricks(w, h)
        }

        // Draw Bricks & Check Ball-Brick Collision
        val ballPxX = ballX * w
        val ballPxY = ballY * h
        val ballR = 9f

        for (brick in bricks) {
          if (brick.isAlive) {
            drawRoundRect(
              color = brick.color,
              topLeft = Offset(brick.x, brick.y),
              size = Size(brick.width, brick.height),
              cornerRadius = CornerRadius(6f, 6f)
            )

            // Collision with ball
            if (ballPxX in (brick.x - ballR)..(brick.x + brick.width + ballR) &&
                ballPxY in (brick.y - ballR)..(brick.y + brick.height + ballR)) {
              brick.isAlive = false
              ballVy = -ballVy
              score += 20
              if (score > bestScore) {
                bestScore = score
                onScoreUpdated(score)
              }
              soundEngine.playScore()
              hapticEngine.vibrateTap()
              particleSystem.spawnScoreSparkles(ballPxX, ballPxY, 15)

              if (bricks.none { it.isAlive }) {
                isWon = true
                soundEngine.playVictory()
                hapticEngine.vibrateSuccess()
                particleSystem.spawnVictoryConfetti(500f, 400f, 90)
              }
            }
          }
        }

        // Paddle at bottom
        val padW = w * 0.28f
        val padH = 16f
        val padLeft = paddleX * w - padW / 2
        val padTop = h * 0.88f

        drawRoundRect(
          brush = Brush.horizontalGradient(listOf(CandyCyan, CandyGrape)),
          topLeft = Offset(padLeft, padTop),
          size = Size(padW, padH),
          cornerRadius = CornerRadius(8f, 8f)
        )

        // Paddle Collision
        if (ballPxY + ballR >= padTop && ballPxY - ballR <= padTop + padH &&
            ballPxX in padLeft..(padLeft + padW)) {
          ballVy = -kotlin.math.abs(ballVy)
          // Angle deflection based on hit position
          val hitOffset = (ballPxX - (padLeft + padW / 2)) / (padW / 2)
          ballVx = hitOffset * 0.5f * speedMultiplier
          soundEngine.playSnap()
          hapticEngine.vibrateMove()
        }

        // Draw Neon Ball
        drawCircle(
          brush = Brush.radialGradient(listOf(Color.White, CandyCyan)),
          radius = ballR,
          center = Offset(ballPxX, ballPxY)
        )
      }
    }

    Text(
      text = "DRAG TO MOVE NEON PADDLE",
      color = VaultTextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 16.dp)
    )
  }
}
