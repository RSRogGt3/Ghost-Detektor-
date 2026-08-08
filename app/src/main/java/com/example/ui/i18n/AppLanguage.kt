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
    FRENCH("fr", "Français", "🇫🇷"),
    ITALIAN("it", "Italiano", "🇮🇹"),
    POLISH("pl", "Polski", "🇵🇱"),
    DUTCH("nl", "Nederlands", "🇳🇱");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: GERMAN
    }
}

object UiStrings {
    fun getNavScanner(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Scanner"
        AppLanguage.ENGLISH -> "Scanner"
        AppLanguage.TURKISH -> "Tarayıcı"
        AppLanguage.SPANISH -> "Escáner"
        AppLanguage.FRENCH -> "Scanner"
        AppLanguage.ITALIAN -> "Scanner"
        AppLanguage.POLISH -> "Skaner"
        AppLanguage.DUTCH -> "Scanner"
    }

    fun getNavSpiritBox(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Spirit Box"
        AppLanguage.ENGLISH -> "Spirit Box"
        AppLanguage.TURKISH -> "Ruh Kutusu"
        AppLanguage.SPANISH -> "Caja Espiritual"
        AppLanguage.FRENCH -> "Boîte d'Esprit"
        AppLanguage.ITALIAN -> "Spirit Box"
        AppLanguage.POLISH -> "Spirit Box"
        AppLanguage.DUTCH -> "Spirit Box"
    }

    fun getNavHistory(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Verlauf"
        AppLanguage.ENGLISH -> "History"
        AppLanguage.TURKISH -> "Geçmiş"
        AppLanguage.SPANISH -> "Historial"
        AppLanguage.FRENCH -> "Historique"
        AppLanguage.ITALIAN -> "Cronologia"
        AppLanguage.POLISH -> "Historia"
        AppLanguage.DUTCH -> "Geschiedenis"
    }

    fun getNavSettings(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Filter & App"
        AppLanguage.ENGLISH -> "Filter & App"
        AppLanguage.TURKISH -> "Filtre ve Ayarlar"
        AppLanguage.SPANISH -> "Filtro y Ajustes"
        AppLanguage.FRENCH -> "Filtre et Réglages"
        AppLanguage.ITALIAN -> "Filtri e App"
        AppLanguage.POLISH -> "Filtry i Aplikacja"
        AppLanguage.DUTCH -> "Filter & App"
    }

    // Scanner Screen
    fun getHudTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "INFRAROT HUD v4.2"
        AppLanguage.ENGLISH -> "INFRARED HUD v4.2"
        AppLanguage.TURKISH -> "KIZILÖTESİ HUD v4.2"
        AppLanguage.SPANISH -> "INFRARROJO HUD v4.2"
        AppLanguage.FRENCH -> "INFRAROUGE HUD v4.2"
        AppLanguage.ITALIAN -> "INFRAROSSI HUD v4.2"
        AppLanguage.POLISH -> "HUD PODCZERWIENI v4.2"
        AppLanguage.DUTCH -> "INFRAROOD HUD v4.2"
    }

    fun getSensorHardwareActive(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENSOR: HARDWARE AKTIV"
        AppLanguage.ENGLISH -> "SENSOR: HARDWARE ACTIVE"
        AppLanguage.TURKISH -> "SENSÖR: DONANIM AKTİF"
        AppLanguage.SPANISH -> "SENSOR: HARDWARE ACTIVO"
        AppLanguage.FRENCH -> "CAPTEUR: MATÉRIEL ACTIF"
        AppLanguage.ITALIAN -> "SENSORE: HARDWARE ATTIVO"
        AppLanguage.POLISH -> "CZUJNIK: HARDWARE AKTYWNY"
        AppLanguage.DUTCH -> "SENSOR: HARDWARE ACTIEF"
    }

    fun getSensorAtmospheric(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENSOR: ATMOSPHÄRISCH"
        AppLanguage.ENGLISH -> "SENSOR: ATMOSPHERIC"
        AppLanguage.TURKISH -> "SENSÖR: ATMOSFERİK"
        AppLanguage.SPANISH -> "SENSOR: ATMOSFÉRICO"
        AppLanguage.FRENCH -> "CAPTEUR: ATMOSPHÉRIQUE"
        AppLanguage.ITALIAN -> "SENSORE: ATMOSFERICO"
        AppLanguage.POLISH -> "CZUJNIK: ATMOSFERYCZNY"
        AppLanguage.DUTCH -> "SENSOR: ATMOSFERISCH"
    }

    fun getQuickCaptureBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SCHNELLE ERFASSUNG IN ROOM DB"
        AppLanguage.ENGLISH -> "QUICK CAPTURE TO ROOM DB"
        AppLanguage.TURKISH -> "ROOM DB'YE HIZLI KAYIT"
        AppLanguage.SPANISH -> "CAPTURA RÁPIDA EN ROOM DB"
        AppLanguage.FRENCH -> "CAPTURE RAPIDE DANS ROOM DB"
        AppLanguage.ITALIAN -> "CATTURA RAPIDA IN ROOM DB"
        AppLanguage.POLISH -> "SZYBKIE PRZECHWYCENIE W ROOM DB"
        AppLanguage.DUTCH -> "SNELLE VASTLEGGING IN ROOM DB"
    }

    fun getEmfCurveTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "PARANORMALE EMF-KURVE"
        AppLanguage.ENGLISH -> "PARANORMAL EMF CURVE"
        AppLanguage.TURKISH -> "PARANORMAL EMF EĞRİSİ"
        AppLanguage.SPANISH -> "CURVA EMF PARANORMAL"
        AppLanguage.FRENCH -> "COURBE EMF PARANORMALE"
        AppLanguage.ITALIAN -> "CURVA EMF PARANORMALE"
        AppLanguage.POLISH -> "KRZYWA EMF PARANORMALNA"
        AppLanguage.DUTCH -> "PARANORMALE EMF-CURVE"
    }

    fun getSaveEntityTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "GEISTER-EREIGNIS SPEICHERN"
        AppLanguage.ENGLISH -> "SAVE GHOST EVENT"
        AppLanguage.TURKISH -> "HAYALET OLAYINI KAYDET"
        AppLanguage.SPANISH -> "GUARDAR EVENTO FANTASMA"
        AppLanguage.FRENCH -> "ENREGISTRER L'ÉVÉNEMENT FANTÔME"
        AppLanguage.ITALIAN -> "SALVA EVENTO FANTASMA"
        AppLanguage.POLISH -> "ZAPISZ ZDARZENIE DUCHA"
        AppLanguage.DUTCH -> "SPOOKGEBEURTENIS OPSLAAN"
    }

    fun getEntityNameLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Name der Erscheinung"
        AppLanguage.ENGLISH -> "Entity Name"
        AppLanguage.TURKISH -> "Varlık Adı"
        AppLanguage.SPANISH -> "Nombre de la Entidad"
        AppLanguage.FRENCH -> "Nom de l'Entité"
        AppLanguage.ITALIAN -> "Nome dell'Entità"
        AppLanguage.POLISH -> "Nazwa Zjawiska"
        AppLanguage.DUTCH -> "Naam van Verschijning"
    }

    fun getEntityLocationLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Ort / Raum"
        AppLanguage.ENGLISH -> "Location / Room"
        AppLanguage.TURKISH -> "Konum / Oda"
        AppLanguage.SPANISH -> "Ubicación / Habitación"
        AppLanguage.FRENCH -> "Lieu / Pièce"
        AppLanguage.ITALIAN -> "Luogo / Stanza"
        AppLanguage.POLISH -> "Lokalizacja / Pokój"
        AppLanguage.DUTCH -> "Locatie / Ruimte"
    }

    fun getCancelBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ABBRECHEN"
        AppLanguage.ENGLISH -> "CANCEL"
        AppLanguage.TURKISH -> "İPTAL"
        AppLanguage.SPANISH -> "CANCELAR"
        AppLanguage.FRENCH -> "ANNULER"
        AppLanguage.ITALIAN -> "ANNULLA"
        AppLanguage.POLISH -> "ANULUJ"
        AppLanguage.DUTCH -> "ANNULEREN"
    }

    fun getSaveBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPEICHERN"
        AppLanguage.ENGLISH -> "SAVE"
        AppLanguage.TURKISH -> "KAYDET"
        AppLanguage.SPANISH -> "GUARDAR"
        AppLanguage.FRENCH -> "ENREGISTRER"
        AppLanguage.ITALIAN -> "SALVA"
        AppLanguage.POLISH -> "ZAPISZ"
        AppLanguage.DUTCH -> "OPSLAAN"
    }

    // Spirit Box Screen
    fun getSpiritBoxHeader(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPIRIT BOX COMMUNICATIONS"
        AppLanguage.ENGLISH -> "SPIRIT BOX COMMUNICATIONS"
        AppLanguage.TURKISH -> "RUH KUTUSU İLETİŞİMİ"
        AppLanguage.SPANISH -> "COMUNICACIÓN CAJA ESPIRITUAL"
        AppLanguage.FRENCH -> "COMMUNICATIONS BOÎTE D'ESPRIT"
        AppLanguage.ITALIAN -> "COMUNICAZIONI SPIRIT BOX"
        AppLanguage.POLISH -> "KOMUNIKACJA SPIRIT BOX"
        AppLanguage.DUTCH -> "SPIRIT BOX COMMUNICATIE"
    }

    fun getVoicePitchLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "AUDIO STIMMEN PITCH"
        AppLanguage.ENGLISH -> "AUDIO VOICE PITCH"
        AppLanguage.TURKISH -> "SES PERDESİ (PITCH)"
        AppLanguage.SPANISH -> "TONO DE VOZ DE AUDIO"
        AppLanguage.FRENCH -> "HAUTEUR DE LA VOIX"
        AppLanguage.ITALIAN -> "TONO DELLA VOCE"
        AppLanguage.POLISH -> "TON GŁOSU"
        AppLanguage.DUTCH -> "AUDIO STEMPITCH"
    }

    fun getVoiceSpeedLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPRECH-GESCHWINDIGKEIT"
        AppLanguage.ENGLISH -> "SPEECH SPEED / RATE"
        AppLanguage.TURKISH -> "KONUŞMA HIZI"
        AppLanguage.SPANISH -> "VELOCIDAD DE HABLA"
        AppLanguage.FRENCH -> "VITESSE DE PAROLE"
        AppLanguage.ITALIAN -> "VELOCITÀ DI PAROLA"
        AppLanguage.POLISH -> "PRĘDKOŚĆ MOWY"
        AppLanguage.DUTCH -> "SPREEKSNELHEID"
    }

    fun getSensorTtsCardTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENSOR-DATEN STIMMEN-SYNTHESE (TTS)"
        AppLanguage.ENGLISH -> "SENSOR DATA VOICE SYNTHESIS (TTS)"
        AppLanguage.TURKISH -> "SENSÖR VERİSİ SES SENTEZİ (TTS)"
        AppLanguage.SPANISH -> "SÍNTESIS DE VOZ CON DATOS SENSORIALES (TTS)"
        AppLanguage.FRENCH -> "SYNTHÈSE VOCALE DES DONNÉES CAPTEUR (TTS)"
        AppLanguage.ITALIAN -> "SINTESI VOCALE DATI SENSORE (TTS)"
        AppLanguage.POLISH -> "SYNTEZA MOWY DANYCH CZUJNIKA (TTS)"
        AppLanguage.DUTCH -> "SENSORDATA STEMSYNTHESE (TTS)"
    }

    fun getSensorTtsDesc(lang: AppLanguage, motion: String, freq: String): String = when(lang) {
        AppLanguage.GERMAN -> "Generiert aus Live-Magnetfeld, Bewegung ($motion) & Frequenz ($freq kHz) gruselige Spirit-Box Phrasen."
        AppLanguage.ENGLISH -> "Generates creepy spirit box phrases from live magnetic field, motion ($motion) & frequency ($freq kHz)."
        AppLanguage.TURKISH -> "Canlı manyetik alan, hareket ($motion) ve frekanstan ($freq kHz) ürkütücü ruh kutusu ifadeleri üretir."
        AppLanguage.SPANISH -> "Genera frases espeluznantes desde el campo magnético, movimiento ($motion) y frecuencia ($freq kHz)."
        AppLanguage.FRENCH -> "Génère des phrases effrayantes à partir du champ magnétique, mouvement ($motion) et fréquence ($freq kHz)."
        AppLanguage.ITALIAN -> "Genera frasi spettrali dal campo magnetico, movimento ($motion) e frequenza ($freq kHz)."
        AppLanguage.POLISH -> "Generuje mroczne frazy z pola magnetycznego, ruchu ($motion) i częstotliwości ($freq kHz)."
        AppLanguage.DUTCH -> "Genereert griezelige zinnen uit magnetisch veld, beweging ($motion) & frequentie ($freq kHz)."
    }

    fun getGenerateSensorPhraseBtn(lang: AppLanguage, isGenerating: Boolean): String = when(lang) {
        AppLanguage.GERMAN -> if (isGenerating) "GENERIEREN & SPEICHERN..." else "SENSOR-PHRASE GENERIEREN & SPRECHEN"
        AppLanguage.ENGLISH -> if (isGenerating) "GENERATING & SAVING..." else "GENERATE & SPEAK SENSOR PHRASE"
        AppLanguage.TURKISH -> if (isGenerating) "ÜRETİLİYOR..." else "SENSÖR İFADESİ ÜRET VE KONUŞ"
        AppLanguage.SPANISH -> if (isGenerating) "GENERANDO..." else "GENERAR Y HABLAR FRASE DEL SENSOR"
        AppLanguage.FRENCH -> if (isGenerating) "GÉNÉRATION..." else "GÉNÉRER ET PARLER LA PHRASE CAPTEUR"
        AppLanguage.ITALIAN -> if (isGenerating) "GENERAZIONE IN CORSO..." else "GENERA E PRONUNCIA FRASE SENSORE"
        AppLanguage.POLISH -> if (isGenerating) "GENEROWANIE..." else "GENERUJ I WYPOWIEDZ FRAZĘ CZUJNIKA"
        AppLanguage.DUTCH -> if (isGenerating) "GENEREREN..." else "SENSORZIN GENEREREN & SPREKEN"
    }

    fun getQuestionPlaceholder(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Frage an das Wesen eingeben..."
        AppLanguage.ENGLISH -> "Enter question for the entity..."
        AppLanguage.TURKISH -> "Varlığa sorulacak soruyu girin..."
        AppLanguage.SPANISH -> "Ingrese pregunta para la entidad..."
        AppLanguage.FRENCH -> "Entrez une question pour l'entité..."
        AppLanguage.ITALIAN -> "Inserisci domanda per l'entità..."
        AppLanguage.POLISH -> "Wpisz pytanie do zjawiska..."
        AppLanguage.DUTCH -> "Voer vraag in voor de entiteit..."
    }

    fun getSendBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SENDEN"
        AppLanguage.ENGLISH -> "SEND"
        AppLanguage.TURKISH -> "GÖNDER"
        AppLanguage.SPANISH -> "ENVIAR"
        AppLanguage.FRENCH -> "ENVOYER"
        AppLanguage.ITALIAN -> "INVIA"
        AppLanguage.POLISH -> "WYŚLIJ"
        AppLanguage.DUTCH -> "VERSTUREN"
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
        AppLanguage.ITALIAN -> listOf(
            "Sei qui con noi nella stanza?",
            "Come ti chiami?",
            "Perché sei qui?",
            "Sei pacifico o pericoloso?"
        )
        AppLanguage.POLISH -> listOf(
            "Czy jesteś z nami w pokoju?",
            "Jak masz na imię?",
            "Dlaczego tu jesteś?",
            "Czy jesteś pokojowy czy niebezpieczny?"
        )
        AppLanguage.DUTCH -> listOf(
            "Ben je bij ons in de ruimte?",
            "Hoe heet je?",
            "Waarom ben je hier?",
            "Ben je vredig of gevaarlijk?"
        )
    }

    // History Screen
    fun getHistoryHeader(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "VERLAUF DER FUNDE"
        AppLanguage.ENGLISH -> "DETECTION HISTORY"
        AppLanguage.TURKISH -> "TESPİT GEÇMİŞİ"
        AppLanguage.SPANISH -> "HISTORIAL DE HALLAZGOS"
        AppLanguage.FRENCH -> "HISTORIQUE DES DÉTECTIONS"
        AppLanguage.ITALIAN -> "CRONOLOGIA RILEVAMENTI"
        AppLanguage.POLISH -> "HISTORIA WYKRYĆ"
        AppLanguage.DUTCH -> "GESCHIEDENIS VAN VONDSTEN"
    }

    fun getHistoryDbCount(lang: AppLanguage, count: Int): String = when(lang) {
        AppLanguage.GERMAN -> "ROOM DB PERSISTENT • $count GESPEICHERTE EREIGNISSE"
        AppLanguage.ENGLISH -> "ROOM DB PERSISTENT • $count SAVED EVENTS"
        AppLanguage.TURKISH -> "ROOM DB KALICI • $count KAYITLI OLAY"
        AppLanguage.SPANISH -> "ROOM DB PERSISTENTE • $count EVENTOS GUARDADOS"
        AppLanguage.FRENCH -> "ROOM DB PERSISTANT • $count ÉVÉNEMENTS ENREGISTRÉS"
        AppLanguage.ITALIAN -> "ROOM DB PERSISTENTE • $count EVENTI SALVATI"
        AppLanguage.POLISH -> "ROOM DB TRWAŁA • $count ZAPISANYCH ZDARZEŃ"
        AppLanguage.DUTCH -> "ROOM DB PERMANENT • $count OPGESLAGEN GEBEURTENISSEN"
    }

    fun getExportBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "EXPORT"
        AppLanguage.ENGLISH -> "EXPORT"
        AppLanguage.TURKISH -> "DIŞA AKTAR"
        AppLanguage.SPANISH -> "EXPORTAR"
        AppLanguage.FRENCH -> "EXPORTER"
        AppLanguage.ITALIAN -> "ESPORTA"
        AppLanguage.POLISH -> "EKSPORT"
        AppLanguage.DUTCH -> "EXPORTEER"
    }

    fun getSearchPlaceholder(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Geist-Namen, Ort oder Notiz suchen..."
        AppLanguage.ENGLISH -> "Search ghost name, location or note..."
        AppLanguage.TURKISH -> "Hayalet adı, konum veya not ara..."
        AppLanguage.SPANISH -> "Buscar nombre de fantasma, ubicación o nota..."
        AppLanguage.FRENCH -> "Rechercher nom de fantôme, lieu ou note..."
        AppLanguage.ITALIAN -> "Cerca nome fantasma, luogo o nota..."
        AppLanguage.POLISH -> "Szukaj nazwy ducha, miejsca lub notatki..."
        AppLanguage.DUTCH -> "Zoek spooknaam, locatie of notitie..."
    }

    fun getFavoritesFilter(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "NUR FAVORITEN"
        AppLanguage.ENGLISH -> "FAVORITES ONLY"
        AppLanguage.TURKISH -> "SADECE FAVORİLER"
        AppLanguage.SPANISH -> "SOLO FAVORITOS"
        AppLanguage.FRENCH -> "FAVORIS SEULEMENT"
        AppLanguage.ITALIAN -> "SOLO PREFERITI"
        AppLanguage.POLISH -> "TYLKO ULUBIONE"
        AppLanguage.DUTCH -> "ALLEEN FAVORIETEN"
    }

    fun getTypeFilters(lang: AppLanguage): List<String> = when(lang) {
        AppLanguage.GERMAN -> listOf("ALLE", "Poltergeist", "Phantom", "Schattenwesen", "Dämon", "Vampir", "Dimensionsriss", "Gefangen")
        AppLanguage.ENGLISH -> listOf("ALL", "Poltergeist", "Phantom", "Shadow Being", "Demon", "Vampire", "Dimensional Rift", "Captured")
        AppLanguage.TURKISH -> listOf("TÜMÜ", "Poltergeist", "Hayalet/Fantom", "Gölge Varlık", "İblis", "Vampir", "Boyut Yarığı", "Yakalanan")
        AppLanguage.SPANISH -> listOf("TODOS", "Poltergeist", "Fantasma", "Ser de Sombra", "Demonio", "Vampiro", "Grieta Dimensional", "Capturado")
        AppLanguage.FRENCH -> listOf("TOUS", "Poltergeist", "Fantôme", "Être d'Ombre", "Démon", "Vampire", "Faille Dimensionnelle", "Capturé")
        AppLanguage.ITALIAN -> listOf("TUTTI", "Poltergeist", "Fantasma", "Essere d'Ombra", "Demone", "Vampiro", "Fenditura Dimensionale", "Catturato")
        AppLanguage.POLISH -> listOf("WSZYSTKIE", "Poltergeist", "Fantom", "Istota Cienia", "Demon", "Wampir", "Wyrwa Wymiarowa", "Pojmany")
        AppLanguage.DUTCH -> listOf("ALLES", "Poltergeist", "Fantoom", "Schaduwwezen", "Demon", "Vampier", "Dimensiekloof", "Gevangen")
    }

    fun getExportDialogTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ROOM DB EXPORT"
        AppLanguage.ENGLISH -> "ROOM DB EXPORT"
        AppLanguage.TURKISH -> "ROOM DB DIŞA AKTARIM"
        AppLanguage.SPANISH -> "EXPORTAR ROOM DB"
        AppLanguage.FRENCH -> "EXPORTATION ROOM DB"
        AppLanguage.ITALIAN -> "ESPORTAZIONE ROOM DB"
        AppLanguage.POLISH -> "EKSPORT ROOM DB"
        AppLanguage.DUTCH -> "ROOM DB EXPORT"
    }

    fun getExportDialogSubtitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Lesbares Text-Protokoll der gespeicherten Geister-Ereignisse:"
        AppLanguage.ENGLISH -> "Readable text log of saved ghost events:"
        AppLanguage.TURKISH -> "Kayıtlı hayalet olaylarının okunabilir metin günlüğü:"
        AppLanguage.SPANISH -> "Registro de texto legible de eventos fantasma guardados:"
        AppLanguage.FRENCH -> "Journal texte lisible des événements fantômes enregistrés:"
        AppLanguage.ITALIAN -> "Registro di testo leggibile degli eventi fantasma salvati:"
        AppLanguage.POLISH -> "Czytelny protokół tekstowy zapisanych zdarzeń duchów:"
        AppLanguage.DUTCH -> "Leesbaar tekstprotocol van opgeslagen spookgebeurtenissen:"
    }

    fun getShareBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "TEILEN"
        AppLanguage.ENGLISH -> "SHARE"
        AppLanguage.TURKISH -> "PAYLAŞ"
        AppLanguage.SPANISH -> "COMPARTIR"
        AppLanguage.FRENCH -> "PARTAGER"
        AppLanguage.ITALIAN -> "CONDIVIDI"
        AppLanguage.POLISH -> "UDOSTĘPNIJ"
        AppLanguage.DUTCH -> "DELEN"
    }

    fun getCopyBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "KOPIEREN"
        AppLanguage.ENGLISH -> "COPY"
        AppLanguage.TURKISH -> "KOPYALA"
        AppLanguage.SPANISH -> "COPIAR"
        AppLanguage.FRENCH -> "COPIER"
        AppLanguage.ITALIAN -> "COPIA"
        AppLanguage.POLISH -> "KOPIUJ"
        AppLanguage.DUTCH -> "KOPIËREN"
    }

    fun getCloseBtn(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SCHLIESSEN"
        AppLanguage.ENGLISH -> "CLOSE"
        AppLanguage.TURKISH -> "KAPAT"
        AppLanguage.SPANISH -> "CERRAR"
        AppLanguage.FRENCH -> "FERMER"
        AppLanguage.ITALIAN -> "CHIUDI"
        AppLanguage.POLISH -> "ZAMKNIJ"
        AppLanguage.DUTCH -> "SLUITEN"
    }

    // Filter Settings Screen
    fun getSettingsHeader(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "INFRAROT FILTER & STEUERUNG"
        AppLanguage.ENGLISH -> "INFRARED FILTER & CONTROLS"
        AppLanguage.TURKISH -> "KIZILÖTESİ FİLTRE VE KONTROLLER"
        AppLanguage.SPANISH -> "FILTRO INFRARROJO Y CONTROLES"
        AppLanguage.FRENCH -> "FILTRE INFRAROUGE ET CONTRÔLES"
        AppLanguage.ITALIAN -> "FILTRO INFRAROSSI E CONTROLLI"
        AppLanguage.POLISH -> "FILTR PODCZERWIENI I STEROWANIE"
        AppLanguage.DUTCH -> "INFRAROOD FILTER & BESTURING"
    }

    fun getActiveFilterTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "AKTIVER SPECTRAL FILTER:"
        AppLanguage.ENGLISH -> "ACTIVE SPECTRAL FILTER:"
        AppLanguage.TURKISH -> "AKTİF SPEKTRAL FİLTRE:"
        AppLanguage.SPANISH -> "FILTRO ESPECTRAL ACTIVO:"
        AppLanguage.FRENCH -> "FILTRE SPECTRAL ACTIF:"
        AppLanguage.ITALIAN -> "FILTRO SPETTRALE ATTIVO:"
        AppLanguage.POLISH -> "AKTYWNY FILTR WIDMOWY:"
        AppLanguage.DUTCH -> "ACTIEF SPECTRAAL FILTER:"
    }

    fun getLanguageCardTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "SPRACHE DER BENUTZEROBERFLÄCHE (HAUPTSPRACHE: DEUTSCH)"
        AppLanguage.ENGLISH -> "INTERFACE LANGUAGE (PRIMARY: GERMAN)"
        AppLanguage.TURKISH -> "KULLANICI ARAYÜZÜ DİLİ (ANA DİL: ALMANCA)"
        AppLanguage.SPANISH -> "IDIOMA DE LA INTERFAZ (PRINCIPAL: ALEMÁN)"
        AppLanguage.FRENCH -> "LANGUE DE L'INTERFACE (PRINCIPALE: ALLEMAND)"
        AppLanguage.ITALIAN -> "LINGUA DELL'INTERFACCIA (PRINCIPALE: TEDESCO)"
        AppLanguage.POLISH -> "JĘZYK INTERFEJSU (GŁÓWNY: NIEMIECKI)"
        AppLanguage.DUTCH -> "TAAL VAN DE INTERFACE (HOOFDTAAL: DUITS)"
    }

    fun getDisplayOptionsTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ANZEIGE & AUDIO EINSTELLUNGEN"
        AppLanguage.ENGLISH -> "DISPLAY & AUDIO SETTINGS"
        AppLanguage.TURKISH -> "GÖRÜNTÜ VE SES AYARLARI"
        AppLanguage.SPANISH -> "AJUSTES DE PANTALLA Y AUDIO"
        AppLanguage.FRENCH -> "RÉGLAGES D'AFFICHAGE ET AUDIO"
        AppLanguage.ITALIAN -> "IMPOSTAZIONI DISPLAY E AUDIO"
        AppLanguage.POLISH -> "USTAWIENIA EKRANU I AUDIO"
        AppLanguage.DUTCH -> "INSTELLINGEN VOOR WEERGAVE EN AUDIO"
    }

    fun getCrtOverlayLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "CRT RÖHREN-SCANLINES OVERLAY"
        AppLanguage.ENGLISH -> "CRT SCANLINES OVERLAY"
        AppLanguage.TURKISH -> "CRT TARAMA ÇİZGİLERİ KATMANI"
        AppLanguage.SPANISH -> "SUPERPOSICIÓN DE LÍNEAS CRT"
        AppLanguage.FRENCH -> "SUPERPOSITION LIGNES CRT"
        AppLanguage.ITALIAN -> "SUPERPOSIZIONE LINEE CRT"
        AppLanguage.POLISH -> "NAKŁADKA LINII SCANLINE CRT"
        AppLanguage.DUTCH -> "CRT SCANLINES OVERLAY"
    }

    fun getCameraFeedLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "LIVE INFRA-GRÜN KAMERA-FEED"
        AppLanguage.ENGLISH -> "LIVE INFRA-GREEN CAMERA FEED"
        AppLanguage.TURKISH -> "CANLI KIZIL-YEŞİL KAMERA AKIŞI"
        AppLanguage.SPANISH -> "CANAL DE CÁMARA INFRARROJO EN VIVO"
        AppLanguage.FRENCH -> "FLUX CAMÉRA INFRAROUGE EN DIRECT"
        AppLanguage.ITALIAN -> "FLUSSO TELECAMERA INFRAROSSI IN TEMPO REALE"
        AppLanguage.POLISH -> "TRANSMISJA KAMERY W ZIELENI PODCZERWIENI"
        AppLanguage.DUTCH -> "LIVE INFRA-GROENE CAMERA-FEED"
    }

    fun getCameraFeedDesc(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "Echte Kamera im Hintergrund mit digitalem Nachtsicht-Filter"
        AppLanguage.ENGLISH -> "Use real camera background with digital night-vision filter"
        AppLanguage.TURKISH -> "Dijital gece görüş filtresi ile canlı kamera arka planı"
        AppLanguage.SPANISH -> "Usar cámara real en segundo plano con filtro de visión nocturna"
        AppLanguage.FRENCH -> "Utiliser une vraie caméra en arrière-plan avec filtre de vision nocturne"
        AppLanguage.ITALIAN -> "Usa telecamera reale in background con filtro di visione notturna"
        AppLanguage.POLISH -> "Użyj prawdziwej kamery w tle z cyfrowym filtrem noktowizyjnym"
        AppLanguage.DUTCH -> "Echte camera op achtergrond met digitaal nachtzichtfilter"
    }

    fun getAudioFeedbackLabel(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "AUDIO-FEEDBACK & FREQUENZ-SUMMEN"
        AppLanguage.ENGLISH -> "AUDIO FEEDBACK & FREQUENCY BUZZ"
        AppLanguage.TURKISH -> "SESLİ GERİ BİLDİRİM VE FREKANS SESİ"
        AppLanguage.SPANISH -> "RETROALIMENTACIÓN DE AUDIO Y ZUMBIDO"
        AppLanguage.FRENCH -> "RETOUR AUDIO ET BROMDONNEMENT"
        AppLanguage.ITALIAN -> "RETROAZIONE AUDIO E RONZIO FREQUENZA"
        AppLanguage.POLISH -> "SPRZĘŻENIE ZWROTNE AUDIO I BUCZENIE FREKWENCJI"
        AppLanguage.DUTCH -> "AUDIO-FEEDBACK & FREQUENTIE-GONZEN"
    }

    fun getClearDbTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "ALLE DB-EINTRÄGE LÖSCHEN"
        AppLanguage.ENGLISH -> "DELETE ALL DB ENTRIES"
        AppLanguage.TURKISH -> "TÜM DB KAYITLARINI SİL"
        AppLanguage.SPANISH -> "ELIMINAR TODOS LOS REGISTROS DB"
        AppLanguage.FRENCH -> "SUPPRIMER TOUTES LES ENTRÉES DB"
        AppLanguage.ITALIAN -> "ELIMINA TUTTE LE VOCI DEL DB"
        AppLanguage.POLISH -> "USUŃ WSZYSTKIE WPISY BAZY DANYCH"
        AppLanguage.DUTCH -> "ALLE DB-ENTRIES WISSEN"
    }

    fun getBatterySaverTitle(lang: AppLanguage): String = when(lang) {
        AppLanguage.GERMAN -> "BATTERIESPARMODUS (BILDSHIRM AUS)"
        AppLanguage.ENGLISH -> "BATTERY SAVER MODE (SCREEN OFF)"
        AppLanguage.TURKISH -> "PİL TASARRUF MODU (EKRAN KAPALI)"
        AppLanguage.SPANISH -> "MODO AHORRO BATERÍA (PANTALLA APAGADA)"
        AppLanguage.FRENCH -> "MODE ÉCONOMIE BATTERIE (ÉCRAN ÉTEINT)"
        AppLanguage.ITALIAN -> "RISPARMIO BATTERIA (SCHERMO SPENTO)"
        AppLanguage.POLISH -> "OSZCZĘDZANIE BATERII (EKRAN WYŁĄCZONY)"
        AppLanguage.DUTCH -> "BATTERIJSPAREN (SCHERM UIT)"
    }

    fun getBatterySaverDesc(lang: AppLanguage, isThrottling: Boolean): String = when(lang) {
        AppLanguage.GERMAN -> if (isThrottling) "AKTIV: Bildschirm aus – Sensor-Abtastung & Scans drosseln auf 4,0s Intervall." else "Reduziert Sensor-Abtastrate und verlangsamt Scans bei ausgeschaltetem Bildschirm (von 0,8s auf 4,0s)."
        AppLanguage.ENGLISH -> if (isThrottling) "ACTIVE: Screen off – Sensor sampling & scans throttled to 4.0s interval." else "Reduces sensor sampling rate and slows scans when screen is off (from 0.8s to 4.0s)."
        AppLanguage.TURKISH -> if (isThrottling) "AKTİF: Ekran kapalı – Sensör ve tarama hızı 4.0sn aralığa düşürüldü." else "Ekran kapalıyken sensör ve tarama hızını azaltır (0.8sn'den 4.0sn'ye)."
        AppLanguage.SPANISH -> if (isThrottling) "ACTIVO: Pantalla apagada – Muestreo y escaneos reducidos a intervalo 4.0s." else "Reduce tasa de sensores y ralentiza escaneos con la pantalla apagada (de 0.8s a 4.0s)."
        AppLanguage.FRENCH -> if (isThrottling) "ACTIF: Écran éteint – Échantillonnage et scans réduits à l'intervalle 4,0s." else "Réduit la fréquence des capteurs et ralentit les scans écran éteint (de 0,8s à 4,0s)."
        AppLanguage.ITALIAN -> if (isThrottling) "ATTIVO: Schermo spento – Sensori e scansione ridotti a intervallo 4,0s." else "Riduce frequenza sensori e rallenta la scansione a schermo spento (da 0,8s a 4,0s)."
        AppLanguage.POLISH -> if (isThrottling) "AKTYWNY: Ekran wyłączony – Próbkowanie i skanowanie ograniczone do 4,0s." else "Zmniejsza częstotliwość czujników i zwalnia skanowanie przy wyłączonym ekranie (z 0,8s do 4,0s)."
        AppLanguage.DUTCH -> if (isThrottling) "ACTIEF: Scherm uit – Sensoren & scans vertraagd naar 4,0s interval." else "Vermindert sensor-bemonstering en vertraagt scans bij uitgeschakeld scherm (van 0,8s naar 4,0s)."
    }
}
