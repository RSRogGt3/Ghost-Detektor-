package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.GhostViewModel

enum class LexiconCategory(val title: String) {
    ALL("ALLE"),
    GHOSTS("GEISTER"),
    DEMONS("DÄMONEN"),
    VAMPIRES("VAMPIRE"),
    DIMENSIONS("DIMENSIONEN"),
    SIGILS("SIEGEL")
}

data class LexiconEntity(
    val name: String,
    val category: LexiconCategory,
    val dangerLevel: Int,
    val emfSignature: String,
    val frequencyRange: String,
    val spectralColor: Color,
    val description: String,
    val behavior: String,
    val counterMeasure: String
)

@Composable
fun LexiconScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val filterMode by viewModel.currentFilterMode.collectAsStateWithLifecycle()
    val primaryColor = filterMode.primaryColor

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LexiconCategory.ALL) }
    var expandedEntityName by remember { mutableStateOf<String?>(null) }

    val lexiconList = remember {
        listOf(
            LexiconEntity(
                name = "Poltergeist",
                category = LexiconCategory.GHOSTS,
                dangerLevel = 4,
                emfSignature = "6.5 – 9.8 mG (Hohe Spikes)",
                frequencyRange = "45.0 – 62.0 kHz",
                spectralColor = Color(0xFF00FF66),
                description = "Geräuschintensiver Geist, der kinetische Energie auf physische Gegenstände überträgt.",
                behavior = "Wirft Gegenstände, stört elektrische Geräte und erzeugt massive EMF-Ausschläge.",
                counterMeasure = "EMF-Unterdrückung aktivieren und Spektral-Befreiung durchführen."
            ),
            LexiconEntity(
                name = "Phantom & Schattenwesen",
                category = LexiconCategory.GHOSTS,
                dangerLevel = 2,
                emfSignature = "2.8 – 4.5 mG (Subtil)",
                frequencyRange = "18.0 – 35.0 kHz",
                spectralColor = Color(0xFF00E5FF),
                description = "Feinstoffliche ätherische Erscheinung. Oft nur als flüchtiger Schatten im Infrarot-Kanal erkennbar.",
                behavior = "Verursacht plötzliche Temperaturabfälle (Cold Spots) und diffuse Lichtbrechung.",
                counterMeasure = "Infrarot-Nachtsicht oder Ultraviolett-Filter nutzen, dann über Radar harmonisieren."
            ),
            LexiconEntity(
                name = "Banshee / Klageweib",
                category = LexiconCategory.GHOSTS,
                dangerLevel = 5,
                emfSignature = "7.0 – 9.5 mG (Oszillierend)",
                frequencyRange = "88.0 – 120.0 kHz (Ultraschall)",
                spectralColor = Color(0xFFFF0055),
                description = "Akustisch hochexplosive Entität mit durchdringenden EVP-Schreien.",
                behavior = "Überlastet Mikrofone und Spirit-Box Kanäle mit verzerrten Stimmfetzen.",
                counterMeasure = "Radar-Audio stummschalten, 'Siegel von Solomon' wirken und sofort einfangen."
            ),
            LexiconEntity(
                name = "Infernale Höllendämonen",
                category = LexiconCategory.DEMONS,
                dangerLevel = 5,
                emfSignature = "8.5 – 9.9 mG (Maximal)",
                frequencyRange = "14.4 – 28.0 kHz",
                spectralColor = Color(0xFFFF0033),
                description = "Bösartige Wesenheiten aus infernalen Ebenen. Höchste Bedrohungsstufe.",
                behavior = "Erzeugt roten Bildschirm-Warnrahmen, aggressives Flackern und drastische Feldstörungen.",
                counterMeasure = "Spektral-Falle scharfschalten, 'Dämonen-Siegel' anwenden und dauerhaft einsperren."
            ),
            LexiconEntity(
                name = "Arch-Demon / Schatten-Unhold",
                category = LexiconCategory.DEMONS,
                dangerLevel = 5,
                emfSignature = "9.2 – 9.9 mG",
                frequencyRange = "6.66 – 13.37 kHz",
                spectralColor = Color(0xFFFF2200),
                description = "Uralter Fürst der Finsternis, der andere Schattenentitäten befehligt.",
                behavior = "Manipuliert Sensor-Kalibrierungen und erzeugt Schein-Signale.",
                counterMeasure = "Magnetfeld-Schild auf 100% setzen und über Dämonen-Falle bannen."
            ),
            LexiconEntity(
                name = "Astral-Vampir & Nosferatu",
                category = LexiconCategory.VAMPIRES,
                dangerLevel = 4,
                emfSignature = "5.0 – 8.0 mG (Pulsierend)",
                frequencyRange = "33.3 – 77.7 kHz",
                spectralColor = Color(0xFFBB33FF),
                description = "Parasitäres Wesen, das sich von feinstofflicher Lebensenergie und EMF-Feldern nährt.",
                behavior = "Entzieht Batterien rasch Energie und taucht als violetter Punkt auf dem Radar auf.",
                counterMeasure = "Ultraviolett-Spektralfilter aktivieren und mit Licht-Siegel vertreiben/fangen."
            ),
            LexiconEntity(
                name = "Interdimensionaler Quanten-Riss",
                category = LexiconCategory.DIMENSIONS,
                dangerLevel = 4,
                emfSignature = "4.0 – 8.8 mG (Gravitativ)",
                frequencyRange = "108.0 – 432.0 kHz",
                spectralColor = Color(0xFF00B0FF),
                description = "Riss im Raum-Zeit-Gefüge, durch den fremde Wesen in unsere Realität dringen.",
                behavior = "Lässt kontinuierlich neue Blips auf dem Radar spawnen bis zur Schließung.",
                counterMeasure = "Button 'PORTAL SCHLIESSEN' betätigen oder Auto-Dimensions-Versiegelung aktivieren."
            ),
            LexiconEntity(
                name = "Void-Lücke / Astrales Portal",
                category = LexiconCategory.DIMENSIONS,
                dangerLevel = 5,
                emfSignature = "7.5 – 9.9 mG",
                frequencyRange = "963.0 kHz",
                spectralColor = Color(0xFF00FFCC),
                description = "Spalte in die Schatten-Ebene und das Astral-Kontinuum.",
                behavior = "Starke Telemetrie-Schwankungen von Barometer und Gyroskop.",
                counterMeasure = "Wechsel auf die Void-Ebene und Anwendung des Siegels der Leere zur Stabilisierung."
            ),
            LexiconEntity(
                name = "Siegel von Solomon",
                category = LexiconCategory.SIGILS,
                dangerLevel = 1,
                emfSignature = "Neutralisierend (0 mG)",
                frequencyRange = "528 Hz (Harmonisch)",
                spectralColor = Color(0xFFFFCC00),
                description = "Uraltes Bann-Symbol zur ultimativen Fesselung von Dämonen und bösen Geistern.",
                behavior = "Errichtet eine unzerstörbare Spektral-Barriere für 60 Sekunden.",
                counterMeasure = "In der 'Sigel-Schmiede' aktivieren bei starkem Dämonen-Befall."
            ),
            LexiconEntity(
                name = "Siegel des Lichts (Astral-Bann)",
                category = LexiconCategory.SIGILS,
                dangerLevel = 1,
                emfSignature = "Harmonisierend",
                frequencyRange = "741 Hz",
                spectralColor = Color(0xFF00FF88),
                description = "Hochfrequentes Schutzsiegel gegen Vampire, Schattenwesen und Spuk.",
                behavior = "Blendet lichtscheue Wesen und erleichtert die Befreiung gefangener Seelen.",
                counterMeasure = "In der 'Sigel-Schmiede' aktivieren."
            )
        )
    }

    val filteredList = remember(searchQuery, selectedCategory) {
        lexiconList.filter { entity ->
            val matchesCategory = (selectedCategory == LexiconCategory.ALL) || (entity.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    entity.name.contains(searchQuery, ignoreCase = true) ||
                    entity.description.contains(searchQuery, ignoreCase = true) ||
                    entity.behavior.contains(searchQuery, ignoreCase = true) ||
                    entity.counterMeasure.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = primaryColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PARANORMALES LEXIKON",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = primaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }

            Text(
                text = "${filteredList.size} EINTRÄGE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.LightGray,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Geist, Dämon, Riss oder Siegel suchen...", color = Color.Gray, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Suchen", tint = primaryColor) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("lexicon_search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        )

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LexiconCategory.values().forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) primaryColor.copy(alpha = 0.25f)
                            else Color(0xFF101612)
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) primaryColor else Color.DarkGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(vertical = 8.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) primaryColor else Color.LightGray,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 8.5.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        // Entries List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList, key = { it.name }) { entity ->
                val isExpanded = expandedEntityName == entity.name
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF09100C)),
                    border = BorderStroke(1.dp, entity.spectralColor.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            expandedEntityName = if (isExpanded) null else entity.name
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Title Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(entity.spectralColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = entity.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = entity.spectralColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "KLASSE ${entity.dangerLevel}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (entity.dangerLevel >= 4) Color(0xFFFF3366) else Color(0xFF00FFCC),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Summary description
                        Text(
                            text = entity.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.5.sp
                            )
                        )

                        // Expanded Details (Behavior, Signatures, Counter-Measures)
                        if (isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF040A07))
                                    .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row {
                                    Text("⚡ EMF-SIGNATUR: ", style = MaterialTheme.typography.labelSmall.copy(color = primaryColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                                    Text(entity.emfSignature, style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray, fontSize = 10.sp))
                                }
                                Row {
                                    Text("📡 FREQUENZ: ", style = MaterialTheme.typography.labelSmall.copy(color = primaryColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                                    Text(entity.frequencyRange, style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray, fontSize = 10.sp))
                                }
                                Column {
                                    Text("⚠️ VERHALTEN:", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFFCC00), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                                    Text(entity.behavior, style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray, fontSize = 10.5.sp))
                                }
                                Column {
                                    Text("🛡️ GEGENMASSNAHME & FANG-METHODE:", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FFCC), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                                    Text(entity.counterMeasure, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE0FFFF), fontSize = 10.5.sp, fontWeight = FontWeight.Medium))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
