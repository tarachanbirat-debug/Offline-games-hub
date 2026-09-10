package com.example.games.memorymatch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(
  val id: Int,
  val symbol: String,
  val color: Color,
  var isFlipped: Boolean = false,
  var isMatched: Boolean = false
)

@Composable
fun MemoryMatchScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  highScore: Int,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  val symbols = listOf(
    Pair("🍭", CandyWatermelon),
    Pair("🍬", CandyCyan),
    Pair("🍩", CandyTangerine),
    Pair("🧁", CandyGrape),
    Pair("🍓", CandyCoral),
    Pair("🍒", CandyLemon),
    Pair("🍍", CandyMint),
    Pair("🍉", CandySkyBlue)
  )

  var cards by remember { mutableStateOf<List<MemoryCard>>(emptyList()) }
  var flippedCards by remember { mutableStateOf<List<Int>>(emptyList()) }
  var score by remember { mutableIntStateOf(0) }
  var bestScore by remember { mutableIntStateOf(highScore) }
  var streak by remember { mutableIntStateOf(0) }
  var isWon by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  fun initGame() {
    val pool = (symbols + symbols).shuffled()
    cards = pool.mapIndexed { idx, pair ->
      MemoryCard(id = idx, symbol = pair.first, color = pair.second)
    }
    flippedCards = emptyList()
    score = 0
    streak = 0
    isWon = false
    soundEngine.playPop()
  }

  LaunchedEffect(Unit) {
    initGame()
  }

  fun onCardClicked(index: Int) {
    if (flippedCards.size >= 2 || cards[index].isFlipped || cards[index].isMatched || isWon) return

    val updated = cards.toMutableList()
    updated[index] = updated[index].copy(isFlipped = true)
    cards = updated
    soundEngine.playTap()
    hapticEngine.vibrateTap()

    val newFlipped = flippedCards + index
    flippedCards = newFlipped

    if (newFlipped.size == 2) {
      val firstIdx = newFlipped[0]
      val secondIdx = newFlipped[1]
      val card1 = cards[firstIdx]
      val card2 = cards[secondIdx]

      if (card1.symbol == card2.symbol) {
        // Matched!
        scope.launch {
          delay(200)
          val matchedList = cards.toMutableList()
          matchedList[firstIdx] = matchedList[firstIdx].copy(isMatched = true)
          matchedList[secondIdx] = matchedList[secondIdx].copy(isMatched = true)
          cards = matchedList
          flippedCards = emptyList()

          streak++
          val points = 50 * streak
          score += points
          if (score > bestScore) {
            bestScore = score
            onScoreUpdated(score)
          }

          soundEngine.playScore()
          hapticEngine.vibrateSuccess()
          particleSystem.spawnScoreSparkles(500f, 400f, 20)

          if (cards.all { it.isMatched }) {
            isWon = true
            soundEngine.playVictory()
            particleSystem.spawnVictoryConfetti(500f, 400f, 100)
          }
        }
      } else {
        // Mismatch
        scope.launch {
          delay(650)
          val resetList = cards.toMutableList()
          resetList[firstIdx] = resetList[firstIdx].copy(isFlipped = false)
          resetList[secondIdx] = resetList[secondIdx].copy(isFlipped = false)
          cards = resetList
          flippedCards = emptyList()
          streak = 0
          soundEngine.playError()
          hapticEngine.vibrateCrash()
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
      title = "Candy Memory Match",
      score = score,
      bestScore = bestScore,
      difficulty = difficulty,
      soundEnabled = !soundEngine.isMuted,
      hapticsEnabled = !hapticEngine.isMuted,
      onBack = onBack,
      onReset = { initGame() },
      onToggleSound = { soundEngine.isMuted = !soundEngine.isMuted },
      onToggleHaptics = { hapticEngine.isMuted = !hapticEngine.isMuted }
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (streak > 1) "🔥 STREAK x$streak" else "PAIR MATCHING",
        color = if (streak > 1) CandyLemon else CandyMint,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp
      )

      if (isWon) {
        Text("🎉 ALL PAIRS MATCHED!", color = CandyMint, fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
    }

    // 4x4 Cards Grid
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(330.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(VaultSurfaceElevated)
          .border(2.dp, VaultBorderGlow, RoundedCornerShape(26.dp))
          .padding(10.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (row in 0 until 4) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              for (col in 0 until 4) {
                val index = row * 4 + col
                if (index < cards.size) {
                  val card = cards[index]
                  val isRevealed = card.isFlipped || card.isMatched

                  val cardScale by animateFloatAsState(
                    targetValue = if (card.isMatched) 0.9f else if (card.isFlipped) 1.02f else 1f,
                    animationSpec = tween(180),
                    label = "memory_card_scale"
                  )

                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .fillMaxHeight()
                      .scale(cardScale)
                      .clip(RoundedCornerShape(14.dp))
                      .background(if (isRevealed) card.color else VaultCardDark)
                      .border(
                        1.5.dp,
                        if (isRevealed) Color.White.copy(alpha = 0.8f) else VaultBorder,
                        RoundedCornerShape(14.dp)
                      )
                      .clickable(enabled = !isRevealed) { onCardClicked(index) }
                      .testTag("memory_card_$index"),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isRevealed) {
                      Text(text = card.symbol, fontSize = 28.sp)
                    } else {
                      Text(text = "★", color = VaultTextMuted, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    Text(
      text = "TAP CARDS TO FIND MATCHING CANDY PAIRS",
      color = VaultTextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 16.dp)
    )
  }
}
