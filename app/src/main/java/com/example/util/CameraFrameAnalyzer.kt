package com.example.util

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.data.AnomalyType
import com.example.data.CameraAnomaly
import java.util.Locale
import kotlin.math.abs

class CameraFrameAnalyzer(
    private val onAnomaliesDetected: (anomalies: List<CameraAnomaly>, avgLuminance: Float) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastYBuffer: ByteArray? = null
    private var lastWidth = 0
    private var lastHeight = 0
    private var lastAnalyzeTime = 0L

    override fun analyze(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastAnalyzeTime < 140) { // Limit to ~7 FPS for smooth performance
            imageProxy.close()
            return
        }
        lastAnalyzeTime = now

        try {
            val planes = imageProxy.planes
            if (planes.isNotEmpty()) {
                val yBuffer = planes[0].buffer
                val width = imageProxy.width
                val height = imageProxy.height
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride

                val ySize = yBuffer.remaining()
                val bytes = ByteArray(ySize)
                yBuffer.get(bytes)

                val gridCols = 8
                val gridRows = 8
                val cellWidth = width / gridCols
                val cellHeight = height / gridRows

                var totalLuminance = 0L
                val cellLuminance = FloatArray(gridCols * gridRows)
                val cellVariance = FloatArray(gridCols * gridRows)

                val prevBytes = lastYBuffer
                val hasPrev = prevBytes != null && lastWidth == width && lastHeight == height

                for (r in 0 until gridRows) {
                    for (c in 0 until gridCols) {
                        var sum = 0L
                        var motionSum = 0L
                        var count = 0

                        val startX = c * cellWidth
                        val startY = r * cellHeight

                        val stepX = (cellWidth / 5).coerceAtLeast(1)
                        val stepY = (cellHeight / 5).coerceAtLeast(1)

                        for (y in startY until (startY + cellHeight) step stepY) {
                            val rowOffset = y * rowStride
                            for (x in startX until (startX + cellWidth) step stepX) {
                                val idx = rowOffset + x * pixelStride
                                if (idx in bytes.indices) {
                                    val lum = bytes[idx].toInt() and 0xFF
                                    sum += lum
                                    count++

                                    if (hasPrev && idx in prevBytes!!.indices) {
                                        val prevLum = prevBytes[idx].toInt() and 0xFF
                                        motionSum += abs(lum - prevLum)
                                    }
                                }
                            }
                        }

                        val avgLum = if (count > 0) sum.toFloat() / count else 128f
                        val avgMotion = if (count > 0) motionSum.toFloat() / count else 0f

                        val gridIdx = r * gridCols + c
                        cellLuminance[gridIdx] = avgLum
                        cellVariance[gridIdx] = avgMotion
                        totalLuminance += sum
                    }
                }

                val frameAvgLum = (totalLuminance.toFloat() / (width * height)).coerceIn(1f, 255f)

                val anomalies = mutableListOf<CameraAnomaly>()

                for (r in 0 until gridRows) {
                    for (c in 0 until gridCols) {
                        val gridIdx = r * gridCols + c
                        val lum = cellLuminance[gridIdx]
                        val motion = cellVariance[gridIdx]

                        val lumDiff = lum - frameAvgLum
                        val absDiff = abs(lumDiff)

                        if (absDiff > 28f || motion > 20f) {
                            val xRatio = (c + 0.5f) / gridCols
                            val yRatio = (r + 0.5f) / gridRows

                            val intensity = ((absDiff / 120f) + (motion / 70f)).coerceIn(0.25f, 1.0f)

                            val type = when {
                                motion > 32f -> AnomalyType.MOTION_SHIFT
                                lumDiff > 40f -> AnomalyType.THERMAL_HOTSPOT
                                lumDiff < -40f -> AnomalyType.COLD_SPOT
                                lumDiff > 22f -> AnomalyType.BRIGHTNESS_SPIKE
                                else -> AnomalyType.SPECTRAL_FLARE
                            }

                            val tempDelta = when (type) {
                                AnomalyType.THERMAL_HOTSPOT -> "+${String.format(Locale.US, "%.1f", 1.8f + intensity * 5f)}°C"
                                AnomalyType.COLD_SPOT -> "-${String.format(Locale.US, "%.1f", 1.2f + intensity * 4f)}°C"
                                AnomalyType.MOTION_SHIFT -> "+${String.format(Locale.US, "%.1f", 0.8f + intensity * 3f)}°C"
                                AnomalyType.BRIGHTNESS_SPIKE -> "+${String.format(Locale.US, "%.1f", 2.0f + intensity * 3.5f)}°C"
                                AnomalyType.SPECTRAL_FLARE -> "+${String.format(Locale.US, "%.1f", 1.0f + intensity * 2.5f)}°C"
                            }

                            anomalies.add(
                                CameraAnomaly(
                                    id = "cam_anom_${r}_${c}",
                                    xRatio = xRatio,
                                    yRatio = yRatio,
                                    intensity = intensity,
                                    type = type,
                                    tempDeltaC = if (type == AnomalyType.COLD_SPOT) -(1.2f + intensity * 4f) else (1.5f + intensity * 4f),
                                    label = "${type.displayName} ($tempDelta)",
                                    emuValueMg = 15f + intensity * 65f
                                )
                            )
                        }
                    }
                }

                val topAnomalies = anomalies.sortedByDescending { it.intensity }.take(6)

                lastYBuffer = bytes
                lastWidth = width
                lastHeight = height

                onAnomaliesDetected(topAnomalies, frameAvgLum)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }
}
