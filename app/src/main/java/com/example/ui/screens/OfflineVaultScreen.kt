package com.example.ui.screens

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class OfflineGameItem(
    val id: String,
    val title: String,
    val category: String,
    val emblem: String,
    val rating: String,
    val bgGradient: List<Color>,
    val assetPath: String
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OfflineVaultScreen() {
    var activeGamePath by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    val view = LocalView.current

    val games = remember {
        listOf(
            OfflineGameItem("1", "Nokia Snake '97", "RETRO", "🐍", "4.9 ★", listOf(Color(0xFF1E1B4B), Color(0xFF312E81)), "file:///android_asset/offline_games/nokia_snake/index.html"),
            OfflineGameItem("2", "Subway Runner 2D", "ACTION", "👟", "4.8 ★", listOf(Color(0xFF7F1D1D), Color(0xFF991B1B)), "file:///android_asset/offline_games/subway_runner/index.html"),
            OfflineGameItem("3", "2048 Deluxe", "PUZZLE", "2048", "4.9 ★", listOf(Color(0xFF581C87), Color(0xFF6B21A8)), "file:///android_asset/offline_games/2048/index.html"),
            OfflineGameItem("4", "Space Blaster 2D", "ARCADE", "🚀", "4.9 ★", listOf(Color(0xFF065F46), Color(0xFF047857)), "file:///android_asset/offline_games/space/index.html"),
            OfflineGameItem("5", "Candy Pop Match-3", "CASUAL", "💎", "4.8 ★", listOf(Color(0xFF831843), Color(0xFF9D174D)), "file:///android_asset/offline_games/match3/index.html"),
            OfflineGameItem("6", "Retro Racer", "RACING", "🏎️", "4.7 ★", listOf(Color(0xFF7C2D12), Color(0xFF9A3412)), "file:///android_asset/offline_games/racer/index.html"),
            OfflineGameItem("7", "Brick Breaker Pro", "ARCADE", "🧱", "4.9 ★", listOf(Color(0xFF0F172A), Color(0xFF1E293B)), "file:///android_asset/offline_games/breaker/index.html"),
            OfflineGameItem("8", "Flappy Deluxe", "TAP", "🐤", "4.8 ★", listOf(Color(0xFF713F12), Color(0xFF854D0E)), "file:///android_asset/offline_games/flappy/index.html"),
            OfflineGameItem("9", "Tic-Tac-Toe AI", "BOARD", "❌⭕", "4.7 ★", listOf(Color(0xFF312E81), Color(0xFF3730A3)), "file:///android_asset/offline_games/tictactoe/index.html"),
            OfflineGameItem("10", "Doodle Bounce", "JUMP", "🦘", "4.9 ★", listOf(Color(0xFF064E3B), Color(0xFF065F46)), "file:///android_asset/offline_games/bounce/index.html"),
            OfflineGameItem("11", "Neon Hextris", "PUZZLE", "🔷", "4.9 ★", listOf(Color(0xFF0369A1), Color(0xFF0284C7)), "file:///android_asset/offline_games/hextris/index.html"),
            OfflineGameItem("12", "Memory Matrix", "BRAIN", "🧠", "4.8 ★", listOf(Color(0xFF047857), Color(0xFF059669)), "file:///android_asset/offline_games/memory_matrix/index.html"),
            OfflineGameItem("13", "Cyber Pong 2D", "RETRO", "🏓", "4.9 ★", listOf(Color(0xFFB45309), Color(0xFFD97706)), "file:///android_asset/offline_games/pong/index.html"),
            OfflineGameItem("14", "Tetris Neon", "ARCADE", "🕹️", "4.9 ★", listOf(Color(0xFF5B21B6), Color(0xFF7C3AED)), "file:///android_asset/offline_games/tetris/index.html"),
            OfflineGameItem("15", "Bubble Shooter", "CASUAL", "🔮", "4.8 ★", listOf(Color(0xFF9D174D), Color(0xFFBE185D)), "file:///android_asset/offline_games/bubble_shooter/index.html"),
            OfflineGameItem("16", "Crossy Road 2D", "ACTION", "🐔", "4.7 ★", listOf(Color(0xFF065F46), Color(0xFF047857)), "file:///android_asset/offline_games/crossy_road/index.html"),
            OfflineGameItem("17", "Tower Stack", "SKILL", "🗼", "4.8 ★", listOf(Color(0xFFD97706), Color(0xFFF59E0B)), "file:///android_asset/offline_games/tower_stack/index.html"),
            OfflineGameItem("18", "Knife Hit", "TARGET", "🗡️", "4.9 ★", listOf(Color(0xFF991B1B), Color(0xFFDC2626)), "file:///android_asset/offline_games/knife_hit/index.html"),
            OfflineGameItem("19", "Color Switch", "TIMING", "🎨", "4.8 ★", listOf(Color(0xFF0369A1), Color(0xFF0284C7)), "file:///android_asset/offline_games/color_switch/index.html"),
            OfflineGameItem("20", "Basketball Shoot", "SPORTS", "🏀", "4.9 ★", listOf(Color(0xFFC2410C), Color(0xFFEA580C)), "file:///android_asset/offline_games/basketball/index.html")
        )
    }

    val categories = listOf("ALL", "RETRO", "ACTION", "PUZZLE", "ARCADE", "CASUAL", "SPORTS", "RACING", "BOARD")

    val filteredGames = remember(games, searchQuery, selectedCategory) {
        games.filter { game ->
            val matchesCategory = selectedCategory == "ALL" || game.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() || game.title.contains(searchQuery, ignoreCase = true) || game.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Offline Vault",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${filteredGames.size} AAA Arcade Engines • 60 FPS",
                        fontSize = 13.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "⚡ 100% OFFLINE",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // GAME SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B),
                    disabledContainerColor = Color(0xFF1E293B),
                    focusedBorderColor = Color(0xFF10B981),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                placeholder = { Text("Search 20 offline games...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF10B981))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color(0xFF94A3B8))
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // QUICK FILTER CATEGORIES
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF10B981) else Color(0xFF1E293B),
                        modifier = Modifier
                            .height(36.dp)
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedCategory = category
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2-COLUMN GRID WITH SCORE & FILTER TRANSITIONS
            AnimatedContent(
                targetState = filteredGames,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { height -> height / 10 } togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                label = "GameGridTransition"
            ) { targetGames ->
                if (targetGames.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No games found matching \"$searchQuery\"",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(targetGames, key = { it.id }) { game ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(205.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                                ) {
                                    // Hero Banner Area
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .background(Brush.linearGradient(game.bgGradient)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Glowing Backdrop Disc
                                        Box(
                                            modifier = Modifier
                                                .size(76.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.15f))
                                        )

                                        // Iconic 3D Game Emblem
                                        Text(
                                            text = game.emblem,
                                            fontSize = if (game.emblem.length > 2) 28.sp else 50.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )

                                        // Rating Chip
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.Black.copy(alpha = 0.6f),
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = game.rating,
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        // Category Pill
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.Black.copy(alpha = 0.6f),
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = game.category,
                                                color = Color(0xFF38BDF8),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Info and Full-width Mint-Green PLAY Button with Haptic Feedback
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = game.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Button(
                                            onClick = {
                                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                                activeGamePath = game.assetPath
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(34.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = "PLAY ▶",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FULLSCREEN IN-APP WEBVIEW GAME PLAYER
        activeGamePath?.let { path ->
            Dialog(
                onDismissRequest = { activeGamePath = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(-1, -1)
                                with(settings) {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    cacheMode = WebSettings.LOAD_DEFAULT
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                }
                                loadUrl(path)
                            }
                        }
                    )
                    // Close Button
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            activeGamePath = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    ) {
                        Text("✕", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
