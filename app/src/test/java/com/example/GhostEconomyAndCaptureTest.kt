package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.GhostDetectionEntity
import com.example.data.GhostRewardCatalog
import com.example.ui.components.RadarBlip
import com.example.ui.viewmodel.GhostViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class GhostEconomyAndCaptureTest {

    private lateinit var app: Application
    private lateinit var viewModel: GhostViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        // Clear prefs before test
        app.getSharedPreferences("ghost_reward_prefs", android.content.Context.MODE_PRIVATE)
            .edit().clear().commit()
        app.getSharedPreferences("ghost_calibration_prefs", android.content.Context.MODE_PRIVATE)
            .edit().clear().commit()
        viewModel = GhostViewModel(app)
    }

    @Test
    fun testCaptureEntityAwardsCoinsAndIncrementsCount() = runTest {
        val initialCoins = viewModel.ghostCoins.value
        val initialCaptured = viewModel.capturedCount.value

        val blip = RadarBlip(
            id = "test_demon_1",
            angleDegrees = 30f,
            distanceRatio = 0.4f,
            dangerLevel = 4,
            label = "Test Dämon",
            category = com.example.ui.components.EntityCategory.DEMON
        )

        viewModel.captureEntity(blip)

        assertTrue(viewModel.capturedCount.value > initialCaptured)
        assertTrue(viewModel.ghostCoins.value > initialCoins)
        assertNotNull(viewModel.liberatedBannerMessage.value)
    }

    @Test
    fun testBuyShopUpgradeDeductsCoinsAndIncreasesLevel() {
        // Give enough coins to buy upgrade
        viewModel.awardCoins(500, "Test Coin Grant", playSound = false)
        val coinsBefore = viewModel.ghostCoins.value
        val upgrade = GhostRewardCatalog.allShopUpgrades.first()
        val cost = upgrade.getCostForLevel(viewModel.getUpgradeLevel(upgrade.id))

        val success = viewModel.buyShopUpgrade(upgrade)
        assertTrue(success)
        assertEquals(coinsBefore - cost, viewModel.ghostCoins.value)
        assertEquals(1, viewModel.getUpgradeLevel(upgrade.id))
    }

    @Test
    fun testSellGhostAwardsCoins() = runTest {
        val ghost = GhostDetectionEntity(
            id = 999,
            name = "Infernale Entität [GEFANGEN]",
            type = "DÄMON (GEFANGEN)",
            emfLevel = 8.5f,
            frequencyKhz = 142.0f,
            dangerLevel = 4,
            locationName = "Labor",
            timestamp = System.currentTimeMillis(),
            notes = "Test Note",
            spectralColorHex = "#FF0055",
            lastWords = "..."
        )

        val price = viewModel.calculateGhostSellPrice(ghost)
        assertTrue(price > 0)

        val coinsBefore = viewModel.ghostCoins.value
        viewModel.sellGhost(ghost)
        org.robolectric.shadows.ShadowLooper.idleMainLooper()

        // Awarded coins should be at least price
        assertTrue(viewModel.ghostCoins.value >= coinsBefore + price)
    }

    @Test
    fun testPeacefulScannerLiberateAndRecord() = runTest {
        val blip = RadarBlip(
            id = "peaceful_blip_1",
            angleDegrees = 45f,
            distanceRatio = 0.5f,
            dangerLevel = 3,
            label = "Sanfter Poltergeist",
            category = com.example.ui.components.EntityCategory.GHOST
        )

        // 1. In LIBERATE mode, blip click liberates into light
        viewModel.setScannerPeacefulMode(com.example.ui.viewmodel.ScannerPeacefulMode.LIBERATE)
        assertEquals(com.example.ui.viewmodel.ScannerPeacefulMode.LIBERATE, viewModel.scannerPeacefulMode.value)
        viewModel.handleRadarBlipClick(blip)

        assertTrue(viewModel.isLiberatingAnomalies.value)
        assertNotNull(viewModel.liberatedBannerMessage.value)
        assertTrue(viewModel.liberatedBannerMessage.value!!.contains("BEFREIT"))

        // 2. Mode toggle works
        viewModel.toggleScannerPeacefulMode()
        assertEquals(com.example.ui.viewmodel.ScannerPeacefulMode.RECORD_HISTORY, viewModel.scannerPeacefulMode.value)

        // 3. Neutralize EMF harmonizes peacefully
        viewModel.neutralizeEmfSpike()
        assertEquals(1.0f, viewModel.emfLevel.value, 0.01f)
        assertEquals(1, viewModel.dangerLevel.value)
    }

    @Test
    fun testHistoryContainsDiverseEntities() = runTest {
        viewModel.ensureDiverseEntitiesDirect()

        val allList = viewModel.getAllDetectionsDirect()
        assertTrue("History must contain entities", allList.isNotEmpty())

        val hasGeist = allList.any { it.type.contains("Geist", ignoreCase = true) || it.name.contains("Geist", ignoreCase = true) }
        val hasVampir = allList.any { it.type.contains("Vampir", ignoreCase = true) || it.name.contains("Vampir", ignoreCase = true) || it.name.contains("Nosferatu", ignoreCase = true) }
        val hasSchatten = allList.any { it.type.contains("Schatten", ignoreCase = true) || it.name.contains("Schatten", ignoreCase = true) }
        val hasDaemon = allList.any { it.type.contains("Dämon", ignoreCase = true) || it.name.contains("Dämon", ignoreCase = true) || it.name.contains("Belial", ignoreCase = true) }
        val hasPoltergeist = allList.any { it.type.contains("Poltergeist", ignoreCase = true) || it.name.contains("Poltergeist", ignoreCase = true) }

        assertTrue("Geister should be in history", hasGeist)
        assertTrue("Vampire should be in history", hasVampir)
        assertTrue("Schattenwesen should be in history", hasSchatten)
        assertTrue("Dämonen should be in history", hasDaemon)
        assertTrue("Poltergeister should be in history", hasPoltergeist)
    }

    @Test
    fun testFavoriteAllAndIndividualFavoriteToggle() = runTest {
        viewModel.ensureDiverseEntitiesDirect()

        val allList = viewModel.getAllDetectionsDirect()
        assertTrue(allList.isNotEmpty())

        // Test Herz für alle
        viewModel.toggleFavoriteAllGhostsDirect()

        val updatedListAfterAllFav = viewModel.getAllDetectionsDirect()
        assertTrue("All entities should have a heart", updatedListAfterAllFav.all { it.isFavorite })

        // Test Individual Favorite Toggle
        val firstEntity = updatedListAfterAllFav.first()
        viewModel.toggleFavorite(firstEntity)
        org.robolectric.shadows.ShadowLooper.idleMainLooper()

        val afterSingleToggle = viewModel.getAllDetectionsDirect().first { it.id == firstEntity.id }
        assertEquals(false, afterSingleToggle.isFavorite)
    }
}
