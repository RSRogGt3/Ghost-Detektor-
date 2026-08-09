package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertInfraRed
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextPrimaryVariant
import com.example.ui.theme.InfraGreenTextPrimary

@Composable
fun AppSecurityLockOverlay(
    isLocked: Boolean,
    isPinSetupDone: Boolean,
    lockoutTimerSeconds: Int,
    failedAttempts: Int,
    recoveryEmail: String,
    onUnlock: (String) -> Boolean,
    onCreateNewPin: (String) -> Boolean,
    onSendRecoveryCode: () -> String,
    onVerifyRecoveryCode: (String) -> Boolean,
    onDirectResetPin: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isLocked) return

    var enteredPin by remember { mutableStateOf("") }
    var firstEnteredPin by remember { mutableStateOf("") }
    var isConfirmingPin by remember { mutableStateOf(false) }
    var isErrorState by remember { mutableStateOf(false) }
    var customErrorMessage by remember { mutableStateOf("") }

    var showEmailResetDialog by remember { mutableStateOf(false) }
    var recoveryCodeInput by remember { mutableStateOf("") }
    var generatedCodeHint by remember { mutableStateOf("") }
    var emailDialogMessage by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_security")
    val shieldScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050B08))
            .padding(24.dp)
            .testTag("app_security_lock_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Security Shield Icon
            Box(
                modifier = Modifier
                    .scale(shieldScale)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (isErrorState || lockoutTimerSeconds > 0) AlertInfraRed.copy(alpha = 0.2f) else InfraGreenPrimary.copy(alpha = 0.15f))
                    .border(2.dp, if (isErrorState || lockoutTimerSeconds > 0) AlertInfraRed else InfraGreenPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isErrorState || lockoutTimerSeconds > 0) Icons.Default.Warning else Icons.Default.Lock,
                    contentDescription = "Sicherheits-Schloss",
                    tint = if (isErrorState || lockoutTimerSeconds > 0) AlertInfraRed else InfraGreenPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Lock Title
            Text(
                text = if (!isPinSetupDone) "INITIALE PIN ERSTELLEN" else "ZUGRIFFSSCHUTZ AKTIV",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = if (isErrorState || lockoutTimerSeconds > 0) AlertInfraRed else InfraGreenPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 2.sp
                )
            )

            Text(
                text = if (!isPinSetupDone) "Wählen Sie Ihre persönliche 4-stellige PIN" else "Nur für den Inhaber geschützt",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = InfraGreenTextPrimaryVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Status message / Lockout warning / Setup instructions
            Surface(
                color = if (lockoutTimerSeconds > 0 || isErrorState) Color(0x33FF3333) else InfraGreenSurfaceVariant,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (lockoutTimerSeconds > 0 || isErrorState) AlertInfraRed else InfraGreenBorder
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(
                    text = when {
                        lockoutTimerSeconds > 0 -> "⚠️ TEMPORÄRE SPERRE!\nWarten Sie ${lockoutTimerSeconds}s vor neuem Versuch."
                        isErrorState -> customErrorMessage.ifEmpty { "⚠️ FALSCHE PIN! ($failedAttempts. Fehlversuch)" }
                        !isPinSetupDone && isConfirmingPin -> "🔑 PIN BESTÄTIGEN:\nGeben Sie die 4 Zahlen erneut ein."
                        !isPinSetupDone -> "🔑 NEUE PIN WAHL:\nGeben Sie eine 4-stellige Zahlen-PIN ein."
                        else -> "Geben Sie Ihre 4-stellige PIN ein"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (lockoutTimerSeconds > 0 || isErrorState) AlertInfraRed else InfraGreenTextPrimary,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4 PIN Dot Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = enteredPin.length > i
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isErrorState -> AlertInfraRed
                                    isFilled -> InfraGreenPrimary
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                2.dp,
                                if (isErrorState) AlertInfraRed else InfraGreenPrimary,
                                CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Keypad Grid (3x4)
            val keypadNumbers = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "⌫")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                keypadNumbers.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            KeypadButton(
                                text = key,
                                enabled = lockoutTimerSeconds == 0,
                                onClick = {
                                    if (lockoutTimerSeconds > 0) return@KeypadButton

                                    when (key) {
                                        "C" -> {
                                            enteredPin = ""
                                            isErrorState = false
                                            customErrorMessage = ""
                                            if (!isPinSetupDone) {
                                                firstEnteredPin = ""
                                                isConfirmingPin = false
                                            }
                                        }
                                        "⌫" -> {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                                isErrorState = false
                                                customErrorMessage = ""
                                            }
                                        }
                                        else -> {
                                            if (enteredPin.length < 4) {
                                                enteredPin += key
                                                isErrorState = false
                                                customErrorMessage = ""

                                                if (enteredPin.length == 4) {
                                                    if (!isPinSetupDone) {
                                                        // First-time setup flow
                                                        if (!isConfirmingPin) {
                                                            firstEnteredPin = enteredPin
                                                            enteredPin = ""
                                                            isConfirmingPin = true
                                                        } else {
                                                            if (enteredPin == firstEnteredPin) {
                                                                onCreateNewPin(enteredPin)
                                                            } else {
                                                                isErrorState = true
                                                                customErrorMessage = "⚠️ PINs stimmen nicht überein!"
                                                                enteredPin = ""
                                                                firstEnteredPin = ""
                                                                isConfirmingPin = false
                                                            }
                                                        }
                                                    } else {
                                                        // Standard unlock flow
                                                        val success = onUnlock(enteredPin)
                                                        if (!success) {
                                                            isErrorState = true
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Forgot PIN / Email Reset Button
            if (isPinSetupDone) {
                Button(
                    onClick = {
                        recoveryCodeInput = ""
                        generatedCodeHint = ""
                        emailDialogMessage = ""
                        showEmailResetDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InfraGreenSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("forgot_pin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = InfraGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PIN VERGESSEN? PER E-MAIL ZURÜCKSETZEN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InfraGreenPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }

    // Email Reset Verification Dialog
    if (showEmailResetDialog) {
        AlertDialog(
            onDismissRequest = { showEmailResetDialog = false },
            confirmButton = {},
            dismissButton = {},
            containerColor = InfraGreenSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, InfraGreenBorder, RoundedCornerShape(12.dp)),
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = InfraGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PIN-WIEDERHERSTELLUNG",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = InfraGreenPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = "Senden Sie einen 6-stelligen Wiederherstellungscode an Ihre hinterlegte E-Mail:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InfraGreenTextPrimaryVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    Surface(
                        color = InfraGreenSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "✉️ $recoveryEmail",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val code = onSendRecoveryCode()
                            generatedCodeHint = code
                            emailDialogMessage = "✅ Code wurde generiert & E-Mail geöffnet!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("send_email_code_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🔑 CODE AN E-MAIL SENDEN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    if (generatedCodeHint.isNotEmpty()) {
                        Surface(
                            color = Color(0x3300FFCC),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenPrimary)
                        ) {
                            Text(
                                text = "Sicherheitscode: $generatedCodeHint",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(8.dp).fillMaxWidth()
                            )
                        }
                    }

                    OutlinedTextField(
                        value = recoveryCodeInput,
                        onValueChange = { if (it.length <= 6) recoveryCodeInput = it },
                        label = { Text("6-stelligen Code eingeben", color = InfraGreenTextPrimaryVariant, fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InfraGreenPrimary,
                            unfocusedBorderColor = InfraGreenBorder,
                            focusedTextColor = InfraGreenTextPrimary,
                            unfocusedTextColor = InfraGreenTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("recovery_code_input")
                    )

                    if (emailDialogMessage.isNotEmpty()) {
                        Text(
                            text = emailDialogMessage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (emailDialogMessage.startsWith("✅")) InfraGreenPrimary else AlertInfraRed,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                onDirectResetPin()
                                showEmailResetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertInfraRed.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AlertInfraRed)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = AlertInfraRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DIREKT ZURÜCKSETZEN", color = AlertInfraRed, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                        }

                        Button(
                            onClick = {
                                val success = onVerifyRecoveryCode(recoveryCodeInput)
                                if (success) {
                                    showEmailResetDialog = false
                                } else {
                                    emailDialogMessage = "❌ Falscher Wiederherstellungscode!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                        ) {
                            Text("PRÜFEN", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun KeypadButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (enabled) InfraGreenSurface else Color(0xFF101813),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) InfraGreenBorder else Color(0xFF1B2B20)
        ),
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .testTag("keypad_btn_$text")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (text == "⌫") {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Löschen",
                    tint = if (enabled) InfraGreenPrimary else InfraGreenTextPrimaryVariant,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (text == "C") AlertInfraRed else if (enabled) InfraGreenPrimary else InfraGreenTextPrimaryVariant,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}
