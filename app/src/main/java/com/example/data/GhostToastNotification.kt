package com.example.data

import androidx.compose.ui.graphics.Color

enum class ToastNotificationType {
    COIN_EARNED,           // 🪙 +35 Geister-Coins
    MILESTONE_UNLOCKED,     // 🏆 Meilenstein erreicht! 5 Geister gefangen!
    MILESTONE_CLAIMED,      // 🎁 Belohnung eingelöst! +150 Coins & Titel
    MILESTONE_PROGRESS,     // ⚡ Fortschritt: 3/5 Dämonen
    UPGRADE_PURCHASED,      // 🛒 Upgrade aktiviert!
    DAILY_BONUS,            // ☀️ Täglicher Login-Bonus
    AI_DIAGNOSTIC           // 🤖 KI-Diagnose & Sprachmodell-Status
}

data class GhostToastNotification(
    val id: Long = System.currentTimeMillis(),
    val type: ToastNotificationType,
    val iconEmoji: String,
    val title: String,
    val description: String,
    val badgeColorHex: Long = 0xFFFFD700,
    val actionText: String? = null,
    val targetDestinationName: String? = null, // e.g. "MISSIONS"
    val durationMs: Long = 3800L
) {
    val badgeColor: Color get() = Color(badgeColorHex)
}
