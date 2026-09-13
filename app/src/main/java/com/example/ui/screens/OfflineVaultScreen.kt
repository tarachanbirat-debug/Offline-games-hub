package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

data class LocalVaultGame(
    val id: String,
    val title: String,
    val tag: String,
    val isLandscape: Boolean,
    val glyph: String,
    val colors: List<Color> = listOf(Color(0xFF0284C7), Color(0xFF0F172A)),
    val htmlCode: String
) : java.io.Serializable

fun getOfflineGamesList(): List<LocalVaultGame> {
    return listOf(
        LocalVaultGame("knife", "Knife Hit Master", "ACTION", false, "🎯", listOf(Color(0xFFE11D48), Color(0xFF881337)), "inline_knife"),
        LocalVaultGame("candy", "Candy Jewel Crush", "CASUAL", false, "💎", listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)), "inline_candy"),
        LocalVaultGame("subway-surfers", "Subway Surfers", "ACTION", false, "🏃", listOf(Color(0xFFE91E63), Color(0xFFFF5722)), "offline_games/subway-surfers-beijing.html"),
        LocalVaultGame("tunnel-rush", "Tunnel Rush", "ARCADE", true, "🌀", listOf(Color(0xFF9C27B0), Color(0xFF673AB7)), "offline_games/tunnel-rush.html"),
        LocalVaultGame("tomb-mask", "Tomb of the Mask", "ARCADE", false, "🎭", listOf(Color(0xFFFF9800), Color(0xFFFFC107)), "offline_games/tomb-of-the-mask.html"),
        LocalVaultGame("vex-7", "Vex 7", "ACTION", true, "⚡", listOf(Color(0xFF00BCD4), Color(0xFF009688)), "offline_games/vex-7.html"),
        LocalVaultGame("vex-8", "Vex 8", "ACTION", true, "🔥", listOf(Color(0xFFF44336), Color(0xFFE91E63)), "offline_games/vex-8.html"),
        LocalVaultGame("tiny-fishing", "Tiny Fishing", "CASUAL", false, "🎣", listOf(Color(0xFF2196F3), Color(0xFF03A9F4)), "offline_games/tiny-fishing.html"),
        LocalVaultGame("table-tennis", "Table Tennis", "SPORTS", false, "🏓", listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)), "offline_games/table-tennis-world-tour.html"),
        LocalVaultGame("temple-boom", "Temple of Boom", "ACTION", true, "💣", listOf(Color(0xFFFF5722), Color(0xFF795548)), "offline_games/temple-of-boom.html"),
        LocalVaultGame("time-shooter-2", "Time Shooter 2", "ACTION", true, "🔫", listOf(Color(0xFF607D8B), Color(0xFF37474F)), "offline_games/time-shooter-2.html"),
        LocalVaultGame("time-shooter-3", "Time Shooter 3", "ACTION", true, "🎯", listOf(Color(0xFF455A64), Color(0xFF263238)), "offline_games/time-shooter-3.html"),
        LocalVaultGame("they-are-coming", "They Are Coming", "ACTION", true, "🧟", listOf(Color(0xFF388E3C), Color(0xFF1B5E20)), "offline_games/they-are-coming.html"),
        LocalVaultGame("super-liquid-soccer", "Super Liquid Soccer", "SPORTS", true, "⚽", listOf(Color(0xFF009688), Color(0xFF004D40)), "offline_games/super-liquid-soccer.html"),
        LocalVaultGame("soccer-skills-world-cup", "Soccer Skills World Cup", "SPORTS", true, "🏆", listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)), "offline_games/soccer-skills-world-cup.html"),
        LocalVaultGame("soccer-skills-euro-cup", "Soccer Skills Euro Cup", "SPORTS", true, "🥇", listOf(Color(0xFF2196F3), Color(0xFF0D47A1)), "offline_games/soccer-skills-euro-cup.html"),
        LocalVaultGame("stickman-hook", "Stickman Hook", "CASUAL", false, "🪝", listOf(Color(0xFFFF4081), Color(0xFFC2185B)), "offline_games/stickman-hook.html"),
        LocalVaultGame("stickman-boost", "Stickman Boost", "ACTION", true, "🏃‍♂️", listOf(Color(0xFFFF9800), Color(0xFFE65100)), "offline_games/stickman-boost.html"),
        LocalVaultGame("stickman-bike", "Stickman Bike", "RACING", true, "🚴", listOf(Color(0xFF607D8B), Color(0xFF263238)), "offline_games/stickman-bike.html"),
        LocalVaultGame("stickman-climb-2", "Stickman Climb 2", "CASUAL", false, "⛏️", listOf(Color(0xFF795548), Color(0xFF3E2723)), "offline_games/stickman-climb-2.html"),
        LocalVaultGame("stick-defenders", "Stick Defenders", "STRATEGY", false, "🛡️", listOf(Color(0xFF3F51B5), Color(0xFF1A237E)), "offline_games/stick-defenders.html"),
        LocalVaultGame("stick-merge", "Stick Merge", "ACTION", false, "🔫", listOf(Color(0xFFF44336), Color(0xFFB71C1C)), "offline_games/stick-merge.html"),
        LocalVaultGame("smash-karts", "Smash Karts", "RACING", true, "🏎️", listOf(Color(0xFFFF5722), Color(0xFFBF360C)), "offline_games/smash-karts.html"),
        LocalVaultGame("snow-rider-3d", "Snow Rider 3D", "ARCADE", true, "🛷", listOf(Color(0xFF00BCD4), Color(0xFF006064)), "offline_games/snow-rider-3d.html"),
        LocalVaultGame("slope", "Slope", "ARCADE", true, "🟢", listOf(Color(0xFF00E676), Color(0xFF00B0FF)), "offline_games/slope.html"),
        LocalVaultGame("retro-bowl", "Retro Bowl", "SPORTS", true, "🏈", listOf(Color(0xFF8D6E63), Color(0xFF4E342E)), "offline_games/retro-bowl.html"),
        LocalVaultGame("rooftop-snipers", "Rooftop Snipers", "ACTION", true, "🏢", listOf(Color(0xFF9E9E9E), Color(0xFF424242)), "offline_games/rooftop-snipers.html"),
        LocalVaultGame("moto-x3m", "Moto X3M", "RACING", true, "🏍️", listOf(Color(0xFFFF9800), Color(0xFFF57C00)), "offline_games/moto-x3m.html"),
        LocalVaultGame("moto-x3m-winter", "Moto X3M Winter", "RACING", true, "❄️", listOf(Color(0xFF03A9F4), Color(0xFF0288D1)), "offline_games/moto-x3m-winter.html"),
        LocalVaultGame("moto-x3m-spooky", "Moto X3M Spooky", "RACING", true, "🎃", listOf(Color(0xFFFF5722), Color(0xFFD84315)), "offline_games/moto-x3m-spooky-land.html"),
        LocalVaultGame("moto-x3m-pool-party", "Moto X3M Pool Party", "RACING", true, "🏖️", listOf(Color(0xFF00BCD4), Color(0xFF00838F)), "offline_games/moto-x3m-pool-party.html"),
        LocalVaultGame("madalin-stunt-cars-2", "Madalin Stunt Cars 2", "RACING", true, "🚗", listOf(Color(0xFFE91E63), Color(0xFF880E4F)), "offline_games/madalin-stunt-cars-2.html"),
        LocalVaultGame("monkey-mart", "Monkey Mart", "CASUAL", false, "🐵", listOf(Color(0xFF8BC34A), Color(0xFF33691E)), "offline_games/monkey-mart.html"),
        LocalVaultGame("level-devil", "Level Devil", "PUZZLE", false, "😈", listOf(Color(0xFFD32F2F), Color(0xFF212121)), "offline_games/level-devil.html"),
        LocalVaultGame("getaway-shootout", "Getaway Shootout", "ACTION", true, "🏃‍♂️", listOf(Color(0xFFFFC107), Color(0xFFFFA000)), "offline_games/getaway-shootout.html"),
        LocalVaultGame("drive-mad", "Drive Mad", "RACING", true, "🚙", listOf(Color(0xFFFF9800), Color(0xFFE65100)), "offline_games/drive-mad.html"),
        LocalVaultGame("dunkers", "Dunkers", "SPORTS", false, "🏀", listOf(Color(0xFFFF5722), Color(0xFFBF360C)), "offline_games/dunkers.html"),
        LocalVaultGame("drift-boss", "Drift Boss", "ARCADE", false, "🚘", listOf(Color(0xFF9C27B0), Color(0xFF4A148C)), "offline_games/drift-boss.html"),
        LocalVaultGame("drift-hunters", "Drift Hunters", "RACING", true, "🏁", listOf(Color(0xFF212121), Color(0xFFD32F2F)), "offline_games/drift-hunters.html"),
        LocalVaultGame("cluster-rush", "Cluster Rush", "ACTION", true, "🚚", listOf(Color(0xFFF44336), Color(0xFFB71C1C)), "offline_games/cluster-rush.html"),
        LocalVaultGame("crossy-road", "Crossy Road", "ARCADE", false, "🐔", listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)), "offline_games/crossy-road.html"),
        LocalVaultGame("cookie-clicker", "Cookie Clicker", "CASUAL", false, "🍪", listOf(Color(0xFF795548), Color(0xFF3E2723)), "offline_games/cookie-clicker.html"),
        LocalVaultGame("bullet-force", "Bullet Force", "ACTION", true, "💥", listOf(Color(0xFF37474F), Color(0xFF212121)), "offline_games/bullet-force.html"),
        LocalVaultGame("bloons-td-4", "Bloons TD 4", "STRATEGY", true, "🎈", listOf(Color(0xFFFFEB3B), Color(0xFFF57F17)), "offline_games/bloons-td-4.html"),
        LocalVaultGame("blockpost", "Blockpost", "ACTION", true, "🧱", listOf(Color(0xFF607D8B), Color(0xFF263238)), "offline_games/blockpost.html"),
        LocalVaultGame("bitlife", "BitLife", "SIMULATION", false, "👶", listOf(Color(0xFFFF4081), Color(0xFFC2185B)), "offline_games/bitlife.html"),
        LocalVaultGame("basketball-stars", "Basketball Stars", "SPORTS", true, "🏀", listOf(Color(0xFFFF9800), Color(0xFFE65100)), "offline_games/basketball-stars.html"),
        LocalVaultGame("basket-random", "Basket Random", "SPORTS", true, "🤾", listOf(Color(0xFF03A9F4), Color(0xFF01579B)), "offline_games/basket-random.html"),
        LocalVaultGame("bad-ice-cream", "Bad Ice Cream", "ARCADE", true, "🍦", listOf(Color(0xFFE0F7FA), Color(0xFF006064)), "offline_games/bad-ice-cream.html"),
        LocalVaultGame("bad-ice-cream-2", "Bad Ice Cream 2", "ARCADE", true, "🍨", listOf(Color(0xFFFFF3E0), Color(0xFFE65100)), "offline_games/bad-ice-cream-2.html"),
        LocalVaultGame("bad-ice-cream-3", "Bad Ice Cream 3", "ARCADE", true, "🍧", listOf(Color(0xFFFCE4EC), Color(0xFF880E4F)), "offline_games/bad-ice-cream-3.html"),
        LocalVaultGame("1v1-lol", "1v1.LOL", "ACTION", true, "🎯", listOf(Color(0xFF2979FF), Color(0xFF0D47A1)), "offline_games/1v1-lol.html"),
        LocalVaultGame("2048", "2048", "PUZZLE", false, "🔢", listOf(Color(0xFFFFC107), Color(0xFFFF6F00)), "offline_games/2048.html")
    )
}

val TRUE_OFFLINE_CATALOG: List<LocalVaultGame> by lazy { getOfflineGamesList() }

@Composable
fun OfflineVaultScreen(
    currentTheme: Any? = null,
    onThemeChange: ((Any) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    var activeGame by rememberSaveable { mutableStateOf<LocalVaultGame?>(null) }
    val games = remember { getOfflineGamesList() }

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
                    text = "Instant-Play Zero Data Engines (${games.size} Games)",
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
                        items = games,
                        key = { it.id }
                    ) { game: LocalVaultGame ->
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
fun GameCard(game: LocalVaultGame, onClick: () -> Unit) {
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

@Composable
fun ActiveGamePlayer(game: LocalVaultGame, onClose: () -> Unit) {
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

                    if (game.htmlCode.startsWith("offline_games/") || game.htmlCode.endsWith(".html")) {
                        loadUrl("file:///android_asset/" + game.htmlCode)
                    } else if (game.htmlCode == "inline_knife") {
                        loadDataWithBaseURL(null, "<html><body style='background:#000;color:#fff;display:flex;align-items:center;justify-content:center;height:100vh;'><h2>Knife Hit Master</h2></body></html>", "text/html", "UTF-8", null)
                    } else if (game.htmlCode == "inline_candy") {
                        loadDataWithBaseURL(null, "<html><body style='background:#000;color:#fff;display:flex;align-items:center;justify-content:center;height:100vh;'><h2>Candy Jewel Crush</h2></body></html>", "text/html", "UTF-8", null)
                    } else {
                        loadDataWithBaseURL(null, game.htmlCode, "text/html", "UTF-8", null)
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
