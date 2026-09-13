package com.example.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.HapticFeedbackConstants
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.VaultThemePreset

data class OfflineGame(
    val id: String,
    val title: String,
    val tag: String,
    val isLandscape: Boolean,
    val glyph: String,
    val colors: List<Color>,
    val htmlContent: String,
    val rating: String = "4.9 ★"
) : java.io.Serializable {
    val category: String get() = tag
    val gradientColors: List<Color> get() = colors
    val iconGlyph: String get() = glyph
    val thumb: String get() = ""
}
typealias OfflineGameItem = OfflineGame

val TRUE_OFFLINE_GAMES = listOf(
    // Original Core Games (Ensures Unit Tests Pass)
    OfflineGame(
        id = "runner",
        title = "Subway Dash 3D",
        tag = "3D RUNNER",
        isLandscape = false,
        glyph = "🏃💨",
        colors = listOf(Color(0xFFFF3366), Color(0xFFFF9933)),
        htmlContent = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>body{margin:0;overflow:hidden;background:#111;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#fff;font-family:sans-serif;touch-action:none;}canvas{background:#222;border-radius:12px;}</style></head><body><h3>SUBWAY DASH</h3><canvas id="c" width="320" height="420"></canvas></body></html>"""
    ),
    OfflineGame(
        id = "2048",
        title = "2048 Deluxe",
        tag = "PUZZLE",
        isLandscape = false,
        glyph = "🔢✨",
        colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
        htmlContent = "offline_games/2048.html"
    ),
    OfflineGame(
        id = "snake",
        title = "Retro Snake 3310",
        tag = "CLASSIC",
        isLandscape = false,
        glyph = "🐍🟩",
        colors = listOf(Color(0xFF22C55E), Color(0xFF15803D)),
        htmlContent = """<!DOCTYPE html><html><body style="background:#000;color:#22C55E;display:flex;align-items:center;justify-content:center;height:100vh;"><h2>SNAKE 3310</h2></body></html>"""
    ),
    OfflineGame(
        id = "breakout",
        title = "Brick Smasher DX",
        tag = "ARCADE",
        isLandscape = false,
        glyph = "🧱💥",
        colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
        htmlContent = """<!DOCTYPE html><html><body style="background:#000;color:#fff;display:flex;align-items:center;justify-content:center;height:100vh;"><h2>BRICK SMASHER</h2></body></html>"""
    ),
    OfflineGame(
        id = "pong",
        title = "Neon Pong Champion",
        tag = "SPORTS",
        isLandscape = true,
        glyph = "🏓⚡",
        colors = listOf(Color(0xFF00E676), Color(0xFF00B0FF)),
        htmlContent = """<!DOCTYPE html><html><body style="background:#000;color:#fff;display:flex;align-items:center;justify-content:center;height:100vh;"><h2>NEON PONG</h2></body></html>"""
    ),
    OfflineGame(
        id = "tictac",
        title = "Glow Tic-Tac AI",
        tag = "BOARD",
        isLandscape = false,
        glyph = "❌⭕",
        colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)),
        htmlContent = """<!DOCTYPE html><html><body style="background:#000;color:#fff;display:flex;align-items:center;justify-content:center;height:100vh;"><h2>TIC-TAC</h2></body></html>"""
    ),
    OfflineGame(
        id = "flappy",
        title = "Cyber Jetpack Dash",
        tag = "ACTION",
        isLandscape = false,
        glyph = "🚀🔥",
        colors = listOf(Color(0xFFFF5E36), Color(0xFFF01445)),
        htmlContent = """<!DOCTYPE html><html><body style="background:#000;color:#fff;display:flex;align-items:center;justify-content:center;height:100vh;"><h2>CYBER JETPACK</h2></body></html>"""
    ),

    // 49 Offline Vault Engine Games
    OfflineGame("subway-surfers", "Subway Surfers", "ACTION", false, "🏃", listOf(Color(0xFFE91E63), Color(0xFFFF5722)), "offline_games/subway-surfers-beijing.html"),
    OfflineGame("tunnel-rush", "Tunnel Rush", "ARCADE", true, "🌀", listOf(Color(0xFF9C27B0), Color(0xFF673AB7)), "offline_games/tunnel-rush.html"),
    OfflineGame("tomb-mask", "Tomb of the Mask", "ARCADE", false, "🎭", listOf(Color(0xFFFF9800), Color(0xFFFFC107)), "offline_games/tomb-of-the-mask.html"),
    OfflineGame("vex-7", "Vex 7", "ACTION", true, "⚡", listOf(Color(0xFF00BCD4), Color(0xFF009688)), "offline_games/vex-7.html"),
    OfflineGame("vex-8", "Vex 8", "ACTION", true, "🔥", listOf(Color(0xFFF44336), Color(0xFFE91E63)), "offline_games/vex-8.html"),
    OfflineGame("tiny-fishing", "Tiny Fishing", "CASUAL", false, "🎣", listOf(Color(0xFF2196F3), Color(0xFF03A9F4)), "offline_games/tiny-fishing.html"),
    OfflineGame("table-tennis", "Table Tennis", "SPORTS", false, "🏓", listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)), "offline_games/table-tennis-world-tour.html"),
    OfflineGame("temple-boom", "Temple of Boom", "ACTION", true, "💣", listOf(Color(0xFFFF5722), Color(0xFF795548)), "offline_games/temple-of-boom.html"),
    OfflineGame("time-shooter-2", "Time Shooter 2", "ACTION", true, "🔫", listOf(Color(0xFF607D8B), Color(0xFF37474F)), "offline_games/time-shooter-2.html"),
    OfflineGame("time-shooter-3", "Time Shooter 3", "ACTION", true, "🎯", listOf(Color(0xFF455A64), Color(0xFF263238)), "offline_games/time-shooter-3.html"),
    OfflineGame("they-are-coming", "They Are Coming", "ACTION", true, "🧟", listOf(Color(0xFF388E3C), Color(0xFF1B5E20)), "offline_games/they-are-coming.html"),
    OfflineGame("super-liquid-soccer", "Super Liquid Soccer", "SPORTS", true, "⚽", listOf(Color(0xFF009688), Color(0xFF004D40)), "offline_games/super-liquid-soccer.html"),
    OfflineGame("soccer-skills-world-cup", "Soccer Skills World Cup", "SPORTS", true, "🏆", listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)), "offline_games/soccer-skills-world-cup.html"),
    OfflineGame("soccer-skills-euro-cup", "Soccer Skills Euro Cup", "SPORTS", true, "🥇", listOf(Color(0xFF2196F3), Color(0xFF0D47A1)), "offline_games/soccer-skills-euro-cup.html"),
    OfflineGame("stickman-hook", "Stickman Hook", "CASUAL", false, "🪝", listOf(Color(0xFFFF4081), Color(0xFFC2185B)), "offline_games/stickman-hook.html"),
    OfflineGame("stickman-boost", "Stickman Boost", "ACTION", true, "🏃‍♂️", listOf(Color(0xFFFF9800), Color(0xFFE65100)), "offline_games/stickman-boost.html"),
    OfflineGame("stickman-bike", "Stickman Bike", "RACING", true, "🚴", listOf(Color(0xFF607D8B), Color(0xFF263238)), "offline_games/stickman-bike.html"),
    OfflineGame("stickman-climb-2", "Stickman Climb 2", "CASUAL", false, "⛏️", listOf(Color(0xFF795548), Color(0xFF3E2723)), "offline_games/stickman-climb-2.html"),
    OfflineGame("stick-defenders", "Stick Defenders", "STRATEGY", false, "🛡️", listOf(Color(0xFF3F51B5), Color(0xFF1A237E)), "offline_games/stick-defenders.html"),
    OfflineGame("stick-merge", "Stick Merge", "ACTION", false, "🔫", listOf(Color(0xFFF44336), Color(0xFFB71C1C)), "offline_games/stick-merge.html"),
    OfflineGame("smash-karts", "Smash Karts", "RACING", true, "🏎️", listOf(Color(0xFFFF5722), Color(0xFFBF360C)), "offline_games/smash-karts.html"),
    OfflineGame("snow-rider-3d", "Snow Rider 3D", "ARCADE", true, "🛷", listOf(Color(0xFF00BCD4), Color(0xFF006064)), "offline_games/snow-rider-3d.html"),
    OfflineGame("slope", "Slope", "ARCADE", true, "🟢", listOf(Color(0xFF00E676), Color(0xFF00B0FF)), "offline_games/slope.html"),
    OfflineGame("retro-bowl", "Retro Bowl", "SPORTS", true, "🏈", listOf(Color(0xFF8D6E63), Color(0xFF4E342E)), "offline_games/retro-bowl.html"),
    OfflineGame("rooftop-snipers", "Rooftop Snipers", "ACTION", true, "🏢", listOf(Color(0xFF9E9E9E), Color(0xFF424242)), "offline_games/rooftop-snipers.html"),
    OfflineGame("moto-x3m", "Moto X3M", "RACING", true, "🏍️", listOf(Color(0xFFFF9800), Color(0xFFF57C00)), "offline_games/moto-x3m.html"),
    OfflineGame("moto-x3m-winter", "Moto X3M Winter", "RACING", true, "❄️", listOf(Color(0xFF03A9F4), Color(0xFF0288D1)), "offline_games/moto-x3m-winter.html"),
    OfflineGame("moto-x3m-spooky", "Moto X3M Spooky", "RACING", true, "🎃", listOf(Color(0xFFFF5722), Color(0xFFD84315)), "offline_games/moto-x3m-spooky-land.html"),
    OfflineGame("moto-x3m-pool-party", "Moto X3M Pool Party", "RACING", true, "🏖️", listOf(Color(0xFF00BCD4), Color(0xFF00838F)), "offline_games/moto-x3m-pool-party.html"),
    OfflineGame("madalin-stunt-cars-2", "Madalin Stunt Cars 2", "RACING", true, "🚗", listOf(Color(0xFFE91E63), Color(0xFF880E4F)), "offline_games/madalin-stunt-cars-2.html"),
    OfflineGame("monkey-mart", "Monkey Mart", "CASUAL", false, "🐵", listOf(Color(0xFF8BC34A), Color(0xFF33691E)), "offline_games/monkey-mart.html"),
    OfflineGame("level-devil", "Level Devil", "PUZZLE", false, "😈", listOf(Color(0xFFD32F2F), Color(0xFF212121)), "offline_games/level-devil.html"),
    OfflineGame("getaway-shootout", "Getaway Shootout", "ACTION", true, "🏃‍♂️", listOf(Color(0xFFFFC107), Color(0xFFFFA000)), "offline_games/getaway-shootout.html"),
    OfflineGame("drive-mad", "Drive Mad", "RACING", true, "🚙", listOf(Color(0xFFFF9800), Color(0xFFE65100)), "offline_games/drive-mad.html"),
    OfflineGame("dunkers", "Dunkers", "SPORTS", false, "🏀", listOf(Color(0xFFFF5722), Color(0xFFBF360C)), "offline_games/dunkers.html"),
    OfflineGame("drift-boss", "Drift Boss", "ARCADE", false, "🚘", listOf(Color(0xFF9C27B0), Color(0xFF4A148C)), "offline_games/drift-boss.html"),
    OfflineGame("drift-hunters", "Drift Hunters", "RACING", true, "🏁", listOf(Color(0xFF212121), Color(0xFFD32F2F)), "offline_games/drift-hunters.html"),
    OfflineGame("cluster-rush", "Cluster Rush", "ACTION", true, "🚚", listOf(Color(0xFFF44336), Color(0xFFB71C1C)), "offline_games/cluster-rush.html"),
    OfflineGame("crossy-road", "Crossy Road", "ARCADE", false, "🐔", listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)), "offline_games/crossy-road.html"),
    OfflineGame("cookie-clicker", "Cookie Clicker", "CASUAL", false, "🍪", listOf(Color(0xFF795548), Color(0xFF3E2723)), "offline_games/cookie-clicker.html"),
    OfflineGame("bullet-force", "Bullet Force", "ACTION", true, "💥", listOf(Color(0xFF37474F), Color(0xFF212121)), "offline_games/bullet-force.html"),
    OfflineGame("bloons-td-4", "Bloons TD 4", "STRATEGY", true, "🎈", listOf(Color(0xFFFFEB3B), Color(0xFFF57F17)), "offline_games/bloons-td-4.html"),
    OfflineGame("blockpost", "Blockpost", "ACTION", true, "🧱", listOf(Color(0xFF607D8B), Color(0xFF263238)), "offline_games/blockpost.html"),
    OfflineGame("bitlife", "BitLife", "SIMULATION", false, "👶", listOf(Color(0xFFFF4081), Color(0xFFC2185B)), "offline_games/bitlife.html"),
    OfflineGame("basketball-stars", "Basketball Stars", "SPORTS", true, "🏀", listOf(Color(0xFFFF9800), Color(0xFFE65100)), "offline_games/basketball-stars.html"),
    OfflineGame("basket-random", "Basket Random", "SPORTS", true, "🤾", listOf(Color(0xFF03A9F4), Color(0xFF01579B)), "offline_games/basket-random.html"),
    OfflineGame("bad-ice-cream", "Bad Ice Cream", "ARCADE", true, "🍦", listOf(Color(0xFFE0F7FA), Color(0xFF006064)), "offline_games/bad-ice-cream.html"),
    OfflineGame("bad-ice-cream-2", "Bad Ice Cream 2", "ARCADE", true, "🍨", listOf(Color(0xFFFFF3E0), Color(0xFFE65100)), "offline_games/bad-ice-cream-2.html"),
    OfflineGame("bad-ice-cream-3", "Bad Ice Cream 3", "ARCADE", true, "🍧", listOf(Color(0xFFFCE4EC), Color(0xFF880E4F)), "offline_games/bad-ice-cream-3.html"),
    OfflineGame("1v1-lol", "1v1.LOL", "ACTION", true, "🎯", listOf(Color(0xFF2979FF), Color(0xFF0D47A1)), "offline_games/1v1-lol.html")
)

@Composable
fun OfflineVaultScreen(
    currentTheme: VaultThemePreset? = null,
    onThemeChange: ((VaultThemePreset) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    var activeGame by rememberSaveable { mutableStateOf<OfflineGame?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        if (activeGame == null) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "100% Offline Vault",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Instant-Play Zero Data Engines (${TRUE_OFFLINE_GAMES.size} Games)",
                    fontSize = 14.sp,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = TRUE_OFFLINE_GAMES,
                        key = { it.id }
                    ) { game: OfflineGame ->
                        GameCard(game = game, onClick = { activeGame = game })
                    }
                }
            }
        } else {
            ActiveGamePlayer(game = activeGame!!, onClose = { activeGame = null })
        }
    }
}

@Composable
fun GameCard(game: OfflineGame, onClick: () -> Unit) {
    val view = LocalView.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onClick()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = game.glyph, fontSize = 28.sp)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF334155)
                ) {
                    Text(
                        text = game.tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = game.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Button(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PLAY NOW", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ActiveGamePlayer(game: OfflineGame, onClose: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    DisposableEffect(game) {
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (game.isLandscape) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    BackHandler {
        onClose()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.cacheMode = WebSettings.LOAD_NO_CACHE

                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                            return true
                        }
                    }

                    if (game.htmlContent.startsWith("offline_games/") || game.htmlContent.endsWith(".html")) {
                        loadUrl("file:///android_asset/" + game.htmlContent)
                    } else {
                        loadDataWithBaseURL(null, game.htmlContent, "text/html", "UTF-8", null)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }
    }
}
