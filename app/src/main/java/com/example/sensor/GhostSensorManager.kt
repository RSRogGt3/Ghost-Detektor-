package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

class GhostSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravityValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasMagnetic = false

    private val _sensorEmfStrength = MutableStateFlow(2.5f)
    val sensorEmfStrength: StateFlow<Float> = _sensorEmfStrength.asStateFlow()

    private val _motionIntensity = MutableStateFlow(0f)
    val motionIntensity: StateFlow<Float> = _motionIntensity.asStateFlow()

    private val _compassAzimuth = MutableStateFlow(0f)
    val compassAzimuth: StateFlow<Float> = _compassAzimuth.asStateFlow()

    private val _isSensorActive = MutableStateFlow(false)
    val isSensorActive: StateFlow<Boolean> = _isSensorActive.asStateFlow()

    // Satellite Support States
    private val _satelliteCount = MutableStateFlow(0)
    val satelliteCount: StateFlow<Int> = _satelliteCount.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private var lastAccelMagnitude = 9.81f
    private var isListening = false
    private val applicationContext = context.applicationContext

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _currentLocation.value = location
            // Simulate satellite count fluctuating based on accuracy
            val simulatedSats = (12 - (location.accuracy / 10).coerceIn(0f, 10f)).toInt().coerceAtLeast(3)
            _satelliteCount.value = simulatedSats
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun startListening() {
        if (isListening) return
        var registeredAny = false

        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            registeredAny = true
        }

        magnetometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            registeredAny = true
        }
        
        try {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || 
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000L,
                    1f,
                    locationListener
                )
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    1f,
                    locationListener
                )
            }
        } catch (e: Exception) {
            // Location access denied or unavailable
        }

        _isSensorActive.value = registeredAny
        isListening = registeredAny
    }

    fun stopListening() {
        if (!isListening) return
        sensorManager?.unregisterListener(this)
        try {
            locationManager?.removeUpdates(locationListener)
        } catch (e: Exception) {
            // Ignored
        }
        _isSensorActive.value = false
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magneticValues, 0, event.values.size)
                hasMagnetic = true

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magTesla = sqrt(x * x + y * y + z * z) // Total magnetic magnitude in uT
                
                // Earth's magnetic field is typically 25 to 65 uT. Map to EMF level scale (1.0 to 9.9 mG)
                // Normalize and add variation
                val calculatedEmf = (magTesla / 10f).coerceIn(1.0f, 9.9f)
                _sensorEmfStrength.value = calculatedEmf
            }

            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityValues, 0, event.values.size)
                hasGravity = true

                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]
                val currentAccel = sqrt(ax * ax + ay * ay + az * az)
                
                val deltaAccel = abs(currentAccel - lastAccelMagnitude)
                lastAccelMagnitude = currentAccel

                // Motion intensity drives extra fluctuations when shaking/moving device
                _motionIntensity.value = deltaAccel.coerceIn(0f, 10f)

                // If magnetometer is unavailable or returning static values, accelerometer motion influences EMF reading directly
                if (magnetometer == null) {
                    val motionEmf = (2.0f + deltaAccel * 1.5f).coerceIn(1.0f, 9.9f)
                    _sensorEmfStrength.value = motionEmf
                }
            }
        }

        if (hasGravity && hasMagnetic) {
            val rMatrix = FloatArray(9)
            val iMatrix = FloatArray(9)
            if (SensorManager.getRotationMatrix(rMatrix, iMatrix, gravityValues, magneticValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rMatrix, orientation)
                // Azimuth in radians
                val azimuthInRadians = orientation[0]
                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                // Convert to 0-360
                val azimuth = (azimuthInDegrees + 360f) % 360f
                _compassAzimuth.value = azimuth
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op for ghost scanner
    }
}
