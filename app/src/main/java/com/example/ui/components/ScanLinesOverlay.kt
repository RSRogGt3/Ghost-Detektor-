package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.InfraGreenPrimary

@Composable
fun ScanLinesOverlay(
    modifier: Modifier = Modifier,
    lineColor: Color = InfraGreenPrimary.copy(alpha = 0.08f),
    lineSpacingPx: Float = 8f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f
            )
            y += lineSpacingPx
        }
    }
}
