package com.example.games.highwayracer

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.Path
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
import kotlinx.coroutines.isActive
import kotlin.random.Random

data class TrafficCar(
  var lane: Int, // 0, 1, 2
  var y: Float,  // 0f (horizon) to 1f (bottom)
  val speed: Float,
  val color: Color
)

data class HighwayCoin(
  val lane: Int,
  var y: Float
)

@Composable
fun HighwayRacerScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  highScore: Int,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  var playerLane by remember { mutableIntStateOf(1) } // 0 = left, 1 = middle, 2 = right
  var score by remember { mutableIntStateOf(0) }
  var bestScore by remember { mutableIntStateOf(highScore) }
  var isGameOver by remember { mutableStateOf(false) }

  val traffic = remember { mutableStateListOf<TrafficCar>() }
  val coins = remember { mutableStateListOf<HighwayCoin>() }
  var roadOffset by remember { mutableFloatStateOf(0f) }

  val trafficSpawnInterval = when (difficulty) {
    "EASY" -> 1600L
    "MEDIUM" -> 1100L
    "HARD" -> 850L
    else -> 650L // EXPERT
  }

  fun restartGame() {
    playerLane = 1
    score = 0
    isGameOver = false
    traffic.clear()
    coins.clear()
    soundEngine.playPop()
  }

  // 60FPS Game Loop
  LaunchedEffect(isGameOver) {
    var lastTime = 0L
    var lastTrafficSpawn = 0L
    var lastCoinSpawn = 0L

    while (isActive && !isGameOver) {
      withInfiniteAnimationFrameMillis { now ->
        if (lastTime != 0L) {
          val dt = ((now - lastTime) / 1000f).coerceIn(0.001f, 0.05f)

          // Road movement
          roadOffset = (roadOffset + dt * 4f) % 1f

          // Spawn traffic
          if (now - lastTrafficSpawn > trafficSpawnInterval) {
            val lane = Random.nextInt(3)
            val color = listOf(CandyWatermelon, CandyTangerine, CandyLemon, CandyGrape).random()
            traffic.add(TrafficCar(lane = lane, y = 0.1f, speed = Random.nextFloat() * 0.4f + 0.35f, color = color))
            lastTrafficSpawn = now
          }

          // Spawn coin
          if (now - lastCoinSpawn > 2200L) {
            val lane = Random.nextInt(3)
            coins.add(HighwayCoin(lane = lane, y = 0.1f))
            lastCoinSpawn = now
          }

          // Update traffic
          val trafficIter = traffic.iterator()
          while (trafficIter.hasNext()) {
            val car = trafficIter.next()
            car.y += car.speed * dt
            // Check collision with player (player is at y = 0.78f)
            if (car.lane == playerLane && car.y in 0.70f..0.86f) {
              isGameOver = true
              soundEngine.playGameOver()
              hapticEngine.vibrateCrash()
              particleSystem.spawnImpactDust(500f, 600f, 40)
              break
            }
            if (car.y > 1.1f) {
              trafficIter.remove()
              score += 10
              if (score > bestScore) {
                bestScore = score
                onScoreUpdated(score)
              }
            }
          }

          // Update coins
          val coinIter = coins.iterator()
          while (coinIter.hasNext()) {
            val coin = coinIter.next()
            coin.y += 0.5f * dt
            if (coin.lane == playerLane && coin.y in 0.72f..0.84f) {
              coinIter.remove()
              score += 25
              if (score > bestScore) {
                bestScore = score
                onScoreUpdated(score)
              }
              soundEngine.playScore()
              hapticEngine.vibrateTap()
              particleSystem.spawnScoreSparkles(500f, 600f, 18)
            } else if (coin.y > 1.1f) {
              coinIter.remove()
            }
          }
        }
        lastTime = now
      }
    }
  }

  fun switchLane(direction: Int) {
    if (isGameOver) return
    val newLane = (playerLane + direction).coerceIn(0, 2)
    if (newLane != playerLane) {
      playerLane = newLane
      soundEngine.playSnap()
      hapticEngine.vibrateMove()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "Highway Racer 3D",
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
        text = "💥 VEHICLE CRASH! TAP RESTART",
        color = CandyWatermelon,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(vertical = 4.dp)
      )
    }

    // Canvas Perspective Road
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .pointerInput(Unit) {
          detectTapGestures { offset ->
            // Tap left half or right half of screen
            if (offset.x < size.width / 3) switchLane(-1)
            else if (offset.x > (size.width * 2) / 3) switchLane(1)
            else playerLane = 1
          }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Sky & Horizon
        drawRect(
          brush = Brush.verticalGradient(listOf(Color(0xFF130E26), VaultBackground)),
          size = Size(w, h * 0.25f)
        )

        // 3D Perspective Road Shape
        val roadTopW = w * 0.35f
        val roadTopX = (w - roadTopW) / 2
        val roadPath = Path().apply {
          moveTo(roadTopX, h * 0.15f)
          lineTo(roadTopX + roadTopW, h * 0.15f)
          lineTo(w * 0.96f, h)
          lineTo(w * 0.04f, h)
          close()
        }

        drawPath(
          brush = Brush.verticalGradient(listOf(Color(0xFF261D42), Color(0xFF100B21))),
          path = roadPath
        )

        // Road Lane Dividers with perspective
        fun getLaneCenterX(lane: Int, yRatio: Float): Float {
          val currentRoadW = roadTopW + (w * 0.92f - roadTopW) * yRatio
          val leftX = (w - currentRoadW) / 2
          val laneW = currentRoadW / 3f
          return leftX + (lane + 0.5f) * laneW
        }

        // Animated Dash Markings
        for (line in 0..1) {
          for (d in 0..6) {
            val yRatio = ((d / 7f + roadOffset) % 1f)
            val currentRoadW = roadTopW + (w * 0.92f - roadTopW) * yRatio
            val leftX = (w - currentRoadW) / 2
            val laneW = currentRoadW / 3f
            val markX = leftX + (line + 1) * laneW
            val markY = h * 0.15f + (h * 0.85f) * yRatio

            drawCircle(
              color = CandyLemon.copy(alpha = 0.8f),
              radius = 2f + yRatio * 4f,
              center = Offset(markX, markY)
            )
          }
        }

        // Draw Gold Coins
        for (coin in coins) {
          val cx = getLaneCenterX(coin.lane, coin.y)
          val cy = h * 0.15f + (h * 0.85f) * coin.y
          val coinR = 8f + coin.y * 14f
          drawCircle(CandyLemon, coinR, Offset(cx, cy))
          drawCircle(Color.White, coinR * 0.5f, Offset(cx, cy))
        }

        // Draw Traffic Cars
        for (car in traffic) {
          val cx = getLaneCenterX(car.lane, car.y)
          val cy = h * 0.15f + (h * 0.85f) * car.y
          val carW = 20f + car.y * 55f
          val carH = 30f + car.y * 70f

          drawRoundRect(
            color = car.color,
            topLeft = Offset(cx - carW / 2, cy - carH / 2),
            size = Size(carW, carH),
            cornerRadius = CornerRadius(6f, 6f)
          )
          // Windshield
          drawRoundRect(
            color = Color(0xFF1E1638),
            topLeft = Offset(cx - carW * 0.35f, cy - carH * 0.2f),
            size = Size(carW * 0.7f, carH * 0.35f),
            cornerRadius = CornerRadius(3f, 3f)
          )
        }

        // Draw Player Sports Car (Cyan) at y = 0.78
        val px = getLaneCenterX(playerLane, 0.78f)
        val py = h * 0.15f + (h * 0.85f) * 0.78f
        val pw = 65f
        val ph = 85f

        // Shadow
        drawOval(
          color = Color.Black.copy(alpha = 0.6f),
          topLeft = Offset(px - pw * 0.6f, py + ph * 0.3f),
          size = Size(pw * 1.2f, ph * 0.35f)
        )

        // Car Body
        drawRoundRect(
          brush = Brush.verticalGradient(listOf(CandyCyan, Color(0xFF007A99))),
          topLeft = Offset(px - pw / 2, py - ph / 2),
          size = Size(pw, ph),
          cornerRadius = CornerRadius(14f, 14f)
        )
        // Windshield
        drawRoundRect(
          color = Color(0xFF16102B),
          topLeft = Offset(px - pw * 0.32f, py - ph * 0.22f),
          size = Size(pw * 0.64f, ph * 0.36f),
          cornerRadius = CornerRadius(6f, 6f)
        )
        // Headlights
        drawCircle(CandyLemon, 6f, Offset(px - pw * 0.32f, py - ph * 0.44f))
        drawCircle(CandyLemon, 6f, Offset(px + pw * 0.32f, py - ph * 0.44f))
      }
    }

    // Lane Switch Controls (Bottom)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { switchLane(-1) },
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(VaultSurfaceHighlight)
          .testTag("racer_steer_left")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left Lane", tint = Color.White, modifier = Modifier.size(28.dp))
      }

      Text(
        text = "TAP TO STEER LANES",
        color = VaultTextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )

      IconButton(
        onClick = { switchLane(1) },
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(VaultSurfaceHighlight)
          .testTag("racer_steer_right")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right Lane", tint = Color.White, modifier = Modifier.size(28.dp))
      }
    }
  }
}
