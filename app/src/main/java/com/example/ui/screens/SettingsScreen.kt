package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
  currentThemeId: String,
  onThemeChanged: (String) -> Unit,
  onOpenWelcome: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  var soundEnabled by remember { mutableStateOf(repository.getSoundEnabled()) }
  var hapticsEnabled by remember { mutableStateOf(repository.getHapticsEnabled()) }
  var selectedCategoryFilter by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }

  val categories = listOf("All", "Pop & Modern", "Classic Retro", "Neon & Cyber", "Mood & Performance")
  val filteredPresets = remember(selectedCategoryFilter, searchQuery) {
    VaultThemeManager.presets.filter { preset ->
      val matchesCategory = selectedCategoryFilter == "All" || preset.category == selectedCategoryFilter
      val matchesSearch = searchQuery.isBlank() || preset.name.contains(searchQuery, ignoreCase = true)
      matchesCategory && matchesSearch
    }
  }

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
          text = "GAME SETTINGS",
          color = CandyLemon,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp,
          letterSpacing = 1.2.sp
        )
        Text(
          text = "Audio & Experience",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 24.sp
        )
        Text(
          text = "Customize your gaming audio effects, vibration feedback, and view your game stats.",
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
          .border(1.dp, VaultBorder, RoundedCornerShape(22.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text("AUDIO & VIBRATION", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)

        // Sound Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = CandyCyan)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Game Sound Effects", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Arcade pops, snaps, victory fanfares", color = VaultTextSecondary, fontSize = 11.sp)
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
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CandyMint),
            modifier = Modifier.testTag("settings_sound_switch")
          )
        }

        HorizontalDivider(color = VaultBorder)

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
              Text("Haptic Feedback", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Tactile vibration on tap, moves, & wins", color = VaultTextSecondary, fontSize = 11.sp)
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

    // 50+ Themes & Mood Customizer Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorder, RoundedCornerShape(22.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("50+ THEMES & MOODS", color = CandyLemon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("Custom Arcade Vibe", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
          }
          Badge(containerColor = CandyMint) {
            Text("50+ Presets", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
          }
        }

        // Category Filter Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = selectedCategoryFilter == cat
            FilterChip(
              selected = isSelected,
              onClick = {
                soundEngine.playPop()
                hapticEngine.vibrateTap()
                selectedCategoryFilter = cat
              },
              label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CandyCyan,
                selectedLabelColor = Color.Black,
                containerColor = VaultSurface,
                labelColor = VaultTextSecondary
              )
            )
          }
        }

        // Search Field for Themes
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search themes (e.g. Retro, Cyber, Zen)...", fontSize = 12.sp, color = VaultTextMuted) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CandyCyan) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CandyCyan,
            unfocusedBorderColor = VaultBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = CandyCyan
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Themes Grid / List (Showing filtered presets)
        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          filteredPresets.take(20).forEach { preset ->
            val isCurrent = preset.id == currentThemeId
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isCurrent) preset.primaryAccent.copy(alpha = 0.2f) else VaultSurface)
                .border(
                  width = if (isCurrent) 2.dp else 1.dp,
                  color = if (isCurrent) preset.primaryAccent else VaultBorder,
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable {
                  soundEngine.playSnap()
                  hapticEngine.vibrateSuccess()
                  repository.setSelectedThemeId(preset.id)
                  onThemeChanged(preset.id)
                }
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                // Color swatches indicator
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                  Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(preset.primaryAccent))
                  Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(preset.secondaryAccent))
                  Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(preset.backgroundColor))
                }
                Column {
                  Text(
                    text = preset.name,
                    color = if (isCurrent) Color.White else VaultTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Text(
                    text = preset.category,
                    color = preset.primaryAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }

              if (isCurrent) {
                Badge(containerColor = preset.primaryAccent) {
                  Text("ACTIVE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
              } else {
                Text("Select", color = VaultTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Gaming Stats
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorder, RoundedCornerShape(22.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("YOUR GAMING STATS", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Ready to Play Games:", color = VaultTextSecondary, fontSize = 13.sp)
          Text("${games.size} Games (100% Offline)", color = CandyMint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Total Sessions Played:", color = VaultTextSecondary, fontSize = 13.sp)
          Text("${games.sumOf { it.totalPlays }} Plays", color = CandyCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Favorite Games:", color = VaultTextSecondary, fontSize = 13.sp)
          Text("${games.count { it.isFavorite }} Games", color = CandyLemon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // About Game Station & Developer
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, VaultBorder, RoundedCornerShape(22.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("CREATOR & INFO", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("Offline Game Station 2.0", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
        Text(
          "Unlimited games built with native physics and zero external internet dependencies. Play anywhere, anytime without Wi-Fi.",
          color = VaultTextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )

        HorizontalDivider(color = VaultBorder)

        // Developed by Sandeep
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 4.dp)
        ) {
          Text("⚡", fontSize = 14.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Developed by Sandeep",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
        }

        // Instagram handle button
        val instagramGradient = androidx.compose.ui.graphics.Brush.linearGradient(
          colors = listOf(
            Color(0xFF833AB4),
            Color(0xFFC13584),
            Color(0xFFE1306C),
            Color(0xFFFD1D1D),
            Color(0xFFF77737),
            Color(0xFFFFDC80)
          )
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(instagramGradient)
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable {
              soundEngine.playPop()
              hapticEngine.vibrateTap()
              try {
                val uri = android.net.Uri.parse("https://instagram.com/_u/sandeep_._kumar52")
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                  setPackage("com.instagram.android")
                  flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
              } catch (e: Exception) {
                try {
                  val webUri = android.net.Uri.parse("https://instagram.com/sandeep_._kumar52")
                  val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                  }
                  context.startActivity(webIntent)
                } catch (e2: Exception) {
                  android.widget.Toast.makeText(context, "Instagram: @sandeep_._kumar52", android.widget.Toast.LENGTH_SHORT).show()
                }
              }
            }
            .padding(vertical = 10.dp, horizontal = 14.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📸", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Insta: @sandeep_._kumar52",
              color = Color.White,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp,
              letterSpacing = 0.6.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Open Welcome Screen Button
        OutlinedButton(
          onClick = {
            soundEngine.playSnap()
            hapticEngine.vibrateTap()
            onOpenWelcome()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, CandyCyan.copy(alpha = 0.7f)),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = CandyCyan)
        ) {
          Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Show Welcome Screen", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
