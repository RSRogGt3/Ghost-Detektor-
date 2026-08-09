package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.ui.i18n.AppLanguage
import java.util.Locale

enum class VoicePersona(
    val displayName: String,
    val basePitch: Float,
    val baseRate: Float,
    val description: String
) {
    GEMINI_AI("🤖 Gemini AI Stimme", 1.05f, 1.05f, "Klar, intelligent & zukunftsweisend"),
    EERIE_PHANTOM("👻 Spektral-Phantom", 0.55f, 0.75f, "Tief, unheimlich & schwebend"),
    DEMONIC_ANOMALY("⚡ Dämonische Frequenz", 0.42f, 0.85f, "Finster, verfremdet & bedrohlich"),
    CYBER_SYNTH("👾 Cybernetic AI", 1.25f, 1.15f, "Synthetisch & hochelementar")
}

class SpiritTtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _speechVolume = MutableStateFlow(1.0f) // 0.0f to 1.0f
    val speechVolume: StateFlow<Float> = _speechVolume.asStateFlow()

    private val _isSystemSpeechEnabled = MutableStateFlow(false) // Default false so system events never interrupt Spirit Box speech!
    val isSystemSpeechEnabled: StateFlow<Boolean> = _isSystemSpeechEnabled.asStateFlow()

    private val _pitch = MutableStateFlow(1.05f) // Gemini AI default pitch
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _speechRate = MutableStateFlow(1.05f) // Gemini AI default speed
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _currentPersona = MutableStateFlow(VoicePersona.GEMINI_AI)
    val currentPersona: StateFlow<VoicePersona> = _currentPersona.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.GERMAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            tts?.setPitch(_pitch.value)
            tts?.setSpeechRate(_speechRate.value)
            
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            isInitialized = true
        }
    }

    fun setMuted(muted: Boolean) {
        _isMuted.value = muted
        if (muted) {
            stop()
        }
    }

    fun toggleMute() {
        setMuted(!_isMuted.value)
    }

    fun setSpeechVolume(volume: Float) {
        val clamped = volume.coerceIn(0.0f, 1.0f)
        _speechVolume.value = clamped
        if (clamped == 0.0f) {
            stop()
        }
    }

    fun setVoicePersona(persona: VoicePersona) {
        _currentPersona.value = persona
        setPitch(persona.basePitch)
        setSpeechRate(persona.baseRate)
    }

    fun setSystemSpeechEnabled(enabled: Boolean) {
        _isSystemSpeechEnabled.value = enabled
    }

    fun toggleSystemSpeechEnabled() {
        _isSystemSpeechEnabled.value = !_isSystemSpeechEnabled.value
    }

    fun setLanguage(language: AppLanguage) {
        if (!isInitialized) return
        val locale = when (language) {
            AppLanguage.GERMAN -> Locale.GERMAN
            AppLanguage.ENGLISH -> Locale.US
            AppLanguage.TURKISH -> Locale.forLanguageTag("tr-TR")
            AppLanguage.SPANISH -> Locale.forLanguageTag("es-ES")
            AppLanguage.FRENCH -> Locale.FRENCH
            AppLanguage.ITALIAN -> Locale.ITALIAN
            AppLanguage.POLISH -> Locale.forLanguageTag("pl-PL")
            AppLanguage.DUTCH -> Locale.forLanguageTag("nl-NL")
        }
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.GERMAN)
        }
    }

    fun setPitch(newPitch: Float) {
        val clamped = newPitch.coerceIn(0.3f, 2.0f)
        _pitch.value = clamped
        if (isInitialized) {
            tts?.setPitch(clamped)
        }
    }

    fun setSpeechRate(newRate: Float) {
        val clamped = newRate.coerceIn(0.3f, 2.0f)
        _speechRate.value = clamped
        if (isInitialized) {
            tts?.setSpeechRate(clamped)
        }
    }

    fun speak(text: String, customPitch: Float? = null, customRate: Float? = null) {
        if (_isMuted.value || _speechVolume.value <= 0.01f || !isInitialized || text.isBlank()) return
        stop()
        val p = customPitch ?: _pitch.value
        val r = customRate ?: _speechRate.value
        tts?.setPitch(p)
        tts?.setSpeechRate(r)

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, _speechVolume.value)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "SPIRIT_UTTERANCE_${System.currentTimeMillis()}")
    }

    /**
     * Spielt eine Phrase als Spirit-Box-Audio über das Text-To-Speech-System ab.
     * Nutzt das ausgewählte Stimmen-Profil (z.B. Gemini AI) und passt Pitch / Speed dynamisch an.
     */
    fun speakSpiritBoxAudio(
        text: String,
        emfLevel: Float,
        dangerLevel: Int,
        soundManager: SoundManager? = null,
        isSystemAnnouncement: Boolean = false
    ) {
        if (_isMuted.value || _speechVolume.value <= 0.01f || !isInitialized || text.isBlank()) return

        if (isSystemAnnouncement) {
            if (!_isSystemSpeechEnabled.value) return
            if (_isSpeaking.value) return
        }

        val baseP = _pitch.value
        val baseR = _speechRate.value

        val calculatedPitch = if (_currentPersona.value == VoicePersona.GEMINI_AI) {
            (baseP + (emfLevel * 0.01f) - (dangerLevel * 0.01f)).coerceIn(0.85f, 1.35f)
        } else {
            (baseP - (dangerLevel * 0.04f) - (emfLevel * 0.015f)).coerceIn(0.35f, 1.8f)
        }
        val calculatedRate = (baseR + (dangerLevel * 0.02f)).coerceIn(0.5f, 1.8f)

        soundManager?.let { sm ->
            if (emfLevel > 5.5f) {
                sm.checkAndPlayEerieSpikeSound(emfLevel, threshold = 5.0f)
            } else {
                sm.playStaticPulse()
            }
        }

        speak(text, customPitch = calculatedPitch, customRate = calculatedRate)
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
            _isSpeaking.value = false
        }
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
