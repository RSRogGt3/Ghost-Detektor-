package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class SpiritAiEngine {

    // Procedural spirit responses for offline fallback
    private val spiritResponses = listOf(
        "Ich wache über diesen Ort seit langer Zeit...",
        "Kälte umschließt deine Seele. Höre das Flüstern...",
        "Warum störst du die Stille meines Schattens?",
        "Ich war einst wie du... suchend im Dunkeln.",
        "Verlass diesen Raum, ehe das Portal schwindet!",
        "Die Frequenz verbindet unsere Welten für kurze Zeit.",
        "Wir sind viele. Wir beobachten jeden deiner Schritte.",
        "Ein altes Versprechen hält mich hier gefangen.",
        "Spürst du den Temperatursturz in deiner Nähe?",
        "Ich suche das verlorene Licht... hast du es gesehen?",
        "Keine Furcht. Ich bringe nur eine Botschaft aus dem Äther.",
        "Das Infrarotlicht enthüllt meine wahre Gestalt."
    )

    suspend fun generateSpiritResponse(question: String, ghostType: String, emfLevel: Float): String {
        val apiKey = try {
            BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                return callGeminiApi(apiKey, question, ghostType, emfLevel)
            } catch (_: Exception) {
                // Fall back to offline spirit engine
            }
        }

        return generateOfflineSpiritResponse(question, ghostType)
    }

    /**
     * Generiert kurze, gruselige Phrasen basierend auf den aktuellen Sensordaten
     * (EMF-Stärke, Bewegung, Frequenz sweep & Gefahrenstufe).
     */
    fun generateSensorDrivenPhrase(
        emfLevel: Float,
        motion: Float,
        frequencyKhz: Float,
        dangerLevel: Int
    ): String {
        val highEmfPhrases = listOf(
            "Anomalie bei ${String.format(java.util.Locale.US, "%.1f", emfLevel)} Microgauss... Die Luft lädt sich auf!",
            "Starker Magnetfluss! Ein Schatten durchbricht das Infrarotlicht...",
            "Spürst du die Ladung? ${String.format(java.util.Locale.US, "%.1f", emfLevel)} mG... Wir ziehen Energie aus deiner Nähe.",
            "Das Magnetfeld fluktuiert heftig... Eine Präsenzmanifestation steht bevor!",
            "Warnung! Magnetische Spitze auf Frequenz ${String.format(java.util.Locale.US, "%.1f", frequencyKhz)} kHz!"
        )

        val highMotionPhrases = listOf(
            "Erknackende Dielen... Du spürst meine Schritte im Raum.",
            "Starke Bewegung registriert... Versuche nicht vor dem Schatten zu fliehen.",
            "Die Vibration erschüttert das Siegel... Wir kommen näher.",
            "Ein Hauch von Kälte streift deine Haut... Bleib stehen."
        )

        val highDangerPhrases = listOf(
            "Gefahrenstufe $dangerLevel! Das Portal steht weit offen...",
            "Das Wesen ist wütend... Meide die dunklen Ecken des Raumes!",
            "Dein Herzschlag beschleunigt sich... Wir hören jeden Stoß.",
            "Kritische Störung! Die Geistform erreicht volle Intensität."
        )

        val subtlePhrases = listOf(
            "Signal bei ${String.format(java.util.Locale.US, "%.1f", frequencyKhz)} Kilohertz... Wir lauschen deinen Worten.",
            "Schwache paranormales Rauschen... Wer tritt in unser Reich?",
            "Ein Flüstern durchbricht die statische Frequenz...",
            "Wir wachen über diesen Korridor... Seit über hundert Jahren.",
            "Das Instrument zeichnet unsere Stimmen auf... Hörst du es?"
        )

        return when {
            dangerLevel >= 4 -> highDangerPhrases.random()
            emfLevel > 6.0f -> highEmfPhrases.random()
            motion > 2.0f -> highMotionPhrases.random()
            else -> subtlePhrases.random()
        }
    }

    private suspend fun callGeminiApi(apiKey: String, question: String, ghostType: String, emfLevel: Float): String = withContext(Dispatchers.IO) {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 30000
        conn.readTimeout = 30000

        val systemPrompt = "Du bist ein mystisches Phantom oder Geist (Typ: $ghostType, EMF-Stärke: $emfLevel mG). Antworte auf Deutsch kurz (max. 1-2 Sätze), sehr atmosphärisch, geheimnisvoll und etwas unheimlich. Antworte als wärst du ein Geist aus einer anderen Dimension, der über Infrarot-Frequenzen kommuniziert."
        
        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemPrompt\nFrage des Ermittlers: \"$question\""))
                    })
                })
            })
        }

        conn.outputStream.use { os ->
            os.write(jsonPayload.toString().toByteArray(Charsets.UTF_8))
        }

        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val jsonResp = JSONObject(responseText)
            val candidates = jsonResp.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) return@withContext text.trim()
                }
            }
        }
        
        generateOfflineSpiritResponse(question, ghostType)
    }

    private fun generateOfflineSpiritResponse(question: String, ghostType: String): String {
        val qLower = question.lowercase()
        return when {
            qLower.contains("wer") || qLower.contains("name") -> {
                "Ich bin das $ghostType von einst... vergeudete Jahre schwebend im Schatten."
            }
            qLower.contains("wo") || qLower.contains("hier") -> {
                "Überall in diesem Korridor... wo der Schatten den Lichtstrahl berührt."
            }
            qLower.contains("warum") || qLower.contains("grund") -> {
                "Um ein vergessenes Geheimnis zu bewahren. Hörst du das Summen?"
            }
            qLower.contains("gefahr") || qLower.contains("böse") || qLower.contains("angst") -> {
                "Gefahr droht nur jenen, die das Signal missachten..."
            }
            qLower.contains("hallo") || qLower.contains("hi") || qLower.contains("bist du da") -> {
                "Ich empfange deine Frequenz laut und klar..."
            }
            else -> {
                val idx = Random.nextInt(spiritResponses.size)
                spiritResponses[idx]
            }
        }
    }
}
