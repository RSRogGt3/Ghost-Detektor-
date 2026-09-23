package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiDebugTelemetry
import com.example.ai.LanguageModelConfig
import com.example.data.InfraLightColor

@Composable
fun AiModelSelectorAndDebugCard(
    activeModel: LanguageModelConfig,
    debugTelemetry: AiDebugTelemetry,
    isTesting: Boolean,
    customApiKey: String,
    filterMode: FilterMode,
    infraLightColor: InfraLightColor,
    onSelectModel: (LanguageModelConfig) -> Unit,
    onTestConnection: (LanguageModelConfig, String?) -> Unit,
    onSaveCustomApiKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showApiKeyInput by remember { mutableStateOf(false) }
    var tempApiKey by remember(customApiKey) { mutableStateOf(customApiKey) }

    val primaryCol = filterMode.primaryColor

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF07100B)),
        border = CardDefaults.outlinedCardBorder(enabled = true),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("ai_model_debug_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = primaryCol,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "KI-SPRACHMODELLE & DEBUGGER",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = primaryCol,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        )
                        Text(
                            text = "Aktiv: ${activeModel.displayName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = if (debugTelemetry.isSuccess) Color(0xFF00FF66).copy(alpha = 0.2f) else Color(0xFFFF9900).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (debugTelemetry.isSuccess) Color(0xFF00FF66) else Color(0xFFFF9900)
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = activeModel.modelTag,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (debugTelemetry.isSuccess) Color(0xFF00FF66) else Color(0xFFFF9900),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Einklappen" else "Ausklappen",
                            tint = primaryCol,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Quick Status Line when collapsed
            if (!isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "● ${debugTelemetry.statusMessage}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (debugTelemetry.isSuccess) Color(0xFF00FF66) else Color.Yellow,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    if (debugTelemetry.latencyMs > 0) {
                        Text(
                            text = "${debugTelemetry.latencyMs}ms",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Expanded Content: Model Selection & Debug Console
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "WÄHLE DAS AKTIVE KI-SPRACHMODELL:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = primaryCol,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )

                    // 4 Models Selector
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LanguageModelConfig.values().forEach { model ->
                            val isSelected = model == activeModel
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) primaryCol.copy(alpha = 0.18f)
                                        else Color.Black.copy(alpha = 0.45f)
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) primaryCol else Color.Gray.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onSelectModel(model) }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { onSelectModel(model) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = primaryCol,
                                                    unselectedColor = Color.Gray
                                                ),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = model.displayName,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    color = if (isSelected) Color.White else Color.LightGray,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.5.sp
                                                )
                                            )
                                        }
                                        Text(
                                            text = model.description,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.Gray,
                                                fontSize = 9.5.sp
                                            ),
                                            modifier = Modifier.padding(start = 24.dp, top = 2.dp)
                                        )
                                    }

                                    Surface(
                                        color = if (model.isCloudModel) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color(0xFFFFCC00).copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            0.8.dp,
                                            if (model.isCloudModel) Color(0xFF00E5FF) else Color(0xFFFFCC00)
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (model.isCloudModel) "CLOUD API" else "OFFLINE",
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (model.isCloudModel) Color(0xFF00E5FF) else Color(0xFFFFCC00),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = primaryCol.copy(alpha = 0.25f), thickness = 0.8.dp)

                    // Debugging Section: Test Button & API Key Config
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAGNOSE & VERBINDUNGSTEST:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = primaryCol,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )

                        TextButton(
                            onClick = { showApiKeyInput = !showApiKeyInput },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = primaryCol,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showApiKeyInput) "Key verbergen" else "Key-Optionen",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = primaryCol,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Optional Custom API Key input for debugging
                    if (showApiKeyInput) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Gemini API-Key für Live-Debugging eingeben (optional, überschreibt Secrets):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.LightGray,
                                    fontSize = 9.5.sp
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = tempApiKey,
                                    onValueChange = { tempApiKey = it },
                                    placeholder = { Text("AIzaSy... (oder leer für Secrets)", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    textStyle = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                                Button(
                                    onClick = { onSaveCustomApiKey(tempApiKey) },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryCol),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Speichern", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "Aktuelle Quelle: ${debugTelemetry.apiKeySource}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.5.sp
                                )
                            )
                        }
                    }

                    // Test & Debug Trigger Button
                    Button(
                        onClick = { onTestConnection(activeModel, customApiKey.ifBlank { null }) },
                        enabled = !isTesting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryCol,
                            disabledContainerColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("test_ai_model_button")
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DEKODIERE SIGNAL & PRÜFE VERBINDUNG...",
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🧪 ${activeModel.displayName} TESTEN & DEBUGGEN",
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Live Debug Telemetry Terminal Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF030704))
                            .border(
                                1.dp,
                                if (debugTelemetry.isSuccess) Color(0xFF00FF66).copy(alpha = 0.5f)
                                else Color(0xFFFF5555).copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "📟 EVP-TELEMETRIE & STATUS-LOG:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (debugTelemetry.isSuccess) Color(0xFF00FF66) else Color(0xFFFF5555),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    )
                                )
                                Text(
                                    text = if (debugTelemetry.httpStatusCode > 0) "HTTP ${debugTelemetry.httpStatusCode}" else "OFFLINE / LOCAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (debugTelemetry.isSuccess) Color(0xFF00FF66) else Color(0xFFFF5555),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            Text(
                                text = "Meldung: ${debugTelemetry.statusMessage}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )

                            if (debugTelemetry.latencyMs > 0) {
                                Text(
                                    text = "Latenz: ${debugTelemetry.latencyMs} ms | Key: ${debugTelemetry.apiKeySource}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.LightGray,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            if (debugTelemetry.responseText.isNotBlank()) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "Antwort: \"${debugTelemetry.responseText}\"",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF00FF66),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.5.sp
                                        ),
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }

                            if (!debugTelemetry.rawError.isNullOrBlank()) {
                                Surface(
                                    color = Color(0xFF2A0000),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "Fehler-Diagnose: ${debugTelemetry.rawError}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFFF7777),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(6.dp)
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
