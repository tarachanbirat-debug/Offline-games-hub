package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VaultHapticEngine
import com.example.audio.VaultSoundEngine
import com.example.discovery.VaultDiscoveryEngine
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DiscoveryScreen(
  discoveryEngine: VaultDiscoveryEngine,
  soundEngine: VaultSoundEngine,
  hapticEngine: VaultHapticEngine,
  onImportSuccess: (GameItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var isInspecting by remember { mutableStateOf(false) }
  var currentReport by remember { mutableStateOf<InspectionReport?>(null) }
  var buildExecution by remember { mutableStateOf<BuildExecution?>(null) }
  var isBuilding by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()
  val keyboardController = LocalSoftwareKeyboardController.current

  fun runInspection(query: String) {
    if (query.isBlank()) return
    isInspecting = true
    buildExecution = null
    keyboardController?.hide()
    soundEngine.playTap()
    hapticEngine.vibrateTap()

    scope.launch {
      val report = discoveryEngine.inspectRepository(query)
      currentReport = report
      isInspecting = false
      soundEngine.playSnap()
      hapticEngine.vibrateSuccess()
    }
  }

  fun triggerBuild(report: InspectionReport) {
    isBuilding = true
    soundEngine.playSnap()
    hapticEngine.vibrateTap()

    scope.launch {
      val execution = discoveryEngine.buildAndPackage(report)
      buildExecution = execution
      isBuilding = false
      soundEngine.playVictory()
      hapticEngine.vibrateSuccess()
    }
  }

  fun importIntoVault(report: InspectionReport) {
    val newGame = GameItem(
      id = report.gameId,
      title = report.title,
      description = report.notes.joinToString(" "),
      category = "Community",
      genre = report.detectedTechnology,
      technology = report.detectedTechnology,
      engine = report.recommendedAdapter,
      version = "1.0.0",
      license = report.licenseType,
      licenseStatus = report.licenseVerdict.name,
      source = "GitHub Open Source",
      entryPoint = report.entryPoint,
      offlineMode = report.offlineCapable,
      onlineMode = report.externalNetworkRequired,
      dependencies = report.dependencies.joinToString(", "),
      controls = "TOUCH / GESTURE",
      difficulty = "MEDIUM",
      orientation = "PORTRAIT",
      author = "Open Contributor",
      credits = "Open Source Integration",
      highScore = 0,
      totalPlays = 0,
      stars = 5,
      isFavorite = false,
      lastPlayed = System.currentTimeMillis(),
      candyColorHex = 0xFF9945FF
    )
    onImportSuccess(newGame)
    soundEngine.playVictory()
    hapticEngine.vibrateSuccess()
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VaultBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
  ) {
    // Header Info
    item {
      Column {
        Text(
          text = "GAME DISCOVERY & INSPECTION",
          color = CandyLemon,
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          letterSpacing = 1.2.sp
        )
        Text(
          text = "Inspect & Package Repos",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 24.sp
        )
        Text(
          text = "Inspect GitHub repositories, verify licenses, analyze dependencies, and package offline games directly into your Vault.",
          color = VaultTextSecondary,
          fontSize = 13.sp,
          lineHeight = 17.sp,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }

    // Search / URL Input Box
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(VaultSurfaceElevated)
          .border(1.5.dp, VaultBorderGlow, RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = CandyCyan)
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Paste repo URL or type name...", color = VaultTextMuted, fontSize = 13.sp) },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { runInspection(searchQuery) }),
          modifier = Modifier
            .weight(1f)
            .testTag("discovery_search_input")
        )
        IconButton(
          onClick = { runInspection(searchQuery) },
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(CandyGrape)
            .testTag("discovery_inspect_button")
        ) {
          Icon(Icons.Default.Check, contentDescription = "Inspect", tint = Color.White)
        }
      }
    }

    // Curated Quick-Pick Catalog Pills
    item {
      Column {
        Text(
          text = "CURATED OPEN-SOURCE CANDIDATES",
          color = VaultTextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("2048 Master", "Godot Platformer", "Hextris", "Three.js 3D").forEach { sample ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(VaultCardDark)
                .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
                .clickable {
                  searchQuery = sample
                  runInspection(sample)
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(sample, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Loading Indicator
    if (isInspecting) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = CandyCyan)
        }
      }
    }

    // Inspection Report Card
    currentReport?.let { report ->
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(VaultSurfaceElevated)
            .border(1.5.dp, CandyCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "INSPECTION REPORT",
              color = CandyCyan,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              letterSpacing = 1.sp
            )
            // Classification Badge
            val badgeColor = when (report.classification) {
              ClassificationStatus.OFFLINE_READY -> CandyMint
              ClassificationStatus.ONLINE -> CandySkyBlue
              ClassificationStatus.BUILD_REQUIRED -> CandyTangerine
              ClassificationStatus.LICENSE_REVIEW -> CandyLemon
              ClassificationStatus.UNSUPPORTED -> CandyWatermelon
              else -> CandyGrape
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(badgeColor.copy(alpha = 0.2f))
                .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = report.classification.name.replace("_", " "),
                color = badgeColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          Text(
            text = report.title,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 6.dp)
          )

          Text(
            text = "Detected: ${report.detectedTechnology} • Entry: ${report.entryPoint}",
            color = VaultTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          // Specs Grid
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(VaultCardDark)
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("LICENSE", color = VaultTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              Text(report.licenseType, color = CandyLemon, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("OFFLINE READY", color = VaultTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              Text(if (report.offlineCapable) "YES (100%)" else "NEEDS CACHE", color = CandyMint, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("ADAPTER", color = VaultTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              Text(report.recommendedAdapter, color = CandyCyan, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Notes
          report.notes.forEach { note ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
              Text("• ", color = CandyMint, fontWeight = FontWeight.Bold)
              Text(note, color = Color.White, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Build & Package Trigger Button
          Button(
            onClick = { triggerBuild(report) },
            enabled = !isBuilding,
            colors = ButtonDefaults.buttonColors(containerColor = CandyGrape),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("trigger_build_button")
          ) {
            if (isBuilding) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("BUILDING PACKAGE...")
            } else {
              Icon(Icons.Default.Build, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("EXECUTE ADAPTER BUILD & PACKAGE", fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }

    // Build Execution Output & Workflow
    buildExecution?.let { execution ->
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(VaultCardDark)
            .border(1.5.dp, CandyMint.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("BUILD EXECUTION: SUCCESS", color = CandyMint, fontWeight = FontWeight.Black, fontSize = 13.sp)
            Text(execution.targetPlatform, color = VaultTextSecondary, fontSize = 10.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Build Steps Logs
          execution.steps.forEach { step ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(modifier = Modifier.weight(1f)) {
                Text("✓ ", color = CandyMint, fontWeight = FontWeight.Bold)
                Column {
                  Text(step.stepName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Text(step.output, color = VaultTextSecondary, fontSize = 11.sp)
                }
              }
              Text("${step.durationMs}ms", color = VaultTextMuted, fontSize = 10.sp)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Generated CI / Actions Workflow snippet
          Text("GENERATED WORKFLOW / RUNNER SPECIFICATION", color = VaultTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Box(
            modifier = Modifier
              .padding(top = 4.dp, bottom = 14.dp)
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0C0917))
              .padding(10.dp)
          ) {
            Text(
              text = execution.generatedWorkflowYaml,
              color = CandyLemon,
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp
            )
          }

          // Import into Vault Button
          Button(
            onClick = { currentReport?.let { importIntoVault(it) } },
            colors = ButtonDefaults.buttonColors(containerColor = CandyMint),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("import_vault_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("IMPORT INTO GAME VAULT", color = Color.Black, fontWeight = FontWeight.Black)
          }
        }
      }
    }
  }
}
