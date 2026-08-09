package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.theme.ThermalAmber

@Composable
fun EmfMeter(
    modifier: Modifier = Modifier,
    emfValue: Float, // 0.0 to 10.0 mG
    dangerLevel: Int, // 1 to 5
    frequencyKhz: Float, // e.g. 42.8
    initiallyMinimized: Boolean = false,
    isEmfSuppressed: Boolean = false,
    onToggleEmfSuppression: (() -> Unit)? = null,
    onNeutralizeEmf: (() -> Unit)? = null
) {
    val animatedEmf by animateFloatAsState(
        targetValue = emfValue,
        animationSpec = tween(durationMillis = 300),
        label = "emf_anim"
    )

    var isMinimized by remember { mutableStateOf(initiallyMinimized) }

    val fillRatio = (animatedEmf / 10f).coerceIn(0f, 1f)
    val statusColor = when {
        dangerLevel >= 4 || animatedEmf > 7.5f -> AlertInfraRed
        dangerLevel >= 3 || animatedEmf > 4.5f -> ThermalAmber
        else -> InfraGreenPrimary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(InfraGreenSurface)
            .border(1.dp, if (isEmfSuppressed) InfraGreenPrimary else InfraGreenBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .animateContentSize()
            .testTag("emf_meter_container")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            .width(8.dp)
                            .height(16.dp)
                            .background(statusColor, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EMF-FELDSTÄRKE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    if (isEmfSuppressed) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(InfraGreenPrimary.copy(alpha = 0.2f))
                                .border(0.5.dp, InfraGreenPrimary, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🛡️ GEDÄMPFT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = String.format("%.1f mG", animatedEmf),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = statusColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(
                        onClick = { isMinimized = !isMinimized },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isMinimized) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isMinimized) "Ausklappen" else "Minimieren",
                            tint = statusColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (!isMinimized) {
                // LED Segment Meter Bar (10 segments)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val numSegments = 10
                    for (i in 1..numSegments) {
                        val segmentThreshold = i / 10f
                        val isActive = fillRatio >= segmentThreshold
                        val segmentColor = when {
                            i >= 8 -> AlertInfraRed
                            i >= 5 -> ThermalAmber
                            else -> InfraGreenPrimary
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isActive) segmentColor else segmentColor.copy(alpha = 0.15f)
                                )
                        )
                    }
                }

                // Action Controls: Neutralize EMF & Toggle Scan Damping
                if (onNeutralizeEmf != null || onToggleEmfSuppression != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onNeutralizeEmf != null) {
                            Button(
                                onClick = onNeutralizeEmf,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("neutralize_emf_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (animatedEmf > 5.0f) AlertInfraRed else InfraGreenPrimary.copy(alpha = 0.2f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (animatedEmf > 5.0f) AlertInfraRed else InfraGreenPrimary
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (animatedEmf > 5.0f) Color.White else InfraGreenPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "⚡ VERNICHTEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (animatedEmf > 5.0f) Color.White else InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        if (onToggleEmfSuppression != null) {
                            Button(
                                onClick = onToggleEmfSuppression,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("toggle_emf_suppression_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isEmfSuppressed) InfraGreenPrimary else Color.Transparent
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isEmfSuppressed) InfraGreenPrimary else InfraGreenBorder
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (isEmfSuppressed) Color.Black else InfraGreenTextPrimaryVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isEmfSuppressed) "🛡️ REDUZIERT" else "🛡️ NORMATIV",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isEmfSuppressed) Color.Black else InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Footer Readouts (Frequency & Danger Status)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("EVP: %.1f kHz", frequencyKhz),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "STUFE $dangerLevel / 5 ${if (dangerLevel >= 4) "⚡ KRITISCH" else "NORMAL"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = statusColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
