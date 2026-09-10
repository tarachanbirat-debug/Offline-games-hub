package com.example.games.dotbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlinx.coroutines.launch

@Composable
fun DotBoxScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  highScore: Int,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  val gridSize = 4 // 4x4 dots = 3x3 = 9 boxes
  val horizontalLines = remember { mutableStateMapOf<Pair<Int, Int>, Int>() } // (row, col) -> 1 (Player) or 2 (AI)
  val verticalLines = remember { mutableStateMapOf<Pair<Int, Int>, Int>() }   // (row, col) -> 1 or 2
  val boxes = remember { mutableStateMapOf<Pair<Int, Int>, Int>() }          // (row, col) -> 1 or 2

  var isPlayerTurn by remember { mutableStateOf(true) }
  var playerScore by remember { mutableIntStateOf(0) }
  var aiScore by remember { mutableIntStateOf(0) }
  var isGameOver by remember { mutableStateOf(false) }
  var isWon by remember { mutableStateOf(false) }
  var bestScore by remember { mutableIntStateOf(highScore) }

  val scope = rememberCoroutineScope()

  fun restartGame() {
    horizontalLines.clear()
    verticalLines.clear()
    boxes.clear()
    playerScore = 0
    aiScore = 0
    isPlayerTurn = true
    isGameOver = false
    isWon = false
    soundEngine.playPop()
  }

  fun checkNewCompletedBoxes(playerNumber: Int): Boolean {
    var completedAny = false
    for (r in 0 until (gridSize - 1)) {
      for (c in 0 until (gridSize - 1)) {
        if (!boxes.containsKey(r to c)) {
          val top = horizontalLines.containsKey(r to c)
          val bottom = horizontalLines.containsKey((r + 1) to c)
          val left = verticalLines.containsKey(r to c)
          val right = verticalLines.containsKey(r to (c + 1))

          if (top && bottom && left && right) {
            boxes[r to c] = playerNumber
            completedAny = true
            if (playerNumber == 1) {
              playerScore++
            } else {
              aiScore++
            }
          }
        }
      }
    }

    if (boxes.size == 9) {
      isGameOver = true
      if (playerScore > aiScore) {
        isWon = true
        val finalScore = playerScore * 100
        if (finalScore > bestScore) {
          bestScore = finalScore
          onScoreUpdated(bestScore)
        }
        soundEngine.playVictory()
        hapticEngine.vibrateSuccess()
        particleSystem.spawnVictoryConfetti(500f, 400f, 90)
      } else {
        soundEngine.playGameOver()
        hapticEngine.vibrateCrash()
      }
    }

    return completedAny
  }

  fun aiMove() {
    if (isGameOver) return
    scope.launch {
      delay(450)

      // 1. Look for any line that completes a box
      var chosenH: Pair<Int, Int>? = null
      var chosenV: Pair<Int, Int>? = null

      for (r in 0 until (gridSize - 1)) {
        for (c in 0 until (gridSize - 1)) {
          if (!boxes.containsKey(r to c)) {
            val top = horizontalLines.containsKey(r to c)
            val bottom = horizontalLines.containsKey((r + 1) to c)
            val left = verticalLines.containsKey(r to c)
            val right = verticalLines.containsKey(r to (c + 1))
            val count = (if (top) 1 else 0) + (if (bottom) 1 else 0) + (if (left) 1 else 0) + (if (right) 1 else 0)

            if (count == 3) {
              if (!top) chosenH = r to c
              else if (!bottom) chosenH = (r + 1) to c
              else if (!left) chosenV = r to c
              else if (!right) chosenV = r to (c + 1)
              break
            }
          }
        }
        if (chosenH != null || chosenV != null) break
      }

      // 2. Otherwise pick any random unselected line
      if (chosenH == null && chosenV == null) {
        val availableH = mutableListOf<Pair<Int, Int>>()
        for (r in 0..3) {
          for (c in 0..2) {
            if (!horizontalLines.containsKey(r to c)) availableH.add(r to c)
          }
        }
        val availableV = mutableListOf<Pair<Int, Int>>()
        for (r in 0..2) {
          for (c in 0..3) {
            if (!verticalLines.containsKey(r to c)) availableV.add(r to c)
          }
        }

        if (availableH.isNotEmpty() && (availableV.isEmpty() || kotlin.random.Random.nextBoolean())) {
          chosenH = availableH.random()
        } else if (availableV.isNotEmpty()) {
          chosenV = availableV.random()
        }
      }

      var gotExtraTurn = false
      if (chosenH != null) {
        horizontalLines[chosenH] = 2
        gotExtraTurn = checkNewCompletedBoxes(2)
      } else if (chosenV != null) {
        verticalLines[chosenV] = 2
        gotExtraTurn = checkNewCompletedBoxes(2)
      }

      soundEngine.playSnap()
      hapticEngine.vibrateMove()

      if (gotExtraTurn && !isGameOver) {
        aiMove()
      } else {
        isPlayerTurn = true
      }
    }
  }

  fun onCanvasTapped(tapX: Float, tapY: Float, boardSize: Float, offsetTop: Float, offsetLeft: Float) {
    if (!isPlayerTurn || isGameOver) return

    val relX = tapX - offsetLeft
    val relY = tapY - offsetTop
    if (relX < 0 || relY < 0 || relX > boardSize || relY > boardSize) return

    val cellSpacing = boardSize / (gridSize - 1)
    val threshold = cellSpacing * 0.38f

    // Check horizontal line clicks
    for (r in 0..3) {
      val lineY = r * cellSpacing
      if (kotlin.math.abs(relY - lineY) < threshold) {
        val c = (relX / cellSpacing).toInt().coerceIn(0, 2)
        val lineX1 = c * cellSpacing
        val lineX2 = (c + 1) * cellSpacing
        if (relX in lineX1..lineX2 && !horizontalLines.containsKey(r to c)) {
          horizontalLines[r to c] = 1
          soundEngine.playSnap()
          hapticEngine.vibrateTap()
          val gotBox = checkNewCompletedBoxes(1)
          if (gotBox) {
            soundEngine.playScore()
            hapticEngine.vibrateSuccess()
          } else {
            isPlayerTurn = false
            aiMove()
          }
          return
        }
      }
    }

    // Check vertical line clicks
    for (c in 0..3) {
      val lineX = c * cellSpacing
      if (kotlin.math.abs(relX - lineX) < threshold) {
        val r = (relY / cellSpacing).toInt().coerceIn(0, 2)
        val lineY1 = r * cellSpacing
        val lineY2 = (r + 1) * cellSpacing
        if (relY in lineY1..lineY2 && !verticalLines.containsKey(r to c)) {
          verticalLines[r to c] = 1
          soundEngine.playSnap()
          hapticEngine.vibrateTap()
          val gotBox = checkNewCompletedBoxes(1)
          if (gotBox) {
            soundEngine.playScore()
            hapticEngine.vibrateSuccess()
          } else {
            isPlayerTurn = false
            aiMove()
          }
          return
        }
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "Dot & Box",
      score = playerScore * 100,
      bestScore = bestScore,
      difficulty = difficulty,
      soundEnabled = !soundEngine.isMuted,
      hapticsEnabled = !hapticEngine.isMuted,
      onBack = onBack,
      onReset = { restartGame() },
      onToggleSound = { soundEngine.isMuted = !soundEngine.isMuted },
      onToggleHaptics = { hapticEngine.isMuted = !hapticEngine.isMuted }
    )

    // Score Board (Player vs AI)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(horizontalAlignment = Alignment.Start) {
        Text("YOU (BLUE)", color = CandyCyan, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Text("$playerScore Boxes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }

      Text(
        text = if (isGameOver) (if (isWon) "🏆 YOU WON!" else "AI WON!") else (if (isPlayerTurn) "YOUR TURN" else "AI THINKING..."),
        color = if (isWon) CandyMint else if (isPlayerTurn) CandyLemon else CandyWatermelon,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp
      )

      Column(horizontalAlignment = Alignment.End) {
        Text("AI (RED)", color = CandyWatermelon, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Text("$aiScore Boxes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }

    // Canvas Board
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      Canvas(
        modifier = Modifier
          .size(320.dp)
          .pointerInput(isPlayerTurn, isGameOver) {
            detectTapGestures { offset ->
              onCanvasTapped(offset.x, offset.y, 280f, 20f, 20f)
            }
          }
      ) {
        val boardSize = 280f
        val offsetLeft = 20f
        val offsetTop = 20f
        val cellSpacing = boardSize / 3f

        // Draw Completed Boxes
        for (r in 0..2) {
          for (c in 0..2) {
            val owner = boxes[r to c]
            if (owner != null) {
              val boxColor = if (owner == 1) CandyCyan.copy(alpha = 0.35f) else CandyWatermelon.copy(alpha = 0.35f)
              drawRoundRect(
                color = boxColor,
                topLeft = Offset(offsetLeft + c * cellSpacing + 4f, offsetTop + r * cellSpacing + 4f),
                size = Size(cellSpacing - 8f, cellSpacing - 8f),
                cornerRadius = CornerRadius(10f, 10f)
              )
            }
          }
        }

        // Draw Horizontal Lines
        for (r in 0..3) {
          for (c in 0..2) {
            val owner = horizontalLines[r to c]
            val lineColor = if (owner == 1) CandyCyan else if (owner == 2) CandyWatermelon else VaultBorder
            val strokeWidth = if (owner != null) 6f else 2.5f

            drawLine(
              color = lineColor,
              start = Offset(offsetLeft + c * cellSpacing, offsetTop + r * cellSpacing),
              end = Offset(offsetLeft + (c + 1) * cellSpacing, offsetTop + r * cellSpacing),
              strokeWidth = strokeWidth
            )
          }
        }

        // Draw Vertical Lines
        for (r in 0..2) {
          for (c in 0..3) {
            val owner = verticalLines[r to c]
            val lineColor = if (owner == 1) CandyCyan else if (owner == 2) CandyWatermelon else VaultBorder
            val strokeWidth = if (owner != null) 6f else 2.5f

            drawLine(
              color = lineColor,
              start = Offset(offsetLeft + c * cellSpacing, offsetTop + r * cellSpacing),
              end = Offset(offsetLeft + c * cellSpacing, offsetTop + (r + 1) * cellSpacing),
              strokeWidth = strokeWidth
            )
          }
        }

        // Draw Dots on Grid
        for (r in 0..3) {
          for (c in 0..3) {
            drawCircle(
              color = Color.White,
              radius = 5.5f,
              center = Offset(offsetLeft + c * cellSpacing, offsetTop + r * cellSpacing)
            )
          }
        }
      }
    }

    Text(
      text = "TAP BETWEEN DOTS TO DRAW LINES & CLOSE BOXES",
      color = VaultTextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 16.dp)
    )
  }
}
