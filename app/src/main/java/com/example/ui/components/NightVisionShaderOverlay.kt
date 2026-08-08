package com.example.ui.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// AGSL Runtime Shader Code for Android 13+ (API 33+) Night Vision Green Phosphor Effect
private const val NIGHT_VISION_AGSL_SHADER = """
    uniform shader composable;
    uniform float2 resolution;
    uniform float time;
    uniform float gain;

    float rand(float2 co) {
        return fract(sin(dot(co, float2(12.9898, 78.233))) * 43758.5453);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / resolution;
        
        // Sample original camera texture
        half4 color = composable.eval(fragCoord);
        
        // Calculate green luminance boost
        float lum = dot(color.rgb, float3(0.299, 0.587, 0.114));
        lum = pow(lum * gain, 0.82);
        
        // Authentic P43 Green Phosphor Color Palette
        float3 nvColor = float3(lum * 0.15, lum * 1.35 + 0.05, lum * 0.25);
        
        // CRT Scanlines
        float scanline = sin(uv.y * resolution.y * 0.8 + time * 8.0) * 0.06;
        nvColor -= scanline;
        
        // Film Grain Noise
        float noise = (rand(uv + float2(time * 0.02, time * 0.03)) - 0.5) * 0.10;
        nvColor += noise;
        
        // Radial Vignette
        float2 dist = (uv - 0.5) * 1.25;
        float vignette = 1.0 - dot(dist, dist);
        nvColor *= clamp(vignette, 0.0, 1.0);
        
        return half4(clamp(nvColor, 0.0, 1.0), color.a);
    }
"""

/**
 * Authentic Green-Filter Night Vision Compose Shader & Canvas Effect Overlay
 * Simulates military-grade P43 phosphor green night vision goggles (NVG) for ghost hunting.
 */
@Composable
fun NightVisionShaderOverlay(
    isActive: Boolean = true,
    gainLevelDb: Float = 14f,
    filterMode: FilterMode = FilterMode.INFRA_GREEN,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    if (!isActive) {
        content()
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "night_vision_shader_transition")

    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shader_time"
    )

    val scanLineOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline_offset"
    )

    val phosphorPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.00f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phosphor_pulse"
    )

    val activePrimaryColor = filterMode.primaryColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .testTag("night_vision_shader_overlay")
    ) {
        // 1. Content Layer with Shader Effect / ColorMatrix filter
        val useAgsl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

        if (useAgsl) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val shader = RuntimeShader(NIGHT_VISION_AGSL_SHADER)
                                shader.setFloatUniform("resolution", size.width, size.height)
                                shader.setFloatUniform("time", animTime)
                                shader.setFloatUniform("gain", (gainLevelDb / 10f).coerceIn(0.8f, 2.2f))
                                renderEffect = RenderEffect
                                    .createRuntimeShaderEffect(shader, "composable")
                                    .asComposeRenderEffect()
                            }
                        } catch (_: Exception) {
                            // Fallback to ColorMatrix if AGSL shader compilation fails on runtime
                        }
                    }
            ) {
                content()
            }
        } else {
            // High-Performance ColorMatrix + GraphicsLayer Fallback
            val nightVisionColorMatrix = remember(filterMode, gainLevelDb) {
                when (filterMode) {
                    FilterMode.INFRA_GREEN -> ColorMatrix(
                        floatArrayOf(
                            0.05f, 0.15f, 0.02f, 0f, 0f,     // Red
                            0.10f, 1.35f, 0.15f, 0f, 20f,    // Green phosphor boost
                            0.02f, 0.15f, 0.05f, 0f, 0f,     // Blue
                            0f,    0f,    0f,    1f, 0f      // Alpha
                        )
                    )
                    FilterMode.THERMAL_RED -> ColorMatrix(
                        floatArrayOf(
                            1.40f, 0.20f, 0.02f, 0f, 25f,    // Thermal Red
                            0.10f, 0.20f, 0.02f, 0f, 0f,
                            0.02f, 0.02f, 0.05f, 0f, 0f,
                            0f,    0f,    0f,    1f, 0f
                        )
                    )
                    FilterMode.QUANTUM_MATRIX -> ColorMatrix(
                        floatArrayOf(
                            0.02f, 0.10f, 0.05f, 0f, 0f,
                            0.10f, 0.80f, 0.80f, 0f, 10f,
                            0.10f, 0.50f, 1.20f, 0f, 20f,    // Quantum Cyan
                            0f,    0f,    0f,    1f, 0f
                        )
                    )
                    else -> ColorMatrix(
                        floatArrayOf(
                            0.05f, 0.15f, 0.02f, 0f, 0f,
                            0.10f, 1.35f, 0.15f, 0f, 20f,
                            0.02f, 0.15f, 0.05f, 0f, 0f,
                            0f,    0f,    0f,    1f, 0f
                        )
                    )
                }
            }

            val filterPaint = remember(nightVisionColorMatrix) {
                androidx.compose.ui.graphics.Paint().apply {
                    colorFilter = ColorFilter.colorMatrix(nightVisionColorMatrix)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContext.canvas.saveLayer(size.toRect(), filterPaint)
                        drawContent()
                        drawContext.canvas.restore()
                    }
            ) {
                content()
            }
        }

        // 2. Phosphor Luminance Glow Base Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    activePrimaryColor.copy(alpha = 0.08f * phosphorPulseAlpha)
                )
        )

        // 3. Dynamic Canvas Raster Pass: Grain Noise, Scanlines & Radial Vignette
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // A. CRT Scanlines
            val scanlineSpacing = 6f
            var y = 0f
            val scanlineColor = Color.Black.copy(alpha = 0.22f)
            while (y < canvasHeight) {
                drawLine(
                    color = scanlineColor,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1.2f
                )
                y += scanlineSpacing
            }

            // B. Moving Laser Scanline Beam Sweep
            val sweepY = (scanLineOffsetY * canvasHeight)
            drawRect(
                color = activePrimaryColor.copy(alpha = 0.18f),
                topLeft = Offset(0f, sweepY),
                size = Size(canvasWidth, 8f)
            )

            // C. Phosphor Grain Noise Particles
            val grainCount = 180
            val grainPoints = ArrayList<Offset>(grainCount)
            val seed = (animTime * 100).toInt()
            val random = Random(seed)
            for (i in 0 until grainCount) {
                grainPoints.add(
                    Offset(
                        x = random.nextFloat() * canvasWidth,
                        y = random.nextFloat() * canvasHeight
                    )
                )
            }
            drawPoints(
                points = grainPoints,
                pointMode = PointMode.Points,
                color = activePrimaryColor.copy(alpha = 0.45f),
                strokeWidth = 2.5f
            )

            // D. Radial Corner Vignette Overlay
            val vignetteBrush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.60f),
                    Color.Black.copy(alpha = 0.95f)
                ),
                center = Offset(canvasWidth / 2f, canvasHeight / 2f),
                radius = Math.max(canvasWidth, canvasHeight) * 0.70f
            )
            drawRect(brush = vignetteBrush)
        }

        // 4. Night Vision HUD Overlay Metrics
        NightVisionHudBanner(
            gainDb = gainLevelDb,
            filterMode = filterMode,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun NightVisionHudBanner(
    gainDb: Float,
    filterMode: FilterMode,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(12.dp)
            .background(Color(0xCC051208), RoundedCornerShape(6.dp))
            .border(1.dp, filterMode.primaryColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LightMode,
            contentDescription = "NVG Gain",
            tint = filterMode.primaryColor,
            modifier = Modifier.width(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "NVG-9 SHADER [${filterMode.displayName.uppercase()}] GAIN: +${gainDb.toInt()}dB",
            style = MaterialTheme.typography.labelSmall.copy(
                color = filterMode.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        )
    }
}
