package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BloodRedPrimary

/**
 * Renders an accessible, visible vertical scroll bar for standard ScrollState.
 */
fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    width: Dp = 5.dp,
    color: Color = BloodRedPrimary.copy(alpha = 0.5f)
): Modifier = drawWithContent {
    drawContent()

    val totalRange = scrollState.maxValue.toFloat()
    if (totalRange <= 0f) return@drawWithContent

    val viewportHeight = size.height
    val scrollableContentHeight = totalRange + viewportHeight
    val thumbHeight = ((viewportHeight / scrollableContentHeight) * viewportHeight).coerceIn(40.dp.toPx(), viewportHeight)
    val thumbOffset = (scrollState.value.toFloat() / totalRange) * (viewportHeight - thumbHeight)

    drawRoundRect(
        color = color,
        topLeft = Offset(size.width - width.toPx() - 2.dp.toPx(), thumbOffset),
        size = Size(width.toPx(), thumbHeight),
        cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
    )
}

/**
 * Renders an accessible, visible vertical scroll bar for LazyListState.
 */
fun Modifier.lazyVerticalScrollbar(
    state: LazyListState,
    width: Dp = 5.dp,
    color: Color = BloodRedPrimary.copy(alpha = 0.55f)
): Modifier = drawWithContent {
    drawContent()

    val totalItems = state.layoutInfo.totalItemsCount
    if (totalItems <= 0) return@drawWithContent

    val visibleItems = state.layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return@drawWithContent

    val firstVisible = state.firstVisibleItemIndex
    val visibleCount = visibleItems.size

    if (visibleCount >= totalItems) return@drawWithContent

    val viewportHeight = size.height
    val thumbHeight = ((visibleCount.toFloat() / totalItems.toFloat()) * viewportHeight).coerceIn(40.dp.toPx(), viewportHeight)
    val scrollProgress = (firstVisible.toFloat() / (totalItems - visibleCount).coerceAtLeast(1).toFloat())
    val thumbOffset = scrollProgress * (viewportHeight - thumbHeight)

    drawRoundRect(
        color = color,
        topLeft = Offset(size.width - width.toPx() - 2.dp.toPx(), thumbOffset),
        size = Size(width.toPx(), thumbHeight),
        cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
    )
}
