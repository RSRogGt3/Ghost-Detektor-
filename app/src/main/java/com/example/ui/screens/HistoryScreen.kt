package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.OutputStreamWriter

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.GhostDetectionEntity
import com.example.ui.components.GhostDetailDialog
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.MaterialTheme.colorScheme.outline
import com.example.ui.theme.MaterialTheme.colorScheme.primary
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings
import com.example.ui.theme.MaterialTheme.colorScheme.surface
import com.example.ui.theme.MaterialTheme.colorScheme.surfaceVariant
import com.example.ui.theme.MaterialTheme.colorScheme.onSurfaceVariant
import com.example.ui.theme.MaterialTheme.colorScheme.onSurface
import com.example.ui.theme.ThermalAmber
import com.example.ui.viewmodel.GhostViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: GhostViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val filteredGhosts by viewModel.filteredDetections.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnlyFilter.collectAsStateWithLifecycle()
    val selectedGhostDetail by viewModel.selectedGhostDetail.collectAsStateWithLifecycle()

    var showExportDialog by remember { mutableStateOf(false) }
    var exportTextToSave by remember { mutableStateOf("") }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(exportTextToSave)
                    }
                }
                Toast.makeText(context, "Log erfolgreich gespeichert", Toast.LENGTH_SHORT).show()
                showExportDialog = false
            } catch (e: Exception) {
                Toast.makeText(context, "Fehler beim Speichern", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val historyListState = rememberLazyListState()

    val typeFilters = UiStrings.getTypeFilters(appLanguage)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .testTag("history_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = UiStrings.getHistoryHeader(appLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = UiStrings.getHistoryDbCount(appLanguage, filteredGhosts.size),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }

                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("export_events_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = UiStrings.getExportBtn(appLanguage),
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = UiStrings.getExportBtn(appLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Dashboard Widget: Frequency Chart
            if (filteredGhosts.isNotEmpty()) {
                com.example.ui.components.GhostFrequencyChart(
                    detections = filteredGhosts,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(UiStrings.getSearchPlaceholder(appLanguage), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )

            // Category Filter Chips & Favorites Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(typeFilters) { type ->
                        val isSelected = type == selectedTypeFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setSelectedTypeFilter(type) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = type.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                // Favorites Filter Button
                IconButton(
                    onClick = { viewModel.setFavoritesOnlyFilter(!favoritesOnly) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (favoritesOnly) AlertInfraRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (favoritesOnly) AlertInfraRed else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = if (favoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favoriten Filter",
                        tint = if (favoritesOnly) AlertInfraRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Favorite all captured Button
                IconButton(
                    onClick = { viewModel.favoriteAllCapturedGhosts() },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Alle Gefangenen herzen",
                        tint = AlertInfraRed
                    )
                }
            }

            // Ghost Discoveries List
            if (filteredGhosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.height(48.dp).width(48.dp)
                        )
                        Text(
                            text = "Keine Funde in dieser Kategorie.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyColumn(
                        state = historyListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filteredGhosts, key = { it.id }) { ghost ->
                            GhostDiscoveryCard(
                                ghost = ghost,
                                onClick = { viewModel.selectGhostDetail(ghost) },
                                onToggleFavorite = { viewModel.toggleFavorite(ghost) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    com.example.ui.components.VerticalScrollbarForLazyList(
                        state = historyListState,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline,
                        width = 6.dp
                    )
                }
            }
        }
    }

    // Detail Dialog Sheet
    selectedGhostDetail?.let { ghost ->
        GhostDetailDialog(
            ghost = ghost,
            onDismiss = { viewModel.selectGhostDetail(null) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onDelete = { viewModel.deleteGhost(it) },
            onFree = { viewModel.freeGhost(it) },
            onUpdateNotes = { g, notes -> viewModel.updateGhostNotes(g, notes) },
            onSpeakText = { text -> viewModel.spiritTtsManager.speak(text) }
        )
    }

    // Export Summary Dialog
    if (showExportDialog) {
        val summaryText = remember(filteredGhosts) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY)
            val sb = StringBuilder()
            sb.appendLine("=====================================================")
            sb.appendLine("  GHOST DETECTOR - PARANORMALE EREIGNIS-PROTOKOLLE  ")
            sb.appendLine("=====================================================")
            sb.appendLine("Exportiert am        : ${dateFormat.format(Date())}")
            sb.appendLine("Einträge in Room DB  : ${filteredGhosts.size}")
            sb.appendLine("-----------------------------------------------------")
            if (filteredGhosts.isEmpty()) {
                sb.appendLine("Keine Ereignisse in der Room-Datenbank vorhanden.")
            } else {
                filteredGhosts.forEachIndexed { index, item ->
                    val dateStr = try { dateFormat.format(Date(item.timestamp)) } catch (_: Exception) { "${item.timestamp}" }
                    sb.appendLine("EINTRAG #${index + 1}: ${item.name} (${item.type})")
                    sb.appendLine("  • Gefahrenstufe : Stufe ${item.dangerLevel}/5")
                    sb.appendLine("  • Zeitstempel   : $dateStr")
                    sb.appendLine("  • Ort / Raum    : ${item.locationName}")
                    sb.appendLine("  • EMF-Stärke    : ${String.format(Locale.US, "%.1f", item.emfLevel)} mG")
                    sb.appendLine("  • Frequenz      : ${String.format(Locale.US, "%.1f", item.frequencyKhz)} kHz")
                    if (item.notes.isNotBlank()) {
                        sb.appendLine("  • Notiz         : \"${item.notes}\"")
                    }
                    if (item.lastWords.isNotBlank()) {
                        sb.appendLine("  • EVP Botschaft : \"${item.lastWords}\"")
                    }
                    if (item.isFavorite) {
                        sb.appendLine("  • Status        : ★ Favorit")
                    }
                    sb.appendLine("-----------------------------------------------------")
                }
            }
            sb.appendLine("=== ENDE DES PROTOKOLLS ===")
            sb.toString()
        }

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ROOM DB EXPORT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Lesbares Text-Protokoll der gespeicherten Geister-Ereignisse:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    val exportScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(exportScrollState)
                        ) {
                            Text(
                                text = summaryText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))
                        com.example.ui.components.VerticalScrollbarForScrollState(
                            state = exportScrollState,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outline,
                            width = 6.dp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Ghost Detector - Paranormale Protokolle")
                            putExtra(Intent.EXTRA_TEXT, summaryText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Protokoll teilen"))
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(UiStrings.getShareBtn(appLanguage), color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            exportTextToSave = summaryText
                            exportLauncher.launch("ghost_log.txt")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Als .txt Speichern", color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    }
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Ghost Detections Log", summaryText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Protokoll in Zwischenablage kopiert!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = ThermalAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(UiStrings.getCopyBtn(appLanguage), color = ThermalAmber, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    }

                    TextButton(onClick = { showExportDialog = false }) {
                        Text(UiStrings.getCloseBtn(appLanguage), color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    }
                }
            }
        )
    }
}

@Composable
fun GhostDiscoveryCard(
    ghost: GhostDetectionEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val dateStr = remember(ghost.timestamp) {
        val sdf = SimpleDateFormat("dd.MM.yy - HH:mm", Locale.GERMANY)
        sdf.format(Date(ghost.timestamp))
    }

    val parsedColor = remember(ghost.spectralColorHex) {
        try {
            Color(android.graphics.Color.parseColor(ghost.spectralColorHex))
        } catch (_: Exception) {
            MaterialTheme.colorScheme.primary
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("ghost_card_${ghost.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(enabled = true)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Color Status Pillar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(54.dp)
                    .background(parsedColor, RoundedCornerShape(3.dp))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ghost.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "${ghost.emfLevel} mG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (ghost.dangerLevel >= 4) AlertInfraRed else ThermalAmber,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${ghost.type.uppercase()} • ${ghost.locationName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (ghost.type.contains("ROTER PUNKT") || ghost.dangerLevel >= 4) AlertInfraRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = if (ghost.dangerLevel >= 4) FontWeight.Bold else FontWeight.Normal
                        )
                    )

                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                }

                if (ghost.notes.isNotBlank()) {
                    Text(
                        text = ghost.notes.lines().firstOrNull() ?: ghost.notes,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (ghost.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorit toggle",
                    tint = if (ghost.isFavorite) AlertInfraRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detail ansehen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
