package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun RealtimeEmfLineChart(
    modifier: Modifier = Modifier,
    dataPoints: List<Float>, // Values from 0.0 to 10.0 mG
    currentEmf: Float,
    isScanning: Boolean = true,
    lineColor: Color = InfraGreenPrimary,
    backgroundColor: Color = Color(0xFF08120B)
) {
    val pulseAnim = remember { Animatable(1f) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            pulseAnim.animateTo(
                targetValue = 1.8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseAnim.snapTo(1f)
        }
    }

    // Calculation of metrics (Peak & Avg)
    val peakValue = dataPoints.maxOrNull() ?: currentEmf
    val avgValue = if (dataPoints.isNotEmpty()) dataPoints.average().toFloat() else currentEmf
    var isMinimized by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, InfraGreenBorder.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .animateContentSize()
            .testTag("realtime_emf_chart_container")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Recharts/D3 Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isScanning) lineColor else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MAGNETFELD SCHWANKUNGEN (D3 LIVE)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InfraGreenTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avg Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AVG: ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", avgValue),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ThermalAmber,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Peak Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MAX: ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", peakValue),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (peakValue > 7.5f) AlertInfraRed else lineColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { isMinimized = !isMinimized },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isMinimized) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isMinimized) "Ausklappen" else "Minimieren",
                            tint = lineColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (!isMinimized) {

            // Real-Time Neon Green D3 Line Chart Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("emf_realtime_canvas")
            ) {
                val width = size.width
                val height = size.height
                val paddingLeft = 32.dp.toPx()
                val paddingBottom = 16.dp.toPx()
                val chartWidth = width - paddingLeft
                val chartHeight = height - paddingBottom

                // Grid lines count (D3 Y-axis scale markers: 0, 2.5, 5.0, 7.5, 10.0 mG)
                val yLevels = listOf(10.0f, 7.5f, 5.0f, 2.5f, 0.0f)
                val strokeDash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                yLevels.forEach { level ->
                    val y = chartHeight * (1.0f - (level / 10.0f))
                    // Draw horizontal D3 grid line
                    drawLine(
                        color = lineColor.copy(alpha = 0.15f),
                        start = Offset(paddingLeft, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = strokeDash
                    )
                }

                // Vertical grid lines (5 columns)
                val gridCols = 5
                for (col in 0..gridCols) {
                    val x = paddingLeft + (chartWidth / gridCols) * col
                    drawLine(
                        color = lineColor.copy(alpha = 0.08f),
                        start = Offset(x, 0f),
                        end = Offset(x, chartHeight),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // If dataset is empty or 1 point, synthesize baseline
                val points = if (dataPoints.size >= 2) dataPoints else listOf(2.5f, 2.5f, 2.5f)
                val numPoints = points.size

                val path = Path()
                val fillPath = Path()

                // Calculate screen coordinates for data points using D3 monotone interpolation
                val coords = points.mapIndexed { index, value ->
                    val x = paddingLeft + (chartWidth / (numPoints - 1).coerceAtLeast(1)) * index
                    val normalizedValue = (value / 10.0f).coerceIn(0f, 1f)
                    val y = chartHeight * (1.0f - normalizedValue)
                    Offset(x, y)
                }

                if (coords.isNotEmpty()) {
                    path.moveTo(coords.first().x, coords.first().y)
                    fillPath.moveTo(coords.first().x, chartHeight)
                    fillPath.lineTo(coords.first().x, coords.first().y)

                    // D3 Monotone Cubic Bezier smoothing logic
                    for (i in 0 until coords.size - 1) {
                        val p0 = coords[if (i > 0) i - 1 else i]
                        val p1 = coords[i]
                        val p2 = coords[i + 1]
                        val p3 = coords[if (i + 2 < coords.size) i + 2 else i + 1]

                        val cp1X = p1.x + (p2.x - p0.x) * 0.18f
                        val cp1Y = p1.y + (p2.y - p0.y) * 0.18f
                        val cp2X = p2.x - (p3.x - p1.x) * 0.18f
                        val cp2Y = p2.y - (p3.y - p1.y) * 0.18f

                        path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2.x, p2.y)
                        fillPath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2.x, p2.y)
                    }

                    fillPath.lineTo(coords.last().x, chartHeight)
                    fillPath.close()

                    // 1. Draw Gradient Area Fill under Neon Line
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.35f),
                                lineColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = chartHeight
                        )
                    )

                    // 2. Draw Ambient Outer Glow Stroke for Recharts Neon look
                    drawPath(
                        path = path,
                        color = lineColor.copy(alpha = 0.3f),
                        style = Stroke(width = 6.dp.toPx())
                    )

                    // 3. Draw Main Crisp Neon Green Line
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // 4. Draw Active Pulse Marker on latest point
                    val lastCoord = coords.last()
                    val pulseRadius = 8.dp.toPx() * pulseAnim.value

                    // Outer pulsing aura ring
                    drawCircle(
                        color = lineColor.copy(alpha = (0.6f / pulseAnim.value).coerceIn(0f, 1f)),
                        radius = pulseRadius,
                        center = lastCoord
                    )

                    // Inner bright white/neon dot
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = lastCoord
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 2.dp.toPx(),
                        center = lastCoord
                    )
                }
            }

            // Bottom X-Axis & Status Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "◀ FREQUENZVERLAUF 30S",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = InfraGreenTextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                )

                Text(
                    text = "LIVE SENSORIK ▶",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = lineColor.copy(alpha = 0.8f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            }
        }
    }
}
