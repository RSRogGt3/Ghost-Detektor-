echo '        }' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
echo '    }' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
echo '}' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
sed -i '/\/\/ HUD Overlays & Audio Toggles/d' app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
