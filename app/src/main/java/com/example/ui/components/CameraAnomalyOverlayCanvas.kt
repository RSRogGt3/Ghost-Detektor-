package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.AnomalyType
import com.example.data.CameraAnomaly
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CameraAnomalyOverlayCanvas(
    anomalies: List<CameraAnomaly>,
    filterMode: FilterMode,
    avgLuminance: Float = 128f,
    modifier: Modifier = Modifier,
    onManualScanTarget: (xRatio: Float, yRatio: Float) -> Unit = { _, _ -> }
) {
    val pulseAnim = remember { Animatable(0f) }
    var userTapOffset by remember { mutableStateOf<Offset?>(null) }
    val tapAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val primaryColor = filterMode.primaryColor
    val accentColor = filterMode.accentColor

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val xRatio = offset.x / size.width
                    val yRatio = offset.y / size.height
                    userTapOffset = offset
                    onManualScanTarget(xRatio, yRatio)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val pulse = pulseAnim.value

        // Draw camera crosshair grid lines for spectral scanner feel
        val gridColor = primaryColor.copy(alpha = 0.12f)
        drawLine(
            color = gridColor,
            start = Offset(width / 2f, 0f),
            end = Offset(width / 2f, height),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = gridColor,
            start = Offset(0f, height / 2f),
            end = Offset(width, height / 2f),
            strokeWidth = 1.dp.toPx()
        )

        // Draw camera framing corners
        val cornerLen = 36.dp.toPx()
        val cornerStroke = 2.5.dp.toPx()
        val frameColor = primaryColor.copy(alpha = 0.5f)

        // Top-Left
        drawLine(frameColor, Offset(16.dp.toPx(), 16.dp.toPx()), Offset(16.dp.toPx() + cornerLen, 16.dp.toPx()), cornerStroke)
        drawLine(frameColor, Offset(16.dp.toPx(), 16.dp.toPx()), Offset(16.dp.toPx(), 16.dp.toPx() + cornerLen), cornerStroke)

        // Top-Right
        drawLine(frameColor, Offset(width - 16.dp.toPx(), 16.dp.toPx()), Offset(width - 16.dp.toPx() - cornerLen, 16.dp.toPx()), cornerStroke)
        drawLine(frameColor, Offset(width - 16.dp.toPx(), 16.dp.toPx()), Offset(width - 16.dp.toPx(), 16.dp.toPx() + cornerLen), cornerStroke)

        // Bottom-Left
        drawLine(frameColor, Offset(16.dp.toPx(), height - 16.dp.toPx()), Offset(16.dp.toPx() + cornerLen, height - 16.dp.toPx()), cornerStroke)
        drawLine(frameColor, Offset(16.dp.toPx(), height - 16.dp.toPx()), Offset(16.dp.toPx(), height - 16.dp.toPx() - cornerLen), cornerStroke)

        // Bottom-Right
        drawLine(frameColor, Offset(width - 16.dp.toPx(), height - 16.dp.toPx()), Offset(width - 16.dp.toPx() - cornerLen, height - 16.dp.toPx()), cornerStroke)
        drawLine(frameColor, Offset(width - 16.dp.toPx(), height - 16.dp.toPx()), Offset(width - 16.dp.toPx(), height - 16.dp.toPx() - cornerLen), cornerStroke)

        // Render each real-time camera anomaly
        anomalies.forEachIndexed { index, anomaly ->
            val cx = anomaly.xRatio * width
            val cy = anomaly.yRatio * height
            val intensity = anomaly.intensity

            val baseRadius = 38.dp.toPx() * intensity
            val pulsingRadius = baseRadius * (1f + pulse * 0.25f)

            // Anomaly color based on type & filter
            val (anomColor, badgeBg) = when (anomaly.type) {
                AnomalyType.THERMAL_HOTSPOT -> Color(0xFFFF3333) to Color(0xDD880000)
                AnomalyType.COLD_SPOT -> Color(0xFF00C8FF) to Color(0xDD004488)
                AnomalyType.BRIGHTNESS_SPIKE -> Color(0xFFFFEE00) to Color(0xDD887700)
                AnomalyType.MOTION_SHIFT -> primaryColor to Color(0xDD005522)
                AnomalyType.SPECTRAL_FLARE -> accentColor to Color(0xDD440066)
            }

            // 1. Aura glow circles
            drawCircle(
                color = anomColor.copy(alpha = 0.22f * intensity),
                center = Offset(cx, cy),
                radius = pulsingRadius * 1.4f
            )
            drawCircle(
                color = anomColor.copy(alpha = 0.45f),
                center = Offset(cx, cy),
                radius = baseRadius,
                style = Stroke(width = 2.dp.toPx())
            )

            // 2. Targeting Reticle Brackets [ ]
            val boxSize = baseRadius * 1.6f
            val bracketLen = boxSize * 0.35f
            val left = cx - boxSize / 2f
            val top = cy - boxSize / 2f
            val right = cx + boxSize / 2f
            val bottom = cy + boxSize / 2f

            val strokePx = 2.5.dp.toPx()

            // Corner brackets around anomaly
            drawLine(anomColor, Offset(left, top), Offset(left + bracketLen, top), strokePx)
            drawLine(anomColor, Offset(left, top), Offset(left, top + bracketLen), strokePx)

            drawLine(anomColor, Offset(right, top), Offset(right - bracketLen, top), strokePx)
            drawLine(anomColor, Offset(right, top), Offset(right, top + bracketLen), strokePx)

            drawLine(anomColor, Offset(left, bottom), Offset(left + bracketLen, bottom), strokePx)
            drawLine(anomColor, Offset(left, bottom), Offset(left, bottom - bracketLen), strokePx)

            drawLine(anomColor, Offset(right, bottom), Offset(right - bracketLen, bottom), strokePx)
            drawLine(anomColor, Offset(right, bottom), Offset(right, bottom - bracketLen), strokePx)

            // 3. Center Target Dot
            drawCircle(
                color = anomColor,
                center = Offset(cx, cy),
                radius = 4.dp.toPx()
            )

            // 4. Connecting Vector Line to Text Badge
            val labelX = (cx + boxSize * 0.7f).coerceAtMost(width - 150.dp.toPx())
            val labelY = (cy - boxSize * 0.5f).coerceAtLeast(40.dp.toPx())

            drawLine(
                color = anomColor.copy(alpha = 0.7f),
                start = Offset(cx + boxSize / 2f, cy - boxSize / 2f),
                end = Offset(labelX, labelY + 12.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )

            // Draw HUD Data Text Tag
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 28f
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
                isFakeBoldText = true
            }

            val textSubPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.LTGRAY
                textSize = 22f
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
            }

            val tagTitle = "ANOMALIE #${index + 1}: ${anomaly.type.displayName}"
            val tagDetail = "DELTA: ${anomaly.label} | ${String.format("%.1f", anomaly.emuValueMg)} mG"

            drawContext.canvas.nativeCanvas.drawRect(
                labelX - 4f,
                labelY - 24f,
                labelX + 320f,
                labelY + 42f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(200, 10, 20, 15)
                    style = android.graphics.Paint.Style.FILL
                }
            )

            drawContext.canvas.nativeCanvas.drawRect(
                labelX - 4f,
                labelY - 24f,
                labelX + 320f,
                labelY + 42f,
                android.graphics.Paint().apply {
                    color = anomColor.toArgb()
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 2f
                }
            )

            drawContext.canvas.nativeCanvas.drawText(
                tagTitle,
                labelX + 6f,
                labelY,
                textPaint
            )

            drawContext.canvas.nativeCanvas.drawText(
                tagDetail,
                labelX + 6f,
                labelY + 30f,
                textSubPaint
            )
        }

        // Render user tap manual target scan marker if tapped
        userTapOffset?.let { tap ->
            drawCircle(
                color = primaryColor.copy(alpha = 0.8f),
                center = tap,
                radius = 32.dp.toPx(),
                style = Stroke(width = 2.5.dp.toPx())
            )
            drawLine(
                color = primaryColor,
                start = Offset(tap.x - 20.dp.toPx(), tap.y),
                end = Offset(tap.x + 20.dp.toPx(), tap.y),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = primaryColor,
                start = Offset(tap.x, tap.y - 20.dp.toPx()),
                end = Offset(tap.x, tap.y + 20.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

private fun Color.toArgb(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}
