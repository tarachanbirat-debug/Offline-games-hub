package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Message
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex

data class CloudPortal(
    val id: String,
    val title: String,
    val url: String,
    val iconGlyph: String,
    val accentColor: Color
)

// SECTION 2: Tabs Priority (Poki -> Gamezop -> CrazyGames)
val CloudPortals = listOf(
    CloudPortal(
        id = "poki",
        title = "Poki",
        url = "https://poki.com",
        iconGlyph = "🎮",
        accentColor = Color(0xFF06B6D4)
    ),
    CloudPortal(
        id = "gamezop",
        title = "Gamezop",
        url = "https://www.gamezop.com",
        iconGlyph = "⚡",
        accentColor = Color(0xFFEC4899)
    ),
    CloudPortal(
        id = "crazygames",
        title = "CrazyGames",
        url = "https://www.crazygames.com",
        iconGlyph = "🕹️",
        accentColor = Color(0xFF6366F1)
    )
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudArcadeScreen(
    onGameActiveChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val configuration = LocalConfiguration.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var selectedPortalIndex by rememberSaveable { mutableIntStateOf(0) }
    val activePortal = CloudPortals[selectedPortalIndex]
    val activeUrl = activePortal.url

    var lastLoadedPortalIndex by rememberSaveable { mutableIntStateOf(-1) }
    var activeWebView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loadProgress by remember { mutableIntStateOf(0) }

    // SECTION 3: Smart Auto-Minimizing State
    var isManuallyCollapsed by rememberSaveable { mutableStateOf(false) }
    val isAutoCollapsed = isLandscape || isManuallyCollapsed

    // Notify parent to hide Scaffold bottom navigation when collapsed or in landscape
    LaunchedEffect(isAutoCollapsed) {
        onGameActiveChanged(isAutoCollapsed)
    }

    DisposableEffect(Unit) {
        onDispose {
            onGameActiveChanged(false)
            activeWebView?.apply {
                stopLoading()
                pauseTimers()
                loadUrl("about:blank")
                destroy()
            }
            activeWebView = null
        }
    }

    // Safe BackHandler: back inside game/portal first before leaving
    BackHandler(enabled = true) {
        if (activeWebView?.canGoBack() == true) {
            activeWebView?.goBack()
        } else if (isManuallyCollapsed) {
            isManuallyCollapsed = false
        } else if (selectedPortalIndex != 0) {
            selectedPortalIndex = 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ====================================================================
        // 60 FPS CHROME-GRADE WEBVIEW ENGINE
        // ====================================================================
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setLayerType(View.LAYER_TYPE_NONE, null)
                    setBackgroundColor(android.graphics.Color.BLACK)

                    // Essential for 3D game assets, cross-domain scripts, and session saves
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    with(settings) {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        setSupportMultipleWindows(true)
                        javaScriptCanOpenWindowsAutomatically = true
                        allowFileAccess = true
                        allowContentAccess = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        setSupportZoom(false)
                        displayZoomControls = false
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_DEFAULT
                        userAgentString =
                            "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                    }

                    // CRITICAL: Allows Poki & Gamezop game cards to launch inside the player on tap
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            loadProgress = newProgress
                            isLoading = newProgress < 100
                            canGoBack = view?.canGoBack() == true
                        }

                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: Message?
                        ): Boolean {
                            val href = view?.handler?.obtainMessage()
                            view?.requestFocusNodeHref(href)
                            val url = href?.data?.getString("url")
                            if (!url.isNullOrEmpty()) {
                                view.loadUrl(url)
                            }
                            val transport = resultMsg?.obj as? WebView.WebViewTransport
                            transport?.webView = view
                            resultMsg?.sendToTarget()
                            return true
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            canGoBack = view?.canGoBack() == true
                            val js = """
                                javascript:(function() {
                                    document.body.style.margin = '0';
                                    document.body.style.padding = '0';
                                })()
                            """.trimIndent()
                            view?.evaluateJavascript(js, null)
                            view?.requestLayout()
                            view?.invalidate()
                        }
                    }

                    loadUrl(activeUrl)
                    lastLoadedPortalIndex = selectedPortalIndex
                    activeWebView = this
                }
            },
            update = { webView ->
                activeWebView = webView
                if (lastLoadedPortalIndex != selectedPortalIndex) {
                    lastLoadedPortalIndex = selectedPortalIndex
                    webView.loadUrl(activeUrl)
                }
            }
        )

        // Web Loading Progress Bar
        if (isLoading) {
            LinearProgressIndicator(
                progress = { loadProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .zIndex(150f),
                color = activePortal.accentColor,
                trackColor = Color.Transparent
            )
        }

        // ====================================================================
        // SECTION 3: SMART AUTO-MINIMIZING TOP BAR (ZERO GAME OCCLUSION)
        // ====================================================================

        // (A) Normal Portrait State: Sleek 44dp height Tab Row
        AnimatedVisibility(
            visible = !isAutoCollapsed,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(100f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back in Portal button (if canGoBack)
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            if (activeWebView?.canGoBack() == true) {
                                activeWebView?.goBack()
                            }
                        },
                        enabled = canGoBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            tint = if (canGoBack) Color.White else Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 3 Primary Portal Tabs: Poki (Priority 1) | Gamezop (Priority 2) | CrazyGames (Priority 3)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CloudPortals.forEachIndexed { index, portal ->
                            val isSelected = selectedPortalIndex == index
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) portal.accentColor.copy(alpha = 0.25f)
                                        else Color.White.copy(alpha = 0.05f)
                                    )
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) portal.accentColor else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (selectedPortalIndex != index) {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            selectedPortalIndex = index
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = portal.iconGlyph,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = portal.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    // Quick Actions: Reload & Fullscreen Collapse
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                activeWebView?.reload()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                isManuallyCollapsed = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Enter Fullscreen Mode",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // (B) In-Game / Landscape State: Ultra-Slim 24dp Frosted Dark Pill Bar (Zero Game Occlusion)
        AnimatedVisibility(
            visible = isAutoCollapsed,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 4.dp)
                .zIndex(100f)
        ) {
            Row(
                modifier = Modifier
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Compact Switcher Tabs
                CloudPortals.forEachIndexed { index, portal ->
                    val isSelected = selectedPortalIndex == index
                    Text(
                        text = "${portal.iconGlyph} ${portal.title}",
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) portal.accentColor else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (selectedPortalIndex != index) {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    selectedPortalIndex = index
                                }
                            }
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                    if (index < CloudPortals.size - 1) {
                        Text(
                            text = "|",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }

                // If collapsed manually in portrait, allow restore button
                if (!isLandscape && isManuallyCollapsed) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "|",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.FullscreenExit,
                        contentDescription = "Exit Fullscreen",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(14.dp)
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                isManuallyCollapsed = false
                            }
                    )
                }
            }
        }

        // Floating 30dp Switcher Handle (if user wants to quickly navigate back inside the game)
        if (isAutoCollapsed && canGoBack) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 8.dp, top = 4.dp)
                    .zIndex(100f)
            ) {
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        activeWebView?.goBack()
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.70f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
