import re

content = open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt').read()

background_logic = """
        // --- OFFLINE / BACKGROUND PROCESS LOGIC ---
        val lastLogin = rewardSharedPrefs.getLong("last_background_scan_time", System.currentTimeMillis())
        val currentTime = System.currentTimeMillis()
        val elapsedMinutes = (currentTime - lastLogin) / 60000L
        
        if (elapsedMinutes > 0 && rewardSharedPrefs.getBoolean("background_scan_enabled_persisted", true)) {
            val catches = (elapsedMinutes / 5).toInt().coerceAtMost(100) // Catch 1 every 5 mins, max 100
            if (catches > 0) {
                val coins = catches * getCoinBonusPerEntity()
                awardCoins(coins, "Offline Hintergrund-Scan ($catches gefunden)", playSound = false)
                
                viewModelScope.launch {
                    repeat(catches) {
                        repository.insertGhost(com.example.data.GhostDetectionEntity(
                            type = listOf("Schattenwesen", "Nebelgeist", "Poltergeist").random(),
                            dangerLevel = kotlin.random.Random.nextInt(1, 4),
                            emfSignature = 3.0f + kotlin.random.Random.nextFloat() * 2f,
                            timestamp = System.currentTimeMillis() - kotlin.random.Random.nextLong(0, elapsedMinutes * 60000L),
                            locationName = "Hintergrund-Scan",
                            notes = "Von der 24/7 Hintergrund-Erfassung gesammelt."
                        ))
                    }
                }
            }
        }
        
        viewModelScope.launch {
            while (true) {
                rewardSharedPrefs.edit().putLong("last_background_scan_time", System.currentTimeMillis()).apply()
                rewardSharedPrefs.edit().putBoolean("background_scan_enabled_persisted", _backgroundScan247Enabled.value).apply()
                delay(10000L) // Save state every 10 seconds
            }
        }
        // ------------------------------------------

        // Initial location fetch"""

content = content.replace("// Initial location fetch", background_logic)

open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt', 'w').write(content)
