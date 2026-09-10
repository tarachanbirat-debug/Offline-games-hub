package com.example.ui.particles

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.*
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  val size: Float,
  val color: Color,
  var alpha: Float = 1f,
  var rotation: Float = 0f,
  var rotSpeed: Float = 0f,
  val shape: ParticleShape = ParticleShape.CONFETTI_RECT,
  var lifetime: Float = 1f, // remaining life 1.0 to 0.0
  val decayRate: Float = 0.015f
)

enum class ParticleShape {
  CONFETTI_RECT,
  SPARKLE_STAR,
  CIRCLE,
  COIN
}

class ParticleSystem {
  val particles = mutableListOf<Particle>()

  private val candyColors = listOf(
    CandyWatermelon,
    CandyTangerine,
    CandyLemon,
    CandyMint,
    CandyCyan,
    CandyGrape,
    CandyCoral,
    CandySkyBlue,
    Color.White
  )

  fun spawnVictoryConfetti(originX: Float, originY: Float, count: Int = 100) {
    for (i in 0 until count) {
      val angle = Random.nextDouble(0.0, Math.PI * 2)
      val speed = Random.nextFloat() * 700f + 200f
      particles.add(
        Particle(
          x = originX + Random.nextFloat() * 100f - 50f,
          y = originY + Random.nextFloat() * 60f - 30f,
          vx = (cos(angle) * speed).toFloat(),
          vy = (sin(angle) * speed - 450f).toFloat(), // Upward burst
          size = Random.nextFloat() * 14f + 8f,
          color = candyColors.random(),
          rotation = Random.nextFloat() * 360f,
          rotSpeed = (Random.nextFloat() * 12f - 6f),
          shape = if (Random.nextBoolean()) ParticleShape.CONFETTI_RECT else ParticleShape.SPARKLE_STAR,
          decayRate = Random.nextFloat() * 0.008f + 0.007f
        )
      )
    }
  }

  fun spawnImpactDust(x: Float, y: Float, count: Int = 30) {
    for (i in 0 until count) {
      val angle = Random.nextDouble(0.0, Math.PI * 2)
      val speed = Random.nextFloat() * 350f + 80f
      particles.add(
        Particle(
          x = x,
          y = y,
          vx = (cos(angle) * speed).toFloat(),
          vy = (sin(angle) * speed).toFloat(),
          size = Random.nextFloat() * 8f + 4f,
          color = listOf(Color.White, CandyTangerine, CandyLemon).random(),
          shape = ParticleShape.CIRCLE,
          decayRate = 0.035f
        )
      )
    }
  }

  fun spawnScoreSparkles(x: Float, y: Float, count: Int = 20) {
    for (i in 0 until count) {
      val angle = Random.nextDouble(-Math.PI * 0.8, -Math.PI * 0.2) // upward cone
      val speed = Random.nextFloat() * 400f + 150f
      particles.add(
        Particle(
          x = x,
          y = y,
          vx = (cos(angle) * speed).toFloat(),
          vy = (sin(angle) * speed).toFloat(),
          size = Random.nextFloat() * 10f + 5f,
          color = listOf(CandyLemon, CandyMint, CandyCyan).random(),
          shape = ParticleShape.SPARKLE_STAR,
          decayRate = 0.025f
        )
      )
    }
  }

  fun update(dt: Float) {
    val gravity = 980f // px/s²
    val drag = 0.985f

    val iterator = particles.iterator()
    while (iterator.hasNext()) {
      val p = iterator.next()
      p.vy += gravity * dt
      p.vx *= drag
      p.vy *= drag
      p.x += p.vx * dt
      p.y += p.vy * dt
      p.rotation += p.rotSpeed
      p.lifetime -= p.decayRate
      p.alpha = p.lifetime.coerceIn(0f, 1f)

      if (p.lifetime <= 0f) {
        iterator.remove()
      }
    }
  }

  fun draw(scope: DrawScope) {
    for (p in particles) {
      val drawColor = p.color.copy(alpha = p.alpha)
      when (p.shape) {
        ParticleShape.CONFETTI_RECT -> {
          scope.rotate(p.rotation, pivot = Offset(p.x, p.y)) {
            drawRect(
              color = drawColor,
              topLeft = Offset(p.x - p.size / 2, p.y - p.size / 4),
              size = Size(p.size, p.size / 2)
            )
          }
        }
        ParticleShape.SPARKLE_STAR -> {
          scope.rotate(p.rotation, pivot = Offset(p.x, p.y)) {
            drawLine(
              color = drawColor,
              start = Offset(p.x - p.size, p.y),
              end = Offset(p.x + p.size, p.y),
              strokeWidth = 3f
            )
            drawLine(
              color = drawColor,
              start = Offset(p.x, p.y - p.size),
              end = Offset(p.x, p.y + p.size),
              strokeWidth = 3f
            )
          }
        }
        ParticleShape.CIRCLE, ParticleShape.COIN -> {
          scope.drawCircle(
            color = drawColor,
            radius = p.size / 2,
            center = Offset(p.x, p.y)
          )
        }
      }
    }
  }
}

@Composable
fun ParticleOverlay(
  particleSystem: ParticleSystem,
  modifier: Modifier = Modifier
) {
  var lastFrameTime by remember { mutableStateOf(0L) }

  LaunchedEffect(particleSystem) {
    while (isActive) {
      withInfiniteAnimationFrameMillis { frameTime ->
        if (lastFrameTime != 0L) {
          val dt = ((frameTime - lastFrameTime) / 1000f).coerceIn(0.001f, 0.05f)
          particleSystem.update(dt)
        }
        lastFrameTime = frameTime
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    particleSystem.draw(this)
  }
}
