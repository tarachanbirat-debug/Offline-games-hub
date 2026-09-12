package com.example.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import coil.compose.AsyncImage
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.theme.VaultThemeManager
import com.example.ui.theme.VaultThemePreset
import com.example.ui.components.GameCardArtwork

// SECTION 3.1: Data Model Definition & State Saver
data class OfflineGame(
    val id: String,
    val title: String,
    val tag: String,
    val isLandscape: Boolean,
    val glyph: String,
    val colors: List<Color>,
    val htmlContent: String,
    val rating: String = "4.9 ★"
) {
    val category: String get() = tag
    val gradientColors: List<Color> get() = colors
    val iconGlyph: String get() = glyph
    val thumb: String get() = ""
}
typealias OfflineGameItem = OfflineGame

val GameItemSaver = listSaver<OfflineGame?, Any>(
    save = { item ->
        if (item != null) {
            listOf(
                item.id,
                item.title,
                item.tag,
                item.isLandscape,
                item.glyph,
                item.colors.map { it.value.toLong() },
                item.htmlContent,
                item.rating
            )
        } else emptyList()
    },
    restore = { list ->
        if (list.isNotEmpty()) {
            val colorsRaw = list[5] as? List<*> ?: emptyList<Long>()
            val colors = colorsRaw.map { Color((it as Number).toLong().toULong()) }
            OfflineGame(
                id = list[0] as String,
                title = list[1] as String,
                tag = list[2] as String,
                isLandscape = list[3] as Boolean,
                glyph = list[4] as String,
                colors = if (colors.isNotEmpty()) colors else listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                htmlContent = list[6] as String,
                rating = if (list.size > 7) list[7] as String else "4.9 ★"
            )
        } else null
    }
)

val TRUE_OFFLINE_GAMES = listOf(
    OfflineGame(
        id = "runner",
        title = "Subway Dash 3D",
        tag = "3D RUNNER",
        isLandscape = false,
        glyph = "🏃💨",
        colors = listOf(Color(0xFFFF3366), Color(0xFFFF9933)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;overflow:hidden;background:#111;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#fff;font-family:sans-serif;touch-action:none;}canvas{background:#222;box-shadow:0 0 20px rgba(0,255,150,0.4);border-radius:12px;max-width:92vw;max-height:80vh;}</style></head>
            <body><h3 style="margin:5px 0;color:#00E676;letter-spacing:1px;">SUBWAY DASH 3D</h3><canvas id="c" width="320" height="420"></canvas>
            <script>
            var c=document.getElementById('c'),ctx=c.getContext('2d'),p={x:135,w:50,h:50,lane:1},obs=[],score=0,spd=4;
            var lanes=[35,135,235];
            function spawn(){obs.push({x:lanes[Math.floor(Math.random()*3)],y:-50,w:50,h:50});}
            setInterval(spawn,1400);
            window.addEventListener('touchstart',function(e){
                var tx=e.touches[0].clientX;
                if(tx<window.innerWidth/2){if(p.lane>0)p.lane--;}else{if(p.lane<2)p.lane++;}
                p.x=lanes[p.lane];
            });
            function loop(){
                ctx.clearRect(0,0,320,420);
                ctx.strokeStyle='#444';ctx.lineWidth=4;ctx.beginPath();ctx.moveTo(110,0);ctx.lineTo(110,420);ctx.moveTo(210,0);ctx.lineTo(210,420);ctx.stroke();
                ctx.fillStyle='#00E676';ctx.fillRect(p.x,340,p.w,p.h);
                ctx.fillStyle='#FF1744';
                for(var i=0;i<obs.length;i++){
                    obs[i].y+=spd;ctx.fillRect(obs[i].x,obs[i].y,obs[i].w,obs[i].h);
                    if(obs[i].y>420){obs.splice(i,1);score+=10;i--;}
                    else if(obs[i].y+40>340 && obs[i].x===p.x){score=0;obs=[];}
                }
                ctx.fillStyle='#FFF';ctx.font='bold 16px sans-serif';ctx.fillText('SCORE: '+score,10,25);
                requestAnimationFrame(loop);
            }
            loop();
            </script></body></html>
        """.trimIndent()
    ),
    OfflineGame(
        id = "2048",
        title = "2048 Deluxe",
        tag = "PUZZLE",
        isLandscape = false,
        glyph = "🔢✨",
        colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;background:#FAF8EF;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;font-family:sans-serif;touch-action:none;}#b{width:300px;height:300px;background:#BBADA0;border-radius:8px;display:grid;grid-template-columns:repeat(4,1fr);gap:10px;padding:10px;box-sizing:border-box;}.c{background:#CDC1B4;border-radius:6px;display:flex;align-items:center;justify-content:center;font-size:24px;font-weight:bold;color:#776E65;}</style></head>
            <body><h2 style="color:#776E65;margin-bottom:10px;">2048 MASTER</h2><div id="b"></div>
            <p style="color:#8f7a66;font-size:12px;margin-top:12px;">Tap screen to merge tiles!</p>
            <script>
            var g=[[0,2,0,0],[0,0,4,0],[0,0,0,0],[2,0,0,0]];
            function draw(){var b=document.getElementById('b');b.innerHTML='';for(var r=0;r<4;r++)for(var c=0;c<4;c++){var d=document.createElement('div');d.className='c';d.innerText=g[r][c]||'';if(g[r][c]>4){d.style.background='#F2B179';d.style.color='#FFF';}b.appendChild(d);}}
            draw();
            window.addEventListener('touchstart',function(){
                var empty=[];
                for(var r=0;r<4;r++)for(var c=0;c<4;c++)if(!g[r][c])empty.push([r,c]);
                if(empty.length>0){
                    var pos=empty[Math.floor(Math.random()*empty.length)];
                    g[pos[0]][pos[1]]=Math.random()>0.3?2:4;
                }
                for(var r=0;r<4;r++)for(var c=0;c<3;c++)if(g[r][c] && g[r][c]===g[r][c+1]){g[r][c]*=2;g[r][c+1]=0;}
                draw();
            });
            </script></body></html>
        """.trimIndent()
    ),
    OfflineGame(
        id = "snake",
        title = "Retro Snake 3310",
        tag = "CLASSIC",
        isLandscape = false,
        glyph = "🐍🟩",
        colors = listOf(Color(0xFF22C55E), Color(0xFF15803D)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;background:#000;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#22C55E;font-family:monospace;touch-action:none;}canvas{border:2px solid #22C55E;background:#051a05;border-radius:6px;box-shadow:0 0 15px rgba(34,197,94,0.3);}</style></head>
            <body><h2 style="margin:5px 0;letter-spacing:2px;">SNAKE 3310</h2><canvas id="s" width="300" height="300"></canvas>
            <p style="font-size:11px;margin:8px 0;color:#16a34a;">Swipe to change direction</p>
            <script>
            var cv=document.getElementById('s'),cx=cv.getContext('2d'),sn=[{x:150,y:150}],dx=10,dy=0,fx=50,fy=50,sc=0;
            function step(){
                var h={x:sn[0].x+dx,y:sn[0].y+dy};
                if(h.x<0||h.x>=300||h.y<0||h.y>=300){sn=[{x:150,y:150}];dx=10;dy=0;sc=0;}
                else{sn.unshift(h);if(Math.abs(h.x-fx)<10&&Math.abs(h.y-fy)<10){sc+=10;fx=Math.floor(Math.random()*28)*10;fy=Math.floor(Math.random()*28)*10;}else sn.pop();}
                cx.clearRect(0,0,300,300);cx.fillStyle='#FF0055';cx.fillRect(fx,fy,10,10);
                cx.fillStyle='#22C55E';sn.forEach(function(p){cx.fillRect(p.x,p.y,9,9);});
                cx.fillStyle='#FFF';cx.font='12px monospace';cx.fillText('SCORE: '+sc,8,16);
            }
            setInterval(step,100);
            var tsX=0,tsY=0;
            window.addEventListener('touchstart',function(e){tsX=e.touches[0].clientX;tsY=e.touches[0].clientY;});
            window.addEventListener('touchend',function(e){
                var kX=e.changedTouches[0].clientX-tsX,kY=e.changedTouches[0].clientY-tsY;
                if(Math.abs(kX)>Math.abs(kY)){if(kX>0&&dx===0){dx=10;dy=0;}else if(kX<0&&dx===0){dx=-10;dy=0;}}
                else{if(kY>0&&dy===0){dx=0;dy=10;}else if(kY<0&&dy===0){dx=0;dy=-10;}}
            });
            </script></body></html>
        """.trimIndent()
    ),
    OfflineGame(
        id = "breakout",
        title = "Brick Smasher DX",
        tag = "ARCADE",
        isLandscape = false,
        glyph = "🧱💥",
        colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;background:#0A0E17;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#FFF;font-family:sans-serif;touch-action:none;}canvas{background:#000;border:1px solid #00E676;border-radius:8px;box-shadow:0 0 20px rgba(0,230,118,0.25);}</style></head>
            <body><h3 style="margin:5px 0;color:#00E676;">BRICK SMASHER DX</h3><canvas id="b" width="320" height="400"></canvas>
            <script>
            var cv=document.getElementById('b'),cx=cv.getContext('2d'),bx=160,by=300,vx=3,vy=-3,px=125,score=0;
            var bricks=[];
            for(var r=0;r<4;r++)for(var c=0;c<6;c++)bricks.push({x:15+c*50,y:30+r*20,w:44,h:14,alive:true,color:['#FF1744','#FF9100','#00E676','#00B0FF'][r]});
            window.addEventListener('touchmove',function(e){
                var rect=cv.getBoundingClientRect();
                px=e.touches[0].clientX-rect.left-35;
                if(px<0)px=0;if(px>250)px=250;
            });
            function run(){
                bx+=vx;by+=vy;
                if(bx<6||bx>314)vx=-vx;
                if(by<6)vy=-vy;
                if(by>378&&bx>=px&&bx<=px+70){vy=-Math.abs(vy);vx=(bx-(px+35))*0.12;}
                for(var i=0;i<bricks.length;i++){
                    var b=bricks[i];
                    if(b.alive&&bx>b.x&&bx<b.x+b.w&&by>b.y&&by<b.y+b.h){
                        b.alive=false;vy=-vy;score+=25;break;
                    }
                }
                if(by>400){bx=160;by=280;vy=-3;vx=3;}
                cx.clearRect(0,0,320,400);
                for(var j=0;j<bricks.length;j++){
                    if(bricks[j].alive){cx.fillStyle=bricks[j].color;cx.fillRect(bricks[j].x,bricks[j].y,bricks[j].w,bricks[j].h);}
                }
                cx.fillStyle='#00E676';cx.fillRect(px,385,70,10);
                cx.fillStyle='#FFF';cx.beginPath();cx.arc(bx,by,6,0,7);cx.fill();
                cx.fillStyle='#FFF';cx.font='12px sans-serif';cx.fillText('SCORE: '+score,10,18);
                requestAnimationFrame(run);
            }
            run();
            </script></body></html>
        """.trimIndent()
    ),
    OfflineGame(
        id = "pong",
        title = "Neon Pong Champion",
        tag = "SPORTS",
        isLandscape = true,
        glyph = "🏓⚡",
        colors = listOf(Color(0xFF00E676), Color(0xFF00B0FF)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;background:#050510;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#FFF;font-family:sans-serif;touch-action:none;}canvas{background:#000;border:2px solid #00B0FF;border-radius:8px;box-shadow:0 0 25px rgba(0,176,255,0.35);max-width:94vw;max-height:85vh;}</style></head>
            <body><canvas id="p" width="540" height="300"></canvas>
            <script>
            var cv=document.getElementById('p'),cx=cv.getContext('2d'),py=110,ay=110,bx=270,by=150,vx=4,vy=3,ps=0,as=0;
            window.addEventListener('touchmove',function(e){
                var rect=cv.getBoundingClientRect();
                py=e.touches[0].clientY-rect.top-40;
                if(py<0)py=0;if(py>220)py=220;
            });
            function loop(){
                bx+=vx;by+=vy;
                if(by<6||by>294)vy=-vy;
                if(bx<25&&by>=py&&by<=py+80){vx=Math.abs(vx)+0.1;vy+=(by-(py+40))*0.08;}
                if(bx>515&&by>=ay&&by<=ay+80){vx=-Math.abs(vx)-0.1;vy+=(by-(ay+40))*0.08;}
                if(bx<0){as++;bx=270;by=150;vx=4;vy=2;}
                if(bx>540){ps++;bx=270;by=150;vx=-4;vy=2;}
                if(ay+40<by)ay+=2.5;else ay-=2.5;
                cx.clearRect(0,0,540,300);
                cx.strokeStyle='#333';cx.setLineDash([6,6]);cx.beginPath();cx.moveTo(270,0);cx.lineTo(270,300);cx.stroke();cx.setLineDash([]);
                cx.fillStyle='#00E676';cx.fillRect(10,py,12,80);
                cx.fillStyle='#FF1744';cx.fillRect(518,ay,12,80);
                cx.fillStyle='#00B0FF';cx.beginPath();cx.arc(bx,by,7,0,7);cx.fill();
                cx.fillStyle='#FFF';cx.font='bold 22px sans-serif';cx.fillText(ps,220,35);cx.fillText(as,300,35);
                requestAnimationFrame(loop);
            }
            loop();
            </script></body></html>
        """.trimIndent()
    ),
    OfflineGame(
        id = "tictac",
        title = "Glow Tic-Tac AI",
        tag = "BOARD",
        isLandscape = false,
        glyph = "❌⭕",
        colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;background:#0F172A;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#FFF;font-family:sans-serif;touch-action:none;}#g{display:grid;grid-template-columns:repeat(3,90px);grid-template-rows:repeat(3,90px);gap:10px;background:#1E293B;padding:12px;border-radius:16px;box-shadow:0 0 25px rgba(139,92,246,0.3);}.cell{background:#0F172A;border-radius:10px;display:flex;align-items:center;justify-content:center;font-size:38px;font-weight:bold;cursor:pointer;}</style></head>
            <body><h2 style="color:#C084FC;margin-bottom:10px;">GLOW TIC-TAC AI</h2><div id="g"></div><h3 id="st" style="color:#94A3B8;margin-top:14px;">Your Turn (X)</h3>
            <script>
            var b=['','','','','','','','',''],over=false;
            function render(){
                var el=document.getElementById('g');el.innerHTML='';
                b.forEach(function(v,i){
                    var d=document.createElement('div');d.className='cell';
                    d.innerText=v;d.style.color=v==='X'?'#38BDF8':'#F43F5E';
                    d.onclick=function(){if(!b[i]&&!over){b[i]='X';check();if(!over)setTimeout(ai,350);}};
                    el.appendChild(d);
                });
            }
            function check(){
                var wins=[[0,1,2],[3,4,5],[6,7,8],[0,3,6],[1,4,7],[2,5,8],[0,4,8],[2,4,6]];
                for(var w of wins){
                    if(b[w[0]]&&b[w[0]]===b[w[1]]&&b[w[0]]===b[w[2]]){
                        over=true;document.getElementById('st').innerText=b[w[0]]+' WINS! Tap to reset.';
                        document.body.onclick=function(){b=['','','','','','','','',''];over=false;document.getElementById('st').innerText='Your Turn (X)';document.body.onclick=null;render();};
                        return;
                    }
                }
                if(!b.includes('')){over=true;document.getElementById('st').innerText='DRAW! Tap to reset.';document.body.onclick=function(){b=['','','','','','','','',''];over=false;document.getElementById('st').innerText='Your Turn (X)';document.body.onclick=null;render();};}
            }
            function ai(){
                var empty=[];b.forEach(function(v,i){if(!v)empty.push(i);});
                if(empty.length>0){b[empty[Math.floor(Math.random()*empty.length)]]='O';check();render();}
            }
            render();
            </script></body></html>
        """.trimIndent()
    ),
    OfflineGame(
        id = "flappy",
        title = "Cyber Jetpack Dash",
        tag = "ACTION",
        isLandscape = false,
        glyph = "🚀🔥",
        colors = listOf(Color(0xFFFF5E36), Color(0xFFF01445)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;background:#090A0F;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#FFF;font-family:sans-serif;touch-action:none;}canvas{background:#0D1117;border:1px solid #F59E0B;border-radius:10px;box-shadow:0 0 20px rgba(245,158,11,0.25);}</style></head>
            <body><h3 style="margin:5px 0;color:#F59E0B;">CYBER JETPACK</h3><canvas id="c" width="300" height="420"></canvas>
            <script>
            var cv=document.getElementById('c'),cx=cv.getContext('2d'),y=200,vy=0,pipes=[],score=0;
            function addPipe(){pipes.push({x:300,top:Math.floor(Math.random()*160)+40,gap:110});}
            setInterval(addPipe,1800);
            window.addEventListener('touchstart',function(){vy=-5.5;});
            function loop(){
                vy+=0.3;y+=vy;
                cx.clearRect(0,0,300,420);
                cx.fillStyle='#F59E0B';cx.beginPath();cx.arc(60,y,12,0,7);cx.fill();
                cx.fillStyle='#10B981';
                for(var i=0;i<pipes.length;i++){
                    var p=pipes[i];p.x-=2.5;
                    cx.fillRect(p.x,0,40,p.top);
                    cx.fillRect(p.x,p.top+p.gap,40,420-(p.top+p.gap));
                    if((60>p.x&&60<p.x+40)&&(y-10<p.top||y+10>p.top+p.gap)){y=200;vy=0;score=0;pipes=[];break;}
                    if(p.x+40<0){pipes.splice(i,1);score+=5;i--;}
                }
                if(y>420||y<0){y=200;vy=0;score=0;pipes=[];}
                cx.fillStyle='#FFF';cx.font='bold 16px sans-serif';cx.fillText('SCORE: '+score,10,25);
                requestAnimationFrame(loop);
            }
            loop();
            </script></body></html>
        """.trimIndent()
    ),
    OfflineGame(
        id = "fruit",
        title = "Fruit Slice Master",
        tag = "ACTION",
        isLandscape = true,
        glyph = "🍉⚔️",
        colors = listOf(Color(0xFF10B981), Color(0xFF059669)),
        htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
            <style>body{margin:0;background:#1A102F;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;color:#FFF;font-family:sans-serif;touch-action:none;}canvas{background:#0E0720;border:2px solid #EC4899;border-radius:10px;box-shadow:0 0 25px rgba(236,72,153,0.3);max-width:94vw;max-height:85vh;}</style></head>
            <body><canvas id="f" width="500" height="280"></canvas>
            <script>
            var cv=document.getElementById('f'),cx=cv.getContext('2d'),fruits=[],score=0;
            function spawn(){fruits.push({x:Math.random()*420+40,y:280,vx:(Math.random()-0.5)*4,vy:-(Math.random()*4+8),r:22,sliced:false,color:['#22C55E','#EF4444','#F97316','#EAB308'][Math.floor(Math.random()*4)]});}
            setInterval(spawn,1100);
            window.addEventListener('touchmove',function(e){
                var rect=cv.getBoundingClientRect(),tx=e.touches[0].clientX-rect.left,ty=e.touches[0].clientY-rect.top;
                for(var i=0;i<fruits.length;i++){
                    var f=fruits[i];
                    if(!f.sliced&&Math.hypot(tx-f.x,ty-f.y)<f.r+15){f.sliced=true;score+=10;}
                }
            });
            function loop(){
                cx.clearRect(0,0,500,280);
                for(var i=0;i<fruits.length;i++){
                    var f=fruits[i];f.x+=f.vx;f.y+=f.vy;f.vy+=0.18;
                    if(f.sliced){cx.fillStyle='#FFF';cx.fillText('+10',f.x,f.y);}
                    else{cx.fillStyle=f.color;cx.beginPath();cx.arc(f.x,f.y,f.r,0,7);cx.fill();}
                    if(f.y>320){fruits.splice(i,1);i--;}
                }
                cx.fillStyle='#FFF';cx.font='bold 16px sans-serif';cx.fillText('SLICED: '+score,15,25);
                requestAnimationFrame(loop);
            }
            loop();
            </script></body></html>
        """.trimIndent()
    )
)

data class ThemePaletteCategory(
    val title: String,
    val icon: String,
    val description: String,
    val presets: List<VaultThemePreset>
)

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OfflineVaultScreen(
    currentThemeId: String = "pop_2d",
    onThemeSelected: (String) -> Unit = {},
    onGamePlayingStateChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val activity = remember(context) { context.findActivity() }

    var activeOfflineGame by rememberSaveable(stateSaver = GameItemSaver) { mutableStateOf<OfflineGameItem?>(null) }
    var activeGameWebView by remember { mutableStateOf<WebView?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("ALL") }
    var favoriteIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var recentIds by rememberSaveable { mutableStateOf(listOf<String>()) }
    var showAchievements by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // SECTION 3: Pure Local 100% Offline Vault Catalog (Direct Memory Payloads)
    val games = remember { TRUE_OFFLINE_GAMES }

    val categories = remember(games) {
        listOf("ALL", "FAVORITES ❤️", "RECENT 🕒") + games.map { it.category }.distinct()
    }

    val filteredGames = remember(games, searchQuery, selectedCategory, favoriteIds, recentIds) {
        val baseFiltered = games.filter { game ->
            val matchesCategory = when (selectedCategory) {
                "ALL" -> true
                "FAVORITES ❤️" -> favoriteIds.contains(game.id)
                "RECENT 🕒" -> recentIds.contains(game.id)
                else -> game.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isBlank() ||
                    game.title.contains(searchQuery, ignoreCase = true) ||
                    game.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        if (selectedCategory == "RECENT 🕒") {
            baseFiltered.sortedBy { game -> recentIds.indexOf(game.id).let { if (it == -1) Int.MAX_VALUE else it } }
        } else {
            baseFiltered
        }
    }

    // SECTION 4.3: Exit Cleanup function
    fun dismissGame() {
        activeGameWebView?.let { wv ->
            try {
                wv.stopLoading()
                wv.pauseTimers()
                wv.loadUrl("about:blank")
                wv.destroy()
            } catch (_: Exception) {}
        }
        activeGameWebView = null
        activeOfflineGame = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity?.window?.let { win ->
            val insetsController = WindowInsetsControllerCompat(win, win.decorView)
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
        onGamePlayingStateChanged(false)
    }

    // Safe BackHandler so Back button dismisses game overlay instead of killing app
    BackHandler(enabled = activeOfflineGame != null) {
        dismissGame()
    }

    // Window Inset synchronization
    LaunchedEffect(activeOfflineGame) {
        val window = activity?.window
        if (activeOfflineGame != null) {
            if (window != null) {
                val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
            onGamePlayingStateChanged(true)
        } else {
            if (window != null) {
                val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
            onGamePlayingStateChanged(false)
        }
    }

    // Curated theme palette groups
    val themePaletteCategories = remember {
        listOf(
            ThemePaletteCategory(
                title = "Arcade Vibe",
                icon = "⚡",
                description = "High-energy cyber arcade with electric neon glows",
                presets = listOf(
                    VaultThemeManager.presets.first { it.id == "pop_2d" },
                    VaultThemeManager.presets.first { it.id == "cyber_neon" },
                    VaultThemeManager.presets.first { it.id == "hyper_pulse" }
                )
            ),
            ThemePaletteCategory(
                title = "Tropical / Nature",
                icon = "🌴",
                description = "Lush emerald foliage, ocean teal, and tranquil rainforest",
                presets = listOf(
                    VaultThemeManager.presets.first { it.id == "emerald_mint" },
                    VaultThemeManager.presets.first { it.id == "forest_serene" },
                    VaultThemeManager.presets.first { it.id == "teal_splash" }
                )
            ),
            ThemePaletteCategory(
                title = "Retro",
                icon = "🕹️",
                description = "80s synthwave neon, CRT amber phosphor, and vintage 8-bit",
                presets = listOf(
                    VaultThemeManager.presets.first { it.id == "synthwave_84" },
                    VaultThemeManager.presets.first { it.id == "classic_retro" },
                    VaultThemeManager.presets.first { it.id == "amber_terminal" }
                )
            ),
            ThemePaletteCategory(
                title = "Minimalist",
                icon = "◽",
                description = "Distraction-free slate, graphite noir, and clean frost",
                presets = listOf(
                    VaultThemeManager.presets.first { it.id == "slate_minimal" },
                    VaultThemeManager.presets.first { it.id == "deep_focus" },
                    VaultThemeManager.presets.first { it.id == "monochrome_noir" }
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // HEADER BAR
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${filteredGames.size} Verified Offline Casual Simulators",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "⚡ 100% OFFLINE",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showThemeDialog = true
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Text("🎨", fontSize = 16.sp)
                    }

                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showAchievements = true
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Text("🏆", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                placeholder = { Text("Search 20 verified simulators...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // CATEGORY TABS
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
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
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECTION 3.3: 2-COLUMN GRID OF STORE CARDS
            AnimatedContent(
                targetState = filteredGames,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) + slideInVertically(animationSpec = tween(250)) { height -> height / 10 } togetherWith
                            fadeOut(animationSpec = tween(180))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                label = "OfflineGameGridTransition"
            ) { targetGames ->
                if (targetGames.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No simulators found matching \"$searchQuery\"",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale by animateFloatAsState(
                                targetValue = if (isPressed) 0.94f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "CardScaleBounce"
                            )

                            // Card Surface: RoundedCornerShape(18.dp), Dark Obsidian background (#111827), subtle 1dp glow border
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .scale(scale)
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        recentIds = (listOf(game.id) + recentIds.filter { it != game.id }).take(10)
                                        activeOfflineGame = game
                                        onGamePlayingStateChanged(true)

                                        activity?.requestedOrientation = if (game.isLandscape) {
                                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                        } else {
                                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        }
                                    },
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFF111827),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Hero Poster: Fixed 140dp height Box with glowing glyph and gradient backdrop
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                            .background(Brush.linearGradient(game.colors))
                                    ) {
                                        // Glowing central glyph
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .background(
                                                    brush = Brush.radialGradient(
                                                        listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                                                    ),
                                                    shape = CircleShape
                                                )
                                                .align(Alignment.Center),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = game.glyph,
                                                fontSize = 42.sp
                                            )
                                        }

                                        // Overlay bottom gradient scrim (Black.copy(0.70f) to Transparent) for 100% text readability
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.70f))
                                                    )
                                                )
                                        )

                                        // Top-Left Badge: Frosted glass badge with game.tag (9.sp, FontWeight.Black)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Black.copy(alpha = 0.60f),
                                            border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f)),
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = game.tag,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }

                                        // Top-Right Badge: Gold rating pill "★ 4.9"
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Black.copy(alpha = 0.60f),
                                            border = BorderStroke(0.8.dp, Color(0xFFFFD700).copy(alpha = 0.4f)),
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "★ 4.9",
                                                color = Color(0xFFFFD700),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }

                                        // Bottom-Right Badge: Cyan / Amber pill ("📱 PORTRAIT" or "🔄 LANDSCAPE")
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.Black.copy(alpha = 0.65f),
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = if (game.isLandscape) "🔄 LANDSCAPE" else "📱 PORTRAIT",
                                                color = if (game.isLandscape) Color(0xFFFBBF24) else Color(0xFF38BDF8),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Content & Button Footer
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = game.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "HD 60 FPS • 100% Offline",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Full-width Gradient Button: Emerald Green (#00E676) to Sky Blue (#00B0FF)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFF00E676), Color(0xFF00B0FF))
                                                    )
                                                )
                                                .clickable(
                                                    interactionSource = interactionSource,
                                                    indication = null
                                                ) {
                                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                    recentIds = (listOf(game.id) + recentIds.filter { it != game.id }).take(10)
                                                    activeOfflineGame = game
                                                    onGamePlayingStateChanged(true)

                                                    activity?.requestedOrientation = if (game.isLandscape) {
                                                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                                    } else {
                                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "PLAY ▶",
                                                fontSize = 13.sp,
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

        // SECTION 4: FULLSCREEN OFFLINE WEBVIEW PLAYER (ZERO NETWORK / AIRPLANE-SAFE)
        activeOfflineGame?.let { game ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(999f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)
                            setBackgroundColor(android.graphics.Color.BLACK)

                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    view?.evaluateJavascript(
                                        """
                                        document.body.style.margin = '0';
                                        document.body.style.padding = '0';
                                        document.body.style.overflow = 'hidden';
                                        window.dispatchEvent(new Event('resize'));
                                        """.trimIndent(), null
                                    )
                                }
                            }

                            with(settings) {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                allowFileAccess = true
                                allowContentAccess = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                cacheMode = WebSettings.LOAD_NO_CACHE
                            }
                            activeGameWebView = this
                        }
                    },
                    update = { webView ->
                        activeGameWebView = webView
                        if (webView.tag != game.id) {
                            webView.tag = game.id
                            // CRITICAL: Pure local memory load. Never requests network, 100% works in Airplane mode.
                            webView.loadDataWithBaseURL(
                                "https://offline.app",
                                game.htmlContent,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    }
                )

                // Floating Exit Action: Top-right frosted pill button (36.dp, icon ✕)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(14.dp)
                        .zIndex(1000f)
                ) {
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            dismissGame()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.70f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Game",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // THEME CUSTOMIZATION DIALOG
        if (showThemeDialog) {
            Dialog(onDismissRequest = { showThemeDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🎨 App Color Palettes",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Customize the entire app shell look",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { showThemeDialog = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("✕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 420.dp)
                        ) {
                            items(themePaletteCategories) { category ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = category.icon, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = category.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = category.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        category.presets.forEach { preset ->
                                            val isCurrent = currentThemeId == preset.id
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (isCurrent)
                                                            preset.primaryAccent.copy(alpha = 0.20f)
                                                        else
                                                            MaterialTheme.colorScheme.surface
                                                    )
                                                    .border(
                                                        width = if (isCurrent) 1.5.dp else 1.dp,
                                                        color = if (isCurrent) preset.primaryAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable {
                                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                        onThemeSelected(preset.id)
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = preset.name,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isCurrent) preset.primaryAccent else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(preset.backgroundColor)
                                                            .border(0.8.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(preset.surfaceColor)
                                                            .border(0.8.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(preset.primaryAccent)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(preset.secondaryAccent)
                                                    )

                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    if (isCurrent) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Active",
                                                            tint = preset.primaryAccent,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                showThemeDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Apply & Close", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ACHIEVEMENTS DIALOG
        if (showAchievements) {
            Dialog(onDismissRequest = { showAchievements = false }) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏆 Offline Achievements",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val achievements = listOf(
                            Triple("Arcade Pioneer", "Launch your first offline game", recentIds.isNotEmpty()),
                            Triple("Favorite Collector", "Add 3 games to Favorites", favoriteIds.size >= 3),
                            Triple("Marathon Gamer", "Play 5 different games", recentIds.size >= 5),
                            Triple("Vault Master", "Explore 10 different games", recentIds.size >= 10)
                        )

                        achievements.forEach { (title, desc, unlocked) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (unlocked) "🏅" else "🔒", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = title, color = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                }
                                if (unlocked) {
                                    Text(text = "UNLOCKED", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                showAchievements = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
