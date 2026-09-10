package com.example.games.miniludo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.components.UniversalGameHeader
import com.example.ui.particles.ParticleSystem
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Pawn state: -1 = in Base, 0..15 = on Main Track, 16..18 = Home Stretch, 19 = HOME GOAL
data class LudoPawn(
  val id: Int,
  val isPlayer: Boolean, // true = Red (Human), false = Blue (AI)
  var position: Int = -1
)

@Composable
fun MiniLudoScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  var playerPawns by remember { mutableStateOf(listOf(LudoPawn(1, true), LudoPawn(2, true))) }
  var aiPawns by remember { mutableStateOf(listOf(LudoPawn(3, false), LudoPawn(4, false))) }

  var isPlayerTurn by remember { mutableStateOf(true) }
  var diceValue by remember { mutableIntStateOf(1) }
  var isRolling by remember { mutableStateOf(false) }
  var hasRolled by remember { mutableStateOf(false) }
  var winner by remember { mutableStateOf<String?>(null) } // "HUMAN" or "AI"
  var humanWins by remember { mutableIntStateOf(0) }

  val scope = rememberCoroutineScope()

  val rollRotation by animateFloatAsState(
    targetValue = if (isRolling) 360f else 0f,
    animationSpec = tween(300),
    label = "dice_rot"
  )

  fun restartGame() {
    playerPawns = listOf(LudoPawn(1, true), LudoPawn(2, true))
    aiPawns = listOf(LudoPawn(3, false), LudoPawn(4, false))
    isPlayerTurn = true
    diceValue = 1
    isRolling = false
    hasRolled = false
    winner = null
    soundEngine.playPop()
  }

  fun checkWin() {
    if (playerPawns.all { it.position >= 19 }) {
      winner = "HUMAN"
      humanWins++
      onScoreUpdated(humanWins)
      soundEngine.playVictory()
      hapticEngine.vibrateSuccess()
      particleSystem.spawnVictoryConfetti(500f, 400f, 100)
    } else if (aiPawns.all { it.position >= 19 }) {
      winner = "AI"
      soundEngine.playGameOver()
      hapticEngine.vibrateCrash()
    }
  }

  fun movePawn(pawn: LudoPawn, roll: Int, isHuman: Boolean): Boolean {
    if (pawn.position == -1) {
      // Out of base requirement (Roll 6 or roll >= 5 on Easy)
      val canExit = if (difficulty == "EASY") (roll >= 4) else (roll == 6)
      if (canExit) {
        pawn.position = if (isHuman) 0 else 8 // Start positions
        soundEngine.playSnap()
        hapticEngine.vibrateTap()
        return true
      }
      return false
    }

    if (pawn.position in 0..15) {
      val newPos = pawn.position + roll
      val homeThreshold = if (isHuman) 15 else 23 // simplified track length
      if (isHuman && newPos > 15) {
        pawn.position = minOf(19, 16 + (newPos - 16))
      } else if (!isHuman && pawn.position >= 8 && newPos > 23) {
        pawn.position = minOf(19, 16 + (newPos - 24))
      } else {
        pawn.position = newPos % 16
        // Capture logic
        val enemyPawns = if (isHuman) aiPawns else playerPawns
        val enemyHit = enemyPawns.find { it.position == pawn.position }
        if (enemyHit != null) {
          enemyHit.position = -1 // Sent back to base!
          soundEngine.playScore()
          hapticEngine.vibrateCrash()
          particleSystem.spawnImpactDust(500f, 500f, 25)
        }
      }
      soundEngine.playTap()
      hapticEngine.vibrateMove()
      return true
    }

    if (pawn.position in 16..18) {
      val newPos = pawn.position + roll
      if (newPos <= 19) {
        pawn.position = newPos
        soundEngine.playTap()
        hapticEngine.vibrateMove()
        return true
      }
    }
    return false
  }

  fun rollDice() {
    if (isRolling || hasRolled || winner != null || !isPlayerTurn) return
    isRolling = true
    soundEngine.playSnap()
    hapticEngine.vibrateTap()

    scope.launch {
      delay(300)
      diceValue = Random.nextInt(1, 7)
      isRolling = false
      hasRolled = true

      // Check if player has any valid moves
      val canMove = playerPawns.any {
        (it.position == -1 && (if (difficulty == "EASY") diceValue >= 4 else diceValue == 6)) ||
        (it.position in 0..18)
      }

      if (!canMove) {
        delay(600)
        hasRolled = false
        isPlayerTurn = false
        // Trigger AI Turn
      }
    }
  }

  // Trigger AI Turn when isPlayerTurn = false
  LaunchedEffect(isPlayerTurn, winner) {
    if (!isPlayerTurn && winner == null) {
      delay(700)
      isRolling = true
      soundEngine.playSnap()
      delay(300)
      val aiRoll = Random.nextInt(1, 7)
      diceValue = aiRoll
      isRolling = false

      // AI selects pawn to move
      val candidate = aiPawns.find { p ->
        (p.position == -1 && (if (difficulty == "EASY") aiRoll >= 4 else aiRoll == 6)) ||
        (p.position in 0..18)
      } ?: aiPawns.first()

      val moved = movePawn(candidate, aiRoll, false)
      aiPawns = aiPawns.toList() // trigger recompose
      checkWin()

      delay(600)
      if (aiRoll == 6 && moved && winner == null) {
        // AI gets extra turn!
        isPlayerTurn = false
      } else {
        isPlayerTurn = true
        hasRolled = false
      }
    }
  }

  fun onPlayerPawnClicked(pawn: LudoPawn) {
    if (!isPlayerTurn || !hasRolled || winner != null) return

    val moved = movePawn(pawn, diceValue, true)
    if (moved) {
      playerPawns = playerPawns.toList()
      checkWin()

      if (diceValue == 6 && winner == null) {
        // Extra turn!
        hasRolled = false
      } else {
        hasRolled = false
        isPlayerTurn = false
      }
    } else {
      soundEngine.playError()
      hapticEngine.vibrateCrash()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "Mini Ludo Championship",
      score = humanWins,
      bestScore = humanWins,
      difficulty = difficulty,
      soundEnabled = !soundEngine.isMuted,
      hapticsEnabled = !hapticEngine.isMuted,
      onBack = onBack,
      onReset = { restartGame() },
      onToggleSound = { soundEngine.isMuted = !soundEngine.isMuted },
      onToggleHaptics = { hapticEngine.isMuted = !hapticEngine.isMuted }
    )

    // Turn / Status banner
    Text(
      text = when {
        winner == "HUMAN" -> "🏆 YOU WON THE LUDO MATCH!"
        winner == "AI" -> "🤖 AI WINS! TRY AGAIN!"
        isPlayerTurn && !hasRolled -> "🎲 TAP DICE TO ROLL!"
        isPlayerTurn && hasRolled -> "🔴 TAP A RED PAWN TO MOVE ($diceValue)"
        else -> "🤖 AI ROLLING..."
      },
      color = when (winner) {
        "HUMAN" -> CandyMint
        "AI" -> CandyWatermelon
        else -> Color.White
      },
      fontWeight = FontWeight.Black,
      fontSize = 15.sp,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(vertical = 4.dp)
    )

    // Ludo Arena Board
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(320.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(VaultSurfaceElevated)
          .border(2.dp, VaultBorderGlow, RoundedCornerShape(26.dp))
          .padding(8.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Red Base (Top Left)
          val baseW = w * 0.38f
          drawRoundRect(
            color = CandyWatermelon.copy(alpha = 0.75f),
            topLeft = Offset(4f, 4f),
            size = Size(baseW, baseW),
            cornerRadius = CornerRadius(14f, 14f)
          )

          // Blue Base (Bottom Right)
          drawRoundRect(
            color = CandyCyan.copy(alpha = 0.75f),
            topLeft = Offset(w - baseW - 4f, h - baseW - 4f),
            size = Size(baseW, baseW),
            cornerRadius = CornerRadius(14f, 14f)
          )

          // Center Home Goal Triangle
          drawCircle(
            color = CandyLemon.copy(alpha = 0.85f),
            radius = w * 0.14f,
            center = Offset(w / 2, h / 2)
          )

          // Ring Track 16 tiles
          val ringR = w * 0.34f
          for (step in 0 until 16) {
            val angle = (step * (360f / 16f)) * (Math.PI / 180.0)
            val tx = (w / 2 + Math.cos(angle) * ringR).toFloat()
            val ty = (h / 2 + Math.sin(angle) * ringR).toFloat()
            drawCircle(
              color = if (step == 0) CandyWatermelon else if (step == 8) CandyCyan else VaultCardDark,
              radius = 11f,
              center = Offset(tx, ty)
            )
            drawCircle(
              color = VaultBorderGlow,
              radius = 11f,
              center = Offset(tx, ty),
              style = Stroke(width = 1.5f)
            )
          }
        }

        // Pawns Overlay
        // Red Pawn 1 & 2
        playerPawns.forEach { pawn ->
          val isClickable = isPlayerTurn && hasRolled
          PawnPiece(
            pawn = pawn,
            isClickable = isClickable,
            onClick = { onPlayerPawnClicked(pawn) },
            modifier = Modifier.align(
              if (pawn.position == -1) {
                if (pawn.id == 1) Alignment.TopStart else Alignment.CenterStart
              } else Alignment.Center
            )
          )
        }

        // Blue Pawn 1 & 2
        aiPawns.forEach { pawn ->
          PawnPiece(
            pawn = pawn,
            isClickable = false,
            onClick = {},
            modifier = Modifier.align(
              if (pawn.position == -1) {
                if (pawn.id == 3) Alignment.BottomEnd else Alignment.CenterEnd
              } else Alignment.Center
            )
          )
        }
      }
    }

    // 3D Animated Dice Roller (Bottom)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .rotate(rollRotation)
          .size(62.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .border(2.dp, CandyLemon, RoundedCornerShape(16.dp))
          .clickable(enabled = isPlayerTurn && !hasRolled && !isRolling && winner == null) { rollDice() }
          .testTag("ludo_dice_roller"),
        contentAlignment = Alignment.Center
      ) {
        DiceDots(value = diceValue)
      }
    }
  }
}

@Composable
private fun PawnPiece(
  pawn: LudoPawn,
  isClickable: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val pawnColor = if (pawn.isPlayer) CandyWatermelon else CandyCyan

  Box(
    modifier = modifier
      .padding(14.dp)
      .size(36.dp)
      .clip(CircleShape)
      .background(pawnColor)
      .border(2.dp, Color.White, CircleShape)
      .clickable(enabled = isClickable, onClick = onClick)
      .testTag("ludo_pawn_${pawn.id}"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = if (pawn.isPlayer) "P${pawn.id}" else "A${pawn.id - 2}",
      color = Color.White,
      fontWeight = FontWeight.Black,
      fontSize = 11.sp
    )
  }
}

@Composable
private fun DiceDots(value: Int) {
  Canvas(modifier = Modifier.size(46.dp)) {
    val dotColor = Color(0xFF1E1638)
    val r = 3.5f
    val w = size.width
    val h = size.height

    fun dot(xRatio: Float, yRatio: Float) {
      drawCircle(dotColor, r, Offset(w * xRatio, h * yRatio))
    }

    when (value) {
      1 -> dot(0.5f, 0.5f)
      2 -> { dot(0.25f, 0.25f); dot(0.75f, 0.75f) }
      3 -> { dot(0.25f, 0.25f); dot(0.5f, 0.5f); dot(0.75f, 0.75f) }
      4 -> { dot(0.25f, 0.25f); dot(0.75f, 0.25f); dot(0.25f, 0.75f); dot(0.75f, 0.75f) }
      5 -> { dot(0.25f, 0.25f); dot(0.75f, 0.25f); dot(0.5f, 0.5f); dot(0.25f, 0.75f); dot(0.75f, 0.75f) }
      6 -> {
        dot(0.25f, 0.2f); dot(0.75f, 0.2f)
        dot(0.25f, 0.5f); dot(0.75f, 0.5f)
        dot(0.25f, 0.8f); dot(0.75f, 0.8f)
      }
    }
  }
}
