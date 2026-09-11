package com.example

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.audio.VaultLofiEngine

/**
 * Bulletproof GamePlayer Engine for HTML5, WebGL, Canvas, and WebAssembly Games.
 * - Hardware Accelerated (60 - 120 FPS)
 * - Hard Anti-Redirect Guard (blocks Google Play, market://, external intents)
 * - Immersive Fullscreen Gaming Mode
 * - In-Game Exit Confirmation Intercept
 */
class GamePlayerActivity : ComponentActivity() {

  companion object {
    const val EXTRA_GAME_ID = "extra_game_id"
    const val EXTRA_GAME_TITLE = "extra_game_title"
    const val EXTRA_GAME_URL = "extra_game_url"
    const val EXTRA_IS_OFFLINE = "extra_is_offline"

    fun launch(
      context: Context,
      gameId: String,
      title: String,
      url: String,
      isOffline: Boolean = false
    ) {
      val intent = Intent(context, GamePlayerActivity::class.java).apply {
        putExtra(EXTRA_GAME_ID, gameId)
        putExtra(EXTRA_GAME_TITLE, title)
        putExtra(EXTRA_GAME_URL, url)
        putExtra(EXTRA_IS_OFFLINE, isOffline)
      }
      context.startActivity(intent)
    }
  }

  private var webView: WebView? = null
  private var lofiEngine: VaultLofiEngine? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // 1. Enable Hardware Acceleration & Keep Screen On for uninterrupted gaming
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    // 2. Set Immersive Mode
    setupImmersiveMode()

    val gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: "game"
    val gameTitle = intent.getStringExtra(EXTRA_GAME_TITLE) ?: "Gaming Arena"
    val gameUrl = intent.getStringExtra(EXTRA_GAME_URL) ?: "about:blank"
    val isOffline = intent.getBooleanExtra(EXTRA_IS_OFFLINE, false)

    lofiEngine = VaultLofiEngine(this).apply {
      startLofi(VaultLofiEngine.LofiMood.CYBER_SYNTH)
    }

    setContent {
      GamePlayerScreen(
        gameId = gameId,
        gameTitle = gameTitle,
        gameUrl = gameUrl,
        isOffline = isOffline,
        onExitGame = { finish() },
        onAttachWebView = { wv -> webView = wv }
      )
    }
  }

  private fun setupImmersiveMode() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller.hide(WindowInsetsCompat.Type.systemBars())
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      setupImmersiveMode()
    }
  }

  override fun onPause() {
    webView?.onPause()
    webView?.pauseTimers()
    super.onPause()
  }

  override fun onResume() {
    super.onResume()
    setupImmersiveMode()
    webView?.onResume()
    webView?.resumeTimers()
  }

  override fun onDestroy() {
    lofiEngine?.stopLofi()
    lofiEngine = null
    webView?.let { wv ->
      wv.stopLoading()
      wv.loadUrl("about:blank")
      wv.destroy()
    }
    webView = null
    super.onDestroy()
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun GamePlayerScreen(
  gameId: String,
  gameTitle: String,
  gameUrl: String,
  isOffline: Boolean,
  onExitGame: () -> Unit,
  onAttachWebView: (WebView) -> Unit
) {
  val context = LocalContext.current
  var showExitDialog by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(true) }
  var loadProgress by remember { mutableIntStateOf(0) }
  var hasError by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf("") }
  var activeWebView by remember { mutableStateOf<WebView?>(null) }

  // Intercept back button to show the Exit Confirmation Dialog
  androidx.activity.compose.BackHandler {
    showExitDialog = true
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F0B1E))
  ) {
    // 1. Core Gaming WebView Engine
    AndroidView(
      factory = { ctx ->
        WebView(ctx).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )

          // Hardware Layer for 60 - 120 FPS
          setLayerType(View.LAYER_TYPE_HARDWARE, null)

          // Required Game Engine WebSettings
          settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT

            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true

            useWideViewPort = true
            loadWithOverviewMode = true
            displayZoomControls = false
            builtInZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
          }

          webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
              loadProgress = newProgress
              if (newProgress >= 90) {
                isLoading = false
              }
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
              return true
            }
          }

          webViewClient = object : WebViewClient() {
            // HARD ANTI-REDIRECT GUARD:
            // Intercept & block external app store pushes, market://, play.google.com, intent://
            override fun shouldOverrideUrlLoading(
              view: WebView?,
              request: WebResourceRequest?
            ): Boolean {
              val targetUrl = request?.url?.toString() ?: return false
              val lower = targetUrl.lowercase()

              // Block Play Store and App Market intents
              if (lower.startsWith("market://") ||
                lower.startsWith("https://play.google.com/") ||
                lower.startsWith("http://play.google.com/") ||
                lower.startsWith("intent://")
              ) {
                return true // Intercepted: Do not open external app store
              }

              // Allow standard http, https, file, and data schemes
              if (lower.startsWith("http://") ||
                lower.startsWith("https://") ||
                lower.startsWith("file://") ||
                lower.startsWith("data:")
              ) {
                return false // Allow WebView to navigate inside the game
              }

              // Guard any unknown schemes from crashing or redirecting out
              return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              super.onPageStarted(view, url, favicon)
              isLoading = true
              hasError = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              isLoading = false

              // Inject anti-ad & fullscreen layout clean-up script
              view?.evaluateJavascript(
                """
                (function() {
                  var style = document.createElement('style');
                  style.innerHTML = `
                    .download-button, .google-play, [href*="play.google"], [href*="market://"],
                    .adsbygoogle, .ad-banner, #ad-container { display: none !important; }
                    body { margin: 0; padding: 0; overflow: hidden; }
                  `;
                  document.head.appendChild(style);
                })();
                """.trimIndent(),
                null
              )
            }

            override fun onReceivedError(
              view: WebView?,
              request: WebResourceRequest?,
              error: WebResourceError?
            ) {
              if (request?.isForMainFrame == true) {
                hasError = true
                errorMessage = error?.description?.toString() ?: "Failed to load game"
                isLoading = false
              }
            }
          }

          // Load local asset bundle or remote HTML5 game URL
          loadUrl(gameUrl)

          activeWebView = this
          onAttachWebView(this)
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    // 2. Loading Indicator Overlay
    if (isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF0F0B1ECC)),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          CircularProgressIndicator(
            progress = { loadProgress / 100f },
            color = Color(0xFF00D2FF),
            trackColor = Color(0x3300D2FF),
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Launching $gameTitle...",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Loading game assets ($loadProgress%)",
            color = Color(0xFFA59BC8),
            fontSize = 12.sp
          )
        }
      }
    }

    // 3. Error Fallback Card
    if (hasError) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF0F0B1E)),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1638)),
          modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.SportsEsports,
              contentDescription = null,
              tint = Color(0xFFFF3366),
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Unable to Stream Game",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = errorMessage.ifBlank { "Check your network connection to stream this web game." },
              color = Color(0xFFA59BC8),
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              OutlinedButton(
                onClick = onExitGame,
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Back to Hub", color = Color.White)
              }
              Button(
                onClick = {
                  hasError = false
                  isLoading = true
                  activeWebView?.reload()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D2FF)),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Retry", color = Color.Black, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 4. Subtle Floating Top Navigation Controls (Auto-hiding overlay)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Exit / Back Button
      IconButton(
        onClick = { showExitDialog = true },
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(Color(0x88000000))
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Exit Game",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }

      // Refresh Button
      IconButton(
        onClick = {
          isLoading = true
          activeWebView?.reload()
        },
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(Color(0x88000000))
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reload Game",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // 5. In-Game Exit Confirmation Dialog
    if (showExitDialog) {
      Dialog(onDismissRequest = { showExitDialog = false }) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1638)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0x22FF3366)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color(0xFFFF3366),
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "Quit Gameplay?",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 19.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Are you sure you want to return to the Game Hub? Any unsaved round progress will be lost.",
              color = Color(0xFFA59BC8),
              fontSize = 13.sp,
              lineHeight = 18.sp,
              modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { showExitDialog = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
              ) {
                Text("Keep Playing", fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
              Button(
                onClick = {
                  showExitDialog = false
                  val intent = Intent(context, MainActivity::class.java).apply {
                    putExtra("extra_switch_to_offline", true)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                  }
                  context.startActivity(intent)
                  (context as? ComponentActivity)?.finish()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D2FF))
              ) {
                Text("Offline", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
              Button(
                onClick = {
                  showExitDialog = false
                  onExitGame()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366))
              ) {
                Text("Exit Hub", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
  }
}
