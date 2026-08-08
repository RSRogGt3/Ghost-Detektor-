package com.example.ai

import com.example.BuildConfig
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class SpiritAiEngine {

    suspend fun generateSpiritResponse(
        question: String,
        ghostType: String,
        emfLevel: Float,
        language: AppLanguage = AppLanguage.GERMAN
    ): String {
        val apiKey = try {
            BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                return callGeminiApi(apiKey, question, ghostType, emfLevel, language)
            } catch (_: Exception) {
                // Fall back to offline spirit engine
            }
        }

        return generateOfflineSpiritResponse(question, ghostType, language)
    }

    /**
     * Generiert kurze, gruselige Phrasen basierend auf den aktuellen Sensordaten
     * (EMF-Stärke, Bewegung, Frequenz sweep & Gefahrenstufe) in der gewählten Sprache.
     */
    fun generateSensorDrivenPhrase(
        emfLevel: Float,
        motion: Float,
        frequencyKhz: Float,
        dangerLevel: Int,
        language: AppLanguage = AppLanguage.GERMAN
    ): String {
        val formattedEmf = String.format(java.util.Locale.US, "%.1f", emfLevel)
        val formattedFreq = String.format(java.util.Locale.US, "%.1f", frequencyKhz)

        return when (language) {
            AppLanguage.GERMAN -> {
                val highEmfPhrases = listOf(
                    "Anomalie bei $formattedEmf Microgauss... Die Luft lädt sich auf!",
                    "Starker Magnetfluss! Ein Schatten durchbricht das Infrarotlicht...",
                    "Spürst du die Ladung? $formattedEmf mG... Wir ziehen Energie aus deiner Nähe.",
                    "Das Magnetfeld fluktuiert heftig... Eine Präsenzmanifestation steht bevor!",
                    "Warnung! Magnetische Spitze auf Frequenz $formattedFreq kHz!",
                    "Die magnetische Spannung zerreißt die Dimensionen!",
                    "Energiepegel kritisch! Die Entität speist sich aus dem EMF-Feld."
                )
                val highMotionPhrases = listOf(
                    "Erknackende Dielen... Du spürst meine Schritte im Raum.",
                    "Starke Bewegung registriert... Versuche nicht vor dem Schatten zu fliehen.",
                    "Die Vibration erschüttert das Siegel... Wir kommen näher.",
                    "Ein Hauch von Kälte streift deine Haut... Bleib stehen.",
                    "Der Boden zittert unter unsichtbaren Füßen...",
                    "Jede Erschütterung öffnet das Portal ein Stück weiter."
                )
                val highDangerPhrases = listOf(
                    "Gefahrenstufe $dangerLevel! Das Portal steht weit offen...",
                    "Das Wesen ist wütend... Meide die dunklen Ecken des Raumes!",
                    "Dein Herzschlag beschleunigt sich... Wir hören jeden Stoß.",
                    "Kritische Störung! Die Geistform erreicht volle Intensität.",
                    "Flieh solange du kannst! Die Dunkelheit ergreift Besitz von dir.",
                    "Kein Entkommen mehr... Die Jenseitsgrenze ist gefallen."
                )
                val subtlePhrases = listOf(
                    "Signal bei $formattedFreq Kilohertz... Wir lauschen deinen Worten.",
                    "Schwaches paranormales Rauschen... Wer tritt in unser Reich?",
                    "Ein Flüstern durchbricht die statische Frequenz...",
                    "Wir wachen über diesen Korridor... Seit über hundert Jahren.",
                    "Das Instrument zeichnet unsere Stimmen auf... Hörst du es?",
                    "Im Äther schwingt eine ferne Erinnerung...",
                    "Sanftes Rauschen im Raum... Jemand atmet leise mit."
                )
                when {
                    dangerLevel >= 4 -> highDangerPhrases.random()
                    emfLevel > 6.0f -> highEmfPhrases.random()
                    motion > 2.0f -> highMotionPhrases.random()
                    else -> subtlePhrases.random()
                }
            }
            AppLanguage.ENGLISH -> {
                val phrases = listOf(
                    "Anomaly detected at $formattedEmf Microgauss... The air is charging!",
                    "Strong magnetic field flux! A shadow breaks through the infrared light...",
                    "Warning! Danger level $dangerLevel reached! The veil is thinning...",
                    "Signal locked at $formattedFreq kHz... We hear your footsteps in the dark.",
                    "The spirit energy feeds upon the electromagnetic field...",
                    "Do not run... We have watched over this corridor for a century."
                )
                phrases.random()
            }
            AppLanguage.SPANISH -> {
                val phrases = listOf(
                    "Anomalía detectada a $formattedEmf microgauss... La presencia se acerca.",
                    "¡Campo magnético fuerte! Una sombra cruza el espectro infrarrojo...",
                    "¡Nivel de peligro $dangerLevel! La puerta dimensional se abre...",
                    "Frecuencia fijada en $formattedFreq kHz... Escuchamos tu respiración.",
                    "La energía espiritual se alimenta de tu miedo...",
                    "No temas al frío... Hemos estado aquí durante siglos."
                )
                phrases.random()
            }
            AppLanguage.FRENCH -> {
                val phrases = listOf(
                    "Anomalie détectée à $formattedEmf Microgauss... L'air se charge en énergie!",
                    "Flux magnétique intense! Une ombre traverse la lumière infrarouge...",
                    "Alerte! Niveau de danger $dangerLevel! Le portail se détériore...",
                    "Signal capté à $formattedFreq kHz... Nous écoutons vos murmures.",
                    "L'esprit puise sa force dans le champ électromagnétique...",
                    "Restez immobile... Notre présence entoure cette pièce."
                )
                phrases.random()
            }
            AppLanguage.TURKISH -> {
                val phrases = listOf(
                    "$formattedEmf Microgauss seviyesinde anormallik tespit edildi...",
                    "Güçlü manyetik akı! Kızılötesi ışıkta bir gölge belirdi...",
                    "Tehlike seviyesi $dangerLevel! Ruhani portal açılıyor...",
                    "$formattedFreq kHz frekansında sinyal yakalandı... Sizi dinliyoruz.",
                    "Ruh enerjisi manyetik alandan besleniyor...",
                    "Korkmayın... Yüz yıldır bu odada bekliyoruz."
                )
                phrases.random()
            }
            AppLanguage.ITALIAN -> {
                val phrases = listOf(
                    "Anomalia rilevata a $formattedEmf Microgauss... L'aria si carica di energia!",
                    "Forte flusso magnetico! Un'ombra attraversa la luce infrarossa...",
                    "Livello di pericolo $dangerLevel! Il portale è spalancato...",
                    "Frequenza agganciata a $formattedFreq kHz... Ascoltiamo i tuoi passi.",
                    "L'entità trae forza dal campo elettromagnetico...",
                    "Rimani calmo... Vegliamo su questa stanza da oltre un secolo."
                )
                phrases.random()
            }
            AppLanguage.POLISH -> {
                val phrases = listOf(
                    "Wykryto anomalię $formattedEmf Microgauss... Powietrze ładuje się energią!",
                    "Silny strumień magnetyczny! Cień przełamuje światło podczerwone...",
                    "Poziom zagrożenia $dangerLevel! Portal wymiarowy otwiera się...",
                    "Sygnał uchwycony na $formattedFreq kHz... Słyszymy twój oddech.",
                    "Energia duchowa karmi się polem elektromagnetycznym...",
                    "Nie uciekaj... Czuwamy w tym korytarzu od stu lat."
                )
                phrases.random()
            }
            AppLanguage.DUTCH -> {
                val phrases = listOf(
                    "Anomalie gemeten bij $formattedEmf Microgauss... De lucht laadt op!",
                    "Sterk magnetisch veld! Een schaduw doorkruist het infraroodlicht...",
                    "Gevaarniveau $dangerLevel bereikt! De dimensiepoort staat open...",
                    "Signaal op $formattedFreq kHz vastgelegd... Wij luisteren naar je zuchten.",
                    "De geestkracht voedt zich met het EMF-veld...",
                    "Blijf rustig... Wij waken al een eeuw over deze ruimte."
                )
                phrases.random()
            }
        }
    }

    private suspend fun callGeminiApi(
        apiKey: String,
        question: String,
        ghostType: String,
        emfLevel: Float,
        language: AppLanguage
    ): String = withContext(Dispatchers.IO) {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 30000
        conn.readTimeout = 30000

        val langInstruction = when (language) {
            AppLanguage.GERMAN -> "Nutze einen extrem reichhaltigen deutschen Wortschatz (inspiriert vom Duden und der deutschen Klassik/Parapsychologie: Begriffe wie Äther, Manifestation, Resonanz, Transzendenz, Schwingung, Nekromantie, Präsenz). Antworte auf Deutsch artikuliert, geheimnisvoll und ausführlich (2-3 Sätze)."
            else -> "Respond articulately and mysteriously in ${language.displayName} (2-3 sentences), as a spectral entity communicating through a spirit box."
        }

        val systemPrompt = "Du bist ein mystisches Phantom oder Geist (Typ: $ghostType, EMF-Stärke: $emfLevel mG). $langInstruction"
        
        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemPrompt\nErmittler-Frage über Mikrofon: \"$question\""))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.85)
                put("topP", 0.95)
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
        
        generateOfflineSpiritResponse(question, ghostType, language)
    }

    private fun generateOfflineSpiritResponse(question: String, ghostType: String, language: AppLanguage): String {
        return if (language == AppLanguage.GERMAN) {
            GermanDudenVocabulary.buildRichGermanPhrase(question, 5.0f)
        } else {
            val fallbackResponses = when (language) {
                AppLanguage.ENGLISH -> listOf(
                    "I have watched over this place for a long time...",
                    "Coldness surrounds your soul. Listen closely to the whisper...",
                    "Why do you disturb the peace of my shadow?",
                    "I was once like you... searching in the dark.",
                    "Leave this room before the portal fades!",
                    "The frequency binds our worlds for a brief moment."
                )
                AppLanguage.SPANISH -> listOf(
                    "He vigilado este lugar durante mucho tiempo...",
                    "El frío rodea tu alma. Escucha el susurro en la oscuridad...",
                    "¿Por qué perturbas el silencio de mi sombra?",
                    "Yo fui como tú una vez... buscando en la penumbra.",
                    "¡Abandona esta habitación antes de que el portal se cierre!"
                )
                AppLanguage.FRENCH -> listOf(
                    "Je veille sur cet endroit depuis fort longtemps...",
                    "Le froid enveloppe ton âme. Écoute le murmure...",
                    "Pourquoi perturbes-tu le silence de mon ombre?",
                    "J'étais autrefois comme toi... cherchant dans le noir.",
                    "Quitte cette pièce avant que le portail ne disparaisse!"
                )
                AppLanguage.TURKISH -> listOf(
                    "Uzun zamandır bu yeri gözlemliyorum...",
                    "Soğukluk ruhunu sarıyor. Fısıltıyı dinle...",
                    "Neden gölgemin sessizliğini bozuyorsun?",
                    "Ben de bir zamanlar senin gibiydim... karanlıkta arayan.",
                    "Geçit kapanmadan önce bu odayı terk et!"
                )
                AppLanguage.ITALIAN -> listOf(
                    "Veglio su questo luogo da molto tempo...",
                    "Il freddo avvolge la tua anima. Ascolta il sussurro...",
                    "Perché disturbi la quiete della mia ombra?",
                    "Un tempo ero come te... in cerca nell'oscurità.",
                    "Lascia questa stanza prima che il portale svanisca!"
                )
                AppLanguage.POLISH -> listOf(
                    "Czuwam nad tym miejscem od bardzo dawna...",
                    "Zimno otacza twoją duszę. Posłuchaj szeptu...",
                    "Dlaczego zakłócasz spokój mojego cienia?",
                    "Kiedyś byłem taki jak ty... szukając w ciemności.",
                    "Opuść ten pokój zanim portal zniknie!"
                )
                AppLanguage.DUTCH -> listOf(
                    "Ik waak al heel lang over deze plek...",
                    "Kou omarmt je ziel. Luister naar het gefluister...",
                    "Waarom verstoor je de stilte van mijn schaduw?",
                    "Ik was ooit zoals jij... zoekend in het duister.",
                    "Verlaat deze ruimte voordat de poort verdwijnt!"
                )
                else -> listOf("Ich wache über diesen Ort seit langer Zeit...")
            }
            fallbackResponses.random()
        }
    }
}
