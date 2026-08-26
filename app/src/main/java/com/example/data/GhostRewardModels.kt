package com.example.data

import androidx.compose.ui.graphics.Color

enum class RewardCategory(val label: String, val icon: String) {
    ALL("ALLE ERFOLGE", "🏆"),
    GHOSTS("5+ GEISTER", "👻"),
    DEMONS("5+ DÄMONEN", "🔥"),
    VAMPIRES("5+ VAMPIRE", "🧛"),
    DIMENSIONS("5+ PORTALE", "🌀"),
    EVP("5+ EVP-FUNK", "🎙️"),
    SPECIAL("SPEZIAL & MEISTER", "💎")
}

data class GhostRewardMilestone(
    val id: String,
    val title: String,
    val description: String,
    val category: RewardCategory,
    val targetCount: Int,
    val coinReward: Int,
    val xpReward: Int,
    val badgeIcon: String,
    val unlockedTitle: String,
    val perkDescription: String,
    val colorHex: Long = 0xFF00FF88
) {
    val color: Color get() = Color(colorHex)
}

data class GhostShopUpgrade(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val basePrice: Int,
    val priceMultiplier: Float,
    val maxLevel: Int,
    val colorHex: Long,
    val getLevelDescription: (level: Int) -> String
) {
    val color: Color get() = Color(colorHex)

    fun getCostForLevel(currentLevel: Int): Int {
        if (currentLevel >= maxLevel) return 0
        return (basePrice * Math.pow(priceMultiplier.toDouble(), currentLevel.toDouble())).toInt()
    }
}

object GhostRewardCatalog {

    val allMilestones: List<GhostRewardMilestone> = listOf(
        // ==================== GEISTER-MEILENSTEINE ====================
        GhostRewardMilestone(
            id = "ghost_5",
            title = "5 Geister-Bändiger Paket",
            description = "Fange oder erlöse 5 Geister mit dem Spektral-Radar.",
            category = RewardCategory.GHOSTS,
            targetCount = 50,
            coinReward = 150,
            xpReward = 200,
            badgeIcon = "👻",
            unlockedTitle = "Geister-Bändiger I",
            perkDescription = "+10% Ecto-Coin Bonus beim Fangen von Geistern",
            colorHex = 0xFF00FF88
        ),
        GhostRewardMilestone(
            id = "ghost_10",
            title = "10 Geister-Jäger Auszeichnung",
            description = "Fange oder erlöse 10 paranormale Geister & Schattenwesen.",
            category = RewardCategory.GHOSTS,
            targetCount = 100,
            coinReward = 300,
            xpReward = 450,
            badgeIcon = "🎖️",
            unlockedTitle = "Spektral-Jäger II",
            perkDescription = "+15% Radar-Präzision für Infrarot-Anomalien",
            colorHex = 0xFF00FF88
        ),
        GhostRewardMilestone(
            id = "ghost_25",
            title = "25 Geister-Meister Trophäe",
            description = "Bändige 25 Geister und beweise deine parapsychologische Meisterschaft.",
            category = RewardCategory.GHOSTS,
            targetCount = 250,
            coinReward = 750,
            xpReward = 1000,
            badgeIcon = "👑",
            unlockedTitle = "Großmeister der Geisterwelt",
            perkDescription = "+25% schnellere Seelen-Harmonisierung",
            colorHex = 0xFF00FF88
        ),
        GhostRewardMilestone(
            id = "ghost_50",
            title = "50 Geister-Legenden Pokal",
            description = "Erfasse und erlöse 50 Geister in der irdischen Sphäre.",
            category = RewardCategory.GHOSTS,
            targetCount = 500,
            coinReward = 1500,
            xpReward = 2500,
            badgeIcon = "💎",
            unlockedTitle = "Ewiger Geister-Hüter",
            perkDescription = "+50% Ecto-Coin Ertrag bei allen Spektral-Scans",
            colorHex = 0xFF00FF88
        ),

        // ==================== DÄMONEN-MEILENSTEINE ====================
        GhostRewardMilestone(
            id = "demon_1",
            title = "Erster Dämonen-Bann",
            description = "Fange deinen ersten infernalen Dämon in der Spektral-Falle.",
            category = RewardCategory.DEMONS,
            targetCount = 10,
            coinReward = 100,
            xpReward = 150,
            badgeIcon = "🔥",
            unlockedTitle = "Novize des Exorzismus",
            perkDescription = "+10% Schutz gegen Dämonen-EMF-Spikes",
            colorHex = 0xFFFF0055
        ),
        GhostRewardMilestone(
            id = "demon_5",
            title = "5 Dämonen Höllenfeuer-Paket",
            description = "Isoliere 5 Höllendämonen oder Arch-Dämonen im Containment-Tresor.",
            category = RewardCategory.DEMONS,
            targetCount = 50,
            coinReward = 250,
            xpReward = 350,
            badgeIcon = "👿",
            unlockedTitle = "Dämonen-Inquisitor",
            perkDescription = "Spektral-Falle bannt Dämonen 25% widerstandsfähiger",
            colorHex = 0xFFFF0055
        ),
        GhostRewardMilestone(
            id = "demon_15",
            title = "15 Dämonen-Erzbann Trophäe",
            description = "Banne 15 hochgefährliche Dämonen-Entitäten der Gefahrenklasse 5.",
            category = RewardCategory.DEMONS,
            targetCount = 150,
            coinReward = 800,
            xpReward = 1200,
            badgeIcon = "⚔️",
            unlockedTitle = "Hohepriester des Exorzismus",
            perkDescription = "Dämonen-Siegel lädt sich 30% schneller auf",
            colorHex = 0xFFFF0055
        ),
        GhostRewardMilestone(
            id = "demon_30",
            title = "30 Dämonen-Untergang Orden",
            description = "Vernichte den Einfluss von 30 Dämonen und schütze die Lebenden.",
            category = RewardCategory.DEMONS,
            targetCount = 300,
            coinReward = 2000,
            xpReward = 3000,
            badgeIcon = "🔱",
            unlockedTitle = "Erz-Exorzist von Sankt Michael",
            perkDescription = "Doppelte Ecto-Coins bei jedem gefangenen Dämon",
            colorHex = 0xFFFF0055
        ),

        // ==================== VAMPIR-MEILENSTEINE ====================
        GhostRewardMilestone(
            id = "vampire_1",
            title = "Erster Vampir-Pflock",
            description = "Fange deinen ersten Astral-Vampir mit UV-Frequenzen.",
            category = RewardCategory.VAMPIRES,
            targetCount = 10,
            coinReward = 100,
            xpReward = 150,
            badgeIcon = "🧛",
            unlockedTitle = "Vampir-Jäger Lehrling",
            perkDescription = "Ultraviolett-Sensor erfasst Vampir-Kältefelder sofort",
            colorHex = 0xFFBB33FF
        ),
        GhostRewardMilestone(
            id = "vampire_5",
            title = "5 Vampire Blutmond-Paket",
            description = "Überliste und fange 5 Astral-Vampire & Nosferatu-Wesen.",
            category = RewardCategory.VAMPIRES,
            targetCount = 50,
            coinReward = 250,
            xpReward = 350,
            badgeIcon = "🦇",
            unlockedTitle = "Blutmond-Bezwinger",
            perkDescription = "+20% Resistenz gegen nächtliche EMF-Schwankungen",
            colorHex = 0xFFBB33FF
        ),
        GhostRewardMilestone(
            id = "vampire_15",
            title = "15 Vampire Van-Helsing Orden",
            description = "Banne 15 Energie-Vampire und reinige die Aura des Raumes.",
            category = RewardCategory.VAMPIRES,
            targetCount = 150,
            coinReward = 800,
            xpReward = 1200,
            badgeIcon = "🛡️",
            unlockedTitle = "Orden von Van Helsing",
            perkDescription = "Automatisches Licht-Siegel gegen parasitäre Vampire",
            colorHex = 0xFFBB33FF
        ),
        GhostRewardMilestone(
            id = "vampire_30",
            title = "30 Vampire Sonnenlicht-Medaille",
            description = "Isoliere 30 Vampirfürsten im Spektral-Käfig.",
            category = RewardCategory.VAMPIRES,
            targetCount = 300,
            coinReward = 2000,
            xpReward = 3000,
            badgeIcon = "☀️",
            unlockedTitle = "Ewiger Lichtbringer",
            perkDescription = "+50% Ecto-Coins bei Vampir-Fängen",
            colorHex = 0xFFBB33FF
        ),

        // ==================== DIMENSIONEN-MEILENSTEINE ====================
        GhostRewardMilestone(
            id = "portal_1",
            title = "Erster Dimensions-Riss",
            description = "Versiegele dein erstes interdimensionales Portal.",
            category = RewardCategory.DIMENSIONS,
            targetCount = 10,
            coinReward = 80,
            xpReward = 100,
            badgeIcon = "🌀",
            unlockedTitle = "Torwächter Novize",
            perkDescription = "Quanten-Stabilisator kalibriert",
            colorHex = 0xFF00B0FF
        ),
        GhostRewardMilestone(
            id = "portal_5",
            title = "5 Portale Astral-Pionier",
            description = "Schließe 5 gefährliche Risse im Raum-Zeit-Gefüge.",
            category = RewardCategory.DIMENSIONS,
            targetCount = 50,
            coinReward = 200,
            xpReward = 300,
            badgeIcon = "🌌",
            unlockedTitle = "Multiversum-Schildwache",
            perkDescription = "Auto-Portal-Siegel reagiert 35% schneller",
            colorHex = 0xFF00B0FF
        ),
        GhostRewardMilestone(
            id = "portal_15",
            title = "15 Portale Nexus-Wächter",
            description = "Stabilisiere 15 kollabierende Portal-Singularitäten.",
            category = RewardCategory.DIMENSIONS,
            targetCount = 150,
            coinReward = 700,
            xpReward = 1100,
            badgeIcon = "🪐",
            unlockedTitle = "Meister der 5 Dimensionen",
            perkDescription = "Alle 5 Ebenen dauerhaft synchronisiert",
            colorHex = 0xFF00B0FF
        ),

        // ==================== EVP-FUNK MEILENSTEINE ====================
        GhostRewardMilestone(
            id = "evp_1",
            title = "Erster EVP-Stimmenkontakt",
            description = "Empfange deine erste echte Geisterstimme in der Spirit Box.",
            category = RewardCategory.EVP,
            targetCount = 10,
            coinReward = 60,
            xpReward = 80,
            badgeIcon = "📻",
            unlockedTitle = "Äther-Lauscher",
            perkDescription = "Stimmen-Dekoder aktiviert",
            colorHex = 0xFFFFCC00
        ),
        GhostRewardMilestone(
            id = "evp_5",
            title = "5 EVP-Medium Paket",
            description = "Führe 5 verifizierte Dialoge mit dem Jenseits.",
            category = RewardCategory.EVP,
            targetCount = 50,
            coinReward = 180,
            xpReward = 250,
            badgeIcon = "🎙️",
            unlockedTitle = "Geister-Medium I",
            perkDescription = "Pink-Noise Rauschfilter optimiert",
            colorHex = 0xFFFFCC00
        ),
        GhostRewardMilestone(
            id = "evp_20",
            title = "20 EVP-Transkriptionen",
            description = "Dokumentiere 20 Geister-Sätze im Kommunikations-Protokoll.",
            category = RewardCategory.EVP,
            targetCount = 200,
            coinReward = 600,
            xpReward = 900,
            badgeIcon = "📜",
            unlockedTitle = "Okkulter Kryptologe",
            perkDescription = "Klare Tonmodulation für alle Geister-Stimmen",
            colorHex = 0xFFFFCC00
        ),

        // ==================== SPEZIAL & GESAMT-MEILENSTEINE ====================
        GhostRewardMilestone(
            id = "total_50_captured",
            title = "50 Gesamtfänge Elite-Auszeichnung",
            description = "Fange insgesamt 50 beliebige paranormale Wesenheiten.",
            category = RewardCategory.SPECIAL,
            targetCount = 500,
            coinReward = 1500,
            xpReward = 2000,
            badgeIcon = "🌟",
            unlockedTitle = "Elite-Parapsychologe",
            perkDescription = "+25% Gesamt-Coin Multiplikator",
            colorHex = 0xFFFFD700
        ),
        GhostRewardMilestone(
            id = "total_100_captured",
            title = "100 Entitäten Legenden-Krone",
            description = "Erreiche den Meilenstein von 100 gefangenen Entitäten!",
            category = RewardCategory.SPECIAL,
            targetCount = 100,
            coinReward = 3500,
            xpReward = 5000,
            badgeIcon = "👑",
            unlockedTitle = "LEGENDE DER SCHATTENWELT",
            perkDescription = "Unbegrenzter Meister-Status & goldener HUD-Glow",
            colorHex = 0xFFFFD700
        )
    )

    val allShopUpgrades: List<GhostShopUpgrade> = listOf(
        GhostShopUpgrade(
            id = "upgrade_coin_multiplier",
            title = "Ecto-Coin Multiplikator",
            subtitle = "Mehr Coins pro Individuum (+2 pro Level)",
            iconEmoji = "🪙",
            basePrice = 100,
            priceMultiplier = 1.8f,
            maxLevel = 50,
            colorHex = 0xFFFFD700,
            getLevelDescription = { lvl ->
                if (lvl == 0) "Stufe 0: 2 Coins pro Individuum"
                else "Stufe $lvl: ${2 + lvl * 2} Coins pro Individuum"
            }
        ),

        GhostShopUpgrade(
            id = "upgrade_trap_speed",
            title = "Spektral-Fallen Turbokondensator",
            subtitle = "Schnellere Fangzeit für Dämonen & Vampire",
            iconEmoji = "⚡",
            basePrice = 120,
            priceMultiplier = 1.6f,
            maxLevel = 5,
            colorHex = 0xFF00FF88,
            getLevelDescription = { lvl ->
                when (lvl) {
                    0 -> "Stufe 0: Standard-Fallen-Aufladung (1.8s)"
                    1 -> "Stufe 1: +20% Fang-Geschwindigkeit (1.4s)"
                    2 -> "Stufe 2: +40% Fang-Geschwindigkeit (1.0s)"
                    3 -> "Stufe 3: +60% Fang-Geschwindigkeit (0.7s)"
                    4 -> "Stufe 4: +80% Fang-Geschwindigkeit (0.4s)"
                    else -> "Stufe MAX: Sofortige Blitz-Einkerkerung (0.1s)"
                }
            }
        ),
        GhostShopUpgrade(
            id = "upgrade_radar_range",
            title = "Quanten-Radar Overclocking",
            subtitle = "Größere Reichweite & präzisere Anomaly-Scans",
            iconEmoji = "📡",
            basePrice = 150,
            priceMultiplier = 1.7f,
            maxLevel = 5,
            colorHex = 0xFF00E5FF,
            getLevelDescription = { lvl ->
                when (lvl) {
                    0 -> "Stufe 0: Standard 360° Radar Reichweite (10m)"
                    1 -> "Stufe 1: +25% Reichweite & Telemetrie-Filter (15m)"
                    2 -> "Stufe 2: +50% Reichweite & Frühwarnung vor Spikes (20m)"
                    3 -> "Stufe 3: +75% Reichweite & Entity-Typ-Vorschau (25m)"
                    4 -> "Stufe 4: +100% Reichweite & Multikanal-Scanning (35m)"
                    else -> "Stufe MAX: Omnipräsente Satelliten-Synchronisation (50m)"
                }
            }
        ),
        GhostShopUpgrade(
            id = "upgrade_magnet_shield",
            title = "Arkaner Magnet-Schild Reflektor",
            subtitle = "Filtert EM-Störquellen & TV-Strahlung doppelt stark",
            iconEmoji = "🛡️",
            basePrice = 100,
            priceMultiplier = 1.5f,
            maxLevel = 5,
            colorHex = 0xFF00FF66,
            getLevelDescription = { lvl ->
                when (lvl) {
                    0 -> "Stufe 0: Basis EM-Strahlungsschild"
                    1 -> "Stufe 1: +20% Schild-Effizienz gegen Haushaltsgeräte"
                    2 -> "Stufe 2: +40% Schild-Effizienz & automatische Dämpfung"
                    3 -> "Stufe 3: +60% Schild-Effizienz & Nullfeld-Absorption"
                    4 -> "Stufe 4: +80% Schild-Effizienz & Schutz vor Dämonen-Spikes"
                    else -> "Stufe MAX: Absolutes Parapsychologisches Kraftfeld"
                }
            }
        ),
        GhostShopUpgrade(
            id = "upgrade_ecto_lure",
            title = "Ecto-Magnet Geister-Köder",
            subtitle = "Zieht seltene Dämonen & Vampire für mehr Coins an",
            iconEmoji = "🔮",
            basePrice = 200,
            priceMultiplier = 1.8f,
            maxLevel = 4,
            colorHex = 0xFFFF0055,
            getLevelDescription = { lvl ->
                when (lvl) {
                    0 -> "Stufe 0: Natürliche Geister-Erscheinungsrate"
                    1 -> "Stufe 1: +25% Chance auf seltene Dämonen & Vampire"
                    2 -> "Stufe 2: +50% Chance & +15% extra Ecto-Coins pro Fang"
                    3 -> "Stufe 3: +75% Chance & +30% extra Ecto-Coins pro Fang"
                    else -> "Stufe MAX: Doppelter Ertrag & Magnet für Hochrisiko-Wesen"
                }
            }
        ),
        GhostShopUpgrade(
            id = "upgrade_evp_crystal",
            title = "Ätherischer EVP-Kristall-Tuner",
            subtitle = "Kristallklare Spirit-Box Resonanzen & Frequenzen",
            iconEmoji = "🎙️",
            basePrice = 130,
            priceMultiplier = 1.5f,
            maxLevel = 4,
            colorHex = 0xFFFFCC00,
            getLevelDescription = { lvl ->
                when (lvl) {
                    0 -> "Stufe 0: Standard EVP-Empfang"
                    1 -> "Stufe 1: Pink-Noise Verstärker & Rauschreduktion"
                    2 -> "Stufe 2: 2x schnellere KI-Stimmen-Generierung"
                    3 -> "Stufe 3: Seltene geheime Phrasen freigeschaltet"
                    else -> "Stufe MAX: Direkter Kanal zur Geisterwelt (Höchste Klarheit)"
                }
            }
        ),
        GhostShopUpgrade(
            id = "upgrade_bank_vault",
            title = "Ecto-Tresor & Tägliche Zinsen",
            subtitle = "Generiert täglich passive Geister-Coins beim Öffnen der App",
            iconEmoji = "💰",
            basePrice = 250,
            priceMultiplier = 2.0f,
            maxLevel = 3,
            colorHex = 0xFFFFD700,
            getLevelDescription = { lvl ->
                when (lvl) {
                    0 -> "Stufe 0: Kein passives Einkommen"
                    1 -> "Stufe 1: +50 Geister-Coins täglicher Login-Bonus"
                    2 -> "Stufe 2: +120 Geister-Coins täglicher Login-Bonus"
                    else -> "Stufe MAX: +250 Geister-Coins täglicher Login-Bonus!"
                }
            }
        )
    )
}
