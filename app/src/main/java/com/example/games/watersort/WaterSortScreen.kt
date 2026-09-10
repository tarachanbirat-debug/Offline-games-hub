package com.example.games.watersort

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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

data class LiquidTube(
  val layers: MutableList<Color> = mutableListOf(),
  val capacity: Int = 4
) {
  fun isFull(): Boolean = layers.size >= capacity
  fun isEmpty(): Boolean = layers.isEmpty()
  fun topColor(): Color? = layers.lastOrNull()
  fun isSolved(): Boolean = isEmpty() || (isFull() && layers.all { it == layers.first() })
}

@Composable
fun WaterSortScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  particleSystem: ParticleSystem,
  difficulty: String,
  onBack: () -> Unit,
  onScoreUpdated: (Int) -> Unit
) {
  var currentLevel by remember { mutableIntStateOf(1) }
  var tubes by remember { mutableStateOf<List<LiquidTube>>(emptyList()) }
  var selectedTubeIndex by remember { mutableStateOf<Int?>(null) }
  var isWon by remember { mutableStateOf(false) }
  var movesCount by remember { mutableIntStateOf(0) }
  var historyStack by remember { mutableStateOf<List<List<List<Color>>>>(emptyList()) }
  val scope = rememberCoroutineScope()

  val availableColors = listOf(
    CandyWatermelon,
    CandyMint,
    CandyCyan,
    CandyLemon,
    CandyGrape,
    CandyTangerine
  )

  fun initLevel(level: Int, diff: String) {
    val colorCount = when (diff) {
      "EASY" -> 3
      "MEDIUM" -> 4
      "HARD" -> 5
      else -> 6 // EXPERT
    }
    val colors = availableColors.take(colorCount)
    val totalLayers = colorCount * 4
    val layerPool = mutableListOf<Color>()
    colors.forEach { c -> repeat(4) { layerPool.add(c) } }
    layerPool.shuffle()

    val totalTubes = colorCount + 2 // 2 buffer empty tubes
    val newTubes = List(totalTubes) { LiquidTube() }

    var poolIdx = 0
    for (t in 0 until colorCount) {
      repeat(4) {
        newTubes[t].layers.add(layerPool[poolIdx++])
      }
    }

    tubes = newTubes
    selectedTubeIndex = null
    isWon = false
    movesCount = 0
    historyStack = emptyList()
    soundEngine.playPop()
  }

  LaunchedEffect(currentLevel, difficulty) {
    initLevel(currentLevel, difficulty)
  }

  fun checkVictory(): Boolean {
    return tubes.all { it.isSolved() }
  }

  fun onTubeTapped(index: Int) {
    if (isWon) return

    val selected = selectedTubeIndex
    if (selected == null) {
      // Selecting source tube
      if (!tubes[index].isEmpty()) {
        selectedTubeIndex = index
        soundEngine.playTap()
        hapticEngine.vibrateTap()
      }
    } else if (selected == index) {
      // Deselect
      selectedTubeIndex = null
      soundEngine.playTap()
    } else {
      // Pouring from selected into index
      val source = tubes[selected]
      val target = tubes[index]

      val canPour = !target.isFull() && (target.isEmpty() || target.topColor() == source.topColor())

      if (canPour) {
        // Save history for undo
        val snapshot = tubes.map { it.layers.toList() }
        historyStack = historyStack + listOf(snapshot)

        val colorToPour = source.topColor()!!
        var pouredLayers = 0

        while (!source.isEmpty() && source.topColor() == colorToPour && !target.isFull()) {
          source.layers.removeAt(source.layers.lastIndex)
          target.layers.add(colorToPour)
          pouredLayers++
        }

        movesCount++
        selectedTubeIndex = null
        soundEngine.playPour()
        hapticEngine.vibrateMove()

        // Check Win
        if (checkVictory()) {
          isWon = true
          soundEngine.playVictory()
          hapticEngine.vibrateSuccess()
          particleSystem.spawnVictoryConfetti(500f, 400f, 100)
          onScoreUpdated(currentLevel * 50)
        }
      } else {
        // Invalid pour feedback
        soundEngine.playError()
        hapticEngine.vibrateCrash()
        selectedTubeIndex = null
      }
    }
  }

  fun undo() {
    if (historyStack.isNotEmpty()) {
      val lastState = historyStack.last()
      historyStack = historyStack.dropLast(1)
      val restoredTubes = lastState.map { layers ->
        LiquidTube().apply { this.layers.addAll(layers) }
      }
      tubes = restoredTubes
      selectedTubeIndex = null
      soundEngine.playPop()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = "Water Sort Puzzle",
      score = currentLevel,
      bestScore = currentLevel,
      difficulty = difficulty,
      soundEnabled = !soundEngine.isMuted,
      hapticsEnabled = !hapticEngine.isMuted,
      onBack = onBack,
      onReset = { initLevel(currentLevel, difficulty) },
      onToggleSound = { soundEngine.isMuted = !soundEngine.isMuted },
      onToggleHaptics = { hapticEngine.isMuted = !hapticEngine.isMuted }
    )

    // Level & Moves Info
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "LEVEL $currentLevel",
        color = CandyCyan,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp
      )

      Text(
        text = "MOVES: $movesCount",
        color = VaultTextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )

      // Undo Button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(VaultSurfaceHighlight)
          .clickable(enabled = historyStack.isNotEmpty()) { undo() }
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = "UNDO",
          color = if (historyStack.isNotEmpty()) Color.White else VaultTextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    if (isWon) {
      Text(
        text = "🎉 PUZZLE SOLVED! ALL COLORS SORTED!",
        color = CandyMint,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(vertical = 4.dp)
      )
    }

    // Glass Tubes Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      // Split tubes into 2 rows for responsive mobile layout
      val half = (tubes.size + 1) / 2
      val row1 = tubes.take(half)
      val row2 = tubes.drop(half)

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(26.dp)
      ) {
        // Row 1
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          row1.forEachIndexed { i, tube ->
            val isSelected = (selectedTubeIndex == i)
            TubeView(tube = tube, isSelected = isSelected, onClick = { onTubeTapped(i) })
          }
        }
        // Row 2
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          row2.forEachIndexed { i, tube ->
            val actualIdx = half + i
            val isSelected = (selectedTubeIndex == actualIdx)
            TubeView(tube = tube, isSelected = isSelected, onClick = { onTubeTapped(actualIdx) })
          }
        }
      }
    }

    // Next Level Action Button
    if (isWon) {
      Button(
        onClick = { currentLevel++ },
        colors = ButtonDefaults.buttonColors(containerColor = CandyMint),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(bottom = 24.dp)
          .testTag("water_sort_next_level")
      ) {
        Text("NEXT LEVEL", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
      }
    }
  }
}

@Composable
private fun TubeView(
  tube: LiquidTube,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val offsetY by animateFloatAsState(
    targetValue = if (isSelected) -18f else 0f,
    animationSpec = spring(dampingRatio = 0.6f),
    label = "tube_lift"
  )

  Box(
    modifier = Modifier
      .offset(y = offsetY.dp)
      .width(46.dp)
      .height(138.dp)
      .clickable(onClick = onClick)
      .testTag("tube_item")
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Selection Glow
      if (isSelected) {
        drawRoundRect(
          color = CandyCyan.copy(alpha = 0.35f),
          topLeft = Offset(-4f, -4f),
          size = Size(w + 8f, h + 8f),
          cornerRadius = CornerRadius(20f, 20f)
        )
      }

      // Draw Colored Liquid Layers from bottom up
      val layerHeight = (h - 14f) / 4f
      for (l in tube.layers.indices) {
        val color = tube.layers[l]
        val ly = h - (l + 1) * layerHeight - 3f

        drawRoundRect(
          brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.9f), color)),
          topLeft = Offset(3f, ly),
          size = Size(w - 6f, layerHeight),
          cornerRadius = CornerRadius(if (l == 0) 14f else 4f, if (l == 0) 14f else 4f)
        )
        // Surface highlight
        drawLine(
          color = Color.White.copy(alpha = 0.45f),
          start = Offset(6f, ly + 2f),
          end = Offset(w - 6f, ly + 2f),
          strokeWidth = 2f
        )
      }

      // Outer Glass Tube
      drawRoundRect(
        color = Color.White.copy(alpha = if (isSelected) 0.95f else 0.75f),
        topLeft = Offset(1.5f, 6f),
        size = Size(w - 3f, h - 8f),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = if (isSelected) 3.5f else 2.5f)
      )

      // Glass Rim
      drawRoundRect(
        color = Color.White,
        topLeft = Offset(0f, 0f),
        size = Size(w, 8f),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(width = 2.5f)
      )

      // Glossy glass reflection streak
      drawLine(
        color = Color.White.copy(alpha = 0.35f),
        start = Offset(6f, 12f),
        end = Offset(6f, h - 18f),
        strokeWidth = 2f
      )
    }
  }
}
