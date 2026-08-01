package com.example.data

data class SpiritLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val question: String,
    val phrase: String,
    val emfLevel: Float,
    val dangerLevel: Int
)
