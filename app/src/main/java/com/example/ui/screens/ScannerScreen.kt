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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.util.ImageCaptureUtil
import android.widget.Toast
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AudioWaveformCanvas
import com.example.ui.components.CameraBackgroundView
import com.example.ui.components.EmfMeter
import com.example.ui.components.FilterMode
import com.example.ui.components.MagnetFieldAndShieldCard
import com.example.ui.components.RadarScannerCanvas
import com.example.ui.components.SpiritBoxTranscriptListCard
import com.example.ui.components.RealtimeEmfLineChart
import com.example.ui.components.ScanLinesOverlay
import com.example.ui.viewmodel.GhostViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings

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
    val isSensorActive by viewModel.sensorManager.isSensorActive.collectAsStateWithLifecycle()
    val compassAzimuth by viewModel.compassAzimuth.collectAsStateWithLifecycle()
    val satelliteCount by viewModel.satelliteCount.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val cameraAnomalies by viewModel.cameraAnomalies.collectAsStateWithLifecycle()
    val cameraAvgLuminance by viewModel.cameraAvgLuminance.collectAsStateWithLifecycle()
    val isLiberatingAnomalies by viewModel.isLiberatingAnomalies.collectAsStateWithLifecycle()
    val liberatedBannerMessage by viewModel.liberatedBannerMessage.collectAsStateWithLifecycle()

    val activeSensorCount by viewModel.activeSensorCount.collectAsStateWithLifecycle()
    val activeSensorNames by viewModel.activeSensorNames.collectAsStateWithLifecycle()
    val gyroSpeed by viewModel.gyroSpeed.collectAsStateWithLifecycle()
    val lightLux by viewModel.lightLux.collectAsStateWithLifecycle()
    val pressureHpa by viewModel.pressureHpa.collectAsStateWithLifecycle()

    val micAmplitude by viewModel.microphoneAnalyzer.amplitude.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.spiritTtsManager.isSpeaking.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingSpiritResponse.collectAsStateWithLifecycle()
    val spiritLogList by viewModel.spiritPhraseLog.collectAsStateWithLifecycle()

    val capturedCount by viewModel.capturedCount.collectAsStateWithLifecycle()
    val isClosingDimension by viewModel.isClosingDimension.collectAsStateWithLifecycle()
    val isCapturingEntity by viewModel.isCapturingEntity.collectAsStateWithLifecycle()
    val demonVampireCount by viewModel.demonVampireCount.collectAsStateWithLifecycle()
    val hasHighDemonVampireConcentration by viewModel.hasHighDemonVampireConcentration.collectAsStateWithLifecycle()

    val isMagnetShieldActive by viewModel.isMagnetShieldActive.collectAsStateWithLifecycle()
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
        com.example.ui.components.GhostCompassOverlay(
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

        // Spectral Color Tint Layer based on active FilterMode and filterIntensity
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
        com.example.ui.components.CameraAnomalyOverlayCanvas(
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
        com.example.ui.components.FlashingRedWarningOverlay(
            isActive = hasHighDemonVampireConcentration,
            entityCount = demonVampireCount
        )

        // Main Foreground Content Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bannerMsg,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFE0FFFF),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
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

            // HUD Top Header Bar with Deep Obsidian Dark Theme & Glowing Neon Accents
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070C09).copy(alpha = 0.88f))
                    .border(1.5.dp, filterMode.primaryColor.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = filterMode.primaryColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = UiStrings.getHudTitle(appLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = filterMode.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = if (isSensorActive) "HARDWARE-SENSOREN ($activeSensorCount AKTIV): ${activeSensorNames.joinToString(" • ")}" else UiStrings.getSensorAtmospheric(appLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = filterMode.primaryColor.copy(alpha = 0.9f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "TELEMETRIE: GYRO ${String.format(java.util.Locale.US, "%.1f", gyroSpeed)}rad/s | LUX ${String.format(java.util.Locale.US, "%.0f", lightLux)}lx | BARO ${String.format(java.util.Locale.US, "%.1f", pressureHpa)}hPa",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00FFCC),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp
                            )
                        )
                        Text(
                            text = if (cameraAnomalies.isNotEmpty()) "KAMERA-ANOMALIEN: ${cameraAnomalies.size} ENTDECKT" else "KAMERA-ANALYZER: IN SPEKTRAL-SUCHE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (cameraAnomalies.isNotEmpty()) Color(0xFFFF3333) else filterMode.primaryColor.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (satelliteCount > 0) {
                            Text(
                                text = "SATELLITEN UPLINK: $satelliteCount SAT | COMPASS: ${compassAzimuth.toInt()}°",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF00E5FF),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleFlashlight() },
                        modifier = Modifier.testTag("flashlight_quick_icon_button")
                    ) {
                        Icon(
                            imageVector = if (isFlashlightEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Taschenlampe & Lichtkegel",
                            tint = if (isFlashlightEnabled) Color(0xFFFFFF00) else filterMode.primaryColor.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = { viewModel.toggleScanning() }) {
                        Icon(
                            imageVector = if (isScanning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Scan Toggle",
                            tint = filterMode.primaryColor
                        )
                    }
                }
            }

            // Dedicated Tactical Flashlight & Light Cone Control Button
            Button(
                onClick = { viewModel.toggleFlashlight() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                    contentDescription = "Kamera-Taschenlampe & Lichtkegel Steuerung",
                    tint = if (isFlashlightEnabled) Color.Black else filterMode.primaryColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFlashlightEnabled) "⚡ TASCHENLAMPE: FOKUSSIERTER LICHTKEGEL [AN]" else "🔦 TASCHENLAMPE & LICHTKEGEL STEUERN [AUS]",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.8.sp
                    )
                )
            }

            // Central Interactive Radar Scanner Canvas directly under Taschenlampe
            RadarScannerCanvas(
                blips = radarBlips,
                filterMode = filterMode,
                isScanning = isScanning,
                isLiberating = isLiberatingAnomalies,
                onLiberateAll = { viewModel.liberateRadarAnomalies() },
                onLiberateBlip = { blip -> viewModel.handleRadarBlipClick(blip) }
            )

            // Primary Spirit Liberation Action Button
            Button(
                onClick = { viewModel.liberateRadarAnomalies() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("liberate_spirits_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF)
                ),
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
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Dedicated Radar Audio & Vibration Toggle Controls
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
                        text = "VIBRATION:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    androidx.compose.material3.Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { viewModel.toggleVibration() },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
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
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    androidx.compose.material3.Switch(
                        checked = audioFeedbackEnabled,
                        onCheckedChange = { viewModel.toggleAudioFeedback() },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = filterMode.primaryColor,
                            uncheckedThumbColor = filterMode.primaryColor.copy(alpha = 0.5f),
                            uncheckedTrackColor = Color(0xFF1A1A1A)
                        ),
                        modifier = Modifier.testTag("radar_mute_switch")
                    )
                }
            }

            // Spirit Box Live Transcripts & Communications Log History ("Verlauf nach oben verschoben")
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

            // Magnetfeld-Analyse, Notiz-Protokoll & TV/PC-Monitor Schild-Schutz Panel
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

            // EMF Feldstärke Meter Gauge (unter Geister & Anomalien platziert)
            EmfMeter(
                emfValue = emfLevel,
                dangerLevel = dangerLevel,
                frequencyKhz = frequencyKhz
            )

            // Dimension Portal Closing & Vampire/Demon Containment Trap Panel
            com.example.ui.components.CaptureAndPortalCard(
                activeRiftsCount = radarBlips.count { it.category == com.example.ui.components.EntityCategory.DIMENSION_RIFT },
                capturedCount = capturedCount,
                isClosingDimension = isClosingDimension,
                isCapturingEntity = isCapturingEntity,
                primaryColor = filterMode.primaryColor,
                onCloseDimension = { viewModel.closeDimensionRift() },
                onSpawnDimension = { viewModel.spawnDimensionRift() },
                onCaptureEntity = { viewModel.captureEntity() },
                onSpawnThreat = { viewModel.spawnDemonOrVampire() }
            )

            // Real-Time Neon Green Line Chart for Magnetic Field Fluctuations
            RealtimeEmfLineChart(
                dataPoints = emfHistory,
                currentEmf = emfLevel,
                isScanning = isScanning,
                lineColor = filterMode.primaryColor
            )

            // Spirit-Box Audio Spectrum Waveform (Voice Scanner)
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

            // Filter Selector Chips ("Filteransicht") & Intensity Slider
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

            // Capture Anomaly / Save Entity Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        showSaveDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("save_entity_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = filterMode.primaryColor
                    ),
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
                            fontWeight = FontWeight.Bold
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
                        .height(52.dp)
                        .testTag("capture_snapshot_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = filterMode.primaryColor
                    ),
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
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    // Quick Capture Save Dialog
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
