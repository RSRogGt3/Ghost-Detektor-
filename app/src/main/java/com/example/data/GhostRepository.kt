package com.example.data

import kotlinx.coroutines.flow.Flow

class GhostRepository(private val ghostDao: GhostDao) {

    val allDetections: Flow<List<GhostDetectionEntity>> = ghostDao.getAllDetections()
    val favoriteDetections: Flow<List<GhostDetectionEntity>> = ghostDao.getFavoriteDetections()

    suspend fun insertGhost(ghost: GhostDetectionEntity): Long {
        return ghostDao.insert(ghost)
    }

    suspend fun updateGhost(ghost: GhostDetectionEntity) {
        ghostDao.update(ghost)
    }

    suspend fun deleteGhost(ghost: GhostDetectionEntity) {
        ghostDao.delete(ghost)
    }

    suspend fun clearAll() {
        ghostDao.clearAll()
    }

    suspend fun prepopulateIfEmpty() {
        if (ghostDao.getCount() == 0) {
            val now = System.currentTimeMillis()
            val hourMs = 3600_000L
            val initialGhosts = listOf(
                GhostDetectionEntity(
                    name = "Anomalie Alpha-9",
                    type = "Poltergeist",
                    emfLevel = 8.4f,
                    frequencyKhz = 44.1f,
                    dangerLevel = 4,
                    locationName = "Kellerbereich Nord",
                    timestamp = now - hourMs * 2,
                    notes = "Starke elektromagnetische Fluktuationen und kühle Luftströme registriert.",
                    spectralColorHex = "#00FF66",
                    isFavorite = true,
                    lastWords = "Ich ruhe nie..."
                ),
                GhostDetectionEntity(
                    name = "Vaporous Entity #12",
                    type = "Phantom",
                    emfLevel = 4.2f,
                    frequencyKhz = 18.7f,
                    dangerLevel = 2,
                    locationName = "Hauptkorridor OG",
                    timestamp = now - hourMs * 6,
                    notes = "Schwache Infrarot-Silhouettierung an der Ostwand.",
                    spectralColorHex = "#00E5FF",
                    isFavorite = false,
                    lastWords = "Sucht mich..."
                ),
                GhostDetectionEntity(
                    name = "Schattengestalt Sigma",
                    type = "Schattenwesen",
                    emfLevel = 9.8f,
                    frequencyKhz = 62.0f,
                    dangerLevel = 5,
                    locationName = "Treppenaufgang West",
                    timestamp = now - hourMs * 18,
                    notes = "Warnung: Hohe Entropiewerte! Abrupte Frequenzverschiebungen im Spirit-Box Bereich.",
                    spectralColorHex = "#FF2244",
                    isFavorite = true,
                    lastWords = "Verlasst diesen Raum!"
                ),
                GhostDetectionEntity(
                    name = "Orb-Formation Echo",
                    type = "Orb-Vorkommen",
                    emfLevel = 3.1f,
                    frequencyKhz = 24.3f,
                    dangerLevel = 1,
                    locationName = "Dachboden",
                    timestamp = now - hourMs * 30,
                    notes = "Drei schwebende Quantenlichter in Infrarot-Ansicht aufgezeichnet.",
                    spectralColorHex = "#66FFAA",
                    isFavorite = false,
                    lastWords = "Licht... überall."
                )
            )
            initialGhosts.forEach { ghostDao.insert(it) }
        }
    }
}
