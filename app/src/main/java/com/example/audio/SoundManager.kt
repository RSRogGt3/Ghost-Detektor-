package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class SoundManager {

    private var toneGen: ToneGenerator? = null
    private var isMuted: Boolean = false
    private val scope = CoroutineScope(Dispatchers.Default)

    private var lastEerieSoundTime: Long = 0L
    private val eerieSoundCooldownMs: Long = 4000L // Minimum delay between eerie ambient triggers

    init {
        try {
            toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            toneGen = null
        }
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun playRadarPing() {
        if (isMuted) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
            } catch (_: Exception) {}
        }
    }

    fun playGeigerClick() {
        if (isMuted) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 15)
            } catch (_: Exception) {}
        }
    }

    fun playThreatAlert() {
        if (isMuted) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_SUP_ERROR, 150)
            } catch (_: Exception) {}
        }
    }

    fun playStaticPulse() {
        if (isMuted) return
        scope.launch {
            try {
                // Short low frequency pulse
                playSineTone(frequencyHz = 120, durationMs = 60, volume = 0.3f)
            } catch (_: Exception) {}
        }
    }

    /**
     * Plays short radio static burst for real-time Spirit Box frequency sweep noise.
     */
    fun playRadioStaticSweep() {
        if (isMuted) return
        scope.launch {
            try {
                val sampleRate = 16000
                val durationMs = 45
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ByteArray(2 * numSamples)
                var idx = 0
                for (i in 0 until numSamples) {
                    // White noise with envelope
                    val noise = (Random.nextFloat() * 2f - 1f) * 0.25f
                    val sampleVal = (noise * 32767).toInt().coerceIn(-32767, 32767).toShort()
                    buffer[idx++] = (sampleVal.toInt() and 0x00ff).toByte()
                    buffer[idx++] = (sampleVal.toInt() and 0xff00 ushr 8).toByte()
                }
                playPcmTrack(buffer, sampleRate, durationMs.toLong())
            } catch (_: Exception) {}
        }
    }

    /**
     * Checks EMF spike threshold and periodically triggers eerie, ambient ghost-hunting sound effects.
     */
    fun checkAndPlayEerieSpikeSound(emfLevel: Float, threshold: Float = 6.5f) {
        if (isMuted || emfLevel < threshold) return

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEerieSoundTime < eerieSoundCooldownMs) return

        lastEerieSoundTime = currentTime
        scope.launch {
            try {
                when (Random.nextInt(3)) {
                    0 -> playSubBassDrone(durationMs = 900, baseFreq = 65f + (emfLevel * 3f), volume = 0.45f)
                    1 -> playSpectralSweep(durationMs = 800, startFreq = 350f + (emfLevel * 20f), endFreq = 160f, volume = 0.35f)
                    else -> playDissonantTritonePulse(durationMs = 750, rootFreq = 180f + (emfLevel * 10f), volume = 0.4f)
                }
            } catch (_: Exception) {}
        }
    }

    fun playGhostFreedSound() {
        if (isMuted) return
        scope.launch {
            try {
                playSpectralSweep(durationMs = 1500, startFreq = 200f, endFreq = 900f, volume = 0.5f)
            } catch (_: Exception) {}
        }
    }

    private fun playSubBassDrone(durationMs: Int, baseFreq: Float, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        var idx = 0
        var phase = 0.0
        val attackSamples = (sampleRate * 0.1).toInt()
        val releaseSamples = (sampleRate * 0.2).toInt()

        for (i in 0 until numSamples) {
            // Envelope calculation for smooth start and end fade
            val env = when {
                i < attackSamples -> i.toFloat() / attackSamples
                i > numSamples - releaseSamples -> (numSamples - i).toFloat() / releaseSamples
                else -> 1.0f
            }

            // Frequency modulation for pitch wobble / eerie sub-bass rumble
            val mod = sin(2.0 * Math.PI * i * 3.5 / sampleRate) * 6.0
            val currentFreq = baseFreq + mod
            phase += 2.0 * Math.PI * currentFreq / sampleRate

            // Sub-harmonic octave lower
            val subHarmonic = sin(phase * 0.5) * 0.4
            val sampleVal = ((sin(phase) + subHarmonic) * 0.7 * 32767 * volume * env).toInt().coerceIn(-32767, 32767).toShort()

            generatedSnd[idx++] = (sampleVal.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = (sampleVal.toInt() and 0xff00 ushr 8).toByte()
        }

        playPcmTrack(generatedSnd, sampleRate, durationMs.toLong())
    }

    private fun playSpectralSweep(durationMs: Int, startFreq: Float, endFreq: Float, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        var idx = 0
        var phase = 0.0
        val attackSamples = (sampleRate * 0.08).toInt()
        val releaseSamples = (sampleRate * 0.25).toInt()

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress.toFloat()

            // Envelope
            val env = when {
                i < attackSamples -> i.toFloat() / attackSamples
                i > numSamples - releaseSamples -> (numSamples - i).toFloat() / releaseSamples
                else -> 1.0f
            }

            // Tremolo & subtle noise flutter for phantom ghost voice EVP aesthetic
            val tremolo = 0.85 + 0.15 * sin(2.0 * Math.PI * i * 12.0 / sampleRate)
            val noise = (Random.nextFloat() * 2f - 1f) * 0.08f

            phase += 2.0 * Math.PI * currentFreq / sampleRate
            val tone = sin(phase) + noise
            val sampleVal = (tone * 32767 * volume * env * tremolo).toInt().coerceIn(-32767, 32767).toShort()

            generatedSnd[idx++] = (sampleVal.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = (sampleVal.toInt() and 0xff00 ushr 8).toByte()
        }

        playPcmTrack(generatedSnd, sampleRate, durationMs.toLong())
    }

    private fun playDissonantTritonePulse(durationMs: Int, rootFreq: Float, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        // Tritone interval ratio is sqrt(2) approx 1.4142 for unsettling paranormal dissonance
        val tritoneFreq = rootFreq * 1.4142f

        var idx = 0
        var phase1 = 0.0
        var phase2 = 0.0
        val attackSamples = (sampleRate * 0.05).toInt()
        val releaseSamples = (sampleRate * 0.2).toInt()

        for (i in 0 until numSamples) {
            val env = when {
                i < attackSamples -> i.toFloat() / attackSamples
                i > numSamples - releaseSamples -> (numSamples - i).toFloat() / releaseSamples
                else -> 1.0f
            }

            phase1 += 2.0 * Math.PI * rootFreq / sampleRate
            phase2 += 2.0 * Math.PI * tritoneFreq / sampleRate

            val combined = (sin(phase1) + sin(phase2)) * 0.5
            val sampleVal = (combined * 32767 * volume * env).toInt().coerceIn(-32767, 32767).toShort()

            generatedSnd[idx++] = (sampleVal.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = (sampleVal.toInt() and 0xff00 ushr 8).toByte()
        }

        playPcmTrack(generatedSnd, sampleRate, durationMs.toLong())
    }

    private fun playSineTone(frequencyHz: Int, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        var idx = 0
        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i / (sampleRate / frequencyHz.toDouble())
            val sampleVal = (sin(angle) * 32767 * volume).toInt().toShort()
            generatedSnd[idx++] = (sampleVal.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = (sampleVal.toInt() and 0xff00 ushr 8).toByte()
        }

        playPcmTrack(generatedSnd, sampleRate, durationMs.toLong())
    }

    private fun playPcmTrack(buffer: ByteArray, sampleRate: Int, durationMs: Long) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                kotlinx.coroutines.delay(durationMs + 100)
                audioTrack.release()
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGen?.release()
            toneGen = null
        } catch (_: Exception) {}
    }
}
