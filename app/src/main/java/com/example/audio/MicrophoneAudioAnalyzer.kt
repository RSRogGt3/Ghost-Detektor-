package com.example.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class MicrophoneAudioAnalyzer(private val context: Context) {

    private val _amplitude = MutableStateFlow(0f) // 0.0 to 1.0
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _speechDetectedEvent = MutableStateFlow(false)
    val speechDetectedEvent: StateFlow<Boolean> = _speechDetectedEvent.asStateFlow()

    private val _speechThreshold = MutableStateFlow(0.15f)
    val speechThreshold: StateFlow<Float> = _speechThreshold.asStateFlow()

    private var audioRecord: AudioRecord? = null
    private var recordJob: Job? = null

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    fun setSpeechThreshold(threshold: Float) {
        _speechThreshold.value = threshold.coerceIn(0.04f, 0.50f)
    }

    fun startListening(coroutineScope: CoroutineScope, onSpeechDetected: (() -> Unit)? = null) {
        if (_isListening.value) return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        try {
            val bufferSize = if (minBufferSize > 0) minBufferSize * 2 else 4096
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                audioRecord = AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(audioFormat)
                            .setSampleRate(sampleRate)
                            .setChannelMask(channelConfig)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setContext(context)
                    .build()
            } else {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )
            }

            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord?.startRecording()
                _isListening.value = true

                recordJob = coroutineScope.launch(Dispatchers.IO) {
                    val buffer = ShortArray(1024)
                    var speechFrameCount = 0
                    var silentFrameCount = 0

                    while (isActive && _isListening.value) {
                        try {
                            val record = audioRecord ?: break
                            if (record.recordingState != AudioRecord.RECORDSTATE_RECORDING) break
                            val read = record.read(buffer, 0, buffer.size)
                            if (read > 0) {
                                var sumSquares = 0.0
                                for (i in 0 until read) {
                                    val sample = buffer[i].toDouble()
                                    sumSquares += sample * sample
                                }
                                val rms = sqrt(sumSquares / read)
                                // Normalized amplitude approx 0 to 1
                                val normalizedAmp = (rms / 7500.0).toFloat().coerceIn(0f, 1f)
                                _amplitude.value = normalizedAmp

                                // Speech burst detection logic (above threshold)
                                val currentThresh = _speechThreshold.value
                                if (normalizedAmp > currentThresh) {
                                    speechFrameCount++
                                    silentFrameCount = 0
                                } else {
                                    if (speechFrameCount > 4) { // was speaking for a moment
                                        silentFrameCount++
                                        if (silentFrameCount > 8) { // now paused after speaking
                                            _speechDetectedEvent.value = true
                                            onSpeechDetected?.invoke()
                                            speechFrameCount = 0
                                            silentFrameCount = 0
                                        }
                                    } else {
                                        speechFrameCount = 0
                                    }
                                }
                            }
                        } catch (_: Exception) {
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopListening()
        }
    }

    fun stopListening() {
        _isListening.value = false
        recordJob?.cancel()
        recordJob = null
        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
        _amplitude.value = 0f
    }

    fun consumeSpeechEvent() {
        _speechDetectedEvent.value = false
    }
}
