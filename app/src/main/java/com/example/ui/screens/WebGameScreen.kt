package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.model.GameItem
import com.example.ui.components.UniversalGameHeader
import com.example.ui.theme.CandyCyan
import com.example.ui.theme.VaultBackground

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebGameScreen(
  game: GameItem,
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onBack: () -> Unit
) {
  var isLoading by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    UniversalGameHeader(
      title = game.title,
      score = 0,
      bestScore = game.highScore,
      difficulty = game.difficulty,
      soundEnabled = !soundEngine.isMuted,
      hapticsEnabled = !hapticEngine.isMuted,
      onBack = onBack,
      onReset = { /* web reset */ },
      onToggleSound = { soundEngine.isMuted = !soundEngine.isMuted },
      onToggleHaptics = { hapticEngine.isMuted = !hapticEngine.isMuted }
    )

    Box(modifier = Modifier.fillMaxSize()) {
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
              override fun onPageFinished(view: WebView?, url: String?) {
                isLoading = false
              }
            }

            // Load sandboxed HTML5 / Canvas bundle or web runner
            val htmlContent = """
              <!DOCTYPE html>
              <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
                <style>
                  body { margin: 0; padding: 0; background: #1E1638; color: #fff; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; font-family: sans-serif; }
                  canvas { background: #150F26; border: 2px solid #00D2FF; border-radius: 16px; touch-action: none; }
                  h2 { color: #FFD600; margin-bottom: 8px; }
                  p { color: #A59BC8; font-size: 14px; text-align: center; max-width: 80%; }
                </style>
              </head>
              <body>
                <h2>${game.title}</h2>
                <p>Sandboxed ${game.technology} Runtime Engine Active</p>
                <canvas id="gameCanvas" width="300" height="300"></canvas>
                <script>
                  const canvas = document.getElementById('gameCanvas');
                  const ctx = canvas.getContext('2d');
                  let angle = 0;
                  function render() {
                    ctx.clearRect(0, 0, 300, 300);
                    ctx.save();
                    ctx.translate(150, 150);
                    ctx.rotate(angle);
                    ctx.fillStyle = '#00E699';
                    ctx.fillRect(-40, -40, 80, 80);
                    ctx.fillStyle = '#FF3366';
                    ctx.beginPath();
                    ctx.arc(0, 0, 25, 0, Math.PI * 2);
                    ctx.fill();
                    ctx.restore();
                    angle += 0.03;
                    requestAnimationFrame(render);
                  }
                  render();
                </script>
              </body>
              </html>
            """.trimIndent()

            loadDataWithBaseURL("https://gamevault.local", htmlContent, "text/html", "UTF-8", null)
          }
        },
        modifier = Modifier.fillMaxSize()
      )

      if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = CandyCyan)
        }
      }
    }
  }
}
