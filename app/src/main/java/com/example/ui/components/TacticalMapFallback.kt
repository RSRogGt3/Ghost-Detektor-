package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GhostDetectionEntity
import com.example.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

@Composable
fun TacticalTacticalMapFallback(
    detections: List<GhostDetectionEntity>,
    modifier: Modifier = Modifier,
    onDetectionClick: (GhostDetectionEntity) -> Unit = {}
) {
    var zoomLevel by remember { mutableStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedEntity by remember { mutableStateOf<GhostDetectionEntity?>(null) }

    val scanAngle = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scanAngle.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val baseLatitude = remember(detections) {
        val valid = detections.mapNotNull { it.latitude }
        if (valid.isNotEmpty()) valid.average() else 51.1657
    }
    val baseLongitude = remember(detections) {
        val valid = detections.mapNotNull { it.longitude }
        if (valid.isNotEmpty()) valid.average() else 10.4515
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(InfraGreenBackground)
            .testTag("tactical_map_canvas_container")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomLevel = (zoomLevel * zoom).coerceIn(0.5f, 5f)
                        panOffset += pan
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f + panOffset.x, height / 2f + panOffset.y)

            // Grid background
            val gridSize = 40.dp.toPx() * zoomLevel
            val startX = (panOffset.x % gridSize)
            val startY = (panOffset.y % gridSize)

            var x = startX
            while (x < width) {
                drawLine(
                    color = InfraGreenBorder.copy(alpha = 0.4f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridSize
            }

            var y = startY
            while (y < height) {
                drawLine(
                    color = InfraGreenBorder.copy(alpha = 0.4f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridSize
            }

            // Tactical range rings
            val baseRadius = (min(width, height) * 0.25f) * zoomLevel
            for (i in 1..4) {
                val r = baseRadius * i
                drawCircle(
                    color = InfraGreenPrimary.copy(alpha = 0.25f / i),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }

            // Radar sweep cone
            val sweepRad = Math.toRadians(scanAngle.value.toDouble())
            val sweepRadius = max(width, height)
            val sweepEnd = Offset(
                center.x + (sweepRadius * cos(sweepRad)).toFloat(),
                center.y + (sweepRadius * sin(sweepRad)).toFloat()
            )
            drawLine(
                color = InfraGreenPrimary.copy(alpha = 0.6f),
                start = center,
                end = sweepEnd,
                strokeWidth = 2f
            )

            // Draw detections
            detections.forEach { ghost ->
                val lat = ghost.latitude ?: (baseLatitude + (Random(ghost.id.hashCode()).nextDouble() - 0.5) * 0.05)
                val lng = ghost.longitude ?: (baseLongitude + (Random(ghost.id.hashCode() + 1).nextDouble() - 0.5) * 0.05)

                val deltaLat = (lat - baseLatitude) * 10000.0 * zoomLevel
                val deltaLng = (lng - baseLongitude) * 10000.0 * zoomLevel

                val markerX = (center.x + deltaLng).toFloat()
                val markerY = (center.y - deltaLat).toFloat()

                if (markerX in 0f..width && markerY in 0f..height) {
                    val color = when (ghost.type) {
                        "Poltergeist" -> UvViolet
                        "Dämon" -> AlertInfraRed
                        "Schattenwesen" -> CyberCyan
                        else -> InfraGreenPrimary
                    }

                    // Outer pulse ring
                    drawCircle(
                        color = color.copy(alpha = 0.3f),
                        radius = 16.dp.toPx() * (1f + (ghost.dangerLevel / 10f)),
                        center = Offset(markerX, markerY)
                    )

                    // Core marker
                    drawCircle(
                        color = color,
                        radius = 6.dp.toPx(),
                        center = Offset(markerX, markerY)
                    )

                    // Ghost Name label
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            this.color = android.graphics.Color.WHITE
                            this.textSize = 28f
                            this.isFakeBoldText = true
                            this.setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                        }
                        drawText(
                            "${ghost.name} (Stufe ${ghost.dangerLevel})",
                            markerX + 18f,
                            markerY + 8f,
                            paint
                        )
                    }
                }
            }

            // Center User / Base coordinate
            drawCircle(
                color = CyberCyan,
                radius = 8.dp.toPx(),
                center = center
            )
            drawCircle(
                color = CyberCyan.copy(alpha = 0.4f),
                radius = 18.dp.toPx(),
                center = center,
                style = Stroke(width = 2f)
            )
        }

        // Tactical HUD Top Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(InfraGreenSurface.copy(alpha = 0.85f), RoundedCornerShape(10.dp))
                .border(1.dp, InfraGreenBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = InfraGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "TAKTIK-RADAR KARTE (OFFLINE-MODUS)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InfraGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Text(
                    text = "${detections.size} Anomalie-Hotspots erfasst",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = InfraGreenTextPrimaryVariant,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = { zoomLevel = (zoomLevel * 1.3f).coerceAtMost(5f) },
                    modifier = Modifier.size(32.dp).background(InfraGreenSurfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = InfraGreenPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { zoomLevel = (zoomLevel / 1.3f).coerceAtLeast(0.5f) },
                    modifier = Modifier.size(32.dp).background(InfraGreenSurfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = InfraGreenPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { panOffset = Offset.Zero; zoomLevel = 1f },
                    modifier = Modifier.size(32.dp).background(InfraGreenSurfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Zentrieren", tint = InfraGreenPrimary, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Entity quick inspect card if clicked or selected
        selectedEntity?.let { entity ->
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entity.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = InfraGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "${entity.type} • Gefahrenstufe ${entity.dangerLevel} • EMF: ${entity.emfLevel} mG",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    }
                    IconButton(onClick = { selectedEntity = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = InfraGreenTextPrimaryVariant)
                    }
                }
            }
        }
    }
}
