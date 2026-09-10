package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.ui.theme.*

// Instagram Brand Gradient
val InstagramGradient = Brush.linearGradient(
  colors = listOf(
    Color(0xFF833AB4), // Purple
    Color(0xFFC13584), // Magenta
    Color(0xFFE1306C), // Pink
    Color(0xFFFD1D1D), // Crimson
    Color(0xFFF77737), // Orange
    Color(0xFFFFDC80)  // Golden Yellow
  )
)

@Composable
fun WelcomeScreen(
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onStartPlaying: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Gentle breathing pulsing animation for the game badge
  val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale_anim"
  )

  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.75f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_anim"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            VaultBackground,
            VaultSurface,
            Color(0xFF061421)
          )
        )
      )
      .windowInsetsPadding(WindowInsets.statusBars)
      .windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {

      // Top Status Tag
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(VaultSurfaceElevated)
          .border(1.dp, CandyCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            tint = CandyMint,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "100% OFFLINE ARCADE",
            color = CandyMint,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Center Hero Graphic & Title
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Glowing Mascot Container
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(130.dp)
            .scale(pulseScale)
        ) {
          // Ambient Glow Behind
          Box(
            modifier = Modifier
              .size(126.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(CandyCyan.copy(alpha = glowAlpha), Color.Transparent)
                )
              )
          )

          // 3D Outer Ring
          Box(
            modifier = Modifier
              .size(105.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(CandyWatermelon, CandyTangerine, CandyLemon)
                )
              )
              .border(3.dp, Color.White.copy(alpha = 0.8f), CircleShape)
              .shadow(16.dp, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SportsEsports,
              contentDescription = "Game Station",
              tint = Color.White,
              modifier = Modifier.size(56.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title
        Text(
          text = "GAME VAULT",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 32.sp,
          letterSpacing = 1.5.sp,
          textAlign = TextAlign.Center
        )

        Text(
          text = "ALL-IN-ONE OFFLINE STATION",
          color = CandyLemon,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 13.sp,
          letterSpacing = 1.8.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "10 classic native games packed into one super-fast app. No Wi-Fi, no mobile data, and no ads. Just pure fun!",
          color = VaultTextSecondary,
          fontSize = 13.sp,
          lineHeight = 19.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Feature Badges
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          FeatureMiniBadge(emoji = "🧩", title = "10 Games", subtitle = "Puzzles & Arcade")
          FeatureMiniBadge(emoji = "⚡", title = "Zero Lag", subtitle = "Native Canvas")
          FeatureMiniBadge(emoji = "🏆", title = "High Scores", subtitle = "Local Records")
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Button: Start Playing
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = {
            soundEngine.playSnap()
            hapticEngine.vibrateSuccess()
            onStartPlaying()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .testTag("welcome_start_button"),
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(
                  listOf(CandyMint, Color(0xFF00C853), CandyCyan)
                )
              )
              .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "LET'S PLAY",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.2.sp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(Color.Black),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = CandyMint,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Developer Credits Section (as requested by user)
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(VaultSurfaceElevated.copy(alpha = 0.85f))
            .border(1.dp, VaultBorder, RoundedCornerShape(18.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
          // Line 1: Developed by Sandeep
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text("⚡", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Developed by Sandeep",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp,
              letterSpacing = 0.5.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Line 2: Instagram line with distinct styling and Instagram gradient pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(InstagramGradient)
              .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
              .clickable {
                soundEngine.playPop()
                hapticEngine.vibrateTap()
                openInstagram(context, "sandeep_._kumar52")
              }
              .padding(horizontal = 14.dp, vertical = 7.dp)
              .testTag("insta_handle_badge")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text("📸", fontSize = 13.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Insta: @sandeep_._kumar52",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.sp,
                letterSpacing = 0.6.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FeatureMiniBadge(emoji: String, title: String, subtitle: String) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .width(96.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(VaultSurfaceElevated)
      .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
      .padding(vertical = 10.dp, horizontal = 4.dp)
  ) {
    Text(emoji, fontSize = 20.sp)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = title,
      color = Color.White,
      fontWeight = FontWeight.Black,
      fontSize = 11.sp,
      textAlign = TextAlign.Center
    )
    Text(
      text = subtitle,
      color = VaultTextMuted,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
  }
}

private fun openInstagram(context: Context, username: String) {
  try {
    val uri = Uri.parse("https://instagram.com/_u/$username")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
      setPackage("com.instagram.android")
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  } catch (e: Exception) {
    // Fallback to web browser
    try {
      val webUri = Uri.parse("https://instagram.com/$username")
      val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(webIntent)
    } catch (e2: Exception) {
      Toast.makeText(context, "Instagram: @$username", Toast.LENGTH_SHORT).show()
    }
  }
}
