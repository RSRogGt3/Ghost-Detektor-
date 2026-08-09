package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

import com.example.ui.viewmodel.GhostViewModel

@Composable
fun GhostDetectorTheme(
    viewModel: GhostViewModel? = null,
    content: @Composable () -> Unit
) {
    val themeColor by (viewModel?.appThemeColor?.collectAsState() ?: androidx.compose.runtime.mutableStateOf("GREEN"))

    val primaryColor = when (themeColor) {
        "RED" -> Color(0xFFFF2244)
        "CYAN" -> Color(0xFF00E5FF)
        "PURPLE" -> Color(0xFFBB33FF)
        else -> Color(0xFF00FF66)
    }
    
    // Update color variables
    val dynamicColorScheme = darkColorScheme(
        primary = primaryColor,
        onPrimary = Color.Black,
        primaryContainer = InfraGreenSurfaceVariant,
        onPrimaryContainer = InfraGreenTextPrimary,
        secondary = InfraGreenSecondary,
        onSecondary = Color.Black,
        secondaryContainer = InfraGreenSurface,
        onSecondaryContainer = InfraGreenTertiary,
        tertiary = InfraGreenTertiary,
        onTertiary = Color.Black,
        background = InfraGreenBackground,
        onBackground = InfraGreenTextPrimary,
        surface = InfraGreenSurface,
        onSurface = InfraGreenTextPrimary,
        surfaceVariant = InfraGreenSurfaceVariant,
        onSurfaceVariant = InfraGreenTextMuted,
        outline = InfraGreenBorder,
        outlineVariant = InfraGreenBorder.copy(alpha = 0.5f),
        error = AlertInfraRed,
        onError = Color.White
    )

    MaterialTheme(
        colorScheme = dynamicColorScheme,
        typography = Typography,
        content = content
    )
}
