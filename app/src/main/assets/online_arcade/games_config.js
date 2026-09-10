/**
 * =============================================================================
 * ONLINE ARCADE & OFFLINE GAMES BUNDLE CONFIGURATION
 * =============================================================================
 * Includes both high-graphics offline bundled HTML5 games and verified
 * instant-play web arcade games.
 * =============================================================================
 */

const ONLINE_GAMES = [
  // --- High Graphics Offline Bundled Games (100% Offline, Zero Lag) ---
  {
    id: "cyber-neon-racer",
    title: "Cyber Neon Racer 3D",
    category: "Action",
    isOfflineBundle: true,
    thumbnail: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
    embedUrl: "games/cyber_racer/index.html",
    developer: "Vault Studio (Offline Ready)",
    rating: "4.9",
    description: "High-speed synthwave 3D highway racer. Dodge traffic, boost nitro!"
  },
  {
    id: "galaxy-defender",
    title: "Galaxy Space Defender",
    category: "Action",
    isOfflineBundle: true,
    thumbnail: "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
    embedUrl: "games/galaxy_defender/index.html",
    developer: "Vault Studio (Offline Ready)",
    rating: "4.8",
    description: "Battle alien armadas, dodge lasers, and upgrade fighter shields!"
  },
  {
    id: "tile-2048-web",
    title: "2048 Master Deluxe",
    category: "Puzzle",
    isOfflineBundle: true,
    thumbnail: "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=80",
    embedUrl: "games/tile_2048/index.html",
    developer: "Vault Studio (Offline Ready)",
    rating: "4.9",
    description: "Slide and merge numbers to reach the legendary 2048 tile."
  },

  // --- High Quality Online HTML5 Games ---
  {
    id: "checkers-classic",
    title: "Classic Checkers 3D",
    category: "Board",
    isOfflineBundle: false,
    thumbnail: "https://playpager.com/embed/checkers/minicheckers.jpg",
    embedUrl: "https://playpager.com/embed/checkers/game/index.html",
    developer: "Playpager Online",
    rating: "4.8",
    description: "Classic checkers board game with single player vs AI and 2 player modes."
  },
  {
    id: "chess-master",
    title: "Master Chess 3D",
    category: "Board",
    isOfflineBundle: false,
    thumbnail: "https://playpager.com/embed/chess/minichess.jpg",
    embedUrl: "https://playpager.com/embed/chess/game/index.html",
    developer: "Playpager Online",
    rating: "4.9",
    description: "Full-featured Chess game with smooth touch moves and smart AI opponent."
  },
  {
    id: "clumsy-bird",
    title: "Clumsy Flappy Bird",
    category: "Arcade",
    isOfflineBundle: false,
    thumbnail: "https://ellisonleao.github.io/clumsy-bird/data/img/bg.png",
    embedUrl: "https://ellisonleao.github.io/clumsy-bird/",
    developer: "MelonJS Arcade",
    rating: "4.7",
    description: "Addictive tap-to-fly bird arcade with silky smooth 60fps physics."
  },
  {
    id: "t-rex-dino",
    title: "T-Rex Chrome Runner",
    category: "Action",
    isOfflineBundle: false,
    thumbnail: "https://images.unsplash.com/photo-1579373903781-fd5c0c30c4cd?w=600&auto=format&fit=crop&q=80",
    embedUrl: "https://wayou.github.io/t-rex-runner/",
    developer: "Chromium Arcade",
    rating: "4.9",
    description: "The iconic endless desert runner. Jump cacti, duck pterodactyls!"
  },
  {
    id: "alien-invasion",
    title: "Space Alien Invasion",
    category: "Action",
    isOfflineBundle: false,
    thumbnail: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
    embedUrl: "https://cykod.github.io/AlienInvasion/",
    developer: "HTML5 Retro Arcade",
    rating: "4.6",
    description: "Classic retro arcade space shooter. Blast incoming alien waves!"
  },
  {
    id: "connect-4",
    title: "Connect 4 (Four in a Row)",
    category: "Board",
    isOfflineBundle: false,
    thumbnail: "https://images.unsplash.com/photo-1611996575749-79a3a250f948?w=600&auto=format&fit=crop&q=80",
    embedUrl: "https://kenrick95.github.io/c4/",
    developer: "Kenrick Indie",
    rating: "4.7",
    description: "Drop tokens into the vertical grid to connect 4 matching pieces."
  },
  {
    id: "solitaire-pro",
    title: "Klondike Solitaire",
    category: "Puzzle",
    isOfflineBundle: false,
    thumbnail: "https://playpager.com/embed/solitaire/solitairegame.jpg",
    embedUrl: "https://playpager.com/embed/solitaire/game/index.html",
    developer: "Playpager Online",
    rating: "4.8",
    description: "The classic card patience game with smooth card drags and auto-complete."
  },
  {
    id: "sudoku-puzzle",
    title: "Classic Sudoku Master",
    category: "Puzzle",
    isOfflineBundle: false,
    thumbnail: "https://playpager.com/embed/sudoku/sudokugame.jpg",
    embedUrl: "https://playpager.com/embed/sudoku/game/index.html",
    developer: "Playpager Online",
    rating: "4.8",
    description: "Brain-sharpening numerical logic puzzle with Easy, Medium and Hard grids."
  },
  {
    id: "reversi-othello",
    title: "Reversi (Othello)",
    category: "Board",
    isOfflineBundle: false,
    thumbnail: "https://playpager.com/embed/reversi/miniothello.jpg",
    embedUrl: "https://playpager.com/embed/reversi/game/index.html",
    developer: "Playpager Online",
    rating: "4.7",
    description: "Outflank your opponent's discs to flip them to your color."
  },
  {
    id: "kick-the-pinata",
    title: "Kick The Piñata!",
    category: "Arcade",
    isOfflineBundle: false,
    thumbnail: "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
    embedUrl: "https://html-classic.itch.zone/html/19098024/index.html",
    developer: "Eduard Scarpato (Itch.io)",
    rating: "4.6",
    description: "Frenetic party arcade game with funny physics and candy explosions."
  },
  {
    id: "the-freak-circus",
    title: "The Freak Circus",
    category: "Arcade",
    isOfflineBundle: false,
    thumbnail: "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600&auto=format&fit=crop&q=80",
    embedUrl: "https://html-classic.itch.zone/html/16572088/index.html",
    developer: "Garula (Itch.io)",
    rating: "4.7",
    description: "Atmospheric indie arcade adventure with unique visual art."
  },
  {
    id: "snake-retro",
    title: "Retro Snake 2.0",
    category: "Arcade",
    isOfflineBundle: false,
    thumbnail: "https://images.unsplash.com/photo-1628277613967-6abca504d0ac?w=600&auto=format&fit=crop&q=80",
    embedUrl: "https://hyj-hello.github.io/snake-game-js/",
    developer: "HTML5 Classic",
    rating: "4.8",
    description: "Feed your snake and grow longer without crashing into the borders."
  }
];

if (typeof module !== 'undefined' && module.exports) {
  module.exports = ONLINE_GAMES;
}
