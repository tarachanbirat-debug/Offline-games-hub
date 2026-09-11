package com.example.ui.screens

import com.example.GamePlayerActivity
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudArcadeScreen() {
    val tabs = listOf("CrazyGames", "Gamezop", "GamePix")
    val urls = listOf(
        "https://www.crazygames.com/t/2d",
        "https://www.gamezop.com/?id=games",
        "https://m.gamepix.com/"
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isFullscreen by remember { mutableStateOf(false) }
    var activeWebView by remember { mutableStateOf<WebView?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Tabs (Hidden in Fullscreen mode so game doesn't get clipped)
            AnimatedVisibility(
                visible = !isFullscreen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFFFF6B6B)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    text = title, 
                                    color = if (selectedTabIndex == index) Color(0xFFFF6B6B) else Color(0xFF94A3B8)
                                ) 
                            }
                        )
                    }
                }
            }

            // Game Streaming WebView Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)

                            with(settings) {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                allowFileAccess = true
                                mediaPlaybackRequiresUserGesture = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                cacheMode = WebSettings.LOAD_DEFAULT
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                javaScriptCanOpenWindowsAutomatically = true
                                userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                            }
                            CookieManager.getInstance().setAcceptCookie(true)
                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                            webChromeClient = object : WebChromeClient() {
                                // Support HTML5 Fullscreen requests from games
                                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                    super.onShowCustomView(view, callback)
                                    isFullscreen = true
                                }

                                override fun onHideCustomView() {
                                    super.onHideCustomView()
                                    isFullscreen = false
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url.toString()
                                    if (url.startsWith("market://") || 
                                        url.startsWith("https://play.google.com/") || 
                                        url.startsWith("intent://")) {
                                        return true // Block external hijacking
                                    }

                                    // Automatically switch to immersive fullscreen GamePlayerActivity when opening a game
                                    val lower = url.lowercase()
                                    if (lower.contains("/game/") || lower.contains("/play/") || lower.contains("/g/") || lower.contains("/gameplay") || lower.contains("/room/")) {
                                        val context = view?.context
                                        if (context != null) {
                                            GamePlayerActivity.launch(
                                                context = context,
                                                gameId = "cloud_stream",
                                                title = "Cloud Stream",
                                                url = url,
                                                isOffline = false
                                            )
                                            return true
                                        }
                                    }
                                    return false
                                }
                            }
                            loadUrl(urls[selectedTabIndex])
                            activeWebView = this
                        }
                    },
                    update = { webView ->
                        activeWebView = webView
                        if (webView.url != urls[selectedTabIndex]) {
                            webView.loadUrl(urls[selectedTabIndex])
                        }
                    }
                )
            }
        }

        // Floating Fullscreen & Reload Controls Overlay (Always easily accessible)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = if (isFullscreen) 12.dp else 56.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Reload Button
            IconButton(
                onClick = { activeWebView?.reload() },
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xAA1E293B))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Fullscreen Toggle Button
            Button(
                onClick = { isFullscreen = !isFullscreen },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFullscreen) Color(0xFFFF3366) else Color(0xFF00D2FF)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "Toggle Fullscreen",
                    tint = if (isFullscreen) Color.White else Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isFullscreen) "Exit Full" else "Fullscreen",
                    color = if (isFullscreen) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

