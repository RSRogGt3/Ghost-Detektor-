import re

content = open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt').read()

old = """                        repository.insertGhost(com.example.data.GhostDetectionEntity(
                            type = listOf("Schattenwesen", "Nebelgeist", "Poltergeist").random(),
                            dangerLevel = kotlin.random.Random.nextInt(1, 4),
                            emfSignature = 3.0f + kotlin.random.Random.nextFloat() * 2f,
                            timestamp = System.currentTimeMillis() - kotlin.random.Random.nextLong(0, elapsedMinutes * 60000L),
                            locationName = "Hintergrund-Scan",
                            notes = "Von der 24/7 Hintergrund-Erfassung gesammelt."
                        ))"""
new = """                        repository.insertGhost(com.example.data.GhostDetectionEntity(
                            name = "Unbekannte Entität",
                            type = listOf("Schattenwesen", "Nebelgeist", "Poltergeist").random(),
                            emfLevel = 3.0f + kotlin.random.Random.nextFloat() * 2f,
                            frequencyKhz = 42.5f,
                            dangerLevel = kotlin.random.Random.nextInt(1, 4),
                            timestamp = System.currentTimeMillis() - kotlin.random.Random.nextLong(0, elapsedMinutes * 60000L),
                            locationName = "Hintergrund-Scan",
                            notes = "Von der 24/7 Hintergrund-Erfassung gesammelt."
                        ))"""

content = content.replace(old, new)
open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt', 'w').write(content)
