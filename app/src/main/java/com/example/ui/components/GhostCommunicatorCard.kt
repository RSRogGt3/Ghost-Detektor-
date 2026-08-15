package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.viewmodel.GhostViewModel

@Composable
fun GhostCommunicatorCard(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage = viewModel.appLanguage.value

    val isListening by viewModel.microphoneAnalyzer.isListening.collectAsStateWithLifecycle()
    val micAmplitude by viewModel.microphoneAnalyzer.amplitude.collectAsStateWithLifecycle()
    val speechThreshold by viewModel.microphoneAnalyzer.speechThreshold.collectAsStateWithLifecycle()
    val isCommunicatorActive by viewModel.isCommunicatorActive.collectAsStateWithLifecycle()
    val communicatorStatusText by viewModel.communicatorStatusText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingSpiritResponse.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.spiritTtsManager.isSpeaking.collectAsStateWithLifecycle()
    val spiritResponse by viewModel.spiritResponse.collectAsStateWithLifecycle()
    val spiritQuestion by viewModel.spiritQuestion.collectAsStateWithLifecycle()
    val currentEmf by viewModel.emfLevel.collectAsStateWithLifecycle()
    val currentDanger by viewModel.dangerLevel.collectAsStateWithLifecycle()

    var customQuestionText by remember { mutableStateOf("") }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            viewModel.toggleCommunicatorMode(true)
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = matches?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                customQuestionText = spoken
                viewModel.askCommunicator(spoken)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val quickQuestions = listOf(
        "Ist jemand im Raum?",
        "Wie lautet dein Name?",
        "Warum spukst du hier?",
        "Bist du gut oder böse?",
        "Brauchst du Hilfe?",
        "Gib uns ein Zeichen!"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
        border = BorderStroke(1.5.dp, if (isCommunicatorActive) InfraGreenPrimary else InfraGreenBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ghost_communicator_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Title + Status Pulse Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .then(if (isCommunicatorActive || isListening) Modifier.scale(pulseScale) else Modifier)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSpeaking -> AlertInfraRed
                                    isGenerating -> Color(0xFFFFCC00)
                                    isCommunicatorActive || isListening -> InfraGreenPrimary
                                    else -> Color.Gray
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "📻 ECHTZEIT-KOMMUNIKATOR",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = if (isCommunicatorActive) "Zwei-Wege EVP-Sprachfunk AKTIV" else "Standby: Mikrofon bereit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCommunicatorActive) InfraGreenPrimary else InfraGreenSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isSpeaking) "GEIST SPRICHT" else if (isGenerating) "ANALYSE..." else if (isCommunicatorActive) "LAUSCHT..." else "INAKTIV",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isCommunicatorActive) Color.Black else InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Status Banner Message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(InfraGreenSurfaceVariant)
                    .border(1.dp, InfraGreenBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.RecordVoiceOver else if (isListening) Icons.Default.GraphicEq else Icons.Default.Sensors,
                        contentDescription = null,
                        tint = InfraGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = communicatorStatusText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Real-Time Live Microphone Volume & Peak Meter
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MIKROFON-EVP AKUSTIK-PEGEL:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "${(micAmplitude * 100).toInt()}% PEAK • Schwellenwert: ${(speechThreshold * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (micAmplitude > speechThreshold) AlertInfraRed else InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                // Dynamic Graphic Equalizer Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(InfraGreenSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (isListening || isCommunicatorActive) micAmplitude.coerceIn(0.04f, 1f) else 0.02f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        InfraGreenPrimary,
                                        if (micAmplitude > 0.4f) AlertInfraRed else InfraGreenPrimary
                                    )
                                )
                            )
                    )
                }
            }

            // Main Action Controls: Master Mic Toggle + Voice Recognition Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Live Continuous Microphone Toggle
                Button(
                    onClick = {
                        if (!hasMicPermission) {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.toggleCommunicatorMode()
                        }
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("toggle_live_mic_communicator_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCommunicatorActive || isListening) InfraGreenPrimary else InfraGreenSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isCommunicatorActive || isListening) InfraGreenPrimary else InfraGreenBorder)
                ) {
                    Icon(
                        imageVector = if (isCommunicatorActive || isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = null,
                        tint = if (isCommunicatorActive || isListening) Color.Black else InfraGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!hasMicPermission) "MIKROFON FREIGEBEN" else if (isCommunicatorActive || isListening) "MIKROFON: AN" else "LIVE MIKROFON: AUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isCommunicatorActive || isListening) Color.Black else InfraGreenTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // 2. Speech Recognizer Button (Push-to-Talk)
                Button(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Stelle deine Frage an die Geisterwelt...")
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (_: Exception) {
                            viewModel.askCommunicator("Wer ist im Raum anwesend?")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("push_to_talk_speech_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, InfraGreenPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = InfraGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SPRACHEINGABE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Sensitivity / Detection Threshold Slider
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "EMPFINDLICHKEIT (STIMMERKENNUNG):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = when {
                            speechThreshold < 0.10f -> "ULTRA-SENSITIV (FLÜSTERN)"
                            speechThreshold < 0.20f -> "NORMAL"
                            else -> "HOCH (RAUSCHFILTER)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
                Slider(
                    value = speechThreshold,
                    onValueChange = { viewModel.setCommunicatorSpeechThreshold(it) },
                    valueRange = 0.05f..0.35f,
                    colors = SliderDefaults.colors(
                        thumbColor = InfraGreenPrimary,
                        activeTrackColor = InfraGreenPrimary,
                        inactiveTrackColor = InfraGreenSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("communicator_sensitivity_slider")
                )
            }

            // Quick Questions Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SCHNELL-FRAGEN AN DEN KOMMUNIKATOR:",
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
                    quickQuestions.take(3).forEach { question ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(InfraGreenSurfaceVariant)
                                .border(1.dp, InfraGreenBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    customQuestionText = question
                                    viewModel.askCommunicator(question)
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickQuestions.drop(3).take(3).forEach { question ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(InfraGreenSurfaceVariant)
                                .border(1.dp, InfraGreenBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    customQuestionText = question
                                    viewModel.askCommunicator(question)
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InfraGreenTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Custom Text Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customQuestionText,
                    onValueChange = { customQuestionText = it },
                    placeholder = {
                        Text(
                            text = "Frage eintippen oder ins Mikrofon sprechen...",
                            color = InfraGreenTextPrimaryVariant,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("communicator_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InfraGreenPrimary,
                        unfocusedBorderColor = InfraGreenBorder,
                        focusedTextColor = InfraGreenTextPrimary,
                        unfocusedTextColor = InfraGreenTextPrimary
                    ),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (customQuestionText.isNotBlank()) {
                            viewModel.askCommunicator(customQuestionText)
                        }
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("communicator_send_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Senden",
                        tint = Color.Black
                    )
                }
            }

            // Active Live Dialogue Box (User Question & Ghost Voice Output)
            if (spiritQuestion.isNotBlank() || spiritResponse.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, InfraGreenBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (spiritQuestion.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🗣️ DU: ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "\"$spiritQuestion\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = InfraGreenTextPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }

                        if (isGenerating) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = InfraGreenPrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Entschlüssele paranormale EVP-Frequenz...",
                                    style = MaterialTheme.typography.bodySmall.copy(
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "👻 GEISTER-STIMME (EVP):",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AlertInfraRed,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "\"$spiritResponse\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = InfraGreenPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            lineHeight = 20.sp
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.spiritTtsManager.speakSpiritBoxAudio(
                                            text = spiritResponse,
                                            emfLevel = currentEmf,
                                            dangerLevel = currentDanger,
                                            soundManager = viewModel.soundManager
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Nochmal abspielen",
                                        tint = InfraGreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
