package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.adapters.AdapterRegistry
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.data.GameVaultRepository
import com.example.model.GameItem
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
  repository: GameVaultRepository,
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  games: List<GameItem>,
  modifier: Modifier = Modifier
) {
  var soundEnabled by remember { mutableStateOf(repository.getSoundEnabled()) }
  var hapticsEnabled by remember { mutableStateOf(repository.getHapticsEnabled()) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
  ) {
    item {
      Column {
        Text(
          text = "PLATFORM SETTINGS",
          color = CandyLemon,
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          letterSpacing = 1.2.sp
        )
        Text(
          text = "Vault Configuration",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 24.sp
        )
        Text(
          text = "Control hardware synthesis, sensory vibration, adapter pipelines, and local database storage.",
          color = VaultTextSecondary,
          fontSize = 13.sp,
          lineHeight = 17.sp,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }

    // Audio & Haptics Toggles
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorderGlow, RoundedCornerShape(22.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text("SENSORY FEEDBACK", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)

        // Sound Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = CandyCyan)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Procedural Sound Synthesis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Real-time AudioTrack waveform synthesis", color = VaultTextSecondary, fontSize = 11.sp)
            }
          }
          Switch(
            checked = soundEnabled,
            onCheckedChange = {
              soundEnabled = it
              repository.setSoundEnabled(it)
              soundEngine.isMuted = !it
              if (it) soundEngine.playPop()
            },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CandyCyan),
            modifier = Modifier.testTag("settings_sound_switch")
          )
        }

        Divider(color = VaultBorder)

        // Haptic Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Vibration, contentDescription = null, tint = CandyWatermelon)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Haptic Feedback Engine", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Tactile impulse vibration on tap & score", color = VaultTextSecondary, fontSize = 11.sp)
            }
          }
          Switch(
            checked = hapticsEnabled,
            onCheckedChange = {
              hapticsEnabled = it
              repository.setHapticsEnabled(it)
              hapticEngine.isMuted = !it
              if (it) hapticEngine.vibrateSuccess()
            },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CandyWatermelon),
            modifier = Modifier.testTag("settings_haptic_switch")
          )
        }
      }
    }

    // Adapter Architecture Status
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorderGlow, RoundedCornerShape(22.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("MODULAR ADAPTER ARCHITECTURE", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)

        AdapterRegistry.adapters.forEach { adapter ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(VaultCardDark)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(adapter.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(adapter.supportedTech, color = VaultTextSecondary, fontSize = 11.sp)
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CandyMint.copy(alpha = 0.2f))
                .border(1.dp, CandyMint, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("ACTIVE", color = CandyMint, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Vault Storage & Statistics
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorderGlow, RoundedCornerShape(22.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text("VAULT DATABASE STORAGE", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Installed Games in Vault:", color = VaultTextSecondary, fontSize = 13.sp)
          Text("${games.size} Titles", color = CandyLemon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Offline Ready Titles:", color = VaultTextSecondary, fontSize = 13.sp)
          Text("${games.count { it.offlineMode }} / ${games.size}", color = CandyMint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Total Combined Plays:", color = VaultTextSecondary, fontSize = 13.sp)
          Text("${games.sumOf { it.totalPlays }} Sessions", color = CandyCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Persistence Engine:", color = VaultTextSecondary, fontSize = 13.sp)
          Text("Room SQLite KSP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
