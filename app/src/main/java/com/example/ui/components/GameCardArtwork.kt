package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.*

/**
 * Procedural 2.5D visual artwork for each game.
 * Occupies 70-85% of the game card area, rendering rich, recognizable game elements.
 */
@Composable
fun GameCardArtwork(
  gameId: String,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.fillMaxSize()) {
    when (gameId) {
      "water_sort" -> drawWaterSortArtwork(this)
      "game_2048" -> draw2048Artwork(this)
      "snake" -> drawSnakeArtwork(this)
      "highway_racer", "threejs_galaxy_runner" -> drawHighwayRacerArtwork(this)
      "mini_ludo" -> drawLudoArtwork(this)
      "tic_tac_toe" -> drawTicTacToeArtwork(this)
      "brick_breaker" -> drawBrickBreakerArtwork(this)
      "memory_match" -> drawMemoryMatchArtwork(this)
      else -> drawGenericArcadeArtwork(this)
    }
  }
}

// 1. Water Sort: Real glass tubes + layered liquid
private fun drawWaterSortArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Background subtle glow
  scope.drawCircle(
    brush = Brush.radialGradient(listOf(CandyWatermelon.copy(alpha = 0.25f), Color.Transparent)),
    radius = w * 0.45f,
    center = Offset(w * 0.5f, h * 0.5f)
  )

  val tubeWidth = w * 0.18f
  val tubeHeight = h * 0.65f
  val tubeY = h * 0.2f

  // 3 Test Tubes
  val tubeXCoords = listOf(w * 0.16f, w * 0.41f, w * 0.66f)
  val liquidColors = listOf(
    listOf(CandyGrape, CandyCyan, CandyLemon, CandyWatermelon),
    listOf(CandyWatermelon, CandyMint, CandyCyan, CandyGrape),
    listOf(CandyMint, CandyLemon, CandyWatermelon, CandyCyan)
  )

  for (t in 0 until 3) {
    val tx = tubeXCoords[t]
    // Liquid Layers (4 layers per tube)
    val layerHeight = tubeHeight * 0.21f
    for (l in 0 until 4) {
      val ly = tubeY + tubeHeight - (l + 1) * layerHeight - 4f
      val color = liquidColors[t][l]
      scope.drawRoundRect(
        brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.9f), color)),
        topLeft = Offset(tx + 4f, ly),
        size = Size(tubeWidth - 8f, layerHeight),
        cornerRadius = CornerRadius(if (l == 0) 12f else 4f, if (l == 0) 12f else 4f)
      )
      // Liquid top surface meniscus highlight
      scope.drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(tx + 8f, ly + 2f),
        end = Offset(tx + tubeWidth - 8f, ly + 2f),
        strokeWidth = 2f
      )
    }

    // Glass Tube Body Outline
    scope.drawRoundRect(
      color = Color.White.copy(alpha = 0.85f),
      topLeft = Offset(tx, tubeY),
      size = Size(tubeWidth, tubeHeight),
      cornerRadius = CornerRadius(16f, 16f),
      style = Stroke(width = 3.5f)
    )

    // Glass rim at top
    scope.drawRoundRect(
      color = Color.White,
      topLeft = Offset(tx - 3f, tubeY - 3f),
      size = Size(tubeWidth + 6f, 8f),
      cornerRadius = CornerRadius(4f, 4f),
      style = Stroke(width = 3f)
    )

    // Glass glossy reflection streak
    scope.drawLine(
      color = Color.White.copy(alpha = 0.4f),
      start = Offset(tx + 6f, tubeY + 12f),
      end = Offset(tx + 6f, tubeY + tubeHeight - 16f),
      strokeWidth = 2.5f
    )
  }
}

// 2. 2048: Large glossy numbered tiles
private fun draw2048Artwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Background grid board
  val boardSize = minOf(w, h) * 0.82f
  val bx = (w - boardSize) / 2
  val by = (h - boardSize) / 2

  scope.drawRoundRect(
    color = Color(0xFF2E2055),
    topLeft = Offset(bx, by),
    size = Size(boardSize, boardSize),
    cornerRadius = CornerRadius(16f, 16f)
  )

  // 4 Main glossy tiles
  val tileSize = boardSize * 0.42f
  val gap = boardSize * 0.08f

  val tiles = listOf(
    Pair(Offset(bx + gap, by + gap), CandyLemon),       // 2048 tile
    Pair(Offset(bx + gap * 2 + tileSize, by + gap), CandyTangerine), // 512 tile
    Pair(Offset(bx + gap, by + gap * 2 + tileSize), CandyWatermelon), // 1024 tile
    Pair(Offset(bx + gap * 2 + tileSize, by + gap * 2 + tileSize), CandyMint) // 256 tile
  )

  for ((pos, color) in tiles) {
    // Tile 2.5D bevel
    scope.drawRoundRect(
      color = color.copy(alpha = 0.5f),
      topLeft = Offset(pos.x, pos.y + 4f),
      size = Size(tileSize, tileSize),
      cornerRadius = CornerRadius(12f, 12f)
    )
    scope.drawRoundRect(
      brush = Brush.verticalGradient(listOf(color, color.copy(alpha = 0.85f))),
      topLeft = pos,
      size = Size(tileSize, tileSize),
      cornerRadius = CornerRadius(12f, 12f)
    )
    // Glossy highlight
    scope.drawRoundRect(
      color = Color.White.copy(alpha = 0.35f),
      topLeft = Offset(pos.x + 4f, pos.y + 3f),
      size = Size(tileSize - 8f, tileSize * 0.4f),
      cornerRadius = CornerRadius(8f, 8f)
    )
    // Center emblem / number bar
    scope.drawRoundRect(
      color = Color.White.copy(alpha = 0.9f),
      topLeft = Offset(pos.x + tileSize * 0.25f, pos.y + tileSize * 0.42f),
      size = Size(tileSize * 0.5f, tileSize * 0.16f),
      cornerRadius = CornerRadius(4f, 4f)
    )
  }
}

// 3. Snake: Actual snake on colorful grid + pulsing apple
private fun drawSnakeArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Grid background
  val boardSize = minOf(w, h) * 0.8f
  val bx = (w - boardSize) / 2
  val by = (h - boardSize) / 2

  scope.drawRoundRect(
    color = Color(0xFF162A28),
    topLeft = Offset(bx, by),
    size = Size(boardSize, boardSize),
    cornerRadius = CornerRadius(16f, 16f)
  )

  // Snake body segments (Mint gradient)
  val segSize = boardSize * 0.16f
  val segments = listOf(
    Offset(bx + segSize * 3.5f, by + segSize * 1.5f), // Head
    Offset(bx + segSize * 2.5f, by + segSize * 1.5f),
    Offset(bx + segSize * 1.5f, by + segSize * 1.5f),
    Offset(bx + segSize * 1.5f, by + segSize * 2.5f),
    Offset(bx + segSize * 1.5f, by + segSize * 3.5f),
    Offset(bx + segSize * 2.5f, by + segSize * 3.5f)  // Tail
  )

  for (i in segments.indices) {
    val seg = segments[i]
    val isHead = (i == 0)
    scope.drawCircle(
      brush = Brush.radialGradient(
        listOf(if (isHead) CandyMint else CandyMint.copy(alpha = 0.8f), Color(0xFF009966))
      ),
      radius = segSize * 0.45f,
      center = Offset(seg.x + segSize * 0.5f, seg.y + segSize * 0.5f)
    )
    if (isHead) {
      // Cute eyes on head
      scope.drawCircle(
        color = Color.White,
        radius = segSize * 0.12f,
        center = Offset(seg.x + segSize * 0.65f, seg.y + segSize * 0.35f)
      )
      scope.drawCircle(
        color = Color.Black,
        radius = segSize * 0.06f,
        center = Offset(seg.x + segSize * 0.68f, seg.y + segSize * 0.35f)
      )
    }
  }

  // Juicy Apple / Food
  val appleCenter = Offset(bx + segSize * 4.2f, by + segSize * 3.2f)
  scope.drawCircle(
    brush = Brush.radialGradient(listOf(CandyWatermelon, Color(0xFFB3003B))),
    radius = segSize * 0.45f,
    center = appleCenter
  )
  // Apple leaf
  scope.drawCircle(
    color = CandyMint,
    radius = segSize * 0.12f,
    center = Offset(appleCenter.x + segSize * 0.2f, appleCenter.y - segSize * 0.35f)
  )
}

// 4. Highway Racer: 3D perspective road + car + traffic
private fun drawHighwayRacerArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Horizon road perspective
  val roadPath = Path().apply {
    moveTo(w * 0.35f, h * 0.15f)
    lineTo(w * 0.65f, h * 0.15f)
    lineTo(w * 0.95f, h * 0.88f)
    lineTo(w * 0.05f, h * 0.88f)
    close()
  }

  scope.drawPath(
    brush = Brush.verticalGradient(listOf(Color(0xFF221A3D), Color(0xFF0D0A1C))),
    path = roadPath
  )

  // Road Dash Markings
  val dashColor = CandyLemon.copy(alpha = 0.85f)
  scope.drawLine(
    color = dashColor,
    start = Offset(w * 0.5f, h * 0.2f),
    end = Offset(w * 0.5f, h * 0.32f),
    strokeWidth = 2f
  )
  scope.drawLine(
    color = dashColor,
    start = Offset(w * 0.5f, h * 0.42f),
    end = Offset(w * 0.5f, h * 0.60f),
    strokeWidth = 4f
  )
  scope.drawLine(
    color = dashColor,
    start = Offset(w * 0.5f, h * 0.68f),
    end = Offset(w * 0.5f, h * 0.85f),
    strokeWidth = 6f
  )

  // Traffic Car (ahead)
  val enemyW = w * 0.14f
  val enemyH = h * 0.14f
  val enemyX = w * 0.38f
  val enemyY = h * 0.32f
  scope.drawRoundRect(
    color = CandyTangerine,
    topLeft = Offset(enemyX, enemyY),
    size = Size(enemyW, enemyH),
    cornerRadius = CornerRadius(6f, 6f)
  )

  // Player Sports Car (foreground 2.5D)
  val carW = w * 0.28f
  val carH = h * 0.26f
  val carX = (w - carW) / 2
  val carY = h * 0.62f

  // Car Shadow
  scope.drawOval(
    color = Color.Black.copy(alpha = 0.55f),
    topLeft = Offset(carX - 4f, carY + carH * 0.75f),
    size = Size(carW + 8f, carH * 0.35f)
  )

  // Car Body (Cyan Sports Car)
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(CandyCyan, Color(0xFF008FB3))),
    topLeft = Offset(carX, carY),
    size = Size(carW, carH),
    cornerRadius = CornerRadius(14f, 14f)
  )
  // Windshield
  scope.drawRoundRect(
    color = Color(0xFF1E1638),
    topLeft = Offset(carX + carW * 0.18f, carY + carH * 0.25f),
    size = Size(carW * 0.64f, carH * 0.35f),
    cornerRadius = CornerRadius(6f, 6f)
  )
  // Headlight flares
  scope.drawCircle(
    color = CandyLemon,
    radius = 5f,
    center = Offset(carX + carW * 0.2f, carY + 4f)
  )
  scope.drawCircle(
    color = CandyLemon,
    radius = 5f,
    center = Offset(carX + carW * 0.8f, carY + 4f)
  )
}

// 5. Mini Ludo: Authentic board + pawns + dice
private fun drawLudoArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  val boardSize = minOf(w, h) * 0.82f
  val bx = (w - boardSize) / 2
  val by = (h - boardSize) / 2

  // Board Base
  scope.drawRoundRect(
    color = Color(0xFF281C48),
    topLeft = Offset(bx, by),
    size = Size(boardSize, boardSize),
    cornerRadius = CornerRadius(16f, 16f)
  )

  // Red Home & Blue Home
  val half = boardSize * 0.42f
  scope.drawRoundRect(
    color = CandyWatermelon.copy(alpha = 0.8f),
    topLeft = Offset(bx + 8f, by + 8f),
    size = Size(half, half),
    cornerRadius = CornerRadius(10f, 10f)
  )
  scope.drawRoundRect(
    color = CandyCyan.copy(alpha = 0.8f),
    topLeft = Offset(bx + boardSize - half - 8f, by + boardSize - half - 8f),
    size = Size(half, half),
    cornerRadius = CornerRadius(10f, 10f)
  )

  // 3D Ludo Pawn (Red)
  val pawnX = bx + half * 0.5f
  val pawnY = by + half * 0.5f
  scope.drawCircle(
    color = Color.White,
    radius = 12f,
    center = Offset(pawnX, pawnY - 8f)
  )
  scope.drawCircle(
    brush = Brush.radialGradient(listOf(CandyWatermelon, Color(0xFF990033))),
    radius = 10f,
    center = Offset(pawnX, pawnY - 8f)
  )
  scope.drawRoundRect(
    color = CandyWatermelon,
    topLeft = Offset(pawnX - 8f, pawnY),
    size = Size(16f, 18f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // Center Animated Dice (3D perspective)
  val diceSize = boardSize * 0.3f
  val dx = (w - diceSize) / 2
  val dy = (h - diceSize) / 2

  scope.drawRoundRect(
    color = Color.White,
    topLeft = Offset(dx, dy),
    size = Size(diceSize, diceSize),
    cornerRadius = CornerRadius(10f, 10f)
  )
  scope.drawRoundRect(
    color = Color(0xFFE2DDF5),
    topLeft = Offset(dx, dy + diceSize * 0.85f),
    size = Size(diceSize, diceSize * 0.15f),
    cornerRadius = CornerRadius(6f, 6f)
  )
  // Dice Dots (showing 5)
  val dotR = 3.5f
  val dotColor = CandyWatermelon
  scope.drawCircle(dotColor, dotR, Offset(dx + diceSize * 0.25f, dy + diceSize * 0.25f))
  scope.drawCircle(dotColor, dotR, Offset(dx + diceSize * 0.75f, dy + diceSize * 0.25f))
  scope.drawCircle(dotColor, dotR, Offset(dx + diceSize * 0.5f, dy + diceSize * 0.5f))
  scope.drawCircle(dotColor, dotR, Offset(dx + diceSize * 0.25f, dy + diceSize * 0.75f))
  scope.drawCircle(dotColor, dotR, Offset(dx + diceSize * 0.75f, dy + diceSize * 0.75f))
}

// 6. Tic-Tac-Toe: Chunky 3D board + X/O pieces
private fun drawTicTacToeArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  val boardSize = minOf(w, h) * 0.8f
  val bx = (w - boardSize) / 2
  val by = (h - boardSize) / 2

  // Chunky Grid Lines
  val gridColor = CandyLemon.copy(alpha = 0.9f)
  val cell = boardSize / 3f

  scope.drawLine(gridColor, Offset(bx + cell, by + 8f), Offset(bx + cell, by + boardSize - 8f), strokeWidth = 5f)
  scope.drawLine(gridColor, Offset(bx + cell * 2, by + 8f), Offset(bx + cell * 2, by + boardSize - 8f), strokeWidth = 5f)
  scope.drawLine(gridColor, Offset(bx + 8f, by + cell), Offset(bx + boardSize - 8f, by + cell), strokeWidth = 5f)
  scope.drawLine(gridColor, Offset(bx + 8f, by + cell * 2), Offset(bx + boardSize - 8f, by + cell * 2), strokeWidth = 5f)

  // Top Left: 3D X Piece (Candy Watermelon)
  val xCenter = Offset(bx + cell * 0.5f, by + cell * 0.5f)
  val arm = cell * 0.3f
  scope.drawLine(CandyWatermelon, Offset(xCenter.x - arm, xCenter.y - arm), Offset(xCenter.x + arm, xCenter.y + arm), strokeWidth = 7f)
  scope.drawLine(CandyWatermelon, Offset(xCenter.x + arm, xCenter.y - arm), Offset(xCenter.x - arm, xCenter.y + arm), strokeWidth = 7f)

  // Center: 3D O Piece (Candy Cyan)
  val oCenter = Offset(bx + cell * 1.5f, by + cell * 1.5f)
  scope.drawCircle(CandyCyan, radius = arm, center = oCenter, style = Stroke(width = 6.5f))

  // Bottom Right: 3D X Piece
  val x2Center = Offset(bx + cell * 2.5f, by + cell * 2.5f)
  scope.drawLine(CandyWatermelon, Offset(x2Center.x - arm, x2Center.y - arm), Offset(x2Center.x + arm, x2Center.y + arm), strokeWidth = 7f)
  scope.drawLine(CandyWatermelon, Offset(x2Center.x + arm, x2Center.y - arm), Offset(x2Center.x - arm, x2Center.y + arm), strokeWidth = 7f)
}

// 7. Brick Breaker: Neon paddle + ball + glowing bricks
private fun drawBrickBreakerArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  val cols = 4
  val rows = 3
  val brickW = w * 0.18f
  val brickH = h * 0.08f
  val startX = (w - (cols * brickW + (cols - 1) * 6f)) / 2
  val startY = h * 0.16f

  val rowColors = listOf(CandyWatermelon, CandyTangerine, CandyMint)
  for (r in 0 until rows) {
    for (c in 0 until cols) {
      val bx = startX + c * (brickW + 6f)
      val by = startY + r * (brickH + 5f)
      scope.drawRoundRect(
        color = rowColors[r],
        topLeft = Offset(bx, by),
        size = Size(brickW, brickH),
        cornerRadius = CornerRadius(4f, 4f)
      )
    }
  }

  // Bouncing Neon Ball
  val ballCenter = Offset(w * 0.52f, h * 0.58f)
  scope.drawCircle(
    brush = Brush.radialGradient(listOf(Color.White, CandyCyan)),
    radius = 10f,
    center = ballCenter
  )

  // Paddle at bottom
  val padW = w * 0.45f
  val padH = h * 0.08f
  val padX = (w - padW) / 2
  val padY = h * 0.78f

  scope.drawRoundRect(
    brush = Brush.horizontalGradient(listOf(CandyGrape, CandyCyan, CandyMint)),
    topLeft = Offset(padX, padY),
    size = Size(padW, padH),
    cornerRadius = CornerRadius(10f, 10f)
  )
}

// 8. Memory Match: 3D candy cards
private fun drawMemoryMatchArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  val cardSize = minOf(w, h) * 0.34f
  val spacing = minOf(w, h) * 0.08f
  val startX = (w - (cardSize * 2 + spacing)) / 2
  val startY = (h - (cardSize * 2 + spacing)) / 2

  val cardTints = listOf(CandyWatermelon, CandyCyan, CandyMint, CandyLemon)
  var idx = 0
  for (r in 0 until 2) {
    for (c in 0 until 2) {
      val cx = startX + c * (cardSize + spacing)
      val cy = startY + r * (cardSize + spacing)
      val tint = cardTints[idx % cardTints.size]

      scope.drawRoundRect(
        color = Color(0xFF2C1B4A),
        topLeft = Offset(cx, cy + 4f),
        size = Size(cardSize, cardSize),
        cornerRadius = CornerRadius(12f, 12f)
      )
      scope.drawRoundRect(
        color = tint,
        topLeft = Offset(cx, cy),
        size = Size(cardSize, cardSize),
        cornerRadius = CornerRadius(12f, 12f)
      )
      scope.drawCircle(
        color = Color.White,
        radius = cardSize * 0.2f,
        center = Offset(cx + cardSize * 0.5f, cy + cardSize * 0.5f)
      )
      idx++
    }
  }
}

// Generic Arcade Card
private fun drawGenericArcadeArtwork(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  scope.drawCircle(
    brush = Brush.radialGradient(listOf(CandyGrape, Color.Transparent)),
    radius = w * 0.45f,
    center = Offset(w * 0.5f, h * 0.5f)
  )
  scope.drawRoundRect(
    color = CandyCyan,
    topLeft = Offset(w * 0.25f, h * 0.35f),
    size = Size(w * 0.5f, h * 0.3f),
    cornerRadius = CornerRadius(14f, 14f)
  )
}
