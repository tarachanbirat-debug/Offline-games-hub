package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class VaultThemePreset(
  val id: String,
  val name: String,
  val category: String, // "Pop & Modern", "Classic Retro", "Neon & Cyber", "Mood & Performance"
  val backgroundColor: Color,
  val surfaceColor: Color,
  val surfaceElevatedColor: Color,
  val surfaceHighlightColor: Color,
  val primaryAccent: Color,
  val secondaryAccent: Color,
  val textColor: Color,
  val textMutedColor: Color
)

object VaultThemeManager {
  val presets: List<VaultThemePreset> = listOf(
    // Pop & Modern
    VaultThemePreset("pop_2d", "Pop 2D Arcade", "Pop & Modern", Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569), Color(0xFFFF6B6B), Color(0xFF38BDF8), Color(0xFFFFFFFF), Color(0xFF94A3B8)),
    VaultThemePreset("emerald_mint", "Emerald Mint", "Pop & Modern", Color(0xFF042F2E), Color(0xFF064E3B), Color(0xFF065F46), Color(0xFF047857), Color(0xFF10B981), Color(0xFF34D399), Color(0xFFFFFFFF), Color(0xFFA7F3D0)),
    VaultThemePreset("royal_gold", "Royal Obsidian Gold", "Pop & Modern", Color(0xFF121212), Color(0xFF1E1E1E), Color(0xFF2C2C2C), Color(0xFF3D3D3D), Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFFFFFFFF), Color(0xFFA3A3A3)),
    VaultThemePreset("candy_pop", "Candy Bubblegum", "Pop & Modern", Color(0xFF3B0764), Color(0xFF581C87), Color(0xFF6B21A8), Color(0xFF7E22CE), Color(0xFFFF2E63), Color(0xFFF43F5E), Color(0xFFFFFFFF), Color(0xFFFBCFE8)),
    VaultThemePreset("midnight_purple", "Midnight Cosmos", "Pop & Modern", Color(0xFF0B0F19), Color(0xFF131B2E), Color(0xFF1D2A4A), Color(0xFF283B6B), Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFFFFFFF), Color(0xFF94A3B8)),
    VaultThemePreset("speedway_orange", "Speedway Nitro", "Pop & Modern", Color(0xFF180E05), Color(0xFF2E1C0A), Color(0xFF472A0F), Color(0xFF613915), Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFFFFFFF), Color(0xFFFFCC80)),
    VaultThemePreset("coral_sunset", "Coral Sunset", "Pop & Modern", Color(0xFF1F1212), Color(0xFF381C1C), Color(0xFF522828), Color(0xFF6E3636), Color(0xFFFF7043), Color(0xFFFFAB91), Color(0xFFFFFFFF), Color(0xFFFFCCBC)),
    VaultThemePreset("azure_sky", "Azure Sky Dream", "Pop & Modern", Color(0xFF0C1929), Color(0xFF162A45), Color(0xFF233D63), Color(0xFF315385), Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFFFFFFFF), Color(0xFFBAE6FD)),
    VaultThemePreset("lime_punch", "Lime Punch", "Pop & Modern", Color(0xFF0F1F0B), Color(0xFF1A3813), Color(0xFF26541C), Color(0xFF347326), Color(0xFF84CC16), Color(0xFFA3E635), Color(0xFFFFFFFF), Color(0xFFECFCCB)),
    VaultThemePreset("lavender_bliss", "Lavender Bliss", "Pop & Modern", Color(0xFF171026), Color(0xFF2A1C47), Color(0xFF3E2A6B), Color(0xFF543A8F), Color(0xFFC084FC), Color(0xFFE879F9), Color(0xFFFFFFFF), Color(0xFFF3E8FF)),
    VaultThemePreset("teal_splash", "Teal Splash", "Pop & Modern", Color(0xFF022326), Color(0xFF053E42), Color(0xFF0A5C63), Color(0xFF117D87), Color(0xFF14B8A6), Color(0xFF2DD4BF), Color(0xFFFFFFFF), Color(0xFFCCFBF1)),
    VaultThemePreset("rose_gold", "Rose Gold Elite", "Pop & Modern", Color(0xFF211317), Color(0xFF3B2129), Color(0xFF57313D), Color(0xFF754353), Color(0xFFFB7185), Color(0xFFFDA4AF), Color(0xFFFFFFFF), Color(0xFFFFE4E6)),

    // Classic Retro
    VaultThemePreset("classic_retro", "Classic Retro 8-Bit", "Classic Retro", Color(0xFF0A0A0A), Color(0xFF141414), Color(0xFF222222), Color(0xFF333333), Color(0xFFFFB000), Color(0xFF00FF66), Color(0xFFFFB000), Color(0xFF888888)),
    VaultThemePreset("matrix_green", "Matrix Terminal", "Classic Retro", Color(0xFF022002), Color(0xFF043804), Color(0xFF075907), Color(0xFF0B840B), Color(0xFF00FF66), Color(0xFF4ADE80), Color(0xFF00FF66), Color(0xFF86EFAC)),
    VaultThemePreset("synthwave_84", "1984 Synthwave", "Classic Retro", Color(0xFF110C1D), Color(0xFF1F1535), Color(0xFF2D1F4D), Color(0xFF3E2B6B), Color(0xFFFF71CE), Color(0xFF01CDFE), Color(0xFFFFFFFF), Color(0xFFB9B4D9)),
    VaultThemePreset("gameboy_classic", "Gameboy DMG-01", "Classic Retro", Color(0xFF8B956D), Color(0xFF7C885B), Color(0xFF6A754B), Color(0xFF59633A), Color(0xFF0F380F), Color(0xFF306230), Color(0xFF0F380F), Color(0xFF306230)),
    VaultThemePreset("amber_terminal", "Fallout Amber", "Classic Retro", Color(0xFF1A1200), Color(0xFF332400), Color(0xFF4D3600), Color(0xFF664800), Color(0xFFFFB000), Color(0xFFFFD166), Color(0xFFFFB000), Color(0xFFE5A100)),
    VaultThemePreset("arcade_cabinet", "Arcade Cabinet Red", "Classic Retro", Color(0xFF1F0505), Color(0xFF3D0A0A), Color(0xFF5C1010), Color(0xFF7A1919), Color(0xFFFF3333), Color(0xFFFF6666), Color(0xFFFFFFFF), Color(0xFFFFCCCC)),
    VaultThemePreset("c64_blue", "Commodore 64 Blue", "Classic Retro", Color(0xFF0A0A2E), Color(0xFF141452), Color(0xFF20207A), Color(0xFF2E2EA3), Color(0xFF7B7BFF), Color(0xFFA3A3FF), Color(0xFF7B7BFF), Color(0xFFCCCCFF)),
    VaultThemePreset("atari_2600", "Atari 2600 Wood", "Classic Retro", Color(0xFF241408), Color(0xFF3D2210), Color(0xFF5C3318), Color(0xFF7A4522), Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFFFFFFFF), Color(0xFFFDE68A)),
    VaultThemePreset("nes_gray", "NES Classic Gray", "Classic Retro", Color(0xFF1C1C1C), Color(0xFF2B2B2B), Color(0xFF3A3A3A), Color(0xFF4D4D4D), Color(0xFFE52521), Color(0xFF0070EC), Color(0xFFFFFFFF), Color(0xFFB0B0B0)),
    VaultThemePreset("sega_blue", "Sega Genesis Blue", "Classic Retro", Color(0xFF05051C), Color(0xFF0D0D38), Color(0xFF171759), Color(0xFF24247A), Color(0xFF0099FF), Color(0xFF00FFFF), Color(0xFFFFFFFF), Color(0xFF99CCFF)),
    VaultThemePreset("neon_pixel", "Pixelated Neon", "Classic Retro", Color(0xFF0C0714), Color(0xFF1A0F2B), Color(0xFF2A1945), Color(0xFF3C2561), Color(0xFFFF0055), Color(0xFF00FFFF), Color(0xFFFFFFFF), Color(0xFFD4BFFF)),
    VaultThemePreset("retro_phosphor", "Phosphor Green", "Classic Retro", Color(0xFF001100), Color(0xFF002E00), Color(0xFF004D00), Color(0xFF007500), Color(0xFF00FF00), Color(0xFF66FF66), Color(0xFF00FF00), Color(0xFF88FF88)),
    VaultThemePreset("monochrome_noir", "Monochrome Noir", "Classic Retro", Color(0xFF000000), Color(0xFF121212), Color(0xFF262626), Color(0xFF3D3D3D), Color(0xFFFFFFFF), Color(0xFFCCCCCC), Color(0xFFFFFFFF), Color(0xFF999999)),
    VaultThemePreset("cyber_vhs", "Cyber VHS Glitch", "Classic Retro", Color(0xFF120A1F), Color(0xFF221338), Color(0xFF351C57), Color(0xFF4B287A), Color(0xFFFF007F), Color(0xFF7928CA), Color(0xFFFFFFFF), Color(0xFFD8B4FE)),

    // Neon & Cyber
    VaultThemePreset("cyber_neon", "Cyberpunk Neon", "Neon & Cyber", Color(0xFF090614), Color(0xFF130E24), Color(0xFF1D1638), Color(0xFF2D2254), Color(0xFF00F0FF), Color(0xFFEC4899), Color(0xFFFFFFFF), Color(0xFF94A3B8)),
    VaultThemePreset("sunset_vaporwave", "Sunset Vaporwave", "Neon & Cyber", Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF3730A3), Color(0xFF4338CA), Color(0xFFF472B6), Color(0xFF38BDF8), Color(0xFFFFFFFF), Color(0xFFC7D2FE)),
    VaultThemePreset("crimson_overdrive", "Crimson Overdrive", "Neon & Cyber", Color(0xFF1A0505), Color(0xFF2D0A0A), Color(0xFF451010), Color(0xFF5E1919), Color(0xFFEF4444), Color(0xFFF97316), Color(0xFFFFFFFF), Color(0xFFFCA5A5)),
    VaultThemePreset("cyber_teal", "Cyber Teal Horizon", "Neon & Cyber", Color(0xFF001F24), Color(0xFF003842), Color(0xFF005261), Color(0xFF007385), Color(0xFF00E5FF), Color(0xFF14B8A6), Color(0xFFFFFFFF), Color(0xFF99F6E4)),
    VaultThemePreset("neon_lime", "Laser Neon Lime", "Neon & Cyber", Color(0xFF051905), Color(0xFF0D2E0D), Color(0xFF144714), Color(0xFF1E661E), Color(0xFF84CC16), Color(0xFF10B981), Color(0xFFFFFFFF), Color(0xFFBEF264)),
    VaultThemePreset("electric_violet", "Electric Violet Pulse", "Neon & Cyber", Color(0xFF0F071E), Color(0xFF1D0E38), Color(0xFF2C1654), Color(0xFF3F2173), Color(0xFFA855F7), Color(0xFFC084FC), Color(0xFFFFFFFF), Color(0xFFE9D5FF)),
    VaultThemePreset("plasma_cyan", "Plasma Cyan Arc", "Neon & Cyber", Color(0xFF031926), Color(0xFF072F47), Color(0xFF0D4B70), Color(0xFF15699C), Color(0xFF00FFFF), Color(0xFF38BDF8), Color(0xFFFFFFFF), Color(0xFFBAE6FD)),
    VaultThemePreset("laser_magenta", "Laser Magenta", "Neon & Cyber", Color(0xFF1F071A), Color(0xFF380D2F), Color(0xFF571648), Color(0xFF7A2065), Color(0xFFFF00FF), Color(0xFFF472B6), Color(0xFFFFFFFF), Color(0xFFFBCFE8)),
    VaultThemePreset("nuclear_yellow", "Nuclear Yellow Hazard", "Neon & Cyber", Color(0xFF1F1A02), Color(0xFF383004), Color(0xFF594B08), Color(0xFF7A680C), Color(0xFFEAB308), Color(0xFFFACC15), Color(0xFFFFFFFF), Color(0xFFFEF08A)),
    VaultThemePreset("matrix_cyan", "Matrix Quantum", "Neon & Cyber", Color(0xFF021C24), Color(0xFF043342), Color(0xFF085166), Color(0xFF0E7594), Color(0xFF00F0FF), Color(0xFF2DD4BF), Color(0xFFFFFFFF), Color(0xFF99F6E4)),
    VaultThemePreset("cyber_grid", "Cyber Grid Neon", "Neon & Cyber", Color(0xFF0A0F1D), Color(0xFF141F38), Color(0xFF20325C), Color(0xFF2E4885), Color(0xFF3B82F6), Color(0xFF60A5FA), Color(0xFFFFFFFF), Color(0xFF93C5FD)),
    VaultThemePreset("neon_flame", "Neon Flame", "Neon & Cyber", Color(0xFF1F0C02), Color(0xFF381704), Color(0xFF5C2607), Color(0xFF80380A), Color(0xFFFF5722), Color(0xFFFF9100), Color(0xFFFFFFFF), Color(0xFFFFCC80)),
    VaultThemePreset("hyper_pulse", "Hyper Pulse", "Neon & Cyber", Color(0xFF140324), Color(0xFF260847), Color(0xFF3C0F70), Color(0xFF561A9E), Color(0xFFD946EF), Color(0xFFEC4899), Color(0xFFFFFFFF), Color(0xFFF5D0FE)),

    // Mood & Performance
    VaultThemePreset("arctic_frost", "Arctic Frost", "Mood & Performance", Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569), Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFF8FAFC), Color(0xFF94A3B8)),
    VaultThemePreset("solar_flare", "Solar Flare", "Mood & Performance", Color(0xFF1F1300), Color(0xFF3D2600), Color(0xFF5C3800), Color(0xFF7A4A00), Color(0xFFF97316), Color(0xFFEAB308), Color(0xFFFFFFFF), Color(0xFFFED7AA)),
    VaultThemePreset("pastel_dream", "Pastel Dream", "Mood & Performance", Color(0xFF1E1B2E), Color(0xFF2A2640), Color(0xFF3A3459), Color(0xFF4C4473), Color(0xFFF472B6), Color(0xFF67E8F9), Color(0xFFFFFFFF), Color(0xFFDDD6FE)),
    VaultThemePreset("zen_matcha", "Zen Matcha Calm", "Mood & Performance", Color(0xFF131A15), Color(0xFF213025), Color(0xFF324A38), Color(0xFF46664F), Color(0xFF34D399), Color(0xFF6EE7B7), Color(0xFFFFFFFF), Color(0xFFA7F3D0)),
    VaultThemePreset("deep_focus", "Deep Focus Graphite", "Mood & Performance", Color(0xFF0D0D0D), Color(0xFF1A1A1A), Color(0xFF2B2B2B), Color(0xFF3D3D3D), Color(0xFF60A5FA), Color(0xFF93C5FD), Color(0xFFFFFFFF), Color(0xFFA3A3A3)),
    VaultThemePreset("coffee_breeze", "Warm Coffee Calm", "Mood & Performance", Color(0xFF1A1412), Color(0xFF2E2420), Color(0xFF453630), Color(0xFF5E4B42), Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFFFFFFFF), Color(0xFFD7CCC8)),
    VaultThemePreset("midnight_blue", "Midnight Calm Blue", "Mood & Performance", Color(0xFF0B132B), Color(0xFF1C2541), Color(0xFF3A506B), Color(0xFF47597E), Color(0xFF6FFFE9), Color(0xFF5BC0BE), Color(0xFFFFFFFF), Color(0xFFB2DFDB)),
    VaultThemePreset("sunset_relax", "Sunset Relax", "Mood & Performance", Color(0xFF24121F), Color(0xFF3D1F34), Color(0xFF5C2F4E), Color(0xFF7A4069), Color(0xFFFF758C), Color(0xFFFF7EB3), Color(0xFFFFFFFF), Color(0xFFF8BBD0)),
    VaultThemePreset("forest_serene", "Forest Serene", "Mood & Performance", Color(0xFF0A1F12), Color(0xFF123620), Color(0xFF1C5230), Color(0xFF297044), Color(0xFF22C55E), Color(0xFF4ADE80), Color(0xFFFFFFFF), Color(0xFFBBF7D0)),
    VaultThemePreset("autumn_glow", "Autumn Warmth", "Mood & Performance", Color(0xFF21140C), Color(0xFF382316), Color(0xFF543421), Color(0xFF75492F), Color(0xFFEA580C), Color(0xFFF97316), Color(0xFFFFFFFF), Color(0xFFFED7AA)),
    VaultThemePreset("slate_minimal", "Slate Minimalist", "Mood & Performance", Color(0xFF111827), Color(0xFF1F2937), Color(0xFF374151), Color(0xFF4B5563), Color(0xFF9CA3AF), Color(0xFFD1D5DB), Color(0xFFFFFFFF), Color(0xFF9CA3AF)),
    VaultThemePreset("quantum_starlight", "Quantum Starlight", "Mood & Performance", Color(0xFF080C14), Color(0xFF10192D), Color(0xFF1B2A4E), Color(0xFF283F73), Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFFFFFFF), Color(0xFFC7D2FE))
  )
}
// Cache invalidation comment

