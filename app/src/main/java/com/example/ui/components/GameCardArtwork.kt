package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.ui.theme.*

/**
 * Hyper-polished 3D commercial casual game icon artwork.
 * Rendered with rich volumetric lighting, specular highlights, character expressions,
 * and high-contrast vibrant colors matching Google Play Store / App Store top casual games.
 */
@Composable
fun GameCardArtwork(
  gameId: String,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.fillMaxSize()) {
    when (gameId) {
      "water_sort" -> drawWaterSortIcon(this)
      "game_2048" -> draw2048Icon(this)
      "snake" -> drawSnakeIcon(this)
      "highway_racer", "threejs_galaxy_runner" -> drawHighwayRacerIcon(this)
      "mini_ludo" -> drawLudoIcon(this)
      "tic_tac_toe" -> drawTicTacToeIcon(this)
      "brick_breaker" -> drawBrickBreakerIcon(this)
      "memory_match" -> drawMemoryMatchIcon(this)
      "word_guess" -> drawWordGuessIcon(this)
      "dot_box" -> drawDotBoxIcon(this)
      "pong" -> drawBrickBreakerIcon(this)
      else -> drawGenericArcadeIcon(this)
    }

    // Universal 3D Glossy App Icon Glass Sheen (Gives that premium App Store squircle shine)
    drawGlossyGlassSheen(this)
  }
}

// -------------------------------------------------------------
// Universal Glossy Glass Reflection Overlay
// -------------------------------------------------------------
private fun drawGlossyGlassSheen(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  val sheenPath = Path().apply {
    moveTo(0f, 0f)
    lineTo(w, 0f)
    lineTo(w, h * 0.32f)
    cubicTo(
      w * 0.7f, h * 0.42f,
      w * 0.3f, h * 0.46f,
      0f, h * 0.28f
    )
    close()
  }

  scope.drawPath(
    brush = Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.28f),
        Color.White.copy(alpha = 0.08f),
        Color.Transparent
      )
    ),
    path = sheenPath
  )
}

// -------------------------------------------------------------
// 1. Water Sort 3D Icon (Test tubes with bubbles and pouring liquid)
// -------------------------------------------------------------
private fun drawWaterSortIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Rich Royal Sapphire Background with radial light
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF194475), Color(0xFF09192E), Color(0xFF040A14)),
      center = Offset(w * 0.5f, h * 0.35f),
      radius = w * 0.7f
    )
  )

  // Floating ambient bubbles
  val bubbleCenters = listOf(
    Offset(w * 0.15f, h * 0.25f) to 6f,
    Offset(w * 0.85f, h * 0.3f) to 8f,
    Offset(w * 0.2f, h * 0.75f) to 5f,
    Offset(w * 0.8f, h * 0.8f) to 7f
  )
  for ((pos, r) in bubbleCenters) {
    scope.drawCircle(
      brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.6f), Color(0x334DD0E1))),
      radius = r,
      center = pos
    )
  }

  // 3 Glass Test Tubes (Two standing, one pouring)
  val tubeW = w * 0.20f
  val tubeH = h * 0.60f
  val baseY = h * 0.28f

  // Left Standing Tube
  draw3DTestTube(
    scope = scope,
    x = w * 0.16f,
    y = baseY,
    width = tubeW,
    height = tubeH,
    liquidColors = listOf(Color(0xFFE91E63), Color(0xFFFFC107), Color(0xFF00E5FF), Color(0xFF7C4DFF))
  )

  // Center Target Tube (filling up)
  draw3DTestTube(
    scope = scope,
    x = w * 0.42f,
    y = baseY,
    width = tubeW,
    height = tubeH,
    liquidColors = listOf(Color(0xFF00E5FF), Color(0xFF00E676), Color(0xFFFF5722), Color(0xFFFFEB3B))
  )

  // Right Tilted Pouring Tube
  val pourStartX = w * 0.76f
  val pourStartY = baseY - h * 0.04f
  draw3DTestTube(
    scope = scope,
    x = pourStartX,
    y = pourStartY,
    width = tubeW,
    height = tubeH * 0.85f,
    liquidColors = listOf(Color(0xFFFFEB3B), Color(0xFFFFEB3B), Color(0xFF9C27B0)),
    tiltAngle = -15f
  )

  // Glowing Pouring Liquid Arc from right tube to center tube
  val pourStream = Path().apply {
    moveTo(pourStartX, pourStartY + 8f)
    cubicTo(
      w * 0.65f, pourStartY + 14f,
      w * 0.56f, baseY + 18f,
      w * 0.52f, baseY + tubeH * 0.25f
    )
    lineTo(w * 0.48f, baseY + tubeH * 0.25f)
    cubicTo(
      w * 0.54f, baseY + 14f,
      w * 0.62f, pourStartY + 10f,
      pourStartX - 4f, pourStartY + 4f
    )
    close()
  }
  scope.drawPath(
    brush = Brush.verticalGradient(listOf(Color(0xFFFFEB3B), Color(0xFFFF9800))),
    path = pourStream
  )

  // Splash droplets
  scope.drawCircle(Color(0xFFFFEB3B), 4f, Offset(w * 0.51f, baseY + tubeH * 0.22f))
  scope.drawCircle(Color.White, 2.5f, Offset(w * 0.49f, baseY + tubeH * 0.24f))
}

private fun draw3DTestTube(
  scope: DrawScope,
  x: Float,
  y: Float,
  width: Float,
  height: Float,
  liquidColors: List<Color>,
  tiltAngle: Float = 0f
) {
  // Tube Shadow
  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.45f),
    topLeft = Offset(x + 4f, y + 8f),
    size = Size(width, height),
    cornerRadius = CornerRadius(width / 2, width / 2)
  )

  // Liquid Layers inside
  val layerCount = liquidColors.size
  val layerH = (height - 10f) / layerCount

  for (i in liquidColors.indices) {
    val color = liquidColors[i]
    val layerY = y + height - (i + 1) * layerH
    val isBottom = (i == 0)

    scope.drawRoundRect(
      brush = Brush.horizontalGradient(
        colors = listOf(
          color.copy(alpha = 0.95f),
          color,
          color.copy(alpha = 0.8f)
        )
      ),
      topLeft = Offset(x + 3f, layerY),
      size = Size(width - 6f, layerH + 2f),
      cornerRadius = CornerRadius(if (isBottom) (width - 6f) / 2 else 4f, if (isBottom) (width - 6f) / 2 else 4f)
    )

    // Meniscus curved surface
    scope.drawOval(
      color = Color.White.copy(alpha = 0.45f),
      topLeft = Offset(x + 5f, layerY - 3f),
      size = Size(width - 10f, 6f)
    )

    // Liquid effervescent bubble
    scope.drawCircle(
      color = Color.White.copy(alpha = 0.65f),
      radius = 2.5f,
      center = Offset(x + width * 0.35f, layerY + layerH * 0.4f)
    )
  }

  // Crystal Glass Tube Body Outline
  scope.drawRoundRect(
    color = Color.White.copy(alpha = 0.9f),
    topLeft = Offset(x, y),
    size = Size(width, height),
    cornerRadius = CornerRadius(width / 2, width / 2),
    style = Stroke(width = 3.5f)
  )

  // Glass Lip / Rim at Top
  scope.drawRoundRect(
    color = Color.White,
    topLeft = Offset(x - 3f, y - 4f),
    size = Size(width + 6f, 8f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // Specular Glass Reflection Sheen (Vertical white stripe down the left edge)
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.15f))
    ),
    topLeft = Offset(x + 4f, y + 10f),
    size = Size(3.5f, height - 24f),
    cornerRadius = CornerRadius(2f, 2f)
  )
}

// -------------------------------------------------------------
// 2. 2048 Merge 3D Icon (Glossy beveled cubes with 3D numbers)
// -------------------------------------------------------------
private fun draw2048Icon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Rich Cosmic Purple & Sunset Orange Backdrop
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF7B1FA2), Color(0xFF311B92), Color(0xFF12005E)),
      center = Offset(w * 0.5f, h * 0.4f),
      radius = w * 0.75f
    )
  )

  // Background Starburst / Diamond accents
  drawStarBurst(scope, Offset(w * 0.18f, h * 0.2f), 12f, Color(0xFFFFD54F))
  drawStarBurst(scope, Offset(w * 0.82f, h * 0.25f), 16f, Color(0xFFFF80AB))
  drawStarBurst(scope, Offset(w * 0.85f, h * 0.78f), 10f, Color(0xFF80D8FF))

  // 1. Supporting Cube (Left: 1024 - Coral)
  draw3DPuzzleCube(
    scope = scope,
    x = w * 0.08f,
    y = h * 0.48f,
    size = w * 0.38f,
    frontColor = Color(0xFFFF5252),
    topColor = Color(0xFFFF8A80),
    sideColor = Color(0xFFD50000),
    label = "1024"
  )

  // 2. Supporting Cube (Right: 512 - Tangerine)
  draw3DPuzzleCube(
    scope = scope,
    x = w * 0.54f,
    y = h * 0.48f,
    size = w * 0.38f,
    frontColor = Color(0xFFFF9800),
    topColor = Color(0xFFFFB74D),
    sideColor = Color(0xFFE65100),
    label = "512"
  )

  // 3. Hero Center Cube (2048 - Brilliant Gold with 3D crown glow)
  draw3DPuzzleCube(
    scope = scope,
    x = w * 0.22f,
    y = h * 0.15f,
    size = w * 0.56f,
    frontColor = Color(0xFFFFC107),
    topColor = Color(0xFFFFF176),
    sideColor = Color(0xFFFF8F00),
    label = "2048",
    hasGoldenCrown = true
  )
}

private fun draw3DPuzzleCube(
  scope: DrawScope,
  x: Float,
  y: Float,
  size: Float,
  frontColor: Color,
  topColor: Color,
  sideColor: Color,
  label: String,
  hasGoldenCrown: Boolean = false
) {
  // Soft Drop Shadow
  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.5f),
    topLeft = Offset(x + size * 0.05f, y + size * 0.15f),
    size = Size(size, size),
    cornerRadius = CornerRadius(size * 0.22f, size * 0.22f)
  )

  // Main Beveled Cube Body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      listOf(topColor, frontColor, sideColor)
    ),
    topLeft = Offset(x, y),
    size = Size(size, size),
    cornerRadius = CornerRadius(size * 0.22f, size * 0.22f)
  )

  // Top Glossy Bevel Reflection
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      listOf(Color.White.copy(alpha = 0.65f), Color.White.copy(alpha = 0.1f))
    ),
    topLeft = Offset(x + 4f, y + 4f),
    size = Size(size - 8f, size * 0.42f),
    cornerRadius = CornerRadius(size * 0.18f, size * 0.18f)
  )

  // Crisp Inner Border
  scope.drawRoundRect(
    color = Color.White.copy(alpha = 0.7f),
    topLeft = Offset(x, y),
    size = Size(size, size),
    cornerRadius = CornerRadius(size * 0.22f, size * 0.22f),
    style = Stroke(width = 2.5f)
  )

  // Embossed Bold 3D Text using native canvas
  scope.drawContext.canvas.nativeCanvas.apply {
    val paint = Paint().apply {
      isAntiAlias = true
      textAlign = Paint.Align.CENTER
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      this.textSize = size * 0.28f
      // 3D Drop Shadow on text
      setShadowLayer(6f, 0f, 3f, android.graphics.Color.BLACK)
    }

    // Shadow text pass
    paint.color = android.graphics.Color.argb(160, 0, 0, 0)
    drawText(label, x + size * 0.5f, y + size * 0.64f + 2f, paint)

    // Crisp White Top Text pass
    paint.clearShadowLayer()
    paint.color = android.graphics.Color.WHITE
    drawText(label, x + size * 0.5f, y + size * 0.64f, paint)
  }

  // Golden Sparkle / Crown on 2048 hero cube
  if (hasGoldenCrown) {
    drawStarBurst(scope, Offset(x + size * 0.15f, y + size * 0.18f), size * 0.14f, Color.White)
    drawStarBurst(scope, Offset(x + size * 0.85f, y + size * 0.22f), size * 0.16f, Color(0xFFFFF9C4))
  }
}

// -------------------------------------------------------------
// 3. Retro Snake 3D Icon (Adorable cartoon worm with big eyes & apple)
// Directly inspired by row 3, col 12 of user's reference image!
// -------------------------------------------------------------
private fun drawSnakeIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Lush Green Checkerboard Lawn Background
  val tileCount = 6
  val tileSize = w / tileCount
  for (r in 0 until tileCount) {
    for (c in 0 until tileCount) {
      val isEven = (r + c) % 2 == 0
      scope.drawRect(
        color = if (isEven) Color(0xFF4CAF50) else Color(0xFF43A047),
        topLeft = Offset(c * tileSize, r * tileSize),
        size = Size(tileSize, tileSize)
      )
    }
  }

  // 3D Juicy Red Candy Apple (Target food on top-right)
  val appleX = w * 0.72f
  val appleY = h * 0.28f
  val appleR = w * 0.16f

  // Apple Drop Shadow
  scope.drawOval(
    color = Color.Black.copy(alpha = 0.35f),
    topLeft = Offset(appleX - appleR * 0.8f, appleY + appleR * 0.6f),
    size = Size(appleR * 1.6f, appleR * 0.6f)
  )

  // Apple 3D Body (Watermelon gradient)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFF5252), Color(0xFFD50000), Color(0xFF880E4F)),
      center = Offset(appleX - appleR * 0.3f, appleY - appleR * 0.3f),
      radius = appleR * 1.2f
    ),
    radius = appleR,
    center = Offset(appleX, appleY)
  )

  // Apple Specular Curved Shine
  scope.drawOval(
    color = Color.White.copy(alpha = 0.7f),
    topLeft = Offset(appleX - appleR * 0.65f, appleY - appleR * 0.65f),
    size = Size(appleR * 0.7f, appleR * 0.45f)
  )

  // Apple Stem & Green Leaf
  val stemPath = Path().apply {
    moveTo(appleX, appleY - appleR * 0.8f)
    quadraticBezierTo(appleX + 6f, appleY - appleR * 1.3f, appleX + 12f, appleY - appleR * 1.4f)
  }
  scope.drawPath(stemPath, color = Color(0xFF5D4037), style = Stroke(width = 4f))

  val leafPath = Path().apply {
    moveTo(appleX + 4f, appleY - appleR * 0.9f)
    quadraticBezierTo(appleX + 22f, appleY - appleR * 1.3f, appleX + 26f, appleY - appleR * 0.9f)
    quadraticBezierTo(appleX + 14f, appleY - appleR * 0.7f, appleX + 4f, appleY - appleR * 0.9f)
    close()
  }
  scope.drawPath(leafPath, brush = Brush.linearGradient(listOf(Color(0xFF76FF03), Color(0xFF2E7D32))))

  // 3D Cartoon Worm / Snake Body Segments (Warm Orange with 3D sphere gradient)
  val segR = w * 0.14f
  val bodyPoints = listOf(
    Offset(w * 0.16f, h * 0.78f), // Tail
    Offset(w * 0.28f, h * 0.66f),
    Offset(w * 0.44f, h * 0.62f),
    Offset(w * 0.58f, h * 0.68f),
    Offset(w * 0.68f, h * 0.76f),
    Offset(w * 0.45f, h * 0.38f)  // Giant Cute Head
  )

  // Body segment shadows
  for (pt in bodyPoints) {
    scope.drawOval(
      color = Color.Black.copy(alpha = 0.3f),
      topLeft = Offset(pt.x - segR * 0.9f, pt.y + segR * 0.3f),
      size = Size(segR * 1.8f, segR * 0.7f)
    )
  }

  // Body segments rendering
  for (i in 0 until bodyPoints.size - 1) {
    val pt = bodyPoints[i]
    scope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800), Color(0xFFE65100)),
        center = Offset(pt.x - segR * 0.3f, pt.y - segR * 0.3f),
        radius = segR * 1.1f
      ),
      radius = segR,
      center = pt
    )
    // Segment highlight
    scope.drawCircle(
      color = Color.White.copy(alpha = 0.45f),
      radius = segR * 0.3f,
      center = Offset(pt.x - segR * 0.3f, pt.y - segR * 0.3f)
    )
  }

  // Giant Cute Head (Foreground)
  val head = bodyPoints.last()
  val headR = w * 0.22f

  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFFCC80), Color(0xFFFF9800), Color(0xFFEF6C00)),
      center = Offset(head.x - headR * 0.3f, head.y - headR * 0.3f),
      radius = headR * 1.2f
    ),
    radius = headR,
    center = head
  )

  // Big Expressive Cartoon Eyes (Look right towards the apple!)
  val eye1 = Offset(head.x - headR * 0.25f, head.y - headR * 0.25f)
  val eye2 = Offset(head.x + headR * 0.40f, head.y - headR * 0.25f)
  val eyeR = headR * 0.40f

  for (eye in listOf(eye1, eye2)) {
    // Eye white sclera
    scope.drawCircle(Color.White, eyeR, eye)
    // Eye border
    scope.drawCircle(Color.Black.copy(alpha = 0.2f), eyeR, eye, style = Stroke(2f))

    // Black pupil looking toward apple (shifted right-up)
    val pupilCenter = Offset(eye.x + eyeR * 0.25f, eye.y - eyeR * 0.1f)
    scope.drawCircle(Color(0xFF1A1A1A), eyeR * 0.58f, pupilCenter)

    // Sparkly white specular catchlights (Double catchlight like Disney/Pixar!)
    scope.drawCircle(Color.White, eyeR * 0.24f, Offset(pupilCenter.x - eyeR * 0.15f, pupilCenter.y - eyeR * 0.18f))
    scope.drawCircle(Color.White, eyeR * 0.12f, Offset(pupilCenter.x + eyeR * 0.22f, pupilCenter.y + eyeR * 0.18f))
  }

  // Rosy Blush Cheeks
  scope.drawOval(
    color = Color(0xFFFF5252).copy(alpha = 0.5f),
    topLeft = Offset(head.x - headR * 0.8f, head.y + headR * 0.2f),
    size = Size(headR * 0.45f, headR * 0.25f)
  )
  scope.drawOval(
    color = Color(0xFFFF5252).copy(alpha = 0.5f),
    topLeft = Offset(head.x + headR * 0.45f, head.y + headR * 0.2f),
    size = Size(headR * 0.45f, headR * 0.25f)
  )

  // Cheerful Smile
  val smilePath = Path().apply {
    moveTo(head.x - headR * 0.25f, head.y + headR * 0.35f)
    quadraticBezierTo(head.x, head.y + headR * 0.65f, head.x + headR * 0.35f, head.y + headR * 0.32f)
  }
  scope.drawPath(smilePath, color = Color(0xFF3E2723), style = Stroke(width = 4f))
}

// -------------------------------------------------------------
// 4. Highway Racer 3D Icon (Sleek red supercar on neon highway)
// -------------------------------------------------------------
private fun drawHighwayRacerIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Sunset Desert / Cyberpunk Neon Horizon Background
  scope.drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(
        Color(0xFFFF6F00), // Sunset Orange
        Color(0xFFD81B60), // Hot Pink
        Color(0xFF4A148C), // Royal Purple
        Color(0xFF1A1A2E)  // Asphalt Dark
      )
    )
  )

  // Big Glowing Sun on horizon
  scope.drawCircle(
    brush = Brush.radialGradient(listOf(Color(0xFFFFF176), Color(0xFFFF8F00).copy(alpha = 0.6f), Color.Transparent)),
    radius = w * 0.32f,
    center = Offset(w * 0.5f, h * 0.22f)
  )

  // Perspective Highway Asphalt Road
  val road = Path().apply {
    moveTo(w * 0.38f, h * 0.26f)
    lineTo(w * 0.62f, h * 0.26f)
    lineTo(w * 0.98f, h)
    lineTo(w * 0.02f, h)
    close()
  }
  scope.drawPath(
    brush = Brush.verticalGradient(listOf(Color(0xFF263238), Color(0xFF101619))),
    path = road
  )

  // Glowing Yellow Dashed Centerlines (in perspective)
  val dashColor = Color(0xFFFFEB3B)
  scope.drawLine(dashColor, Offset(w * 0.5f, h * 0.28f), Offset(w * 0.5f, h * 0.38f), strokeWidth = 2.5f)
  scope.drawLine(dashColor, Offset(w * 0.5f, h * 0.44f), Offset(w * 0.5f, h * 0.60f), strokeWidth = 5f)
  scope.drawLine(dashColor, Offset(w * 0.5f, h * 0.68f), Offset(w * 0.5f, h * 0.92f), strokeWidth = 8f)

  // Glowing Guardrail Lights
  scope.drawLine(Color(0xFF00E5FF), Offset(w * 0.38f, h * 0.26f), Offset(w * 0.02f, h), strokeWidth = 3f)
  scope.drawLine(Color(0xFF00E5FF), Offset(w * 0.62f, h * 0.26f), Offset(w * 0.98f, h), strokeWidth = 3f)

  // Sleek Red 3D Supercar (Foreground)
  val carW = w * 0.52f
  val carH = h * 0.42f
  val carX = (w - carW) / 2
  val carY = h * 0.48f

  // Car Ground Shadow
  scope.drawOval(
    color = Color.Black.copy(alpha = 0.7f),
    topLeft = Offset(carX - 8f, carY + carH * 0.70f),
    size = Size(carW + 16f, carH * 0.35f)
  )

  // Twin Nitro Flame Exhaust Bursts!
  val flame1 = Offset(carX + carW * 0.32f, carY + carH * 0.88f)
  val flame2 = Offset(carX + carW * 0.68f, carY + carH * 0.88f)
  for (flame in listOf(flame1, flame2)) {
    scope.drawCircle(
      brush = Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF2979FF), Color.Transparent)),
      radius = 14f,
      center = flame
    )
    scope.drawCircle(Color.White, 5f, flame)
  }

  // Aerodynamic Car Rear Body (Chunky Supercar)
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      listOf(Color(0xFFFF1744), Color(0xFFD50000), Color(0xFF880E4F))
    ),
    topLeft = Offset(carX, carY + carH * 0.3f),
    size = Size(carW, carH * 0.55f),
    cornerRadius = CornerRadius(20f, 20f)
  )

  // Cabin & Roof (Black glossy roof with tinted glass)
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFF212121), Color(0xFF000000))),
    topLeft = Offset(carX + carW * 0.16f, carY + carH * 0.05f),
    size = Size(carW * 0.68f, carH * 0.42f),
    cornerRadius = CornerRadius(16f, 16f)
  )

  // Curved Rear Windshield Glass Specular Shine
  scope.drawRoundRect(
    brush = Brush.linearGradient(
      listOf(Color(0xFF80D8FF).copy(alpha = 0.8f), Color.Transparent)
    ),
    topLeft = Offset(carX + carW * 0.22f, carY + carH * 0.12f),
    size = Size(carW * 0.56f, carH * 0.25f),
    cornerRadius = CornerRadius(10f, 10f)
  )

  // Glowing Neon Red Cyber Taillight Strip
  scope.drawRoundRect(
    color = Color(0xFFFF1744),
    topLeft = Offset(carX + carW * 0.08f, carY + carH * 0.52f),
    size = Size(carW * 0.84f, 6f),
    cornerRadius = CornerRadius(3f, 3f)
  )
  scope.drawRoundRect(
    color = Color.White,
    topLeft = Offset(carX + carW * 0.15f, carY + carH * 0.53f),
    size = Size(carW * 0.70f, 3f),
    cornerRadius = CornerRadius(2f, 2f)
  )

  // High-Downforce Rear Spoiler Wing
  scope.drawRoundRect(
    color = Color(0xFF1E1E1E),
    topLeft = Offset(carX - 4f, carY + carH * 0.25f),
    size = Size(carW + 8f, 10f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  scope.drawRoundRect(
    color = Color(0xFFFF5252),
    topLeft = Offset(carX - 4f, carY + carH * 0.25f),
    size = Size(carW + 8f, 3f),
    cornerRadius = CornerRadius(2f, 2f)
  )
}

// -------------------------------------------------------------
// 5. Mini Ludo 3D Icon (Chunky 3D Dice with red dots & 3D Pawns)
// Directly inspired by row 7, col 6 of user's reference image!
// -------------------------------------------------------------
private fun drawLudoIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Rich Teak Wooden Board Base
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF4E342E), Color(0xFF3E2723), Color(0xFF1B0000)),
      center = Offset(w * 0.5f, h * 0.5f),
      radius = w * 0.8f
    )
  )

  // Authentic 4-Color Ludo Quadrants (Ruby, Emerald, Gold, Azure)
  val margin = w * 0.08f
  val boardW = w - margin * 2
  val qSize = boardW * 0.42f

  // Top Left: Red Quadrant
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFFFF5252), Color(0xFFD50000))),
    topLeft = Offset(margin, margin),
    size = Size(qSize, qSize),
    cornerRadius = CornerRadius(14f, 14f)
  )
  // Top Right: Green Quadrant
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFF69F0AE), Color(0xFF00C853))),
    topLeft = Offset(margin + boardW - qSize, margin),
    size = Size(qSize, qSize),
    cornerRadius = CornerRadius(14f, 14f)
  )
  // Bottom Left: Yellow Quadrant
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFB300))),
    topLeft = Offset(margin, margin + boardW - qSize),
    size = Size(qSize, qSize),
    cornerRadius = CornerRadius(14f, 14f)
  )
  // Bottom Right: Blue Quadrant
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFF448AFF), Color(0xFF2979FF))),
    topLeft = Offset(margin + boardW - qSize, margin + boardW - qSize),
    size = Size(qSize, qSize),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // 3D Conical Ludo Pawns
  // Left: Red Pawn
  draw3DLudoPawn(scope, Offset(w * 0.22f, h * 0.42f), Color(0xFFFF1744), Color(0xFFD50000))
  // Right: Yellow Pawn
  draw3DLudoPawn(scope, Offset(w * 0.78f, h * 0.42f), Color(0xFFFFD600), Color(0xFFFFAB00))

  // Center Huge 3D Isometric White Dice!
  val diceSize = w * 0.44f
  val diceX = (w - diceSize) / 2
  val diceY = (h - diceSize) / 2

  // Dice Soft Drop Shadow
  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.6f),
    topLeft = Offset(diceX + 6f, diceY + 14f),
    size = Size(diceSize, diceSize),
    cornerRadius = CornerRadius(22f, 22f)
  )

  // 3D Beveled Pure White Dice Body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      listOf(Color.White, Color(0xFFF5F5F5), Color(0xFFE0E0E0))
    ),
    topLeft = Offset(diceX, diceY),
    size = Size(diceSize, diceSize),
    cornerRadius = CornerRadius(22f, 22f)
  )

  // Top Glossy Bevel
  scope.drawRoundRect(
    color = Color.White.copy(alpha = 0.8f),
    topLeft = Offset(diceX + 4f, diceY + 4f),
    size = Size(diceSize - 8f, diceSize * 0.35f),
    cornerRadius = CornerRadius(18f, 18f)
  )

  // 5 Classic Indented Crimson Dots (Pips) on the Dice!
  val dotR = diceSize * 0.08f
  val dotColor = Color(0xFFD50000)
  val pips = listOf(
    Offset(diceX + diceSize * 0.26f, diceY + diceSize * 0.26f),
    Offset(diceX + diceSize * 0.74f, diceY + diceSize * 0.26f),
    Offset(diceX + diceSize * 0.50f, diceY + diceSize * 0.50f), // Center dot
    Offset(diceX + diceSize * 0.26f, diceY + diceSize * 0.74f),
    Offset(diceX + diceSize * 0.74f, diceY + diceSize * 0.74f)
  )

  for (pip in pips) {
    // Inset shadow for 3D engraved look
    scope.drawCircle(Color.Black.copy(alpha = 0.35f), dotR + 1.5f, Offset(pip.x, pip.y + 1.5f))
    scope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFF5252), dotColor, Color(0xFF880E4F)),
        center = Offset(pip.x - 2f, pip.y - 2f),
        radius = dotR * 1.2f
      ),
      radius = dotR,
      center = pip
    )
    // White reflection in pip
    scope.drawCircle(Color.White.copy(alpha = 0.7f), dotR * 0.3f, Offset(pip.x - dotR * 0.3f, pip.y - dotR * 0.3f))
  }
}

private fun draw3DLudoPawn(scope: DrawScope, pos: Offset, lightColor: Color, darkColor: Color) {
  val r = 16f
  // Pawn Head (Sphere)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White.copy(alpha = 0.7f), lightColor, darkColor),
      center = Offset(pos.x - r * 0.3f, pos.y - r * 0.3f),
      radius = r * 1.2f
    ),
    radius = r,
    center = pos
  )
  // Pawn Conical Base
  val basePath = Path().apply {
    moveTo(pos.x - r * 0.6f, pos.y + r * 0.5f)
    lineTo(pos.x + r * 0.6f, pos.y + r * 0.5f)
    lineTo(pos.x + r * 1.1f, pos.y + r * 2.2f)
    lineTo(pos.x - r * 1.1f, pos.y + r * 2.2f)
    close()
  }
  scope.drawPath(basePath, brush = Brush.verticalGradient(listOf(lightColor, darkColor)))
}

// -------------------------------------------------------------
// 6. Word Guess 3D Icon (Glossy Scrabble letter blocks + 3D Pencil)
// -------------------------------------------------------------
private fun drawWordGuessIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Warm Golden Sunburst Studio Background
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFFECB3), Color(0xFFFFCA28), Color(0xFFFF8F00), Color(0xFFE65100)),
      center = Offset(w * 0.5f, h * 0.4f),
      radius = w * 0.8f
    )
  )

  // Floating Star Sparkles
  drawStarBurst(scope, Offset(w * 0.15f, h * 0.18f), 12f, Color.White)
  drawStarBurst(scope, Offset(w * 0.85f, h * 0.22f), 14f, Color.White)

  // 4 Chunky 3D Letter Tiles: W, O, R, D
  val letters = listOf("W", "O", "R", "D")
  val tileColors = listOf(
    Color(0xFF00C853), // Green (Correct)
    Color(0xFF00C853), // Green
    Color(0xFFFFD600), // Yellow (Present)
    Color(0xFF00C853)  // Green
  )

  val tileSize = w * 0.20f
  val gap = w * 0.035f
  val totalW = 4 * tileSize + 3 * gap
  val startX = (w - totalW) / 2
  val startY = h * 0.28f

  for (i in 0 until 4) {
    val tx = startX + i * (tileSize + gap)
    val color = tileColors[i]

    // 3D Drop Shadow
    scope.drawRoundRect(
      color = Color.Black.copy(alpha = 0.45f),
      topLeft = Offset(tx + 3f, startY + 8f),
      size = Size(tileSize, tileSize),
      cornerRadius = CornerRadius(14f, 14f)
    )

    // Beveled Tile Body
    scope.drawRoundRect(
      brush = Brush.verticalGradient(
        listOf(color, color, color.copy(alpha = 0.8f))
      ),
      topLeft = Offset(tx, startY),
      size = Size(tileSize, tileSize),
      cornerRadius = CornerRadius(14f, 14f)
    )

    // Top Gloss Highlight
    scope.drawRoundRect(
      color = Color.White.copy(alpha = 0.45f),
      topLeft = Offset(tx + 2f, startY + 2f),
      size = Size(tileSize - 4f, tileSize * 0.4f),
      cornerRadius = CornerRadius(10f, 10f)
    )

    // Inner Border
    scope.drawRoundRect(
      color = Color.White.copy(alpha = 0.8f),
      topLeft = Offset(tx, startY),
      size = Size(tileSize, tileSize),
      cornerRadius = CornerRadius(14f, 14f),
      style = Stroke(width = 2.5f)
    )

    // Embossed Letter
    scope.drawContext.canvas.nativeCanvas.apply {
      val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        this.textSize = tileSize * 0.62f
        setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
        this.color = android.graphics.Color.WHITE
      }
      drawText(letters[i], tx + tileSize * 0.5f, startY + tileSize * 0.72f, paint)
    }
  }

  // Cute 3D Classic Yellow Pencil resting across bottom
  val penStartX = w * 0.12f
  val penY = h * 0.72f
  val penW = w * 0.76f
  val penH = h * 0.12f

  // Pencil Shadow
  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.4f),
    topLeft = Offset(penStartX + 4f, penY + 6f),
    size = Size(penW, penH),
    cornerRadius = CornerRadius(penH / 2, penH / 2)
  )

  // Pencil Body (Hexagonal Yellow)
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFFFFEB3B), Color(0xFFFBC02D), Color(0xFFF57F17))),
    topLeft = Offset(penStartX, penY),
    size = Size(penW * 0.75f, penH),
    cornerRadius = CornerRadius(penH / 2, penH / 2)
  )

  // Metal Ferrule & Pink Eraser on Right
  scope.drawRoundRect(
    color = Color(0xFFB0BEC5),
    topLeft = Offset(penStartX + penW * 0.62f, penY),
    size = Size(penW * 0.12f, penH),
    cornerRadius = CornerRadius(2f, 2f)
  )
  scope.drawRoundRect(
    color = Color(0xFFFF80AB),
    topLeft = Offset(penStartX + penW * 0.74f, penY),
    size = Size(penW * 0.14f, penH),
    cornerRadius = CornerRadius(penH / 2, penH / 2)
  )
}

// -------------------------------------------------------------
// 7. Brick Breaker 3D Icon (Neon ball shattering cyber bricks)
// -------------------------------------------------------------
private fun drawBrickBreakerIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Cyberpunk Arcade Grid Background
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF311B92), Color(0xFF1A237E), Color(0xFF0D1B2A)),
      center = Offset(w * 0.5f, h * 0.35f),
      radius = w * 0.8f
    )
  )

  // Multiple Rows of Glossy 3D Bricks
  val rows = 3
  val cols = 4
  val bW = w * 0.19f
  val bH = h * 0.08f
  val startX = (w - (cols * bW + (cols - 1) * 6f)) / 2
  val startY = h * 0.14f

  val rowColors = listOf(
    Color(0xFFFF1744), // Neon Red
    Color(0xFFFFEA00), // Neon Yellow
    Color(0xFF00E676)  // Neon Green
  )

  for (r in 0 until rows) {
    for (c in 0 until cols) {
      // Omit one brick to simulate it was just shattered!
      if (r == 1 && c == 2) continue

      val bx = startX + c * (bW + 6f)
      val by = startY + r * (bH + 6f)
      val col = rowColors[r]

      // Brick 3D bevel
      scope.drawRoundRect(
        color = Color.Black.copy(alpha = 0.4f),
        topLeft = Offset(bx + 2f, by + 4f),
        size = Size(bW, bH),
        cornerRadius = CornerRadius(6f, 6f)
      )
      scope.drawRoundRect(
        brush = Brush.verticalGradient(listOf(col, col.copy(alpha = 0.85f))),
        topLeft = Offset(bx, by),
        size = Size(bW, bH),
        cornerRadius = CornerRadius(6f, 6f)
      )
      // Top glossy highlight
      scope.drawRoundRect(
        color = Color.White.copy(alpha = 0.5f),
        topLeft = Offset(bx + 2f, by + 1.5f),
        size = Size(bW - 4f, bH * 0.45f),
        cornerRadius = CornerRadius(4f, 4f)
      )
    }
  }

  // Shattered Brick Flying Particles (at r=1, c=2)
  val impactX = startX + 2 * (bW + 6f) + bW * 0.5f
  val impactY = startY + 1 * (bH + 6f) + bH * 0.5f
  val shardColors = listOf(Color(0xFFFFEA00), Color(0xFFFF9100), Color.White)
  val shardOffsets = listOf(
    Offset(-12f, -14f), Offset(16f, -10f), Offset(-18f, 12f),
    Offset(14f, 16f), Offset(0f, -20f), Offset(-6f, 22f)
  )
  for (i in shardOffsets.indices) {
    val off = shardOffsets[i]
    scope.drawCircle(shardColors[i % shardColors.size], 4.5f, Offset(impactX + off.x, impactY + off.y))
  }

  // Glowing Electric Energy Ball & Comet Trail
  val ballCenter = Offset(w * 0.54f, h * 0.56f)
  val ballR = w * 0.08f

  // Comet Glow Tail
  scope.drawLine(
    brush = Brush.linearGradient(listOf(Color(0xFF00E5FF).copy(alpha = 0.8f), Color.Transparent)),
    start = ballCenter,
    end = Offset(w * 0.44f, h * 0.72f),
    strokeWidth = 14f
  )

  // Energy Ball Core (Super bright white-hot center)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White, Color(0xFF00E5FF), Color(0xFF2979FF), Color.Transparent),
      radius = ballR * 1.8f,
      center = ballCenter
    ),
    radius = ballR * 1.8f,
    center = ballCenter
  )
  scope.drawCircle(Color.White, ballR * 0.6f, ballCenter)

  // Chrome Metallic Arcade Paddle (at bottom)
  val padW = w * 0.62f
  val padH = h * 0.10f
  val padX = (w - padW) / 2
  val padY = h * 0.78f

  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.55f),
    topLeft = Offset(padX, padY + 6f),
    size = Size(padW, padH),
    cornerRadius = CornerRadius(padH / 2, padH / 2)
  )
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFFECEFF1), Color(0xFF90A4AE), Color(0xFF37474F))),
    topLeft = Offset(padX, padY),
    size = Size(padW, padH),
    cornerRadius = CornerRadius(padH / 2, padH / 2)
  )
  // Glowing Neon Cyan Center Core on paddle
  scope.drawRoundRect(
    brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF76FF03), Color(0xFF00E5FF))),
    topLeft = Offset(padX + padW * 0.2f, padY + padH * 0.35f),
    size = Size(padW * 0.6f, padH * 0.3f),
    cornerRadius = CornerRadius(4f, 4f)
  )
}

// -------------------------------------------------------------
// 8. Tic-Tac-Toe 3D Icon (Neon acrylic X & O cylinders)
// -------------------------------------------------------------
private fun drawTicTacToeIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Dark Obsidian Acrylic Board
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF212121), Color(0xFF141414), Color(0xFF050505)),
      center = Offset(w * 0.5f, h * 0.5f),
      radius = w * 0.75f
    )
  )

  // Golden / Neon Grid Lines with 3D groove shadow
  val boardSize = w * 0.76f
  val bx = (w - boardSize) / 2
  val by = (h - boardSize) / 2
  val cell = boardSize / 3f

  val gridGlow = Color(0xFFFFD54F)
  for (i in 1..2) {
    val x = bx + cell * i
    val y = by + cell * i
    // Vertical grooves
    scope.drawLine(Color.Black, Offset(x + 2f, by), Offset(x + 2f, by + boardSize), strokeWidth = 5f)
    scope.drawLine(gridGlow, Offset(x, by), Offset(x, by + boardSize), strokeWidth = 4f)
    // Horizontal grooves
    scope.drawLine(Color.Black, Offset(bx, y + 2f), Offset(bx + boardSize, y + 2f), strokeWidth = 5f)
    scope.drawLine(gridGlow, Offset(bx, y), Offset(bx + boardSize, y), strokeWidth = 4f)
  }

  // 3D Glowing Coral "X" (Top-Left)
  val xCenter = Offset(bx + cell * 0.5f, by + cell * 0.5f)
  val arm = cell * 0.36f
  draw3DXPiece(scope, xCenter, arm, Color(0xFFFF1744), Color(0xFFFF8A80))

  // 3D Glowing Cyan "O" (Center)
  val oCenter = Offset(bx + cell * 1.5f, by + cell * 1.5f)
  draw3DOPiece(scope, oCenter, cell * 0.36f, Color(0xFF00E5FF), Color(0xFF80D8FF))

  // 3D Glowing Coral "X" (Bottom-Right)
  val x2Center = Offset(bx + cell * 2.5f, by + cell * 2.5f)
  draw3DXPiece(scope, x2Center, arm, Color(0xFFFF1744), Color(0xFFFF8A80))

  // Golden Diagonal Win Strike Line
  scope.drawLine(
    brush = Brush.linearGradient(listOf(Color(0xFFFFEB3B), Color(0xFFFF9800), Color(0xFFFFEB3B))),
    start = Offset(bx + 10f, by + 10f),
    end = Offset(bx + boardSize - 10f, by + boardSize - 10f),
    strokeWidth = 6f
  )
  drawStarBurst(scope, Offset(bx + boardSize * 0.5f, by + boardSize * 0.5f), 16f, Color.White)
}

private fun draw3DXPiece(scope: DrawScope, center: Offset, arm: Float, color: Color, highlight: Color) {
  // Shadow
  scope.drawLine(Color.Black.copy(alpha = 0.5f), Offset(center.x - arm + 4f, center.y - arm + 6f), Offset(center.x + arm + 4f, center.y + arm + 6f), strokeWidth = 14f)
  scope.drawLine(Color.Black.copy(alpha = 0.5f), Offset(center.x + arm + 4f, center.y - arm + 6f), Offset(center.x - arm + 4f, center.y + arm + 6f), strokeWidth = 14f)
  // Main X body
  scope.drawLine(color, Offset(center.x - arm, center.y - arm), Offset(center.x + arm, center.y + arm), strokeWidth = 12f)
  scope.drawLine(color, Offset(center.x + arm, center.y - arm), Offset(center.x - arm, center.y + arm), strokeWidth = 12f)
  // Top specular highlight
  scope.drawLine(highlight, Offset(center.x - arm * 0.7f, center.y - arm * 0.7f - 1f), Offset(center.x + arm * 0.7f, center.y + arm * 0.7f - 1f), strokeWidth = 4f)
}

private fun draw3DOPiece(scope: DrawScope, center: Offset, radius: Float, color: Color, highlight: Color) {
  // Shadow
  scope.drawCircle(Color.Black.copy(alpha = 0.5f), radius, Offset(center.x + 3f, center.y + 5f), style = Stroke(width = 12f))
  // Main O Torus
  scope.drawCircle(color, radius, center, style = Stroke(width = 12f))
  // Specular Highlight
  scope.drawArc(
    color = highlight,
    startAngle = 180f,
    sweepAngle = 120f,
    useCenter = false,
    topLeft = Offset(center.x - radius, center.y - radius),
    size = Size(radius * 2, radius * 2),
    style = Stroke(width = 4f)
  )
}

// -------------------------------------------------------------
// 9. Dots & Boxes 3D Icon (Neon laser pegs & captured 3D tiles)
// -------------------------------------------------------------
private fun drawDotBoxIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Deep Navy Blueprint Background
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF0D47A1), Color(0xFF0A192F), Color(0xFF020C1B)),
      center = Offset(w * 0.5f, h * 0.5f),
      radius = w * 0.8f
    )
  )

  // 3x3 Peg Grid
  val boardSize = w * 0.68f
  val bx = (w - boardSize) / 2
  val by = (h - boardSize) / 2
  val step = boardSize / 2f

  // Captured Box 1 (Top-Left: Cyan 3D Beveled Box with crown)
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFF00E5FF), Color(0xFF0091EA))),
    topLeft = Offset(bx + 4f, by + 4f),
    size = Size(step - 8f, step - 8f),
    cornerRadius = CornerRadius(12f, 12f)
  )
  drawStarBurst(scope, Offset(bx + step * 0.5f, by + step * 0.5f), 12f, Color.White)

  // Captured Box 2 (Bottom-Right: Orange 3D Beveled Box)
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFFFF9100), Color(0xFFFF3D00))),
    topLeft = Offset(bx + step + 4f, by + step + 4f),
    size = Size(step - 8f, step - 8f),
    cornerRadius = CornerRadius(12f, 12f)
  )
  drawStarBurst(scope, Offset(bx + step * 1.5f, by + step * 1.5f), 12f, Color.White)

  // Neon Laser Lines connecting pegs
  val laserColor = Color(0xFF76FF03)
  // Horizontal connections
  scope.drawLine(laserColor, Offset(bx, by), Offset(bx + step, by), strokeWidth = 5f)
  scope.drawLine(laserColor, Offset(bx, by + step), Offset(bx + step, by + step), strokeWidth = 5f)
  scope.drawLine(laserColor, Offset(bx + step, by + step), Offset(bx + step * 2, by + step), strokeWidth = 5f)
  scope.drawLine(laserColor, Offset(bx + step, by + step * 2), Offset(bx + step * 2, by + step * 2), strokeWidth = 5f)

  // Vertical connections
  scope.drawLine(laserColor, Offset(bx, by), Offset(bx, by + step), strokeWidth = 5f)
  scope.drawLine(laserColor, Offset(bx + step, by), Offset(bx + step, by + step), strokeWidth = 5f)
  scope.drawLine(laserColor, Offset(bx + step, by + step), Offset(bx + step, by + step * 2), strokeWidth = 5f)
  scope.drawLine(laserColor, Offset(bx + step * 2, by + step), Offset(bx + step * 2, by + step * 2), strokeWidth = 5f)

  // 3D Polished Brass Dome Pegs
  for (r in 0..2) {
    for (c in 0..2) {
      val pegCenter = Offset(bx + c * step, by + r * step)
      val pegR = 10f
      // Shadow
      scope.drawCircle(Color.Black.copy(alpha = 0.5f), pegR, Offset(pegCenter.x + 2f, pegCenter.y + 3f))
      // 3D Metallic Golden Sphere
      scope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color.White, Color(0xFFFFD54F), Color(0xFFFF8F00)),
          center = Offset(pegCenter.x - 3f, pegCenter.y - 3f),
          radius = pegR * 1.2f
        ),
        radius = pegR,
        center = pegCenter
      )
    }
  }
}

// -------------------------------------------------------------
// 10. Memory Match 3D Icon (Royal casino velvet + 3D mystery cards)
// -------------------------------------------------------------
private fun drawMemoryMatchIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Royal Velvet Purple & Deep Blue Casino Backdrop
  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF4A148C), Color(0xFF1A0033), Color(0xFF090014)),
      center = Offset(w * 0.5f, h * 0.4f),
      radius = w * 0.8f
    )
  )

  // Golden Magic Bokeh sparkles in background
  drawStarBurst(scope, Offset(w * 0.18f, h * 0.2f), 14f, Color(0xFFFFD54F))
  drawStarBurst(scope, Offset(w * 0.84f, h * 0.25f), 12f, Color(0xFFFF80AB))

  // Card Dimensions
  val cardW = w * 0.38f
  val cardH = h * 0.54f

  // 1. Back Card (Tilted Left: Flipped face up showing Glowing 3D Diamond/Star)
  val c1X = w * 0.12f
  val c1Y = h * 0.26f

  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.5f),
    topLeft = Offset(c1X + 4f, c1Y + 10f),
    size = Size(cardW, cardH),
    cornerRadius = CornerRadius(16f, 16f)
  )
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFF00E5FF), Color(0xFF0091EA), Color(0xFF01579B))),
    topLeft = Offset(c1X, c1Y),
    size = Size(cardW, cardH),
    cornerRadius = CornerRadius(16f, 16f)
  )
  // Golden Card Border
  scope.drawRoundRect(
    color = Color(0xFFFFD700),
    topLeft = Offset(c1X, c1Y),
    size = Size(cardW, cardH),
    cornerRadius = CornerRadius(16f, 16f),
    style = Stroke(width = 3.5f)
  )
  // Center Glowing Diamond Gemstone
  drawStarBurst(scope, Offset(c1X + cardW * 0.5f, c1Y + cardH * 0.5f), cardW * 0.35f, Color.White)

  // 2. Front Card (Overlapping Right: Face down showing 3D Golden Question Mark "?")
  val c2X = w * 0.48f
  val c2Y = h * 0.20f

  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.65f),
    topLeft = Offset(c2X + 6f, c2Y + 14f),
    size = Size(cardW, cardH),
    cornerRadius = CornerRadius(16f, 16f)
  )
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFFFF1744), Color(0xFFC2185B), Color(0xFF880E4F))),
    topLeft = Offset(c2X, c2Y),
    size = Size(cardW, cardH),
    cornerRadius = CornerRadius(16f, 16f)
  )
  // Golden Card Border
  scope.drawRoundRect(
    color = Color(0xFFFFD700),
    topLeft = Offset(c2X, c2Y),
    size = Size(cardW, cardH),
    cornerRadius = CornerRadius(16f, 16f),
    style = Stroke(width = 3.5f)
  )

  // Embossed Golden 3D Question Mark ("?")
  scope.drawContext.canvas.nativeCanvas.apply {
    val paint = Paint().apply {
      isAntiAlias = true
      textAlign = Paint.Align.CENTER
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      this.textSize = cardW * 0.65f
      setShadowLayer(8f, 0f, 4f, android.graphics.Color.BLACK)
      this.color = android.graphics.Color.parseColor("#FFD700")
    }
    drawText("?", c2X + cardW * 0.5f, c2Y + cardH * 0.65f, paint)
  }
}

// -------------------------------------------------------------
// Fallback Generic Arcade Icon (Chunky 3D Gamepad)
// -------------------------------------------------------------
private fun drawGenericArcadeIcon(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  scope.drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF2979FF), Color(0xFF1565C0), Color(0xFF0D47A1)),
      center = Offset(w * 0.5f, h * 0.5f),
      radius = w * 0.7f
    )
  )

  val padW = w * 0.76f
  val padH = h * 0.44f
  val padX = (w - padW) / 2
  val padY = (h - padH) / 2

  scope.drawRoundRect(
    color = Color.Black.copy(alpha = 0.5f),
    topLeft = Offset(padX + 4f, padY + 8f),
    size = Size(padW, padH),
    cornerRadius = CornerRadius(24f, 24f)
  )
  scope.drawRoundRect(
    brush = Brush.verticalGradient(listOf(Color(0xFF263238), Color(0xFF000000))),
    topLeft = Offset(padX, padY),
    size = Size(padW, padH),
    cornerRadius = CornerRadius(24f, 24f)
  )
  // D-pad & Buttons
  scope.drawCircle(Color(0xFF00E5FF), padH * 0.16f, Offset(padX + padW * 0.25f, padY + padH * 0.5f))
  scope.drawCircle(Color(0xFFFF1744), padH * 0.14f, Offset(padX + padW * 0.75f, padY + padH * 0.35f))
  scope.drawCircle(Color(0xFFFFD600), padH * 0.14f, Offset(padX + padW * 0.85f, padY + padH * 0.65f))
}

// -------------------------------------------------------------
// Helper: 4-Point Star Burst (For 3D sparkle & shine)
// -------------------------------------------------------------
private fun drawStarBurst(scope: DrawScope, center: Offset, size: Float, color: Color) {
  val path = Path().apply {
    moveTo(center.x, center.y - size)
    quadraticBezierTo(center.x, center.y, center.x + size, center.y)
    quadraticBezierTo(center.x, center.y, center.x, center.y + size)
    quadraticBezierTo(center.x, center.y, center.x - size, center.y)
    quadraticBezierTo(center.x, center.y, center.x, center.y - size)
    close()
  }
  scope.drawPath(path, color)
  scope.drawCircle(Color.White, size * 0.25f, center)
}
