package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GhostToastNotification
import com.example.data.ToastNotificationType

@Composable
fun GhostHudToastSnackbar(
    notification: GhostToastNotification?,
    onDismiss: () -> Unit,
    onActionClick: ((String?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(350)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(250)),
        modifier = modifier
    ) {
        if (notification != null) {
            val infiniteTransition = rememberInfiniteTransition(label = "ToastGlowTransition")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "ToastPulseAlpha"
            )

            val badgeColor = notification.badgeColor
            val isGold = notification.type == ToastNotificationType.COIN_EARNED || notification.type == ToastNotificationType.DAILY_BONUS

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xEE0D120E)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.5.dp,
                    color = badgeColor.copy(alpha = pulseAlpha)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp), ambientColor = badgeColor, spotColor = badgeColor)
                    .testTag("ghost_hud_toast")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    badgeColor.copy(alpha = 0.22f),
                                    Color(0xEE0B150F),
                                    Color(0xEE090D0A)
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Icon with Glowing Ring
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(badgeColor.copy(alpha = 0.20f))
                                .border(1.dp, badgeColor.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = notification.iconEmoji,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Title and Description Column
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Category Pill
                                val categoryTag = when (notification.type) {
                                    ToastNotificationType.COIN_EARNED -> "COINS"
                                    ToastNotificationType.MILESTONE_UNLOCKED -> "ERFOLG ERREICHT"
                                    ToastNotificationType.MILESTONE_CLAIMED -> "BELOHNUNG"
                                    ToastNotificationType.MILESTONE_PROGRESS -> "FORTSCHRITT"
                                    ToastNotificationType.UPGRADE_PURCHASED -> "UPGRADE"
                                    ToastNotificationType.DAILY_BONUS -> "TÄGLICHER BONUS"
                                    ToastNotificationType.AI_DIAGNOSTIC -> "KI-DIAGNOSE"
                                }

                                Surface(
                                    color = badgeColor.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = categoryTag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = badgeColor,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }

                                Text(
                                    text = notification.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = if (isGold) Color(0xFFFFD700) else Color.White,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Text(
                                text = notification.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFCCFFDD),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Optional Action Button (e.g. "ZU ERFOLGEN")
                        if (!notification.actionText.isNullOrBlank()) {
                            Surface(
                                color = badgeColor.copy(alpha = 0.90f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onActionClick?.invoke(notification.targetDestinationName)
                                        onDismiss()
                                    }
                                    .testTag("ghost_hud_toast_action")
                            ) {
                                Text(
                                    text = notification.actionText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        // Close Button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("ghost_hud_toast_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Benachrichtigung schließen",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
