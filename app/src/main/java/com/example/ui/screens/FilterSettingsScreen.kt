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
import androidx.compose.material.icons.filled.Palette
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
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.viewmodel.GhostViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FilterSettingsScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val appThemeColor by viewModel.appThemeColor.collectAsStateWithLifecycle()
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
                                        color = InfraGreenTextPrimaryVariant,
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
                                    color = InfraGreenTextPrimaryVariant,
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
                                    color = InfraGreenTextPrimaryVariant,
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

            // Theme Color Selection Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = InfraGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("THEME-FARBE", style = MaterialTheme.typography.titleMedium.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        val colors = listOf("GREEN" to Color(0xFF00FF66), "RED" to Color(0xFFFF2244), "CYAN" to Color(0xFF00E5FF), "PURPLE" to Color(0xFFBB33FF))
                        colors.forEach { (name, colorValue) ->
                            val isSelected = appThemeColor == name
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(colorValue)
                                    .border(2.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(20.dp))
                                    .clickable { viewModel.setAppThemeColor(name) }
                            )
                        }
                    }
                }
            }

            // Language Selection Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = InfraGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SPRACHE / LANGUAGE", style = MaterialTheme.typography.titleMedium.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.setAppLanguage(AppLanguage.GERMAN) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (appLanguage == AppLanguage.GERMAN) InfraGreenPrimary.copy(alpha = 0.2f) else Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (appLanguage == AppLanguage.GERMAN) InfraGreenPrimary else InfraGreenBorder)
                        ) {
                            Text("DEUTSCH 🇩🇪", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        OutlinedButton(
                            onClick = { viewModel.setAppLanguage(AppLanguage.ENGLISH) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (appLanguage == AppLanguage.ENGLISH) InfraGreenPrimary.copy(alpha = 0.2f) else Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (appLanguage == AppLanguage.ENGLISH) InfraGreenPrimary else InfraGreenBorder)
                        ) {
                            Text("ENGLISH 🇬🇧", color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Security PIN / Lock Settings Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = InfraGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SICHERHEIT & PIN-SCHUTZ", style = MaterialTheme.typography.titleMedium.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("App-Sperre aktivieren", style = MaterialTheme.typography.bodyMedium.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace))
                            Text(if (isSecurityEnabled) "PIN-Schutz ist aktiv" else "Ohne PIN aufrufbar", style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 11.sp))
                        }
                        Switch(
                            checked = isSecurityEnabled,
                            onCheckedChange = { viewModel.setSecurityEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = InfraGreenPrimary)
                        )
                    }
                    if (isSecurityEnabled) {
                        Button(
                            onClick = { showChangePinDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = InfraGreenPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PIN ÄNDERN", color = InfraGreenPrimary, style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace))
                        }
                    }
                }
            }

            // System Information & Clear All Ghost Data Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = InfraGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SYSTEM INFORMATION", style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace))
                    }
                    Text(
                        text = "Geister-Detektor Pro v1.3a\nInfra-Grün HUD & Spektral-Scanner Engine\nOffline & Gemini AI Spirit Box Protokoll",
                        style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                    )
                    Button(
                        onClick = { showClearConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlertInfraRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = AlertInfraRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ALLE FUNDE LÖSCHEN", color = AlertInfraRed, style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Clear All Confirmation Dialog
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
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AlertInfraRed,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Möchtest du wirklich die gesamte Datenbank leeren? Diese Aktion kann nicht rückgängig gemacht werden.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = InfraGreenTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
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
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
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
