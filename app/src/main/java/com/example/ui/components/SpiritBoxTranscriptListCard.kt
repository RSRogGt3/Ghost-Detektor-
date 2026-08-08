package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.SpiritLogEntry
import com.example.ui.theme.AlertInfraRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpiritBoxTranscriptListCard(
    logs: List<SpiritLogEntry>,
    filterMode: FilterMode,
    isGenerating: Boolean,
    isSpeaking: Boolean,
    onAskQuestion: (String) -> Unit,
    onTriggerAutoSweep: () -> Unit,
    onRespeak: (SpiritLogEntry) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var isCardMinimized by remember { mutableStateOf(false) }
    var showOverlayDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val listState = rememberLazyListState()

    // Auto-scroll to top when a new transcript arrives
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    if (showOverlayDialog) {
        SpiritLogOverlayDialog(
            logs = logs,
            onDismiss = { showOverlayDialog = false },
            onClearLogs = onClearLogs
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF09120C)),
        border = CardDefaults.outlinedCardBorder(enabled = true),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("spirit_box_transcript_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Title & Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = filterMode.primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "SPIRIT-BOX TRANSKRIPTE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )

                    Surface(
                        color = filterMode.primaryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${logs.size}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = filterMode.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Minimize / Expand Whole Card Toggle Button
                    IconButton(
                        onClick = { isCardMinimized = !isCardMinimized },
                        modifier = Modifier.testTag("minimize_transcript_card_button")
                    ) {
                        Icon(
                            imageVector = if (isCardMinimized) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isCardMinimized) "Karte ausklappen" else "Karte minimieren",
                            tint = filterMode.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (!isCardMinimized) {
                        // Height Expand / Collapse Toggle Button
                        IconButton(
                            onClick = { isExpanded = !isExpanded }
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Einklappen" else "Verlauf vergrößern",
                                tint = filterMode.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Open Full Overlay Logbook Dialog
                    IconButton(
                        onClick = { showOverlayDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Ganzes Logbuch öffnen",
                            tint = filterMode.primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share Transcripts Export Button
                    IconButton(
                        onClick = { exportAndShareSpiritTranscripts(context, logs) },
                        enabled = logs.isNotEmpty(),
                        modifier = Modifier.testTag("share_spirit_transcripts_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Transkripte teilen / exportieren",
                            tint = if (logs.isNotEmpty()) filterMode.primaryColor else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Trigger Instant Äther Sweep
                    IconButton(
                        onClick = onTriggerAutoSweep,
                        enabled = !isGenerating
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Äther-Scan auslösen",
                            tint = if (isGenerating) Color.Gray else filterMode.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Clear Transcript Logs
                    IconButton(
                        onClick = onClearLogs,
                        enabled = logs.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Transkripte löschen",
                            tint = if (logs.isNotEmpty()) AlertInfraRed else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (!isCardMinimized) {

            // Interactive Ask Spirit Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("transcript_question_input"),
                    placeholder = {
                        Text(
                            text = "Frage an Geist eingeben...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    },
                    singleLine = true,
                    enabled = !isGenerating,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = filterMode.primaryColor,
                        unfocusedBorderColor = filterMode.primaryColor.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = filterMode.primaryColor
                    )
                )

                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onAskQuestion(inputText)
                            inputText = ""
                        } else {
                            onTriggerAutoSweep()
                        }
                    },
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = filterMode.primaryColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("send_question_button")
                ) {
                    Icon(
                        imageVector = if (inputText.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.QuestionAnswer,
                        contentDescription = "Senden",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Scrollable Transcripts Container with Custom Visible Scrollbar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isExpanded) 460.dp else 250.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, filterMode.primaryColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                color = Color(0xFF040805)
            ) {
                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = filterMode.primaryColor.copy(alpha = 0.4f),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Warte auf Äther-Signale...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Tippe oben auf 'Frage eingeben' oder 'Äther-Scan', um Geisterstimmen aufzuzeichnen.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.DarkGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .testTag("spirit_transcript_scrollable_list"),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(logs, key = { it.id }) { entry ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1710)),
                                    border = CardDefaults.outlinedCardBorder(enabled = true),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        // Row 1: Time, EMF & Danger badges
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = timeFormat.format(Date(entry.timestamp)),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.Gray,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp
                                                )
                                            )

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Surface(
                                                    color = filterMode.primaryColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "EMF: ${String.format(Locale.US, "%.1f", entry.emfLevel)}",
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = filterMode.primaryColor,
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                }

                                                Surface(
                                                    color = if (entry.dangerLevel >= 3) AlertInfraRed.copy(alpha = 0.2f) else filterMode.primaryColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "GEFAHR: ${entry.dangerLevel}",
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = if (entry.dangerLevel >= 3) AlertInfraRed else filterMode.primaryColor,
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        // Row 2: Trigger / Question asked
                                        Text(
                                            text = if (entry.question.startsWith("Frage:") || entry.question == "Sensor-Ätherabtastung") entry.question else "Frage: \"${entry.question}\"",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.LightGray.copy(alpha = 0.7f),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            )
                                        )

                                        // Row 3: Transcribed Spoken Spirit Text & Re-speak button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "\"${entry.phrase}\"",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = filterMode.primaryColor,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )

                                            IconButton(
                                                onClick = { onRespeak(entry) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Wiedergeben",
                                                    tint = filterMode.primaryColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Visible Functional Neon Scrollbar
                        Spacer(modifier = Modifier.width(4.dp))
                        VerticalScrollbarForLazyList(
                            state = listState,
                            color = filterMode.primaryColor,
                            trackColor = filterMode.primaryColor.copy(alpha = 0.12f),
                            width = 6.dp
                        )
                    }
                }
            }

            // Quick Link Footer to Open Full Logbook Screen Overlay
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Ansicht erweitert (${logs.size} Einträge)" else "Scrollen oder Erweitern für mehr Einträge",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = filterMode.primaryColor.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                )

                TextButton(
                    onClick = { showOverlayDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VOLLSTÄNDIGES LOGBUCH ↗",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = filterMode.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
            }
        }
    }
}

fun exportAndShareSpiritTranscripts(context: Context, logs: List<SpiritLogEntry>) {
    if (logs.isEmpty()) return

    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    val logTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val exportTime = dateFormat.format(Date())

    val content = buildString {
        appendLine("==============================================")
        appendLine("   GHOST DETECTOR - SPIRIT BOX TRANSKRIPTE")
        appendLine("==============================================")
        appendLine("Exportiert am: $exportTime")
        appendLine("Anzahl Protokolle: ${logs.size}")
        appendLine("----------------------------------------------\n")

        logs.forEachIndexed { index, entry ->
            val timeStr = logTimeFormat.format(Date(entry.timestamp))
            appendLine("Eintrag #${index + 1} [$timeStr]")
            appendLine("Frage : ${if (entry.question.isNotBlank()) entry.question else "Sensor-Abtastung"}")
            appendLine("Geist : \"${entry.phrase}\"")
            appendLine("EMF   : ${String.format(Locale.US, "%.1f", entry.emfLevel)} mG | Gefahr: Level ${entry.dangerLevel}")
            appendLine("----------------------------------------------")
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Spirit Box Transkripte (${logs.size} Einträge)")
        putExtra(Intent.EXTRA_TEXT, content)
    }

    val chooser = Intent.createChooser(intent, "Spirit Box Transkripte teilen / exportieren")
    context.startActivity(chooser)
}

