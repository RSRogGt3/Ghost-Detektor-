package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.InfraGreenPrimary
import kotlin.math.sin

@Composable
fun AudioWaveformCanvas(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    isScanning: Boolean = true,
    isGenerating: Boolean = false,
    isSpeaking: Boolean = false,
    liveMicAmplitude: Float = 0f,
    frequencyKhz: Float = 100f,
    emfLevel: Float = 1f,
    waveColor: Color = InfraGreenPrimary,
    amplitudeMultiplier: Float = 1.0f
) {
    val phaseAnim = remember { Animatable(0f) }
    val sweepNeedleAnim = remember { Animatable(0f) }

    val effectiveIsActive = isActive || isGenerating || isSpeaking || isScanning || liveMicAmplitude > 0.03f

    LaunchedEffect(effectiveIsActive) {
        if (effectiveIsActive) {
            phaseAnim.animateTo(
                targetValue = (2 * Math.PI).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(if (isSpeaking || isGenerating) 500 else 1100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            phaseAnim.snapTo(0f)
        }
    }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            sweepNeedleAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            sweepNeedleAnim.snapTo(0.5f)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(95.dp)
            .testTag("audio_waveform_canvas")
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val phase = phaseAnim.value

        val micBoost = liveMicAmplitude * (height / 2f) * 2.8f
        val genBoost = if (isGenerating) height * 0.38f else 0f
        val speakBoost = if (isSpeaking) height * 0.42f else 0f
        val baseAmplitude = ((height / 3.2f) * amplitudeMultiplier + micBoost + genBoost + speakBoost).coerceAtMost(height * 0.45f)

        // 1. Equalizer Frequency Spectrum Bars (Background)
        if (effectiveIsActive) {
            val barCount = 36
            val barWidth = width / (barCount * 1.4f)
            val spacing = barWidth * 0.4f
            for (i in 0 until barCount) {
                val x = i * (barWidth + spacing) + spacing
                val normalized = i.toFloat() / barCount
                val sineMod = (sin(normalized * Math.PI * 7 + phase * 2.5) + 1f).toFloat() * 0.5f
                val barHeight = (sineMod * (baseAmplitude * 0.9f) + 6f).coerceAtMost(height * 0.85f)
                
                val barAlpha = if (isSpeaking || isGenerating) 0.35f else 0.18f
                val barColor = if (i % 6 == 0 && (isSpeaking || liveMicAmplitude > 0.2f)) Color(0xFF00FFCC) else waveColor

                drawRect(
                    color = barColor.copy(alpha = barAlpha),
                    topLeft = Offset(x, centerY - barHeight / 2f),
                    size = Size(barWidth, barHeight)
                )
            }
        }

        // 2. Sweeping Frequency Laser Line (Needle)
        if (isScanning) {
            val needleX = width * sweepNeedleAnim.value
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.6f),
                start = Offset(needleX, 0f),
                end = Offset(needleX, height),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(needleX, centerY)
            )
        }

        // 3. Secondary High-Frequency Voice Formant Wave
        val harmonicPath = Path()
        harmonicPath.moveTo(0f, centerY)
        val numPoints = 140
        for (i in 0..numPoints) {
            val x = (width / numPoints) * i
            val normX = i.toFloat() / numPoints
            val y = centerY + sin(normX * 10 * Math.PI - phase * 2f).toFloat() * (baseAmplitude * 0.45f)
            harmonicPath.lineTo(x, y)
        }
        drawPath(
            path = harmonicPath,
            color = if (isSpeaking) Color(0xFF00FFCC).copy(alpha = 0.7f) else waveColor.copy(alpha = 0.3f),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // 4. Primary Carrier Wave (Spirit Box Scanner Waveform)
        val path = Path()
        path.moveTo(0f, centerY)

        val peakNodes = mutableListOf<Offset>()

        for (i in 0..numPoints) {
            val x = (width / numPoints) * i
            val normalizedX = i.toFloat() / numPoints
            val y = centerY + sin(normalizedX * 4 * Math.PI + phase).toFloat() * baseAmplitude * 0.75f +
                    sin(normalizedX * 14 * Math.PI - phase * 2.4f).toFloat() * (baseAmplitude * 0.25f)
            path.lineTo(x, y)

            // Detect peak points for glowing voice node indicators
            if (i % 20 == 10 && (isSpeaking || isGenerating || liveMicAmplitude > 0.15f)) {
                peakNodes.add(Offset(x, y))
            }
        }

        // Draw primary stroke
        drawPath(
            path = path,
            color = when {
                isSpeaking -> Color(0xFF00FFCC)
                isGenerating -> Color(0xFFFFD700)
                liveMicAmplitude > 0.2f -> Color(0xFFFF3333)
                else -> waveColor
            },
            style = Stroke(width = if (isSpeaking || isGenerating) 3.5.dp.toPx() else 2.5.dp.toPx())
        )

        // Draw glowing nodes on voice peak frequencies
        peakNodes.forEach { node ->
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 4.dp.toPx(),
                center = node
            )
            drawCircle(
                color = Color(0xFF00FFCC).copy(alpha = 0.5f),
                radius = 8.dp.toPx(),
                center = node
            )
        }

        // 5. Center Baseline Grid Line
        drawLine(
            color = waveColor.copy(alpha = 0.35f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1.dp.toPx()
        )

        // 6. Live Telemetry Text Overlay
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GREEN
            textSize = 20f
            isAntiAlias = true
            typeface = android.graphics.Typeface.MONOSPACE
        }

        val leftText = "SWEEP: ${String.format(java.util.Locale.US, "%.1f", frequencyKhz)} kHz"
        val statusText = when {
            isSpeaking -> "STIMME ERFASST (TTS)"
            isGenerating -> "IN VERBINDUNG..."
            liveMicAmplitude > 0.15f -> "STIMMEN-IMPULS!"
            isScanning -> "STIMMEN-SUCHE..."
            else -> "BEREIT"
        }

        drawContext.canvas.nativeCanvas.drawText(leftText, 12f, 24f, textPaint)

        textPaint.textAlign = android.graphics.Paint.Align.RIGHT
        textPaint.color = if (isSpeaking || isGenerating) android.graphics.Color.CYAN else android.graphics.Color.GREEN
        drawContext.canvas.nativeCanvas.drawText(statusText, width - 12f, 24f, textPaint)
    }
}


