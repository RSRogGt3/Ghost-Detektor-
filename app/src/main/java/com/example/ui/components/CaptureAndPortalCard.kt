package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.IconButton
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
import com.example.data.DimensionPlane
import com.example.data.SigilType

@Composable
fun CaptureAndPortalCard(
    activeRiftsCount: Int,
    capturedCount: Int,
    isClosingDimension: Boolean,
    isCapturingEntity: Boolean,
    primaryColor: Color,
    activeSigil: SigilType? = null,
    sigilTimerSeconds: Int = 0,
    activeDimension: DimensionPlane = DimensionPlane.MORTAL_PRIME,
    onCloseDimension: () -> Unit,
    onSpawnDimension: () -> Unit,
    onCaptureEntity: () -> Unit,
    onSpawnThreat: () -> Unit,
    onOpenSigilForge: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isMinimized by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090E11)),
        border = BorderStroke(1.5.dp, primaryColor.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("capture_and_portal_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Dimension & Siegel",
                        tint = primaryColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DIMENSIONEN & DÄMONEN-SIEGEL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Ebene: ${activeDimension.codeName} • ${activeDimension.frequencyHz} Hz",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { isMinimized = !isMinimized },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isMinimized) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = if (isMinimized) "Ausklappen" else "Minimieren",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (!isMinimized) {
                // Active Sigil Status Banner
                if (activeSigil != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1D17)),
                        border = BorderStroke(1.dp, Color(activeSigil.colorHex)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = activeSigil.symbol, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AKTIVES SIEGEL: ${activeSigil.title}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(activeSigil.colorHex),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "Dauer: ${sigilTimerSeconds}s verbleibend • ${activeSigil.purpose}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Main Interactive Sigil Forge Launch Button
                Button(
                    onClick = onOpenSigilForge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("open_sigil_forge_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Sigel-Schmiede",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔮 SIGEL-SCHMIEDE & DIMENSIONEN ÖFFNEN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    )
                }

                // 1. Dimension Portal Control Box
                BoxPortalControl(
                    activeRiftsCount = activeRiftsCount,
                    isClosingDimension = isClosingDimension,
                    primaryColor = primaryColor,
                    onCloseDimension = onCloseDimension,
                    onSpawnDimension = onSpawnDimension
                )

                // 2. Vampire & Demon Trap Box
                BoxTrapControl(
                    capturedCount = capturedCount,
                    isCapturingEntity = isCapturingEntity,
                    primaryColor = primaryColor,
                    onCaptureEntity = onCaptureEntity,
                    onSpawnThreat = onSpawnThreat
                )
            }
        }
    }
}

@Composable
private fun BoxPortalControl(
    activeRiftsCount: Int,
    isClosingDimension: Boolean,
    primaryColor: Color,
    onCloseDimension: () -> Unit,
    onSpawnDimension: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D1821))
            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🌀 DIMENSIONS-PORTAL VERSIEGELER",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF00E5FF),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = if (activeRiftsCount > 0) "⚠️ $activeRiftsCount RISS(E) AKTIV" else "STABIL",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (activeRiftsCount > 0) Color(0xFFFF3366) else Color(0xFF00FFCC),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            text = "Ortet interdimensionale Quanten-Risse & Raum-Zeit-Spalten. Schließt und versiegelt gefährliche Portale dauerhaft.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.LightGray,
                fontSize = 11.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCloseDimension,
                enabled = !isClosingDimension,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B0FF),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("close_dimension_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Versiegeln",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isClosingDimension) "VERSIEGELT..." else "PORTAL SCHLIESSEN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            OutlinedButton(
                onClick = onSpawnDimension,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("scan_dimension_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Scannen",
                    tint = Color(0xFF00E5FF)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ORTEN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}

@Composable
private fun BoxTrapControl(
    capturedCount: Int,
    isCapturingEntity: Boolean,
    primaryColor: Color,
    onCaptureEntity: () -> Unit,
    onSpawnThreat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1C0D18))
            .border(1.dp, Color(0xFFFF0055).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⚡ SPEKTRAL-FALLE & DÄMONEN-SIEGEL",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFFF0055),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "$capturedCount GEFANGEN",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFFFCC00),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            text = "Bannt Dämonen, Vampir-Entitäten und aggressive Geister direkt vom Radar/Kamera-Feed in den Spektral-Käfig.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.LightGray,
                fontSize = 11.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCaptureEntity,
                enabled = !isCapturingEntity,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF0055),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1.4f)
                    .testTag("capture_entity_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Fangen",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCapturingEntity) "BANNT..." else "FANGEN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            OutlinedButton(
                onClick = onSpawnThreat,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF0055)),
                border = BorderStroke(1.dp, Color(0xFFFF0055)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("spawn_threat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Dämon / Vampir orten",
                    tint = Color(0xFFFF0055)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ORTEN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}
