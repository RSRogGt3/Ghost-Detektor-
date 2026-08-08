package com.example.data

import androidx.compose.ui.graphics.Color

enum class AnomalyType(val displayName: String) {
    THERMAL_HOTSPOT("THERMISCHER HOTSPOT"),
    COLD_SPOT("KÄLTE-ANOMALIE"),
    BRIGHTNESS_SPIKE("LICHT-REFLEKTION"),
    MOTION_SHIFT("SPEKTRAL-BEWEGUNG"),
    SPECTRAL_FLARE("INFRAROT-FLARE")
}

data class CameraAnomaly(
    val id: String,
    val xRatio: Float, // 0.0 .. 1.0 position across frame width
    val yRatio: Float, // 0.0 .. 1.0 position across frame height
    val intensity: Float, // 0.0 .. 1.0
    val type: AnomalyType,
    val tempDeltaC: Float,
    val label: String,
    val emuValueMg: Float = 15f + intensity * 60f
)
