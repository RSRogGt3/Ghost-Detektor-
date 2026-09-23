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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.data.GhostRewardCatalog
import com.example.data.GhostRewardMilestone
import com.example.data.GhostShopUpgrade
import com.example.data.RewardCategory
import com.example.ui.components.CelebrationRewardDialog
import com.example.ui.components.CollapsibleHudWindow
import com.example.ui.viewmodel.GhostViewModel

enum class MissionsScreenTab(val title: String, val icon: String) {
    REWARDS("ERFOLGE & BELOHNUNGEN", "🏆"),
    SHOP("GEISTER-SHOP", "🛒"),
    TROPHIES("TITEL & STATS", "📜")
}

@Composable
fun MissionsScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val ghostCoins by viewModel.ghostCoins.collectAsStateWithLifecycle()
    val totalCoinsEarned by viewModel.totalCoinsEarned.collectAsStateWithLifecycle()
    val claimedMilestoneIds by viewModel.claimedMilestoneIds.collectAsStateWithLifecycle()
    val purchasedUpgrades by viewModel.purchasedUpgradeLevels.collectAsStateWithLifecycle()
    val celebrationMilestone by viewModel.celebrationMilestone.collectAsStateWithLifecycle()
    val allDetections by viewModel.allDetections.collectAsStateWithLifecycle()
    val evpList by viewModel.spiritPhraseLog.collectAsStateWithLifecycle()
    val filterMode by viewModel.currentFilterMode.collectAsStateWithLifecycle()
    val primaryColor = filterMode.primaryColor

    var currentTab by remember { mutableStateOf(MissionsScreenTab.REWARDS) }
    var selectedCategory by remember { mutableStateOf(RewardCategory.ALL) }

    // Calculate Ready to Claim Milestones
    val readyToClaimMilestones = remember(allDetections, evpList, claimedMilestoneIds) {
        GhostRewardCatalog.allMilestones.filter { milestone ->
            val count = viewModel.getMilestoneCurrentCount(milestone, allDetections, evpList.size)
            count >= milestone.targetCount && !claimedMilestoneIds.contains(milestone.id)
        }
    }

    // Rank & XP Calculation
    val totalXp = remember(allDetections, claimedMilestoneIds, totalCoinsEarned) {
        val milestoneXp = GhostRewardCatalog.allMilestones
            .filter { claimedMilestoneIds.contains(it.id) }
            .sumOf { it.xpReward }
        allDetections.size * 50 + milestoneXp + (totalCoinsEarned / 2)
    }

    val (currentRankTitle, rankTier, nextRankXp) = remember(totalXp) {
        when {
            totalXp < 300 -> Triple("Novize des Okkulten", 1, 300)
            totalXp < 800 -> Triple("Geister-Jäger Lehrling", 2, 800)
            totalXp < 1800 -> Triple("Parapsychologe II", 3, 1800)
            totalXp < 3500 -> Triple("Dämonen-Inquisitor", 4, 3500)
            totalXp < 6000 -> Triple("Großmeister der Schattenwelt", 5, 6000)
            else -> Triple("LEGENDE DES JENSEITS", 6, 10000)
        }
    }

    // Active celebration popup
    celebrationMilestone?.let { milestone ->
        CelebrationRewardDialog(
            milestone = milestone,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("missions_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. TOP HERO HEADER: GHOST COINS & RANK WALLET
            CollapsibleHudWindow(
                title = "GEISTER-KONTO & RANG",
                icon = Icons.Default.MilitaryTech,
                badgeText = "$ghostCoins COINS",
                initialExpanded = true,
                isScrollable = false,
                testTag = "missions_hero_wallet_window"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ghost Coin Counter
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2A2000))
                                    .border(1.dp, Color(0xFFFFD700), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🪙", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$ghostCoins COINS",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color(0xFFFFD700),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp
                                    )
                                )
                                Text(
                                    text = "Gesamt gesammelt: $totalCoinsEarned 🪙",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Rank Badge Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF14241B))
                                .border(1.dp, primaryColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "RANG $rankTier",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = primaryColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Rank Progress Bar
                    val progressRatio = (totalXp.toFloat() / nextRankXp.toFloat()).coerceIn(0f, 1f)
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TITEL: $currentRankTitle",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF00FFCC),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                )
                            )
                            Text(
                                text = "$totalXp / $nextRankXp XP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFFD700),
                            trackColor = Color(0xFF1C261E),
                        )
                    }

                    // Multiplier info
                    val lureMultiplier = viewModel.getCoinMultiplier()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ Bonus: +15 pro Fang | +35 Dämon/Vampir | +30 Portal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp
                            )
                        )
                        if (lureMultiplier > 1.0f) {
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", lureMultiplier)}x Ertrag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFF0055),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }
            }

            // 2. CLAIM READY MILESTONES NOTIFICATION BANNER (IF AVAILABLE)
            if (readyToClaimMilestones.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261D00)),
                    border = BorderStroke(1.5.dp, Color(0xFFFFD700)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🎁", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${readyToClaimMilestones.size} BELOHNUNGEN ABHOLBEREIT!",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color(0xFFFFD700),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                )
                                val totalCoinsReady = readyToClaimMilestones.sumOf { it.coinReward }
                                Text(
                                    text = "+$totalCoinsReady Geister-Coins warten auf dich!",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFFECC0),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.5.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.claimAllAvailableMilestones(allDetections, evpList.size)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("claim_all_milestones_button")
                        ) {
                            Text(
                                text = "ALLE EINLÖSEN",
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }

            // 3. MAIN SECTION NAVIGATION TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0E1611))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MissionsScreenTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) primaryColor.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) primaryColor else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { currentTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = tab.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) primaryColor else Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }
            }

            // TAB 1: REWARDS & MILESTONES
            if (currentTab == MissionsScreenTab.REWARDS) {
                // Category Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(RewardCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = "${category.icon} ${category.label}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 9.5.sp
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryColor.copy(alpha = 0.3f),
                                selectedLabelColor = primaryColor,
                                containerColor = Color(0xFF0F1712),
                                labelColor = Color.Gray
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) primaryColor else Color(0xFF1E2D22)
                            )
                        )
                    }
                }

                // Filtered Milestones List
                val filteredMilestones = remember(selectedCategory) {
                    if (selectedCategory == RewardCategory.ALL) {
                        GhostRewardCatalog.allMilestones
                    } else {
                        GhostRewardCatalog.allMilestones.filter { it.category == selectedCategory }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredMilestones, key = { it.id }) { milestone ->
                        val currentCount = viewModel.getMilestoneCurrentCount(milestone, allDetections, evpList.size)
                        val isClaimed = claimedMilestoneIds.contains(milestone.id)
                        val isReady = currentCount >= milestone.targetCount && !isClaimed

                        MilestoneRewardCard(
                            milestone = milestone,
                            currentCount = currentCount,
                            isClaimed = isClaimed,
                            isReadyToClaim = isReady,
                            onClaim = { viewModel.claimMilestone(milestone) }
                        )
                    }
                }
            }

            // TAB 2: GHOST SHOP & OCCULT UPGRADES
            if (currentTab == MissionsScreenTab.SHOP) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        // Shop Info Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF09140E)),
                            border = BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🛒", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "OKKULTER ARTEFAKTE- & UPGRADE-MARKT",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color(0xFF00FF88),
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = "Investiere Geister-Coins in Fallen-Geschwindigkeit, Radar-Reichweite & Schutzschilde.",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.LightGray,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    items(GhostRewardCatalog.allShopUpgrades, key = { it.id }) { upgrade ->
                        val currentLevel = viewModel.getUpgradeLevel(upgrade.id)
                        val isMax = currentLevel >= upgrade.maxLevel
                        val nextCost = upgrade.getCostForLevel(currentLevel)
                        val canAfford = ghostCoins >= nextCost && !isMax

                        ShopUpgradeCard(
                            upgrade = upgrade,
                            currentLevel = currentLevel,
                            nextCost = nextCost,
                            isMax = isMax,
                            canAfford = canAfford,
                            onBuy = { viewModel.buyShopUpgrade(upgrade) }
                        )
                    }
                }
            }

            // TAB 3: TITLES, TROPHIES & LIFETIME STATS
            if (currentTab == MissionsScreenTab.TROPHIES) {
                val claimedMilestonesList = remember(claimedMilestoneIds) {
                    GhostRewardCatalog.allMilestones.filter { claimedMilestoneIds.contains(it.id) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        // Stats Overview Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B140F)),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "📊 PARAPSYCHOLOGISCHE GESAMT-STATISTIK",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF00E5FF),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )

                                val totalGhosts = allDetections.count {
                                    it.type.contains("GEIST", ignoreCase = true) ||
                                    it.type.contains("BEFREIT", ignoreCase = true) ||
                                    it.type.contains("POLTERGEIST", ignoreCase = true)
                                }
                                val totalDemons = allDetections.count {
                                    it.type.contains("DÄMON", ignoreCase = true) || it.type.contains("DEMON", ignoreCase = true)
                                }
                                val totalVampires = allDetections.count {
                                    it.type.contains("VAMPIR", ignoreCase = true)
                                }
                                val totalPortals = allDetections.count {
                                    it.type.contains("DIMENSION", ignoreCase = true) || it.name.contains("VERRIEGELT", ignoreCase = true)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatBox(title = "👻 Geister", value = "$totalGhosts", color = Color(0xFF00FF88), modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatBox(title = "🔥 Dämonen", value = "$totalDemons", color = Color(0xFFFF0055), modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatBox(title = "🧛 Vampire", value = "$totalVampires", color = Color(0xFFBB33FF), modifier = Modifier.weight(1f))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatBox(title = "🌀 Portale", value = "$totalPortals", color = Color(0xFF00B0FF), modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatBox(title = "🎙️ EVP-Stimmen", value = "${evpList.size}", color = Color(0xFFFFCC00), modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatBox(title = "🪙 Coins", value = "$totalCoinsEarned", color = Color(0xFFFFD700), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "🏅 FREIGESCHALTETE EHRENTITEL & PERKS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFFFD700),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    if (claimedMilestonesList.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F0C)),
                                border = BorderStroke(1.dp, Color.DarkGray),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Noch keine Meilensteine beansprucht. Fange Geister, Dämonen & Vampire, um Titel freizuschalten!",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.Gray,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        items(claimedMilestonesList, key = { it.id }) { milestone ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A13)),
                                border = BorderStroke(1.dp, milestone.color.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = milestone.badgeIcon, fontSize = 26.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = milestone.unlockedTitle,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = milestone.color,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Text(
                                            text = "Perk: ${milestone.perkDescription}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.LightGray,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF00FF88),
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
}

@Composable
fun MilestoneRewardCard(
    milestone: GhostRewardMilestone,
    currentCount: Int,
    isClaimed: Boolean,
    isReadyToClaim: Boolean,
    onClaim: () -> Unit
) {
    var expanded by remember { mutableStateOf(isReadyToClaim) }

    val progress = (currentCount.toFloat() / milestone.targetCount.toFloat()).coerceIn(0f, 1f)
    val remaining = (milestone.targetCount - currentCount).coerceAtLeast(0)

    val containerColor = when {
        isClaimed -> Color(0xFF08120C)
        isReadyToClaim -> Color(0xFF1E1700)
        else -> Color(0xFF0D1410)
    }

    val borderColor = when {
        isClaimed -> Color(0xFF00FF88).copy(alpha = 0.4f)
        isReadyToClaim -> Color(0xFFFFD700)
        else -> milestone.color.copy(alpha = 0.35f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isReadyToClaim) 2.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("milestone_card_${milestone.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Row: Badge, Title & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = milestone.badgeIcon, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (isReadyToClaim) Color(0xFFFFD700) else Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        )
                        Text(
                            text = if (expanded) milestone.description else "Tippe zum Erweitern",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                // Status Badge Pill
                val statusText = when {
                    isClaimed -> "✅ BEANSPRUCHT"
                    isReadyToClaim -> "🎁 BEREIT!"
                    else -> "⏳ IN ARBEIT"
                }
                val statusColor = when {
                    isClaimed -> Color(0xFF00FF88)
                    isReadyToClaim -> Color(0xFFFFD700)
                    else -> Color.LightGray
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Progress Bar & Counter
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isClaimed) "Erfolgreich abgeschlossen" else if (isReadyToClaim) "Meilenstein erreicht!" else "Noch $remaining bis zum Ziel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isReadyToClaim) Color(0xFFFFD700) else Color.LightGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = "$currentCount / ${milestone.targetCount}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = milestone.color,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isReadyToClaim) Color(0xFFFFD700) else milestone.color,
                            trackColor = Color(0xFF142018),
                        )
                    }

                    // Rewards Row & Claim Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF282000))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+${milestone.coinReward} 🪙 Coins",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFFD700),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF002922))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+${milestone.xpReward} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF00FFCC),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        if (isReadyToClaim) {
                            Button(
                                onClick = onClaim,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("claim_button_${milestone.id}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🎁", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "BELOHNUNG HOLEN",
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
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

@Composable
fun ShopUpgradeCard(
    upgrade: GhostShopUpgrade,
    currentLevel: Int,
    nextCost: Int,
    isMax: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1610)),
        border = BorderStroke(1.dp, upgrade.color.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("shop_card_${upgrade.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = upgrade.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = upgrade.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = upgrade.color,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = if (expanded) upgrade.subtitle else "Tippe zum Erweitern",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                // Level indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF16251C))
                        .border(1.dp, upgrade.color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isMax) "MAX" else "STUFE $currentLevel/${upgrade.maxLevel}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isMax) Color(0xFFFFD700) else upgrade.color,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Current / Next Level Description
                    Text(
                        text = upgrade.getLevelDescription(currentLevel),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFD4E8DC),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )

                    // Purchase Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isMax) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF14241B))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "MAXIMALE STUFE ERREICHT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF00FF88),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    )
                                )
                            }
                        } else {
                            Button(
                                onClick = onBuy,
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canAfford) Color(0xFFFFD700) else Color(0xFF262626),
                                    disabledContainerColor = Color(0xFF1F1F1F)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("buy_upgrade_${upgrade.id}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪙", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AUFRÜSTEN ($nextCost COINS)",
                                        color = if (canAfford) Color.Black else Color.Gray,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
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

@Composable
fun StatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF080E0A))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = color,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp
                )
            )
        }
    }
}
