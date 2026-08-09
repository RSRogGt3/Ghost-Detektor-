echo '        }' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
echo '    }' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
echo '}' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
sed -i '/\/\/ Spectral Filter Intensity Card/d' app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
sed -i '/Card(/d' app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
