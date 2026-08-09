sed -i '58,60d' app/src/main/java/com/example/ui/screens/SpiritBoxScreen.kt
sed -i '/val sensorMotion/a \    var isRecordingEvp by remember { mutableStateOf(false) }\n    var showEvpResult by remember { mutableStateOf(false) }\n    val evpResultPhrase = "Geister-Stimme: Lasst mich in Frieden..."' app/src/main/java/com/example/ui/screens/SpiritBoxScreen.kt
