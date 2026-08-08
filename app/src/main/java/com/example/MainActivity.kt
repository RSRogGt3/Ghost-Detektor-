package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ui.components.AppSecurityLockOverlay
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.UiStrings
import com.example.ui.screens.FilterSettingsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SpiritBoxScreen
import com.example.ui.theme.GhostDetectorTheme
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenTextMuted
import com.example.ui.viewmodel.GhostViewModel

enum class GhostNavDestination(
    val icon: ImageVector,
    val testTag: String
) {
    SCANNER(Icons.Default.Radar, "nav_scanner"),
    SPIRIT_BOX(Icons.Default.RecordVoiceOver, "nav_spirit_box"),
    HISTORY(Icons.Default.History, "nav_history"),
    SETTINGS(Icons.Default.Tune, "nav_settings");

    fun getTitle(lang: AppLanguage): String = when (this) {
        SCANNER -> UiStrings.getNavScanner(lang)
        SPIRIT_BOX -> UiStrings.getNavSpiritBox(lang)
        HISTORY -> UiStrings.getNavHistory(lang)
        SETTINGS -> UiStrings.getNavSettings(lang)
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GhostDetectorTheme {
                GhostAppMainContent()
            }
        }
    }
}

@Composable
fun GhostAppMainContent(
    viewModel: GhostViewModel = viewModel()
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()

    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val isSecurityEnabled by viewModel.isSecurityEnabled.collectAsStateWithLifecycle()
    val isPinSetupDone by viewModel.isPinSetupDone.collectAsStateWithLifecycle()
    val recoveryEmail by viewModel.recoveryEmail.collectAsStateWithLifecycle()
    val autoLockOnBackground by viewModel.autoLockOnBackground.collectAsStateWithLifecycle()
    val lockoutTimerSeconds by viewModel.lockoutTimerSeconds.collectAsStateWithLifecycle()
    val failedPinAttempts by viewModel.failedPinAttempts.collectAsStateWithLifecycle()

    val context = androidx.compose.ui.platform.LocalContext.current

    var currentDestination by remember { mutableStateOf(GhostNavDestination.SCANNER) }

    // Auto-lock when app moves to background
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                if (autoLockOnBackground && isSecurityEnabled) {
                    viewModel.lockApp()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Handle result if needed
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!isFullscreen && (!isAppLocked || !isSecurityEnabled)) {
                NavigationBar(
                    containerColor = InfraGreenSurface,
                    contentColor = InfraGreenPrimary,
                    tonalElevation = androidx.compose.ui.unit.Dp.Unspecified
                ) {
                    GhostNavDestination.values().forEach { dest ->
                        val isSelected = dest == currentDestination
                        val title = dest.getTitle(appLanguage)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = dest },
                            icon = {
                                Icon(
                                    imageVector = dest.icon,
                                    contentDescription = title
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = InfraGreenPrimary,
                                indicatorColor = InfraGreenPrimary,
                                unselectedIconColor = InfraGreenTextMuted,
                                unselectedTextColor = InfraGreenTextMuted
                            ),
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullscreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
                .background(Color.Black)
        ) {
            // Main Active Screen Destination
            when (currentDestination) {
                GhostNavDestination.SCANNER -> ScannerScreen(viewModel = viewModel)
                GhostNavDestination.SPIRIT_BOX -> SpiritBoxScreen(viewModel = viewModel)
                GhostNavDestination.HISTORY -> HistoryScreen(viewModel = viewModel)
                GhostNavDestination.SETTINGS -> FilterSettingsScreen(viewModel = viewModel)
            }

            // Floating Quick Action Controls (Lock & Fullscreen Toggle)
            if (!isAppLocked || !isSecurityEnabled) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 8.dp, end = 12.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    // Quick App Lock Button
                    if (isSecurityEnabled) {
                        Surface(
                            color = Color(0xCC1A0000),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.AlertInfraRed),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.lockApp() }
                                .testTag("quick_lock_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "App sperren",
                                    tint = com.example.ui.theme.AlertInfraRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SPERREN",
                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                        color = com.example.ui.theme.AlertInfraRed,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Fullscreen Toggle Button
                    Surface(
                        color = if (isFullscreen) Color(0xDD00FFCC) else Color(0xCC09120C),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenPrimary),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.toggleFullscreen() }
                            .testTag("toggle_fullscreen_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullscreen) "Vollbild beenden" else "Vollbildanzeige",
                                tint = if (isFullscreen) Color.Black else InfraGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFullscreen) "VOLLBILD BEENDEN" else "VOLLBILD",
                                style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                    color = if (isFullscreen) Color.Black else InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Top-Level Fullscreen Security Lock Overlay (Access Protection)
            AppSecurityLockOverlay(
                isLocked = isAppLocked && isSecurityEnabled,
                isPinSetupDone = isPinSetupDone,
                lockoutTimerSeconds = lockoutTimerSeconds,
                failedAttempts = failedPinAttempts,
                recoveryEmail = recoveryEmail,
                onUnlock = { enteredPin -> viewModel.unlockApp(enteredPin) },
                onCreateNewPin = { newPin -> viewModel.createNewPin(newPin) },
                onSendRecoveryCode = { viewModel.generateAndSendRecoveryCode(context) },
                onVerifyRecoveryCode = { code -> viewModel.verifyRecoveryCodeAndResetPin(code) },
                onDirectResetPin = { viewModel.resetPinSetup() }
            )
        }
    }
}
