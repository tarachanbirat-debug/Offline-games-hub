package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.particles.ParticleSystem
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun PongScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String = "MEDIUM",
  highScore: Int = 0,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var playerScore by remember { mutableIntStateOf(0) }
  var aiScore by remember { mutableIntStateOf(0) }
  var isPaused by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }

  // Dimensions & Physics state
  var canvasWidth by remember { mutableFloatStateOf(0f) }
  var canvasHeight by remember { mutableFloatStateOf(0f) }

  val paddleWidth = 240f
  val paddleHeight = 32f
  val ballRadius = 20f

  var playerPaddleX by remember { mutableFloatStateOf(300f) }
  var aiPaddleX by remember { mutableFloatStateOf(300f) }

  var ballX by remember { mutableFloatStateOf(400f) }
  var ballY by remember { mutableFloatStateOf(600f) }
  var ballVx by remember { mutableFloatStateOf(8f) }
  var ballVy by remember { mutableFloatStateOf(10f) }

  val aiSpeed = when (difficulty.uppercase()) {
    "EASY" -> 6.2f
    "HARD" -> 10.5f
    "EXPERT" -> 12f
    else -> 8.5f
  }

  // Game Loop (60 - 120 FPS via 16ms tick)
  LaunchedEffect(isPaused, isGameOver, canvasWidth, canvasHeight) {
    if (canvasWidth <= 0 || canvasHeight <= 0) return@LaunchedEffect

    while (!isPaused && !isGameOver) {
      delay(16)

      // Move Ball
      ballX += ballVx
      ballY += ballVy

      // Wall Bounce (Left / Right)
      if (ballX - ballRadius <= 0f) {
        ballX = ballRadius
        ballVx = abs(ballVx)
        soundEngine.playSnap()
        hapticEngine.vibrateTap()
      } else if (ballX + ballRadius >= canvasWidth) {
        ballX = canvasWidth - ballRadius
        ballVx = -abs(ballVx)
        soundEngine.playSnap()
        hapticEngine.vibrateTap()
      }

      // Player Paddle Collision (Bottom)
      val playerY = canvasHeight - 80f
      if (ballY + ballRadius >= playerY && ballY - ballRadius <= playerY + paddleHeight) {
        if (ballX >= playerPaddleX - 20f && ballX <= playerPaddleX + paddleWidth + 20f) {
          ballVy = -abs(ballVy) * 1.03f
          val hitOffset = (ballX - (playerPaddleX + paddleWidth / 2f)) / (paddleWidth / 2f)
          ballVx = hitOffset * 14f
          playerScore++
          onScoreUpdated(playerScore)
          soundEngine.playScore()
          hapticEngine.vibrateSuccess()
          particleSystem.spawnScoreSparkles(ballX, ballY, 12)
        }
      }

      // AI Paddle Collision (Top)
      val aiY = 60f
      if (ballY - ballRadius <= aiY + paddleHeight && ballY + ballRadius >= aiY) {
        if (ballX >= aiPaddleX - 20f && ballX <= aiPaddleX + paddleWidth + 20f) {
          ballVy = abs(ballVy) * 1.03f
          val hitOffset = (ballX - (aiPaddleX + paddleWidth / 2f)) / (paddleWidth / 2f)
          ballVx = hitOffset * 14f
          soundEngine.playSnap()
          hapticEngine.vibrateTap()
          particleSystem.spawnImpactDust(ballX, ballY, 10)
        }
      }

      // AI Logic: Smooth tracking towards ball
      val aiCenter = aiPaddleX + paddleWidth / 2f
      if (aiCenter < ballX - 12f) {
        aiPaddleX = (aiPaddleX + aiSpeed).coerceAtMost(canvasWidth - paddleWidth)
      } else if (aiCenter > ballX + 12f) {
        aiPaddleX = (aiPaddleX - aiSpeed).coerceAtLeast(0f)
      }

      // Score checking
      if (ballY > canvasHeight) {
        aiScore++
        soundEngine.playPop()
        hapticEngine.vibrateTap()
        particleSystem.spawnImpactDust(ballX, canvasHeight, 20)
        if (aiScore >= 7) {
          isGameOver = true
        } else {
          // Reset ball to center
          ballX = canvasWidth / 2f
          ballY = canvasHeight / 2f
          ballVy = -10f
          ballVx = if (Random.nextBoolean()) 8f else -8f
        }
      } else if (ballY < 0f) {
        playerScore += 5
        onScoreUpdated(playerScore)
        soundEngine.playVictory()
        hapticEngine.vibrateSuccess()
        particleSystem.spawnVictoryConfetti(ballX, 50f, 25)
        if (playerScore >= 15) {
          isGameOver = true
        } else {
          ballX = canvasWidth / 2f
          ballY = canvasHeight / 2f
          ballVy = 10f
          ballVx = if (Random.nextBoolean()) 8f else -8f
        }
      }
    }
  }

  fun resetGame() {
    playerScore = 0
    aiScore = 0
    isGameOver = false
    isPaused = false
    if (canvasWidth > 0 && canvasHeight > 0) {
      ballX = canvasWidth / 2f
      ballY = canvasHeight / 2f
      ballVx = 8f
      ballVy = 10f
      playerPaddleX = (canvasWidth - paddleWidth) / 2f
      aiPaddleX = (canvasWidth - paddleWidth) / 2f
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
      .systemBarsPadding()
  ) {
    // 1. Top HUD
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(VaultSurfaceElevated)
          .testTag("pong_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("YOU", color = CandyCyan, fontSize = 11.sp, fontWeight = FontWeight.Black)
          Text("$playerScore", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        Text("VS", color = VaultTextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("AI", color = CandyWatermelon, fontSize = 11.sp, fontWeight = FontWeight.Black)
          Text("$aiScore", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(
          onClick = { isPaused = !isPaused },
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(VaultSurfaceElevated)
            .testTag("pong_pause_button")
        ) {
          Icon(
            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
            contentDescription = "Pause",
            tint = Color.White
          )
        }
        IconButton(
          onClick = { resetGame() },
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(VaultSurfaceElevated)
            .testTag("pong_reset_button")
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
        }
      }
    }

    // 2. Interactive Pong Arena
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(16.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(VaultSurfaceElevated)
        .pointerInput(canvasWidth) {
          detectDragGestures { change, dragAmount ->
            change.consume()
            playerPaddleX = (playerPaddleX + dragAmount.x).coerceIn(0f, canvasWidth - paddleWidth)
          }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        canvasWidth = size.width
        canvasHeight = size.height

        // Center court net
        val dashHeight = 12.dp.toPx()
        val dashGap = 12.dp.toPx()
        var currentY = 0f
        while (currentY < size.height) {
          drawLine(
            color = VaultBorder.copy(alpha = 0.4f),
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2.dp.toPx()
          )
          currentY += dashHeight + dashGap
        }

        // AI Paddle (Top - Candy Watermelon)
        drawRoundRect(
          brush = Brush.horizontalGradient(listOf(CandyWatermelon, CandyTangerine)),
          topLeft = Offset(aiPaddleX, 60f),
          size = Size(paddleWidth, paddleHeight),
          cornerRadius = CornerRadius(16f, 16f)
        )

        // Player Paddle (Bottom - Candy Cyan)
        drawRoundRect(
          brush = Brush.horizontalGradient(listOf(CandyCyan, CandyMint)),
          topLeft = Offset(playerPaddleX, size.height - 80f),
          size = Size(paddleWidth, paddleHeight),
          cornerRadius = CornerRadius(16f, 16f)
        )

        // Glowing Ball
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color.White, CandyLemon, Color.Transparent),
            center = Offset(ballX, ballY),
            radius = ballRadius * 1.5f
          ),
          radius = ballRadius,
          center = Offset(ballX, ballY)
        )
      }

      // Overlays
      if (isGameOver) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1638)),
          modifier = Modifier
            .padding(24.dp)
            .align(Alignment.Center)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (playerScore > aiScore) "VICTORY! 🏆" else "MATCH OVER",
              color = if (playerScore > aiScore) CandyMint else CandyWatermelon,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Final Score: $playerScore - $aiScore",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = { resetGame() },
              colors = ButtonDefaults.buttonColors(containerColor = CandyCyan),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("Play Again", color = Color.Black, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }
  }
}
