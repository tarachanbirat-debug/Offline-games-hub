package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.theme.*

private const val ONLINE_ARCADE_ASSET_URL = "file:///android_asset/online_arcade/arcade.html"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OnlineGamesScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onBackToHub: () -> Unit,
  onOpenLocalGames: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var webView by remember { mutableStateOf<WebView?>(null) }
  var isGameActive by remember { mutableStateOf(false) }
  var activeGameTitle by remember { mutableStateOf<String?>(null) }
  var isFullscreen by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(true) }
  var loadProgress by remember { mutableIntStateOf(0) }
  var hasNetworkError by remember { mutableStateOf(false) }

  // Hardware/System Back Button interception
  BackHandler(enabled = true) {
    if (isGameActive) {
      soundEngine.playPop()
      hapticEngine.vibrateTap()
      webView?.evaluateJavascript("window.handleAndroidBack()", null)
      isGameActive = false
      activeGameTitle = null
    } else if (webView?.canGoBack() == true) {
      soundEngine.playPop()
      hapticEngine.vibrateTap()
      webView?.goBack()
    } else {
      onBackToHub()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    // 1. Primary WebView Container
    AndroidView(
      factory = { ctx ->
        WebView(ctx).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
          setLayerType(View.LAYER_TYPE_NONE, null)

          @Suppress("DEPRECATION")
          settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
            displayZoomControls = false
            builtInZoomControls = false
          }

          // Native Android to JS Bridge
          addJavascriptInterface(object {
            @JavascriptInterface
            fun onGameLaunched(title: String, url: String) {
              post {
                activeGameTitle = title
                isGameActive = true
                soundEngine.playSnap()
                hapticEngine.vibrateSuccess()
              }
            }

            @JavascriptInterface
            fun onGameExited() {
              post {
                activeGameTitle = null
                isGameActive = false
                soundEngine.playPop()
                hapticEngine.vibrateTap()
              }
            }

            @JavascriptInterface
            fun onBackToHome() {
              post {
                onBackToHub()
              }
            }

            @JavascriptInterface
            fun onOpenLocalGames() {
              post {
                soundEngine.playSnap()
                hapticEngine.vibrateTap()
                onOpenLocalGames()
              }
            }
          }, "AndroidBridge")

          webViewClient = object : WebViewClient() {
            override fun onRenderProcessGone(
              view: WebView?,
              detail: RenderProcessGoneDetail?
            ): Boolean {
              try {
                view?.let { wv ->
                  (wv.parent as? ViewGroup)?.removeView(wv)
                  wv.destroy()
                }
              } catch (_: Exception) {}
              webView = null
              return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              super.onPageStarted(view, url, favicon)
              isLoading = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              isLoading = false

              // Inject CSS and DOM Cleaner to remove any download buttons, Google Play banners, and headers
              // keeping purely the game content in pure full-screen!
              val cleanUpJs = """
                (function() {
                  var css = `
                    .download-section, 
                    .download-container,
                    .download-button,
                    .download-button-wrapper,
                    .google-play-button,
                    .qr-code-wrapper,
                    .qr-code-container,
                    .app-header,
                    .header-container,
                    .app-footer,
                    .footer-container,
                    [href*="play.google.com"],
                    [href*="market://"],
                    a[href*="games.ushareit.offlinegames"] {
                      display: none !important;
                      visibility: hidden !important;
                      height: 0 !important;
                      overflow: hidden !important;
                      pointer-events: none !important;
                    }
                    body, #app, main, .games-container, .games-section {
                      padding-top: 0 !important;
                      padding-bottom: 0 !important;
                      margin-top: 0 !important;
                      margin-bottom: 0 !important;
                    }
                  `;
                  var style = document.createElement('style');
                  style.type = 'text/css';
                  style.appendChild(document.createTextNode(css));
                  document.head.appendChild(style);

                  // Intercept and neutralize any clicks heading to Google Play or external app store
                  document.addEventListener('click', function(e) {
                    var target = e.target;
                    while (target && target !== document) {
                      if (target.tagName === 'A' && (target.href.includes('play.google.com') || target.href.includes('market://') || target.href.includes('games.ushareit'))) {
                        e.preventDefault();
                        e.stopPropagation();
                        return false;
                      }
                      target = target.parentNode;
                    }
                  }, true);
                })();
              """.trimIndent()
              view?.evaluateJavascript(cleanUpJs, null)
            }

            override fun onReceivedError(
              view: WebView?,
              request: WebResourceRequest?,
              error: WebResourceError?
            ) {
              super.onReceivedError(view, request, error)
              // Only trigger fallback if the asset page itself fails
              if (request?.isForMainFrame == true && request.url.toString().startsWith("file:///")) {
                hasNetworkError = true
                isLoading = false
              }
            }

            override fun shouldOverrideUrlLoading(
              view: WebView?,
              request: WebResourceRequest?
            ): Boolean {
              val targetUrl = request?.url?.toString() ?: return false
              
              // Block any attempts to redirect user to Google Play Store or external APK/app downloads
              if (targetUrl.contains("play.google.com") || 
                  targetUrl.contains("market://") || 
                  targetUrl.contains("games.ushareit.offlinegames") ||
                  targetUrl.endsWith(".apk")) {
                // Block silently so user stays inside our game app!
                return true
              }

              return if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://") || targetUrl.startsWith("file:///")) {
                false // Handle inside WebView / iframe
              } else {
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                  context.startActivity(intent)
                  true
                } catch (_: Exception) {
                  true
                }
              }
            }
          }

          webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
              loadProgress = newProgress
              if (newProgress >= 100) {
                isLoading = false
              }
            }
          }

          loadUrl(ONLINE_ARCADE_ASSET_URL)
          webView = this
        }
      },
      update = { view ->
        webView = view
      },
      onRelease = { view ->
        try {
          view.stopLoading()
          view.loadUrl("about:blank")
          (view.parent as? ViewGroup)?.removeView(view)
          view.destroy()
        } catch (_: Exception) {}
      },
      modifier = Modifier
        .fillMaxSize()
        .testTag("online_arcade_webview")
    )

    // 2. Loading Progress Indicator
    if (isLoading && loadProgress < 100) {
      LinearProgressIndicator(
        progress = { loadProgress / 100f },
        color = CandyCyan,
        trackColor = VaultSurfaceElevated,
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .align(Alignment.TopCenter)
      )
    }

    // 3. Floating "Back to Home" and "Full Screen" Buttons over the Iframe
    // Shown prominently whenever a game is active
    AnimatedVisibility(
      visible = isGameActive,
      enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .statusBarsPadding()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(30.dp))
          .background(VaultSurfaceElevated.copy(alpha = 0.92f))
          .border(1.5.dp, Brush.horizontalGradient(listOf(CandyCyan, CandyMint)), RoundedCornerShape(30.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Floating Back to Home button
        Button(
          onClick = {
            soundEngine.playPop()
            hapticEngine.vibrateTap()
            webView?.evaluateJavascript("window.handleAndroidBack()", null)
            isGameActive = false
            activeGameTitle = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = VaultCardDark),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          modifier = Modifier
            .border(1.dp, CandyWatermelon.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .testTag("floating_back_to_home")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Home",
            tint = CandyWatermelon,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "BACK TO HOME",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }

        // Active Game Title Pill
        activeGameTitle?.let { title ->
          Text(
            text = title,
            color = CandyMint,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
        }

        // Floating Full Screen Toggle button
        Button(
          onClick = {
            soundEngine.playTap()
            hapticEngine.vibrateTap()
            isFullscreen = !isFullscreen
            webView?.evaluateJavascript("window.toggleFullScreen()", null)
          },
          colors = ButtonDefaults.buttonColors(containerColor = CandyCyan),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          modifier = Modifier.testTag("floating_fullscreen_toggle")
        ) {
          Icon(
            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
            contentDescription = "Full Screen",
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isFullscreen) "EXIT FULL" else "FULL SCREEN",
            color = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }
    }

    // 4. Offline Fallback screen (in case asset or engine has error)
    if (hasNetworkError) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .background(VaultBackground)
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(CandyWatermelon.copy(alpha = 0.15f))
            .border(1.5.dp, CandyWatermelon, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PublicOff,
            contentDescription = null,
            tint = CandyWatermelon,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Online Arcade Portal",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 20.sp
        )

        Text(
          text = "Embed games require an active internet connection to load external iframes (GameDistribution, Itch.io, etc.).\n\nYou can also play our unlimited offline games without any Wi-Fi!",
          color = VaultTextSecondary,
          fontSize = 13.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Button(
            onClick = {
              soundEngine.playTap()
              hapticEngine.vibrateTap()
              hasNetworkError = false
              webView?.loadUrl(ONLINE_ARCADE_ASSET_URL)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CandyCyan),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text("RETRY", color = Color.Black, fontWeight = FontWeight.Black)
          }

          OutlinedButton(
            onClick = {
              soundEngine.playSnap()
              hapticEngine.vibrateTap()
              onBackToHub()
            },
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CandyMint)
          ) {
            Text("PLAY OFFLINE GAMES", color = CandyMint, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
