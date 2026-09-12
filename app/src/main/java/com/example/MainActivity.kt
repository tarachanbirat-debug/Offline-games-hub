package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.network.NetworkChecker
import com.example.ui.GameVaultApp
import com.example.ui.theme.GameVaultTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val isOnline = NetworkChecker.isOnline(applicationContext)
    val switchToOffline = intent.getBooleanExtra("extra_switch_to_offline", false)

    setContent {
      GameVaultTheme {
        GameVaultApp(initialIsOnline = isOnline, switchToOffline = switchToOffline)
      }
    }
  }
}

