package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DimensionPlane
import com.example.data.SigilType
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.viewmodel.GhostViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DimensionSigilDialog(
    viewModel: GhostViewModel,
    onDismiss: () -> Unit
) {
    val activeDimension by viewModel.activeDimensionPlane.collectAsStateWithLifecycle()
    val activeSigil by viewModel.activeSigil.collectAsStateWithLifecycle()
    val sigilTimerSeconds by viewModel.sigilTimerSeconds.collectAsStateWithLifecycle()
    val isCastingRitual by viewModel.isCastingSigilRitual.collectAsStateWithLifecycle()
    val capturedCount by viewModel.capturedCount.collectAsStateWithLifecycle()
    val activeRiftsCount by viewModel.activeRiftsCount.collectAsStateWithLifecycle()
    val isClosingDimension by viewModel.isClosingDimension.collectAsStateWithLifecycle()
    val isCapturingEntity by viewModel.isCapturingEntity.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedSigil by remember { mutableStateOf(SigilType.DEMON_BANISHING) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(activeDimension.colorHex), RoundedCornerShape(16.dp)),
            color = Color(0xFF070B0E)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔮",
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DIMENSIONEN & SIGEL-SCHMIEDE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color(activeDimension.colorHex),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Ebene: ${activeDimension.codeName} • ${activeDimension.frequencyHz} Hz",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dimension_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0D141A),
                    contentColor = InfraGreenPrimary,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(activeDimension.colorHex)
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "✨ SIGEL-SCHMIEDE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "🌀 DIMENSIONEN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "⚡ TRESOR & STATUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Tab Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> TabSigilForge(
                            viewModel = viewModel,
                            selectedSigil = selectedSigil,
                            activeSigil = activeSigil,
                            sigilTimerSeconds = sigilTimerSeconds,
                            isCastingRitual = isCastingRitual,
                            onSelectSigil = { selectedSigil = it }
                        )
                        1 -> TabDimensionsTransmitter(
                            viewModel = viewModel,
                            activeDimension = activeDimension
                        )
                        2 -> TabVaultAndStatus(
                            viewModel = viewModel,
                            capturedCount = capturedCount,
                            activeRiftsCount = activeRiftsCount,
                            isClosingDimension = isClosingDimension,
                            isCapturingEntity = isCapturingEntity,
                            activeSigil = activeSigil,
                            sigilTimerSeconds = sigilTimerSeconds
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabSigilForge(
    viewModel: GhostViewModel,
    selectedSigil: SigilType,
    activeSigil: SigilType?,
    sigilTimerSeconds: Int,
    isCastingRitual: Boolean,
    onSelectSigil: (SigilType) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Active Sigil Status Banner (if active)
        if (activeSigil != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B15)),
                border = BorderStroke(1.dp, Color(activeSigil.colorHex)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = activeSigil.symbol, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AKTIVES SIEGEL: ${activeSigil.title}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(activeSigil.colorHex),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Verbleibende Wirkung: ${sigilTimerSeconds}s • ${activeSigil.purpose}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.LightGray,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.cancelSigil() },
                        border = BorderStroke(1.dp, AlertInfraRed),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LÖSCHEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AlertInfraRed,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }

        // Sigil Selector Cards
        Text(
            text = "1. SIEGEL ZUM SCHMIEDEN WÄHLEN:",
            style = MaterialTheme.typography.labelMedium.copy(
                color = InfraGreenPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(SigilType.values()) { sigil ->
                val isSelected = sigil == selectedSigil
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(sigil.colorHex).copy(alpha = 0.25f) else Color(0xFF0F1518)
                    ),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) Color(sigil.colorHex) else InfraGreenBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { onSelectSigil(sigil) }
                        .testTag("sigil_card_${sigil.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = sigil.symbol, fontSize = 20.sp)
                            if (activeSigil == sigil) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Aktiv",
                                    tint = Color(sigil.colorHex),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = sigil.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(sigil.colorHex),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )

                        Text(
                            text = sigil.purpose,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.LightGray,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }

        // Interactive Canvas for Finger Tracing / Drawing Sigil
        Text(
            text = "2. INTERAKTIVES BANN-SIEGEL ZEICHNEN & RITUAL LADE ZUSTAND:",
            style = MaterialTheme.typography.labelMedium.copy(
                color = InfraGreenPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )

        InteractiveSigilDrawingCanvas(
            sigil = selectedSigil,
            isCasting = isCastingRitual,
            onSigilCompleted = {
                viewModel.castSigil(selectedSigil)
            }
        )

        // Description Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091014)),
            border = BorderStroke(1.dp, Color(selectedSigil.colorHex).copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🔮 RITUAL-WIRKUNG & EFFEKT:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(selectedSigil.colorHex),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = selectedSigil.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "Dauer des Schutzeffekts: ${selectedSigil.durationSeconds} Sekunden nach Manifestierung.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = InfraGreenTextPrimaryVariant,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        // Manifestation Button
        Button(
            onClick = { viewModel.castSigil(selectedSigil) },
            enabled = !isCastingRitual,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(selectedSigil.colorHex),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("manifest_sigil_button")
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isCastingRitual) "MANIFESTIERE RITUAL-SIEGEL..." else "⚡ ${selectedSigil.title.uppercase()} MANIFESTIEREN",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            )
        }
    }
}

@Composable
private fun InteractiveSigilDrawingCanvas(
    sigil: SigilType,
    isCasting: Boolean,
    onSigilCompleted: () -> Unit
) {
    val drawnPathPoints = remember { mutableStateListOf<Offset>() }
    var traceProgress by remember { mutableStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF05080A)),
        border = BorderStroke(1.5.dp, Color(sigil.colorHex)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(sigil) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                drawnPathPoints.clear()
                                drawnPathPoints.add(offset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                drawnPathPoints.add(change.position)
                                traceProgress = (drawnPathPoints.size / 50f).coerceIn(0f, 1f)
                            },
                            onDragEnd = {
                                if (drawnPathPoints.size >= 15) {
                                    onSigilCompleted()
                                }
                            }
                        )
                    }
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension / 2.4f) * pulseScale

                // 1. Outer Rotating Geometry Ring
                rotate(rotationAngle, center) {
                    drawCircle(
                        color = Color(sigil.colorHex).copy(alpha = 0.3f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color(sigil.colorHex).copy(alpha = 0.15f),
                        radius = radius * 0.85f,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Draw Sigil Geometry Lines based on type
                    when (sigil) {
                        SigilType.DEMON_BANISHING -> {
                            // Solomon's Hexagram Star
                            for (i in 0 until 6) {
                                val angle1 = Math.toRadians((i * 60).toDouble())
                                val angle2 = Math.toRadians(((i + 2) * 60).toDouble())
                                val p1 = Offset(
                                    (center.x + radius * 0.8f * cos(angle1)).toFloat(),
                                    (center.y + radius * 0.8f * sin(angle1)).toFloat()
                                )
                                val p2 = Offset(
                                    (center.x + radius * 0.8f * cos(angle2)).toFloat(),
                                    (center.y + radius * 0.8f * sin(angle2)).toFloat()
                                )
                                drawLine(
                                    color = Color(sigil.colorHex).copy(alpha = 0.5f),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        }
                        SigilType.DIMENSION_ANCHOR -> {
                            // Quantum Spiral Anchor
                            for (i in 0 until 12) {
                                val rSpiral = (radius * 0.8f) * (i / 12f)
                                val angle = Math.toRadians((i * 30).toDouble())
                                val p = Offset(
                                    (center.x + rSpiral * cos(angle)).toFloat(),
                                    (center.y + rSpiral * sin(angle)).toFloat()
                                )
                                drawCircle(
                                    color = Color(sigil.colorHex).copy(alpha = 0.6f),
                                    radius = 3.dp.toPx(),
                                    center = p
                                )
                            }
                        }
                        SigilType.ARCHANGEL_SHIELD -> {
                            // Aegis Cross Shield
                            drawLine(
                                color = Color(sigil.colorHex).copy(alpha = 0.6f),
                                start = Offset(center.x, center.y - radius * 0.8f),
                                end = Offset(center.x, center.y + radius * 0.8f),
                                strokeWidth = 3.dp.toPx()
                            )
                            drawLine(
                                color = Color(sigil.colorHex).copy(alpha = 0.6f),
                                start = Offset(center.x - radius * 0.7f, center.y - radius * 0.2f),
                                end = Offset(center.x + radius * 0.7f, center.y - radius * 0.2f),
                                strokeWidth = 3.dp.toPx()
                            )
                        }
                        SigilType.LIGHT_HARMONY -> {
                            // Pentagram Star
                            for (i in 0 until 5) {
                                val angle1 = Math.toRadians((i * 72 - 90).toDouble())
                                val angle2 = Math.toRadians(((i + 2) * 72 - 90).toDouble())
                                val p1 = Offset(
                                    (center.x + radius * 0.8f * cos(angle1)).toFloat(),
                                    (center.y + radius * 0.8f * sin(angle1)).toFloat()
                                )
                                val p2 = Offset(
                                    (center.x + radius * 0.8f * cos(angle2)).toFloat(),
                                    (center.y + radius * 0.8f * sin(angle2)).toFloat()
                                )
                                drawLine(
                                    color = Color(sigil.colorHex).copy(alpha = 0.6f),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        }
                        SigilType.QUANTUM_STABILIZER -> {
                            // Atom Matrix Rings
                            drawCircle(
                                color = Color(sigil.colorHex).copy(alpha = 0.5f),
                                radius = radius * 0.5f,
                                center = center,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }

                // 2. Draw User Touched Traced Lines
                if (drawnPathPoints.size > 1) {
                    val userPath = Path()
                    userPath.moveTo(drawnPathPoints.first().x, drawnPathPoints.first().y)
                    for (i in 1 until drawnPathPoints.size) {
                        userPath.lineTo(drawnPathPoints[i].x, drawnPathPoints[i].y)
                    }
                    drawPath(
                        path = userPath,
                        color = Color(sigil.colorHex),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 3. Casting Ritual Pulsing Burst Overlay
                if (isCasting) {
                    drawCircle(
                        color = Color(sigil.colorHex).copy(alpha = 0.4f),
                        radius = radius * 1.2f,
                        center = center
                    )
                }
            }

            // Foreground Instructional Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (drawnPathPoints.isEmpty() && !isCasting) {
                    Text(
                        text = "✍️ STREICHE DEINEN FINGER HIER, UM DAS SIEGEL ZU ZEICHNEN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(sigil.colorHex),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                } else if (isCasting) {
                    Text(
                        text = "⚡ RITUAL MANIFESTIERT...",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { drawnPathPoints.clear() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabDimensionsTransmitter(
    viewModel: GhostViewModel,
    activeDimension: DimensionPlane
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🌀 PARALLEL-DIMENSION WÄHLEN & FREQUENZ EINSTIMMEN:",
            style = MaterialTheme.typography.labelMedium.copy(
                color = InfraGreenPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )

        DimensionPlane.values().forEach { plane ->
            val isCurrent = plane == activeDimension
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color(plane.colorHex).copy(alpha = 0.2f) else Color(0xFF0A0F12)
                ),
                border = BorderStroke(
                    if (isCurrent) 2.dp else 1.dp,
                    if (isCurrent) Color(plane.colorHex) else InfraGreenBorder
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.switchDimensionPlane(plane) }
                    .testTag("dimension_card_${plane.id}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (plane) {
                                    DimensionPlane.MORTAL_PRIME -> "🌍"
                                    DimensionPlane.ETHERIAL_DRIFT -> "👻"
                                    DimensionPlane.INFERNUS_VOID -> "🔥"
                                    DimensionPlane.LIMBUS_ECLIPSE -> "🌌"
                                    DimensionPlane.QUANTUM_SINGULARITY -> "⚛️"
                                },
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = plane.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color(plane.colorHex),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "${plane.codeName} • ${plane.frequencyHz} Hz • Faktor ${plane.threatMultiplier}x",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        if (isCurrent) {
                            Surface(
                                color = Color(plane.colorHex),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "AKTIV",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = plane.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    )

                    Button(
                        onClick = { viewModel.switchDimensionPlane(plane) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrent) Color(plane.colorHex) else Color(0xFF142028),
                            contentColor = if (isCurrent) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isCurrent) "🌀 AKTUELL EINGESTIMMT" else "🌀 FREQUENZ EINSTIMMEN (${plane.frequencyHz} Hz)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabVaultAndStatus(
    viewModel: GhostViewModel,
    capturedCount: Int,
    activeRiftsCount: Int,
    isClosingDimension: Boolean,
    isCapturingEntity: Boolean,
    activeSigil: SigilType?,
    sigilTimerSeconds: Int
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "⚡ SPEKTRAL-TRESOR & RITUAL-SIEGEL STATUS:",
            style = MaterialTheme.typography.labelMedium.copy(
                color = InfraGreenPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )

        // Vault Overview Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF180A14)),
                border = BorderStroke(1.dp, Color(0xFFFF0055)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🔒", fontSize = 22.sp)
                    Text(
                        text = "$capturedCount ENTITÄTEN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFFF0055),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "Im Dämonen-Käfig",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray, fontSize = 10.sp)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1820)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🌀", fontSize = 22.sp)
                    Text(
                        text = "$activeRiftsCount PORTALE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF00E5FF),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "Aktive Risse",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray, fontSize = 10.sp)
                    )
                }
            }
        }

        // Quick Ritual Direct Action Buttons
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A120E)),
            border = BorderStroke(1.dp, InfraGreenBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🧙 DIRECT RITUAL SCHNELL-AKTIONEN:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = InfraGreenPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.closeDimensionRift() },
                        enabled = !isClosingDimension,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isClosingDimension) "VERSIEGELT..." else "🌀 RISS SCHLIESSEN",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = { viewModel.captureEntity() },
                        enabled = !isCapturingEntity,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0055), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isCapturingEntity) "BANNT..." else "⚡ DÄMON BANNING",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.spawnDimensionRift() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2B35), contentColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🔍 RISS ERZEUGEN",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = { viewModel.spawnDemonOrVampire() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A1018), contentColor = Color(0xFFFF0055)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "👹 DÄMON ORTEN",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
