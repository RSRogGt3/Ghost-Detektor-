package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun VerticalScrollbarForLazyList(
    state: LazyListState,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00FF66),
    trackColor: Color = color.copy(alpha = 0.15f),
    width: Dp = 6.dp
) {
    val coroutineScope = rememberCoroutineScope()
    val totalItemsCount = state.layoutInfo.totalItemsCount
    val visibleItems = state.layoutInfo.visibleItemsInfo

    if (totalItemsCount == 0 || visibleItems.isEmpty()) return

    val firstVisibleIndex = state.firstVisibleItemIndex
    val visibleCount = visibleItems.size

    val firstItemOffset = state.firstVisibleItemScrollOffset
    val averageItemSize = visibleItems.sumOf { it.size }.toFloat() / visibleCount.coerceAtLeast(1)

    val scrollFraction = remember(firstVisibleIndex, firstItemOffset, totalItemsCount, averageItemSize) {
        val totalEstimatedPx = totalItemsCount * averageItemSize
        val currentPx = firstVisibleIndex * averageItemSize + firstItemOffset
        if (totalEstimatedPx > 0) (currentPx / totalEstimatedPx).coerceIn(0f, 1f) else 0f
    }

    val thumbHeightFraction = remember(visibleCount, totalItemsCount) {
        (visibleCount.toFloat() / totalItemsCount.toFloat()).coerceIn(0.12f, 1.0f)
    }

    val animatedFraction by animateFloatAsState(targetValue = scrollFraction, label = "scrollbar_fraction")

    BoxWithConstraints(
        modifier = modifier
            .width(width + 4.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(trackColor)
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        val maxHeightPx = constraints.maxHeight.toFloat()
        val thumbHeightDp = maxHeight * thumbHeightFraction
        val maxTopDp = maxHeight - thumbHeightDp

        val topPaddingDp = maxTopDp * animatedFraction

        val dragPxToItemIndex = remember(totalItemsCount, maxHeightPx) {
            fun(dragAmountPx: Float) {
                if (maxHeightPx > 0 && totalItemsCount > 0) {
                    val targetIndex = ((dragAmountPx / maxHeightPx) * totalItemsCount).toInt()
                    coroutineScope.launch {
                        state.scrollToItem(targetIndex.coerceIn(0, totalItemsCount - 1))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = topPaddingDp)
                .height(thumbHeightDp.coerceAtLeast(24.dp))
                .clip(CircleShape)
                .background(color)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        val targetPx = (animatedFraction * maxHeightPx) + delta
                        dragPxToItemIndex(targetPx)
                    }
                )
        )
    }
}

@Composable
fun VerticalScrollbarForScrollState(
    state: ScrollState,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00FF66),
    trackColor: Color = color.copy(alpha = 0.15f),
    width: Dp = 6.dp
) {
    val coroutineScope = rememberCoroutineScope()
    val maxValue = state.maxValue

    if (maxValue <= 0) return

    val currentValue = state.value
    val scrollFraction = (currentValue.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(targetValue = scrollFraction, label = "scroll_state_fraction")

    BoxWithConstraints(
        modifier = modifier
            .width(width + 4.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(trackColor)
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        val maxHeightPx = constraints.maxHeight.toFloat()
        val thumbHeightDp = (maxHeight * 0.25f).coerceAtLeast(24.dp)
        val maxTopDp = maxHeight - thumbHeightDp

        val topPaddingDp = maxTopDp * animatedFraction

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = topPaddingDp)
                .height(thumbHeightDp)
                .clip(CircleShape)
                .background(color)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        val newPx = (animatedFraction * maxValue) + (delta / maxHeightPx) * maxValue
                        coroutineScope.launch {
                            state.scrollTo(newPx.toInt().coerceIn(0, maxValue))
                        }
                    }
                )
        )
    }
}
