package com.example

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.network.NetworkChecker
import com.example.ui.GameVaultApp
import com.example.ui.theme.GameVaultTheme

class MainActivity : ComponentActivity() {
  private var prewarmedWebView1: WebView? = null
  private var prewarmedWebView2: WebView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val isOnline = NetworkChecker.isOnline(applicationContext)
    val switchToOffline = intent.getBooleanExtra("extra_switch_to_offline", false)

    // Background WebView Pre-warming for instant load (Zero Buffering)
    try {
      prewarmedWebView1 = WebView(applicationContext).apply {
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          databaseEnabled = true
          cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
          mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        loadUrl("https://pub.gamezop.com/")
      }
      prewarmedWebView2 = WebView(applicationContext).apply {
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          databaseEnabled = true
          cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
          mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        loadUrl("https://games.gamepix.com/")
      }
    } catch (_: Exception) {}

    setContent {
      GameVaultTheme {
        GameVaultApp(initialIsOnline = isOnline, switchToOffline = switchToOffline)
      }
    }
  }

  override fun onDestroy() {
    try {
      prewarmedWebView1?.destroy()
      prewarmedWebView2?.destroy()
    } catch (_: Exception) {}
    super.onDestroy()
  }
}

