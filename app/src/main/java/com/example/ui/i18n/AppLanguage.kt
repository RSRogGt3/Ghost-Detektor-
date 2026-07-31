package com.example.ui.i18n

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flag: String
) {
    GERMAN("de", "Deutsch", "🇩🇪"),
    ENGLISH("en", "English", "🇬🇧"),
    TURKISH("tr", "Türkçe", "🇹🇷"),
    SPANISH("es", "Español", "🇪🇸"),
    FRENCH("fr", "Français", "🇫🇷");

    companion object {
        fun fromCode(code: String): AppLanguage =
            values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: GERMAN
    }
}

object UiStrings {
    fun getNavScanner(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Scanner"
        AppLanguage.ENGLISH -> "Scanner"
        AppLanguage.TURKISH -> "Tarayıcı"
        AppLanguage.SPANISH -> "Escáner"
        AppLanguage.FRENCH -> "Scanner"
    }

    fun getNavSpiritBox(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Spirit Box"
        AppLanguage.ENGLISH -> "Spirit Box"
        AppLanguage.TURKISH -> "Ruh Kutusu"
        AppLanguage.SPANISH -> "Caja Espiritual"
        AppLanguage.FRENCH -> "Boîte d'Esprit"
    }

    fun getNavHistory(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Verlauf"
        AppLanguage.ENGLISH -> "History"
        AppLanguage.TURKISH -> "Geçmiş"
        AppLanguage.SPANISH -> "Historial"
        AppLanguage.FRENCH -> "Historique"
    }

    fun getNavSettings(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Filter & App"
        AppLanguage.ENGLISH -> "Filter & App"
        AppLanguage.TURKISH -> "Filtre ve Ayarlar"
        AppLanguage.SPANISH -> "Filtro y Ajustes"
        AppLanguage.FRENCH -> "Filtre et Réglages"
    }

    // Scanner Screen
    fun getHudTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "INFRAROT HUD v4.2"
        AppLanguage.ENGLISH -> "INFRARED HUD v4.2"
        AppLanguage.TURKISH -> "KIZILÖTESİ HUD v4.2"
        AppLanguage.SPANISH -> "INFRARROJO HUD v4.2"
        AppLanguage.FRENCH -> "INFRAROUGE HUD v4.2"
    }

    fun getSensorHardwareActive(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENSOR: HARDWARE AKTIV"
        AppLanguage.ENGLISH -> "SENSOR: HARDWARE ACTIVE"
        AppLanguage.TURKISH -> "SENSÖR: DONANIM AKTİF"
        AppLanguage.SPANISH -> "SENSOR: HARDWARE ACTIVO"
        AppLanguage.FRENCH -> "CAPTEUR: MATÉRIEL ACTIF"
    }

    fun getSensorAtmospheric(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENSOR: ATMOSPHÄRISCH"
        AppLanguage.ENGLISH -> "SENSOR: ATMOSPHERIC"
        AppLanguage.TURKISH -> "SENSÖR: ATMOSFERİK"
        AppLanguage.SPANISH -> "SENSOR: ATMOSFÉRICO"
        AppLanguage.FRENCH -> "CAPTEUR: ATMOSPHÉRIQUE"
    }

    fun getQuickCaptureBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SCHNELLE ERFASSUNG IN ROOM DB"
        AppLanguage.ENGLISH -> "QUICK CAPTURE TO ROOM DB"
        AppLanguage.TURKISH -> "ROOM DB'YE HIZLI KAYIT"
        AppLanguage.SPANISH -> "CAPTURA RÁPIDA EN ROOM DB"
        AppLanguage.FRENCH -> "CAPTURE RAPIDE DANS ROOM DB"
    }

    fun getEmfCurveTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "PARANORMALE EMF-KURVE"
        AppLanguage.ENGLISH -> "PARANORMAL EMF CURVE"
        AppLanguage.TURKISH -> "PARANORMAL EMF EĞRİSİ"
        AppLanguage.SPANISH -> "CURVA EMF PARANORMAL"
        AppLanguage.FRENCH -> "COURBE EMF PARANORMALE"
    }

    fun getSaveEntityTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "GEISTER-EREIGNIS SPEICHERN"
        AppLanguage.ENGLISH -> "SAVE GHOST EVENT"
        AppLanguage.TURKISH -> "HAYALET OLAYINI KAYDET"
        AppLanguage.SPANISH -> "GUARDAR EVENTO FANTASMA"
        AppLanguage.FRENCH -> "ENREGISTRER L'ÉVÉNEMENT FANTÔME"
    }

    fun getEntityNameLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Name der Erscheinung"
        AppLanguage.ENGLISH -> "Entity Name"
        AppLanguage.TURKISH -> "Varlık Adı"
        AppLanguage.SPANISH -> "Nombre de la Entidad"
        AppLanguage.FRENCH -> "Nom de l'Entité"
    }

    fun getEntityLocationLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Ort / Raum"
        AppLanguage.ENGLISH -> "Location / Room"
        AppLanguage.TURKISH -> "Konum / Oda"
        AppLanguage.SPANISH -> "Ubicación / Habitación"
        AppLanguage.FRENCH -> "Lieu / Pièce"
    }

    fun getCancelBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ABBRECHEN"
        AppLanguage.ENGLISH -> "CANCEL"
        AppLanguage.TURKISH -> "İPTAL"
        AppLanguage.SPANISH -> "CANCELAR"
        AppLanguage.FRENCH -> "ANNULER"
    }

    fun getSaveBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPEICHERN"
        AppLanguage.ENGLISH -> "SAVE"
        AppLanguage.TURKISH -> "KAYDET"
        AppLanguage.SPANISH -> "GUARDAR"
        AppLanguage.FRENCH -> "ENREGISTRER"
    }

    // Spirit Box Screen
    fun getSpiritBoxHeader(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPIRIT BOX COMMUNICATIONS"
        AppLanguage.ENGLISH -> "SPIRIT BOX COMMUNICATIONS"
        AppLanguage.TURKISH -> "RUH KUTUSU İLETİŞİMİ"
        AppLanguage.SPANISH -> "COMUNICACIÓN CAJA ESPIRITUAL"
        AppLanguage.FRENCH -> "COMMUNICATIONS BOÎTE D'ESPRIT"
    }

    fun getVoicePitchLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "AUDIO STIMMEN PITCH"
        AppLanguage.ENGLISH -> "AUDIO VOICE PITCH"
        AppLanguage.TURKISH -> "SES PERDESİ (PITCH)"
        AppLanguage.SPANISH -> "TONO DE VOZ DE AUDIO"
        AppLanguage.FRENCH -> "HAUTEUR DE LA VOIX"
    }

    fun getVoiceSpeedLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPRECH-GESCHWINDIGKEIT"
        AppLanguage.ENGLISH -> "SPEECH SPEED / RATE"
        AppLanguage.TURKISH -> "KONUŞMA HIZI"
        AppLanguage.SPANISH -> "VELOCIDAD DE HABLA"
        AppLanguage.FRENCH -> "VITESSE DE PAROLE"
    }

    fun getSensorTtsCardTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENSOR-DATEN STIMMEN-SYNTHESE (TTS)"
        AppLanguage.ENGLISH -> "SENSOR DATA VOICE SYNTHESIS (TTS)"
        AppLanguage.TURKISH -> "SENSÖR VERİSİ SES SENTEZİ (TTS)"
        AppLanguage.SPANISH -> "SÍNTESIS DE VOZ CON DATOS SENSORIALES (TTS)"
        AppLanguage.FRENCH -> "SYNTHÈSE VOCALE DES DONNÉES CAPTEUR (TTS)"
    }

    fun getSensorTtsDesc(lang: AppLanguage, motion: String, freq: String): String = when(lang) {
        AppLanguage.GERMAN -> "Generiert aus Live-Magnetfeld, Bewegung ($motion) & Frequenz ($freq kHz) gruselige Spirit-Box Phrasen."
        AppLanguage.ENGLISH -> "Generates creepy spirit box phrases from live magnetic field, motion ($motion) & frequency ($freq kHz)."
        AppLanguage.TURKISH -> "Canlı manyetik alan, hareket ($motion) ve frekanstan ($freq kHz) ürkütücü ruh kutusu ifadeleri üretir."
        AppLanguage.SPANISH -> "Genera frases espeluznantes desde el campo magnético, movimiento ($motion) y frecuencia ($freq kHz)."
        AppLanguage.FRENCH -> "Génère des phrases effrayantes à partir du champ magnétique, mouvement ($motion) et fréquence ($freq kHz)."
    }

    fun getGenerateSensorPhraseBtn(lang: AppLanguage, isGenerating: Boolean): String = when(lang) {
        AppLanguage.GERMAN -> if (isGenerating) "GENERIEREN & SPEICHERN..." else "SENSOR-PHRASE GENERIEREN & SPRECHEN"
        AppLanguage.ENGLISH -> if (isGenerating) "GENERATING & SAVING..." else "GENERATE & SPEAK SENSOR PHRASE"
        AppLanguage.TURKISH -> if (isGenerating) "ÜRETİLİYOR..." else "SENSÖR İFADESİ ÜRET VE KONUŞ"
        AppLanguage.SPANISH -> if (isGenerating) "GENERANDO..." else "GENERAR Y HABLAR FRASE DEL SENSOR"
        AppLanguage.FRENCH -> if (isGenerating) "GÉNÉRATION..." else "GÉNÉRER ET PARLER LA PHRASE CAPTEUR"
    }

    fun getQuestionPlaceholder(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Frage an das Wesen eingeben..."
        AppLanguage.ENGLISH -> "Enter question for the entity..."
        AppLanguage.TURKISH -> "Varlığa sorulacak soruyu girin..."
        AppLanguage.SPANISH -> "Ingrese pregunta para la entidad..."
        AppLanguage.FRENCH -> "Entrez une question pour l'entité..."
    }

    fun getSendBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENDEN"
        AppLanguage.ENGLISH -> "SEND"
        AppLanguage.TURKISH -> "GÖNDER"
        AppLanguage.SPANISH -> "ENVIAR"
        AppLanguage.FRENCH -> "ENVOYER"
    }

    fun getPresetQuestions(lang: AppLanguage): List<String> = when(lang) {
        AppLanguage.GERMAN -> listOf(
            "Bist du bei uns im Raum?",
            "Wie heißt du?",
            "Warum bist du hier?",
            "Bist du friedlich oder gefährlich?"
        )
        AppLanguage.ENGLISH -> listOf(
            "Are you here with us in the room?",
            "What is your name?",
            "Why are you here?",
            "Are you peaceful or dangerous?"
        )
        AppLanguage.TURKISH -> listOf(
            "Bizimle bu odada mısın?",
            "Adın nedir?",
            "Neden buradasın?",
            "Barışçıl mısın yoksa tehlikeli mi?"
        )
        AppLanguage.SPANISH -> listOf(
            "¿Estás aquí con nosotros en la habitación?",
            "¿Cuál es tu nombre?",
            "¿Por qué estás aquí?",
            "¿Eres pacífico o peligroso?"
        )
        AppLanguage.FRENCH -> listOf(
            "Êtes-vous avec nous dans la pièce?",
            "Quel est votre nom?",
            "Pourquoi êtes-vous ici?",
            "Êtes-vous paisible ou dangereux?"
        )
    }

    // History Screen
    fun getHistoryHeader(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "VERLAUF DER FUNDE"
        AppLanguage.ENGLISH -> "DETECTION HISTORY"
        AppLanguage.TURKISH -> "TESPİT GEÇMİŞİ"
        AppLanguage.SPANISH -> "HISTORIAL DE HALLAZGOS"
        AppLanguage.FRENCH -> "HISTORIQUE DES DÉTECTIONS"
    }

    fun getHistoryDbCount(lang: AppLanguage, count: Int): String = when(lang) {
        AppLanguage.GERMAN -> "ROOM DB PERSISTENT • $count GESPEICHERTE EREIGNISSE"
        AppLanguage.ENGLISH -> "ROOM DB PERSISTENT • $count SAVED EVENTS"
        AppLanguage.TURKISH -> "ROOM DB KALICI • $count KAYITLI OLAY"
        AppLanguage.SPANISH -> "ROOM DB PERSISTENTE • $count EVENTOS GUARDADOS"
        AppLanguage.FRENCH -> "ROOM DB PERSISTANT • $count ÉVÉNEMENTS ENREGISTRÉS"
    }

    fun getExportBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "EXPORT"
        AppLanguage.ENGLISH -> "EXPORT"
        AppLanguage.TURKISH -> "DIŞA AKTAR"
        AppLanguage.SPANISH -> "EXPORTAR"
        AppLanguage.FRENCH -> "EXPORTER"
    }

    fun getSearchPlaceholder(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Geist-Namen, Ort oder Notiz suchen..."
        AppLanguage.ENGLISH -> "Search ghost name, location or note..."
        AppLanguage.TURKISH -> "Hayalet adı, konum veya not ara..."
        AppLanguage.SPANISH -> "Buscar nombre de fantasma, ubicación o nota..."
        AppLanguage.FRENCH -> "Rechercher nom de fantôme, lieu ou note..."
    }

    fun getFavoritesFilter(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "NUR FAVORITEN"
        AppLanguage.ENGLISH -> "FAVORITES ONLY"
        AppLanguage.TURKISH -> "SADECE FAVORİLER"
        AppLanguage.SPANISH -> "SOLO FAVORITOS"
        AppLanguage.FRENCH -> "FAVORIS SEULEMENT"
    }

    fun getTypeFilters(lang: AppLanguage): List<String> = when(lang) {
        AppLanguage.GERMAN -> listOf("ALLE", "Poltergeist", "Phantom", "Schattenwesen", "Orb-Vorkommen")
        AppLanguage.ENGLISH -> listOf("ALL", "Poltergeist", "Phantom", "Shadow Being", "Orb Occurrence")
        AppLanguage.TURKISH -> listOf("TÜMÜ", "Poltergeist", "Hayalet/Fantom", "Gölge Varlık", "Orb Olayı")
        AppLanguage.SPANISH -> listOf("TODOS", "Poltergeist", "Fantasma", "Ser de Sombra", "Aparición de Orbe")
        AppLanguage.FRENCH -> listOf("TOUS", "Poltergeist", "Fantôme", "Être d'Ombre", "Apparition d'Orbe")
    }

    fun getExportDialogTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ROOM DB EXPORT"
        AppLanguage.ENGLISH -> "ROOM DB EXPORT"
        AppLanguage.TURKISH -> "ROOM DB DIŞA AKTARIM"
        AppLanguage.SPANISH -> "EXPORTAR ROOM DB"
        AppLanguage.FRENCH -> "EXPORTATION ROOM DB"
    }

    fun getExportDialogSubtitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Lesbares Text-Protokoll der gespeicherten Geister-Ereignisse:"
        AppLanguage.ENGLISH -> "Readable text log of saved ghost events:"
        AppLanguage.TURKISH -> "Kayıtlı hayalet olaylarının okunabilir metin günlüğü:"
        AppLanguage.SPANISH -> "Registro de texto legible de eventos fantasma guardados:"
        AppLanguage.FRENCH -> "Journal texte lisible des événements fantômes enregistrés:"
    }

    fun getShareBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "TEILEN"
        AppLanguage.ENGLISH -> "SHARE"
        AppLanguage.TURKISH -> "PAYLAŞ"
        AppLanguage.SPANISH -> "COMPARTIR"
        AppLanguage.FRENCH -> "PARTAGER"
    }

    fun getCopyBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "KOPIEREN"
        AppLanguage.ENGLISH -> "COPY"
        AppLanguage.TURKISH -> "KOPYALA"
        AppLanguage.SPANISH -> "COPIAR"
        AppLanguage.FRENCH -> "COPIER"
    }

    fun getCloseBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SCHLIESSEN"
        AppLanguage.ENGLISH -> "CLOSE"
        AppLanguage.TURKISH -> "KAPAT"
        AppLanguage.SPANISH -> "CERRAR"
        AppLanguage.FRENCH -> "FERMER"
    }

    // Filter Settings Screen
    fun getSettingsHeader(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "INFRAROT FILTER & STEUERUNG"
        AppLanguage.ENGLISH -> "INFRARED FILTER & CONTROLS"
        AppLanguage.TURKISH -> "KIZILÖTESİ FİLTRE VE KONTROLLER"
        AppLanguage.SPANISH -> "FILTRO INFRARROJO Y CONTROLES"
        AppLanguage.FRENCH -> "FILTRE INFRAROUGE ET CONTRÔLES"
    }

    fun getActiveFilterTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "AKTIVER SPECTRAL FILTER:"
        AppLanguage.ENGLISH -> "ACTIVE SPECTRAL FILTER:"
        AppLanguage.TURKISH -> "AKTİF SPEKTRAL FİLTRE:"
        AppLanguage.SPANISH -> "FILTRO ESPECTRAL ACTIVO:"
        AppLanguage.FRENCH -> "FILTRE SPECTRAL ACTIF:"
    }

    fun getLanguageCardTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPRACHE DER BENUTZEROBERFLÄCHE"
        AppLanguage.ENGLISH -> "INTERFACE LANGUAGE"
        AppLanguage.TURKISH -> "KULLANICI ARAYÜZÜ DİLİ"
        AppLanguage.SPANISH -> "IDIOMA DE LA INTERFAZ"
        AppLanguage.FRENCH -> "LANGUE DE L'INTERFACE"
    }

    fun getDisplayOptionsTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ANZEIGE & AUDIO EINSTELLUNGEN"
        AppLanguage.ENGLISH -> "DISPLAY & AUDIO SETTINGS"
        AppLanguage.TURKISH -> "GÖRÜNTÜ VE SES AYARLARI"
        AppLanguage.SPANISH -> "AJUSTES DE PANTALLA Y AUDIO"
        AppLanguage.FRENCH -> "RÉGLAGES D'AFFICHAGE ET AUDIO"
    }

    fun getCrtOverlayLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "CRT RÖHREN-SCANLINES OVERLAY"
        AppLanguage.ENGLISH -> "CRT SCANLINES OVERLAY"
        AppLanguage.TURKISH -> "CRT TARAMA ÇİZGİLERİ KATMANI"
        AppLanguage.SPANISH -> "SUPERPOSICIÓN DE LÍNEAS CRT"
        AppLanguage.FRENCH -> "SUPERPOSITION LIGNES CRT"
    }

    fun getCameraFeedLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "LIVE INFRA-GRÜN KAMERA-FEED"
        AppLanguage.ENGLISH -> "LIVE INFRA-GREEN CAMERA FEED"
        AppLanguage.TURKISH -> "CANLI KIZIL-YEŞİL KAMERA AKIŞI"
        AppLanguage.SPANISH -> "CANAL DE CÁMARA INFRARROJO EN VIVO"
        AppLanguage.FRENCH -> "FLUX CAMÉRA INFRAROUGE EN DIRECT"
    }

    fun getCameraFeedDesc(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Echte Kamera im Hintergrund mit digitalem Nachtsicht-Filter"
        AppLanguage.ENGLISH -> "Use real camera background with digital night-vision filter"
        AppLanguage.TURKISH -> "Dijital gece görüş filtresi ile canlı kamera arka planı"
        AppLanguage.SPANISH -> "Usar cámara real en segundo plano con filtro de visión nocturna"
        AppLanguage.FRENCH -> "Utiliser une vraie caméra en arrière-plan avec filtre de vision nocturne"
    }

    fun getAudioFeedbackLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "AUDIO-FEEDBACK & FREQUENZ-SUMMEN"
        AppLanguage.ENGLISH -> "AUDIO FEEDBACK & FREQUENCY BUZZ"
        AppLanguage.TURKISH -> "SESLİ GERİ BİLDİRİM VE FREKANS SESİ"
        AppLanguage.SPANISH -> "RETROALIMENTACIÓN DE AUDIO Y ZUMBIDO"
        AppLanguage.FRENCH -> "RETOUR AUDIO ET BROMDONNEMENT"
    }

    fun getClearDbTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ALLE DB-EINTRÄGE LÖSCHEN"
        AppLanguage.ENGLISH -> "DELETE ALL DB ENTRIES"
        AppLanguage.TURKISH -> "TÜM DB KAYITLARINI SİL"
        AppLanguage.SPANISH -> "ELIMINAR TODOS LOS REGISTROS DB"
        AppLanguage.FRENCH -> "SUPPRIMER TOUTES LES ENTRÉES DB"
    }
}
