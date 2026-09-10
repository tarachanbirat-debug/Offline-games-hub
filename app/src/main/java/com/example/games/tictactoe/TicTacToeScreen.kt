package com.example.games.tictactoe

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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

@Composable
fun TicTacToeScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  var board by remember { mutableStateOf(List(9) { "" }) }
  var isPlayerTurn by remember { mutableStateOf(true) }
  var winner by remember { mutableStateOf<String?>(null) } // "X", "O", "DRAW"
  var winningLine by remember { mutableStateOf<List<Int>?>(null) }
  var playerWins by remember { mutableIntStateOf(0) }
  var aiWins by remember { mutableIntStateOf(0) }
  var draws by remember { mutableIntStateOf(0) }

  var currentDifficulty by remember { mutableStateOf(difficulty) }
  val scope = rememberCoroutineScope()

  fun checkWinner(b: List<String>): Pair<String?, List<Int>?> {
    val lines = listOf(
      listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
      listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
      listOf(0, 4, 8), listOf(2, 4, 6)                   // Diagonals
    )
    for (line in lines) {
      val (x, y, z) = line
      if (b[x].isNotEmpty() && b[x] == b[y] && b[y] == b[z]) {
        return Pair(b[x], line)
      }
    }
    if (b.all { it.isNotEmpty() }) {
      return Pair("DRAW", null)
    }
    return Pair(null, null)
  }

  // Real Minimax Algorithm with Alpha-Beta Pruning
  fun minimax(b: MutableList<String>, isMax: Boolean, depth: Int, alpha: Int, beta: Int): Int {
    val (res, _) = checkWinner(b)
    if (res == "O") return 10 - depth // AI is O
    if (res == "X") return depth - 10 // Human is X
    if (res == "DRAW") return 0

    var currentAlpha = alpha
    var currentBeta = beta

    if (isMax) {
      var best = -1000
      for (i in 0 until 9) {
        if (b[i].isEmpty()) {
          b[i] = "O"
          best = maxOf(best, minimax(b, false, depth + 1, currentAlpha, currentBeta))
          b[i] = ""
          currentAlpha = maxOf(currentAlpha, best)
          if (currentBeta <= currentAlpha) break
        }
      }
      return best
    } else {
      var best = 1000
      for (i in 0 until 9) {
        if (b[i].isEmpty()) {
          b[i] = "X"
          best = minOf(best, minimax(b, true, depth + 1, currentAlpha, currentBeta))
          b[i] = ""
          currentBeta = minOf(currentBeta, best)
          if (currentBeta <= currentAlpha) break
        }
      }
      return best
    }
  }

  fun getBestMove(b: List<String>, diff: String): Int {
    val available = b.indices.filter { b[it].isEmpty() }
    if (available.isEmpty()) return -1

    return when (diff) {
      "EASY" -> {
        // Mostly random move
        available.random()
      }
      "MEDIUM" -> {
        // 50% chance optimal, 50% chance random
        if (Random.nextFloat() > 0.5f) {
          available.random()
        } else {
          // Check if can win or block, else random
          for (pos in available) {
            val copy = b.toMutableList()
            copy[pos] = "O"
            if (checkWinner(copy).first == "O") return pos
          }
          for (pos in available) {
            val copy = b.toMutableList()
            copy[pos] = "X"
            if (checkWinner(copy).first == "X") return pos
          }
          available.random()
        }
      }
      "HARD" -> {
        // Minimax with slight depth imperfection
        if (Random.nextFloat() < 0.25f) available.random()
        else {
          var bestVal = -1000
          var bestMove = available.first()
          val copy = b.toMutableList()
          for (pos in available) {
            copy[pos] = "O"
            val moveVal = minimax(copy, false, 0, -1000, 1000)
            copy[pos] = ""
            if (moveVal > bestVal) {
              bestVal = moveVal
              bestMove = pos
            }
          }
          bestMove
        }
      }
      else -> { // EXPERT - Unbeatable Minimax
        var bestVal = -1000
        var bestMove = available.first()
        val copy = b.toMutableList()
        for (pos in available) {
          copy[pos] = "O"
          val moveVal = minimax(copy, false, 0, -1000, 1000)
          copy[pos] = ""
          if (moveVal > bestVal) {
            bestVal = moveVal
            bestMove = pos
          }
        }
        bestMove
      }
    }
  }

  fun restartGame() {
    board = List(9) { "" }
    winner = null
    winningLine = null
    isPlayerTurn = true
    soundEngine.playPop()
  }

  fun onCellClicked(index: Int) {
    if (board[index].isNotEmpty() || !isPlayerTurn || winner != null) return

    val newBoard = board.toMutableList()
    newBoard[index] = "X"
    board = newBoard
    soundEngine.playSnap()
    hapticEngine.vibrateTap()

    val (winResult, line) = checkWinner(newBoard)
    if (winResult != null) {
      winner = winResult
      winningLine = line
      if (winResult == "X") {
        playerWins++
        onScoreUpdated(playerWins)
        soundEngine.playVictory()
        hapticEngine.vibrateSuccess()
        particleSystem.spawnVictoryConfetti(500f, 500f, 90)
      } else if (winResult == "DRAW") {
        draws++
        soundEngine.playPop()
      }
      return
    }

    // AI Turn
    isPlayerTurn = false
    scope.launch {
      delay(Random.nextLong(280, 500))
      val aiMove = getBestMove(board, currentDifficulty)
      if (aiMove != -1 && winner == null) {
        val aiBoard = board.toMutableList()
        aiBoard[aiMove] = "O"
        board = aiBoard
        soundEngine.playPop()
        hapticEngine.vibrateMove()

        val (aiWinResult, aiLine) = checkWinner(aiBoard)
        if (aiWinResult != null) {
          winner = aiWinResult
          winningLine = aiLine
          if (aiWinResult == "O") {
            aiWins++
            soundEngine.playGameOver()
            hapticEngine.vibrateCrash()
          } else if (aiWinResult == "DRAW") {
            draws++
            soundEngine.playPop()
          }
        } else {
          isPlayerTurn = true
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
      title = "Tic-Tac-Toe Minimax",
      score = playerWins,
      bestScore = playerWins,
      difficulty = currentDifficulty,
      soundEnabled = !soundEngine.isMuted,
      hapticsEnabled = !hapticEngine.isMuted,
      onBack = onBack,
      onReset = { restartGame() },
      onToggleSound = { soundEngine.isMuted = !soundEngine.isMuted },
      onToggleHaptics = { hapticEngine.isMuted = !hapticEngine.isMuted },
      onDifficultyClick = {
        val next = when (currentDifficulty) {
          "EASY" -> "MEDIUM"
          "MEDIUM" -> "HARD"
          "HARD" -> "EXPERT"
          else -> "EASY"
        }
        currentDifficulty = next
        restartGame()
      }
    )

    // Score Board (Human vs AI vs Draw)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      ScoreBox(label = "PLAYER (X)", score = playerWins, color = CandyWatermelon)
      ScoreBox(label = "DRAWS", score = draws, color = VaultTextSecondary)
      ScoreBox(label = "AI BOT (O)", score = aiWins, color = CandyCyan)
    }

    // Turn Indicator
    Text(
      text = when {
        winner == "X" -> "🎉 YOU WON!"
        winner == "O" -> "🤖 AI WINS!"
        winner == "DRAW" -> "🤝 IT'S A DRAW!"
        isPlayerTurn -> "👉 Your Turn (X)"
        else -> "🤔 AI Thinking..."
      },
      color = when (winner) {
        "X" -> CandyMint
        "O" -> CandyWatermelon
        "DRAW" -> CandyLemon
        else -> Color.White
      },
      fontWeight = FontWeight.Black,
      fontSize = 18.sp,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(vertical = 12.dp)
    )

    // 3x3 Board with animated pieces
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
          .padding(12.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (row in 0 until 3) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              for (col in 0 until 3) {
                val index = row * 3 + col
                val piece = board[index]
                val isWinningCell = winningLine?.contains(index) == true

                val cellScale by animateFloatAsState(
                  targetValue = if (piece.isNotEmpty()) 1f else 0.92f,
                  animationSpec = tween(180),
                  label = "cell_scale"
                )

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .scale(cellScale)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                      if (isWinningCell) CandyLemon.copy(alpha = 0.25f)
                      else VaultCardDark
                    )
                    .border(
                      1.5.dp,
                      if (isWinningCell) CandyLemon else VaultBorder,
                      RoundedCornerShape(18.dp)
                    )
                    .clickable { onCellClicked(index) }
                    .testTag("tictactoe_cell_$index"),
                  contentAlignment = Alignment.Center
                ) {
                  if (piece == "X") {
                    Canvas(modifier = Modifier.size(48.dp)) {
                      val arm = size.width * 0.4f
                      val center = Offset(size.width / 2, size.height / 2)
                      drawLine(
                        color = CandyWatermelon,
                        start = Offset(center.x - arm, center.y - arm),
                        end = Offset(center.x + arm, center.y + arm),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                      )
                      drawLine(
                        color = CandyWatermelon,
                        start = Offset(center.x + arm, center.y - arm),
                        end = Offset(center.x - arm, center.y + arm),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                      )
                    }
                  } else if (piece == "O") {
                    Canvas(modifier = Modifier.size(48.dp)) {
                      drawCircle(
                        color = CandyCyan,
                        radius = size.width * 0.38f,
                        style = Stroke(width = 8f)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Play Again Button when finished
    if (winner != null) {
      Button(
        onClick = { restartGame() },
        colors = ButtonDefaults.buttonColors(containerColor = CandyGrape),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(bottom = 24.dp)
          .testTag("tictactoe_play_again")
      ) {
        Text("PLAY AGAIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }
  }
}

@Composable
private fun ScoreBox(label: String, score: Int, color: Color) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(14.dp))
      .background(VaultCardDark)
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    Text(label, color = VaultTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    Text("$score", color = color, fontSize = 16.sp, fontWeight = FontWeight.Black)
  }
}
