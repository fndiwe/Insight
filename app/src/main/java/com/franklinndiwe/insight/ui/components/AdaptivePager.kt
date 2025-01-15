package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.SwipeDirection
import kotlin.math.absoluteValue

@Composable
fun AdaptivePager(
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    swipeDirection: SwipeDirection,
    pagerState: PagerState? = null,
    key: (Int) -> Any,
    content: @Composable (Int, Modifier) -> Unit,
) {
    when (swipeDirection) {
        SwipeDirection.Horizontal -> pagerState?.let { state ->
            HorizontalPager(
                modifier = modifier,
                state = state,
                key = key,
                contentPadding = PaddingValues(24.dp),
                userScrollEnabled = userScrollEnabled,
                beyondViewportPageCount = 1
            ) { value ->
                content(
                    value,
                    Modifier
                        .carouselTransition(value, state)
                        .clip(defaultShape)
                )
            }
        }

        SwipeDirection.Vertical -> pagerState?.let { state ->
            VerticalPager(
                modifier = modifier,
                state = state,
                key = key,
                pageSpacing = 8.dp,
                userScrollEnabled = userScrollEnabled,
                beyondViewportPageCount = 1
            ) {
                content(
                    it,
                    Modifier
                )
            }
        }
    }
}

fun Modifier.carouselTransition(page: Int, pagerState: PagerState) = graphicsLayer {
    val pageOffset =
        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
    lerp(
        start = .91f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f..1f)
    ).also {
        alpha = it
        scaleX = it
        scaleY = it
    }
}