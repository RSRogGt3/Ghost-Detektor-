package com.example.ai

/**
 * Unterstützte KI-Sprachmodelle für die parapsychologische Spirit-Box & EVP-Analyse.
 * Beinhaltet moderne Gemini-Modelle nach den offiziellen Richtlinien sowie
 * die lokale Offline-Duden-Geister-Synthese.
 */
enum class LanguageModelConfig(
    val id: String,
    val displayName: String,
    val modelTag: String,
    val description: String,
    val isCloudModel: Boolean
) {
    GEMINI_3_5_FLASH(
        id = "gemini-3.5-flash",
        displayName = "Gemini 3.5 Flash",
        modelTag = "3.5 FLASH",
        description = "Standard Sprachmodell: Schnell, reaktionsstark & parapsychologisch artikuliert",
        isCloudModel = true
    ),
    GEMINI_3_1_PRO(
        id = "gemini-3.1-pro-preview",
        displayName = "Gemini 3.1 Pro Preview",
        modelTag = "3.1 PRO",
        description = "Tiefgründiges Okkultismus- & Geister-Wissen mit komplexer Bewusstseins-Logik",
        isCloudModel = true
    ),
    GEMINI_3_1_FLASH_LITE(
        id = "gemini-3.1-flash-lite-preview",
        displayName = "Gemini 3.1 Flash-Lite Preview",
        modelTag = "FLASH-LITE",
        description = "Ultra-schneller Echtzeit-Äther-Sweep mit minimaler Reaktionszeit",
        isCloudModel = true
    ),
    OFFLINE_SPECTRAL_DUDEN(
        id = "offline-spectral-duden",
        displayName = "Offline Phantom Engine (Duden)",
        modelTag = "OFFLINE DUDEN",
        description = "Lokale EVP-Geister-Synthese ohne Internet & ohne API-Key",
        isCloudModel = false
    )
}

/**
 * Detaillierte Debug- und Telemetriedaten für die Sprachmodell-Diagnose.
 */
data class AiDebugTelemetry(
    val timestamp: Long = System.currentTimeMillis(),
    val model: LanguageModelConfig = LanguageModelConfig.GEMINI_3_5_FLASH,
    val latencyMs: Long = 0,
    val httpStatusCode: Int = 0,
    val isSuccess: Boolean = false,
    val statusMessage: String = "Bereit für Diagnose",
    val requestPrompt: String = "",
    val responseText: String = "",
    val rawError: String? = null,
    val apiKeySource: String = "Unbekannt"
)
