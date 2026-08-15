package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.viewmodel.GhostViewModel

enum class MissionDifficultyTier(
    val displayName: String,
    val badgeIcon: String,
    val color: Color,
    val xpReward: Int
) {
    BRONZE("Bronze (1-10)", "🥉", Color(0xFFCD7F32), 100),
    SILVER("Silber (1-25)", "🥈", Color(0xFFC0C0C0), 250),
    GOLD("Gold (1-50)", "🥇", Color(0xFFFFD700), 500),
    PLATINUM("Platin (1-100)", "💠", Color(0xFF00E5FF), 1000),
    DIAMOND("Großmeister (1-150+)", "💎", Color(0xFFFF007F), 2500)
}

enum class MissionCategory(val label: String) {
    ALL("ALLE STUFEN"),
    TIER_10("1-10 BASIS"),
    TIER_50("1-50 PROFI"),
    TIER_100("1-100 MEISTER"),
    HUNTING("DÄMONEN & VAMPIRE"),
    PORTALS("DIMENSIONEN"),
    COMMUNICATION("EVP-FUNK")
}

data class MissionItem(
    val id: String,
    val title: String,
    val description: String,
    val current: Int,
    val target: Int,
    val tier: MissionDifficultyTier,
    val category: String,
    val iconColor: Color
) {
    val isCompleted: Boolean get() = current >= target
    val progress: Float get() = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val remaining: Int get() = (target - current).coerceAtLeast(0)
}

@Composable
fun MissionsScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val detections by viewModel.allDetections.collectAsStateWithLifecycle()
    val filterMode by viewModel.currentFilterMode.collectAsStateWithLifecycle()
    val spiritLogList by viewModel.spiritPhraseLog.collectAsStateWithLifecycle()
    val primaryColor = filterMode.primaryColor

    var selectedCategory by remember { mutableStateOf(MissionCategory.ALL) }

    val totalDetections = detections.size
    val capturedCount = detections.count { it.type.contains("GEFANGEN", ignoreCase = true) || it.name.contains("GEFANGEN", ignoreCase = true) || it.type.contains("DÄMON", ignoreCase = true) || it.type.contains("VAMPIR", ignoreCase = true) }
    val closedRiftsCount = detections.count { it.type.contains("DIMENSION", ignoreCase = true) || it.name.contains("VERRIEGELT", ignoreCase = true) || it.type.contains("RISS", ignoreCase = true) }
    val favoritesCount = detections.count { it.isFavorite }
    val spiritBoxCount = spiritLogList.size

    val allMissions = remember(totalDetections, capturedCount, closedRiftsCount, favoritesCount, spiritBoxCount) {
        listOf(
            // --- 1-10 Tier: Bronze Stufe ---
            MissionItem(
                id = "hunt_10",
                title = "Spektral-Spürhund I",
                description = "Erfasse 10 paranormale Entitäten oder Spektral-Anomalien.",
                current = totalDetections,
                target = 10,
                tier = MissionDifficultyTier.BRONZE,
                category = "GEISTERJAGD",
                iconColor = Color(0xFF00FF88)
            ),
            MissionItem(
                id = "demon_10",
                title = "Exorzisten-Lehrling I",
                description = "Fange 10 Dämonen oder Vampire mit der Spektral-Falle.",
                current = capturedCount,
                target = 10,
                tier = MissionDifficultyTier.BRONZE,
                category = "DÄMONEN & VAMPIRE",
                iconColor = Color(0xFFFF0055)
            ),
            MissionItem(
                id = "portal_10",
                title = "Dimensions-Wächter I",
                description = "Schließe und versiegele 10 interdimensionale Raum-Zeit-Risse.",
                current = closedRiftsCount,
                target = 10,
                tier = MissionDifficultyTier.BRONZE,
                category = "DIMENSIONEN",
                iconColor = Color(0xFF00B0FF)
            ),
            MissionItem(
                id = "evp_10",
                title = "Äther-Funkamateur I",
                description = "Führe 10 erfolgreiche EVP-Kommunikationen über den Kommunikator.",
                current = spiritBoxCount,
                target = 10,
                tier = MissionDifficultyTier.BRONZE,
                category = "EVP-FUNK",
                iconColor = Color(0xFFFFCC00)
            ),
            MissionItem(
                id = "fav_10",
                title = "Archivar-Katalog I",
                description = "Markiere 10 wichtige Phänomene als Favoriten im Verlauf.",
                current = favoritesCount,
                target = 10,
                tier = MissionDifficultyTier.BRONZE,
                category = "ARCHIV",
                iconColor = Color(0xFFBB33FF)
            ),

            // --- 1-25 Tier: Silber Stufe ---
            MissionItem(
                id = "hunt_25",
                title = "Geister-Jäger II (Silber)",
                description = "Erforsche und katalogisiere 25 paranormale Manifestationen.",
                current = totalDetections,
                target = 25,
                tier = MissionDifficultyTier.SILVER,
                category = "GEISTERJAGD",
                iconColor = Color(0xFF00FF88)
            ),
            MissionItem(
                id = "demon_25",
                title = "Dämonen-Inquisitor II",
                description = "Banne 25 Höllenfürsten, Arch-Dämonen oder Astral-Vampire.",
                current = capturedCount,
                target = 25,
                tier = MissionDifficultyTier.SILVER,
                category = "DÄMONEN & VAMPIRE",
                iconColor = Color(0xFFFF0055)
            ),
            MissionItem(
                id = "portal_25",
                title = "Nexus-Siegel-Meister II",
                description = "Stabilisiere 25 kollabierende Portal-Singularitäten.",
                current = closedRiftsCount,
                target = 25,
                tier = MissionDifficultyTier.SILVER,
                category = "DIMENSIONEN",
                iconColor = Color(0xFF00B0FF)
            ),
            MissionItem(
                id = "evp_25",
                title = "Geister-Medium II",
                description = "Empfange 25 Stimmenbotschaften aus dem Jenseits.",
                current = spiritBoxCount,
                target = 25,
                tier = MissionDifficultyTier.SILVER,
                category = "EVP-FUNK",
                iconColor = Color(0xFFFFCC00)
            ),

            // --- 1-50 Tier: Gold Stufe ---
            MissionItem(
                id = "hunt_50",
                title = "Parapsychologie-Veteran (1-50)",
                description = "Dokumentiere 50 nachgewiesene paranormale Phänomene.",
                current = totalDetections,
                target = 50,
                tier = MissionDifficultyTier.GOLD,
                category = "GEISTERJAGD",
                iconColor = Color(0xFF00FF88)
            ),
            MissionItem(
                id = "demon_50",
                title = "Infernale Auslöschung (1-50)",
                description = "Fange und isoliere 50 gefährliche Dämonen & Vampir-Wesen.",
                current = capturedCount,
                target = 50,
                tier = MissionDifficultyTier.GOLD,
                category = "DÄMONEN & VAMPIRE",
                iconColor = Color(0xFFFF0055)
            ),
            MissionItem(
                id = "portal_50",
                title = "Multiversum-Schildwache (1-50)",
                description = "Versiegele 50 Risse zwischen den Realitätsebenen.",
                current = closedRiftsCount,
                target = 50,
                tier = MissionDifficultyTier.GOLD,
                category = "DIMENSIONEN",
                iconColor = Color(0xFF00B0FF)
            ),
            MissionItem(
                id = "evp_50",
                title = "EVP-Transkript-Pionier (1-50)",
                description = "Entschlüssele 50 spektrale Audio-Sätze und Wortfragmente.",
                current = spiritBoxCount,
                target = 50,
                tier = MissionDifficultyTier.GOLD,
                category = "EVP-FUNK",
                iconColor = Color(0xFFFFCC00)
            ),

            // --- 1-100 Tier: Platin Stufe ---
            MissionItem(
                id = "hunt_100",
                title = "Meister-Okkultist (1-100)",
                description = "Erreiche 100 vollständige Spektral-Aufzeichnungen im Datenarchiv.",
                current = totalDetections,
                target = 100,
                tier = MissionDifficultyTier.PLATINUM,
                category = "GEISTERJAGD",
                iconColor = Color(0xFF00E5FF)
            ),
            MissionItem(
                id = "demon_100",
                title = "Dämonen-Erzfeind (1-100)",
                description = "Banne 100 unheilige Entitäten in die Spektral-Falle.",
                current = capturedCount,
                target = 100,
                tier = MissionDifficultyTier.PLATINUM,
                category = "DÄMONEN & VAMPIRE",
                iconColor = Color(0xFFFF0055)
            ),
            MissionItem(
                id = "portal_100",
                title = "Raum-Zeit-Kollaps-Verhinderer (1-100)",
                description = "Schließe 100 Dimensionsrisse und bewahre die irdische Realität.",
                current = closedRiftsCount,
                target = 100,
                tier = MissionDifficultyTier.PLATINUM,
                category = "DIMENSIONEN",
                iconColor = Color(0xFF00B0FF)
            ),
            MissionItem(
                id = "evp_100",
                title = "Brücke zum Jenseits (1-100)",
                description = "Führe 100 Zwei-Wege-Dialoge über die Spirit Box & Mikrofon.",
                current = spiritBoxCount,
                target = 100,
                tier = MissionDifficultyTier.PLATINUM,
                category = "EVP-FUNK",
                iconColor = Color(0xFFFFCC00)
            ),

            // --- 1-150+ Tier: Großmeister Diamant Stufe ---
            MissionItem(
                id = "hunt_150",
                title = "Legende der Schattenwelt (1-150)",
                description = "Katalogisiere 150 paranormale Entitäten in der Datenbank.",
                current = totalDetections,
                target = 150,
                tier = MissionDifficultyTier.DIAMOND,
                category = "GEISTERJAGD",
                iconColor = Color(0xFFFF007F)
            ),
            MissionItem(
                id = "demon_150",
                title = "Ewiger Bann-Großmeister (1-150)",
                description = "Fange 150 Dämonen & Astral-Vampire für ewigen Frieden.",
                current = capturedCount,
                target = 150,
                tier = MissionDifficultyTier.DIAMOND,
                category = "DÄMONEN & VAMPIRE",
                iconColor = Color(0xFFFF0055)
            )
        )
    }

    val filteredMissions = remember(allMissions, selectedCategory) {
        when (selectedCategory) {
            MissionCategory.ALL -> allMissions
            MissionCategory.TIER_10 -> allMissions.filter { it.target <= 10 }
            MissionCategory.TIER_50 -> allMissions.filter { it.target in 25..50 }
            MissionCategory.TIER_100 -> allMissions.filter { it.target >= 100 }
            MissionCategory.HUNTING -> allMissions.filter { it.category == "DÄMONEN & VAMPIRE" }
            MissionCategory.PORTALS -> allMissions.filter { it.category == "DIMENSIONEN" }
            MissionCategory.COMMUNICATION -> allMissions.filter { it.category == "EVP-FUNK" }
        }
    }

    val totalCompleted = allMissions.count { it.isCompleted }
    val totalXpEarned = allMissions.filter { it.isCompleted }.sumOf { it.tier.xpReward }
    val maxPossibleXp = allMissions.sumOf { it.tier.xpReward }

    val userRank = when {
        totalXpEarned >= 8000 -> "GRANDMASTER V (LEGENDE)"
        totalXpEarned >= 4500 -> "EXORZIST-OFFIZIER IV"
        totalXpEarned >= 2000 -> "PARAPSYCHOLOGE III"
        totalXpEarned >= 750 -> "GEISTERJÄGER-ERMITTLER II"
        else -> "REKRUT-ANFÄNGER I"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Overview Dashboard
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B140F)),
            border = BorderStroke(1.5.dp, primaryColor.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("missions_overview_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "MISSIONEN & ERFOLGE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = primaryColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "RANG: $userRank",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFFD700),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$totalCompleted / ${allMissions.size} ERFÜLLT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF00FF88),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "$totalXpEarned / $maxPossibleXp XP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { (totalXpEarned.toFloat() / maxPossibleXp.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = primaryColor,
                    trackColor = Color(0xFF18231C)
                )

                // Quick stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🥉 Bronze: ${allMissions.count { it.tier == MissionDifficultyTier.BRONZE && it.isCompleted }}/${allMissions.count { it.tier == MissionDifficultyTier.BRONZE }}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCD7F32), fontFamily = FontFamily.Monospace, fontSize = 9.5.sp)
                    )
                    Text(
                        text = "🥈 Silber: ${allMissions.count { it.tier == MissionDifficultyTier.SILVER && it.isCompleted }}/${allMissions.count { it.tier == MissionDifficultyTier.SILVER }}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC0C0C0), fontFamily = FontFamily.Monospace, fontSize = 9.5.sp)
                    )
                    Text(
                        text = "🥇 Gold: ${allMissions.count { it.tier == MissionDifficultyTier.GOLD && it.isCompleted }}/${allMissions.count { it.tier == MissionDifficultyTier.GOLD }}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFFD700), fontFamily = FontFamily.Monospace, fontSize = 9.5.sp)
                    )
                    Text(
                        text = "💠 Platin: ${allMissions.count { it.tier == MissionDifficultyTier.PLATINUM && it.isCompleted }}/${allMissions.count { it.tier == MissionDifficultyTier.PLATINUM }}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00E5FF), fontFamily = FontFamily.Monospace, fontSize = 9.5.sp)
                    )
                }
            }
        }

        // Difficulty / Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(MissionCategory.values()) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = category }
                        .border(
                            1.dp,
                            if (isSelected) primaryColor else InfraGreenBorder,
                            RoundedCornerShape(8.dp)
                        ),
                    color = if (isSelected) primaryColor else Color(0xFF0E1712),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = category.label,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) Color.Black else InfraGreenTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Missions List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredMissions, key = { it.id }) { mission ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (mission.isCompleted) Color(0xFF091F14) else Color(0xFF0C1013)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (mission.isCompleted) Color(0xFF00FF88).copy(alpha = 0.8f) else mission.tier.color.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("mission_item_${mission.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (mission.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (mission.isCompleted) Color(0xFF00FF88) else Color.Gray,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = mission.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = if (mission.isCompleted) Color(0xFF00FF88) else Color.White,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = mission.tier.badgeIcon,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "[${mission.category}]",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = mission.iconColor,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "• +${mission.tier.xpReward} XP",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = mission.tier.color,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (mission.isCompleted) Color(0xFF00FF88).copy(alpha = 0.2f) else Color(0xFF16201B))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${mission.current.coerceAtMost(mission.target)} / ${mission.target}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (mission.isCompleted) Color(0xFF00FF88) else Color.LightGray,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = mission.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        )

                        // Progress bar with remaining amount indicator
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            LinearProgressIndicator(
                                progress = { mission.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (mission.isCompleted) Color(0xFF00FF88) else mission.tier.color,
                                trackColor = Color(0xFF171F1B)
                            )
                            if (!mission.isCompleted) {
                                Text(
                                    text = "Noch ${mission.remaining} bis zum Abschluss",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
