package com.example.data

import kotlinx.coroutines.flow.Flow

class GhostRepository(private val ghostDao: GhostDao) {

    val allDetections: Flow<List<GhostDetectionEntity>> = ghostDao.getAllDetections()
    val favoriteDetections: Flow<List<GhostDetectionEntity>> = ghostDao.getFavoriteDetections()

    suspend fun getAllDetectionsList(): List<GhostDetectionEntity> {
        return ghostDao.getAllDetectionsList()
    }

    suspend fun insertGhost(ghost: GhostDetectionEntity): Long {
        return ghostDao.insert(ghost)
    }

    suspend fun updateGhost(ghost: GhostDetectionEntity) {
        ghostDao.update(ghost)
    }

    suspend fun setAllFavorite(favorite: Boolean) {
        ghostDao.setAllFavorite(favorite)
    }

    suspend fun deleteGhost(ghost: GhostDetectionEntity) {
        ghostDao.delete(ghost)
    }

    suspend fun clearAll() {
        ghostDao.clearAll()
    }

    suspend fun prepopulateIfEmpty() {
        if (ghostDao.getCount() == 0) {
            val initialGhosts = getDiverseSampleEntities()
            initialGhosts.forEach { ghostDao.insert(it) }
        } else {
            ensureDiverseEntities()
        }
    }

    suspend fun ensureDiverseEntities() {
        val existing = ghostDao.getAllDetectionsList()
        val hasGeist = existing.any { it.type.contains("Geist", ignoreCase = true) || it.name.contains("Geist", ignoreCase = true) }
        val hasVampir = existing.any { it.type.contains("Vampir", ignoreCase = true) || it.type.contains("Vampire", ignoreCase = true) || it.name.contains("Vampir", ignoreCase = true) || it.name.contains("Nosferatu", ignoreCase = true) }
        val hasSchatten = existing.any { it.type.contains("Schatten", ignoreCase = true) || it.name.contains("Schatten", ignoreCase = true) }
        val hasDaemon = existing.any { it.type.contains("Dämon", ignoreCase = true) || it.type.contains("Demon", ignoreCase = true) || it.name.contains("Dämon", ignoreCase = true) || it.name.contains("Belial", ignoreCase = true) }
        val hasPoltergeist = existing.any { it.type.contains("Poltergeist", ignoreCase = true) || it.name.contains("Poltergeist", ignoreCase = true) }
        val hasPhantom = existing.any { it.type.contains("Phantom", ignoreCase = true) || it.name.contains("Phantom", ignoreCase = true) }
        val hasBanshee = existing.any { it.type.contains("Banshee", ignoreCase = true) || it.name.contains("Banshee", ignoreCase = true) }

        val missingEntities = mutableListOf<GhostDetectionEntity>()
        val now = System.currentTimeMillis()
        val hourMs = 3600_000L

        if (!hasGeist) {
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Rastloser Geist Cornelius",
                    type = "Geist",
                    emfLevel = 6.4f,
                    frequencyKhz = 48.2f,
                    dangerLevel = 2,
                    locationName = "Spiegelsaal",
                    timestamp = now - hourMs * 2,
                    notes = "Klassische spukhafte Erscheinung mit kühlem Luftzug und Flüstern.",
                    spectralColorHex = "#00FF66",
                    isFavorite = true,
                    lastWords = "Vergesst mich nicht..."
                )
            )
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Spukgeist Mathilda",
                    type = "Geist",
                    emfLevel = 5.8f,
                    frequencyKhz = 52.0f,
                    dangerLevel = 3,
                    locationName = "Bibliothek Süd",
                    timestamp = now - hourMs * 5,
                    notes = "Schwebende Silhouette vor den alten Folianten.",
                    spectralColorHex = "#33FF99",
                    isFavorite = false,
                    lastWords = "Die Bücher schweigen nie."
                )
            )
        }

        if (!hasVampir) {
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Vampir Nosferatu [GEFANGEN]",
                    type = "VAMPIR (GEFANGEN)",
                    emfLevel = 9.1f,
                    frequencyKhz = 74.2f,
                    dangerLevel = 5,
                    locationName = "Gruft Ostflügel",
                    timestamp = now - hourMs * 4,
                    notes = "Uralter Blut-Vampir in die Spektral-Falle gebannt.",
                    spectralColorHex = "#DD00FF",
                    isFavorite = true,
                    lastWords = "Die Nacht gehört mir..."
                )
            )
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Astral-Vampir Lord Carmilla",
                    type = "Vampir",
                    emfLevel = 8.7f,
                    frequencyKhz = 68.4f,
                    dangerLevel = 4,
                    locationName = "Gewölbekeller",
                    timestamp = now - hourMs * 8,
                    notes = "Astral-parasitäres Wesen, absorbiert Umgebungs-Chi.",
                    spectralColorHex = "#9900EE",
                    isFavorite = false,
                    lastWords = "Eure Lebenskraft erlischt."
                )
            )
        }

        if (!hasSchatten) {
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Schattengestalt Sigma",
                    type = "Schattenwesen",
                    emfLevel = 9.8f,
                    frequencyKhz = 62.0f,
                    dangerLevel = 5,
                    locationName = "Treppenaufgang West",
                    timestamp = now - hourMs * 18,
                    notes = "Warnung: Hohe Entropiewerte! Abrupte Frequenzverschiebungen.",
                    spectralColorHex = "#FF2244",
                    isFavorite = true,
                    lastWords = "Verlasst diesen Raum!"
                )
            )
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Schattenkriecher Umbra",
                    type = "Schattenwesen",
                    emfLevel = 7.2f,
                    frequencyKhz = 58.1f,
                    dangerLevel = 3,
                    locationName = "Schattengang",
                    timestamp = now - hourMs * 10,
                    notes = "Kriecht lautlos an Wänden und Ecken entlang.",
                    spectralColorHex = "#444455",
                    isFavorite = false,
                    lastWords = "Im Dunkeln sind wir viele."
                )
            )
        }

        if (!hasDaemon) {
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Dämon Belial [GEFANGEN]",
                    type = "DÄMON (GEFANGEN)",
                    emfLevel = 9.9f,
                    frequencyKhz = 88.5f,
                    dangerLevel = 5,
                    locationName = "Schattenkammer B3",
                    timestamp = now - hourMs * 1,
                    notes = "Gefangen im Dämonen-Siegel. Bösartige Spektralaura neutralisiert.",
                    spectralColorHex = "#FF0033",
                    isFavorite = true,
                    lastWords = "Das Siegel wird nicht ewig halten!"
                )
            )
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Infernaler Dämon Azazel",
                    type = "Dämon",
                    emfLevel = 9.5f,
                    frequencyKhz = 94.0f,
                    dangerLevel = 5,
                    locationName = "Katakomben Level 4",
                    timestamp = now - hourMs * 7,
                    notes = "Hitze-Emission und Brandgeruch im Spektral-Scanner.",
                    spectralColorHex = "#FF3300",
                    isFavorite = false,
                    lastWords = "Feuer und Asche erwarten euch."
                )
            )
        }

        if (!hasPoltergeist) {
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Anomalie Alpha-9",
                    type = "Poltergeist",
                    emfLevel = 8.4f,
                    frequencyKhz = 44.1f,
                    dangerLevel = 4,
                    locationName = "Kellerbereich Nord",
                    timestamp = now - hourMs * 2,
                    notes = "Starke elektromagnetische Fluktuationen und Erschütterungen.",
                    spectralColorHex = "#00FF66",
                    isFavorite = false,
                    lastWords = "Ich ruhe nie..."
                )
            )
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Poltergeist Poldi (Kinetik)",
                    type = "Poltergeist",
                    emfLevel = 7.6f,
                    frequencyKhz = 46.5f,
                    dangerLevel = 3,
                    locationName = "Dachkammer",
                    timestamp = now - hourMs * 9,
                    notes = "Bewegt Gegenstände und wirft spektrale Funken.",
                    spectralColorHex = "#FFAA00",
                    isFavorite = false,
                    lastWords = "Fass das nicht an!"
                )
            )
        }

        if (!hasPhantom) {
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Ätherisches Phantom #12",
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
                )
            )
        }

        if (!hasBanshee) {
            missingEntities.add(
                GhostDetectionEntity(
                    name = "Banshee Klageweib Mara",
                    type = "Banshee",
                    emfLevel = 8.9f,
                    frequencyKhz = 14.2f,
                    dangerLevel = 4,
                    locationName = "Moor-Garten",
                    timestamp = now - hourMs * 14,
                    notes = "Akustische Frequenzspitze vor dem Klagelaut.",
                    spectralColorHex = "#00FFDD",
                    isFavorite = false,
                    lastWords = "Das Unheil naht..."
                )
            )
        }

        missingEntities.forEach { ghostDao.insert(it) }
    }

    private fun getDiverseSampleEntities(): List<GhostDetectionEntity> {
        val now = System.currentTimeMillis()
        val hourMs = 3600_000L
        return listOf(
            GhostDetectionEntity(
                name = "Rastloser Geist Cornelius",
                type = "Geist",
                emfLevel = 6.4f,
                frequencyKhz = 48.2f,
                dangerLevel = 2,
                locationName = "Spiegelsaal",
                timestamp = now - hourMs * 2,
                notes = "Klassische spukhafte Erscheinung mit kühlem Luftzug und Flüstern.",
                spectralColorHex = "#00FF66",
                isFavorite = true,
                lastWords = "Vergesst mich nicht..."
            ),
            GhostDetectionEntity(
                name = "Vampir Nosferatu [GEFANGEN]",
                type = "VAMPIR (GEFANGEN)",
                emfLevel = 9.1f,
                frequencyKhz = 74.2f,
                dangerLevel = 5,
                locationName = "Gruft Ostflügel",
                timestamp = now - hourMs * 4,
                notes = "Uralter Blut-Vampir in die Spektral-Falle gebannt.",
                spectralColorHex = "#DD00FF",
                isFavorite = true,
                lastWords = "Die Nacht gehört mir..."
            ),
            GhostDetectionEntity(
                name = "Schattengestalt Sigma",
                type = "Schattenwesen",
                emfLevel = 9.8f,
                frequencyKhz = 62.0f,
                dangerLevel = 5,
                locationName = "Treppenaufgang West",
                timestamp = now - hourMs * 18,
                notes = "Warnung: Hohe Entropiewerte! Abrupte Frequenzverschiebungen.",
                spectralColorHex = "#FF2244",
                isFavorite = true,
                lastWords = "Verlasst diesen Raum!"
            ),
            GhostDetectionEntity(
                name = "Dämon Belial [GEFANGEN]",
                type = "DÄMON (GEFANGEN)",
                emfLevel = 9.9f,
                frequencyKhz = 88.5f,
                dangerLevel = 5,
                locationName = "Schattenkammer B3",
                timestamp = now - hourMs * 1,
                notes = "Gefangen im Dämonen-Siegel. Bösartige Spektralaura neutralisiert.",
                spectralColorHex = "#FF0033",
                isFavorite = true,
                lastWords = "Das Siegel wird nicht ewig halten!"
            ),
            GhostDetectionEntity(
                name = "Anomalie Alpha-9",
                type = "Poltergeist",
                emfLevel = 8.4f,
                frequencyKhz = 44.1f,
                dangerLevel = 4,
                locationName = "Kellerbereich Nord",
                timestamp = now - hourMs * 3,
                notes = "Starke elektromagnetische Fluktuationen und Erschütterungen.",
                spectralColorHex = "#00FF66",
                isFavorite = false,
                lastWords = "Ich ruhe nie..."
            ),
            GhostDetectionEntity(
                name = "Astral-Vampir Lord Carmilla",
                type = "Vampir",
                emfLevel = 8.7f,
                frequencyKhz = 68.4f,
                dangerLevel = 4,
                locationName = "Gewölbekeller",
                timestamp = now - hourMs * 8,
                notes = "Astral-parasitäres Wesen, absorbiert Umgebungs-Chi.",
                spectralColorHex = "#9900EE",
                isFavorite = false,
                lastWords = "Eure Lebenskraft erlischt."
            ),
            GhostDetectionEntity(
                name = "Infernaler Dämon Azazel",
                type = "Dämon",
                emfLevel = 9.5f,
                frequencyKhz = 94.0f,
                dangerLevel = 5,
                locationName = "Katakomben Level 4",
                timestamp = now - hourMs * 7,
                notes = "Hitze-Emission und Brandgeruch im Spektral-Scanner.",
                spectralColorHex = "#FF3300",
                isFavorite = false,
                lastWords = "Feuer und Asche erwarten euch."
            ),
            GhostDetectionEntity(
                name = "Poltergeist Poldi (Kinetik)",
                type = "Poltergeist",
                emfLevel = 7.6f,
                frequencyKhz = 46.5f,
                dangerLevel = 3,
                locationName = "Dachkammer",
                timestamp = now - hourMs * 9,
                notes = "Bewegt Gegenstände und wirft spektrale Funken.",
                spectralColorHex = "#FFAA00",
                isFavorite = false,
                lastWords = "Fass das nicht an!"
            ),
            GhostDetectionEntity(
                name = "Schattenkriecher Umbra",
                type = "Schattenwesen",
                emfLevel = 7.2f,
                frequencyKhz = 58.1f,
                dangerLevel = 3,
                locationName = "Schattengang",
                timestamp = now - hourMs * 10,
                notes = "Kriecht lautlos an Wänden und Ecken entlang.",
                spectralColorHex = "#444455",
                isFavorite = false,
                lastWords = "Im Dunkeln sind wir viele."
            ),
            GhostDetectionEntity(
                name = "Spukgeist Mathilda",
                type = "Geist",
                emfLevel = 5.8f,
                frequencyKhz = 52.0f,
                dangerLevel = 3,
                locationName = "Bibliothek Süd",
                timestamp = now - hourMs * 5,
                notes = "Schwebende Silhouette vor den alten Folianten.",
                spectralColorHex = "#33FF99",
                isFavorite = false,
                lastWords = "Die Bücher schweigen nie."
            ),
            GhostDetectionEntity(
                name = "Quanten-Spalte Omega [VERRIEGELT]",
                type = "DIMENSIONSRISS (GESCHLOSSEN)",
                emfLevel = 8.8f,
                frequencyKhz = 102.4f,
                dangerLevel = 4,
                locationName = "Dimensionen-Portalraum",
                timestamp = now - hourMs * 12,
                notes = "Interdimensionaler Riss versiegelt & stabilisiert.",
                spectralColorHex = "#00E5FF",
                isFavorite = true,
                lastWords = "Portal geschlossen."
            ),
            GhostDetectionEntity(
                name = "Ätherisches Phantom #12",
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
                name = "Banshee Klageweib Mara",
                type = "Banshee",
                emfLevel = 8.9f,
                frequencyKhz = 14.2f,
                dangerLevel = 4,
                locationName = "Moor-Garten",
                timestamp = now - hourMs * 14,
                notes = "Akustische Frequenzspitze vor dem Klagelaut.",
                spectralColorHex = "#00FFDD",
                isFavorite = false,
                lastWords = "Das Unheil naht..."
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
    }
}
