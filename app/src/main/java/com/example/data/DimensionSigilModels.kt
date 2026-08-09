package com.example.data

enum class DimensionPlane(
    val id: String,
    val title: String,
    val frequencyHz: Int,
    val codeName: String,
    val colorHex: Long,
    val description: String,
    val threatMultiplier: Float
) {
    MORTAL_PRIME(
        id = "dim_0",
        title = "Diesseits (Physikalische Realität)",
        frequencyHz = 1000,
        codeName = "DIM-01 / PRIME",
        colorHex = 0xFF00FFCC,
        description = "Standard-Menschliche Ebene. Ausgewogene elektromagnetische Schwingung.",
        threatMultiplier = 1.0f
    ),
    ETHERIAL_DRIFT(
        id = "dim_1",
        title = "Ätherische Zwischenwelt",
        frequencyHz = 432,
        codeName = "DIM-02 / ETHER",
        colorHex = 0xFF00E5FF,
        description = "Spiegelwelt ruheloser Phantome & spektraler Echos.",
        threatMultiplier = 1.3f
    ),
    INFERNUS_VOID(
        id = "dim_2",
        title = "Infernale Abgrund-Dimension",
        frequencyHz = 666,
        codeName = "DIM-03 / VOID",
        colorHex = 0xFFFF0055,
        description = "Ursprung aggressiver Dämonen & Vampir-Entitäten. Hohe EMF-Spikes.",
        threatMultiplier = 2.2f
    ),
    LIMBUS_ECLIPSE(
        id = "dim_3",
        title = "Limbus-Schattenreich",
        frequencyHz = 888,
        codeName = "DIM-04 / LIMBUS",
        colorHex = 0xFFAA00FF,
        description = "Verlassene Raum-Zeit-Schleife. Eiskalte Schattengestalten.",
        threatMultiplier = 1.6f
    ),
    QUANTUM_SINGULARITY(
        id = "dim_4",
        title = "Quanten-Singularitäts-Riss",
        frequencyHz = 999,
        codeName = "DIM-05 / RIFT",
        colorHex = 0xFFFFCC00,
        description = "Instabiles Wurmloch. Erzeugt spontane interdimensionale Portale.",
        threatMultiplier = 2.5f
    )
}

enum class SigilType(
    val id: String,
    val title: String,
    val symbol: String,
    val purpose: String,
    val colorHex: Long,
    val durationSeconds: Int,
    val description: String
) {
    DEMON_BANISHING(
        id = "sigil_demon",
        title = "Dämonen-Bannsiegel (Solomon)",
        symbol = "✡️",
        purpose = "Bannt Dämonen & Vampire",
        colorHex = 0xFFFF0055,
        durationSeconds = 60,
        description = "Zwingt aggressive Wesen direkt in den Spektral-Käfig und neutralisiert böse Auren."
    ),
    DIMENSION_ANCHOR(
        id = "sigil_anchor",
        title = "Dimensions-Anker (Alpha)",
        symbol = "🌀",
        purpose = "Versiegelt Portal-Risse",
        colorHex = 0xFF00E5FF,
        durationSeconds = 90,
        description = "Verschließt alle offenen Raumzeit-Spalten und stabilisiert das lokale Portalgefüge."
    ),
    ARCHANGEL_SHIELD(
        id = "sigil_shield",
        title = "Erzengel Schutz-Siegel (Aegis)",
        symbol = "🛡️",
        purpose = "Dämpft EMF-Spikes & Angriffe",
        colorHex = 0xFF00FFCC,
        durationSeconds = 120,
        description = "Errichtet ein geschütztes Machtfeld. Verhindert Bedrohungs-Invasionen."
    ),
    LIGHT_HARMONY(
        id = "sigil_light",
        title = "Licht-Harmonie Siegel",
        symbol = "🔮",
        purpose = "Befreit Geister ins Licht",
        colorHex = 0xFFFF00FF,
        durationSeconds = 45,
        description = "Aktiviert reines Licht-Pulse. Löst verlorene Geister sanft aus der Erde."
    ),
    QUANTUM_STABILIZER(
        id = "sigil_quantum",
        title = "Quanten-Frequenz-Siegel (999Hz)",
        symbol = "⚛️",
        purpose = "Harmonisiert Dimensionen",
        colorHex = 0xFFFFCC00,
        durationSeconds = 150,
        description = "Stoppt Frequenzstörungen und gleicht interdimensionale Schwankungen aus."
    )
}
