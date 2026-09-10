package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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

private const val ONLINE_GAMES_PORTAL_URL = "https://offlinegames.wshareit.com/"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OnlineGamesScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onBackToHub: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var webView by remember { mutableStateOf<WebView?>(null) }
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(true) }
  var loadProgress by remember { mutableIntStateOf(0) }
  var hasNetworkError by remember { mutableStateOf(false) }
  var isFullscreen by remember { mutableStateOf(false) }
  var currentUrl by remember { mutableStateOf(ONLINE_GAMES_PORTAL_URL) }

  // Hardware/System Back button intercepts web navigation history
  BackHandler(enabled = canGoBack && !hasNetworkError) {
    soundEngine.playPop()
    hapticEngine.vibrateTap()
    webView?.goBack()
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
  ) {
    // 1. Top Portal Header (Hide in fullscreen mode to maximize game area)
    AnimatedVisibility(
      visible = !isFullscreen,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorderGlow)
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Brand / Title
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(listOf(CandyCyan, CandySkyBlue, CandyGrape))
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "ONLINE ARCADE",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp,
                  letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CandyLemon.copy(alpha = 0.2f))
                    .border(1.dp, CandyLemon.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "100+ GAMES",
                    color = CandyLemon,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.sp
                  )
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "SSL Secure",
                  tint = CandyMint,
                  modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "offlinegames.wshareit.com",
                  color = CandyMint,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
          }

          // Right: Action controls (Home, Fullscreen, Open in Browser)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Fullscreen toggle
            IconButton(
              onClick = {
                soundEngine.playTap()
                hapticEngine.vibrateTap()
                isFullscreen = true
              },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VaultCardDark)
                .testTag("online_fullscreen_toggle")
            ) {
              Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "Fullscreen",
                tint = CandySkyBlue,
                modifier = Modifier.size(18.dp)
              )
            }

            // Open in external browser
            IconButton(
              onClick = {
                soundEngine.playTap()
                hapticEngine.vibrateTap()
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                  context.startActivity(intent)
                } catch (_: Exception) { }
              },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VaultCardDark)
                .testTag("online_open_browser")
            ) {
              Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "Open in Browser",
                tint = CandyLemon,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Bar: Back, Forward, Refresh, Home
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VaultCardDark)
            .border(1.dp, VaultBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Back button
            IconButton(
              onClick = {
                if (canGoBack) {
                  soundEngine.playPop()
                  hapticEngine.vibrateTap()
                  webView?.goBack()
                } else {
                  onBackToHub()
                }
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (canGoBack) Color.White else VaultTextMuted,
                modifier = Modifier.size(18.dp)
              )
            }

            // Forward button
            IconButton(
              onClick = {
                if (canGoForward) {
                  soundEngine.playTap()
                  hapticEngine.vibrateTap()
                  webView?.goForward()
                }
              },
              enabled = canGoForward,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Forward",
                tint = if (canGoForward) Color.White else VaultTextMuted,
                modifier = Modifier.size(18.dp)
              )
            }

            // Reload button
            IconButton(
              onClick = {
                soundEngine.playTap()
                hapticEngine.vibrateTap()
                hasNetworkError = false
                webView?.reload()
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reload",
                tint = CandyCyan,
                modifier = Modifier.size(18.dp)
              )
            }

            // Home button (Back to portal homepage)
            IconButton(
              onClick = {
                soundEngine.playSnap()
                hapticEngine.vibrateSuccess()
                hasNetworkError = false
                webView?.loadUrl(ONLINE_GAMES_PORTAL_URL)
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = CandyMint,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          // Connection status pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(CandyMint.copy(alpha = 0.15f))
              .border(1.dp, CandyMint.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(CandyMint)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "LIVE CLOUD",
                color = CandyMint,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp
              )
            }
          }
        }

        // Quick Category Chips
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "🔥 All Games" to ONLINE_GAMES_PORTAL_URL,
            "🧩 Puzzle" to "$ONLINE_GAMES_PORTAL_URL#puzzle",
            "🏎️ Racing" to "$ONLINE_GAMES_PORTAL_URL#racing",
            "🎯 Arcade" to "$ONLINE_GAMES_PORTAL_URL#arcade",
            "🃏 Cards" to "$ONLINE_GAMES_PORTAL_URL#cards",
            "⚽ Sports" to "$ONLINE_GAMES_PORTAL_URL#sports",
            "🧠 Match" to "$ONLINE_GAMES_PORTAL_URL#match"
          ).forEach { (label, url) ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(VaultCardDark)
                .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
                .clickable {
                  soundEngine.playTap()
                  hapticEngine.vibrateTap()
                  hasNetworkError = false
                  webView?.loadUrl(url)
                }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // Floating Exit Fullscreen Button when in Fullscreen Mode
    if (isFullscreen) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        contentAlignment = Alignment.TopEnd
      ) {
        Button(
          onClick = {
            soundEngine.playTap()
            hapticEngine.vibrateTap()
            isFullscreen = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = VaultSurfaceElevated.copy(alpha = 0.9f)),
          shape = RoundedCornerShape(16.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.border(1.dp, CandyCyan, RoundedCornerShape(16.dp))
        ) {
          Icon(Icons.Default.FullscreenExit, contentDescription = null, tint = CandyCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("EXIT FULLSCREEN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
      }
    }

    // Loading Progress Indicator
    if (isLoading && loadProgress < 100) {
      LinearProgressIndicator(
        progress = { loadProgress / 100f },
        color = CandyCyan,
        trackColor = VaultSurfaceElevated,
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
      )
    }

    // 2. Main Content: WebView or Offline Error Fallback
    Box(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f)
    ) {
      if (hasNetworkError) {
        // Offline / No Internet Connection Fallback UI
        Column(
          modifier = Modifier
            .fillMaxSize()
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
              imageVector = Icons.Default.WifiOff,
              contentDescription = null,
              tint = CandyWatermelon,
              modifier = Modifier.size(36.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Network Connection Needed",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )

          Text(
            text = "The Online Games portal (offlinegames.wshareit.com) requires an active internet connection to load new games.\n\nYou can also play our 10 native offline games anytime without Wi-Fi!",
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
                webView?.reload()
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
      } else {
        // High Performance Android WebView
        AndroidView(
          factory = { ctx ->
            WebView(ctx).apply {
              layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
              )

              settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                useWideViewPort = true
                loadWithOverviewMode = true
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_DEFAULT
                displayZoomControls = false
                builtInZoomControls = false
              }

              webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                  super.onPageStarted(view, url, favicon)
                  isLoading = true
                  url?.let { currentUrl = it }
                  canGoBack = view?.canGoBack() == true
                  canGoForward = view?.canGoForward() == true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                  super.onPageFinished(view, url)
                  isLoading = false
                  url?.let { currentUrl = it }
                  canGoBack = view?.canGoBack() == true
                  canGoForward = view?.canGoForward() == true
                }

                override fun onReceivedError(
                  view: WebView?,
                  request: WebResourceRequest?,
                  error: WebResourceError?
                ) {
                  super.onReceivedError(view, request, error)
                  if (request?.isForMainFrame == true) {
                    hasNetworkError = true
                    isLoading = false
                  }
                }

                override fun shouldOverrideUrlLoading(
                  view: WebView?,
                  request: WebResourceRequest?
                ): Boolean {
                  val targetUrl = request?.url?.toString() ?: return false
                  return if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")) {
                    currentUrl = targetUrl
                    false // Handle inside WebView
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

              loadUrl(ONLINE_GAMES_PORTAL_URL)
              webView = this
            }
          },
          update = { view ->
            webView = view
            canGoBack = view.canGoBack()
            canGoForward = view.canGoForward()
          },
          modifier = Modifier
            .fillMaxSize()
            .testTag("online_arcade_webview")
        )

        // Loading Overlay Spinner for initial load
        if (isLoading && loadProgress < 40) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(VaultBackground.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(
                color = CandyCyan,
                modifier = Modifier.size(42.dp),
                strokeWidth = 3.5.dp
              )
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "Connecting to Online Arcade...",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "Loading offlinegames.wshareit.com",
                color = VaultTextSecondary,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }
  }
}
