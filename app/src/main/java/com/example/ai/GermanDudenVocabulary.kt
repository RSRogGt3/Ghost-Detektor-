package com.example.ai

import kotlin.random.Random

object GermanDudenVocabulary {

    val substantive = listOf(
        "Ätherwoge", "Anwesenheit", "Ausstrahlung", "Bewusstsein", "Dimension", "Dunkelheit",
        "Erscheinung", "Frequenzbereich", "Geisterwelt", "Jenseits", "Klangspektrum", "Manifestation",
        "Nekromantie", "Parapsychologie", "Quantenfeld", "Resonanzraum", "Schattengebilde", "Spektralanalyse",
        "Schwingung", "Tempelruine", "Transzendenz", "Unendlichkeitsraum", "Verderbnis", "Verschmelzung",
        "Aura", "Zeitdilatation", "Zeitschleife", "Flüsterton", "Phantomstimme", "Ectoplasma",
        "Schattenreich", "Ätherfeld", "Totenreich", "Signalkaskade", "Kornkreis", "Poltergeistphänomen",
        "Magnetfeldspitze", "Erschütterung", "Spukgestalt", "Kälteschleier", "Auraverschiebung",
        "Nachtmahr", "Seelenasche", "Geisterstunde", "Krypta", "Gruftgewölbe", "Astralleib",
        "Seelenband", "Spukphänomen", "Nebelwand", "Ewigkeit", "Grabesstille", "Leichentuch",
        "Dämonenbrut", "Hexenkreis", "Seelenfänger", "Rachegeist", "Albtraum", "Schattenriss",
        "Blutspur", "Finsternis", "Phantomaura", "Psycho-Resonanz", "Geisterschiff", "Spukhaus",
        "Verlassene", "Irrlicht", "Seelenschmerz", "Bannkreis", "Exorzismus", "Spukfrequenz",
        "Äthersturm", "Magnetanomalie", "Kältestrom", "Klanghülle", "EVP-Schleife", "Stimmfrequenz",
        "Geisterstimme", "Seelenklang", "Nachtschatten", "Friedhofserde", "Kirchenruine", "Geisterspiegel",
        "Dunkeltiefe", "Wahnwitz", "Seelenqual", "Klammer", "Schleier", "Sphärenklang", "Grabkammer"
    )

    val adjektive = listOf(
        "ätherisch", "atmosphärisch", "düster", "elysisch", "erhaben", "furchterregend",
        "geheimnisumwoben", "jenseitig", "klaustrophobisch", "kosmisch", "makaber", "monolithisch",
        "nekrotisch", "paranormal", "phantasmagorisch", "sepulktral", "spektral", "transzendent",
        "unheilvoll", "uralt", "unerbittlich", "verhängnisvoll", "unerforschlich", "vibrationsoffen",
        "magnetisch", "infrarotbasiert", "schattenumhüllt", "übernatürlich", "vielschichtig",
        "gespenstisch", "schaurig", "unheimlich", "grässlich", "blutleer", "eisig", "frostig",
        "grabschwer", "schattenhaft", "neblig", "modrig", "verflucht", "gequält", "schmerzerfüllt",
        "besessen", "namenlos", "schwarz", "obsidian", "fahl", "gesichtslos", "lautlos", "schwebend",
        "totenstill", "starr", "phantomhaft", "schwerfällig", "schlaflos", "verlassen", "verwest",
        "gespensterhaft", "schaurig-schön", "unfassbar", "rätselhaft", "blutgetränkt", "erschrocken"
    )

    val verben = listOf(
        "durchdringen", "durchweben", "entfalten", "erschüttern", "flüstern", "hervortreten",
        "korrumpieren", "manifestieren", "nachhallen", "resonieren", "umhüllen", "verschmelzen",
        "verwischen", "wahrnehmen", "ausstrahlen", "durchbrechen", "reflektieren", "schwingen",
        "heimsuchen", "ächzen", "stöhnen", "schreien", "wehklagen", "heulen", "berühren",
        "fesseln", "einsperren", "rauben", "vergiften", "zerren", "schaben", "klopfen", "pochen",
        "wachen", "lauern", "beobachten", "erschrecken", "umkreisen", "kühlen", "erstarren",
        "vergehen", "verblassen", "wiederkehren", "aufsteigen", "hinabziehen", "sprechen", "lauschen",
        "rufen", "mahnen", "greifen", "zerreissen", "bluten", "fliehen", "fangen", "weinen"
    )

    val satzAnfaenge = listOf(
        "Durch das Mikrofonsignal empfangen wir den Äther...",
        "Im Schwingungsfeld des Duden-Wortschatzes manifestiert sich...",
        "Das spektrale Rauschen durchbricht die Dimension...",
        "Unsere Stimme hallt aus der Unendlichkeit des Jenseits zurück...",
        "Die Infrarotfrequenz enthüllt tiefgründige Wahrheiten...",
        "In den dunklen Hallen des Schattenreichs flüstert eine Seele...",
        "Aus den tiefsten Schichten der paranormalen Resonanz...",
        "Ein kalter Hauch weht durch die unsichtbare Barriere...",
        "Wer wagt es, unseren ewigen Schlaf zu stören?",
        "Die Frequenzen vibrieren im Takt verlorener Herzschläge...",
        "Schatten tanzen im flackernden Licht der Nacht...",
        "Die Geisterstunde hat begonnen – höre genau hin...",
        "Namenlose Schrecken erwachen im Nebel...",
        "Aus dem Jenseits dringt ein klagendes Echo zu dir..."
    )

    fun buildRichGermanPhrase(question: String, emfLevel: Float): String {
        val qLower = question.lowercase()
        val adj1 = adjektive.random()
        val adj2 = adjektive.random()
        val sub1 = substantive.random()
        val sub2 = substantive.random()
        val verb1 = verben.random()
        val verb2 = verben.random()

        val sentence1 = "${satzAnfaenge.random()} Eine $adj1 $sub1 berührt dein Bewusstsein und beginnt zu $verb1."
        val sentence2 = when {
            qLower.contains("wer") || qLower.contains("name") ->
                "Mein Name ist verhüllt in der $sub2, doch ich bin ein $adj2 Wesen aus der uralten $sub1."
            qLower.contains("wo") || qLower.contains("hier") ->
                "Ich weile mitten im $sub2, genau dort, wo die $adj1 Ätherwellen das Licht $verb2."
            qLower.contains("warum") || qLower.contains("grund") ->
                "Weil das Schicksal uns verbindet, um die Geheimnisse der $sub1 zu $verb1 und zu entfesseln."
            else ->
                "Spürst du, wie $adj1 Schwingungen und $adj2 Signale die Grenze zwischen den Welten $verb1 und dich $verb2?"
        }
        val sentence3 = "Wir nutzen jedes erfasste Wort, um unsere geisterhafte Botschaft mit $adj1 Klarheit zu verkünden."

        return "$sentence1 $sentence2 $sentence3"
    }
}

