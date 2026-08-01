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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.InfraGreenPrimary
import kotlin.math.sin

@Composable
fun AudioWaveformCanvas(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isGenerating: Boolean = false,
    liveMicAmplitude: Float = 0f,
    waveColor: Color = InfraGreenPrimary,
    amplitudeMultiplier: Float = 1.0f
) {
    val phaseAnim = remember { Animatable(0f) }

    val effectiveIsActive = isActive || isGenerating || liveMicAmplitude > 0.05f

    LaunchedEffect(effectiveIsActive) {
        if (effectiveIsActive) {
            phaseAnim.animateTo(
                targetValue = (2 * Math.PI).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(if (isGenerating) 600 else 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            phaseAnim.snapTo(0f)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val phase = phaseAnim.value

        val micBoost = liveMicAmplitude * (height / 2f) * 2.2f
        val genBoost = if (isGenerating) height * 0.35f else 0f
        val baseAmplitude = ((height / 3f) * amplitudeMultiplier + micBoost + genBoost).coerceAtMost(height * 0.45f)

        // Draw frequency spectrum vertical bars in background when active/generating
        if (effectiveIsActive) {
            val barCount = 32
            val barWidth = width / (barCount * 1.5f)
            val spacing = barWidth * 0.5f
            for (i in 0 until barCount) {
                val x = i * (barWidth + spacing) + spacing
                val normalized = i.toFloat() / barCount
                val barHeight = ((sin(normalized * Math.PI * 6 + phase * 2) + 1f).toFloat() * 0.5f * (baseAmplitude * 0.8f) + 4f)
                drawRect(
                    color = waveColor.copy(alpha = 0.18f),
                    topLeft = Offset(x, centerY - barHeight / 2f),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
            }
        }

        // Secondary Harmonic Wave
        val harmonicPath = Path()
        harmonicPath.moveTo(0f, centerY)
        val numPoints = 120
        for (i in 0..numPoints) {
            val x = (width / numPoints) * i
            val normX = i.toFloat() / numPoints
            val y = centerY + sin(normX * 8 * Math.PI - phase * 1.8f).toFloat() * (baseAmplitude * 0.4f)
            harmonicPath.lineTo(x, y)
        }
        drawPath(
            path = harmonicPath,
            color = waveColor.copy(alpha = 0.35f),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Primary Spirit Voice Waveform
        val path = Path()
        path.moveTo(0f, centerY)

        for (i in 0..numPoints) {
            val x = (width / numPoints) * i
            val normalizedX = i.toFloat() / numPoints
            val y = centerY + sin(normalizedX * 4 * Math.PI + phase).toFloat() * baseAmplitude * 0.75f +
                    sin(normalizedX * 12 * Math.PI - phase * 2.2f).toFloat() * (baseAmplitude * 0.25f)
            path.lineTo(x, y)
        }

        // Draw glowing gradient stroke under path or outline
        drawPath(
            path = path,
            color = if (isGenerating || liveMicAmplitude > 0.15f) waveColor else waveColor.copy(alpha = 0.85f),
            style = Stroke(width = if (isGenerating) 3.5.dp.toPx() else 2.5.dp.toPx())
        )

        // Draw center baseline glow
        drawLine(
            color = waveColor.copy(alpha = 0.3f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

