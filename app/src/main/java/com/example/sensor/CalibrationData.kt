package com.example.sensor

data class CalibrationTelemetry(
    val isAutoCalibrationEnabled: Boolean = true,
    val isCalibrating: Boolean = false,
    val calibrationProgress: Float = 1.0f,
    val currentStepText: String = "BEREIT / VERMESSEN",
    val calibratedEmfBaseline: Float = 1.8f,
    val rawEmf: Float = 1.8f,
    val netEmf: Float = 0.0f,
    val magneticNoiseRms: Float = 0.04f,
    val calibrationQualityPercent: Int = 100,
    val lastCalibrationTime: Long = System.currentTimeMillis(),
    val totalCalibrationsCount: Int = 1,
    val autoTrackingDriftFilterActive: Boolean = true
)
