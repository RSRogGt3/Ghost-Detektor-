import re
content = open('app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt').read()

permission_card = """
            // Permissions Card
            val context = LocalContext.current
            var hasGpsPermission by remember { mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) }
            var hasNotifPermission by remember { mutableStateOf(
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                } else true
            ) }

            val gpsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                hasGpsPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            }

            val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                hasNotifPermission = isGranted
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                border = CardDefaults.outlinedCardBorder(enabled = true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = InfraGreenPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("System-Berechtigungen", color = InfraGreenTextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (hasGpsPermission) "GPS (Maps): Aktiv" else "GPS (Maps): Fehlt",
                            color = if (hasGpsPermission) InfraGreenTextPrimary else AlertInfraRed
                        )
                        Button(
                            onClick = { gpsLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
                            enabled = !hasGpsPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                        ) {
                            Text("Freigeben", color = Color.Black)
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (hasNotifPermission) "Benachrichtigungen: Aktiv" else "Benachrichtigungen: Fehlt",
                                color = if (hasNotifPermission) InfraGreenTextPrimary else AlertInfraRed
                            )
                            Button(
                                onClick = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                enabled = !hasNotifPermission,
                                colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                            ) {
                                Text("Freigeben", color = Color.Black)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
"""

content = content.replace("            // Security PIN / Lock Settings Card", permission_card + "            // Security PIN / Lock Settings Card")

open('app/src/main/java/com/example/ui/screens/FilterSettingsScreen.kt', 'w').write(content)
