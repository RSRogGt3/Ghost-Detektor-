echo '        }' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
echo '    }' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
echo '}' >> app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
sed -i '/\/\/ Text-to-Speech Engine Controls/d' app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
