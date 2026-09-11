package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.theme.*

private const val GAMEZOP_PORTAL_URL = "https://games.gamezop.com/"
private const val GAMEDISTRIBUTION_PORTAL_URL = "https://html5.gamedistribution.com/"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudArcadeScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onBackToHub: () -> Unit,
  modifier: Modifier = Modifier
) {
  var activePortalUrl by remember { mutableStateOf(GAMEZOP_PORTAL_URL) }
  var webView by remember { mutableStateOf<WebView?>(null) }
  var isLoading by remember { mutableStateOf(true) }
  var loadProgress by remember { mutableIntStateOf(0) }
  var hasNetworkError by remember { mutableStateOf(false) }

  // Intercept back navigation inside Cloud Arcade WebView
  BackHandler(enabled = true) {
    if (webView?.canGoBack() == true) {
      soundEngine.playPop()
      hapticEngine.vibrateTap()
      webView?.goBack()
    } else {
      onBackToHub()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
      .systemBarsPadding()
  ) {
    // 1. Single-Pane Cloud Arcade Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBackToHub,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(VaultSurfaceElevated)
            .testTag("cloud_arcade_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Cloud Arcade Stream",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
          )
          Text(
            text = if (activePortalUrl.contains("gamezop")) "Gamezop Instant Stream" else "GameDistribution Portal",
            color = CandyCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }

      // Portal Switcher Chips & Reload Button
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Switch between Gamezop & GameDistribution
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (activePortalUrl.contains("gamezop")) CandyCyan else VaultSurfaceElevated)
            .clickable {
              if (!activePortalUrl.contains("gamezop")) {
                activePortalUrl = GAMEZOP_PORTAL_URL
                webView?.loadUrl(GAMEZOP_PORTAL_URL)
              }
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "GAMEZOP",
            color = if (activePortalUrl.contains("gamezop")) Color.Black else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (!activePortalUrl.contains("gamezop")) CandyCyan else VaultSurfaceElevated)
            .clickable {
              if (activePortalUrl.contains("gamezop")) {
                activePortalUrl = GAMEDISTRIBUTION_PORTAL_URL
                webView?.loadUrl(GAMEDISTRIBUTION_PORTAL_URL)
              }
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "GD HUB",
            color = if (!activePortalUrl.contains("gamezop")) Color.Black else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          )
        }

        IconButton(
          onClick = {
            isLoading = true
            webView?.reload()
          },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(VaultSurfaceElevated)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reload",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // 2. Loading progress bar
    if (isLoading) {
      LinearProgressIndicator(
        progress = { loadProgress / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp),
        color = CandyCyan,
        trackColor = VaultSurfaceElevated
      )
    }

    // 3. Full-Viewport Dedicated Gaming WebView
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Hardware layer for optimal frame rates
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

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
            }

            webViewClient = object : WebViewClient() {
              // HARD ANTI-REDIRECT GUARD:
              // Strip external Play Store and download APK triggers
              override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
              ): Boolean {
                val target = request?.url?.toString() ?: return false
                val lower = target.lowercase()

                if (lower.startsWith("market://") ||
                  lower.startsWith("https://play.google.com/") ||
                  lower.startsWith("http://play.google.com/") ||
                  lower.startsWith("intent://") ||
                  lower.endsWith(".apk")
                ) {
                  return true // Intercept: Never leave the app!
                }

                if (lower.startsWith("http://") ||
                  lower.startsWith("https://") ||
                  lower.startsWith("file://")
                ) {
                  return false
                }

                return true
              }

              override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
                hasNetworkError = false
              }

              override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false

                // Inject DOM sanitizer removing download buttons and promotional app banners
                val cleanJs = """
                  (function() {
                    var style = document.createElement('style');
                    style.innerHTML = `
                      .download-button, .app-download-banner, .google-play,
                      [href*="play.google"], [href*="market://"], .install-app-banner {
                        display: none !important;
                      }
                      body { margin: 0; padding: 0; }
                    `;
                    document.head.appendChild(style);
                  })();
                """.trimIndent()
                view?.evaluateJavascript(cleanJs, null)
              }

              override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
              ) {
                if (request?.isForMainFrame == true) {
                  hasNetworkError = true
                  isLoading = false
                }
              }
            }

            loadUrl(activePortalUrl)
            webView = this
          }
        },
        modifier = Modifier.fillMaxSize()
      )

      // Network error state
      if (hasNetworkError) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(VaultBackground),
          contentAlignment = Alignment.Center
        ) {
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
            modifier = Modifier.padding(24.dp)
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = CandyWatermelon,
                modifier = Modifier.size(44.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Cloud Stream Offline",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Please check your network connection to stream Cloud Arcade games.",
                color = VaultTextMuted,
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.height(18.dp))
              Button(
                onClick = {
                  hasNetworkError = false
                  isLoading = true
                  webView?.reload()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CandyCyan),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Retry Connection", color = Color.Black, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
