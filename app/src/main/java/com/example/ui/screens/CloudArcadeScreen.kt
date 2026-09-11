package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudArcadeScreen(
    onGameActiveChanged: (Boolean) -> Unit = {}
) {
    val view = LocalView.current

    // SECTION 2.1: Portal Endpoints
    val tabs = listOf("CrazyGames", "Gamezop", "Poki")
    val urls = listOf(
        "https://www.crazygames.com",
        "https://www.gamezop.com",
        "https://poki.com"
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var activeWebView by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableIntStateOf(0) }

    // SECTION 2.5: Navigation Safety with BackHandler
    BackHandler(enabled = activeWebView?.canGoBack() == true) {
        activeWebView?.goBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Portal Selector Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF10B981)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            selectedTabIndex = index
                        },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color(0xFF10B981) else Color(0xFF94A3B8),
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Web Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            // SECTION 2.2: Performance & Hardware Acceleration
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(android.graphics.Color.BLACK)

                            // SECTION 2.3: WebSettings Configuration
                            with(settings) {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                setSupportZoom(false)
                                builtInZoomControls = false
                                displayZoomControls = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                userAgentString =
                                    "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                                allowFileAccess = true
                                mediaPlaybackRequiresUserGesture = false
                                javaScriptCanOpenWindowsAutomatically = true
                            }
                            CookieManager.getInstance().setAcceptCookie(true)
                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    loadProgress = newProgress
                                    isLoading = newProgress < 85
                                }

                                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                    super.onShowCustomView(view, callback)
                                    onGameActiveChanged(true)
                                }

                                override fun onHideCustomView() {
                                    super.onHideCustomView()
                                    onGameActiveChanged(false)
                                }
                            }

                            // SECTION 2.4: Viewport Normalization
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false

                                    view?.evaluateJavascript(
                                        """
                                        (function() {
                                            var style = document.createElement('style');
                                            style.innerHTML = `
                                                html, body {
                                                    margin: 0 !important;
                                                    padding: 0 !important;
                                                    overflow-x: hidden !important;
                                                }
                                                canvas, iframe, #game-container, #canvas, .game-canvas {
                                                    max-width: 100vw !important;
                                                    max-height: 100vh !important;
                                                    object-fit: contain !important;
                                                    margin: auto !important;
                                                }
                                            `;
                                            document.head.appendChild(style);
                                        })();
                                        """.trimIndent(),
                                        null
                                    )
                                }

                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url.toString()
                                    if (url.startsWith("market://") ||
                                        url.startsWith("https://play.google.com/") ||
                                        url.startsWith("intent://")
                                    ) {
                                        return true // Intercept app store redirects
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

                // Neon progress indicator while loading
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xD9000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { loadProgress / 100f },
                                color = Color(0xFF10B981),
                                trackColor = Color(0x3310B981),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Loading Cloud Arcade ($loadProgress%)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Top-Right Refresh Button
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                activeWebView?.reload()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp)
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xCC1E293B))
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
