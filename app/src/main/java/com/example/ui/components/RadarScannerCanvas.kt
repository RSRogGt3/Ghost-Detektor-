package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSecondary
import com.example.ui.theme.InfraGreenTertiary
import com.example.ui.theme.ThermalAmber
import com.example.ui.theme.UvViolet
import kotlin.math.cos
import kotlin.math.sin

data class RadarBlip(
    val id: String,
    val angleDegrees: Float,
    val distanceRatio: Float, // 0.1 to 0.9
    val dangerLevel: Int,
    val color: Color = InfraGreenPrimary,
    val label: String = "EMF-Fokus"
)

enum class FilterMode(val displayName: String, val primaryColor: Color, val accentColor: Color) {
    INFRA_GREEN("INFRA-GRÜN", InfraGreenPrimary, InfraGreenTertiary),
    THERMAL_RED("THERMISCH", ThermalAmber, AlertInfraRed),
    QUANTUM_MATRIX("MATRIX GRID", InfraGreenSecondary, Color(0xFF00FFCC)),
    ULTRAVIOLET("ULTRAVIOLETT", UvViolet, Color(0xFFFF44EE)),
    INFRA_YELLOW("INFRA-GELB", Color(0xFFFFDD00), Color(0xFFFFEE88)),
    INFRA_BLUE("INFRA-BLAU", Color(0xFF00A8FF), Color(0xFF88EEFF)),
    INFRARED("INFRAROT", Color(0xFFFF2A2A), Color(0xFFFF9999))
}

@Composable
fun RadarScannerCanvas(
    modifier: Modifier = Modifier,
    blips: List<RadarBlip> = emptyList(),
    filterMode: FilterMode = FilterMode.INFRA_GREEN,
    sweepSpeedMs: Int = 3000,
    isScanning: Boolean = true
) {
    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(isScanning, sweepSpeedMs) {
        if (isScanning) {
            rotationAnim.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(sweepSpeedMs, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotationAnim.snapTo(0f)
        }
    }

    val primaryColor = filterMode.primaryColor
    val accentColor = filterMode.accentColor

    Box(
        modifier = modifier
            .size(280.dp)
            .testTag("radar_scanner_canvas"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.minDimension / 2f) * 0.9f

            // Background Grid Rings
            val numRings = 4
            for (i in 1..numRings) {
                val radius = maxRadius * (i / numRings.toFloat())
                drawCircle(
                    color = primaryColor.copy(alpha = 0.25f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Crosshair axes (N-S, E-W)
            drawLine(
                color = primaryColor.copy(alpha = 0.35f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = primaryColor.copy(alpha = 0.35f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.5.dp.toPx()
            )

            // Outer Frame Ring
            drawCircle(
                color = primaryColor,
                radius = maxRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Compass markings
            val paint = android.graphics.Paint().apply {
                color = primaryColor.hashCode()
                textSize = 28f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawContext.canvas.nativeCanvas.drawText("N", center.x, center.y - maxRadius + 32f, paint)
            drawContext.canvas.nativeCanvas.drawText("S", center.x, center.y + maxRadius - 16f, paint)
            drawContext.canvas.nativeCanvas.drawText("O", center.x + maxRadius - 24f, center.y + 10f, paint)
            drawContext.canvas.nativeCanvas.drawText("W", center.x - maxRadius + 24f, center.y + 10f, paint)

            // Rotating Sweep Cone
            if (isScanning) {
                val sweepAngleDegrees = rotationAnim.value
                val sweepRad = Math.toRadians(sweepAngleDegrees.toDouble())

                val sweepPath = Path().apply {
                    moveTo(center.x, center.y)
                    val coneAngleRad = Math.toRadians(45.0)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            center.x - maxRadius,
                            center.y - maxRadius,
                            center.x + maxRadius,
                            center.y + maxRadius
                        ),
                        startAngleDegrees = sweepAngleDegrees - 45f,
                        sweepAngleDegrees = 45f,
                        forceMoveTo = false
                    )
                    lineTo(center.x, center.y)
                    close()
                }

                drawPath(
                    path = sweepPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.6f),
                            primaryColor.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = maxRadius
                    )
                )

                // Leading line beam
                val beamX = center.x + maxRadius * cos(sweepRad).toFloat()
                val beamY = center.y + maxRadius * sin(sweepRad).toFloat()
                drawLine(
                    color = accentColor,
                    start = center,
                    end = Offset(beamX, beamY),
                    strokeWidth = 2.5.dp.toPx()
                )
            }

            // Draw Detected Target Blips
            blips.forEach { blip ->
                val blipRad = Math.toRadians(blip.angleDegrees.toDouble())
                val distance = maxRadius * blip.distanceRatio.coerceIn(0.15f, 0.85f)
                val bx = center.x + distance * cos(blipRad).toFloat()
                val by = center.y + distance * sin(blipRad).toFloat()
                val blipCenter = Offset(bx, by)

                val blipColor = if (blip.dangerLevel >= 4) AlertInfraRed else blip.color

                // Glowing Halo
                drawCircle(
                    color = blipColor.copy(alpha = 0.35f),
                    radius = 16.dp.toPx(),
                    center = blipCenter
                )
                // Core Blip
                drawCircle(
                    color = blipColor,
                    radius = 6.dp.toPx(),
                    center = blipCenter
                )
                // Inner White Pulse
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = blipCenter
                )
            }
        }
    }
}
