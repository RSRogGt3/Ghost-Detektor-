package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MagnetLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Magnetfeld-Analyse, Notiz-System & TV/PC-Monitor Schild-Schutz Panel.
 * Ermöglicht das Anzeigen, Protokollieren und gezielte Unterbinden von Magnetfeld-Angriffen
 * sowie automatisches Filtern & Hintergrund-Scannen.
 */
@Composable
fun MagnetFieldAndShieldCard(
    emfLevel: Float,
    isMagnetShieldActive: Boolean,
    magnetLogNotes: List<MagnetLogEntry>,
    autoFilterRotationEnabled: Boolean,
    autoCaptureLiberateEnabled: Boolean,
    backgroundScan247Enabled: Boolean,
    filterMode: FilterMode,
    onToggleMagnetShield: () -> Unit,
    onAddMagnetNote: (noteText: String, sourceTag: String) -> Unit,
    onClearMagnetNotes: () -> Unit,
    onToggleAutoFilterRotation: () -> Unit,
    onToggleAutoCaptureLiberate: () -> Unit,
    onToggleBackgroundScan247: () -> Unit,
    modifier: Modifier = Modifier,
    batterySaverEnabled: Boolean = true,
    isBatterySaverThrottling: Boolean = false,
    appLanguage: com.example.ui.i18n.AppLanguage = com.example.ui.i18n.AppLanguage.GERMAN,
    onToggleBatterySaver: () -> Unit = {}
) {
    var noteText by remember { mutableStateOf("") }
    var selectedSourceTag by remember { mutableStateOf("Fernseher / PC-Monitor Störung") }
    var isMinimized by remember { mutableStateOf(true) }

    val primaryColor = filterMode.primaryColor
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    val isElevated = emfLevel > 6.0f
    val isCritical = emfLevel > 8.0f

    val sourceOptions = listOf(
        "Fernseher / PC-Monitor Störung",
        "Poltergeist Magnet-Angriff",
        "Elektronik EMI-Frequenz",
        "Ätherische Raumspitze"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF09120C)),
        border = BorderStroke(1.5.dp, if (isMagnetShieldActive) Color(0xFF00FFCC) else primaryColor.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("magnet_field_shield_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Magnetometer Status, Active Shield Indicator & Minimization Toggle
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
                        imageVector = Icons.Default.CompassCalibration,
                        contentDescription = null,
                        tint = if (isMagnetShieldActive) Color(0xFF00FFCC) else primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "MAGNETFELD-ANALYSE & SCHILD-SCHUTZ",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isMagnetShieldActive) Color(0xFF00FFCC) else primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "LIVE-MAGNETFLUSS: ${String.format(Locale.US, "%.1f", emfLevel * 10.2f)} µT (${String.format(Locale.US, "%.1f", emfLevel)} mG)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isCritical) Color(0xFFFF3333) else if (isElevated) Color(0xFFFFCC00) else Color(0xFF00FFCC),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isMagnetShieldActive) Color(0x3300FFCC) else Color(0x33FF3333))
                            .border(1.dp, if (isMagnetShieldActive) Color(0xFF00FFCC) else Color(0xFFFF3333), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isMagnetShieldActive) "🛡️ AN" else "⚠️ AUS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isMagnetShieldActive) Color(0xFF00FFCC) else Color(0xFFFF3333),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Card Minimization Toggle Button
                    IconButton(
                        onClick = { isMinimized = !isMinimized },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isMinimized) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isMinimized) "Karte ausklappen" else "Karte minimieren",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (!isMinimized) {

            // TV & PC Monitor Specific Protection Shield Toggle Button
            Button(
                onClick = onToggleMagnetShield,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("toggle_magnet_shield_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMagnetShieldActive) Color(0xFF00FFCC) else Color(0xFF221111),
                    contentColor = if (isMagnetShieldActive) Color.Black else Color(0xFFFF5555)
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (isMagnetShieldActive) Color(0xFF00FFCC) else Color(0xFFFF5555)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (isMagnetShieldActive) Icons.Default.Shield else Icons.Default.Warning,
                    contentDescription = "TV & PC Monitor Magnetfeld Schild",
                    tint = if (isMagnetShieldActive) Color.Black else Color(0xFFFF5555)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMagnetShieldActive) "🛡️ TV & MONITOR SCHILD: AKTIV (ANGRIFFE ABGEFANGEN)" else "⚠️ TV & MONITOR SCHILD: DEAKTIVIERT (ANGRIFFE UNTERBINDEN)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                )
            }

            // Note Taking Input Section for Recording Magnetfield Anomalies & TV/PC Interference
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1B12))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MAGNETFELD-NOTIZ PROTOKOLLIEREN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Tag Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    sourceOptions.take(2).forEach { tag ->
                        val isSelected = selectedSourceTag == tag
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) primaryColor else Color(0xFF16251A))
                                .border(1.dp, if (isSelected) primaryColor else Color(0xFF223828), RoundedCornerShape(6.dp))
                                .clickable { selectedSourceTag = tag }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.Black else Color.LightGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // Text Field Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = {
                            Text(
                                text = "z.B. Fernseher-Störung 8.5µT im Wohnzimmer...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("magnet_note_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color(0xFF2B4432),
                            focusedContainerColor = Color(0xFF070E09),
                            unfocusedContainerColor = Color(0xFF070E09),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Button(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                onAddMagnetNote(noteText, selectedSourceTag)
                                noteText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_magnet_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Speichern",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Scrollable List of Magnet Log Notes
                if (magnetLogNotes.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PROTOKOLLIERTE MAGNETFELD-NOTIZEN (${magnetLogNotes.size}):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "LÖSCHEN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFF5555),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.clickable { onClearMagnetNotes() }
                            )
                        }

                        magnetLogNotes.take(4).forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF08100A))
                                    .border(1.dp, Color(0xFF1E3324), RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "[${timeFormat.format(Date(entry.timestamp))}] ${entry.sourceTag}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = primaryColor,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (entry.isShielded) {
                                            Text(
                                                text = "🛡️ SCHILD",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF00FFCC),
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 8.5.sp
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = entry.noteText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.LightGray,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", entry.emfValue)} mG",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (entry.emfValue > 6.5f) Color(0xFFFFCC00) else primaryColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Automations Suite: Auto-Filter 5 Min Rotation, Auto-Capture & Liberation, 24/7 Background Mode
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A150D))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "AUTOMATISIERUNGS- & HINTERGRUND-SYSTEME",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF00FFCC),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                )

                // 1. Auto Infra-Filter Rotation Every 5 Minutes
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
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (autoFilterRotationEnabled) Color(0xFF00FFCC) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AUTO-FILTER ZYKLUS (ALLE 5 MIN)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = if (autoFilterRotationEnabled) "Aktiv: Rotiert alle Spektralfilter automatisch" else "Inaktiv: Manueller Filterwechsel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = autoFilterRotationEnabled,
                        onCheckedChange = { onToggleAutoFilterRotation() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00FFCC),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E2E22)
                        ),
                        modifier = Modifier.testTag("auto_filter_rotation_switch")
                    )
                }

                // 2. Auto-Capture & Auto-Liberation
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (autoCaptureLiberateEnabled) Color(0xFF00FFCC) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AUTO-FANG & BEFREIUNG",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = if (autoCaptureLiberateEnabled) "Aktiv: Harmonisiert Geister & Anomalien automatisch" else "Inaktiv: Manuelles Fangen & Befreien",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = autoCaptureLiberateEnabled,
                        onCheckedChange = { onToggleAutoCaptureLiberate() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00FFCC),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E2E22)
                        ),
                        modifier = Modifier.testTag("auto_capture_liberate_switch")
                    )
                }

                // 3. 24/7 Background Scanning
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
                            imageVector = Icons.Default.PhonelinkSetup,
                            contentDescription = null,
                            tint = if (backgroundScan247Enabled) Color(0xFF00FFCC) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "24/7 HINTERGRUND-SCANNEN",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = if (backgroundScan247Enabled) "Aktiv: Läuft dauerhaft im Hintergrund weiter" else "Inaktiv: Nur bei geöffneter App",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = backgroundScan247Enabled,
                        onCheckedChange = { onToggleBackgroundScan247() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00FFCC),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E2E22)
                        ),
                        modifier = Modifier.testTag("background_scan_247_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Batteriesparmodus für Hintergrundbetrieb
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
                            imageVector = Icons.Default.BatterySaver,
                            contentDescription = null,
                            tint = if (isBatterySaverThrottling) Color(0xFFFFCC00) else if (batterySaverEnabled) Color(0xFF00FFCC) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = com.example.ui.i18n.UiStrings.getBatterySaverTitle(appLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = com.example.ui.i18n.UiStrings.getBatterySaverDesc(appLanguage, isBatterySaverThrottling),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isBatterySaverThrottling) Color(0xFFFFCC00) else Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = batterySaverEnabled,
                        onCheckedChange = { onToggleBatterySaver() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF00FFCC),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E2E22)
                        ),
                        modifier = Modifier.testTag("battery_saver_switch")
                    )
                }
            }
            }
        }
    }
}
