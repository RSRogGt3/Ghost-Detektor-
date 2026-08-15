package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.AudioWaveformCanvas
import com.example.ui.components.CameraAnomalyOverlayCanvas
import com.example.ui.components.CameraBackgroundView
import com.example.ui.components.CaptureAndPortalCard
import com.example.ui.components.DimensionSigilDialog
import com.example.ui.components.EmfMeter
import com.example.ui.components.FilterMode
import com.example.ui.components.FlashingRedWarningOverlay
import com.example.ui.components.GhostCompassOverlay
import com.example.ui.components.MagnetFieldAndShieldCard
import com.example.ui.components.RadarScannerCanvas
import com.example.ui.components.RealtimeEmfLineChart
import com.example.ui.components.ScanLinesOverlay
import com.example.ui.components.SpiritBoxTranscriptListCard
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings
import com.example.ui.viewmodel.GhostViewModel
import com.example.util.ImageCaptureUtil
import kotlinx.coroutines.launch
import android.widget.Toast

enum class ScannerModuleTab(val title: String) {
    ALL("ÜBERSICHT"),
    RADAR("RADAR & HUD"),
    DIMENSIONS("DIMENSIONEN & SIEGEL"),
    EMF_FILTER("EMF & FILTER"),
    SPIRIT_BOX("SPIRIT-BOX")
}

@Composable
fun ScannerScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val filterMode by viewModel.currentFilterMode.collectAsStateWithLifecycle()
    val filterIntensity by viewModel.filterIntensity.collectAsStateWithLifecycle()
    val emfLevel by viewModel.emfLevel.collectAsStateWithLifecycle()
    val emfHistory by viewModel.emfHistory.collectAsStateWithLifecycle()
    val dangerLevel by viewModel.dangerLevel.collectAsStateWithLifecycle()
    val frequencyKhz by viewModel.frequencyKhz.collectAsStateWithLifecycle()
    val radarBlips by viewModel.radarBlips.collectAsStateWithLifecycle()
    val showCrtOverlay by viewModel.showCrtOverlay.collectAsStateWithLifecycle()
    val isCameraEnabled by viewModel.isCameraBackgroundEnabled.collectAsStateWithLifecycle()
    val isFlashlightEnabled by viewModel.isFlashlightEnabled.collectAsStateWithLifecycle()
    val audioFeedbackEnabled by viewModel.audioFeedbackEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    
    val isDeviceConnected by viewModel.isDeviceConnected.collectAsStateWithLifecycle()
    val isSensorActive by viewModel.sensorManager.isSensorActive.collectAsStateWithLifecycle()
    val activeSensorCount by viewModel.activeSensorCount.collectAsStateWithLifecycle()
    val activeSensorNames by viewModel.activeSensorNames.collectAsStateWithLifecycle()
    val compassAzimuth by viewModel.compassAzimuth.collectAsStateWithLifecycle()
    val satelliteCount by viewModel.satelliteCount.collectAsStateWithLifecycle()
    val gyroSpeed by viewModel.gyroSpeed.collectAsStateWithLifecycle()
    val lightLux by viewModel.lightLux.collectAsStateWithLifecycle()
    val pressureHpa by viewModel.pressureHpa.collectAsStateWithLifecycle()
    
    val cameraAnomalies by viewModel.cameraAnomalies.collectAsStateWithLifecycle()
    val cameraAvgLuminance by viewModel.cameraAvgLuminance.collectAsStateWithLifecycle()
    val isLiberatingAnomalies by viewModel.isLiberatingAnomalies.collectAsStateWithLifecycle()
    val liberatedBannerMessage by viewModel.liberatedBannerMessage.collectAsStateWithLifecycle()

    val micAmplitude by viewModel.microphoneAnalyzer.amplitude.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.spiritTtsManager.isSpeaking.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingSpiritResponse.collectAsStateWithLifecycle()
    val spiritLogList by viewModel.spiritPhraseLog.collectAsStateWithLifecycle()

    val capturedCount by viewModel.capturedCount.collectAsStateWithLifecycle()
    val isClosingDimension by viewModel.isClosingDimension.collectAsStateWithLifecycle()
    val isCapturingEntity by viewModel.isCapturingEntity.collectAsStateWithLifecycle()
    val demonVampireCount by viewModel.demonVampireCount.collectAsStateWithLifecycle()
    val activeDimension by viewModel.activeDimensionPlane.collectAsStateWithLifecycle()
    val activeSigil by viewModel.activeSigil.collectAsStateWithLifecycle()
    val sigilTimerSeconds by viewModel.sigilTimerSeconds.collectAsStateWithLifecycle()
    val autoDimensionSealingEnabled by viewModel.autoDimensionSealingEnabled.collectAsStateWithLifecycle()

    var showSigilForgeDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(ScannerModuleTab.ALL) }
    val hasHighDemonVampireConcentration by viewModel.hasHighDemonVampireConcentration.collectAsStateWithLifecycle()

    val isMagnetShieldActive by viewModel.isMagnetShieldActive.collectAsStateWithLifecycle()
    val isEmfSuppressionActive by viewModel.isEmfSuppressionActive.collectAsStateWithLifecycle()
    val isSystemSpeechEnabled by viewModel.isSystemSpeechEnabled.collectAsStateWithLifecycle()
    val isTtsMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    val magnetLogNotes by viewModel.magnetLogNotes.collectAsStateWithLifecycle()
    val autoFilterRotationEnabled by viewModel.autoFilterRotationEnabled.collectAsStateWithLifecycle()
    val autoCaptureLiberateEnabled by viewModel.autoCaptureLiberateEnabled.collectAsStateWithLifecycle()
    val backgroundScan247Enabled by viewModel.backgroundScan247Enabled.collectAsStateWithLifecycle()
    val isBatterySaverEnabled by viewModel.isBatterySaverEnabled.collectAsStateWithLifecycle()
    val isBatterySaverThrottling by viewModel.isBatterySaverThrottling.collectAsStateWithLifecycle()

    var showSaveDialog by remember { mutableStateOf(false) }
    var captureName by remember { mutableStateOf("") }
    var captureLocation by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("scanner_screen")
    ) {
        // Live Camera Feed with Digital Infra-Green / Night-Vision Overlay & Frame Analyzer
        CameraBackgroundView(
            modifier = Modifier.fillMaxSize(),
            primaryColor = filterMode.primaryColor,
            filterMode = filterMode,
            filterIntensity = filterIntensity,
            isEnabled = isCameraEnabled,
            isFlashlightEnabled = isFlashlightEnabled,
            imageCapture = imageCapture,
            onAnomaliesDetected = { anomalies, avgLum ->
                viewModel.onCameraFrameAnomalies(anomalies, avgLum)
            }
        )

        // Animated Compass Overlay
        GhostCompassOverlay(
            azimuth = compassAzimuth,
            emfLevel = emfLevel,
            primaryColor = filterMode.primaryColor,
            modifier = Modifier.fillMaxSize()
        )

        // Atmospheric Infrared Spectral Overlay
        Image(
            painter = painterResource(id = R.drawable.img_spectral_bg),
            contentDescription = "Infrarot Nachtsicht Hintergrund",
            modifier = Modifier
                .fillMaxSize()
                .alpha((0.15f + filterIntensity * 0.20f).coerceIn(0.05f, 0.45f)),
            contentScale = ContentScale.Crop
        )

        // Spectral Color Tint Layer
        val tintColor = when (filterMode) {
            FilterMode.INFRA_GREEN -> Color(0xFF00FF66).copy(alpha = (0.05f + filterIntensity * 0.18f).coerceIn(0.02f, 0.35f))
            FilterMode.THERMAL_RED -> Color(0xFFFF4400).copy(alpha = (0.05f + filterIntensity * 0.22f).coerceIn(0.02f, 0.40f))
            FilterMode.QUANTUM_MATRIX -> Color(0xFF00E5FF).copy(alpha = (0.05f + filterIntensity * 0.18f).coerceIn(0.02f, 0.35f))
            FilterMode.ULTRAVIOLET -> Color(0xFFBB33FF).copy(alpha = (0.05f + filterIntensity * 0.22f).coerceIn(0.02f, 0.40f))
            FilterMode.INFRA_YELLOW -> Color(0xFFFFCC00).copy(alpha = (0.05f + filterIntensity * 0.18f).coerceIn(0.02f, 0.35f))
            FilterMode.INFRA_BLUE -> Color(0xFF0088FF).copy(alpha = (0.05f + filterIntensity * 0.18f).coerceIn(0.02f, 0.35f))
            FilterMode.INFRARED -> Color(0xFFFF2A2A).copy(alpha = (0.05f + filterIntensity * 0.22f).coerceIn(0.02f, 0.40f))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tintColor)
        )

        // Real-Time Camera Anomaly Targeting Overlay
        CameraAnomalyOverlayCanvas(
            anomalies = cameraAnomalies,
            filterMode = filterMode,
            avgLuminance = cameraAvgLuminance,
            modifier = Modifier.fillMaxSize()
        )

        // CRT Scanlines Overlay
        if (showCrtOverlay) {
            ScanLinesOverlay(
                modifier = Modifier.fillMaxSize(),
                lineColor = filterMode.primaryColor.copy(alpha = 0.08f)
            )
        }

        // Flashing Red Border Visual Warning when a high concentration of Demons or Vampires is detected
        FlashingRedWarningOverlay(
            isActive = hasHighDemonVampireConcentration,
            entityCount = demonVampireCount
        )

        // Main Foreground Content Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Liberation Banner Notification Card
            liberatedBannerMessage?.let { bannerMsg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF003D33)),
                    border = BorderStroke(1.5.dp, Color(0xFF00FFCC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bannerMsg,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFE0FFFF),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.dismissLiberationBanner() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Banner Schließen",
                                tint = Color(0xFF00FFCC)
                            )
                        }
                    }
                }
            }

            // 1. TOP HEADER & DEVICE CONNECTION STATUS BAR
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF070C09).copy(alpha = 0.92f)),
                border = BorderStroke(1.5.dp, filterMode.primaryColor.copy(alpha = 0.75f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header Row with Title and Quick Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = filterMode.primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = UiStrings.getHudTitle(appLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = filterMode.primaryColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.toggleFlashlight() },
                                modifier = Modifier.size(34.dp).testTag("flashlight_quick_icon_button")
                            ) {
                                Icon(
                                    imageVector = if (isFlashlightEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Taschenlampe",
                                    tint = if (isFlashlightEnabled) Color(0xFFFFFF00) else filterMode.primaryColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.toggleScanning() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (isScanning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Scan Toggle",
                                    tint = filterMode.primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Device & Hardware Sensor Connectivity Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D1B13))
                            .border(1.dp, Color(0xFF00FF66).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00FF66))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GERÄT VERBUNDEN • $activeSensorCount SENSOREN AKTIV",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF00FF88),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                            )
                        }

                        Text(
                            text = "${compassAzimuth.toInt()}° | ${satelliteCount} SAT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00E5FF),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }

                    // Sensor Names & Telemetry String
                    Text(
                        text = "KANÄLE: ${activeSensorNames.joinToString(" • ")} | GYRO ${String.format(java.util.Locale.US, "%.1f", gyroSpeed)} | LUX ${String.format(java.util.Locale.US, "%.0f", lightLux)}lx | BARO ${String.format(java.util.Locale.US, "%.1f", pressureHpa)}hPa",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = filterMode.primaryColor.copy(alpha = 0.85f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp
                        )
                    )
                }
            }

            // 2. CLEAN SECTION SELECTOR TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0C130F))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ScannerModuleTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) filterMode.primaryColor.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) filterMode.primaryColor else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) filterMode.primaryColor else Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 8.5.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            // Tactical Flashlight Button
            Button(
                onClick = { viewModel.toggleFlashlight() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("flashlight_toggle_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFlashlightEnabled) Color(0xFFFFFF00) else Color(0xFF131A15),
                    contentColor = if (isFlashlightEnabled) Color.Black else filterMode.primaryColor
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (isFlashlightEnabled) Color(0xFFFFFF00) else filterMode.primaryColor.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (isFlashlightEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Taschenlampe & Lichtkegel",
                    tint = if (isFlashlightEnabled) Color.Black else filterMode.primaryColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFlashlightEnabled) "⚡ TASCHENLAMPE: FOKUSSIERTER LICHTKEGEL [AN]" else "🔦 TASCHENLAMPE & LICHTKEGEL STEUERN [AUS]",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.6.sp
                    )
                )
            }

            // Quick Hardware Toggles (Vibration, Radar Audio, Speech)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vibration Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VIB:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { viewModel.toggleVibration() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = filterMode.primaryColor,
                            uncheckedThumbColor = filterMode.primaryColor.copy(alpha = 0.5f),
                            uncheckedTrackColor = Color(0xFF1A1A1A)
                        ),
                        modifier = Modifier.testTag("vibration_quick_switch")
                    )
                }

                // Radar Audio Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RADAR-TON:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = audioFeedbackEnabled,
                        onCheckedChange = { viewModel.toggleAudioFeedback() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = filterMode.primaryColor,
                            uncheckedThumbColor = filterMode.primaryColor.copy(alpha = 0.5f),
                            uncheckedTrackColor = Color(0xFF1A1A1A)
                        ),
                        modifier = Modifier.testTag("radar_mute_switch")
                    )
                }

                // System Speech Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SPRACHE:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = isSystemSpeechEnabled && !isTtsMuted,
                        onCheckedChange = { viewModel.toggleSystemSpeechEnabled() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = filterMode.primaryColor,
                            uncheckedThumbColor = filterMode.primaryColor.copy(alpha = 0.5f),
                            uncheckedTrackColor = Color(0xFF1A1A1A)
                        ),
                        modifier = Modifier.testTag("system_speech_switch")
                    )
                }
            }

            // === 3. TAB CONTENT SECTIONS ===
            // SECTION: RADAR & HUD
            if (selectedTab == ScannerModuleTab.ALL || selectedTab == ScannerModuleTab.RADAR) {
                // Central Interactive Radar Scanner Canvas
                RadarScannerCanvas(
                    blips = radarBlips,
                    filterMode = filterMode,
                    isScanning = isScanning,
                    isLiberating = isLiberatingAnomalies,
                    onLiberateAll = { viewModel.liberateRadarAnomalies() },
                    onLiberateBlip = { blip -> viewModel.handleRadarBlipClick(blip) }
                )

                // Primary Action Button: Liberate / Harmonize
                Button(
                    onClick = { viewModel.liberateRadarAnomalies() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("liberate_spirits_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✨ GEISTER & ANOMALIEN BEFREIEN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // SECTION: DIMENSIONEN & DÄMONEN-SIEGEL
            if (selectedTab == ScannerModuleTab.ALL || selectedTab == ScannerModuleTab.DIMENSIONS) {
                CaptureAndPortalCard(
                    activeRiftsCount = radarBlips.count { it.category == com.example.ui.components.EntityCategory.DIMENSION_RIFT },
                    capturedCount = capturedCount,
                    isClosingDimension = isClosingDimension,
                    isCapturingEntity = isCapturingEntity,
                    primaryColor = filterMode.primaryColor,
                    activeSigil = activeSigil,
                    sigilTimerSeconds = sigilTimerSeconds,
                    activeDimension = activeDimension,
                    autoDimensionSealingEnabled = autoDimensionSealingEnabled,
                    onToggleAutoDimensionSealing = { viewModel.toggleAutoDimensionSealing() },
                    onCloseDimension = { viewModel.closeDimensionRift() },
                    onSpawnDimension = { viewModel.spawnDimensionRift() },
                    onCaptureEntity = { viewModel.captureEntity() },
                    onSpawnThreat = { viewModel.spawnDemonOrVampire() },
                    onOpenSigilForge = { showSigilForgeDialog = true }
                )
            }

            // SECTION: EMF, SPEKTRUM & MAGNETFELD
            if (selectedTab == ScannerModuleTab.ALL || selectedTab == ScannerModuleTab.EMF_FILTER) {
                // EMF Feldstärke Meter Gauge
                EmfMeter(
                    emfValue = emfLevel,
                    dangerLevel = dangerLevel,
                    frequencyKhz = frequencyKhz,
                    isEmfSuppressed = isEmfSuppressionActive,
                    onToggleEmfSuppression = { viewModel.toggleEmfSuppression() },
                    onNeutralizeEmf = { viewModel.neutralizeEmfSpike() }
                )

                // Real-Time Neon Green Line Chart for Magnetic Field Fluctuations
                RealtimeEmfLineChart(
                    dataPoints = emfHistory,
                    currentEmf = emfLevel,
                    isScanning = isScanning,
                    lineColor = filterMode.primaryColor
                )

                // Magnetfeld-Analyse, Notiz-Protokoll & Schild-Schutz Panel
                MagnetFieldAndShieldCard(
                    emfLevel = emfLevel,
                    isMagnetShieldActive = isMagnetShieldActive,
                    magnetLogNotes = magnetLogNotes,
                    autoFilterRotationEnabled = autoFilterRotationEnabled,
                    autoCaptureLiberateEnabled = autoCaptureLiberateEnabled,
                    backgroundScan247Enabled = backgroundScan247Enabled,
                    filterMode = filterMode,
                    batterySaverEnabled = isBatterySaverEnabled,
                    isBatterySaverThrottling = isBatterySaverThrottling,
                    appLanguage = appLanguage,
                    onToggleMagnetShield = { viewModel.toggleMagnetShield() },
                    onAddMagnetNote = { noteText, sourceTag -> viewModel.addMagnetNote(noteText, sourceTag) },
                    onClearMagnetNotes = { viewModel.clearMagnetLogNotes() },
                    onToggleAutoFilterRotation = { viewModel.toggleAutoFilterRotation() },
                    onToggleAutoCaptureLiberate = { viewModel.toggleAutoCaptureLiberate() },
                    onToggleBackgroundScan247 = { viewModel.toggleBackgroundScan247() },
                    onToggleBatterySaver = { viewModel.toggleBatterySaverEnabled() }
                )

                // Filter Selector Chips & Intensity Slider
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = filterMode.primaryColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SPECTRAL FILTERANSICHT:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterMode.values().forEach { mode ->
                            val isSelected = mode == filterMode
                            val activeColor = mode.primaryColor

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) activeColor.copy(alpha = 0.25f)
                                        else Color.Black.copy(alpha = 0.6f)
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setFilterMode(mode) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) activeColor else Color.White.copy(alpha = 0.6f),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Filter Intensity / Contrast Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, filterMode.primaryColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FILTER-INTENSITÄT / KONTRAST:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = filterMode.primaryColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "${(filterIntensity * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = filterMode.primaryColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Slider(
                            value = filterIntensity,
                            onValueChange = { viewModel.setFilterIntensity(it) },
                            valueRange = 0.10f..1.00f,
                            colors = SliderDefaults.colors(
                                thumbColor = filterMode.primaryColor,
                                activeTrackColor = filterMode.primaryColor,
                                inactiveTrackColor = filterMode.primaryColor.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("filter_intensity_slider")
                        )
                    }
                }
            }

            // SECTION: SPIRIT-BOX & AUDIO
            if (selectedTab == ScannerModuleTab.ALL || selectedTab == ScannerModuleTab.SPIRIT_BOX) {
                // Spirit Box Live Transcripts & Communications Log History
                SpiritBoxTranscriptListCard(
                    logs = spiritLogList,
                    filterMode = filterMode,
                    isGenerating = isGenerating,
                    isSpeaking = isSpeaking,
                    onAskQuestion = { question -> viewModel.askSpirit(question) },
                    onTriggerAutoSweep = { viewModel.generateAndPlaySensorCreepyPhrase() },
                    onRespeak = { entry -> viewModel.respeakSpiritLogEntry(entry) },
                    onClearLogs = { viewModel.clearSpiritLog() }
                )

                // Spirit-Box Audio Spectrum Waveform
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D140F)),
                    border = BorderStroke(1.dp, filterMode.primaryColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
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
                            Text(
                                text = "SPIRIT-BOX AUDIO SPEKTRUM (STIMMEN-SCANNER)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = filterMode.primaryColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (isScanning) "ACTIVE SWEEP" else "STANDBY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isScanning) Color(0xFF00FFCC) else Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        AudioWaveformCanvas(
                            isActive = true,
                            isScanning = isScanning,
                            isGenerating = isGenerating,
                            isSpeaking = isSpeaking,
                            liveMicAmplitude = micAmplitude,
                            frequencyKhz = frequencyKhz,
                            emfLevel = emfLevel,
                            waveColor = filterMode.primaryColor,
                            amplitudeMultiplier = if (isSpeaking || isGenerating) 1.5f else 0.8f
                        )
                    }
                }
            }

            // Capture Anomaly / Save Entity Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("save_entity_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = filterMode.primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = UiStrings.getQuickCaptureBtn(appLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                Button(
                    onClick = {
                        imageCapture.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    coroutineScope.launch {
                                        val success = ImageCaptureUtil.saveTintedImageToGallery(context, image, filterMode.primaryColor)
                                        Toast.makeText(context, if (success) "Snapshot saved" else "Failed to save", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("capture_snapshot_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = filterMode.primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SNAPSHOT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }

    // Dimension & Sigil Forge Dialog Overlay (Root Level)
    if (showSigilForgeDialog) {
        DimensionSigilDialog(
            viewModel = viewModel,
            onDismiss = { showSigilForgeDialog = false }
        )
    }

    // Quick Capture Save Dialog (Root Level)
    if (showSaveDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showSaveDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF041C0E)),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = UiStrings.getSaveEntityTitle(appLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "EMF: $emfLevel mG | Frequenz $frequencyKhz kHz",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    OutlinedTextField(
                        value = captureName,
                        onValueChange = { captureName = it },
                        label = { Text(UiStrings.getEntityNameLabel(appLanguage), color = filterMode.primaryColor) },
                        placeholder = { Text("e.g. Shadow Entity #01", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = filterMode.primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = captureLocation,
                        onValueChange = { captureLocation = it },
                        label = { Text(UiStrings.getEntityLocationLabel(appLanguage), color = filterMode.primaryColor) },
                        placeholder = { Text("e.g. Attic Room 3", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = filterMode.primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showSaveDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text(UiStrings.getCancelBtn(appLanguage), color = Color.Gray, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.saveCurrentScanAsGhost(captureName, captureLocation)
                                captureName = ""
                                captureLocation = ""
                                showSaveDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = filterMode.primaryColor)
                        ) {
                            Text(UiStrings.getSaveBtn(appLanguage), color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
