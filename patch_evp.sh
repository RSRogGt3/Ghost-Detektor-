sed -i '532i \
            // EVP Recorder Widget\
            Card(\
                colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),\
                border = CardDefaults.outlinedCardBorder(enabled = true),\
                modifier = Modifier.fillMaxWidth().testTag("evp_recorder_card")\
            ) {\
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {\
                    Row(verticalAlignment = Alignment.CenterVertically) {\
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = InfraGreenPrimary)\
                        Spacer(modifier = Modifier.width(8.dp))\
                        Text("EVP-REKORDER", style = MaterialTheme.typography.titleSmall.copy(color = InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))\
                    }\
                    Text("Nimmt Umgebungsgeräusche auf und filtert verborgene Stimmen heraus (EVP).", style = MaterialTheme.typography.bodySmall.copy(color = InfraGreenTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.sp))\
                    if (showEvpResult) {\
                        Text("ERFASST: $evpResultPhrase", style = MaterialTheme.typography.bodyMedium.copy(color = com.example.ui.theme.AlertInfraRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))\
                    }\
                    Button(\
                        onClick = {\
                            if (isRecordingEvp) {\
                                isRecordingEvp = false\
                                showEvpResult = true\
                                viewModel.soundManager.playGhostFreedSound()\
                                viewModel.askSpirit("Hast du eine Nachricht hinterlassen?")\
                            } else {\
                                isRecordingEvp = true\
                                showEvpResult = false\
                            }\
                        },\
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRecordingEvp) com.example.ui.theme.AlertInfraRed else InfraGreenSurfaceVariant),\
                        modifier = Modifier.fillMaxWidth(),\
                        shape = RoundedCornerShape(6.dp)\
                    ) {\
                        Text(if (isRecordingEvp) "AUFNAHME STOPPEN" else "EVP-AUFNAHME STARTEN", color = if (isRecordingEvp) Color.White else InfraGreenPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)\
                    }\
                }\
            }' app/src/main/java/com/example/ui/screens/SpiritBoxScreen.kt
