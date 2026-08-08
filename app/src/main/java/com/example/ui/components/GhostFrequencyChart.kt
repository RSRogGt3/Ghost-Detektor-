package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GhostDetectionEntity
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenTextMuted
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.theme.ThermalAmber
import java.util.Calendar

@Composable
fun GhostFrequencyChart(
    detections: List<GhostDetectionEntity>,
    modifier: Modifier = Modifier
) {
    // Group detections by hour of day (0-23)
    val hourCounts = remember(detections) {
        val counts = IntArray(24) { 0 }
        val calendar = Calendar.getInstance()
        for (detection in detections) {
            calendar.timeInMillis = detection.timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            counts[hour]++
        }
        counts
    }

    val maxCount = remember(hourCounts) { hourCounts.maxOrNull()?.coerceAtLeast(1) ?: 1 }
    var isMinimized by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(InfraGreenSurface)
            .border(1.dp, InfraGreenBorder, RoundedCornerShape(10.dp))
            .padding(14.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AKTIVITÄT ÜBER DEN TAG",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = InfraGreenTextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Statistische Häufigkeit der Geister-Ereignisse nach Uhrzeit",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = InfraGreenTextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                )
            }

            IconButton(
                onClick = { isMinimized = !isMinimized },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isMinimized) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                    contentDescription = if (isMinimized) "Ausklappen" else "Minimieren",
                    tint = InfraGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        if (!isMinimized) {
            Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                val barCount = 24
                val spacing = 3.dp.toPx()
                val availableWidth = size.width - (spacing * (barCount - 1))
                val barWidth = availableWidth / barCount
                
                for (hour in 0 until 24) {
                    val count = hourCounts[hour]
                    val heightRatio = count.toFloat() / maxCount.toFloat()
                    val barHeight = size.height * heightRatio
                    val startX = hour * (barWidth + spacing)
                    val startY = size.height - barHeight
                    
                    val color = if (count == maxCount && count > 0) ThermalAmber else InfraGreenPrimary.copy(alpha = 0.6f)
                    
                    if (count > 0) {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(startX, startY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    } else {
                        // Draw empty placeholder line
                        drawRoundRect(
                            color = InfraGreenBorder,
                            topLeft = Offset(startX, size.height - 2.dp.toPx()),
                            size = Size(barWidth, 2.dp.toPx()),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("00:00", style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace))
            Text("06:00", style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace))
            Text("12:00", style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace))
            Text("18:00", style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace))
            Text("23:59", style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace))
        }
        }
    }
}
