package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.MaterialTheme.colorScheme.primary
import com.example.ui.theme.MaterialTheme.colorScheme.surface
import com.example.ui.theme.MaterialTheme.colorScheme.onSurface
import com.example.ui.theme.MaterialTheme.colorScheme.onSurfaceVariant
import com.example.ui.viewmodel.GhostViewModel

@Composable
fun LexiconScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val ghostTypes = listOf(
        "Poltergeist" to "Ein lauter Geist, der physische Objekte bewegen kann. Hohe EMF-Ausschläge.",
        "DÄMON (GEFANGEN)" to "Ein bösartiges Wesen, das oft tiefrote Spektralfarben aufweist. Extrem gefährlich.",
        "Phantom" to "Eine ätherische Erscheinung. Oft nur als schwacher Schatten sichtbar.",
        "Schattenwesen" to "Versteckt sich in Dunkelheit. Senkt die Raumtemperatur.",
        "DIMENSIONSRISS (GESCHLOSSEN)" to "Kein Geist, sondern eine Anomalie in der Raumzeit.",
        "VAMPIR (GEFANGEN)" to "Ein untotes Wesen. Saugt elektromagnetische Energie auf.",
        "Orb-Vorkommen" to "Schwebende Lichtkugeln. Oft Vorboten stärkerer Entitäten."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "GEISTER-LEXIKON",
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ghostTypes) { (type, description) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}
