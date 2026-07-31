package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ghost_detections")
data class GhostDetectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String, // Poltergeist, Schattenwesen, Phantom, Banshee, Elementar, EVP-Stimme, Orb-Vorkommen
    val emfLevel: Float, // e.g., 7.8 mG
    val frequencyKhz: Float, // e.g., 42.5 kHz
    val dangerLevel: Int, // 1 to 5
    val locationName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val spectralColorHex: String = "#00FF66",
    val isFavorite: Boolean = false,
    val lastWords: String = "" // Spirit box response text if captured
)
