package com.example.ui.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.SpiritAiEngine
import com.example.audio.SoundManager
import com.example.audio.SpiritTtsManager
import com.example.data.GhostDatabase
import com.example.data.GhostDetectionEntity
import com.example.data.GhostRepository
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

class GhostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GhostRepository
    val soundManager = SoundManager()
    val spiritTtsManager = SpiritTtsManager(application)
    private val spiritAiEngine = SpiritAiEngine()
    val sensorManager = GhostSensorManager(application)
    
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
    }

    // Room DB Flow
    val allDetections: StateFlow<List<GhostDetectionEntity>>

    private val _appLanguage = MutableStateFlow(AppLanguage.GERMAN)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
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

    val compassAzimuth: StateFlow<Float> = sensorManager.compassAzimuth
    
    val satelliteCount: StateFlow<Int> = sensorManager.satelliteCount
    val currentLocation: StateFlow<android.location.Location?> = sensorManager.currentLocation

    private val _showCrtOverlay = MutableStateFlow(true)
    val showCrtOverlay: StateFlow<Boolean> = _showCrtOverlay.asStateFlow()

    private val _isCameraBackgroundEnabled = MutableStateFlow(true)
    val isCameraBackgroundEnabled: StateFlow<Boolean> = _isCameraBackgroundEnabled.asStateFlow()

    private val _audioFeedbackEnabled = MutableStateFlow(true)
    val audioFeedbackEnabled: StateFlow<Boolean> = _audioFeedbackEnabled.asStateFlow()

    // Spirit Box UI State
    private val _spiritQuestion = MutableStateFlow("")
    val spiritQuestion: StateFlow<String> = _spiritQuestion.asStateFlow()

    private val _spiritResponse = MutableStateFlow("")
    val spiritResponse: StateFlow<String> = _spiritResponse.asStateFlow()

    private val _isGeneratingSpiritResponse = MutableStateFlow(false)
    val isGeneratingSpiritResponse: StateFlow<Boolean> = _isGeneratingSpiritResponse.asStateFlow()

    private val _autoSpiritBoxEnabled = MutableStateFlow(false)
    val autoSpiritBoxEnabled: StateFlow<Boolean> = _autoSpiritBoxEnabled.asStateFlow()

    // Selected Ghost detail for modal dialog
    private val _selectedGhostDetail = MutableStateFlow<GhostDetectionEntity?>(null)
    val selectedGhostDetail: StateFlow<GhostDetectionEntity?> = _selectedGhostDetail.asStateFlow()

    private var scanJob: Job? = null
    private var chartHistoryJob: Job? = null

    init {
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
                val matchesType = typeFilter == "ALLE" || item.type.equals(typeFilter, ignoreCase = true)
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

        sensorManager.startListening()
        startScanningLoop()
        startChartHistoryLoop()
    }

    private fun startChartHistoryLoop() {
        chartHistoryJob?.cancel()
        chartHistoryJob = viewModelScope.launch {
            while (true) {
                delay(250)
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
                delay(1200)
                if (_isScanning.value) {
                    val rawSensorEmf = sensorManager.sensorEmfStrength.value
                    val motion = sensorManager.motionIntensity.value

                    // Blend hardware sensor readings with procedural atmospheric variance
                    val motionBoost = (motion * 0.8f)
                    val baseEmf = (rawSensorEmf + motionBoost).coerceIn(1.0f, 9.5f)
                    val isSpike = Random.nextFloat() > (if (motion > 2.0f) 0.4f else 0.7f)
                    val newEmf = if (isSpike) (baseEmf + Random.nextFloat() * 4f).coerceAtMost(9.9f) else baseEmf
                    _emfLevel.value = String.format(java.util.Locale.US, "%.1f", newEmf).toFloat()

                    _dangerLevel.value = when {
                        newEmf >= 8.0f -> 5
                        newEmf >= 6.0f -> 4
                        newEmf >= 4.0f -> 3
                        newEmf >= 2.0f -> 2
                        else -> 1
                    }

                    _frequencyKhz.value = String.format(java.util.Locale.US, "%.1f", 14.0f + Random.nextFloat() * 70.0f).toFloat()

                    // Generate random radar blips based on EMF and motion
                    val count = if (newEmf > 5f) Random.nextInt(2, 5) else Random.nextInt(1, 3)
                    val newBlips = (1..count).map { id ->
                        val danger = if (newEmf > 7f) Random.nextInt(4, 6) else Random.nextInt(1, 4)
                        RadarBlip(
                            id = "blip_$id",
                            angleDegrees = Random.nextFloat() * 360f,
                            distanceRatio = Random.nextFloat() * 0.7f + 0.15f,
                            dangerLevel = danger
                        )
                    }
                    _radarBlips.value = newBlips

                    if (_audioFeedbackEnabled.value) {
                        soundManager.playGeigerClick()
                        if (newEmf >= 7.5f) {
                            soundManager.playThreatAlert()
                            // Intense Haptic Kick for high spikes
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(100)
                            }
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
                dangerLevel = danger
            )

            _spiritResponse.value = creepyPhrase
            _isGeneratingSpiritResponse.value = false

            // Voice synthesis with Spirit-Box audio sweep & dynamic pitch
            spiritTtsManager.speakSpiritBoxAudio(
                text = creepyPhrase,
                emfLevel = emf,
                dangerLevel = danger,
                soundManager = soundManager
            )
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
                emfLevel = _emfLevel.value
            )

            _spiritResponse.value = response
            _isGeneratingSpiritResponse.value = false

            // Vocalize through Text-to-Speech with Spirit Box audio effects
            spiritTtsManager.speakSpiritBoxAudio(
                text = response,
                emfLevel = _emfLevel.value,
                dangerLevel = _dangerLevel.value,
                soundManager = soundManager
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopListening()
        soundManager.release()
        spiritTtsManager.release()
    }
}
