package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.AudioWaveformCanvas
import com.example.ui.components.EmfMeter
import com.example.ui.components.SpiritLogOverlayDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.VolumeOff
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.viewmodel.GhostViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings

@Composable
fun SpiritBoxScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val spiritQuestion by viewModel.spiritQuestion.collectAsStateWithLifecycle()
    val spiritResponse by viewModel.spiritResponse.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingSpiritResponse.collectAsStateWithLifecycle()
    val autoSpiritBoxEnabled by viewModel.autoSpiritBoxEnabled.collectAsStateWithLifecycle()
    val showLogOverlay by viewModel.showSpiritLogOverlay.collectAsStateWithLifecycle()
    val spiritLogList by viewModel.spiritPhraseLog.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.spiritTtsManager.isSpeaking.collectAsStateWithLifecycle()
    val currentPitch by viewModel.spiritTtsManager.pitch.collectAsStateWithLifecycle()
    val currentRate by viewModel.spiritTtsManager.speechRate.collectAsStateWithLifecycle()

    val micAmplitude by viewModel.microphoneAnalyzer.amplitude.collectAsStateWithLifecycle()
    val isMicListening by viewModel.microphoneAnalyzer.isListening.collectAsStateWithLifecycle()
    val isRealtimeSweepActive by viewModel.isRealtimeSweepActive.collectAsStateWithLifecycle()
    val realtimeSweepSpeedMs by viewModel.realtimeSweepSpeedMs.collectAsStateWithLifecycle()

    val currentEmf by viewModel.emfLevel.collectAsStateWithLifecycle()
    val currentDanger by viewModel.dangerLevel.collectAsStateWithLifecycle()
    val currentFreq by viewModel.frequencyKhz.collectAsStateWithLifecycle()
    val isEmfSuppressionActive by viewModel.isEmfSuppressionActive.collectAsStateWithLifecycle()
    val isTtsMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    val isSystemSpeechEnabled by viewModel.isSystemSpeechEnabled.collectAsStateWithLifecycle()
    val ttsVolume by viewModel.ttsVolume.collectAsStateWithLifecycle()
    val ttsPitch by viewModel.ttsPitch.collectAsStateWithLifecycle()
    val ttsSpeechRate by viewModel.ttsSpeechRate.collectAsStateWithLifecycle()
    val ttsVoicePersona by viewModel.ttsVoicePersona.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val sensorEmf by viewModel.sensorManager.sensorEmfStrength.collectAsStateWithLifecycle()
    val sensorMotion by viewModel.sensorManager.motionIntensity.collectAsStateWithLifecycle()
    var isRecordingEvp by remember { mutableStateOf(false) }
    var showEvpResult by remember { mutableStateOf(false) }
    val evpResultPhrase = "Geister-Stimme: Lasst mich in Frieden..."

    var inputQuestion by remember { mutableStateOf("") }
    
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = matches?.firstOrNull()
            if (!recognizedText.isNullOrBlank()) {
                inputQuestion = recognizedText
                viewModel.askSpirit(recognizedText)
            }
        }
    }

    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            viewModel.toggleMicListening()
        }
    }

    val questionPresets = UiStrings.getPresetQuestions(appLanguage)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .testTag("spirit_box_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top HUD Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(InfraGreenSurface)
                    .border(1.dp, InfraGreenBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = InfraGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SPIRIT BOX COMMUNICATOR",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSpeaking) InfraGreenPrimary else InfraGreenBorder)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isSpeaking) "SPRECHT..." else "BEREIT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSpeaking) Color.Black else InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // EMF Feldstärke Meter Gauge (Plaziert über Spirit Box)
            EmfMeter(
                emfValue = currentEmf,
                dangerLevel = currentDanger,
                frequencyKhz = currentFreq,
                initiallyMinimized = false,
                isEmfSuppressed = isEmfSuppressionActive,
                onToggleEmfSuppression = { viewModel.toggleEmfSuppression() },
                onNeutralizeEmf = { viewModel.neutralizeEmfSpike() }
            )

            // Microphone Permission Card
            if (!hasMicPermission) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                    border = CardDefaults.outlinedCardBorder(enabled = true),
                    modifier = Modifier.fillMaxWidth().testTag("mic_permission_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MicOff,
                                contentDescription = null,
                                tint = InfraGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "MIKROFON FREIGABE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Zugriff für echte Audio-Analysen erforderlich",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { launcher.launch(Manifest.permission.RECORD_AUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "FREIGEBEN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Audio Waveform Display Canvas
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SPEKTRAL-AUDIO WELLENFORM (TTS)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        Text(
                            text = "STATUS: ${if (isSpeaking) "TEXT-TO-SPEECH AKTIV" else "EMPFANG"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    AudioWaveformCanvas(
                        isActive = true,
                        isScanning = isMicListening || autoSpiritBoxEnabled || isGenerating || isSpeaking,
                        isGenerating = isGenerating,
                        isSpeaking = isSpeaking,
                        liveMicAmplitude = micAmplitude,
                        frequencyKhz = currentFreq,
                        emfLevel = currentEmf,
                        waveColor = InfraGreenPrimary,
                        amplitudeMultiplier = if (isSpeaking || isGenerating) 1.6f else 0.8f
                    )
                }
            }

            
            // Dedicated Spirit Box Voice & Volume Control Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("audio_mute_system_voice_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Row with Mute Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isTtsMuted || ttsVolume <= 0.01f) Icons.Default.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isTtsMuted || ttsVolume <= 0.01f) AlertInfraRed else InfraGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🎙️ SPIRIT BOX SPRACHAUSGABE & STIMME",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = if (isTtsMuted) "STUMM GESCHALTET (MUTED)" else "Lautstärke: ${(ttsVolume * 100).toInt()}% • ${ttsVoicePersona.displayName}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.toggleTtsMute() },
                            modifier = Modifier.testTag("toggle_tts_mute_button")
                        ) {
                            Icon(
                                imageVector = if (isTtsMuted) Icons.Default.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Mute Toggle",
                                tint = if (isTtsMuted) AlertInfraRed else InfraGreenPrimary
                            )
                        }
                    }

                    // Independent Spirit Box Volume Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EIGENE SPRACH-LAUTSTÄRKE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = if (isTtsMuted) "STUMM" else "${(ttsVolume * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isTtsMuted) AlertInfraRed else InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Slider(
                            value = if (isTtsMuted) 0f else ttsVolume,
                            onValueChange = {
                                if (isTtsMuted && it > 0f) {
                                    viewModel.setTtsMuted(false)
                                }
                                viewModel.setTtsVolume(it)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = InfraGreenPrimary,
                                activeTrackColor = InfraGreenPrimary,
                                inactiveTrackColor = InfraGreenSurfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("tts_volume_slider")
                        )
                    }

                    // Voice Persona Selector ("Gib ihr mal ne Stimme von dir")
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "STIMMEN-PROFIL (AI & SPEKTRAL):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            com.example.audio.VoicePersona.values().forEach { persona ->
                                val isSelected = ttsVoicePersona == persona
                                Button(
                                    onClick = { viewModel.setTtsVoicePersona(persona) },
                                    modifier = Modifier.weight(1f).testTag("persona_chip_${persona.name}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) InfraGreenPrimary else InfraGreenSurfaceVariant
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) InfraGreenPrimary else InfraGreenBorder),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = when(persona) {
                                            com.example.audio.VoicePersona.GEMINI_AI -> "🤖 Gemini"
                                            com.example.audio.VoicePersona.EERIE_PHANTOM -> "👻 Phantom"
                                            com.example.audio.VoicePersona.DEMONIC_ANOMALY -> "⚡ Dämon"
                                            com.example.audio.VoicePersona.CYBER_SYNTH -> "👾 Cyber"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.Black else InfraGreenTextPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Pitch & Rate Fine Tuning Sliders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TONHÖHE: ${String.format(java.util.Locale.US, "%.2f", ttsPitch)}x",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                            Slider(
                                value = ttsPitch,
                                onValueChange = { viewModel.setTtsPitch(it) },
                                valueRange = 0.4f..1.6f,
                                colors = SliderDefaults.colors(
                                    thumbColor = InfraGreenPrimary,
                                    activeTrackColor = InfraGreenPrimary
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("tts_pitch_slider")
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TEMPO: ${String.format(java.util.Locale.US, "%.2f", ttsSpeechRate)}x",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                            Slider(
                                value = ttsSpeechRate,
                                onValueChange = { viewModel.setTtsSpeechRate(it) },
                                valueRange = 0.5f..1.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = InfraGreenPrimary,
                                    activeTrackColor = InfraGreenPrimary
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("tts_rate_slider")
                            )
                        }
                    }

                    // Action Buttons (Test Voice & System Voice Protection)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Test Voice Button
                        Button(
                            onClick = { viewModel.testSpiritVoice() },
                            modifier = Modifier.weight(1f).testTag("test_spirit_voice_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🔊 TEST STIMME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                    // Haptic Vibration Switch Row for Spirit Box Entity Detection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📳 HAPTISCHE ENTITÄTEN-VIBRATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                        androidx.compose.material3.Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration() },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = InfraGreenPrimary,
                                checkedTrackColor = InfraGreenSurfaceVariant,
                                uncheckedThumbColor = InfraGreenTextPrimaryVariant,
                                uncheckedTrackColor = InfraGreenSurface
                            ),
                            modifier = Modifier.testTag("spirit_box_vibration_switch")
                        )
                    }

                    // System Voice Protection Switch
                    Button(
                            onClick = { viewModel.toggleSystemSpeechEnabled() },
                            modifier = Modifier.weight(1f).testTag("toggle_system_speech_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InfraGreenSurfaceVariant
                            ),
                            border = BorderStroke(1.dp, if (isSystemSpeechEnabled) InfraGreenPrimary else InfraGreenBorder),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = if (isSystemSpeechEnabled) InfraGreenPrimary else InfraGreenTextPrimaryVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSystemSpeechEnabled) "📢 SYSTEM: AN" else "🛡️ SYSTEM: AUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSystemSpeechEnabled) InfraGreenPrimary else InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Real-Time Radio Frequency Sweep Control Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("realtime_sweep_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isRealtimeSweepActive) InfraGreenPrimary else InfraGreenTextPrimaryVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ECHTZEIT RADIO-SWEEP (FM/AM)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f", currentFreq)} MHz • Rauschen: ${realtimeSweepSpeedMs}ms",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleRealtimeSweep() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRealtimeSweepActive) InfraGreenPrimary else InfraGreenSurfaceVariant
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isRealtimeSweepActive) "SWEEP: AN" else "SWEEP: AUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isRealtimeSweepActive) Color.Black else InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    if (isRealtimeSweepActive) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "SWEEP-GESCHWINDIGKEIT:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(100L to "100ms", 200L to "200ms", 350L to "350ms", 500L to "500ms").forEach { (speed, label) ->
                                    val isSelected = realtimeSweepSpeedMs == speed
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) InfraGreenPrimary else InfraGreenSurfaceVariant)
                                            .border(1.dp, if (isSelected) InfraGreenPrimary else InfraGreenBorder, RoundedCornerShape(6.dp))
                                            .clickable { viewModel.setRealtimeSweepSpeed(speed) }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.Black else InfraGreenTextPrimary,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Live Microphone Input & Duden Sprach-AI Status Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("live_mic_ai_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMicListening) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = null,
                                tint = InfraGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ECHTZEIT MIKROFON & DUDEN SPRACH-AI",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (hasMicPermission) {
                                    viewModel.toggleMicListening()
                                } else {
                                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMicListening) InfraGreenPrimary else InfraGreenSurfaceVariant
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (!hasMicPermission) "FREIGEBEN" else if (isMicListening) "MIKROFON: AN" else "MIKROFON: AUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isMicListening) Color.Black else InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    if (isMicListening) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "MIKROFON PEGGEL (DB):",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "${(micAmplitude * 100).toInt()}% PEAK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = InfraGreenPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            // Dynamic Live Microphone Level Meter Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(InfraGreenSurfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(micAmplitude.coerceIn(0.05f, 1f))
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (micAmplitude > 0.4f) Color.Red else InfraGreenPrimary)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(InfraGreenSurfaceVariant)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "✨ ERWEITERTE DUDEN SPRACH-AI: Nutzt gemini-3.5-flash & reichhaltigen deutschen Wortschatz (Substantive, Verben & Parapsychologie-Begriffe) für tiefgründige Geist-Dialoge.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }

            // Sensor-Based TTS Phrase Generator Control Card
            // EVP Recorder Widget
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth().testTag("evp_recorder_card")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = InfraGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EVP-REKORDER", style = MaterialTheme.typography.titleSmall.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Text("Nimmt Umgebungsgeräusche auf und filtert verborgene Stimmen heraus (EVP).", style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.sp))
                    if (showEvpResult) {
                        Text("ERFASST: $evpResultPhrase", style = MaterialTheme.typography.bodyMedium.copy(color = com.example.ui.theme.AlertInfraRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                    }
                    Button(
                        onClick = {
                            if (isRecordingEvp) {
                                isRecordingEvp = false
                                showEvpResult = true
                                viewModel.soundManager.playGhostFreedSound()
                                viewModel.askSpirit("Hast du eine Nachricht hinterlassen?")
                            } else {
                                isRecordingEvp = true
                                showEvpResult = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRecordingEvp) com.example.ui.theme.AlertInfraRed else InfraGreenSurfaceVariant),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(if (isRecordingEvp) "AUFNAHME STOPPEN" else "EVP-AUFNAHME STARTEN", color = if (isRecordingEvp) Color.White else InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = UiStrings.getSensorTtsCardTitle(appLanguage),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(InfraGreenSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.1f", currentEmf)} mG • Lvl $currentDanger",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = UiStrings.getSensorTtsDesc(appLanguage, String.format(java.util.Locale.US, "%.1f", sensorMotion), String.format(java.util.Locale.US, "%.1f", currentFreq)),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    Button(
                        onClick = { viewModel.generateAndPlaySensorCreepyPhrase() },
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("generate_sensor_phrase_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = UiStrings.getGenerateSensorPhraseBtn(appLanguage, isGenerating),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Spirit Transmission Dialogue Box
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurfaceVariant),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "GEIST BOTSCHAFT (ECHTZEIT):",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    if (spiritQuestion.isNotBlank()) {
                        Text(
                            text = "Frage: \"$spiritQuestion\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    if (isGenerating) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp).width(20.dp),
                                color = InfraGreenPrimary,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Durchsuche Äther-Frequenzen...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    } else if (spiritResponse.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "\"$spiritResponse\"",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    lineHeight = 22.sp
                                )
                            )

                            IconButton(onClick = {
                                viewModel.spiritTtsManager.speakSpiritBoxAudio(
                                    text = spiritResponse,
                                    emfLevel = currentEmf,
                                    dangerLevel = currentDanger,
                                    soundManager = viewModel.soundManager
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Vorlesen",
                                    tint = InfraGreenPrimary
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Stelle eine Frage unten oder tippe auf 'SENSOR-PHRASE GENERIEREN'...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            // Auto Spirit Box & Random 10s Mode Control Card
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AUTO-FRAGEN (10 SEK. INTERVALL):",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (autoSpiritBoxEnabled) "Aktiv: Zufällige Fragen & Sätze alle 10s" else "Inaktiv: Manuell fragen",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (autoSpiritBoxEnabled) InfraGreenPrimary else InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.askRandomQuestion() },
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ZUFALL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleAutoSpiritBox() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (autoSpiritBoxEnabled) InfraGreenPrimary else InfraGreenSurfaceVariant
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (autoSpiritBoxEnabled) "AUTO: AN" else "AUTO: AUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (autoSpiritBoxEnabled) Color.Black else InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Logbook Overlay Access Button
            Button(
                onClick = { viewModel.toggleSpiritLogOverlay(true) },
                colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, InfraGreenPrimary, RoundedCornerShape(10.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(InfraGreenPrimary, RoundedCornerShape(50))
                    )
                    Text(
                        text = "PARANORMALES LOGBUCH ÖFFNEN (${spiritLogList.size} PHRASEN)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            if (showLogOverlay) {
                SpiritLogOverlayDialog(
                    logs = spiritLogList,
                    onDismiss = { viewModel.toggleSpiritLogOverlay(false) },
                    onClearLogs = { viewModel.clearSpiritLog() }
                )
            }

            // Preset Question Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SCHNELL-FRAGEN (PRESETS):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InfraGreenTextPrimaryVariant,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    questionPresets.take(3).forEach { q ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(InfraGreenSurface)
                                .border(1.dp, InfraGreenBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    inputQuestion = q
                                    viewModel.askSpirit(q)
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = q,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    questionPresets.drop(3).take(3).forEach { q ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(InfraGreenSurface)
                                .border(1.dp, InfraGreenBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    inputQuestion = q
                                    viewModel.askSpirit(q)
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = q,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Custom Question Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputQuestion,
                    onValueChange = { inputQuestion = it },
                    placeholder = { Text(UiStrings.getQuestionPlaceholder(appLanguage), color = InfraGreenTextPrimaryVariant) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("spirit_question_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InfraGreenPrimary,
                        unfocusedBorderColor = InfraGreenBorder,
                        focusedTextColor = InfraGreenTextPrimary,
                        unfocusedTextColor = InfraGreenTextPrimary
                    )
                )

                IconButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE") // Defaulting to German
                        }
                        try {
                            speechRecognizerLauncher.launch(intent)
                        } catch (e: Exception) {
                            // Ignoriert, falls keine Speech-App installiert ist
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(InfraGreenSurfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.dp, InfraGreenBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Spracheingabe",
                        tint = InfraGreenPrimary
                    )
                }

                Button(
                    onClick = {
                        viewModel.askSpirit(inputQuestion)
                    },
                    modifier = Modifier.height(56.dp).testTag("send_question_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = UiStrings.getSendBtn(appLanguage),
                        tint = Color.Black
                    )
                }
            }

            // TTS Voice Pitch Adjuster
            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = UiStrings.getVoicePitchLabel(appLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace)
                        )
                        Text(
                            text = String.format("%.2fx", currentPitch),
                            style = MaterialTheme.typography.labelSmall.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )
                    }

                    Slider(
                        value = currentPitch,
                        onValueChange = { viewModel.spiritTtsManager.setPitch(it) },
                        valueRange = 0.4f..1.2f,
                        colors = SliderDefaults.colors(
                            thumbColor = InfraGreenPrimary,
                            activeTrackColor = InfraGreenPrimary,
                            inactiveTrackColor = InfraGreenBorder
                        )
                    )
                }
            }
        }
    }
}
