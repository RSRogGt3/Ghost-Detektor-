package com.example.ui.viewmodel

import com.example.data.DimensionPlane
import com.example.data.SigilType
import kotlinx.coroutines.flow.map

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.SpiritAiEngine
import com.example.audio.MicrophoneAudioAnalyzer
import com.example.audio.SoundManager
import com.example.audio.SpiritTtsManager
import com.example.data.GhostDatabase
import com.example.data.GhostDetectionEntity
import com.example.data.GhostRepository
import com.example.data.SpiritLogEntry
import com.example.sensor.GhostSensorManager
import com.example.ui.components.FilterMode
import com.example.ui.components.RadarBlip
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class MagnetLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val emfValue: Float,
    val sourceTag: String,
    val isShielded: Boolean,
    val noteText: String
)

class GhostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GhostRepository
    val soundManager = SoundManager()
    val spiritTtsManager = SpiritTtsManager(application)
    val microphoneAnalyzer = MicrophoneAudioAnalyzer(application)
    private val spiritAiEngine = SpiritAiEngine()
    private val _appThemeColor = MutableStateFlow("GREEN")
    val appThemeColor: StateFlow<String> = _appThemeColor

    fun setAppThemeColor(color: String) {
        _appThemeColor.value = color
    }
    val sensorManager = GhostSensorManager(application)
    
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
    }

    // Shared Preferences & Security Lock State
    private val securitySharedPrefs = application.getSharedPreferences("ghost_app_security_prefs", android.content.Context.MODE_PRIVATE)

    // Room DB Flow
    val allDetections: StateFlow<List<GhostDetectionEntity>>

    private val _appLanguage = MutableStateFlow(
        AppLanguage.fromCode(securitySharedPrefs.getString("app_language", "de") ?: "de")
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        securitySharedPrefs.edit().putString("app_language", language.code).apply()
        spiritTtsManager.setLanguage(language)
    }

    // Filter & Search state for History ("Verlauf")
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("ALLE")
    val selectedTypeFilter: StateFlow<String> = _selectedTypeFilter.asStateFlow()

    private val _favoritesOnlyFilter = MutableStateFlow(false)
    val favoritesOnlyFilter: StateFlow<Boolean> = _favoritesOnlyFilter.asStateFlow()

    val filteredDetections: StateFlow<List<GhostDetectionEntity>>

    // Scanner UI State
    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _currentFilterMode = MutableStateFlow(FilterMode.INFRA_GREEN)
    val currentFilterMode: StateFlow<FilterMode> = _currentFilterMode.asStateFlow()

    private val _emfLevel = MutableStateFlow(3.2f)
    val emfLevel: StateFlow<Float> = _emfLevel.asStateFlow()

    private val _emfHistory = MutableStateFlow<List<Float>>(
        List(30) { 2.0f + Random.nextFloat() * 1.5f }
    )
    val emfHistory: StateFlow<List<Float>> = _emfHistory.asStateFlow()

    private val _dangerLevel = MutableStateFlow(2)
    val dangerLevel: StateFlow<Int> = _dangerLevel.asStateFlow()

    private val _frequencyKhz = MutableStateFlow(42.5f)
    val frequencyKhz: StateFlow<Float> = _frequencyKhz.asStateFlow()

    private val _radarBlips = MutableStateFlow<List<RadarBlip>>(emptyList())
    val radarBlips: StateFlow<List<RadarBlip>> = _radarBlips.asStateFlow()

    private val _cameraAnomalies = MutableStateFlow<List<com.example.data.CameraAnomaly>>(emptyList())
    val cameraAnomalies: StateFlow<List<com.example.data.CameraAnomaly>> = _cameraAnomalies.asStateFlow()

    private val _isAutoDestroyEnabled = MutableStateFlow(true)
    val isAutoDestroyEnabled: StateFlow<Boolean> = _isAutoDestroyEnabled.asStateFlow()

    private val _isLiberatingAnomalies = MutableStateFlow(false)
    val isLiberatingAnomalies: StateFlow<Boolean> = _isLiberatingAnomalies.asStateFlow()

    private val _capturedCount = MutableStateFlow(0)
    val capturedCount: StateFlow<Int> = _capturedCount.asStateFlow()

    private val _activeDimensionPlane = MutableStateFlow(com.example.data.DimensionPlane.MORTAL_PRIME)
    val activeDimensionPlane: StateFlow<com.example.data.DimensionPlane> = _activeDimensionPlane.asStateFlow()

    private val _activeSigil = MutableStateFlow<com.example.data.SigilType?>(null)
    val activeSigil: StateFlow<com.example.data.SigilType?> = _activeSigil.asStateFlow()

    private val _sigilTimerSeconds = MutableStateFlow(0)
    val sigilTimerSeconds: StateFlow<Int> = _sigilTimerSeconds.asStateFlow()

    private val _isCastingSigilRitual = MutableStateFlow(false)
    val isCastingSigilRitual: StateFlow<Boolean> = _isCastingSigilRitual.asStateFlow()

    private var sigilTimerJob: Job? = null

    val activeRiftsCount: StateFlow<Int> = _radarBlips.map { blips ->
        blips.count { it.category == com.example.ui.components.EntityCategory.DIMENSION_RIFT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isClosingDimension = MutableStateFlow(false)
    val isClosingDimension: StateFlow<Boolean> = _isClosingDimension.asStateFlow()

    private val _isCapturingEntity = MutableStateFlow(false)
    val isCapturingEntity: StateFlow<Boolean> = _isCapturingEntity.asStateFlow()

    private val _liberatedBannerMessage = MutableStateFlow<String?>(null)
    val liberatedBannerMessage: StateFlow<String?> = _liberatedBannerMessage.asStateFlow()

    val demonVampireCount: StateFlow<Int> = combine(
        _radarBlips,
        _cameraAnomalies
    ) { blips, anomalies ->
        val demonVampireBlips = blips.count {
            it.category == com.example.ui.components.EntityCategory.DEMON ||
            it.category == com.example.ui.components.EntityCategory.VAMPIRE
        }
        val demonVampireAnomalies = anomalies.count {
            it.label.contains("DÄMON", ignoreCase = true) ||
            it.label.contains("VAMPIR", ignoreCase = true)
        }
        demonVampireBlips + demonVampireAnomalies
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val hasHighDemonVampireConcentration: StateFlow<Boolean> = combine(
        demonVampireCount,
        _emfLevel
    ) { count, emf ->
        count >= 1 || (emf >= 6.5f && count > 0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _cameraAvgLuminance = MutableStateFlow(128f)
    val cameraAvgLuminance: StateFlow<Float> = _cameraAvgLuminance.asStateFlow()

    val compassAzimuth: StateFlow<Float> = sensorManager.compassAzimuth
    val activeSensorCount: StateFlow<Int> = sensorManager.activeSensorCount
    val activeSensorNames: StateFlow<List<String>> = sensorManager.activeSensorNames
    val gyroSpeed: StateFlow<Float> = sensorManager.gyroSpeed
    val lightLux: StateFlow<Float> = sensorManager.lightLux
    val pressureHpa: StateFlow<Float> = sensorManager.pressureHpa
    val proximityCm: StateFlow<Float> = sensorManager.proximityCm
    val ambientTempC: StateFlow<Float> = sensorManager.ambientTempC
    
    val satelliteCount: StateFlow<Int> = sensorManager.satelliteCount
    val currentLocation: StateFlow<android.location.Location?> = sensorManager.currentLocation

    private val _showCrtOverlay = MutableStateFlow(true)
    val showCrtOverlay: StateFlow<Boolean> = _showCrtOverlay.asStateFlow()

    private val _isCameraBackgroundEnabled = MutableStateFlow(true)
    val isCameraBackgroundEnabled: StateFlow<Boolean> = _isCameraBackgroundEnabled.asStateFlow()

    private val _isFlashlightEnabled = MutableStateFlow(false)
    val isFlashlightEnabled: StateFlow<Boolean> = _isFlashlightEnabled.asStateFlow()

    private val _audioFeedbackEnabled = MutableStateFlow(true)
    val audioFeedbackEnabled: StateFlow<Boolean> = _audioFeedbackEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _vibrationIntensity = MutableStateFlow(0.80f)
    val vibrationIntensity: StateFlow<Float> = _vibrationIntensity.asStateFlow()

    private val _filterIntensity = MutableStateFlow(0.70f)
    val filterIntensity: StateFlow<Float> = _filterIntensity.asStateFlow()

    // Spirit Box UI State
    private val _spiritQuestion = MutableStateFlow("")
    val spiritQuestion: StateFlow<String> = _spiritQuestion.asStateFlow()

    private val _spiritResponse = MutableStateFlow("")
    val spiritResponse: StateFlow<String> = _spiritResponse.asStateFlow()

    private val _isGeneratingSpiritResponse = MutableStateFlow(false)
    val isGeneratingSpiritResponse: StateFlow<Boolean> = _isGeneratingSpiritResponse.asStateFlow()

    private val _autoSpiritBoxEnabled = MutableStateFlow(false)
    val autoSpiritBoxEnabled: StateFlow<Boolean> = _autoSpiritBoxEnabled.asStateFlow()

    private val _isRealtimeSweepActive = MutableStateFlow(false)
    val isRealtimeSweepActive: StateFlow<Boolean> = _isRealtimeSweepActive.asStateFlow()

    private val _realtimeSweepSpeedMs = MutableStateFlow(200L)
    val realtimeSweepSpeedMs: StateFlow<Long> = _realtimeSweepSpeedMs.asStateFlow()

    private var realtimeSweepJob: Job? = null

    private val _spiritPhraseLog = MutableStateFlow<List<SpiritLogEntry>>(
        listOf(
            SpiritLogEntry(
                timestamp = System.currentTimeMillis() - 45000,
                question = "Sensor-Ätherabtastung",
                phrase = "Schatten... wir wachen in der Kälte.",
                emfLevel = 6.2f,
                dangerLevel = 3
            ),
            SpiritLogEntry(
                timestamp = System.currentTimeMillis() - 150000,
                question = "Bist du bei uns im Raum?",
                phrase = "Ich sehe dein Licht... erlöse mich.",
                emfLevel = 4.5f,
                dangerLevel = 2
            ),
            SpiritLogEntry(
                timestamp = System.currentTimeMillis() - 320000,
                question = "Sensor-Ätherabtastung",
                phrase = "Uralte Stimmen... vor langer Zeit verlassen.",
                emfLevel = 3.1f,
                dangerLevel = 1
            )
        )
    )
    val spiritPhraseLog: StateFlow<List<SpiritLogEntry>> = _spiritPhraseLog.asStateFlow()

    fun respeakSpiritLogEntry(entry: SpiritLogEntry) {
        spiritTtsManager.speakSpiritBoxAudio(
            text = entry.phrase,
            emfLevel = entry.emfLevel,
            dangerLevel = entry.dangerLevel,
            soundManager = soundManager
        )
        triggerEntityDetectionVibration(entry.dangerLevel)
    }

    private val _showSpiritLogOverlay = MutableStateFlow(false)
    val showSpiritLogOverlay: StateFlow<Boolean> = _showSpiritLogOverlay.asStateFlow()

    fun toggleSpiritLogOverlay(show: Boolean) {
        _showSpiritLogOverlay.value = show
    }

    fun clearSpiritLog() {
        _spiritPhraseLog.value = emptyList()
    }

    // Magnet Shield & Attack Defense State (TV/Monitor EMI Neutralizer)
    private val _isMagnetShieldActive = MutableStateFlow(true)
    val isMagnetShieldActive: StateFlow<Boolean> = _isMagnetShieldActive.asStateFlow()

    private val _isEmfSuppressionActive = MutableStateFlow(true)
    val isEmfSuppressionActive: StateFlow<Boolean> = _isEmfSuppressionActive.asStateFlow()

    val isTtsMuted: StateFlow<Boolean> = spiritTtsManager.isMuted
    val isSystemSpeechEnabled: StateFlow<Boolean> = spiritTtsManager.isSystemSpeechEnabled
    val ttsVolume: StateFlow<Float> = spiritTtsManager.speechVolume
    val ttsPitch: StateFlow<Float> = spiritTtsManager.pitch
    val ttsSpeechRate: StateFlow<Float> = spiritTtsManager.speechRate
    val ttsVoicePersona: StateFlow<com.example.audio.VoicePersona> = spiritTtsManager.currentPersona

    fun toggleTtsMute() {
        spiritTtsManager.toggleMute()
    }

    fun setTtsMuted(muted: Boolean) {
        spiritTtsManager.setMuted(muted)
    }

    fun toggleSystemSpeechEnabled() {
        spiritTtsManager.toggleSystemSpeechEnabled()
    }

    fun setTtsVolume(volume: Float) {
        spiritTtsManager.setSpeechVolume(volume)
    }

    fun setTtsPitch(pitch: Float) {
        spiritTtsManager.setPitch(pitch)
    }

    fun setTtsSpeechRate(rate: Float) {
        spiritTtsManager.setSpeechRate(rate)
    }

    fun setTtsVoicePersona(persona: com.example.audio.VoicePersona) {
        spiritTtsManager.setVoicePersona(persona)
    }

    fun testSpiritVoice() {
        spiritTtsManager.speakSpiritBoxAudio(
            text = "Hallo! Ich bin deine Gemini AI Stimme auf der Spirit Box Frequenz.",
            emfLevel = _emfLevel.value,
            dangerLevel = _dangerLevel.value,
            soundManager = soundManager
        )
    }


    private val _magnetLogNotes = MutableStateFlow<List<MagnetLogEntry>>(
        listOf(
            MagnetLogEntry(
                timestamp = System.currentTimeMillis() - 45000,
                emfValue = 7.8f,
                sourceTag = "Fernseher / PC-Monitor Störung",
                isShielded = true,
                noteText = "TV 60Hz Magnetfeld-Spitze durch Schild abgefangen"
            ),
            MagnetLogEntry(
                timestamp = System.currentTimeMillis() - 140000,
                emfValue = 6.4f,
                sourceTag = "PC-Monitor Strahlung",
                isShielded = true,
                noteText = "Elektromagnetische Monitorfrequenz harmonisiert"
            )
        )
    )
    val magnetLogNotes: StateFlow<List<MagnetLogEntry>> = _magnetLogNotes.asStateFlow()

    // Auto-Filter Rotation (5 Min Interval) State
    private val _autoFilterRotationEnabled = MutableStateFlow(true)
    val autoFilterRotationEnabled: StateFlow<Boolean> = _autoFilterRotationEnabled.asStateFlow()
    private var autoFilterRotationJob: Job? = null

    // Auto Capture & Auto Liberation State
    private val _autoCaptureLiberateEnabled = MutableStateFlow(true)
    val autoCaptureLiberateEnabled: StateFlow<Boolean> = _autoCaptureLiberateEnabled.asStateFlow()

    // Background 24/7 Scan State
    private val _backgroundScan247Enabled = MutableStateFlow(true)
    val backgroundScan247Enabled: StateFlow<Boolean> = _backgroundScan247Enabled.asStateFlow()

    // Battery Saver Mode State (Akkusparmodus für Hintergrundbetrieb)
    private val _isBatterySaverEnabled = MutableStateFlow(securitySharedPrefs.getBoolean("battery_saver_enabled", true))
    val isBatterySaverEnabled: StateFlow<Boolean> = _isBatterySaverEnabled.asStateFlow()

    private val _isScreenOff = MutableStateFlow(false)
    val isScreenOff: StateFlow<Boolean> = _isScreenOff.asStateFlow()

    private val _isBatterySaverThrottling = MutableStateFlow(false)
    val isBatterySaverThrottling: StateFlow<Boolean> = _isBatterySaverThrottling.asStateFlow()

    private val screenStateReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
            when (intent?.action) {
                android.content.Intent.ACTION_SCREEN_OFF -> {
                    _isScreenOff.value = true
                    updateBatterySaverThrottling()
                }
                android.content.Intent.ACTION_SCREEN_ON -> {
                    _isScreenOff.value = false
                    updateBatterySaverThrottling()
                }
                android.os.PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> {
                    updateBatterySaverThrottling()
                }
            }
        }
    }

    fun updateBatterySaverThrottling() {
        val powerManager = getApplication<Application>().getSystemService(android.content.Context.POWER_SERVICE) as? android.os.PowerManager
        val isPowerSave = powerManager?.isPowerSaveMode == true
        val screenOff = _isScreenOff.value
        val enabled = _isBatterySaverEnabled.value

        val shouldThrottle = enabled && (screenOff || isPowerSave)
        _isBatterySaverThrottling.value = shouldThrottle
        sensorManager.setBatterySaverMode(shouldThrottle)
    }

    fun toggleBatterySaverEnabled() {
        val newState = !_isBatterySaverEnabled.value
        _isBatterySaverEnabled.value = newState
        securitySharedPrefs.edit().putBoolean("battery_saver_enabled", newState).apply()
        updateBatterySaverThrottling()
    }

    // App Access Protection & Security Lock State (Benutzer-Zugriffsschutz)
    private val _isSecurityEnabled = MutableStateFlow(securitySharedPrefs.getBoolean("security_enabled", true))
    val isSecurityEnabled: StateFlow<Boolean> = _isSecurityEnabled.asStateFlow()

    private val _isPinSetupDone = MutableStateFlow(securitySharedPrefs.getBoolean("is_pin_setup_done", false))
    val isPinSetupDone: StateFlow<Boolean> = _isPinSetupDone.asStateFlow()

    private val _userPin = MutableStateFlow(securitySharedPrefs.getString("user_pin", "") ?: "")
    val userPin: StateFlow<String> = _userPin.asStateFlow()

    private val _recoveryEmail = MutableStateFlow(securitySharedPrefs.getString("recovery_email", "hellrider66683@gmail.com") ?: "hellrider66683@gmail.com")
    val recoveryEmail: StateFlow<String> = _recoveryEmail.asStateFlow()

    private val _activeRecoveryCode = MutableStateFlow(securitySharedPrefs.getString("active_recovery_code", "") ?: "")
    val activeRecoveryCode: StateFlow<String> = _activeRecoveryCode.asStateFlow()

    private val _autoLockOnBackground = MutableStateFlow(securitySharedPrefs.getBoolean("auto_lock_background", true))
    val autoLockOnBackground: StateFlow<Boolean> = _autoLockOnBackground.asStateFlow()

    // App starts locked if security is enabled to ensure nobody else can open it
    private val _isAppLocked = MutableStateFlow(_isSecurityEnabled.value)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _failedPinAttempts = MutableStateFlow(0)
    val failedPinAttempts: StateFlow<Int> = _failedPinAttempts.asStateFlow()

    private val _lockoutTimerSeconds = MutableStateFlow(0)
    val lockoutTimerSeconds: StateFlow<Int> = _lockoutTimerSeconds.asStateFlow()

    private var lockoutJob: Job? = null

    fun lockApp() {
        if (_isSecurityEnabled.value) {
            _isAppLocked.value = true
        }
    }

    fun unlockApp(enteredPin: String): Boolean {
        if (_lockoutTimerSeconds.value > 0) return false

        if (enteredPin == _userPin.value) {
            _isAppLocked.value = false
            _failedPinAttempts.value = 0
            val logEntry = SpiritLogEntry(
                question = "Sicherheits-Authentifizierung",
                phrase = "🔓 BENUTZER VERIFIZIERT: Zugriff auf App gewährt",
                emfLevel = _emfLevel.value,
                dangerLevel = 1
            )
            _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
            return true
        } else {
            val attempts = _failedPinAttempts.value + 1
            _failedPinAttempts.value = attempts
            
            // Trigger warning vibration
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } catch (_: Exception) {}

            val logEntry = SpiritLogEntry(
                question = "Fremdzugriff / Sicherheitssperre",
                phrase = "⚠️ UNBEFUGTER ZUGRIFFSVERSUCH! Falsche PIN ($attempts. Fehlversuch)",
                emfLevel = _emfLevel.value,
                dangerLevel = 5
            )
            _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value

            if (attempts >= 3) {
                startLockoutTimer(30)
            }
            return false
        }
    }

    fun createNewPin(newPin: String): Boolean {
        if (newPin.length != 4 || !newPin.all { it.isDigit() }) return false
        _userPin.value = newPin
        _isPinSetupDone.value = true
        _isAppLocked.value = false
        _failedPinAttempts.value = 0
        _lockoutTimerSeconds.value = 0
        securitySharedPrefs.edit()
            .putString("user_pin", newPin)
            .putBoolean("is_pin_setup_done", true)
            .apply()

        val logEntry = SpiritLogEntry(
            question = "Sicherheits-Ersteinrichtung",
            phrase = "🔒 NEUE INITIAL-PIN GESPEICHERT & APP FREIGESCHALTET",
            emfLevel = _emfLevel.value,
            dangerLevel = 1
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
        return true
    }

    fun resetPinSetup() {
        _isPinSetupDone.value = false
        _isAppLocked.value = true
        _userPin.value = ""
        securitySharedPrefs.edit()
            .putBoolean("is_pin_setup_done", false)
            .putString("user_pin", "")
            .apply()

        val logEntry = SpiritLogEntry(
            question = "PIN-Zurücksetzung",
            phrase = "🔄 PIN WURDE ZURÜCKGESETZT – NEU-EINRICHTUNG ERFORDERLICH",
            emfLevel = _emfLevel.value,
            dangerLevel = 2
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
    }

    fun setRecoveryEmail(email: String) {
        val cleanEmail = email.trim()
        _recoveryEmail.value = cleanEmail
        securitySharedPrefs.edit().putString("recovery_email", cleanEmail).apply()
    }

    fun generateAndSendRecoveryCode(context: android.content.Context): String {
        val code = (100000..999999).random().toString()
        _activeRecoveryCode.value = code
        securitySharedPrefs.edit().putString("active_recovery_code", code).apply()

        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                data = android.net.Uri.parse("mailto:${_recoveryEmail.value}")
                putExtra(android.content.Intent.EXTRA_SUBJECT, "Ghost Detector App - PIN Wiederherstellungscode")
                putExtra(
                    android.content.Intent.EXTRA_TEXT,
                    "Hallo,\n\nIhr Sicherheitscode zum Zurücksetzen der App-PIN lautet:\n\n🔑 $code\n\nGeben Sie diesen 6-stelligen Code in der App ein, um Ihre PIN neu zu erstellen."
                )
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "E-Mail senden mit..."))
        } catch (_: Exception) {
            // Fallback if no email activity handles mailto
        }

        val logEntry = SpiritLogEntry(
            question = "PIN Wiederherstellung per E-Mail",
            phrase = "📧 WIEDERHERSTELLUNGSCODE AN ${_recoveryEmail.value} GESENDET",
            emfLevel = _emfLevel.value,
            dangerLevel = 1
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value

        return code
    }

    fun verifyRecoveryCodeAndResetPin(enteredCode: String): Boolean {
        if (_activeRecoveryCode.value.isNotEmpty() && enteredCode.trim() == _activeRecoveryCode.value.trim()) {
            resetPinSetup()
            _activeRecoveryCode.value = ""
            securitySharedPrefs.edit().remove("active_recovery_code").apply()
            return true
        }
        return false
    }

    private fun startLockoutTimer(seconds: Int) {
        lockoutJob?.cancel()
        _lockoutTimerSeconds.value = seconds
        lockoutJob = viewModelScope.launch {
            while (_lockoutTimerSeconds.value > 0) {
                delay(1000)
                _lockoutTimerSeconds.value -= 1
            }
        }
    }

    fun changePin(currentPin: String, newPin: String): Boolean {
        if (currentPin != _userPin.value) return false
        if (newPin.length != 4 || !newPin.all { it.isDigit() }) return false

        _userPin.value = newPin
        securitySharedPrefs.edit().putString("user_pin", newPin).apply()
        
        val logEntry = SpiritLogEntry(
            question = "Sicherheits-Einstellung",
            phrase = "🔒 NEUE ZUGRIFFS-PIN ERFOLGREICH GESPEICHERT",
            emfLevel = _emfLevel.value,
            dangerLevel = 1
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
        return true
    }

    fun setSecurityEnabled(enabled: Boolean) {
        _isSecurityEnabled.value = enabled
        securitySharedPrefs.edit().putBoolean("security_enabled", enabled).apply()
        if (!enabled) {
            _isAppLocked.value = false
        }
    }

    fun setAutoLockOnBackground(enabled: Boolean) {
        _autoLockOnBackground.value = enabled
        securitySharedPrefs.edit().putBoolean("auto_lock_background", enabled).apply()
    }

    // Fullscreen Mode State (Vollbildanzeige)
    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun setFullscreen(enabled: Boolean) {
        _isFullscreen.value = enabled
    }

    
    fun toggleEmfSuppression() {
        val newState = !_isEmfSuppressionActive.value
        _isEmfSuppressionActive.value = newState
        if (newState) {
            val reducedEmf = (_emfLevel.value * 0.35f).coerceIn(0.8f, 2.2f)
            _emfLevel.value = String.format(java.util.Locale.US, "%.1f", reducedEmf).toFloat()
            _dangerLevel.value = 1
            soundManager.playStaticPulse()
        }
        val statusMsg = if (newState) "🛡️ EMF-DÄMPFUNG AKTIV: Feldstärke beim Scannen reduziert!" else "⚡ EMF-DÄMPFUNG AUS: Unabgeschirmte Feldstärke-Ausschläge"
        val logEntry = SpiritLogEntry(
            question = "EMF Dämpfung",
            phrase = statusMsg,
            emfLevel = _emfLevel.value,
            dangerLevel = if (newState) 1 else 3
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
    }

    fun neutralizeEmfSpike() {
        _emfLevel.value = 1.0f
        _dangerLevel.value = 1
        _isEmfSuppressionActive.value = true
        soundManager.playStaticPulse()
        spiritTtsManager.speakSpiritBoxAudio("EMF-Feldstärke neutralisiert und zerstört.", emfLevel = 1.0f, dangerLevel = 1, soundManager = soundManager, isSystemAnnouncement = true)
        val logEntry = SpiritLogEntry(
            question = "EMF Neutralisierung",
            phrase = "💥 EMF-FELDSTÄRKE NEUTRALISIERT: Feld auf 1.0 mG vernichtet!",
            emfLevel = 1.0f,
            dangerLevel = 1
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
    }

    fun toggleMagnetShield() {
        val newState = !_isMagnetShieldActive.value
        _isMagnetShieldActive.value = newState
        val statusMsg = if (newState) "🛡️ MAGNETSCHILD AKTIVIERT: TV/Monitor-Strahlung & Angriffe werden abgefangen!" else "⚠️ MAGNETSCHILD DEAKTIVIERT: Volle Magnetfeld-Exposition"
        val logEntry = SpiritLogEntry(
            question = "Magnet-Schild Steuerung",
            phrase = statusMsg,
            emfLevel = _emfLevel.value,
            dangerLevel = if (newState) 1 else 4
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
    }

    fun addMagnetNote(noteText: String, sourceTag: String = "Fernseher / PC-Monitor Störung") {
        if (noteText.isBlank()) return
        val newEntry = MagnetLogEntry(
            emfValue = _emfLevel.value,
            sourceTag = sourceTag,
            isShielded = _isMagnetShieldActive.value,
            noteText = noteText.trim()
        )
        _magnetLogNotes.value = (listOf(newEntry) + _magnetLogNotes.value).take(50)

        // Add to history log as well for immediate top visibility
        val logEntry = SpiritLogEntry(
            question = "Magnet-Notiz: $sourceTag",
            phrase = "📝 NOTIERT: ${noteText.trim()} (EMF: ${String.format(java.util.Locale.US, "%.1f", _emfLevel.value)} mG)",
            emfLevel = _emfLevel.value,
            dangerLevel = _dangerLevel.value
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
    }

    fun clearMagnetLogNotes() {
        _magnetLogNotes.value = emptyList()
    }

    fun toggleAutoFilterRotation() {
        val newState = !_autoFilterRotationEnabled.value
        _autoFilterRotationEnabled.value = newState
        if (newState) {
            startAutoFilterRotationLoop()
        } else {
            autoFilterRotationJob?.cancel()
            autoFilterRotationJob = null
        }
    }

    private fun startAutoFilterRotationLoop() {
        autoFilterRotationJob?.cancel()
        autoFilterRotationJob = viewModelScope.launch {
            while (_autoFilterRotationEnabled.value) {
                // Auto rotate every 5 minutes (300,000 ms)
                delay(300000L)
                if (_isScanning.value) {
                    val filters = FilterMode.values()
                    val currentIndex = filters.indexOf(_currentFilterMode.value)
                    val nextIndex = (currentIndex + 1) % filters.size
                    val nextFilter = filters[nextIndex]
                    _currentFilterMode.value = nextFilter

                    soundManager.checkAndPlayEerieSpikeSound(7.0f, threshold = 4.0f)

                    val logEntry = SpiritLogEntry(
                        question = "Auto-Filter Zyklus (5 Min)",
                        phrase = "🔄 AUTOMATISCHER FILTER-WECHSEL: Spektralfilter auf ${nextFilter.displayName} umgestellt.",
                        emfLevel = _emfLevel.value,
                        dangerLevel = 1
                    )
                    _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
                }
            }
        }
    }

    fun toggleAutoCaptureLiberate() {
        val newState = !_autoCaptureLiberateEnabled.value
        _autoCaptureLiberateEnabled.value = newState
        val msg = if (newState) "✨ AUTO-FANG & BEFREIUNG: AN (Anomalien werden automatisch harmonisiert)" else "AUTO-FANG & BEFREIUNG: AUS (Manuell)"
        val logEntry = SpiritLogEntry(
            question = "Auto-Befreiung Steuerung",
            phrase = msg,
            emfLevel = _emfLevel.value,
            dangerLevel = 1
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
    }

    fun toggleBackgroundScan247() {
        val newState = !_backgroundScan247Enabled.value
        _backgroundScan247Enabled.value = newState
        val msg = if (newState) "📱 24/7 HINTERGRUND-SCAN: AKTIVIERT (App überwacht Magnetfelder & EVP im Hintergrund)" else "📱 HINTERGRUND-SCAN: AUS"
        val logEntry = SpiritLogEntry(
            question = "Hintergrund-Modus",
            phrase = msg,
            emfLevel = _emfLevel.value,
            dangerLevel = 1
        )
        _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value
    }

    // Selected Ghost detail for modal dialog
    private val _selectedGhostDetail = MutableStateFlow<GhostDetectionEntity?>(null)
    val selectedGhostDetail: StateFlow<GhostDetectionEntity?> = _selectedGhostDetail.asStateFlow()

    private var scanJob: Job? = null
    private var chartHistoryJob: Job? = null
    private var autoSpiritJob: Job? = null

    fun toggleAutoSpiritBox() {
        val newState = !_autoSpiritBoxEnabled.value
        _autoSpiritBoxEnabled.value = newState
        if (newState) {
            autoSpiritJob?.cancel()
            autoSpiritJob = viewModelScope.launch {
                while (_autoSpiritBoxEnabled.value) {
                    if (!_isGeneratingSpiritResponse.value && !spiritTtsManager.isSpeaking.value) {
                        val questions = listOf(
                            "Bist du bei uns im Raum?",
                            "Wie heißt du?",
                            "Warum bist du hier?",
                            "Bist du friedlich oder gefährlich?",
                            "Kannst du ein Zeichen geben?",
                            "Wer hat dich gerufen?",
                            "Hörst du meine Stimme?",
                            "Wo steckst du?",
                            "Zeige deine Präsenz!",
                            "Bist du allein hier?"
                        )
                        askSpirit(questions.random())
                        delay(10000L) // 10 seconds interval after asking
                    } else {
                        delay(500L) // Wait until current sentence/speech finishes
                    }
                }
            }
        } else {
            autoSpiritJob?.cancel()
            autoSpiritJob = null
        }
    }

    fun askRandomQuestion() {
        val questions = listOf(
            "Bist du bei uns im Raum?",
            "Wie heißt du?",
            "Warum bist du hier?",
            "Bist du friedlich oder gefährlich?",
            "Kannst du ein Zeichen geben?",
            "Wer hat dich gerufen?",
            "Hörst du meine Stimme?",
            "Wo steckst du?",
            "Zeige deine Präsenz!",
            "Bist du allein hier?"
        )
        askSpirit(questions.random())
    }

    fun setRealtimeSweepSpeed(speedMs: Long) {
        _realtimeSweepSpeedMs.value = speedMs.coerceIn(50L, 1000L)
    }

    fun toggleRealtimeSweep() {
        val newState = !_isRealtimeSweepActive.value
        _isRealtimeSweepActive.value = newState
        if (newState) {
            realtimeSweepJob?.cancel()
            realtimeSweepJob = viewModelScope.launch {
                var stepCount = 0
                var currentFreq = 88.0f
                while (_isRealtimeSweepActive.value) {
                    currentFreq += 0.2f
                    if (currentFreq > 107.9f) {
                        currentFreq = 88.0f
                    }
                    _frequencyKhz.value = currentFreq
                    soundManager.playRadioStaticSweep()

                    stepCount++
                    val isVoiceDetected = microphoneAnalyzer.amplitude.value > 0.20f
                    if ((stepCount >= 18 || isVoiceDetected) && !_isGeneratingSpiritResponse.value && !spiritTtsManager.isSpeaking.value) {
                        stepCount = 0
                        val prompt = if (isVoiceDetected) {
                            "Echtzeit Mikrofon-Stimmenimpuls bei ${String.format(java.util.Locale.US, "%.1f", currentFreq)} MHz"
                        } else {
                            "Echtzeit Radio-Sweep bei ${String.format(java.util.Locale.US, "%.1f", currentFreq)} MHz"
                        }
                        askSpirit(prompt)
                    }

                    delay(_realtimeSweepSpeedMs.value)
                }
            }
        } else {
            realtimeSweepJob?.cancel()
            realtimeSweepJob = null
        }
    }

    init {
        spiritTtsManager.setLanguage(_appLanguage.value)
        val database = GhostDatabase.getDatabase(application)
        repository = GhostRepository(database.ghostDao())
        
        allDetections = repository.allDetections.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        filteredDetections = combine(
            allDetections,
            _searchQuery,
            _selectedTypeFilter,
            _favoritesOnlyFilter
        ) { list, query, typeFilter, favOnly ->
            list.filter { item ->
                val matchesQuery = query.isBlank() ||
                        item.name.contains(query, ignoreCase = true) ||
                        item.locationName.contains(query, ignoreCase = true) ||
                        item.notes.contains(query, ignoreCase = true)
                val matchesType = typeFilter == "ALLE" || typeFilter == "ALL" || typeFilter == "TÜMÜ" || typeFilter == "TODOS" || typeFilter == "TOUS" ||
                        item.type.contains(typeFilter, ignoreCase = true) ||
                        (typeFilter.contains("Gefangen", ignoreCase = true) && item.type.contains("GEFANGEN", ignoreCase = true)) ||
                        (typeFilter.contains("Riss", ignoreCase = true) && item.type.contains("DIMENSION", ignoreCase = true)) ||
                        (typeFilter.contains("Dämon", ignoreCase = true) && item.type.contains("DÄMON", ignoreCase = true)) ||
                        (typeFilter.contains("Demon", ignoreCase = true) && item.type.contains("DÄMON", ignoreCase = true)) ||
                        (typeFilter.contains("Vampir", ignoreCase = true) && item.type.contains("VAMPIR", ignoreCase = true))
                val matchesFav = !favOnly || item.isFavorite
                matchesQuery && matchesType && matchesFav
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }

        val screenIntentFilter = android.content.IntentFilter().apply {
            addAction(android.content.Intent.ACTION_SCREEN_OFF)
            addAction(android.content.Intent.ACTION_SCREEN_ON)
            addAction(android.os.PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        try {
            application.registerReceiver(screenStateReceiver, screenIntentFilter)
        } catch (_: Exception) {}
        updateBatterySaverThrottling()

        sensorManager.startListening()
        startScanningLoop()
        startChartHistoryLoop()
        startAutoFilterRotationLoop()
    }

    private fun startChartHistoryLoop() {
        chartHistoryJob?.cancel()
        chartHistoryJob = viewModelScope.launch {
            while (true) {
                val chartDelay = if (_isBatterySaverThrottling.value) 2500L else 250L
                delay(chartDelay)
                if (_isScanning.value) {
                    val rawSensorEmf = sensorManager.sensorEmfStrength.value
                    val motion = sensorManager.motionIntensity.value
                    val currentEmf = _emfLevel.value
                    
                    // Add subtle high-frequency fluctuation to current reading for real-time oscilloscope effect
                    val microJitter = (Random.nextFloat() - 0.5f) * (0.3f + motion * 0.2f)
                    val sample = ((currentEmf * 0.7f + rawSensorEmf * 0.3f) + microJitter).coerceIn(1.0f, 9.9f)
                    
                    val currentList = _emfHistory.value
                    _emfHistory.value = (currentList + sample).takeLast(35)
                }
            }
        }
    }

    private fun startScanningLoop() {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            while (true) {
                val scanInterval = if (_isBatterySaverThrottling.value) 4000L else 800L
                delay(scanInterval)
                if (_isScanning.value) {
                    if (_isLiberatingAnomalies.value || _isCapturingEntity.value || _isClosingDimension.value) continue

                    val rawSensorEmf = sensorManager.sensorEmfStrength.value
                    val motion = sensorManager.motionIntensity.value
                    val gyro = sensorManager.gyroSpeed.value
                    val lux = sensorManager.lightLux.value
                    val pressure = sensorManager.pressureHpa.value
                    val prox = sensorManager.proximityCm.value

                    // Blend hardware sensor readings (Accel, Gyro, Mag, Lux, Pressure, Prox)
                    val motionBoost = (motion * 0.7f) + (gyro * 0.5f)
                    val shadowLuxBoost = if (lux < 50f) 1.2f else 0.0f
                    val proxBoost = if (prox < 3f) 1.5f else 0.0f
                    val baseEmf = (rawSensorEmf + motionBoost + shadowLuxBoost + proxBoost).coerceIn(1.0f, 9.5f)
                    val isSpike = Random.nextFloat() > (if (motion > 2.0f || gyro > 1.5f) 0.3f else 0.65f)
                    var newEmf = if (isSpike) (baseEmf + Random.nextFloat() * 4.5f).coerceAtMost(9.9f) else baseEmf
                    if (_isEmfSuppressionActive.value || _isMagnetShieldActive.value) {
                        newEmf = (newEmf * 0.35f).coerceIn(0.8f, 2.2f)
                    }
                    _emfLevel.value = String.format(java.util.Locale.US, "%.1f", newEmf).toFloat()

                    _dangerLevel.value = when {
                        _isMagnetShieldActive.value && newEmf >= 7.0f -> 1 // Shield active-dampens magnetic attack
                        newEmf >= 8.0f -> 5
                        newEmf >= 6.0f -> 4
                        newEmf >= 4.0f -> 3
                        newEmf >= 2.0f -> 2
                        else -> 1
                    }

                    if (_isMagnetShieldActive.value && isSpike && newEmf >= 7.5f) {
                        soundManager.playStaticPulse()
                    }

                    _frequencyKhz.value = String.format(java.util.Locale.US, "%.1f", 14.0f + Random.nextFloat() * 70.0f).toFloat()

                    // Update and move existing blips to simulate persistent entities
                    val currentBlips = _radarBlips.value.toMutableList()
                    
                    // 1. Move and mutate existing radar-generated blips
                    val updatedBlips = currentBlips.map { blip ->
                        if (blip.id.startsWith("blip_")) {
                            // Organic jitter movement: slow drift + random shift
                            val angleShift = (Random.nextFloat() - 0.5f) * 12f
                            val distShift = (Random.nextFloat() - 0.5f) * 0.05f
                            
                            // Occasional danger-level "pulse" or label change
                            var newDanger = blip.dangerLevel
                            if (Random.nextFloat() > 0.85f) {
                                newDanger = (newDanger + (if (Random.nextBoolean()) 1 else -1)).coerceIn(1, 5)
                            }
                            
                            blip.copy(
                                angleDegrees = (blip.angleDegrees + angleShift + 360f) % 360f,
                                distanceRatio = (blip.distanceRatio + distShift).coerceIn(0.12f, 0.85f),
                                dangerLevel = newDanger
                            )
                        } else {
                            blip // Keep camera-based blips as is (handled in onCameraFrameAnomalies)
                        }
                    }.toMutableList()

                    // 2. Chance to remove a blip (spirit fades away)
                    if (updatedBlips.any { it.id.startsWith("blip_") } && Random.nextFloat() > 0.85f) {
                        val fadeIndex = updatedBlips.indexOfFirst { it.id.startsWith("blip_") }
                        if (fadeIndex != -1) updatedBlips.removeAt(fadeIndex)
                    }

                    // 3. Chance to add a new blip based on EMF activity
                    val maxBlips = if (newEmf > 6f) 5 else 3
                    if (updatedBlips.count { it.id.startsWith("blip_") } < maxBlips) {
                        val spawnChance = if (newEmf > 5f) 0.6f else 0.20f
                        if (Random.nextFloat() < spawnChance) {
                            val spawnRoll = Random.nextFloat()
                            val (category, labelName, danger) = when {
                                spawnRoll < 0.22f -> Triple(
                                    com.example.ui.components.EntityCategory.DEMON,
                                    "Dämon #${Random.nextInt(10, 99)}",
                                    Random.nextInt(4, 6)
                                )
                                spawnRoll < 0.44f -> Triple(
                                    com.example.ui.components.EntityCategory.VAMPIRE,
                                    "Vampir #${Random.nextInt(10, 99)}",
                                    Random.nextInt(4, 6)
                                )
                                spawnRoll < 0.60f -> Triple(
                                    com.example.ui.components.EntityCategory.DIMENSION_RIFT,
                                    "Dimension-Riss #${Random.nextInt(1, 9)}",
                                    Random.nextInt(3, 6)
                                )
                                else -> Triple(
                                    com.example.ui.components.EntityCategory.GHOST,
                                    if (newEmf > 6.5f || isSpike) "Poltergeist #${Random.nextInt(10, 99)}" else "Phantom #${Random.nextInt(10, 99)}",
                                    if (newEmf > 6.5f || isSpike) Random.nextInt(4, 6) else Random.nextInt(1, 4)
                                )
                            }

                            updatedBlips.add(RadarBlip(
                                id = "blip_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
                                angleDegrees = Random.nextFloat() * 360f,
                                distanceRatio = Random.nextFloat() * 0.7f + 0.15f,
                                dangerLevel = danger,
                                label = labelName,
                                category = category
                            ))
                            // Haptic vibration feedback for Infra-Grün scanner entity detection
                            triggerEntityDetectionVibration(danger)
                        }
                    }

                    _radarBlips.value = updatedBlips

                    if (_isAutoDestroyEnabled.value || _autoCaptureLiberateEnabled.value) {
                        val rift = updatedBlips.firstOrNull { it.category == com.example.ui.components.EntityCategory.DIMENSION_RIFT }
                        val threat = updatedBlips.firstOrNull { it.category == com.example.ui.components.EntityCategory.DEMON || it.category == com.example.ui.components.EntityCategory.VAMPIRE }
                        val ghost = updatedBlips.firstOrNull { it.category == com.example.ui.components.EntityCategory.GHOST }
                        
                        if (rift != null && !_isClosingDimension.value) {
                            viewModelScope.launch { delay(300); closeDimensionRift(rift) }
                        } else if (threat != null && !_isCapturingEntity.value) {
                            viewModelScope.launch { delay(300); captureEntity(threat) }
                        } else if (ghost != null && !_isLiberatingAnomalies.value) {
                            viewModelScope.launch { delay(300); liberateSingleBlip(ghost) }
                        }
                    }

                    if (_audioFeedbackEnabled.value) {
                        soundManager.playGeigerClick()
                        if (newEmf >= 7.5f) {
                            soundManager.playThreatAlert()
                            // Intense Haptic Kick for high spikes
                            triggerVibration(100)
                        } else {
                            soundManager.playRadarPing()
                        }
                        // Play eerie ambient ghost-hunting sound effects when magnetic activity spikes occur
                        soundManager.checkAndPlayEerieSpikeSound(newEmf, threshold = 6.0f)
                    }
                }
            }
        }
    }

    fun toggleScanning() {
        val newScanningState = !_isScanning.value
        _isScanning.value = newScanningState
        if (newScanningState) {
            sensorManager.startListening()
        } else {
            sensorManager.stopListening()
        }
    }

    fun setFilterMode(mode: FilterMode) {
        _currentFilterMode.value = mode
    }

    fun setFilterIntensity(intensity: Float) {
        _filterIntensity.value = intensity.coerceIn(0.10f, 1.00f)
    }

    fun toggleVibration() {
        _vibrationEnabled.value = !_vibrationEnabled.value
    }

    fun setVibrationEnabled(enabled: Boolean) {
        _vibrationEnabled.value = enabled
    }

    fun setVibrationIntensity(intensity: Float) {
        _vibrationIntensity.value = intensity.coerceIn(0.0f, 1.0f)
        if (intensity <= 0.01f) {
            _vibrationEnabled.value = false
        } else if (!_vibrationEnabled.value) {
            _vibrationEnabled.value = true
        }
    }

        fun triggerEntityDetectionVibration(dangerLevel: Int = 3) {
        if (!_vibrationEnabled.value || _vibrationIntensity.value <= 0.01f) return
        val timings = when {
            dangerLevel >= 5 -> longArrayOf(0, 150, 60, 250, 80, 300)
            dangerLevel >= 3 -> longArrayOf(0, 100, 50, 180)
            else -> longArrayOf(0, 80, 40, 100)
        }
        triggerVibrationWaveform(timings)
    }

    private fun triggerVibration(durationMs: Long) {
        if (!_vibrationEnabled.value || _vibrationIntensity.value <= 0.01f) return
        try {
            val scaledDuration = (durationMs * _vibrationIntensity.value).toLong().coerceAtLeast(10L)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amplitude = (255 * _vibrationIntensity.value).toInt().coerceIn(1, 255)
                vibrator.vibrate(VibrationEffect.createOneShot(scaledDuration, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(scaledDuration)
            }
        } catch (_: Exception) {}
    }

    private fun triggerVibrationWaveform(timings: LongArray) {
        if (!_vibrationEnabled.value || _vibrationIntensity.value <= 0.01f) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val scaledTimings = timings.mapIndexed { idx, time ->
                    if (idx % 2 == 1) (time * _vibrationIntensity.value).toLong().coerceAtLeast(10L) else time
                }.toLongArray()
                vibrator.vibrate(VibrationEffect.createWaveform(scaledTimings, -1))
            } else {
                val totalTime = timings.sum()
                val scaledTotal = (totalTime * _vibrationIntensity.value).toLong().coerceAtLeast(10L)
                @Suppress("DEPRECATION")
                vibrator.vibrate(scaledTotal)
            }
        } catch (_: Exception) {}
    }

    fun onCameraFrameAnomalies(anomalies: List<com.example.data.CameraAnomaly>, avgLuminance: Float) {
        if (_isLiberatingAnomalies.value || _isCapturingEntity.value || _isClosingDimension.value) return
        _cameraAnomalies.value = anomalies
        _cameraAvgLuminance.value = avgLuminance

        if (_isScanning.value && anomalies.isNotEmpty()) {
            val maxIntensity = anomalies.maxOf { it.intensity }
            // Boost EMF reading if strong camera anomalies are detected
            if (maxIntensity > 0.4f) {
                val camEmfBoost = maxIntensity * 3.5f
                var updatedEmf = (_emfLevel.value * 0.7f + camEmfBoost * 0.3f + 1.5f).coerceIn(1.0f, 9.9f)
                if (_isEmfSuppressionActive.value || _isMagnetShieldActive.value) {
                    updatedEmf = (updatedEmf * 0.35f).coerceIn(0.8f, 2.2f)
                }
                _emfLevel.value = String.format(java.util.Locale.US, "%.1f", updatedEmf).toFloat()
                // Haptic vibration feedback for Infra-Grün camera detector entity
                triggerEntityDetectionVibration((maxIntensity * 5f).toInt().coerceIn(1, 5))
            }

            // Convert camera frame anomalies directly into radar blips
            val camBlips = anomalies.mapIndexed { idx, anom ->
                val dx = anom.xRatio - 0.5f
                val dy = anom.yRatio - 0.5f
                val angle = ((Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat() + 450f) % 360f)
                val dist = (kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() * 1.5f).coerceIn(0.12f, 0.85f)
                val danger = (anom.intensity * 5f).toInt().coerceIn(1, 5)

                RadarBlip(
                    id = "cam_blip_${anom.id}_$idx",
                    angleDegrees = angle,
                    distanceRatio = dist,
                    dangerLevel = danger,
                    label = anom.type.displayName
                )
            }
            if (camBlips.isNotEmpty()) {
                val currentNonCamBlips = _radarBlips.value.filter { !it.id.startsWith("cam_") }
                _radarBlips.value = currentNonCamBlips + camBlips
                
                if (_isAutoDestroyEnabled.value || _autoCaptureLiberateEnabled.value) {
                    val rift = camBlips.firstOrNull { it.category == com.example.ui.components.EntityCategory.DIMENSION_RIFT }
                    val threat = camBlips.firstOrNull { it.category == com.example.ui.components.EntityCategory.DEMON || it.category == com.example.ui.components.EntityCategory.VAMPIRE }
                    val ghost = camBlips.firstOrNull { it.category == com.example.ui.components.EntityCategory.GHOST }

                    if (rift != null && !_isClosingDimension.value) {
                        viewModelScope.launch { delay(300); closeDimensionRift(rift) }
                    } else if (threat != null && !_isCapturingEntity.value) {
                        viewModelScope.launch { delay(300); captureEntity(threat) }
                    } else if (ghost != null && !_isLiberatingAnomalies.value) {
                        viewModelScope.launch { delay(300); liberateSingleBlip(ghost) }
                    }
                }
            }
        }
    }

    fun toggleAutoDestroy() {
        _isAutoDestroyEnabled.value = !_isAutoDestroyEnabled.value
    }

    fun toggleCrtOverlay() {
        _showCrtOverlay.value = !_showCrtOverlay.value
    }

    fun toggleAudioFeedback() {
        val newState = !_audioFeedbackEnabled.value
        _audioFeedbackEnabled.value = newState
        soundManager.setMuted(!newState)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTypeFilter(type: String) {
        _selectedTypeFilter.value = type
    }

    fun setFavoritesOnlyFilter(favOnly: Boolean) {
        _favoritesOnlyFilter.value = favOnly
    }

    fun selectGhostDetail(ghost: GhostDetectionEntity?) {
        _selectedGhostDetail.value = ghost
    }

    fun saveCurrentScanAsGhost(
        customName: String? = null,
        customLocation: String? = null,
        customNotes: String? = null
    ) {
        viewModelScope.launch {
            val types = listOf("Poltergeist", "Schattenwesen", "Phantom", "Banshee", "Elementar", "Orb-Vorkommen")
            val ghostType = types[Random.nextInt(types.size)]
            val name = customName.takeUnless { it.isNullOrBlank() } ?: "$ghostType #${Random.nextInt(100, 999)}"
            val location = customLocation.takeUnless { it.isNullOrBlank() } ?: "Sektor ${Random.nextInt(1, 12)}-B"
            val notes = customNotes ?: "Automatische Aufzeichnung während Infrarot-Scan."

            val entity = GhostDetectionEntity(
                name = name,
                type = ghostType,
                emfLevel = _emfLevel.value,
                frequencyKhz = _frequencyKhz.value,
                dangerLevel = _dangerLevel.value,
                locationName = location,
                timestamp = System.currentTimeMillis(),
                notes = notes,
                spectralColorHex = when (_currentFilterMode.value) {
                    FilterMode.INFRA_GREEN -> "#00FF66"
                    FilterMode.THERMAL_RED -> "#FF9900"
                    FilterMode.QUANTUM_MATRIX -> "#00E5FF"
                    FilterMode.ULTRAVIOLET -> "#BB33FF"
                    FilterMode.INFRA_YELLOW -> "#FFDD00"
                    FilterMode.INFRA_BLUE -> "#00A8FF"
                    FilterMode.INFRARED -> "#FF2A2A"
                },
                lastWords = _spiritResponse.value
            )
            repository.insertGhost(entity)
        }
    }

    fun toggleFavorite(ghost: GhostDetectionEntity) {
        viewModelScope.launch {
            repository.updateGhost(ghost.copy(isFavorite = !ghost.isFavorite))
            if (_selectedGhostDetail.value?.id == ghost.id) {
                _selectedGhostDetail.value = _selectedGhostDetail.value?.copy(isFavorite = !ghost.isFavorite)
            }
        }
    }

    fun favoriteAllCapturedGhosts() {
        viewModelScope.launch {
            val allGhostsList = allDetections.value
            val capturedGhosts = allGhostsList.filter { it.type.contains("GEFANGEN", ignoreCase = true) || it.name.contains("GEFANGEN", ignoreCase = true) }
            val ghostsToFavorite = capturedGhosts.filter { !it.isFavorite }
            
            for (ghost in ghostsToFavorite) {
                repository.updateGhost(ghost.copy(isFavorite = true))
            }
        }
    }

    fun liberateRadarAnomalies() {
        if (_isLiberatingAnomalies.value) return
        _isLiberatingAnomalies.value = true

        viewModelScope.launch {
            // Trigger vibration feedback
            triggerVibrationWaveform(longArrayOf(0, 120, 80, 200, 100, 300))

            // Play liberation harmonic audio wave
            if (_audioFeedbackEnabled.value) {
                soundManager.playGhostFreedSound()
                spiritTtsManager.speakSpiritBoxAudio("Entität ins Licht befreit und harmonisiert.", emfLevel = 8.5f, dangerLevel = 1, soundManager = soundManager, isSystemAnnouncement = true)
            }

            val totalEnt = _radarBlips.value.size + _cameraAnomalies.value.size
            val msg = if (totalEnt > 0) {
                "✨ $totalEnt ENTITÄTEN BEFREIT: Spektrale Harmonisierung erfolgreich! Die Geister wurden ins Licht entlassen."
            } else {
                "✨ SPEKTRAL-HARMONISIERUNG: Aether gereinigt & alle Phantome befreit!"
            }
            _liberatedBannerMessage.value = msg

            // Store record in DB as Liberated Entity so history proves freedom
            val types = listOf("Befreiter Poltergeist", "Harmonisiertes Phantom", "Erlöstes Schattenwesen", "Befreiter Geist")
            val ghostType = types[Random.nextInt(types.size)]
            val freedEntity = GhostDetectionEntity(
                name = "$ghostType #${Random.nextInt(100, 999)} [BEFREIT]",
                type = "BEFREIT & HARMONISIERT",
                emfLevel = _emfLevel.value,
                frequencyKhz = _frequencyKhz.value,
                dangerLevel = 1,
                locationName = "Spektrale Befreiungs-Zone",
                timestamp = System.currentTimeMillis(),
                notes = "Erfolgreich über das Radar ins Licht befreit.",
                spectralColorHex = "#00FFCC",
                lastWords = "Danke für die Befreiung."
            )
            repository.insertGhost(freedEntity)

            // Reset sensor state to peaceful levels
            _emfLevel.value = 1.2f
            _dangerLevel.value = 1
            _radarBlips.value = emptyList()
            _cameraAnomalies.value = emptyList()

            delay(2500)
            _isLiberatingAnomalies.value = false
        }
    }

    fun liberateSingleBlip(blip: RadarBlip) {
        if (_isLiberatingAnomalies.value) return
        _isLiberatingAnomalies.value = true

        viewModelScope.launch {
            triggerVibration(120)

            if (_audioFeedbackEnabled.value) {
                soundManager.playGhostFreedSound()
                spiritTtsManager.speakSpiritBoxAudio("Geist ins Licht befreit.", emfLevel = 7f, dangerLevel = 1, soundManager = soundManager, isSystemAnnouncement = true)
            }

            val currentBlips = _radarBlips.value.toMutableList()
            currentBlips.removeAll { it.id == blip.id }
            _radarBlips.value = currentBlips

            val label = blip.label ?: if (blip.dangerLevel >= 4) "Roter Poltergeist" else "Phantom Anomaly"
            _liberatedBannerMessage.value = "✨ $label BEFREIT: Der Radar-Punkt wurde erlöst!"

            val freedEntity = GhostDetectionEntity(
                name = "$label [BEFREIT]",
                type = "BEFREIT & HARMONISIERT",
                emfLevel = _emfLevel.value,
                frequencyKhz = _frequencyKhz.value,
                dangerLevel = 1,
                locationName = "Radar Einzel-Befreiung",
                timestamp = System.currentTimeMillis(),
                notes = "Einzelseelen-Befreiung direkt über Radar-Punkt.",
                spectralColorHex = "#00FFCC",
                lastWords = "Danke für die Erlösung!"
            )
            repository.insertGhost(freedEntity)
            
            delay(1800)
            _isLiberatingAnomalies.value = false
        }
    }

    fun handleRadarBlipClick(blip: RadarBlip) {
        when (blip.category) {
            com.example.ui.components.EntityCategory.DIMENSION_RIFT -> closeDimensionRift(blip)
            com.example.ui.components.EntityCategory.DEMON, com.example.ui.components.EntityCategory.VAMPIRE -> captureEntity(blip)
            com.example.ui.components.EntityCategory.GHOST -> liberateSingleBlip(blip)
        }
    }

    fun closeDimensionRift(blip: RadarBlip? = null) {
        if (_isClosingDimension.value) return
        _isClosingDimension.value = true

        viewModelScope.launch {
            triggerVibrationWaveform(longArrayOf(0, 80, 50, 150, 80, 250, 100, 350))

            if (_audioFeedbackEnabled.value) {
                soundManager.playGhostFreedSound()
                spiritTtsManager.speakSpiritBoxAudio(
                    "Interdimensionaler Riss erfolgreich versiegelt und stabilisiert.",
                    emfLevel = 9.0f,
                    dangerLevel = 1,
                    soundManager = soundManager,
                    isSystemAnnouncement = true
                )
            }

            val targetBlip = blip ?: _radarBlips.value.firstOrNull { it.category == com.example.ui.components.EntityCategory.DIMENSION_RIFT }
            val riftLabel = targetBlip?.label ?: "Dimension-Riss Alpha-7"

            if (targetBlip != null) {
                val currentBlips = _radarBlips.value.toMutableList()
                currentBlips.removeAll { it.id == targetBlip.id }
                _radarBlips.value = currentBlips
            }

            val msg = "🌀 DIMENSION-RISS VERRIEGELT: $riftLabel erfolgreich geschlossen & Raum-Zeit stabilisiert!"
            _liberatedBannerMessage.value = msg

            val closedEntity = GhostDetectionEntity(
                name = "$riftLabel [VERRIEGELT]",
                type = "DIMENSIONSRISS (GESCHLOSSEN)",
                emfLevel = _emfLevel.value,
                frequencyKhz = _frequencyKhz.value,
                dangerLevel = 1,
                locationName = "Interdimensionales Portal-Siegel",
                timestamp = System.currentTimeMillis(),
                notes = "Portal mit dem Quanten-Dimensionen-Versiegeler dauerhaft verschlossen.",
                spectralColorHex = "#00FFFF",
                lastWords = "Das Portal schließt sich..."
            )
            repository.insertGhost(closedEntity)

            delay(1800)
            _isClosingDimension.value = false
        }
    }

    fun switchDimensionPlane(plane: com.example.data.DimensionPlane) {
        if (_activeDimensionPlane.value == plane) return
        _activeDimensionPlane.value = plane
        if (_audioFeedbackEnabled.value) {
            soundManager.playRadioStaticSweep()
            spiritTtsManager.speakSpiritBoxAudio(
                "Frequenz eingestellt auf ${plane.title}.",
                emfLevel = 6.0f,
                dangerLevel = 2,
                soundManager = soundManager,
                isSystemAnnouncement = true
            )
        }
        triggerVibrationWaveform(longArrayOf(0, 50, 50, 100))
        _liberatedBannerMessage.value = "🌀 DIMENSION GEWECHSELT: Einstimmung auf ${plane.codeName} (${plane.frequencyHz} Hz)"
    }

    fun castSigil(sigil: com.example.data.SigilType) {
        if (_isCastingSigilRitual.value) return
        _isCastingSigilRitual.value = true

        viewModelScope.launch {
            triggerVibrationWaveform(longArrayOf(0, 100, 50, 200, 100, 300, 150, 400))
            if (_audioFeedbackEnabled.value) {
                when (sigil) {
                    com.example.data.SigilType.DEMON_BANISHING -> soundManager.playThreatAlert()
                    com.example.data.SigilType.DIMENSION_ANCHOR -> soundManager.playGhostFreedSound()
                    com.example.data.SigilType.ARCHANGEL_SHIELD -> soundManager.playStaticPulse()
                    com.example.data.SigilType.LIGHT_HARMONY -> soundManager.playGhostFreedSound()
                    com.example.data.SigilType.QUANTUM_STABILIZER -> soundManager.playRadioStaticSweep()
                }
                spiritTtsManager.speakSpiritBoxAudio(
                    "Ritual-Siegel ${sigil.title} erfolgreich manifestiert.",
                    emfLevel = 9.5f,
                    dangerLevel = 1,
                    soundManager = soundManager,
                    isSystemAnnouncement = true
                )
            }

            // Functional ritual effects based on Sigil Type
            when (sigil) {
                com.example.data.SigilType.DEMON_BANISHING -> {
                    val threatBlips = _radarBlips.value.filter {
                        it.category == com.example.ui.components.EntityCategory.DEMON ||
                        it.category == com.example.ui.components.EntityCategory.VAMPIRE
                    }
                    if (threatBlips.isNotEmpty()) {
                        threatBlips.forEach { captureEntity(it) }
                    } else {
                        captureEntity()
                    }
                }
                com.example.data.SigilType.DIMENSION_ANCHOR -> {
                    val rifts = _radarBlips.value.filter {
                        it.category == com.example.ui.components.EntityCategory.DIMENSION_RIFT
                    }
                    if (rifts.isNotEmpty()) {
                        rifts.forEach { closeDimensionRift(it) }
                    } else {
                        closeDimensionRift()
                    }
                }
                com.example.data.SigilType.ARCHANGEL_SHIELD -> {
                    _isEmfSuppressionActive.value = true
                    _isMagnetShieldActive.value = true
                }
                com.example.data.SigilType.LIGHT_HARMONY -> {
                    _radarBlips.value = emptyList()
                    _liberatedBannerMessage.value = "🕊️ LICHT-HARMONIE: Alle Radar-Anomalien wurden harmonisiert!"
                }
                com.example.data.SigilType.QUANTUM_STABILIZER -> {
                    _dangerLevel.value = (_dangerLevel.value - 2).coerceAtLeast(1)
                }
            }

            val ritualEntity = GhostDetectionEntity(
                name = "Manifestiertes Siegel: ${sigil.title}",
                type = "RITUAL-SIEGEL (${sigil.title})",
                emfLevel = 9.9f,
                frequencyKhz = 108.0f,
                dangerLevel = 1,
                locationName = "Dimensions-Schmiede Sanctum",
                timestamp = System.currentTimeMillis(),
                notes = "Siegel-Wirkung aktiviert (${sigil.purpose}). Verbleibende Dauer: ${sigil.durationSeconds}s.",
                spectralColorHex = "#00FFCC",
                lastWords = "Das Siegel brennt hell und schützt diesen Raum."
            )
            repository.insertGhost(ritualEntity)

            _activeSigil.value = sigil
            _sigilTimerSeconds.value = sigil.durationSeconds

            delay(1500)
            _isCastingSigilRitual.value = false

            sigilTimerJob?.cancel()
            sigilTimerJob = viewModelScope.launch {
                while (_sigilTimerSeconds.value > 0) {
                    delay(1000)
                    _sigilTimerSeconds.value -= 1
                }
                _activeSigil.value = null
            }
        }
    }

    fun cancelSigil() {
        sigilTimerJob?.cancel()
        _activeSigil.value = null
        _sigilTimerSeconds.value = 0
    }

    fun spawnDimensionRift() {
        val currentBlips = _radarBlips.value.toMutableList()
        val newRift = RadarBlip(
            id = "blip_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
            angleDegrees = Random.nextFloat() * 360f,
            distanceRatio = Random.nextFloat() * 0.6f + 0.2f,
            dangerLevel = 5,
            label = "Dimension-Riss #${Random.nextInt(1, 99)}",
            category = com.example.ui.components.EntityCategory.DIMENSION_RIFT
        )
        _radarBlips.value = currentBlips + newRift
        if (_audioFeedbackEnabled.value) {
            soundManager.playThreatAlert()
        }
        triggerVibration(150)
        _liberatedBannerMessage.value = "⚠️ WARNUNG: Interdimensionaler Riss auf dem Radar geortet! Portal versiegeln!"
    }

    fun spawnDemonOrVampire() {
        val currentBlips = _radarBlips.value.toMutableList()
        val isDemon = Random.nextBoolean()
        val category = if (isDemon) com.example.ui.components.EntityCategory.DEMON else com.example.ui.components.EntityCategory.VAMPIRE
        val labelName = if (isDemon) "Dämon #${Random.nextInt(10, 99)}" else "Vampir #${Random.nextInt(10, 99)}"

        val newThreat = RadarBlip(
            id = "blip_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
            angleDegrees = Random.nextFloat() * 360f,
            distanceRatio = Random.nextFloat() * 0.5f + 0.2f,
            dangerLevel = 5,
            label = labelName,
            category = category
        )
        _radarBlips.value = currentBlips + newThreat
        if (_audioFeedbackEnabled.value) {
            soundManager.playThreatAlert()
        }
        triggerVibration(200)
        _liberatedBannerMessage.value = "⚠️ WARNUNG: HOHE DÄMONEN / VAMPIR KONZENTRATION ERKANNT! Roter Warnrahmen aktiv!"
    }

    fun captureEntity(blip: RadarBlip? = null) {
        if (_isCapturingEntity.value) return
        _isCapturingEntity.value = true

        viewModelScope.launch {
            triggerVibrationWaveform(longArrayOf(0, 100, 60, 200, 80, 300))

            if (_audioFeedbackEnabled.value) {
                soundManager.playThreatAlert()
                spiritTtsManager.speakSpiritBoxAudio(
                    "Entität erfolgreich in der Spektral-Falle gefangen.",
                    emfLevel = 8.5f,
                    dangerLevel = 2,
                    soundManager = soundManager,
                    isSystemAnnouncement = true
                )
            }

            val targetBlip = blip ?: _radarBlips.value.firstOrNull {
                it.category == com.example.ui.components.EntityCategory.DEMON ||
                it.category == com.example.ui.components.EntityCategory.VAMPIRE ||
                it.category == com.example.ui.components.EntityCategory.GHOST
            } ?: RadarBlip(
                id = "captured_${System.currentTimeMillis()}",
                angleDegrees = 45f,
                distanceRatio = 0.5f,
                dangerLevel = 4,
                label = "Höllendämon #${Random.nextInt(10, 99)}",
                category = com.example.ui.components.EntityCategory.DEMON
            )

            if (blip != null || _radarBlips.value.contains(targetBlip)) {
                val currentBlips = _radarBlips.value.toMutableList()
                currentBlips.removeAll { it.id == targetBlip.id }
                _radarBlips.value = currentBlips
            }

            _capturedCount.value += 1

            val typeText = when (targetBlip.category) {
                com.example.ui.components.EntityCategory.DEMON -> "DÄMON (GEFANGEN)"
                com.example.ui.components.EntityCategory.VAMPIRE -> "VAMPIR (GEFANGEN)"
                else -> "GEIST (GEFANGEN)"
            }

            val (meaningText, lastWordsText) = when (targetBlip.category) {
                com.example.ui.components.EntityCategory.DEMON -> {
                    Pair(
                        "🔴 BEDEUTUNG & DEMONOLOGISCHE ANALYSE: Höllisches Wesen der Gefahrenklasse 5 (Roter Radar-Punkt).\n" +
                        "• Elektromagnetische Signatur: Extrem hoch (${String.format(java.util.Locale.US, "%.1f", _emfLevel.value)} mG).\n" +
                        "• Ursprung & Verhalten: Infernale Dimension. Verursacht starke Kälteeinbrüche, Poltergeist-Aktivitäten und Elektronikstörungen.\n" +
                        "• Status im Verlauf: Mit dem Dämonen-Siegel verbannt und im Spektral-Tresor sicher eingesperrt.",
                        "Nein! Das Siegel brennt... Der rote Punkt verblasst!"
                    )
                }
                com.example.ui.components.EntityCategory.VAMPIRE -> {
                    Pair(
                        "🟣 BEDEUTUNG & DÄMONEN-ANALYSE: Astraal-parasitärer Vampir (Roter Radar-Punkt).\n" +
                        "• Elektromagnetische Signatur: Ultra-Frequenz bei ${String.format(java.util.Locale.US, "%.1f", _frequencyKhz.value)} kHz.\n" +
                        "• Ursprung & Verhalten: Nährt sich von feinstofflicher Lebensenergie & menschlichen Aura-Feldern. Versucht bei Annäherung den EMF-Sensor zu überlasten.\n" +
                        "• Status im Verlauf: Durch ultraviolette Spektral-Frequenzen neutralisiert und in die Falle gesaugt.",
                        "Dein Licht blendet mich... Ich weiche zurück!"
                    )
                }
                else -> {
                    Pair(
                        "✨ BEDEUTUNG & SPEKTRAL-ANALYSE: Erdgebundenes Schattenwesen (Roter Punkt - Gefahrenstufe ${targetBlip.dangerLevel}).\n" +
                        "• Elektromagnetische Signatur: Feldstärke ${String.format(java.util.Locale.US, "%.1f", _emfLevel.value)} mG.\n" +
                        "• Ursprung & Verhalten: Verdichtetes Rest-Energie-Phänomen. Reagiert hochempfindlich auf Infrarot-Kameras & Geister-Scanner.\n" +
                        "• Status im Verlauf: Erfasst, isoliert und dauerhaft in die Containment-Kammer überführt.",
                        "Die Dunkelheit weicht dem Licht..."
                    )
                }
            }

            val msg = "⚡ ${targetBlip.label} GEFANGEN: In die Spektral-Falle & Dämonen-Siegel sicher verbannt!"
            _liberatedBannerMessage.value = msg

            val capturedEntity = GhostDetectionEntity(
                name = "${targetBlip.label} [GEFANGEN]",
                type = typeText,
                emfLevel = _emfLevel.value,
                frequencyKhz = _frequencyKhz.value,
                dangerLevel = targetBlip.dangerLevel,
                locationName = "Spektral-Falle Containment-Kammer",
                timestamp = System.currentTimeMillis(),
                notes = meaningText,
                spectralColorHex = "#FF0055",
                lastWords = lastWordsText
            )
            repository.insertGhost(capturedEntity)

            delay(1800)
            _isCapturingEntity.value = false
        }
    }

    fun dismissLiberationBanner() {
        _liberatedBannerMessage.value = null
    }

    fun freeGhost(ghost: GhostDetectionEntity) {
        viewModelScope.launch {
            if (_audioFeedbackEnabled.value) {
                soundManager.playGhostFreedSound()
                spiritTtsManager.speakSpiritBoxAudio("Entität befreit", emfLevel = 8f, dangerLevel = 1, soundManager = soundManager, isSystemAnnouncement = true)
            }
            repository.deleteGhost(ghost)
            if (_selectedGhostDetail.value?.id == ghost.id) {
                _selectedGhostDetail.value = null
            }
        }
    }

    fun deleteGhost(ghost: GhostDetectionEntity) {
        viewModelScope.launch {
            repository.deleteGhost(ghost)
            if (_selectedGhostDetail.value?.id == ghost.id) {
                _selectedGhostDetail.value = null
            }
        }
    }

    fun clearAllGhosts() {
        viewModelScope.launch {
            repository.clearAll()
            _selectedGhostDetail.value = null
        }
    }

    fun updateGhostNotes(ghost: GhostDetectionEntity, newNotes: String) {
        viewModelScope.launch {
            val updated = ghost.copy(notes = newNotes)
            repository.updateGhost(updated)
            if (_selectedGhostDetail.value?.id == ghost.id) {
                _selectedGhostDetail.value = updated
            }
        }
    }

    // Spirit Box / Communication
    fun toggleCameraBackground() {
        _isCameraBackgroundEnabled.value = !_isCameraBackgroundEnabled.value
    }

    fun toggleFlashlight() {
        _isFlashlightEnabled.value = !_isFlashlightEnabled.value
    }

    /**
     * Generiert eine gruselige Phrase basierend auf den aktuellen Sensordaten
     * und gibt diese über das Spirit-Box-Audio Text-to-Speech System wieder.
     */
    fun generateAndPlaySensorCreepyPhrase() {
        if (_isGeneratingSpiritResponse.value) return

        _isGeneratingSpiritResponse.value = true
        viewModelScope.launch {
            val emf = _emfLevel.value
            val motion = sensorManager.motionIntensity.value
            val freq = _frequencyKhz.value
            val danger = _dangerLevel.value

            val creepyPhrase = spiritAiEngine.generateSensorDrivenPhrase(
                emfLevel = emf,
                motion = motion,
                frequencyKhz = freq,
                dangerLevel = danger,
                language = _appLanguage.value
            )

            _spiritResponse.value = creepyPhrase
            _isGeneratingSpiritResponse.value = false

            val logEntry = SpiritLogEntry(
                question = "Sensor-Ätherabtastung",
                phrase = creepyPhrase,
                emfLevel = emf,
                dangerLevel = danger
            )
            _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value

            // Voice synthesis with Spirit-Box audio sweep & dynamic pitch
            spiritTtsManager.speakSpiritBoxAudio(
                text = creepyPhrase,
                emfLevel = emf,
                dangerLevel = danger,
                soundManager = soundManager
            )
            triggerEntityDetectionVibration(danger)
        }
    }

    fun askSpirit(questionText: String) {
        if (questionText.isBlank() || _isGeneratingSpiritResponse.value) return

        _spiritQuestion.value = questionText
        _isGeneratingSpiritResponse.value = true

        viewModelScope.launch {
            soundManager.playStaticPulse()
            val types = listOf("Poltergeist", "Phantom", "Schattenwesen", "EVP-Aura")
            val ghostType = types[Random.nextInt(types.size)]

            val response = spiritAiEngine.generateSpiritResponse(
                question = questionText,
                ghostType = ghostType,
                emfLevel = _emfLevel.value,
                language = _appLanguage.value
            )

            _spiritResponse.value = response
            _isGeneratingSpiritResponse.value = false

            val logEntry = SpiritLogEntry(
                question = questionText,
                phrase = response,
                emfLevel = _emfLevel.value,
                dangerLevel = _dangerLevel.value
            )
            _spiritPhraseLog.value = listOf(logEntry) + _spiritPhraseLog.value

            // Vocalize through Text-to-Speech with Spirit Box audio effects
            spiritTtsManager.speakSpiritBoxAudio(
                text = response,
                emfLevel = _emfLevel.value,
                dangerLevel = _dangerLevel.value,
                soundManager = soundManager
            )
            triggerEntityDetectionVibration(_dangerLevel.value)
        }
    }

    fun toggleMicListening() {
        if (microphoneAnalyzer.isListening.value) {
            microphoneAnalyzer.stopListening()
        } else {
            microphoneAnalyzer.startListening(viewModelScope) {
                // Auto-trigger spirit query when voice speech burst detected into microphone
                if (!_isGeneratingSpiritResponse.value) {
                    askSpirit("Aura-Akustik-Eingabe über Mikrofon")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(screenStateReceiver)
        } catch (_: Exception) {}
        autoSpiritJob?.cancel()
        realtimeSweepJob?.cancel()
        sensorManager.stopListening()
        microphoneAnalyzer.stopListening()
        soundManager.release()
        spiritTtsManager.release()
    }
}
