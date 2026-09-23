package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InfraLightColor
import com.example.sensor.CalibrationTelemetry

@Composable
fun RadarCalibrationControlCard(
    calibrationTelemetry: CalibrationTelemetry,
    filterMode: FilterMode,
    infraLightColor: InfraLightColor,
    onStartCalibration: () -> Unit,
    onToggleAutoCalibration: (Boolean) -> Unit,
    onResetCalibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val primaryCol = filterMode.primaryColor

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xDD061109)),
        border = CardDefaults.outlinedCardBorder(enabled = true),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("radar_calibration_control_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = primaryCol,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "RADAR- & SENSOR-KALIBRIERUNG",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = primaryCol,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = if (calibrationTelemetry.isCalibrating) "Kalibrierung läuft..." else "Tara: ${String.format(java.util.Locale.US, "%.1f", calibrationTelemetry.calibratedEmfBaseline)} mG Baseline",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = if (calibrationTelemetry.isCalibrating) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color(0xFF00FF66).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (calibrationTelemetry.isCalibrating) Color(0xFF00E5FF) else Color(0xFF00FF66)
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (calibrationTelemetry.isCalibrating) "MESSUNG..." else "TARA AKTIV",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (calibrationTelemetry.isCalibrating) Color(0xFF00E5FF) else Color(0xFF00FF66),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Einklappen" else "Ausklappen",
                            tint = primaryCol,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Quick status line & primary trigger button
            if (calibrationTelemetry.isCalibrating) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "● ${calibrationTelemetry.currentStepText}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00E5FF),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "${(calibrationTelemetry.calibrationProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00E5FF),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                    LinearProgressIndicator(
                        progress = { calibrationTelemetry.calibrationProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF00FFCC),
                        trackColor = Color(0xFF0B2418)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onStartCalibration,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("start_calibration_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryCol),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Adjust,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🎯 AUTO-KALIBRIERUNG (TARA)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onResetCalibration,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("reset_calibration_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryCol),
                        border = androidx.compose.foundation.BorderStroke(1.dp, primaryCol.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(
                            text = "RESET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Expanded Telemetry & Continuous Auto-Tracking Toggle
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = primaryCol.copy(alpha = 0.25f), thickness = 0.8.dp)

                    // Auto-Tracking Switch (Hintergrund-Driftfilter)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "KONTINUIERLICHE AUTO-NACHKALIBRIERUNG",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Gleicht stetiges Raumrauschen (Elektrogeräte/Leitungen) automatisch an",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontSize = 9.sp
                                )
                            )
                        }

                        Switch(
                            checked = calibrationTelemetry.isAutoCalibrationEnabled,
                            onCheckedChange = onToggleAutoCalibration,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = primaryCol,
                                uncheckedThumbColor = primaryCol.copy(alpha = 0.5f),
                                uncheckedTrackColor = Color(0xFF1A1A1A)
                            ),
                            modifier = Modifier.testTag("auto_calibration_switch")
                        )
                    }

                    // 4 Metric Telemetry Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Ambient Baseline Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.dp, primaryCol.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "BASELINE (TARA)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.LightGray,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.5.sp
                                    )
                                )
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f", calibrationTelemetry.calibratedEmfBaseline)} mG",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = primaryCol,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Net EMF Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "NETTO-SIGNAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.LightGray,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.5.sp
                                    )
                                )
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f", calibrationTelemetry.netEmf)} mG",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color(0xFF00FFCC),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Quality Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "SIGNAL-GÜTE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.LightGray,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.5.sp
                                    )
                                )
                                Text(
                                    text = "${calibrationTelemetry.calibrationQualityPercent}%",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color(0xFF00E5FF),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Explanation Note
                    Text(
                        text = "ℹ️ Die automatische Kalibrierung neutralisiert magnetische Störfelder der Umgebung und arretiert den Nullpunkt des Radars. Paranormale Spikes heben sich dadurch kristallklar ab.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray,
                            fontSize = 8.5.sp
                        )
                    )
                }
            }
        }
    }
}
