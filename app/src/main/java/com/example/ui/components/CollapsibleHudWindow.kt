package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InfraGreenBorder
import com.example.ui.theme.InfraGreenPrimary
import com.example.ui.theme.InfraGreenSurface
import com.example.ui.theme.InfraGreenSurfaceVariant
import com.example.ui.theme.InfraGreenTextPrimary
import com.example.ui.theme.InfraGreenTextPrimaryVariant

/**
 * Taktisches, minimierbares, maximierbares und scrollbares HUD-Fenster.
 * Unterstützt:
 * - Minimieren / Einklappen (Kompakte Titelleiste)
 * - Maximieren / Ausklappen (Voller Inhalt)
 * - Vollbild / Höhen-Maximierung (Erweitertes Scrollfenster)
 * - Scrollbarer Inhaltsbereich mit Höhenbegrenzung
 * - Globale Steuerung über forceExpandedState
 */
@Composable
fun CollapsibleHudWindow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    badgeText: String? = null,
    accentColor: Color = InfraGreenPrimary,
    initialExpanded: Boolean = true,
    forceExpandedState: Boolean? = null,
    isScrollable: Boolean = true,
    defaultMaxHeight: Dp = 380.dp,
    expandedMaxHeight: Dp = 620.dp,
    scrollState: ScrollState = rememberScrollState(),
    testTag: String = "hud_window",
    trailingHeaderActions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }
    var isMaximizedSize by remember { mutableStateOf(false) }

    LaunchedEffect(forceExpandedState) {
        forceExpandedState?.let { isExpanded = it }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
        border = BorderStroke(1.dp, if (isExpanded) accentColor.copy(alpha = 0.8f) else InfraGreenBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(280, easing = FastOutSlowInEasing))
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Taktische Fenster-Kopfzeile (Klickbar für schnelles Minimieren/Maximieren)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(if (isExpanded) accentColor.copy(alpha = 0.12f) else InfraGreenSurfaceVariant)
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Linker Bereich: Icon & Fenstertitel
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = if (isExpanded) accentColor else InfraGreenTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        maxLines = 1
                    )

                    if (!badgeText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = accentColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                            )
                        }
                    }
                }

                // Rechter Bereich: Fenster-Steuerungselemente (Minimieren, Maximieren, Scroll-Indikator)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    trailingHeaderActions?.invoke()

                    // Status Badge (MIN / MAX)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isExpanded) accentColor.copy(alpha = 0.18f) else Color.DarkGray.copy(alpha = 0.35f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isExpanded) {
                                if (isMaximizedSize) "MAXIMIERT" else "OFFEN"
                            } else "MINIMIERT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isExpanded) accentColor else Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    // Button: Fenster-Höhe voll maximieren (nur wenn offen und scrollbar)
                    if (isExpanded && isScrollable) {
                        IconButton(
                            onClick = { isMaximizedSize = !isMaximizedSize },
                            modifier = Modifier.size(32.dp).testTag("${testTag}_size_toggle")
                        ) {
                            Icon(
                                imageVector = if (isMaximizedSize) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isMaximizedSize) "Normalgröße" else "Höhe maximieren",
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Button: Fenster Minimieren / Ausklappen Toggle
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp).testTag("${testTag}_expand_toggle")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Fenster minimieren" else "Fenster ausklappen",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Fenster-Inhaltsbereich (Scrollbar und animiert ein-/ausklappbar)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(280)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(240)) + fadeOut()
            ) {
                val appliedMaxHeight = if (isMaximizedSize) expandedMaxHeight else defaultMaxHeight

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (isScrollable) {
                        // Scrollbarer Inhalt mit maximaler Höhe
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = appliedMaxHeight)
                                .verticalScroll(scrollState)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                content()
                            }
                        }

                        // Dezente Scroll-Hilfe-Leiste am unteren Rand
                        if (scrollState.maxValue > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(InfraGreenSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SCROLLBAR [${((scrollState.value.toFloat() / scrollState.maxValue) * 100).toInt()}%]",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = InfraGreenTextPrimaryVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.5.sp
                                    )
                                )
                                Text(
                                    text = if (isMaximizedSize) "TIPPEN ZUM VERKLEINERN" else "VOLLBILD FÜR MEHR PLATZ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accentColor.copy(alpha = 0.8f),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.5.sp
                                    )
                                )
                            }
                        }
                    } else {
                        // Fester Inhalt ohne Begrenzung
                        content()
                    }
                }
            }
        }
    }
}
