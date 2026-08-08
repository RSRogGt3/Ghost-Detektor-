package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.lifecycle.viewModelScope
import com.example.ui.components.FilterMode
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextMuted
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.viewmodel.GhostViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FilterSettingsScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val currentFilterMode by viewModel.currentFilterMode.collectAsStateWithLifecycle()
    val filterIntensity by viewModel.filterIntensity.collectAsStateWithLifecycle()
    val showCrtOverlay by viewModel.showCrtOverlay.collectAsStateWithLifecycle()
    val isCameraEnabled by viewModel.isCameraBackgroundEnabled.collectAsStateWithLifecycle()
    val audioFeedbackEnabled by viewModel.audioFeedbackEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val vibrationIntensity by viewModel.vibrationIntensity.collectAsStateWithLifecycle()
    val isAutoDestroyEnabled by viewModel.isAutoDestroyEnabled.collectAsStateWithLifecycle()
    val backgroundScan247Enabled by viewModel.backgroundScan247Enabled.collectAsStateWithLifecycle()
    val isBatterySaverEnabled by viewModel.isBatterySaverEnabled.collectAsStateWithLifecycle()
    val isBatterySaverThrottling by viewModel.isBatterySaverThrottling.collectAsStateWithLifecycle()
    val ttsPitch by viewModel.spiritTtsManager.pitch.collectAsStateWithLifecycle()
    val ttsRate by viewModel.spiritTtsManager.speechRate.collectAsStateWithLifecycle()

    val isSecurityEnabled by viewModel.isSecurityEnabled.collectAsStateWithLifecycle()
    val autoLockOnBackground by viewModel.autoLockOnBackground.collectAsStateWithLifecycle()
    val userPin by viewModel.userPin.collectAsStateWithLifecycle()
    val recoveryEmail by viewModel.recoveryEmail.collectAsStateWithLifecycle()

    var showChangePinDialog by remember { mutableStateOf(false) }
    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var pinDialogError by remember { mutableStateOf("") }
    var pinDialogSuccess by remember { mutableStateOf("") }
    var editingRecoveryEmail by remember(recoveryEmail) { mutableStateOf(recoveryEmail) }

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            viewModel.microphoneAnalyzer.startListening(viewModel.viewModelScope)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .testTag("filter_settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
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
                    tint = InfraGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = UiStrings.getSettingsHeader(appLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InfraGreenPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // App Security & User Access Protection Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("app_security_settings_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = InfraGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "SICHERHEIT & ZUGRIFFSSCHUTZ",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "PIN-Sperre & Schutz vor Fremdzugriff",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextMuted,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSecurityEnabled) InfraGreenPrimary.copy(alpha = 0.2f) else AlertInfraRed.copy(alpha = 0.2f))
                                .border(1.dp, if (isSecurityEnabled) InfraGreenPrimary else AlertInfraRed, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isSecurityEnabled) "🔒 AKTIV" else "🔓 INAKTIV",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSecurityEnabled) InfraGreenPrimary else AlertInfraRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // 1. PIN Security Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PIN-SPERRE AKTIVIEREN",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "App erfordert PIN-Eingabe beim Start",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InfraGreenTextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Switch(
                            checked = isSecurityEnabled,
                            onCheckedChange = { viewModel.setSecurityEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = InfraGreenPrimary,
                                checkedTrackColor = InfraGreenPrimary.copy(alpha = 0.4f)
                            )
                        )
                    }

                    // 2. Auto-Lock on Background Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AUTOMATISCH SPERREN IM HINTERGRUND",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Sperrt die App sofort, wenn sie minimiert wird",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InfraGreenTextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Switch(
                            checked = autoLockOnBackground,
                            onCheckedChange = { viewModel.setAutoLockOnBackground(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = InfraGreenPrimary,
                                checkedTrackColor = InfraGreenPrimary.copy(alpha = 0.4f)
                            )
                        )
                    }

                    // 3. Recovery Email Field
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "WIEDERHERSTELLUNGS-E-MAIL",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = InfraGreenTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
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

                    // Action Buttons: Change PIN, Reset PIN & Lock App Now
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = InfraGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PIN ÄNDERN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Button(
                                onClick = { viewModel.resetPinSetup() },
                                colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = InfraGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PIN RESETTEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.lockApp() },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AlertInfraRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AlertInfraRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "JETZT SPERREN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AlertInfraRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Background Operation & Battery Saver Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("background_battery_saver_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatterySaver,
                                contentDescription = null,
                                tint = if (isBatterySaverThrottling) Color(0xFFFFCC00) else InfraGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HINTERGRUNDBETRIEB & AKKUSPARER",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        if (isBatterySaverThrottling) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFCC00).copy(alpha = 0.2f))
                                    .border(1.dp, Color(0xFFFFCC00), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "⚡ DROSSELUNG AKTIV",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFFCC00),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }

                    // 1. 24/7 Background Scan Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "24/7 HINTERGRUND-SCANNEN",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = if (backgroundScan247Enabled) "Radar-Scans laufen weiter, auch wenn die App minimiert ist" else "Scans pausieren beim Minimieren der App",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InfraGreenTextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Switch(
                            checked = backgroundScan247Enabled,
                            onCheckedChange = { viewModel.toggleBackgroundScan247() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = InfraGreenPrimary,
                                uncheckedThumbColor = InfraGreenTextMuted,
                                uncheckedTrackColor = InfraGreenSurfaceVariant
                            )
                        )
                    }

                    // 2. Battery Saver Mode Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = UiStrings.getBatterySaverTitle(appLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = UiStrings.getBatterySaverDesc(appLanguage, isBatterySaverThrottling),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isBatterySaverThrottling) Color(0xFFFFCC00) else InfraGreenTextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Switch(
                            checked = isBatterySaverEnabled,
                            onCheckedChange = { viewModel.toggleBatterySaverEnabled() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = InfraGreenPrimary,
                                uncheckedThumbColor = InfraGreenTextMuted,
                                uncheckedTrackColor = InfraGreenSurfaceVariant
                            ),
                            modifier = Modifier.testTag("settings_battery_saver_switch")
                        )
                    }
                }
            }

            // System Permissions Control Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("system_permissions_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = InfraGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEM-BERECHTIGUNGEN & SENSOR-STEUERUNG",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // 1. Microphone Permission
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (hasMicPermission) InfraGreenPrimary else AlertInfraRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MIKROFON-ZUGRIFF (AUDIO-EVP)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = InfraGreenTextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = if (hasMicPermission) "Aktiv: Echtzeit Spirit Box Audio-Eingabe" else "Inaktiv: Benötigt für Sprach-Eingabe",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextMuted,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        if (hasMicPermission) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(InfraGreenPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, InfraGreenPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = InfraGreenPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "FREIGEGEBEN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = InfraGreenPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "FREIGEBEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // 2. Camera Permission
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = if (hasCameraPermission) InfraGreenPrimary else AlertInfraRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "KAMERA-ZUGRIFF (NACHTSICHT)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = InfraGreenTextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = if (hasCameraPermission) "Aktiv: Kamerafeed & Taschenlampe" else "Inaktiv: Benötigt für Video-Scan",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextMuted,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        if (hasCameraPermission) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(InfraGreenPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, InfraGreenPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = InfraGreenPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "FREIGEGEBEN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = InfraGreenPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "FREIGEBEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // 3. Location Permission
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (hasLocationPermission) InfraGreenPrimary else AlertInfraRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "STANDORT-ZUGRIFF (GPS)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = InfraGreenTextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = if (hasLocationPermission) "Aktiv: Präzise Koordinaten für Funde" else "Inaktiv: Koordinaten-Verortung",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextMuted,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        if (hasLocationPermission) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(InfraGreenPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, InfraGreenPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = InfraGreenPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "FREIGEGEBEN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = InfraGreenPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                                colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "FREIGEBEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Language Selection Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("language_selector_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = UiStrings.getLanguageCardTitle(appLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.entries.chunked(4).forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chunk.forEach { lang ->
                                    val isSelected = lang == appLanguage
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) InfraGreenPrimary.copy(alpha = 0.25f) else InfraGreenSurfaceVariant)
                                            .border(1.dp, if (isSelected) InfraGreenPrimary else InfraGreenBorder, RoundedCornerShape(8.dp))
                                            .clickable { viewModel.setAppLanguage(lang) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = lang.flag,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = lang.displayName,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSelected) InfraGreenPrimary else InfraGreenTextPrimary,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 10.sp
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Spectral Filter Choice Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = UiStrings.getActiveFilterTitle(appLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace)
                    )

                    FilterMode.values().forEach { mode ->
                        val isSelected = mode == currentFilterMode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) mode.primaryColor.copy(alpha = 0.2f) else InfraGreenSurfaceVariant)
                                .border(1.dp, if (isSelected) mode.primaryColor else InfraGreenBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setFilterMode(mode) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (isSelected) mode.primaryColor else InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            if (isSelected) {
                                Text(
                                    text = "AKTIV",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = mode.primaryColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Spectral Filter Intensity Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("filter_intensity_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FILTER INTENSITÄT & KONTRAST",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = currentFilterMode.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${(filterIntensity * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = currentFilterMode.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Stärke und Spektralkontrast des Infrarot-Nachtsicht-Kamera-Overlays anpassen",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

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
            }

            // HUD Overlays & Audio Toggles
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "VISUELLE & AUDIO OPTIONEN:",
                        style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = UiStrings.getCameraFeedLabel(appLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = UiStrings.getCameraFeedDesc(appLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )
                        }

                        Switch(
                            checked = isCameraEnabled,
                            onCheckedChange = { viewModel.toggleCameraBackground() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = InfraGreenPrimary,
                                uncheckedThumbColor = InfraGreenTextMuted,
                                uncheckedTrackColor = InfraGreenSurfaceVariant
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = UiStrings.getCrtOverlayLabel(appLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Nachtsicht-Linienraster im Display anzeigen",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )
                        }

                        Switch(
                            checked = showCrtOverlay,
                            onCheckedChange = { viewModel.toggleCrtOverlay() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = InfraGreenPrimary,
                                uncheckedThumbColor = InfraGreenTextMuted,
                                uncheckedTrackColor = InfraGreenSurfaceVariant
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AUDIO GEIGER & RADAR TON",
                                style = MaterialTheme.typography.bodyMedium.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Akustische Signale bei EMF-Ausschlag",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )
                        }

                        Switch(
                            checked = audioFeedbackEnabled,
                            onCheckedChange = { viewModel.toggleAudioFeedback() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = InfraGreenPrimary,
                                uncheckedThumbColor = InfraGreenTextMuted,
                                uncheckedTrackColor = InfraGreenSurfaceVariant
                            )
                        )
                    }

                    // Vibration Toggle & Intensity Control
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "HAPTISCHE VIBRATION",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Vibration bei EMF-Ausschlägen & Entitäts-Befreiung",
                                    style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                                )
                            }

                            Switch(
                                checked = vibrationEnabled,
                                onCheckedChange = { viewModel.toggleVibration() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = InfraGreenPrimary,
                                    uncheckedThumbColor = InfraGreenTextMuted,
                                    uncheckedTrackColor = InfraGreenSurfaceVariant
                                ),
                                modifier = Modifier.testTag("vibration_switch")
                            )
                        }

                        if (vibrationEnabled) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "VIBRATIONS-INTENSITÄT:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = currentFilterMode.primaryColor,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "${(vibrationIntensity * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = currentFilterMode.primaryColor,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Slider(
                                    value = vibrationIntensity,
                                    onValueChange = { viewModel.setVibrationIntensity(it) },
                                    valueRange = 0.0f..1.00f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = currentFilterMode.primaryColor,
                                        activeTrackColor = currentFilterMode.primaryColor,
                                        inactiveTrackColor = currentFilterMode.primaryColor.copy(alpha = 0.25f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("vibration_intensity_slider")
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AUTOMATISCHE VERNICHTUNG",
                                style = MaterialTheme.typography.bodyMedium.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Geister auf dem Radar automatisch befreien",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )
                        }

                        Switch(
                            checked = isAutoDestroyEnabled,
                            onCheckedChange = { viewModel.toggleAutoDestroy() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = InfraGreenPrimary,
                                uncheckedThumbColor = InfraGreenTextMuted,
                                uncheckedTrackColor = InfraGreenSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Text-to-Speech Engine Controls
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = InfraGreenPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SPIRIT BOX TTS SCHNITTSTELLE:",
                            style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace)
                        )
                    }

                    // Pitch
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tonhöhe (Pitch):",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = String.format("%.2fx", ttsPitch),
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = ttsPitch,
                            onValueChange = { viewModel.spiritTtsManager.setPitch(it) },
                            valueRange = 0.3f..1.2f,
                            colors = SliderDefaults.colors(
                                thumbColor = InfraGreenPrimary,
                                activeTrackColor = InfraGreenPrimary,
                                inactiveTrackColor = InfraGreenBorder
                            )
                        )
                    }

                    // Rate
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sprechgeschwindigkeit (Rate):",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = String.format("%.2fx", ttsRate),
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = ttsRate,
                            onValueChange = { viewModel.spiritTtsManager.setSpeechRate(it) },
                            valueRange = 0.4f..1.4f,
                            colors = SliderDefaults.colors(
                                thumbColor = InfraGreenPrimary,
                                activeTrackColor = InfraGreenPrimary,
                                inactiveTrackColor = InfraGreenBorder
                            )
                        )
                    }
                }
            }

            // App info & Reset Database
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = InfraGreenPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYSTEM INFORMATION",
                            style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace)
                        )
                    }

                    Text(
                        text = "Geister-Detektor Pro v1.2\nInfra-Grün HUD & Spektral-Scanner Engine\nOffline & Gemini AI Spirit Box Protokoll",
                        style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                    )

                    Button(
                        onClick = { showClearConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlertInfraRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = AlertInfraRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ALLE FUNDE LÖSCHEN",
                            color = AlertInfraRed,
                            style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace)
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showClearConfirmDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ALLE FUNDE LÖSCHEN?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AlertInfraRed,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Möchtest du wirklich die gesamte Datenbank leeren? Diese Aktion kann nicht rückgängig gemacht werden.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = InfraGreenTextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showClearConfirmDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("ABBRECHEN", color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.clearAllGhosts()
                                showClearConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed)
                        ) {
                            Text("BESTÄTIGEN", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
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
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = InfraGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SICHERHEITS-PIN ÄNDERN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = "Geben Sie Ihre aktuelle PIN ein und wählen Sie eine neue 4-stellige Zahlen-PIN.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    OutlinedTextField(
                        value = currentPinInput,
                        onValueChange = { if (it.length <= 4) currentPinInput = it },
                        label = { Text("Aktuelle PIN", color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
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
                        label = { Text("Neue 4-stellige PIN", color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
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
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AlertInfraRed,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (pinDialogSuccess.isNotEmpty()) {
                        Text(
                            text = pinDialogSuccess,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
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
                            Text("ABBRECHEN", color = InfraGreenTextMuted, fontFamily = FontFamily.Monospace)
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
