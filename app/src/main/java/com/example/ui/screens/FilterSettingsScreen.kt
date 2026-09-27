package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.VoicePersona
import com.example.ui.components.CollapsibleHudWindow
import com.example.ui.components.FilterMode
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.viewmodel.GhostViewModel
import java.util.Locale

@Composable
fun FilterSettingsScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val appThemeColor by viewModel.appThemeColor.collectAsStateWithLifecycle()
    val currentFilterMode by viewModel.currentFilterMode.collectAsStateWithLifecycle()
    val filterIntensity by viewModel.filterIntensity.collectAsStateWithLifecycle()
    val showCrtOverlay by viewModel.showCrtOverlay.collectAsStateWithLifecycle()
    val isCameraEnabled by viewModel.isCameraBackgroundEnabled.collectAsStateWithLifecycle()
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
    val audioFeedbackEnabled by viewModel.audioFeedbackEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val vibrationIntensity by viewModel.vibrationIntensity.collectAsStateWithLifecycle()
    val isAutoDestroyEnabled by viewModel.isAutoDestroyEnabled.collectAsStateWithLifecycle()
    val backgroundScan247Enabled by viewModel.backgroundScan247Enabled.collectAsStateWithLifecycle()
    val isBatterySaverEnabled by viewModel.isBatterySaverEnabled.collectAsStateWithLifecycle()
    val isBatterySaverThrottling by viewModel.isBatterySaverThrottling.collectAsStateWithLifecycle()

    // TTS & Sound States
    val ttsVolume by viewModel.ttsVolume.collectAsStateWithLifecycle()
    val ttsPitch by viewModel.ttsPitch.collectAsStateWithLifecycle()
    val ttsRate by viewModel.ttsSpeechRate.collectAsStateWithLifecycle()
    val ttsVoicePersona by viewModel.ttsVoicePersona.collectAsStateWithLifecycle()
    val isTtsMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()

    // Microphone & EVP Communicator States
    val micThreshold by viewModel.microphoneAnalyzer.speechThreshold.collectAsStateWithLifecycle()
    val isCommunicatorActive by viewModel.isCommunicatorActive.collectAsStateWithLifecycle()

    // Automations States
    val autoFilterRotationEnabled by viewModel.autoFilterRotationEnabled.collectAsStateWithLifecycle()
    val autoCaptureLiberateEnabled by viewModel.autoCaptureLiberateEnabled.collectAsStateWithLifecycle()
    val autoDimensionSealingEnabled by viewModel.autoDimensionSealingEnabled.collectAsStateWithLifecycle()
    val autoSpiritBoxEnabled by viewModel.autoSpiritBoxEnabled.collectAsStateWithLifecycle()

    // Security States
    val isSecurityEnabled by viewModel.isSecurityEnabled.collectAsStateWithLifecycle()
    val autoLockOnBackground by viewModel.autoLockOnBackground.collectAsStateWithLifecycle()
    val userPin by viewModel.userPin.collectAsStateWithLifecycle()
    val recoveryEmail by viewModel.recoveryEmail.collectAsStateWithLifecycle()

    // Master Collapse / Expand State
    var globalWindowState by remember { mutableStateOf<Boolean?>(null) }

    var showChangePinDialog by remember { mutableStateOf(false) }
    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var pinDialogError by remember { mutableStateOf("") }
    var pinDialogSuccess by remember { mutableStateOf("") }
    var editingRecoveryEmail by remember(recoveryEmail) { mutableStateOf(recoveryEmail) }

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // System Permissions
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasGpsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasNotifPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasMicPermission = granted
        if (granted) {
            viewModel.toggleCommunicatorMode(forceActive = true)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
    }
    val gpsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        hasGpsPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasNotifPermission = granted
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("filter_settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. TOP HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(InfraGreenSurface)
                    .border(1.dp, InfraGreenBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = InfraGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = UiStrings.getSettingsHeader(appLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "Kalibrierung, Sensoren, Haptik, Audio & Sicherheit",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            // Quick Master Collapse/Expand Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { globalWindowState = false },
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                    border = BorderStroke(1.dp, InfraGreenBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = InfraGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ALLE MINIMIEREN", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                }

                Button(
                    onClick = { globalWindowState = true },
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                    border = BorderStroke(1.dp, InfraGreenBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = InfraGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ALLE MAXIMIEREN", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                }
            }

            // 2. SPEKTRALFILTER & ANZEIGE-REGELUNG (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "Spektralfilter & Display Regelung",
                icon = Icons.Default.Tune,
                accentColor = currentFilterMode.primaryColor,
                initialExpanded = true,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 380.dp,
                expandedMaxHeight = 620.dp,
                testTag = "spectral_filter_settings_card"
            ) {
                // Filter Mode Selection Chips
                Text(
                    text = "Aktiver Spektral-Modus:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InfraGreenTextPrimaryVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterMode.values().take(4).forEach { mode ->
                        val isSelected = currentFilterMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFilterMode(mode) },
                            label = {
                                Text(
                                    text = mode.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 9.sp
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = mode.primaryColor.copy(alpha = 0.25f),
                                selectedLabelColor = mode.primaryColor,
                                containerColor = Color.Transparent,
                                labelColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, if (isSelected) mode.primaryColor else Color(0xFF223322)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterMode.values().drop(4).forEach { mode ->
                        val isSelected = currentFilterMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFilterMode(mode) },
                            label = {
                                Text(
                                    text = mode.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 9.sp
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = mode.primaryColor.copy(alpha = 0.25f),
                                selectedLabelColor = mode.primaryColor,
                                containerColor = Color.Transparent,
                                labelColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, if (isSelected) mode.primaryColor else Color(0xFF223322)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Filter-Intensität / Kontrast Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FILTER-INTENSITÄT / KONTRAST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = currentFilterMode.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "${(filterIntensity * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = currentFilterMode.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Slider(
                        value = filterIntensity,
                        onValueChange = { viewModel.setFilterIntensity(it) },
                        valueRange = 0.10f..1.00f,
                        colors = SliderDefaults.colors(
                            thumbColor = currentFilterMode.primaryColor,
                            activeTrackColor = currentFilterMode.primaryColor,
                            inactiveTrackColor = currentFilterMode.primaryColor.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("settings_filter_intensity_slider")
                    )
                }

                // CRT Scanlines Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CRT-SCANLINES EFFEKT", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Authentische HUD-Gitterlinien im Scanner", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = showCrtOverlay,
                        onCheckedChange = { viewModel.toggleCrtOverlay() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("crt_overlay_switch")
                    )
                }

                // Camera Background Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("KAMERA-HINTERGRUND (AR-SUCHER)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Echte Kamera als Hintergrund des Scanners", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isCameraEnabled,
                        onCheckedChange = { viewModel.toggleCameraBackground() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("camera_background_switch")
                    )
                }

                // Fullscreen HUD Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("VOLLBILD HUD MODUS", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Blendet Systemleisten für maximalen Scanner aus", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isFullscreen,
                        onCheckedChange = { viewModel.toggleFullscreen() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("fullscreen_hud_switch")
                    )
                }

                // Auto-Destroy Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AUTO-HARMONISIERUNG & BEFREIUNG", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Befreit kritische Geister bei Stufe 5 friedlich oder hält sie im Verlauf fest", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isAutoDestroyEnabled,
                        onCheckedChange = { viewModel.toggleAutoDestroy() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = AlertInfraRed),
                        modifier = Modifier.testTag("auto_destroy_switch")
                    )
                }
            }

            // 3. HAPTIK & VIBRATIONS-REGELUNG (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "Haptik & Vibrations-Regelung",
                icon = Icons.Default.Vibration,
                initialExpanded = true,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 320.dp,
                expandedMaxHeight = 520.dp,
                testTag = "vibration_settings_card"
            ) {
                // Vibration Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("HAPTISCHES FEEDBACK BEI FUNDEN", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Gerätevibration beim Erfassen von Geistern & Anomalien", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { viewModel.setVibrationEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("vibration_enable_switch")
                    )
                }

                // Vibration Intensity Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("VIBRATIONS-STÄRKE", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("${(vibrationIntensity * 100).toInt()}%", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Slider(
                        value = vibrationIntensity,
                        onValueChange = { viewModel.setVibrationIntensity(it) },
                        valueRange = 0.05f..1.00f,
                        colors = SliderDefaults.colors(
                            thumbColor = InfraGreenPrimary,
                            activeTrackColor = InfraGreenPrimary,
                            inactiveTrackColor = InfraGreenPrimary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("vibration_intensity_slider")
                    )

                    Button(
                        onClick = { viewModel.triggerEntityDetectionVibration(4) },
                        colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, InfraGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp).testTag("test_vibration_button")
                    ) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = InfraGreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("VIBRATION TESTEN", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            // 4. AUDIO & GEISTERSTIMMEN-REGELUNG (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "Audio & Sprachausgabe Regelung",
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                initialExpanded = true,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 400.dp,
                expandedMaxHeight = 620.dp,
                testTag = "audio_tts_settings_card"
            ) {
                // Audio SFX Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AUDIO-EFFEKTE (RADAR & SFX)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Akustische Geigerzähler- & Radartöne", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = audioFeedbackEnabled,
                        onCheckedChange = { viewModel.toggleAudioFeedback() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("audio_feedback_switch")
                    )
                }

                // TTS Mute Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("STUMMSCHALTUNG (MUTE)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text(if (isTtsMuted) "Stumm geschaltet" else "Sprachausgabe aktiv", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isTtsMuted,
                        onCheckedChange = { viewModel.setTtsMuted(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = AlertInfraRed),
                        modifier = Modifier.testTag("tts_mute_switch")
                    )
                }

                // Volume Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("LAUTSTÄRKE (SPIRIT-BOX)", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("${(ttsVolume * 100).toInt()}%", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Slider(
                        value = ttsVolume,
                        onValueChange = { viewModel.setTtsVolume(it) },
                        valueRange = 0.0f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = InfraGreenPrimary, activeTrackColor = InfraGreenPrimary, inactiveTrackColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().testTag("tts_volume_slider")
                    )
                }

                // Pitch Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TONHÖHE (PITCH)", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(String.format(Locale.US, "%.2fx", ttsPitch), color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Slider(
                        value = ttsPitch,
                        onValueChange = { viewModel.setTtsPitch(it) },
                        valueRange = 0.50f..1.80f,
                        colors = SliderDefaults.colors(thumbColor = InfraGreenPrimary, activeTrackColor = InfraGreenPrimary, inactiveTrackColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().testTag("tts_pitch_slider")
                    )
                }

                // Speech Rate Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SPRECH-GESCHWINDIGKEIT (RATE)", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(String.format(Locale.US, "%.2fx", ttsRate), color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Slider(
                        value = ttsRate,
                        onValueChange = { viewModel.setTtsSpeechRate(it) },
                        valueRange = 0.50f..1.80f,
                        colors = SliderDefaults.colors(thumbColor = InfraGreenPrimary, activeTrackColor = InfraGreenPrimary, inactiveTrackColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().testTag("tts_rate_slider")
                    )
                }

                // Voice Persona Chips
                Text("Stimm-Persona auswählen:", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VoicePersona.values().forEach { persona ->
                        val isSelected = ttsVoicePersona == persona
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setTtsVoicePersona(persona) },
                            label = {
                                Text(
                                    text = persona.displayName.split(" ").last(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 9.sp
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = InfraGreenPrimary.copy(alpha = 0.25f),
                                selectedLabelColor = InfraGreenPrimary,
                                containerColor = Color.Transparent,
                                labelColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, if (isSelected) InfraGreenPrimary else Color(0xFF223322)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.testSpiritVoice() },
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, InfraGreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("test_voice_button")
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = InfraGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SPRACHAUSGABE TESTEN", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            // 5. MIKROFON & EVP-SCHWELLENWERT (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "Mikrofon & EVP-Empfindlichkeit",
                icon = Icons.Default.Mic,
                initialExpanded = true,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 260.dp,
                expandedMaxHeight = 420.dp,
                testTag = "mic_settings_card"
            ) {
                // Mic Threshold Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SPRACHERKENNUNGS-SCHWELLE", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("${(micThreshold * 100).toInt()}%", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Slider(
                        value = micThreshold,
                        onValueChange = { viewModel.setCommunicatorSpeechThreshold(it) },
                        valueRange = 0.04f..0.50f,
                        colors = SliderDefaults.colors(thumbColor = InfraGreenPrimary, activeTrackColor = InfraGreenPrimary, inactiveTrackColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().testTag("mic_threshold_slider")
                    )
                }

                // Mic Listening Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfraGreenSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("EVP-MIKROFON LAUSCHEN", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text(if (isCommunicatorActive) "Mikrofon ist scharf geschaltet" else "Mikrofon ist inaktiv", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isCommunicatorActive,
                        onCheckedChange = { viewModel.toggleCommunicatorMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("mic_listening_switch")
                    )
                }
            }

            // 6. AUTOMATISIERUNGS-SUITE (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "Automatisierungen & Assistenz",
                icon = Icons.Default.AutoAwesome,
                initialExpanded = false,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 300.dp,
                expandedMaxHeight = 500.dp,
                testTag = "automations_settings_card"
            ) {
                // Auto-Filter 5 Min Rotation
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AUTO-FILTER ROTATION (5 MIN)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Wechselt automatisch alle 5 Minuten das Spektrum", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = autoFilterRotationEnabled,
                        onCheckedChange = { viewModel.toggleAutoFilterRotation() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("auto_filter_rotation_switch")
                    )
                }

                // Auto-Capture & Liberate
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AUTO-FANG & BEFREIUNG", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Fängt Anomalien bei hohem Signal automatisch ein", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = autoCaptureLiberateEnabled,
                        onCheckedChange = { viewModel.toggleAutoCaptureLiberate() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("auto_capture_liberate_switch")
                    )
                }

                // Auto-Dimension Sealing
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AUTO-PORTAL VERSIEGELUNG", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Schließt Dimensionsrisse automatisch bei Ablauf", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = autoDimensionSealingEnabled,
                        onCheckedChange = { viewModel.toggleAutoDimensionSealing() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("auto_dimension_sealing_switch")
                    )
                }

                // Auto Spirit Box
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AUTO-SPIRIT BOX BOTSCHAFTEN", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Generiert kontinuierlich paranormale Antworten", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = autoSpiritBoxEnabled,
                        onCheckedChange = { viewModel.toggleAutoSpiritBox() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("auto_spirit_box_switch")
                    )
                }
            }

            // 7. HINTERGRUND-BETRIEB & ENERGIE (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "Hintergrund-Scan & Energie",
                icon = Icons.Default.BatterySaver,
                initialExpanded = false,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 260.dp,
                expandedMaxHeight = 420.dp,
                testTag = "background_energy_card"
            ) {
                // 24/7 Background Scanner
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("24/7 HINTERGRUND-PROZESS", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Sucht auch bei geschlossener App nach Anomalien", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = backgroundScan247Enabled,
                        onCheckedChange = { viewModel.toggleBackgroundScan247Enabled(context) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("background_scan_switch")
                    )
                }

                // Battery Saver Mode
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("INTELLIGENTER AKKUSPARMODUS", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text(
                            text = if (isBatterySaverThrottling) "Drosselung aktiv (Sensor-Pausen zur Akkuschonung)" else "Regulärer Sensorbetrieb",
                            color = if (isBatterySaverThrottling) AlertInfraRed else InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = isBatterySaverEnabled,
                        onCheckedChange = { viewModel.toggleBatterySaverEnabled() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary),
                        modifier = Modifier.testTag("battery_saver_switch")
                    )
                }
            }

            // 8. SICHERHEIT & PIN-SCHUTZ (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "Sicherheit & Zugriffsschutz",
                icon = Icons.Default.Lock,
                badgeText = if (isSecurityEnabled) "AKTIV" else "INAKTIV",
                accentColor = if (isSecurityEnabled) InfraGreenPrimary else AlertInfraRed,
                initialExpanded = false,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 340.dp,
                expandedMaxHeight = 540.dp,
                testTag = "app_security_settings_card"
            ) {
                // PIN Toggle
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PIN-SPERRE AKTIVIEREN", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("App erfordert PIN beim Start", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isSecurityEnabled,
                        onCheckedChange = { viewModel.setSecurityEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary)
                    )
                }

                // Auto-Lock on Background
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AUTOMATISCH SPERREN IM HINTERGRUND", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Sperrt sofort beim Minimieren der App", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = autoLockOnBackground,
                        onCheckedChange = { viewModel.setAutoLockOnBackground(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary)
                    )
                }

                // Recovery Email Field
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(InfraGreenSurfaceVariant).padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("WIEDERHERSTELLUNGS-E-MAIL", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editingRecoveryEmail,
                            onValueChange = { editingRecoveryEmail = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = InfraGreenPrimary,
                                unfocusedBorderColor = InfraGreenBorder,
                                focusedTextColor = InfraGreenTextPrimary,
                                unfocusedTextColor = InfraGreenTextPrimary
                            ),
                            modifier = Modifier.weight(1f).testTag("recovery_email_input")
                        )
                        Button(
                            onClick = { viewModel.setRecoveryEmail(editingRecoveryEmail) },
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SPEICHERN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }

                // Action Buttons: Change PIN & Lock Now
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            currentPinInput = ""
                            newPinInput = ""
                            pinDialogError = ""
                            pinDialogSuccess = ""
                            showChangePinDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, InfraGreenBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = InfraGreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PIN ÄNDERN", color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    Button(
                        onClick = { viewModel.lockApp() },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AlertInfraRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AlertInfraRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SOFORT SPERREN", color = AlertInfraRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }

            // 9. THEME-FARBE (MINIMIERBAR)
            CollapsibleHudWindow(
                title = "Theme-Farbe der Benutzeroberfläche",
                icon = Icons.Default.Palette,
                initialExpanded = false,
                forceExpandedState = globalWindowState,
                isScrollable = false,
                testTag = "theme_color_card"
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    val colors = listOf("GREEN" to Color(0xFF00FF66), "RED" to Color(0xFFFF2244), "CYAN" to Color(0xFF00E5FF), "PURPLE" to Color(0xFFBB33FF))
                    colors.forEach { (name, colorValue) ->
                        val isSelected = appThemeColor == name
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorValue)
                                .border(if (isSelected) 3.dp else 1.dp, if (isSelected) Color.White else Color.Black, CircleShape)
                                .clickable { viewModel.setAppThemeColor(name) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Ausgewählt", tint = Color.Black, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            // 10. SPRACHAUSWAHL (MINIMIERBAR)
            CollapsibleHudWindow(
                title = "Sprache / Language",
                icon = Icons.Default.Info,
                initialExpanded = false,
                forceExpandedState = globalWindowState,
                isScrollable = false,
                testTag = "language_card"
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.setAppLanguage(AppLanguage.GERMAN) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (appLanguage == AppLanguage.GERMAN) InfraGreenPrimary.copy(alpha = 0.2f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (appLanguage == AppLanguage.GERMAN) InfraGreenPrimary else InfraGreenBorder)
                    ) {
                        Text("DEUTSCH 🇩🇪", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.setAppLanguage(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (appLanguage == AppLanguage.ENGLISH) InfraGreenPrimary.copy(alpha = 0.2f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (appLanguage == AppLanguage.ENGLISH) InfraGreenPrimary else InfraGreenBorder)
                    ) {
                        Text("ENGLISH 🇬🇧", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 11. SYSTEM-BERECHTIGUNGEN (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "System-Berechtigungen",
                icon = Icons.Default.LocationOn,
                initialExpanded = false,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 280.dp,
                expandedMaxHeight = 450.dp,
                testTag = "permissions_card"
            ) {
                // GPS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("GPS-Standort (Karten & Radar)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.5.sp)
                        Text(if (hasGpsPermission) "Berechtigung erteilt ✅" else "Berechtigung fehlt ❌", color = if (hasGpsPermission) InfraGreenPrimary else AlertInfraRed, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Button(
                        onClick = { gpsLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
                        enabled = !hasGpsPermission,
                        colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                    ) {
                        Text("FREIGEBEN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }

                // Mikrofon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Mikrofon (EVP Rekorder & Kommunikator)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.5.sp)
                        Text(if (hasMicPermission) "Berechtigung erteilt ✅" else "Berechtigung fehlt ❌", color = if (hasMicPermission) InfraGreenPrimary else AlertInfraRed, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Button(
                        onClick = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                        enabled = !hasMicPermission,
                        colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                    ) {
                        Text("FREIGEBEN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }

                // Kamera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Kamera (AR-Sucher & Spektral-Filter)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.5.sp)
                        Text(if (hasCameraPermission) "Berechtigung erteilt ✅" else "Berechtigung fehlt ❌", color = if (hasCameraPermission) InfraGreenPrimary else AlertInfraRed, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Button(
                        onClick = { cameraLauncher.launch(Manifest.permission.CAMERA) },
                        enabled = !hasCameraPermission,
                        colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                    ) {
                        Text("FREIGEBEN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }

                // Benachrichtigungen
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Benachrichtigungen (24/7 Radar)", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.5.sp)
                            Text(if (hasNotifPermission) "Berechtigung erteilt ✅" else "Berechtigung fehlt ❌", color = if (hasNotifPermission) InfraGreenPrimary else AlertInfraRed, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                        }
                        Button(
                            onClick = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            enabled = !hasNotifPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                        ) {
                            Text("FREIGEBEN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }

            // 12. DATENBANK & SYSTEM RESET (MINIMIERBAR & SCROLLBAR)
            CollapsibleHudWindow(
                title = "System Information & Reset",
                icon = Icons.Default.Info,
                initialExpanded = false,
                forceExpandedState = globalWindowState,
                isScrollable = true,
                defaultMaxHeight = 240.dp,
                expandedMaxHeight = 400.dp
            ) {
                Text(
                    text = "Geister-Detektor Pro v1.6\nInfra-Grün HUD & Spektral-Scanner Engine\nEntschärfter Scanner & Befreiungs-Protokoll\nOffline & Gemini AI Spirit Box Protokoll",
                    style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                )
                Button(
                    onClick = { showClearConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, AlertInfraRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("clear_all_ghosts_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = AlertInfraRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ALLE FUNDE LÖSCHEN", color = AlertInfraRed, style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllGhosts()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed)
                ) {
                    Text("BESTÄTIGEN", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("ABBRECHEN", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                }
            },
            containerColor = InfraGreenSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, InfraGreenBorder, RoundedCornerShape(12.dp)),
            title = {
                Text(
                    text = "ALLE FUNDE LÖSCHEN?",
                    style = MaterialTheme.typography.titleMedium.copy(color = AlertInfraRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Möchtest du wirklich die gesamte Datenbank leeren? Diese Aktion kann nicht rückgängig gemacht werden.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                )
            }
        )
    }

    // Change Security PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            confirmButton = {},
            dismissButton = {},
            containerColor = InfraGreenSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, InfraGreenBorder, RoundedCornerShape(12.dp)),
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = InfraGreenPrimary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SICHERHEITS-PIN ÄNDERN",
                            style = MaterialTheme.typography.titleMedium.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "Geben Sie Ihre aktuelle PIN ein und wählen Sie eine neue 4-stellige Zahlen-PIN.",
                        style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    )
                    OutlinedTextField(
                        value = currentPinInput,
                        onValueChange = { if (it.length <= 4) currentPinInput = it },
                        label = { Text("Aktuelle PIN", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InfraGreenPrimary,
                            unfocusedBorderColor = InfraGreenBorder,
                            focusedTextColor = InfraGreenTextPrimary,
                            unfocusedTextColor = InfraGreenTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("current_pin_input")
                    )
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4) newPinInput = it },
                        label = { Text("Neue 4-stellige PIN", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InfraGreenPrimary,
                            unfocusedBorderColor = InfraGreenBorder,
                            focusedTextColor = InfraGreenTextPrimary,
                            unfocusedTextColor = InfraGreenTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                    )
                    if (pinDialogError.isNotEmpty()) {
                        Text(
                            text = pinDialogError,
                            style = MaterialTheme.typography.bodySmall.copy(color = AlertInfraRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                    }
                    if (pinDialogSuccess.isNotEmpty()) {
                        Text(
                            text = pinDialogSuccess,
                            style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showChangePinDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("ABBRECHEN", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newPinInput.length != 4 || !newPinInput.all { it.isDigit() }) {
                                    pinDialogError = "Neue PIN muss genau 4 Zahlen enthalten!"
                                    pinDialogSuccess = ""
                                } else {
                                    val success = viewModel.changePin(currentPinInput, newPinInput)
                                    if (success) {
                                        pinDialogSuccess = "✅ PIN erfolgreich geändert!"
                                        pinDialogError = ""
                                        currentPinInput = ""
                                        newPinInput = ""
                                    } else {
                                        pinDialogError = "❌ Falsche aktuelle PIN!"
                                        pinDialogSuccess = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                        ) {
                            Text("SPEICHERN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }
}
