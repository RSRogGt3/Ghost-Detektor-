package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GhostDetectionEntity
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.theme.ThermalAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GhostDetailDialog(
    ghost: GhostDetectionEntity,
    onDismiss: () -> Unit,
    onToggleFavorite: (GhostDetectionEntity) -> Unit,
    onDelete: (GhostDetectionEntity) -> Unit,
    onFree: (GhostDetectionEntity) -> Unit,
    onUpdateNotes: (GhostDetectionEntity, String) -> Unit,
    onSpeakText: (String) -> Unit
) {
    var isEditingNotes by remember { mutableStateOf(false) }
    var notesText by remember(ghost) { mutableStateOf(ghost.notes) }

    val formattedDate = remember(ghost.timestamp) {
        val sdf = SimpleDateFormat("dd.MM.yyyy - HH:mm:ss 'Uhr'", Locale.GERMANY)
        sdf.format(Date(ghost.timestamp))
    }

    val parsedColor = remember(ghost.spectralColorHex) {
        try {
            Color(android.graphics.Color.parseColor(ghost.spectralColorHex))
        } catch (_: Exception) {
            InfraGreenPrimary
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, InfraGreenBorder, RoundedCornerShape(16.dp))
                .testTag("ghost_detail_dialog"),
            color = InfraGreenSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(10.dp)
                                .height(22.dp)
                                .background(parsedColor, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ghost.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InfraGreenTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row {
                        IconButton(onClick = { onToggleFavorite(ghost) }) {
                            Icon(
                                imageVector = if (ghost.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorit",
                                tint = if (ghost.isFavorite) AlertInfraRed else InfraGreenTextPrimaryVariant
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Schließen",
                                tint = InfraGreenTextPrimaryVariant
                            )
                        }
                    }
                }

                // Grid Stats Cards
                Card(
                    colors = CardDefaults.cardColors(containerColor = InfraGreenSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder(enabled = true),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ENTITÄTS-TYP:",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = ghost.type.uppercase(),
                                style = MaterialTheme.typography.bodySmall.copy(color = parsedColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EMF FELDSTÄRKE:",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = "${ghost.emfLevel} mG",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EVP FREQUENZ:",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = "${ghost.frequencyKhz} kHz",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "GEFAHRENSTUFE:",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = "STUFE ${ghost.dangerLevel} / 5",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (ghost.dangerLevel >= 4) AlertInfraRed else ThermalAmber,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "FUNDORT:",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = ghost.locationName,
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ZEITPUNKT:",
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )
                        }
                    }
                }

                // Spirit Box Recorded Words
                if (ghost.lastWords.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(InfraGreenSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .border(1.dp, InfraGreenBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EVP BOTSCHAFT / TRANSMISSION:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace
                                )
                            )

                            IconButton(
                                onClick = { onSpeakText(ghost.lastWords) },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "EVP Vorlesen",
                                    tint = InfraGreenPrimary
                                )
                            }
                        }

                        Text(
                            text = "\"${ghost.lastWords}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Dedicated Spectral Meaning & Lore Analysis Card (Bedeutung im Verlauf)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (ghost.dangerLevel >= 4) Color(0xFF230009) else InfraGreenSurfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (ghost.dangerLevel >= 4) AlertInfraRed else InfraGreenBorder
                    ),
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
                                text = "BEDEUTUNG & DEMONOLOGISCHE ANALYSE:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (ghost.dangerLevel >= 4) AlertInfraRed else InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            IconButton(
                                onClick = { onSpeakText(ghost.notes) },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Bedeutung Vorlesen",
                                    tint = if (ghost.dangerLevel >= 4) AlertInfraRed else InfraGreenPrimary
                                )
                            }
                        }

                        Text(
                            text = if (ghost.notes.isNotBlank()) ghost.notes else "Keine spezifische Bedeutung im Verlauf protokolliert.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                // Field Notes Section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ERMITTLER-NOTIZEN:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        IconButton(onClick = {
                            if (isEditingNotes) {
                                onUpdateNotes(ghost, notesText)
                            }
                            isEditingNotes = !isEditingNotes
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Bearbeiten",
                                tint = if (isEditingNotes) InfraGreenPrimary else InfraGreenTextPrimaryVariant
                            )
                        }
                    }

                    if (isEditingNotes) {
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = InfraGreenPrimary,
                                unfocusedBorderColor = InfraGreenBorder,
                                focusedTextColor = InfraGreenTextPrimary,
                                unfocusedTextColor = InfraGreenTextPrimary
                            )
                        )
                    } else {
                        Text(
                            text = if (notesText.isNotBlank()) notesText else "Keine Notizen vorhanden.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = InfraGreenTextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Action: Free Ghost
                Button(
                    onClick = { onFree(ghost) },
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = InfraGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ENTITÄT BEFREIEN",
                        color = InfraGreenPrimary,
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Delete Action
                Button(
                    onClick = { onDelete(ghost) },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertInfraRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = AlertInfraRed
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "EINTRAG LÖSCHEN",
                        color = AlertInfraRed,
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            }
        }
    }
}
