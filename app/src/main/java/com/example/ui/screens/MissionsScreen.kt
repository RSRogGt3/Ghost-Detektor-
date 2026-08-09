package com.example.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.GhostViewModel

@Composable
fun MissionsScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val detections by viewModel.allDetections.collectAsStateWithLifecycle()

    val capturedCount = detections.count { it.type.contains("GEFANGEN", ignoreCase = true) || it.name.contains("GEFANGEN", ignoreCase = true) }
    val totalDetections = detections.size
    val favoritesCount = detections.count { it.isFavorite }

    val missions = listOf(
        Mission(
            title = "Geisterjäger-Novize",
            description = "Erfasse deine erste Entität.",
            isCompleted = totalDetections >= 1
        ),
        Mission(
            title = "Erfahrener Ermittler",
            description = "Finde 5 verschiedene Entitäten.",
            isCompleted = totalDetections >= 5
        ),
        Mission(
            title = "Geister-Meister",
            description = "Fange 3 Geister im Radar-Modus.",
            isCompleted = capturedCount >= 3
        ),
        Mission(
            title = "Lieblinge des Jenseits",
            description = "Markiere 3 Geister als Favorit.",
            isCompleted = favoritesCount >= 3
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "MISSIONEN & ERFOLGE",
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
            items(missions) { mission ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (mission.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (mission.isCompleted) "Abgeschlossen" else "Nicht abgeschlossen",
                            tint = if (mission.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = mission.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = if (mission.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mission.description,
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
}

data class Mission(
    val title: String,
    val description: String,
    val isCompleted: Boolean
)
