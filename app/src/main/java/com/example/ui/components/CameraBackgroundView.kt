package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.CameraAnomaly
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.util.CameraFrameAnalyzer

/**
 * CameraBackgroundView renders a live CameraX feed in the background
 * with real-time frame image analysis for anomaly detection and digital spectral color matrix filter overlays.
 */
@Composable
fun CameraBackgroundView(
    modifier: Modifier = Modifier,
    primaryColor: Color = InfraGreenPrimary,
    filterMode: FilterMode = FilterMode.INFRA_GREEN,
    overlayAlpha: Float = 0.65f,
    filterIntensity: Float = 0.70f,
    isEnabled: Boolean = true,
    isFlashlightEnabled: Boolean = false,
    imageCapture: androidx.camera.core.ImageCapture? = null,
    onAnomaliesDetected: (anomalies: List<CameraAnomaly>, avgLuminance: Float) -> Unit = { _, _ -> }
) {
    val baseContext = LocalContext.current
    val context = baseContext
    val lifecycleOwner = LocalLifecycleOwner.current

    var boundCamera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(isEnabled) {
        if (isEnabled && !hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Toggle hardware torch / camera flashlight on camera control
    LaunchedEffect(boundCamera, isFlashlightEnabled) {
        val cam = boundCamera
        if (cam != null) {
            try {
                if (cam.cameraInfo.hasFlashUnit()) {
                    cam.cameraControl.enableTorch(isFlashlightEnabled)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Cleanup camera when isEnabled becomes false or when composable leaves composition
    DisposableEffect(lifecycleOwner, isEnabled, hasCameraPermission) {
        onDispose {
            try {
                val providerFuture = ProcessCameraProvider.getInstance(context.applicationContext)
                if (providerFuture.isDone) {
                    providerFuture.get().unbindAll()
                } else {
                    providerFuture.addListener({
                        try {
                            providerFuture.get().unbindAll()
                        } catch (_: Exception) {}
                    }, ContextCompat.getMainExecutor(context))
                }
            } catch (_: Exception) {}
            boundCamera = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isEnabled && hasCameraPermission) {
            NightVisionShaderOverlay(
                isActive = true,
                gainLevelDb = 12f + filterIntensity * 8f,
                filterMode = filterMode,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx.applicationContext)

                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()
                                        .also { analysis ->
                                            analysis.setAnalyzer(
                                                ContextCompat.getMainExecutor(ctx),
                                                CameraFrameAnalyzer { anomalies, avgLum ->
                                                    onAnomaliesDetected(anomalies, avgLum)
                                                }
                                            )
                                        }

                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                    cameraProvider.unbindAll()
                                    boundCamera = if (imageCapture != null) {
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis,
                                            imageCapture
                                        )
                                    } else {
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis
                                        )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Spectral Color Base Wash tailored to active FilterMode
                    val baseSpectralWash = when (filterMode) {
                        FilterMode.INFRA_GREEN -> Color(0xFF002208)
                        FilterMode.THERMAL_RED -> Color(0xFF2B0A00)
                        FilterMode.QUANTUM_MATRIX -> Color(0xFF001F2B)
                        FilterMode.ULTRAVIOLET -> Color(0xFF1C002B)
                        FilterMode.INFRA_YELLOW -> Color(0xFF2B2200)
                        FilterMode.INFRA_BLUE -> Color(0xFF001B2B)
                        FilterMode.INFRARED -> Color(0xFF2B0000)
                    }

                    // 1. Monochromatic spectral color wash
                    val computedWashAlpha = (overlayAlpha * (0.25f + filterIntensity * 0.65f)).coerceIn(0.05f, 0.90f)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(baseSpectralWash.copy(alpha = computedWashAlpha))
                    )

                    // 2. High-contrast thermal spectral glow blend layer
                    val computedGlowAlpha = (0.05f + filterIntensity * 0.25f).coerceIn(0.02f, 0.75f)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(primaryColor.copy(alpha = computedGlowAlpha))
                    )

                    // 3. Focused Tactical Flashlight Beam Overlay
                    FlashlightConeOverlay(
                        isFlashlightActive = isFlashlightEnabled,
                        primaryColor = primaryColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Dark atmospheric fallback when camera is disabled or permission denied
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF040F06))
            )
        }
    }
}

/**
 * Quick toggle control card for Camera Infra-Green Background view in Settings or Scanner
 */
@Composable
fun CameraPermissionCard(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
        border = CardDefaults.outlinedCardBorder(enabled = true),
        modifier = modifier.fillMaxWidth().testTag("camera_permission_card")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (hasPermission) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    contentDescription = null,
                    tint = InfraGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "INFRA-GRÜN KAMERA-FEED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (hasPermission) "Live Kamera-Hintergrund Aktiv" else "Kamera-Zugriff für Nachtsicht-Feed erforderlich",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            if (!hasPermission) {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "FREIGEBEN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
