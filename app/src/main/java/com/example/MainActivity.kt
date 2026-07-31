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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    var currentDestination by remember { mutableStateOf(GhostNavDestination.SCANNER) }

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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            when (currentDestination) {
                GhostNavDestination.SCANNER -> ScannerScreen(viewModel = viewModel)
                GhostNavDestination.SPIRIT_BOX -> SpiritBoxScreen(viewModel = viewModel)
                GhostNavDestination.HISTORY -> HistoryScreen(viewModel = viewModel)
                GhostNavDestination.SETTINGS -> FilterSettingsScreen(viewModel = viewModel)
            }
        }
    }
}
