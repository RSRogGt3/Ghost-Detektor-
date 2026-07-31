package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val InfraGreenColorScheme = darkColorScheme(
    primary = InfraGreenPrimary,
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

@Composable
fun GhostDetectorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = InfraGreenColorScheme,
        typography = Typography,
        content = content
    )
}
