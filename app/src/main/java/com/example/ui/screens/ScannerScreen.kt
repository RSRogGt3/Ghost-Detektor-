package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.util.ImageCaptureUtil
import android.widget.Toast
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CameraBackgroundView
import com.example.ui.components.EmfMeter
import com.example.ui.components.FilterMode
import com.example.ui.components.RadarScannerCanvas
import com.example.ui.components.RealtimeEmfLineChart
import com.example.ui.components.ScanLinesOverlay
import com.example.ui.viewmodel.GhostViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings

@Composable
fun ScannerScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val filterMode by viewModel.currentFilterMode.collectAsStateWithLifecycle()
    val emfLevel by viewModel.emfLevel.collectAsStateWithLifecycle()
    val emfHistory by viewModel.emfHistory.collectAsStateWithLifecycle()
    val dangerLevel by viewModel.dangerLevel.collectAsStateWithLifecycle()
    val frequencyKhz by viewModel.frequencyKhz.collectAsStateWithLifecycle()
    val radarBlips by viewModel.radarBlips.collectAsStateWithLifecycle()
    val showCrtOverlay by viewModel.showCrtOverlay.collectAsStateWithLifecycle()
    val isCameraEnabled by viewModel.isCameraBackgroundEnabled.collectAsStateWithLifecycle()
    val audioFeedbackEnabled by viewModel.audioFeedbackEnabled.collectAsStateWithLifecycle()
    val isSensorActive by viewModel.sensorManager.isSensorActive.collectAsStateWithLifecycle()
    val compassAzimuth by viewModel.compassAzimuth.collectAsStateWithLifecycle()
    val satelliteCount by viewModel.satelliteCount.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()

    var showSaveDialog by remember { mutableStateOf(false) }
    var captureName by remember { mutableStateOf("") }
    var captureLocation by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("scanner_screen")
    ) {
        // Live Camera Feed with Digital Infra-Green / Night-Vision Overlay
        CameraBackgroundView(
            modifier = Modifier.fillMaxSize(),
            primaryColor = filterMode.primaryColor,
            isEnabled = isCameraEnabled,
            imageCapture = imageCapture
        )

        // Animated Compass Overlay
        com.example.ui.components.GhostCompassOverlay(
            azimuth = compassAzimuth,
            emfLevel = emfLevel,
            primaryColor = filterMode.primaryColor,
            modifier = Modifier.fillMaxSize()
        )

        // Atmospheric Infrared Spectral Overlay
        Image(
            painter = painterResource(id = R.drawable.img_spectral_bg),
            contentDescription = "Infrarot Nachtsicht Hintergrund",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f),
            contentScale = ContentScale.Crop
        )

        // Spectral Color Tint Layer based on active FilterMode
        val tintColor = when (filterMode) {
            FilterMode.INFRA_GREEN -> Color(0xFF00FF66).copy(alpha = 0.12f)
            FilterMode.THERMAL_RED -> Color(0xFFFF4400).copy(alpha = 0.15f)
            FilterMode.QUANTUM_MATRIX -> Color(0xFF00E5FF).copy(alpha = 0.12f)
            FilterMode.ULTRAVIOLET -> Color(0xFFBB33FF).copy(alpha = 0.15f)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tintColor)
        )

        // CRT Scanlines Overlay
        if (showCrtOverlay) {
            ScanLinesOverlay(
                modifier = Modifier.fillMaxSize(),
                lineColor = filterMode.primaryColor.copy(alpha = 0.08f)
            )
        }

        // Main Foreground Content Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HUD Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .border(1.dp, filterMode.primaryColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = filterMode.primaryColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = UiStrings.getHudTitle(appLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = filterMode.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = if (isSensorActive) UiStrings.getSensorHardwareActive(appLanguage) else UiStrings.getSensorAtmospheric(appLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = filterMode.primaryColor.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        )
                        if (satelliteCount > 0) {
                            Text(
                                text = "SATELLITEN UPLINK: $satelliteCount SAT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF00E5FF),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.toggleAudioFeedback() }) {
                        Icon(
                            imageVector = if (audioFeedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Audio Feedback Toggle",
                            tint = filterMode.primaryColor
                        )
                    }

                    IconButton(onClick = { viewModel.toggleScanning() }) {
                        Icon(
                            imageVector = if (isScanning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Scan Toggle",
                            tint = filterMode.primaryColor
                        )
                    }
                }
            }

            // Central Interactive Radar Scanner Canvas
            RadarScannerCanvas(
                blips = radarBlips,
                filterMode = filterMode,
                isScanning = isScanning
            )

            // EMF Feldstärke Meter Gauge
            EmfMeter(
                emfValue = emfLevel,
                dangerLevel = dangerLevel,
                frequencyKhz = frequencyKhz
            )

            // Real-Time Neon Green D3 Line Chart for Magnetic Field Fluctuations
            RealtimeEmfLineChart(
                dataPoints = emfHistory,
                currentEmf = emfLevel,
                isScanning = isScanning,
                lineColor = filterMode.primaryColor
            )

            // Filter Selector Chips ("Filteransicht")
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = filterMode.primaryColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SPECTRAL FILTERANSICHT:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterMode.values().forEach { mode ->
                        val isSelected = mode == filterMode
                        val activeColor = mode.primaryColor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) activeColor.copy(alpha = 0.25f)
                                    else Color.Black.copy(alpha = 0.6f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setFilterMode(mode) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) activeColor else Color.White.copy(alpha = 0.6f),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Capture Anomaly / Save Entity Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        showSaveDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("save_entity_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = filterMode.primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = UiStrings.getQuickCaptureBtn(appLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = {
                        imageCapture.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    coroutineScope.launch {
                                        val success = ImageCaptureUtil.saveTintedImageToGallery(context, image, filterMode.primaryColor)
                                        Toast.makeText(context, if (success) "Snapshot saved" else "Failed to save", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("capture_snapshot_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = filterMode.primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SNAPSHOT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    // Quick Capture Save Dialog
    if (showSaveDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showSaveDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF041C0E)),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = UiStrings.getSaveEntityTitle(appLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "EMF: $emfLevel mG | Frequenz $frequencyKhz kHz",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    OutlinedTextField(
                        value = captureName,
                        onValueChange = { captureName = it },
                        label = { Text(UiStrings.getEntityNameLabel(appLanguage), color = filterMode.primaryColor) },
                        placeholder = { Text("e.g. Shadow Entity #01", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = filterMode.primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = captureLocation,
                        onValueChange = { captureLocation = it },
                        label = { Text(UiStrings.getEntityLocationLabel(appLanguage), color = filterMode.primaryColor) },
                        placeholder = { Text("e.g. Attic Room 3", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = filterMode.primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showSaveDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text(UiStrings.getCancelBtn(appLanguage), color = Color.Gray, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.saveCurrentScanAsGhost(captureName, captureLocation)
                                captureName = ""
                                captureLocation = ""
                                showSaveDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = filterMode.primaryColor)
                        ) {
                            Text(UiStrings.getSaveBtn(appLanguage), color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
