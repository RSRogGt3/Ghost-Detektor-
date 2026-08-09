sed -i '504,528d' app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
sed -i '/\/\/ Language Selection Card/i \            Card(\
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),\
                border = CardDefaults.outlinedCardBorder(enabled = true),\
                modifier = Modifier.fillMaxWidth()\
            ) {\
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {\
                    Row(verticalAlignment = Alignment.CenterVertically) {\
                        Icon(Icons.Default.Palette, contentDescription = null, tint = InfraGreenPrimary)\
                        Spacer(modifier = Modifier.width(8.dp))\
                        Text("THEME-FARBE", style = MaterialTheme.typography.titleMedium.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))\
                    }\
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {\
                        val colors = listOf("GREEN" to Color(0xFF00FF66), "RED" to Color(0xFFFF2244), "CYAN" to Color(0xFF00E5FF), "PURPLE" to Color(0xFFBB33FF))\
                        colors.forEach { (name, colorValue) ->\
                            val isSelected = appThemeColor == name\
                            Box(\
                                modifier = Modifier\
                                    .size(40.dp)\
                                    .clip(RoundedCornerShape(20.dp))\
                                    .background(colorValue)\
                                    .border(2.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(20.dp))\
                                    .clickable { viewModel.setAppThemeColor(name) }\
                            )\
                        }\
                    }\
                }\
            }\n' app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt
