/**
 * =============================================================================
 * ONLINE ARCADE - GAME CONFIGURATION ARRAY
 * =============================================================================
 * Add your game titles, thumbnails, and embed URLs here.
 * Supports GameDistribution, Itch.io, Poki, CrazyGames, WShareIt, and any HTML5 embed.
 * 
 * Instructions to add a game:
 * Simply add a new object to the ONLINE_GAMES array below:
 * {
 *   id: "my-game-id",
 *   title: "Game Title",
 *   category: "Action / Racing / Puzzle / Arcade",
 *   thumbnail: "https://example.com/thumb.jpg",
 *   embedUrl: "https://html5.gamedistribution.com/your-game-id/",
 *   developer: "Studio Name",
 *   description: "Short gameplay description"
 * }
 * =============================================================================
 */

const ONLINE_GAMES = [
  {
    id: "checkers-classic",
    title: "Classic Checkers 3D",
    category: "Board",
    thumbnail: "https://playpager.com/embed/checkers/minicheckers.jpg",
    embedUrl: "https://playpager.com/embed/checkers/game/index.html",
    developer: "Playpager Online",
    description: "Classic checkers board game with single player vs AI and 2 player modes."
  },
  {
    id: "chess-master",
    title: "Master Chess",
    category: "Board",
    thumbnail: "https://playpager.com/embed/chess/minichess.jpg",
    embedUrl: "https://playpager.com/embed/chess/game/index.html",
    developer: "Playpager Online",
    description: "Full-featured Chess game with smooth touch moves and smart AI opponent."
  },
  {
    id: "clumsy-bird",
    title: "Clumsy Flappy Bird",
    category: "Arcade",
    thumbnail: "https://ellisonleao.github.io/clumsy-bird/data/img/bg.png",
    embedUrl: "https://ellisonleao.github.io/clumsy-bird/",
    developer: "MelonJS Arcade",
    description: "Addictive tap-to-fly bird arcade with silky smooth 60fps physics."
  },
  {
    id: "t-rex-dino",
    title: "T-Rex Chrome Runner",
    category: "Action",
    thumbnail: "https://playpager.com/embed/checkers/minicheckers.jpg",
    embedUrl: "https://wayou.github.io/t-rex-runner/",
    developer: "Chromium Arcade",
    description: "The iconic endless desert runner. Jump cacti, duck pterodactyls!"
  },
  {
    id: "alien-invasion",
    title: "Space Alien Invasion",
    category: "Action",
    thumbnail: "https://playpager.com/embed/reversi/miniothello.jpg",
    embedUrl: "https://cykod.github.io/AlienInvasion/",
    developer: "HTML5 Retro Arcade",
    description: "Classic retro arcade space shooter. Blast incoming alien waves!"
  },
  {
    id: "connect-4",
    title: "Connect 4 (Four in a Row)",
    category: "Board",
    thumbnail: "https://playpager.com/embed/reversi/miniothello.jpg",
    embedUrl: "https://kenrick95.github.io/c4/",
    developer: "Kenrick Indie",
    description: "Drop tokens into the vertical grid to connect 4 matching pieces."
  },
  {
    id: "solitaire-pro",
    title: "Klondike Solitaire",
    category: "Puzzle",
    thumbnail: "https://playpager.com/embed/solitaire/solitairegame.jpg",
    embedUrl: "https://playpager.com/embed/solitaire/game/index.html",
    developer: "Playpager Online",
    description: "The classic card patience game with smooth card drags and auto-complete."
  },
  {
    id: "sudoku-puzzle",
    title: "Classic Sudoku Master",
    category: "Puzzle",
    thumbnail: "https://playpager.com/embed/sudoku/sudokugame.jpg",
    embedUrl: "https://playpager.com/embed/sudoku/game/index.html",
    developer: "Playpager Online",
    description: "Brain-sharpening numerical logic puzzle with Easy, Medium and Hard grids."
  },
  {
    id: "reversi-othello",
    title: "Reversi (Othello)",
    category: "Board",
    thumbnail: "https://playpager.com/embed/reversi/miniothello.jpg",
    embedUrl: "https://playpager.com/embed/reversi/game/index.html",
    developer: "Playpager Online",
    description: "Outflank your opponent's discs to flip them to your color."
  },
  {
    id: "kick-the-pinata",
    title: "Kick The Piñata!",
    category: "Arcade",
    thumbnail: "https://playpager.com/embed/sudoku/sudokugame.jpg",
    embedUrl: "https://html-classic.itch.zone/html/19098024/index.html",
    developer: "Eduard Scarpato (Itch.io)",
    description: "Frenetic party arcade game with funny physics and candy explosions."
  },
  {
    id: "the-freak-circus",
    title: "The Freak Circus",
    category: "Arcade",
    thumbnail: "https://playpager.com/embed/checkers/minicheckers.jpg",
    embedUrl: "https://html-classic.itch.zone/html/16572088/index.html",
    developer: "Garula (Itch.io)",
    description: "Atmospheric indie arcade adventure with unique visual art."
  },
  {
    id: "snake-retro",
    title: "Retro Snake 2.0",
    category: "Arcade",
    thumbnail: "https://playpager.com/embed/solitaire/solitairegame.jpg",
    embedUrl: "https://hyj-hello.github.io/snake-game-js/",
    developer: "HTML5 Classic",
    description: "Feed your snake and grow longer without crashing into the borders."
  }
];

// Export for Node/ESM if needed, otherwise available on window
if (typeof module !== 'undefined' && module.exports) {
  module.exports = ONLINE_GAMES;
}
