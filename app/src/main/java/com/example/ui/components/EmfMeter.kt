package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import com.example.ui.theme.InfraGreenTextMuted
import com.example.ui.theme.ThermalAmber

@Composable
fun EmfMeter(
    modifier: Modifier = Modifier,
    emfValue: Float, // 0.0 to 10.0 mG
    dangerLevel: Int, // 1 to 5
    frequencyKhz: Float // e.g. 42.8
) {
    val animatedEmf by animateFloatAsState(
        targetValue = emfValue,
        animationSpec = tween(durationMillis = 300),
        label = "emf_anim"
    )

    var isMinimized by remember { mutableStateOf(false) }

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
            .border(1.dp, InfraGreenBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
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
                            color = InfraGreenTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
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
                    .height(24.dp),
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

            // Footer Readouts (Frequency & Danger Status)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("EVP: %.1f kHz", frequencyKhz),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = InfraGreenTextMuted,
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
