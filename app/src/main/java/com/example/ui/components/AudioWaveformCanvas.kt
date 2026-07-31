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
    waveColor: Color = InfraGreenPrimary,
    amplitudeMultiplier: Float = 1.0f
) {
    val phaseAnim = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            phaseAnim.animateTo(
                targetValue = (2 * Math.PI).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
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
            .height(60.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val phase = phaseAnim.value

        val path = Path()
        path.moveTo(0f, centerY)

        val numPoints = 100
        val baseAmplitude = if (isActive) (height / 2.5f) * amplitudeMultiplier else 4f

        for (i in 0..numPoints) {
            val x = (width / numPoints) * i
            val normalizedX = i.toFloat() / numPoints
            // Composite sine wave for eerie audio frequencies
            val y = centerY + sin(normalizedX * 4 * Math.PI + phase).toFloat() * baseAmplitude * 0.7f +
                    sin(normalizedX * 10 * Math.PI - phase * 1.5f).toFloat() * (baseAmplitude * 0.3f)
            path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = waveColor,
            style = Stroke(width = 2.5.dp.toPx())
        )

        // Draw center baseline glow
        drawLine(
            color = waveColor.copy(alpha = 0.2f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1.dp.toPx()
        )
    }
}
