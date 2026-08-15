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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class GhostSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    // Hardware Sensors
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val gyroscope: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val lightSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val pressureSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)
    private val proximitySensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    private val tempSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    private val humiditySensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

    private val gravityValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasMagnetic = false

    // StateFlows for Sensor Telemetry
    private val _sensorEmfStrength = MutableStateFlow(2.4f)
    val sensorEmfStrength: StateFlow<Float> = _sensorEmfStrength.asStateFlow()

    private val _motionIntensity = MutableStateFlow(0.2f)
    val motionIntensity: StateFlow<Float> = _motionIntensity.asStateFlow()

    private val _gyroSpeed = MutableStateFlow(0.1f)
    val gyroSpeed: StateFlow<Float> = _gyroSpeed.asStateFlow()

    private val _lightLux = MutableStateFlow(120f)
    val lightLux: StateFlow<Float> = _lightLux.asStateFlow()

    private val _pressureHpa = MutableStateFlow(1013.25f)
    val pressureHpa: StateFlow<Float> = _pressureHpa.asStateFlow()

    private val _proximityCm = MutableStateFlow(5f)
    val proximityCm: StateFlow<Float> = _proximityCm.asStateFlow()

    private val _ambientTempC = MutableStateFlow(21.5f)
    val ambientTempC: StateFlow<Float> = _ambientTempC.asStateFlow()

    private val _compassAzimuth = MutableStateFlow(45f)
    val compassAzimuth: StateFlow<Float> = _compassAzimuth.asStateFlow()

    private val _isSensorActive = MutableStateFlow(true)
    val isSensorActive: StateFlow<Boolean> = _isSensorActive.asStateFlow()

    private val _isDeviceConnected = MutableStateFlow(true)
    val isDeviceConnected: StateFlow<Boolean> = _isDeviceConnected.asStateFlow()

    private val _activeSensorCount = MutableStateFlow(6)
    val activeSensorCount: StateFlow<Int> = _activeSensorCount.asStateFlow()

    private val _activeSensorNames = MutableStateFlow<List<String>>(
        listOf("ACCEL", "MAGNET", "GYRO", "LUX", "BARO", "GPS")
    )
    val activeSensorNames: StateFlow<List<String>> = _activeSensorNames.asStateFlow()

    // Satellite Support States
    private val _satelliteCount = MutableStateFlow(8)
    val satelliteCount: StateFlow<Int> = _satelliteCount.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private var lastAccelMagnitude = 9.81f
    private var isListening = false
    private var isBatterySaverActive = false
    private val applicationContext = context.applicationContext

    private val scope = CoroutineScope(Dispatchers.Default)
    private var fallbackSimulationJob: Job? = null

    fun setBatterySaverMode(enabled: Boolean) {
        if (isBatterySaverActive == enabled) return
        isBatterySaverActive = enabled
        if (isListening) {
            stopListening()
            startListening()
        }
    }

    val isBatterySaverModeActive: Boolean get() = isBatterySaverActive

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _currentLocation.value = location
            val simulatedSats = (14 - (location.accuracy / 8).coerceIn(0f, 10f)).toInt().coerceAtLeast(6)
            _satelliteCount.value = simulatedSats
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun startListening() {
        if (isListening) return
        var count = 0
        val names = mutableListOf<String>()

        val sensorDelay = if (isBatterySaverActive) SensorManager.SENSOR_DELAY_NORMAL else SensorManager.SENSOR_DELAY_UI
        val gpsInterval = if (isBatterySaverActive) 15000L else 4000L

        accelerometer?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("ACCEL")
            }
        }

        magnetometer?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("MAGNET")
            }
        }

        gyroscope?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("GYRO")
            }
        }

        lightSensor?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("LUX")
            }
        }

        pressureSensor?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("BARO")
            }
        }

        proximitySensor?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("PROX")
            }
        }

        tempSensor?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("TEMP")
            }
        }

        humiditySensor?.let {
            if (sensorManager?.registerListener(this, it, sensorDelay) == true) {
                count++
                names.add("HUMID")
            }
        }

        try {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || 
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    gpsInterval,
                    1f,
                    locationListener
                )
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    gpsInterval,
                    1f,
                    locationListener
                )
                count++
                names.add("GPS")
            }
        } catch (_: Exception) {}

        // Fallback / Hybrid Synthesis: If running in emulator or some hardware sensors are missing,
        // bridge the remaining channels so all 8 detection layers operate seamlessly.
        if (!names.contains("MAGNET")) {
            names.add("QUANTUM-MAG")
            count++
        }
        if (!names.contains("ACCEL")) {
            names.add("INERTIAL")
            count++
        }
        if (!names.contains("GPS")) {
            names.add("GRID-LOC")
            count++
        }

        _activeSensorCount.value = count.coerceAtLeast(4)
        _activeSensorNames.value = names
        _isSensorActive.value = true
        _isDeviceConnected.value = true
        isListening = true

        startFallbackTelemetryLoop()
    }

    private fun startFallbackTelemetryLoop() {
        fallbackSimulationJob?.cancel()
        fallbackSimulationJob = scope.launch {
            var tick = 0L
            while (isActive) {
                tick++
                if (!hasMagnetic) {
                    val angleRad = (tick * 0.1)
                    val baseMag = 2.2f + 0.6f * sin(angleRad).toFloat() + Random.nextFloat() * 0.3f
                    _sensorEmfStrength.value = baseMag.coerceIn(1.0f, 9.9f)
                }
                if (!hasGravity) {
                    _compassAzimuth.value = (_compassAzimuth.value + 0.5f) % 360f
                }
                delay(if (isBatterySaverActive) 1000L else 300L)
            }
        }
    }

    fun stopListening() {
        if (!isListening) return
        sensorManager?.unregisterListener(this)
        try {
            locationManager?.removeUpdates(locationListener)
        } catch (_: Exception) {}
        fallbackSimulationJob?.cancel()
        _isSensorActive.value = false
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magneticValues, 0, minOf(event.values.size, 3))
                hasMagnetic = true

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magTesla = sqrt(x * x + y * y + z * z)
                
                val calculatedEmf = (magTesla / 10f).coerceIn(1.0f, 9.9f)
                _sensorEmfStrength.value = calculatedEmf
            }

            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityValues, 0, minOf(event.values.size, 3))
                hasGravity = true

                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]
                val currentAccel = sqrt(ax * ax + ay * ay + az * az)
                
                val deltaAccel = abs(currentAccel - lastAccelMagnitude)
                lastAccelMagnitude = currentAccel

                _motionIntensity.value = deltaAccel.coerceIn(0f, 10f)

                if (magnetometer == null) {
                    val motionEmf = (2.0f + deltaAccel * 1.5f).coerceIn(1.0f, 9.9f)
                    _sensorEmfStrength.value = motionEmf
                }
            }

            Sensor.TYPE_GYROSCOPE -> {
                val gx = event.values[0]
                val gy = event.values[1]
                val gz = event.values[2]
                val gyroMag = sqrt(gx * gx + gy * gy + gz * gz)
                _gyroSpeed.value = gyroMag
            }

            Sensor.TYPE_LIGHT -> {
                _lightLux.value = event.values[0]
            }

            Sensor.TYPE_PRESSURE -> {
                _pressureHpa.value = event.values[0]
            }

            Sensor.TYPE_PROXIMITY -> {
                _proximityCm.value = event.values[0]
            }

            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                _ambientTempC.value = event.values[0]
            }
        }

        if (hasGravity && hasMagnetic) {
            val rMatrix = FloatArray(9)
            val iMatrix = FloatArray(9)
            if (SensorManager.getRotationMatrix(rMatrix, iMatrix, gravityValues, magneticValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rMatrix, orientation)
                val azimuthInRadians = orientation[0]
                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                val azimuth = (azimuthInDegrees + 360f) % 360f
                _compassAzimuth.value = azimuth
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
