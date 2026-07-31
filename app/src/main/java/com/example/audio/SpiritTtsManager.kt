package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpiritTtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _pitch = MutableStateFlow(0.65f) // Eerie low pitch default
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _speechRate = MutableStateFlow(0.85f) // Slightly slow default
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

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

    fun setPitch(newPitch: Float) {
        _pitch.value = newPitch
        if (isInitialized) {
            tts?.setPitch(newPitch)
        }
    }

    fun setSpeechRate(newRate: Float) {
        _speechRate.value = newRate
        if (isInitialized) {
            tts?.setSpeechRate(newRate)
        }
    }

    fun speak(text: String, customPitch: Float? = null, customRate: Float? = null) {
        if (!isInitialized || text.isBlank()) return
        stop()
        val p = customPitch ?: _pitch.value
        val r = customRate ?: _speechRate.value
        tts?.setPitch(p)
        tts?.setSpeechRate(r)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SPIRIT_UTTERANCE_${System.currentTimeMillis()}")
    }

    /**
     * Spielt eine gruselige Phrase als Spirit-Box-Audio über das Text-To-Speech-System ab.
     * Passt Pitch und Geschwindigkeit dynamisch an die EMF-/Gefahren-Stufe an
     * und verknüpft sie mit Sound-Manager statischen Sweep-Effekten.
     */
    fun speakSpiritBoxAudio(
        text: String,
        emfLevel: Float,
        dangerLevel: Int,
        soundManager: SoundManager? = null
    ) {
        if (!isInitialized || text.isBlank()) return

        // Dynamic pitch modulation based on paranormal danger level & EMF
        // Higher danger level -> deeper, uncanny voice pitch (0.45f - 0.75f)
        val calculatedPitch = (0.75f - (dangerLevel * 0.06f) - (emfLevel * 0.02f)).coerceIn(0.4f, 0.95f)
        // Fluctuating speech rate for spirit sweep effect
        val calculatedRate = (0.80f + (dangerLevel * 0.04f)).coerceIn(0.7f, 1.25f)

        // Trigger precursor radio static burst or spectral sound effect via SoundManager
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
