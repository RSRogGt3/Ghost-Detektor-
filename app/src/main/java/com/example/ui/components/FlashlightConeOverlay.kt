package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Tactical Flashlight & Volumetric Focused Light Cone Overlay
 * Renders a focused flashlight beam effect with atmospheric dust particles,
 * center hotspot, soft falloff edges, and realistic voltage flicker.
 */
@Composable
fun FlashlightConeOverlay(
    isFlashlightActive: Boolean,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFFFFFFC0)
) {
    val beamAlpha by animateFloatAsState(
        targetValue = if (isFlashlightActive) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "flashlight_beam_alpha"
    )

    if (beamAlpha <= 0.01f) return

    val infiniteTransition = rememberInfiniteTransition(label = "flashlight_flicker_transition")

    // Subtle tactical voltage flicker & beam movement
    val flickerFactor by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voltage_flicker"
    )

    val particleDriftY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_drift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .alpha(beamAlpha)
            .testTag("flashlight_cone_overlay")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 1. Dark Atmospheric Room Mask (Vignette) around the flashlight beam
            val darkMaskBrush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.35f),
                    Color.Black.copy(alpha = 0.75f)
                ),
                center = Offset(canvasWidth / 2f, canvasHeight * 0.45f),
                radius = Math.max(canvasWidth, canvasHeight) * 0.65f
            )
            drawRect(brush = darkMaskBrush)

            // Flashlight emitter position (Top Center of viewport)
            val apexX = canvasWidth / 2f
            val apexY = -20f

            // Flashlight light cone geometry coordinates
            val beamBaseWidth = canvasWidth * 0.85f
            val baseLeft = (canvasWidth - beamBaseWidth) / 2f
            val baseRight = baseLeft + beamBaseWidth
            val beamBottomY = canvasHeight * 0.95f

            // 2. Conical Light Beam Geometry Path
            val conePath = Path().apply {
                moveTo(apexX, apexY)
                lineTo(baseLeft, beamBottomY)
                lineTo(baseRight, beamBottomY)
                close()
            }

            // A. Primary Volumetric Light Cone Brush (Warm Tactical White + Phosphor Tint)
            val beamBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFE0).copy(alpha = (0.75f * flickerFactor).coerceIn(0f, 1f)),
                    primaryColor.copy(alpha = (0.45f * flickerFactor).coerceIn(0f, 1f)),
                    primaryColor.copy(alpha = (0.15f * flickerFactor).coerceIn(0f, 1f)),
                    Color.Transparent
                ),
                startY = apexY,
                endY = beamBottomY
            )

            drawPath(
                path = conePath,
                brush = beamBrush
            )

            // B. Focused Center Hotspot Beam (Intensity Core)
            val coreWidth = canvasWidth * 0.42f
            val coreLeft = (canvasWidth - coreWidth) / 2f
            val coreRight = coreLeft + coreWidth

            val corePath = Path().apply {
                moveTo(apexX, apexY)
                lineTo(coreLeft, beamBottomY)
                lineTo(coreRight, beamBottomY)
                close()
            }

            val coreBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = (0.85f * flickerFactor).coerceIn(0f, 1f)),
                    Color(0xFFFFFFD0).copy(alpha = (0.50f * flickerFactor).coerceIn(0f, 1f)),
                    Color.Transparent
                ),
                startY = apexY,
                endY = beamBottomY * 0.80f
            )

            drawPath(
                path = corePath,
                brush = coreBrush
            )

            // C. Focused Radial Spotlight Circle in center field of view
            val spotlightCenter = Offset(canvasWidth / 2f, canvasHeight * 0.45f)
            val spotlightRadius = canvasWidth * 0.38f

            val spotlightBrush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = (0.45f * flickerFactor).coerceIn(0f, 1f)),
                    primaryColor.copy(alpha = (0.25f * flickerFactor).coerceIn(0f, 1f)),
                    Color.Transparent
                ),
                center = spotlightCenter,
                radius = spotlightRadius
            )

            drawCircle(
                brush = spotlightBrush,
                center = spotlightCenter,
                radius = spotlightRadius
            )

            // 3. Volumetric Dust Particles Floating in Light Cone
            val particleCount = 45
            val seed = 1337
            val random = Random(seed)

            for (i in 0 until particleCount) {
                val relX = random.nextFloat()
                val relY = (random.nextFloat() + particleDriftY) % 1.0f

                val pX = baseLeft + relX * (baseRight - baseLeft)
                val pY = relY * beamBottomY

                // Check if particle is roughly within cone bounds
                val coneHalfWidthAtY = (apexX - baseLeft) * (pY / beamBottomY)
                if (Math.abs(pX - apexX) <= coneHalfWidthAtY) {
                    val radius = random.nextFloat() * 2.2f + 1.0f
                    val alpha = (0.3f + random.nextFloat() * 0.5f) * (1.0f - pY / beamBottomY)
                    
                    drawCircle(
                        color = Color.White.copy(alpha = alpha.coerceIn(0f, 1f)),
                        radius = radius,
                        center = Offset(pX, pY)
                    )
                }
            }

            // 4. Tactical Lens Flare Halo Rings along beam axis
            val ringCenterY = canvasHeight * 0.30f
            drawCircle(
                color = Color.White.copy(alpha = 0.20f),
                center = Offset(apexX, ringCenterY),
                radius = 45f
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.15f),
                center = Offset(apexX, ringCenterY + 80f),
                radius = 90f
            )
        }

        // HUD Status Badge for Torch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color(0xCC051208), RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFFFFFF00).copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Flashlight Active",
                tint = Color(0xFFFFFF00),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "TASCHENLAMPE: FOKUSSIERTER LICHTKEGEL AN",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFFFFF00),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                )
            )
        }
    }
}
