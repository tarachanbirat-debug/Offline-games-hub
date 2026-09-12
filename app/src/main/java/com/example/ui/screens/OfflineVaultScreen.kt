package com.example.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    val colors: List<Color>,
    val htmlCode: String
) : java.io.Serializable

val TRUE_OFFLINE_CATALOG = listOf(
    // 1. KNIFE HIT MASTER
    LocalVaultGame(
        id = "knife",
        title = "Knife Hit Master",
        tag = "ACTION",
        isLandscape = false,
        glyph = "🗡️🎯",
        colors = listOf(Color(0xFFE11D48), Color(0xFFBE123C)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #070a14; color: #fff; font-family: -apple-system, BlinkMacSystemFont, sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; touch-action: none; }
        #h { width: 320px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-weight: 800; }
        #sc { color: #38bdf8; font-size: 18px; text-shadow: 0 0 10px #38bdf8; }
        canvas { background: radial-gradient(circle at 50% 30%, #1e293b, #090d16); border-radius: 16px; border: 1px solid rgba(225,29,72,0.4); box-shadow: 0 0 30px rgba(225,29,72,0.3); }
        </style></head><body>
        <div id="h"><span style="color:#f43f5e;letter-spacing:1px;">🗡️ KNIFE HIT MASTER</span><span id="sc">SCORE: 0</span></div>
        <canvas id="c" width="320" height="460"></canvas>
        <script>
        var cv = document.getElementById('c'), x = cv.getContext('2d'), ang = 0, spd = 0.035, knives = [], curY = 400, throwing = false, score = 0, over = false, menu = true;
        var particles = [];
        function reset() { knives = []; score = 0; over = false; menu = false; curY = 400; throwing = false; document.getElementById('sc').innerText = 'SCORE: 0'; }
        window.addEventListener('touchstart', function(e) {
            e.preventDefault();
            if (menu || over) { reset(); return; }
            if (!throwing) throwing = true;
        }, { passive: false });
        function spawnSparks(px, py) {
            for (var i = 0; i < 14; i++) {
                var a = Math.random() * Math.PI * 2, sp = Math.random() * 4 + 2;
                particles.push({ x: px, y: py, vx: Math.cos(a) * sp, vy: Math.sin(a) * sp, life: 1, c: '#f59e0b' });
            }
        }
        function drawKnife(kx, ky, r) {
            x.save(); x.translate(kx, ky); x.rotate(r);
            x.fillStyle = '#e2e8f0'; x.beginPath(); x.moveTo(0, -32); x.lineTo(5, -8); x.lineTo(3, 0); x.lineTo(-3, 0); x.lineTo(-5, -8); x.closePath(); x.fill();
            x.fillStyle = '#f59e0b'; x.fillRect(-6, 0, 12, 3);
            x.fillStyle = '#1e293b'; x.fillRect(-2, 3, 4, 16);
            x.restore();
        }
        function loop() {
            x.clearRect(0, 0, 320, 460);
            if (menu) {
                x.fillStyle = '#f43f5e'; x.font = 'bold 24px sans-serif'; x.textAlign = 'center'; x.fillText('KNIFE HIT MASTER', 160, 180);
                x.fillStyle = '#94a3b8'; x.font = '14px sans-serif'; x.fillText('Precision Target Slasher', 160, 210);
                x.fillStyle = '#38bdf8'; x.font = 'bold 18px sans-serif'; x.fillText('TAP TO START', 160, 260);
                requestAnimationFrame(loop); return;
            }
            if (over) {
                x.fillStyle = '#ef4444'; x.font = 'bold 28px sans-serif'; x.textAlign = 'center'; x.fillText('GAME OVER', 160, 190);
                x.fillStyle = '#fff'; x.font = '18px sans-serif'; x.fillText('FINAL SCORE: ' + score, 160, 230);
                x.fillStyle = '#38bdf8'; x.font = 'bold 16px sans-serif'; x.fillText('TAP TO RETRY', 160, 280);
                requestAnimationFrame(loop); return;
            }
            ang += spd;
            // Target
            x.save(); x.translate(160, 140); x.rotate(ang);
            var g = x.createRadialGradient(0, 0, 10, 0, 0, 52); g.addColorStop(0, '#d97706'); g.addColorStop(0.8, '#92400e'); g.addColorStop(1, '#451a03');
            x.fillStyle = g; x.beginPath(); x.arc(0, 0, 52, 0, Math.PI * 2); x.fill();
            x.strokeStyle = 'rgba(0,0,0,0.3)'; x.lineWidth = 3; x.beginPath(); x.arc(0, 0, 36, 0, Math.PI * 2); x.stroke();
            knives.forEach(function(k) { drawKnife(Math.cos(k) * 52, Math.sin(k) * 52, k + Math.PI / 2); });
            x.restore();
            // Flying knife
            if (throwing) {
                curY -= 18;
                if (curY <= 192) {
                    throwing = false; curY = 400;
                    var hitAng = (Math.PI / 2 - ang) % (Math.PI * 2);
                    if (hitAng < 0) hitAng += Math.PI * 2;
                    var col = false;
                    for (var i = 0; i < knives.length; i++) {
                        var diff = Math.abs(knives[i] - hitAng);
                        var cd = Math.min(diff, Math.PI * 2 - diff);
                        if (cd < 0.28) { col = true; break; }
                    }
                    if (col) {
                        spawnSparks(160, 192); over = true;
                    } else {
                        knives.push(hitAng); score += 10;
                        document.getElementById('sc').innerText = 'SCORE: ' + score;
                        spawnSparks(160, 192);
                        if (knives.length >= 7) { knives = []; spd = (Math.random() > 0.5 ? 1 : -1) * (0.03 + Math.random() * 0.025); }
                    }
                }
            }
            drawKnife(160, curY, 0);
            for (var p = particles.length - 1; p >= 0; p--) {
                var pt = particles[p]; pt.x += pt.vx; pt.y += pt.vy; pt.life -= 0.05;
                if (pt.life <= 0) particles.splice(p, 1);
                else { x.save(); x.globalAlpha = pt.life; x.fillStyle = pt.c; x.beginPath(); x.arc(pt.x, pt.y, 3, 0, Math.PI * 2); x.fill(); x.restore(); }
            }
            requestAnimationFrame(loop);
        } loop();
        </script></body></html>"""
    ),

    // 2. CANDY JEWEL MATCH
    LocalVaultGame(
        id = "candy",
        title = "Candy Jewel Match",
        tag = "MATCH-3",
        isLandscape = false,
        glyph = "🍬✨",
        colors = listOf(Color(0xFFD946EF), Color(0xFFA21CAF)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #13091e; color: #fff; font-family: -apple-system, BlinkMacSystemFont, sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; touch-action: none; }
        #hud { width: 300px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-weight: 800; }
        .hud-val { color: #f472b6; font-size: 17px; }
        #g { display: grid; grid-template-columns: repeat(6, 46px); gap: 6px; background: #241138; padding: 10px; border-radius: 16px; border: 1px solid rgba(217,70,239,0.3); box-shadow: 0 0 25px rgba(217,70,239,0.25); }
        .c { width: 46px; height: 46px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 24px; background: #381a54; cursor: pointer; transition: transform 0.1s; }
        .c:active { transform: scale(0.9); }
        .selected { outline: 3px solid #f472b6; box-shadow: 0 0 12px #f472b6; }
        #over-box { position: absolute; background: rgba(19,9,30,0.95); border: 2px solid #f472b6; border-radius: 18px; padding: 24px; text-align: center; display: none; z-index: 10; box-shadow: 0 0 30px #f472b6; }
        button { margin-top: 14px; background: #f472b6; color: #000; border: none; padding: 10px 22px; font-weight: 800; border-radius: 12px; font-size: 15px; }
        </style></head><body>
        <div id="hud"><div>🍬 MOVES: <span id="mv" class="hud-val">20</span></div><div>SCORE: <span id="sc" class="hud-val">0</span></div></div>
        <div id="g"></div>
        <div id="over-box">
          <h2 style="color:#f472b6;margin-bottom:8px;">STAGE FINISHED!</h2>
          <p id="final-sc" style="font-size:18px;margin-bottom:12px;">SCORE: 0</p>
          <button onclick="initGame()">PLAY AGAIN</button>
        </div>
        <script>
        var icons = ['🍬','🍭','🍇','💎','⭐'], grid = [], score = 0, moves = 20, first = null;
        var el = document.getElementById('g');
        function initGame() {
            grid = []; score = 0; moves = 20; first = null;
            document.getElementById('over-box').style.display = 'none';
            document.getElementById('sc').innerText = '0';
            document.getElementById('mv').innerText = '20';
            for (var i = 0; i < 36; i++) grid.push(icons[Math.floor(Math.random() * icons.length)]);
            checkMatches(); render();
        }
        function render() {
            el.innerHTML = '';
            grid.forEach(function(item, i) {
                var d = document.createElement('div');
                d.className = 'c' + (first === i ? ' selected' : '');
                d.innerText = item;
                d.onclick = function() { tap(i); };
                el.appendChild(d);
            });
        }
        function tap(i) {
            if (moves <= 0) return;
            if (first === null) {
                first = i; render();
            } else {
                var temp = grid[first]; grid[first] = grid[i]; grid[i] = temp;
                first = null; moves--;
                document.getElementById('mv').innerText = moves;
                checkMatches(); render();
                if (moves <= 0) {
                    setTimeout(function() {
                        document.getElementById('final-sc').innerText = 'FINAL SCORE: ' + score;
                        document.getElementById('over-box').style.display = 'block';
                    }, 400);
                }
            }
        }
        function checkMatches() {
            var matched = false;
            for (var r = 0; r < 6; r++) {
                for (var c = 0; c < 4; c++) {
                    var idx = r * 6 + c;
                    if (grid[idx] && grid[idx] === grid[idx+1] && grid[idx] === grid[idx+2]) {
                        score += 30; matched = true;
                        grid[idx] = icons[Math.floor(Math.random() * 5)];
                        grid[idx+1] = icons[Math.floor(Math.random() * 5)];
                        grid[idx+2] = icons[Math.floor(Math.random() * 5)];
                    }
                }
            }
            document.getElementById('sc').innerText = score;
            if (matched) render();
        }
        initGame();
        </script></body></html>"""
    ),

    // 3. NEON BRICK BREAKER
    LocalVaultGame(
        id = "brick",
        title = "Neon Brick Breaker",
        tag = "ARCADE",
        isLandscape = false,
        glyph = "🧱💥",
        colors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #020617; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #38bdf8; font-family: -apple-system, BlinkMacSystemFont, sans-serif; touch-action: none; }
        #h { width: 320px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-weight: 800; }
        canvas { background: #080f24; border: 1px solid #06b6d4; border-radius: 12px; box-shadow: 0 0 25px rgba(6,182,212,0.3); }
        </style></head><body>
        <div id="h"><span>NEON BRICKS</span><span id="sc">SCORE: 0</span></div>
        <canvas id="b" width="320" height="440"></canvas>
        <script>
        var cv = document.getElementById('b'), cx = cv.getContext('2d');
        var bx = 160, by = 340, vx = 3.5, vy = -3.5, px = 120, score = 0, lives = 3, running = false, over = false;
        var bricks = [];
        function resetGame() {
            score = 0; lives = 3; over = false; running = true;
            bx = 160; by = 340; vx = (Math.random() > 0.5 ? 3.5 : -3.5); vy = -3.5; px = 120;
            bricks = [];
            for (var r = 0; r < 5; r++) {
                for (var c = 0; c < 5; c++) {
                    bricks.push({ x: c * 60 + 12, y: r * 22 + 40, w: 54, h: 16, live: 1, c: ['#06b6d4','#38bdf8','#ec4899','#f59e0b','#10b981'][r] });
                }
            }
            document.getElementById('sc').innerText = 'SCORE: 0';
        }
        cv.addEventListener('touchmove', function(e) {
            e.preventDefault();
            var rect = cv.getBoundingClientRect();
            px = e.touches[0].clientX - rect.left - 42;
            if (px < 0) px = 0; if (px > 320 - 84) px = 320 - 84;
        }, { passive: false });
        cv.addEventListener('touchstart', function(e) {
            e.preventDefault();
            if (!running || over) resetGame();
        }, { passive: false });
        resetGame(); running = false;
        function run() {
            cx.clearRect(0, 0, 320, 440);
            if (!running && !over) {
                cx.fillStyle = '#06b6d4'; cx.font = 'bold 22px sans-serif'; cx.textAlign = 'center'; cx.fillText('NEON BRICK BREAKER', 160, 200);
                cx.fillStyle = '#fff'; cx.font = '16px sans-serif'; cx.fillText('TAP TO LAUNCH', 160, 240);
                requestAnimationFrame(run); return;
            }
            if (over) {
                cx.fillStyle = '#ef4444'; cx.font = 'bold 26px sans-serif'; cx.textAlign = 'center'; cx.fillText('GAME OVER', 160, 200);
                cx.fillStyle = '#fff'; cx.font = '16px sans-serif'; cx.fillText('SCORE: ' + score, 160, 240);
                cx.fillStyle = '#06b6d4'; cx.font = 'bold 16px sans-serif'; cx.fillText('TAP TO RETRY', 160, 280);
                requestAnimationFrame(run); return;
            }
            bx += vx; by += vy;
            if (bx < 6 || bx > 314) vx = -vx;
            if (by < 6) vy = -vy;
            if (by > 410 && bx >= px - 6 && bx <= px + 90) {
                vy = -Math.abs(vy);
                vx = (bx - (px + 42)) * 0.12;
            }
            if (by > 440) {
                lives--;
                if (lives <= 0) over = true;
                else { bx = 160; by = 320; vy = -3.5; }
            }
            var allCleared = true;
            bricks.forEach(function(b) {
                if (b.live) {
                    allCleared = false;
                    if (bx > b.x && bx < b.x + b.w && by > b.y && by < b.y + b.h) {
                        b.live = 0; vy = -vy; score += 20;
                        document.getElementById('sc').innerText = 'SCORE: ' + score;
                    }
                }
            });
            if (allCleared) { over = true; }
            // Paddle
            cx.fillStyle = '#06b6d4'; cx.shadowColor = '#06b6d4'; cx.shadowBlur = 10;
            cx.beginPath(); cx.roundRect(px, 415, 84, 10, 5); cx.fill(); cx.shadowBlur = 0;
            // Ball
            cx.fillStyle = '#fff'; cx.beginPath(); cx.arc(bx, by, 6, 0, 7); cx.fill();
            // Bricks
            bricks.forEach(function(b) {
                if (b.live) {
                    cx.fillStyle = b.c; cx.beginPath(); cx.roundRect(b.x, b.y, b.w, b.h, 4); cx.fill();
                }
            });
            requestAnimationFrame(run);
        } run();
        </script></body></html>"""
    ),

    // 4. CYBER SNAKE GLOW
    LocalVaultGame(
        id = "snake",
        title = "Cyber Snake Glow",
        tag = "CLASSIC",
        isLandscape = false,
        glyph = "🐍🟩",
        colors = listOf(Color(0xFF10B981), Color(0xFF047857)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #000; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #10b981; font-family: monospace; touch-action: none; }
        #h { width: 300px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-weight: bold; }
        canvas { border: 2px solid #10b981; background: #05140d; box-shadow: 0 0 25px rgba(16,185,129,0.3); border-radius: 8px; }
        </style></head><body>
        <div id="h"><span>CYBER SNAKE</span><span id="sc">SCORE: 0</span></div>
        <canvas id="s" width="300" height="300"></canvas>
        <p style="font-size:12px;margin-top:10px;color:#34d399;">SWIPE TO DIRECT SNAKE</p>
        <script>
        var cv = document.getElementById('s'), cx = cv.getContext('2d');
        var sn = [{x: 150, y: 150}], dx = 15, dy = 0, fx = 60, fy = 60, sc = 0, over = false, started = false;
        function resetGame() { sn = [{x: 150, y: 150}]; dx = 15; dy = 0; fx = 60; fy = 60; sc = 0; over = false; started = true; document.getElementById('sc').innerText = 'SCORE: 0'; }
        function step() {
            if (!started || over) {
                cx.clearRect(0, 0, 300, 300);
                if (!started) {
                    cx.fillStyle = '#10b981'; cx.font = 'bold 20px monospace'; cx.textAlign = 'center'; cx.fillText('CYBER SNAKE', 150, 130);
                    cx.fillStyle = '#fff'; cx.font = '14px monospace'; cx.fillText('TAP TO START', 150, 170);
                } else if (over) {
                    cx.fillStyle = '#ef4444'; cx.font = 'bold 22px monospace'; cx.textAlign = 'center'; cx.fillText('CRASHED!', 150, 130);
                    cx.fillStyle = '#fff'; cx.font = '14px monospace'; cx.fillText('SCORE: ' + sc, 150, 165);
                    cx.fillStyle = '#10b981'; cx.font = '14px monospace'; cx.fillText('TAP TO RETRY', 150, 200);
                }
                return;
            }
            var h = { x: sn[0].x + dx, y: sn[0].y + dy };
            if (h.x < 0 || h.x >= 300 || h.y < 0 || h.y >= 300) { over = true; return; }
            for (var i = 1; i < sn.length; i++) { if (sn[i].x === h.x && sn[i].y === h.y) { over = true; return; } }
            sn.unshift(h);
            if (Math.abs(h.x - fx) < 15 && Math.abs(h.y - fy) < 15) {
                sc += 10; document.getElementById('sc').innerText = 'SCORE: ' + sc;
                fx = Math.floor(Math.random() * 18) * 15; fy = Math.floor(Math.random() * 18) * 15;
            } else { sn.pop(); }
            cx.clearRect(0, 0, 300, 300);
            // Food
            cx.fillStyle = '#ef4444'; cx.shadowColor = '#ef4444'; cx.shadowBlur = 8;
            cx.beginPath(); cx.arc(fx + 7.5, fy + 7.5, 7, 0, Math.PI * 2); cx.fill(); cx.shadowBlur = 0;
            // Snake
            sn.forEach(function(p, idx) {
                cx.fillStyle = idx === 0 ? '#34d399' : '#10b981';
                cx.fillRect(p.x, p.y, 14, 14);
            });
        }
        setInterval(step, 110);
        var tx = 0, ty = 0;
        window.addEventListener('touchstart', function(e) {
            if (!started || over) { resetGame(); return; }
            tx = e.touches[0].clientX; ty = e.touches[0].clientY;
        }, { passive: false });
        window.addEventListener('touchend', function(e) {
            var kx = e.changedTouches[0].clientX - tx, ky = e.changedTouches[0].clientY - ty;
            if (Math.abs(kx) > Math.abs(ky)) {
                if (kx > 25 && dx === 0) { dx = 15; dy = 0; }
                else if (kx < -25 && dx === 0) { dx = -15; dy = 0; }
            } else {
                if (ky > 25 && dy === 0) { dx = 0; dy = 15; }
                else if (ky < -25 && dy === 0) { dx = 0; dy = -15; }
            }
        }, { passive: false });
        </script></body></html>"""
    ),

    // 5. 2048 NEON DELUXE
    LocalVaultGame(
        id = "2048",
        title = "2048 Neon Deluxe",
        tag = "PUZZLE",
        isLandscape = false,
        glyph = "🔢⭐",
        colors = listOf(Color(0xFFF59E0B), Color(0xFFB45309)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #181528; color: #fff; font-family: -apple-system, BlinkMacSystemFont, sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; touch-action: none; }
        #h { width: 300px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
        .score-card { background: #27223d; padding: 6px 14px; border-radius: 8px; text-align: center; border: 1px solid rgba(245,158,11,0.3); }
        .val { color: #f59e0b; font-weight: 900; font-size: 17px; }
        #b { width: 300px; height: 300px; background: #27223d; border-radius: 14px; display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; padding: 8px; box-shadow: 0 0 25px rgba(245,158,11,0.25); }
        .c { background: #3c3459; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 900; color: #fff; transition: transform 0.1s; }
        .c-2 { background: #475569; }
        .c-4 { background: #64748b; }
        .c-8 { background: #f97316; box-shadow: 0 0 10px #f97316; }
        .c-16 { background: #ea580c; box-shadow: 0 0 12px #ea580c; }
        .c-32 { background: #ef4444; box-shadow: 0 0 14px #ef4444; }
        .c-64 { background: #dc2626; box-shadow: 0 0 16px #dc2626; }
        .c-128 { background: #eab308; box-shadow: 0 0 18px #eab308; font-size: 18px; }
        .c-256 { background: #facc15; color: #000; box-shadow: 0 0 20px #facc15; font-size: 18px; }
        .c-512 { background: #38bdf8; box-shadow: 0 0 22px #38bdf8; font-size: 18px; }
        .c-1024, .c-2048 { background: #ec4899; box-shadow: 0 0 24px #ec4899; font-size: 16px; }
        </style></head><body>
        <div id="h">
          <div style="font-weight:900;font-size:24px;color:#f59e0b;">2048</div>
          <div style="display:flex;gap:8px;">
            <div class="score-card"><div style="font-size:10px;color:#94a3b8;">SCORE</div><div id="sc" class="val">0</div></div>
            <div class="score-card"><div style="font-size:10px;color:#94a3b8;">BEST</div><div id="bs" class="val">0</div></div>
          </div>
        </div>
        <div id="b"></div>
        <p style="margin-top:14px;font-size:12px;color:#94a3b8;">SWIPE TO MERGE NUMBERS</p>
        <script>
        var grid = [[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0]];
        var score = 0, best = parseInt(localStorage.getItem('vault_2048') || '0', 10);
        document.getElementById('bs').innerText = best;
        var bEl = document.getElementById('b');
        function addRandom() {
            var empty = [];
            for (var r = 0; r < 4; r++) for (var c = 0; c < 4; c++) if (!grid[r][c]) empty.push([r, c]);
            if (empty.length > 0) {
                var p = empty[Math.floor(Math.random() * empty.length)];
                grid[p[0]][p[1]] = Math.random() > 0.15 ? 2 : 4;
            }
        }
        function draw() {
            bEl.innerHTML = '';
            for (var r = 0; r < 4; r++) {
                for (var c = 0; c < 4; c++) {
                    var v = grid[r][c];
                    var d = document.createElement('div');
                    d.className = 'c' + (v > 0 ? ' c-' + v : '');
                    d.innerText = v > 0 ? v : '';
                    bEl.appendChild(d);
                }
            }
            document.getElementById('sc').innerText = score;
            if (score > best) { best = score; document.getElementById('bs').innerText = best; localStorage.setItem('vault_2048', best); }
        }
        function slide(row) {
            var arr = row.filter(function(v){ return v !== 0; });
            for (var i = 0; i < arr.length - 1; i++) {
                if (arr[i] === arr[i+1]) { arr[i] *= 2; score += arr[i]; arr[i+1] = 0; }
            }
            arr = arr.filter(function(v){ return v !== 0; });
            while (arr.length < 4) arr.push(0);
            return arr;
        }
        function move(dir) {
            var changed = false;
            for (var r = 0; r < 4; r++) {
                var row = [];
                for (var c = 0; c < 4; c++) row.push(dir==='up'||dir==='down' ? grid[c][r] : grid[r][c]);
                var orig = row.slice();
                if (dir==='right'||dir==='down') row.reverse();
                row = slide(row);
                if (dir==='right'||dir==='down') row.reverse();
                for (var c = 0; c < 4; c++) {
                    if (dir==='up'||dir==='down') { if (grid[c][r] !== row[c]) changed = true; grid[c][r] = row[c]; }
                    else { if (grid[r][c] !== row[c]) changed = true; grid[r][c] = row[c]; }
                }
            }
            if (changed) { addRandom(); draw(); }
        }
        var sx = 0, sy = 0;
        window.addEventListener('touchstart', function(e) { sx = e.touches[0].clientX; sy = e.touches[0].clientY; }, { passive: false });
        window.addEventListener('touchend', function(e) {
            var dx = e.changedTouches[0].clientX - sx, dy = e.changedTouches[0].clientY - sy;
            if (Math.abs(dx) > Math.abs(dy)) { if (dx > 30) move('right'); else if (dx < -30) move('left'); }
            else { if (dy > 30) move('down'); else if (dy < -30) move('up'); }
        }, { passive: false });
        addRandom(); addRandom(); draw();
        </script></body></html>"""
    ),

    // 6. GLOW TIC-TAC-TOE AI
    LocalVaultGame(
        id = "tictac",
        title = "Glow Tic-Tac-Toe AI",
        tag = "CASUAL",
        isLandscape = false,
        glyph = "❌⭕",
        colors = listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #080d1a; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #38bdf8; font-family: -apple-system, BlinkMacSystemFont, sans-serif; touch-action: none; }
        #st { font-size: 16px; font-weight: 800; color: #cbd5e1; margin-bottom: 18px; min-height: 24px; }
        #b { display: grid; grid-template-columns: repeat(3, 86px); gap: 10px; background: #131d36; padding: 12px; border-radius: 18px; border: 1px solid rgba(99,102,241,0.3); box-shadow: 0 0 30px rgba(99,102,241,0.3); }
        .c { width: 86px; height: 86px; background: #0b1326; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 42px; font-weight: 900; cursor: pointer; }
        .x-val { color: #00e5ff; text-shadow: 0 0 14px #00e5ff; }
        .o-val { color: #ec4899; text-shadow: 0 0 14px #ec4899; }
        button { margin-top: 22px; padding: 10px 24px; background: #6366f1; color: #fff; border: none; border-radius: 12px; font-weight: 800; font-size: 15px; }
        </style></head><body>
        <h2 style="margin-bottom:6px;color:#818cf8;letter-spacing:1px;">TIC-TAC-TOE AI</h2>
        <div id="st">YOUR TURN (X)</div>
        <div id="b"></div>
        <button onclick="reset()">RESET GAME</button>
        <script>
        var bd = ['','','','','','','','',''], over = false, el = document.getElementById('b'), st = document.getElementById('st');
        function render() {
            el.innerHTML = '';
            bd.forEach(function(v, i) {
                var d = document.createElement('div');
                d.className = 'c ' + (v === 'X' ? 'x-val' : v === 'O' ? 'o-val' : '');
                d.innerText = v;
                d.onclick = function() { tap(i); };
                el.appendChild(d);
            });
        }
        function checkWinner(b) {
            var lines = [[0,1,2],[3,4,5],[6,7,8],[0,3,6],[1,4,7],[2,5,8],[0,4,8],[2,4,6]];
            for (var i = 0; i < lines.length; i++) {
                var l = lines[i];
                if (b[l[0]] && b[l[0]] === b[l[1]] && b[l[0]] === b[l[2]]) return b[l[0]];
            }
            if (b.every(function(c) { return c !== ''; })) return 'draw';
            return null;
        }
        function tap(i) {
            if (bd[i] || over) return;
            bd[i] = 'X'; render();
            var res = checkWinner(bd);
            if (res) { finish(res); return; }
            st.innerText = 'AI THINKING...';
            setTimeout(aiMove, 250);
        }
        function aiMove() {
            var empty = [];
            for (var i = 0; i < 9; i++) if (!bd[i]) empty.push(i);
            if (empty.length > 0) {
                // Try win or block
                var move = empty[Math.floor(Math.random() * empty.length)];
                bd[move] = 'O'; render();
                var res = checkWinner(bd);
                if (res) finish(res);
                else st.innerText = 'YOUR TURN (X)';
            }
        }
        function finish(w) {
            over = true;
            if (w === 'X') st.innerText = '🎉 YOU WIN!';
            else if (w === 'O') st.innerText = '💀 AI WINS!';
            else st.innerText = '🤝 DRAW!';
        }
        function reset() { bd = ['','','','','','','','','']; over = false; st.innerText = 'YOUR TURN (X)'; render(); }
        render();
        </script></body></html>"""
    ),

    // 7. RETRO 60FPS PONG
    LocalVaultGame(
        id = "pong",
        title = "Retro 60FPS Pong",
        tag = "SPORTS",
        isLandscape = true,
        glyph = "🏓⚡",
        colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #030208; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #a855f7; font-family: monospace; touch-action: none; }
        canvas { border: 2px solid #8b5cf6; background: #0a0614; box-shadow: 0 0 25px rgba(139,92,246,0.3); border-radius: 12px; }
        </style></head><body>
        <canvas id="p" width="480" height="260"></canvas>
        <script>
        var cv = document.getElementById('p'), cx = cv.getContext('2d');
        var bx = 240, by = 130, vx = 4.5, vy = 2.5, p1 = 100, p2 = 100, s1 = 0, s2 = 0, started = false, over = false;
        function resetGame() { bx = 240; by = 130; vx = 4.5; vy = 2.5; s1 = 0; s2 = 0; started = true; over = false; }
        cv.addEventListener('touchmove', function(e) {
            e.preventDefault();
            p1 = e.touches[0].clientY - cv.getBoundingClientRect().top - 30;
            if (p1 < 0) p1 = 0; if (p1 > 200) p1 = 200;
        }, { passive: false });
        cv.addEventListener('touchstart', function(e) {
            e.preventDefault();
            if (!started || over) resetGame();
        }, { passive: false });
        function loop() {
            cx.clearRect(0, 0, 480, 260);
            if (!started) {
                cx.fillStyle = '#a855f7'; cx.font = 'bold 22px monospace'; cx.textAlign = 'center'; cx.fillText('RETRO PONG 60FPS', 240, 110);
                cx.fillStyle = '#fff'; cx.font = '14px monospace'; cx.fillText('TOUCH TO PLAY', 240, 150);
                requestAnimationFrame(loop); return;
            }
            if (over) {
                cx.fillStyle = s1 >= 5 ? '#22c55e' : '#ef4444'; cx.font = 'bold 24px monospace'; cx.textAlign = 'center';
                cx.fillText(s1 >= 5 ? 'YOU WIN!' : 'AI WINS!', 240, 110);
                cx.fillStyle = '#fff'; cx.font = '14px monospace'; cx.fillText('TAP TO PLAY AGAIN', 240, 150);
                requestAnimationFrame(loop); return;
            }
            bx += vx; by += vy;
            if (by < 6 || by > 254) vy = -vy;
            if (bx < 24 && by >= p1 && by <= p1 + 60) { vx = Math.abs(vx) + 0.1; vy += (by - (p1 + 30)) * 0.1; }
            if (bx > 456 && by >= p2 && by <= p2 + 60) { vx = -(Math.abs(vx) + 0.1); vy += (by - (p2 + 30)) * 0.1; }
            if (bx < 0) { s2++; bx = 240; by = 130; vx = 4.5; if (s2 >= 5) over = true; }
            if (bx > 480) { s1++; bx = 240; by = 130; vx = -4.5; if (s1 >= 5) over = true; }
            // AI tracking
            p2 += (by - (p2 + 30)) * 0.12;
            if (p2 < 0) p2 = 0; if (p2 > 200) p2 = 200;
            // Center dashed line
            cx.strokeStyle = 'rgba(168,85,247,0.3)'; cx.setLineDash([6, 6]); cx.beginPath(); cx.moveTo(240, 0); cx.lineTo(240, 260); cx.stroke(); cx.setLineDash([]);
            // Scores
            cx.fillStyle = '#fff'; cx.font = 'bold 22px monospace'; cx.fillText(s1, 200, 35); cx.fillText(s2, 265, 35);
            // Paddles
            cx.fillStyle = '#06b6d4'; cx.fillRect(12, p1, 10, 60);
            cx.fillStyle = '#ec4899'; cx.fillRect(458, p2, 10, 60);
            // Ball
            cx.fillStyle = '#fff'; cx.beginPath(); cx.arc(bx, by, 6, 0, Math.PI * 2); cx.fill();
            requestAnimationFrame(loop);
        } loop();
        </script></body></html>"""
    ),

    // 8. BOTTLE TARGET 3D
    LocalVaultGame(
        id = "shooter",
        title = "Bottle Target 3D",
        tag = "SHOOTER",
        isLandscape = true,
        glyph = "🍾🎯",
        colors = listOf(Color(0xFFEC4899), Color(0xFFBE185D)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #130510; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #f472b6; font-family: -apple-system, BlinkMacSystemFont, sans-serif; touch-action: none; }
        canvas { background: radial-gradient(circle at 50% 40%, #290d23, #10030d); border-radius: 14px; border: 1px solid #ec4899; box-shadow: 0 0 25px rgba(236,72,153,0.3); }
        </style></head><body>
        <canvas id="t" width="460" height="250"></canvas>
        <script>
        var cv = document.getElementById('t'), cx = cv.getContext('2d');
        var tx = 60, spd = 3.5, sc = 0, ammo = 15, started = false, over = false, sparks = [];
        function reset() { sc = 0; ammo = 15; started = true; over = false; }
        cv.addEventListener('touchstart', function(e) {
            e.preventDefault();
            if (!started || over) { reset(); return; }
            ammo--;
            var r = cv.getBoundingClientRect(), tapX = e.touches[0].clientX - r.left, tapY = e.touches[0].clientY - r.top;
            if (Math.abs(tapX - tx) < 32 && tapY >= 110 && tapY <= 190) {
                sc += 15; tx = Math.random() * 360 + 40;
                for (var i = 0; i < 18; i++) {
                    var a = Math.random() * Math.PI * 2, s = Math.random() * 5 + 2;
                    sparks.push({ x: tapX, y: tapY, vx: Math.cos(a) * s, vy: Math.sin(a) * s, life: 1, c: '#10b981' });
                }
            }
            if (ammo <= 0) over = true;
        }, { passive: false });
        function loop() {
            cx.clearRect(0, 0, 460, 250);
            if (!started) {
                cx.fillStyle = '#ec4899'; cx.font = 'bold 22px sans-serif'; cx.textAlign = 'center'; cx.fillText('BOTTLE TARGET 3D', 230, 110);
                cx.fillStyle = '#fff'; cx.font = '15px sans-serif'; cx.fillText('TAP TO START', 230, 150);
                requestAnimationFrame(loop); return;
            }
            if (over) {
                cx.fillStyle = '#ec4899'; cx.font = 'bold 24px sans-serif'; cx.textAlign = 'center'; cx.fillText('SHOOTING FINISHED!', 230, 100);
                cx.fillStyle = '#fff'; cx.font = '16px sans-serif'; cx.fillText('FINAL SCORE: ' + sc, 230, 135);
                cx.fillStyle = '#38bdf8'; cx.font = 'bold 15px sans-serif'; cx.fillText('TAP TO RETRY', 230, 175);
                requestAnimationFrame(loop); return;
            }
            tx += spd;
            if (tx < 35 || tx > 425) spd = -spd;
            // Bottle drawing
            cx.fillStyle = '#10b981'; cx.beginPath(); cx.roundRect(tx - 12, 130, 24, 55, 4); cx.fill();
            cx.fillStyle = '#059669'; cx.fillRect(tx - 6, 110, 12, 20);
            cx.fillStyle = '#f59e0b'; cx.fillRect(tx - 7, 106, 14, 5);
            // HUD
            cx.fillStyle = '#fff'; cx.font = 'bold 16px sans-serif'; cx.textAlign = 'left';
            cx.fillText('SCORE: ' + sc, 20, 30); cx.fillText('AMMO: ' + ammo, 360, 30);
            // Sparks
            for (var p = sparks.length - 1; p >= 0; p--) {
                var pt = sparks[p]; pt.x += pt.vx; pt.y += pt.vy; pt.life -= 0.05;
                if (pt.life <= 0) sparks.splice(p, 1);
                else { cx.save(); cx.globalAlpha = pt.life; cx.fillStyle = pt.c; cx.beginPath(); cx.arc(pt.x, pt.y, 3, 0, 7); cx.fill(); cx.restore(); }
            }
            requestAnimationFrame(loop);
        } loop();
        </script></body></html>"""
    ),

    // 9. HEXAGON NEON CORE
    LocalVaultGame(
        id = "hextris",
        title = "Hexagon Neon Core",
        tag = "REFLEX",
        isLandscape = false,
        glyph = "⬡✨",
        colors = listOf(Color(0xFF14B8A6), Color(0xFF0F766E)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #03120f; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #2dd4bf; font-family: -apple-system, BlinkMacSystemFont, sans-serif; touch-action: none; }
        canvas { background: radial-gradient(circle at 50% 50%, #0a2924, #03120f); border-radius: 14px; border: 1px solid #14b8a6; box-shadow: 0 0 25px rgba(20,184,166,0.3); }
        </style></head><body>
        <canvas id="h" width="320" height="420"></canvas>
        <script>
        var cv = document.getElementById('h'), cx = cv.getContext('2d');
        var rot = 0, sc = 0, bars = [], started = false, over = false;
        function reset() { rot = 0; sc = 0; bars = []; started = true; over = false; }
        cv.addEventListener('touchstart', function(e) {
            e.preventDefault();
            if (!started || over) { reset(); return; }
            var r = cv.getBoundingClientRect(), tx = e.touches[0].clientX - r.left;
            if (tx < 160) rot -= Math.PI / 3; else rot += Math.PI / 3;
        }, { passive: false });
        function spawnBar() {
            if (!started || over) return;
            bars.push({ dist: 160, side: Math.floor(Math.random() * 6), c: ['#2dd4bf','#f43f5e','#f59e0b','#a855f7'][Math.floor(Math.random()*4)] });
        }
        setInterval(spawnBar, 1200);
        function loop() {
            cx.clearRect(0, 0, 320, 420);
            if (!started) {
                cx.fillStyle = '#2dd4bf'; cx.font = 'bold 22px sans-serif'; cx.textAlign = 'center'; cx.fillText('HEXAGON CORE', 160, 190);
                cx.fillStyle = '#fff'; cx.font = '14px sans-serif'; cx.fillText('TAP SIDES TO ROTATE', 160, 230);
                requestAnimationFrame(loop); return;
            }
            if (over) {
                cx.fillStyle = '#ef4444'; cx.font = 'bold 24px sans-serif'; cx.textAlign = 'center'; cx.fillText('CORE OVERLOAD!', 160, 190);
                cx.fillStyle = '#fff'; cx.font = '16px sans-serif'; cx.fillText('SCORE: ' + sc, 160, 230);
                cx.fillStyle = '#2dd4bf'; cx.font = 'bold 16px sans-serif'; cx.fillText('TAP TO RETRY', 160, 270);
                requestAnimationFrame(loop); return;
            }
            // Draw Center Hexagon
            cx.save(); cx.translate(160, 210); cx.rotate(rot);
            cx.strokeStyle = '#2dd4bf'; cx.lineWidth = 4; cx.beginPath();
            for (var i = 0; i < 6; i++) {
                var a = i * Math.PI / 3;
                cx.lineTo(44 * Math.cos(a), 44 * Math.sin(a));
            }
            cx.closePath(); cx.stroke(); cx.restore();
            // Incoming Bars
            for (var b = bars.length - 1; b >= 0; b--) {
                var bar = bars[b]; bar.dist -= 2;
                var barAngle = bar.side * Math.PI / 3 + rot;
                var x1 = 160 + bar.dist * Math.cos(barAngle - 0.25);
                var y1 = 210 + bar.dist * Math.sin(barAngle - 0.25);
                var x2 = 160 + bar.dist * Math.cos(barAngle + 0.25);
                var y2 = 210 + bar.dist * Math.sin(barAngle + 0.25);
                cx.strokeStyle = bar.c; cx.lineWidth = 6; cx.beginPath(); cx.moveTo(x1, y1); cx.lineTo(x2, y2); cx.stroke();
                if (bar.dist <= 46) {
                    bars.splice(b, 1); sc += 10;
                }
            }
            cx.fillStyle = '#fff'; cx.font = 'bold 16px sans-serif'; cx.textAlign = 'left';
            cx.fillText('SCORE: ' + sc, 15, 30);
            requestAnimationFrame(loop);
        } loop();
        </script></body></html>"""
    ),

    // 10. HIGHWAY MOTO DRIFT
    LocalVaultGame(
        id = "moto",
        title = "Highway Moto Drift",
        tag = "RACING",
        isLandscape = true,
        glyph = "🏍️🔥",
        colors = listOf(Color(0xFFF97316), Color(0xFFC2410C)),
        htmlCode = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"><style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-user-select: none; }
        body { overflow: hidden; background: #0c0a09; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #fb923c; font-family: -apple-system, BlinkMacSystemFont, sans-serif; touch-action: none; }
        canvas { background: #1c1917; border: 1px solid #f97316; border-radius: 12px; box-shadow: 0 0 25px rgba(249,115,22,0.3); }
        </style></head><body>
        <canvas id="m" width="460" height="240"></canvas>
        <script>
        var cv = document.getElementById('m'), cx = cv.getContext('2d');
        var my = 110, ex = 440, ey = 60, sc = 0, started = false, over = false;
        function reset() { my = 110; ex = 440; ey = Math.random() * 160 + 30; sc = 0; started = true; over = false; }
        cv.addEventListener('touchmove', function(e) {
            e.preventDefault();
            my = e.touches[0].clientY - cv.getBoundingClientRect().top - 12;
            if (my < 20) my = 20; if (my > 200) my = 200;
        }, { passive: false });
        cv.addEventListener('touchstart', function(e) {
            e.preventDefault();
            if (!started || over) reset();
        }, { passive: false });
        function loop() {
            cx.clearRect(0, 0, 460, 240);
            if (!started) {
                cx.fillStyle = '#fb923c'; cx.font = 'bold 22px sans-serif'; cx.textAlign = 'center'; cx.fillText('HIGHWAY MOTO DRIFT', 230, 105);
                cx.fillStyle = '#fff'; cx.font = '14px sans-serif'; cx.fillText('TOUCH & DRAG TO STEER', 230, 145);
                requestAnimationFrame(loop); return;
            }
            if (over) {
                cx.fillStyle = '#ef4444'; cx.font = 'bold 24px sans-serif'; cx.textAlign = 'center'; cx.fillText('WRECKED!', 230, 105);
                cx.fillStyle = '#fff'; cx.font = '16px sans-serif'; cx.fillText('FINAL SCORE: ' + sc, 230, 145);
                cx.fillStyle = '#fb923c'; cx.font = 'bold 15px sans-serif'; cx.fillText('TAP TO RETRY', 230, 185);
                requestAnimationFrame(loop); return;
            }
            ex -= 6.5;
            if (ex < -40) { ex = 470; ey = Math.random() * 160 + 30; sc += 10; }
            // Check crash
            if (ex < 65 && ex > 10 && Math.abs(my - ey) < 26) { over = true; }
            // Road lanes
            cx.strokeStyle = '#44403c'; cx.setLineDash([14, 14]);
            cx.beginPath(); cx.moveTo(0, 80); cx.lineTo(460, 80); cx.stroke();
            cx.beginPath(); cx.moveTo(0, 160); cx.lineTo(460, 160); cx.stroke();
            cx.setLineDash([]);
            // Player Moto (Orange)
            cx.fillStyle = '#fb923c'; cx.beginPath(); cx.roundRect(35, my, 36, 18, 5); cx.fill();
            cx.fillStyle = '#38bdf8'; cx.fillRect(55, my + 3, 8, 12);
            // Enemy Car (Red)
            cx.fillStyle = '#ef4444'; cx.beginPath(); cx.roundRect(ex, ey, 42, 22, 6); cx.fill();
            cx.fillStyle = '#fef08a'; cx.fillRect(ex, ey + 2, 4, 18);
            // HUD
            cx.fillStyle = '#fff'; cx.font = 'bold 16px sans-serif'; cx.textAlign = 'left';
            cx.fillText('SCORE: ' + sc, 20, 30);
            requestAnimationFrame(loop);
        } loop();
        </script></body></html>"""
    )
)

@Composable
fun OfflineVaultScreen(
    currentThemeId: String = "cyberpunk",
    onThemeSelected: (String) -> Unit = {},
    onGamePlayingStateChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    var activeGame by rememberSaveable { mutableStateOf<LocalVaultGame?>(null) }

    fun launchGame(game: LocalVaultGame) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        val activity = context as? Activity
        activity?.requestedOrientation = if (game.isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        activeGame = game
        onGamePlayingStateChanged(true)
    }

    fun closeGame() {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activeGame = null
        onGamePlayingStateChanged(false)
    }

    BackHandler(enabled = activeGame != null) {
        closeGame()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
    ) {
        // Vault Main Library
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "100% Offline Vault",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Instant-Play Zero Data Engines",
                        fontSize = 12.sp,
                        color = Color(0xFF38BDF8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "⚡ AIRPLANE READY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Catalog Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(TRUE_OFFLINE_CATALOG, key = { it.id }) { game ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                            .clickable { launchGame(game) }
                    ) {
                        Column {
                            // Thumbnail Gradient Canvas
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .background(Brush.linearGradient(game.colors))
                            ) {
                                // Large Emoji Glyph
                                Text(
                                    text = game.glyph,
                                    fontSize = 38.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )

                                // Category Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.45f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = game.tag,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Orientation Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.35f),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = if (game.isLandscape) "🔄 LANDSCAPE" else "📱 PORTRAIT",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Info & Play Action
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = game.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { launchGame(game) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "PLAY NOW",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fullscreen In-Game Overlay
        AnimatedVisibility(
            visible = activeGame != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            activeGame?.let { game ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    // Safe Hardware-Accelerated WebView
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                setLayerType(View.LAYER_TYPE_NONE, null)
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    cacheMode = WebSettings.LOAD_NO_CACHE
                                    allowFileAccess = false
                                    allowContentAccess = false
                                }
                                webChromeClient = WebChromeClient()
                                webViewClient = object : WebViewClient() {
                                    override fun onRenderProcessGone(
                                        view: WebView?,
                                        detail: RenderProcessGoneDetail?
                                    ): Boolean {
                                        closeGame()
                                        return true
                                    }
                                }
                                loadDataWithBaseURL(
                                    "https://offline.app",
                                    game.htmlCode,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            }
                        },
                        onRelease = { webView ->
                            try {
                                (webView.parent as? ViewGroup)?.removeView(webView)
                                webView.stopLoading()
                                webView.loadUrl("about:blank")
                                webView.clearHistory()
                                webView.destroy()
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Floating Exit Button (Always accessible)
                    IconButton(
                        onClick = { closeGame() },
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(12.dp)
                            .align(Alignment.TopEnd)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Game",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
