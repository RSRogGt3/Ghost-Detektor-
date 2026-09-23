package com.example.data

import androidx.compose.ui.graphics.Color

/**
 * Palette for all Infrared & Night-Vision Lights across the scanner HUD,
 * radar sweep light cones, tactical torch beams, and telemetry indicators.
 */
enum class InfraLightColor(
    val id: String,
    val displayName: String,
    val color: Color,
    val glowColor: Color,
    val hexCode: String
) {
    CYAN(
        id = "cyan",
        displayName = "CYAN",
        color = Color(0xFF00FFCC),
        glowColor = Color(0xFF80FFE6),
        hexCode = "#00FFCC"
    ),
    NEON_GREEN(
        id = "green",
        displayName = "GRÜN",
        color = Color(0xFF00FF66),
        glowColor = Color(0xFF66FFAA),
        hexCode = "#00FF66"
    ),
    ICE_BLUE(
        id = "blue",
        displayName = "BLAU",
        color = Color(0xFF00A8FF),
        glowColor = Color(0xFF88EEFF),
        hexCode = "#00A8FF"
    ),
    AMBER_GOLD(
        id = "amber",
        displayName = "AMBER",
        color = Color(0xFFFFB300),
        glowColor = Color(0xFFFFDD66),
        hexCode = "#FFB300"
    ),
    VIOLET(
        id = "violet",
        displayName = "VIOLETT",
        color = Color(0xFFBB33FF),
        glowColor = Color(0xFFEE88FF),
        hexCode = "#BB33FF"
    ),
    WHITE_PHOSPHOR(
        id = "phosphor",
        displayName = "PHOSPHOR",
        color = Color(0xFFE0FFFF),
        glowColor = Color(0xFFFFFFFF),
        hexCode = "#E0FFFF"
    )
}
