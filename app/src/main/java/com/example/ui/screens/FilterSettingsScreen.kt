package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val showCrtOverlay by viewModel.showCrtOverlay.collectAsStateWithLifecycle()
    val isCameraEnabled by viewModel.isCameraBackgroundEnabled.collectAsStateWithLifecycle()
    val audioFeedbackEnabled by viewModel.audioFeedbackEnabled.collectAsStateWithLifecycle()
    val ttsPitch by viewModel.spiritTtsManager.pitch.collectAsStateWithLifecycle()
    val ttsRate by viewModel.spiritTtsManager.speechRate.collectAsStateWithLifecycle()

    var showClearConfirmDialog by remember { mutableStateOf(false) }

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.values().forEach { lang ->
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
                                        )
                                    )
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
                        text = "Geister-Detektor Pro v1.0\nInfra-Grün HUD & Spektral-Scanner Engine\nOffline & Gemini AI Spirit Box Protokoll",
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
}
