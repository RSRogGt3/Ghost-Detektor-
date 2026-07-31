package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GhostCompassOverlay(
    azimuth: Float,
    emfLevel: Float,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedAzimuth by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "compassAzimuth"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width.coerceAtMost(size.height) / 2f * 0.9f

            // Draw outer dashed circle
            drawCircle(
                color = primaryColor.copy(alpha = 0.3f),
                radius = radius,
                center = center,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 15f))
                )
            )

            // Draw Inner circle
            drawCircle(
                color = primaryColor.copy(alpha = 0.1f),
                radius = radius * 0.8f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Compass rotation based on magnetometer (animate heading to top)
            rotate(degrees = -animatedAzimuth, pivot = center) {
                // North indicator
                val northPath = Path().apply {
                    moveTo(center.x, center.y - radius + 10f)
                    lineTo(center.x - 15f, center.y - radius + 40f)
                    lineTo(center.x + 15f, center.y - radius + 40f)
                    close()
                }
                drawPath(
                    path = northPath,
                    color = primaryColor
                )

                // Draw South, East, West ticks
                drawLine(
                    color = primaryColor.copy(alpha = 0.6f),
                    start = Offset(center.x, center.y + radius - 20f),
                    end = Offset(center.x, center.y + radius),
                    strokeWidth = 3.dp.toPx()
                )
                drawLine(
                    color = primaryColor.copy(alpha = 0.6f),
                    start = Offset(center.x + radius - 20f, center.y),
                    end = Offset(center.x + radius, center.y),
                    strokeWidth = 3.dp.toPx()
                )
                drawLine(
                    color = primaryColor.copy(alpha = 0.6f),
                    start = Offset(center.x - radius + 20f, center.y),
                    end = Offset(center.x - radius, center.y),
                    strokeWidth = 3.dp.toPx()
                )

                // High Activity Spike Arrows (if EMF > 5.0)
                if (emfLevel > 5.0f) {
                    val spikeCount = 3
                    val offsetRadius = radius * 0.6f
                    for (i in 0 until spikeCount) {
                        val angle = (Math.random() * 360).toFloat()
                        rotate(degrees = angle, pivot = center) {
                            val arrowPath = Path().apply {
                                moveTo(center.x, center.y - offsetRadius)
                                lineTo(center.x - 10f, center.y - offsetRadius + 20f)
                                lineTo(center.x + 10f, center.y - offsetRadius + 20f)
                                close()
                            }
                            drawPath(
                                path = arrowPath,
                                color = Color.Red.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            
            // Draw crosshair at center
            drawLine(
                color = primaryColor.copy(alpha = 0.5f),
                start = Offset(center.x - 15f, center.y),
                end = Offset(center.x + 15f, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = primaryColor.copy(alpha = 0.5f),
                start = Offset(center.x, center.y - 15f),
                end = Offset(center.x, center.y + 15f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}
