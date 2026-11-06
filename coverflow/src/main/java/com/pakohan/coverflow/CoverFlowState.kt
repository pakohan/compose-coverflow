package com.pakohan.coverflow

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlin.math.abs

/**
 * Initializes a CoverFlowState.
 *
 * @param onSelectHandler will be called whenever [CoverFlowState.selectedIndex] changes. It is
 * called with -1 as long as no cover is centered, e.g. during scrolling.
 */
@Composable
fun rememberCoverFlowState(onSelectHandler: (Int) -> Unit = {}): CoverFlowState {
    val lazyListState: LazyListState = rememberLazyListState()
    val state = remember(lazyListState) { CoverFlowState(lazyListState) }

    // Always call the latest lambda, even if the caller passes a new one on recomposition.
    val currentOnSelectHandler by rememberUpdatedState(onSelectHandler)
    LaunchedEffect(state) {
        snapshotFlow { state.selectedIndex }.collect { currentOnSelectHandler(it) }
    }

    return state
}

/**
 * Exposes the scroll state.
 *
 * Every value is derived from the underlying [LazyListState], so it is always consistent with
 * what's on screen and observable by Compose.
 */
@Stable
class CoverFlowState internal constructor(
    internal val lazyListState: LazyListState,
) {
    /**
     * Index of the selected (centered) cover. -1 while no cover is centered, e.g. during scrolling.
     *
     * This is snapshot state: reading it in a composable recomposes it when the selection changes.
     * Use `snapshotFlow { state.selectedIndex }` to react to changes in a coroutine.
     */
    val selectedIndex: Int by derivedStateOf {
        val layoutInfo = lazyListState.layoutInfo
        val viewportCenter = layoutInfo.viewportSize.width / 2
        val cover = layoutInfo.coverItems().firstOrNull {
            // Same criterion as before: the cover is less than a pixel away from the center.
            abs(it.offset + it.size * .5f - viewportCenter).toInt() == 0
        }
        cover?.index?.toCoverIndex() ?: -1
    }

    /**
     * Whether the cover flow is currently being scrolled by gesture, fling or animation.
     */
    val isScrollInProgress: Boolean
        get() = lazyListState.isScrollInProgress

    /**
     * Animates scrolling so that the cover at [index] ends up in the center.
     */
    suspend fun animateScrollToItem(index: Int) {
        val offset = centeringOffset() ?: return
        lazyListState.animateScrollToItem(index.toLazyIndex(), offset)
    }

    /**
     * Instantly scrolls so that the cover at [index] ends up in the center.
     */
    suspend fun scrollToItem(index: Int) {
        val offset = centeringOffset() ?: return
        lazyListState.scrollToItem(index.toLazyIndex(), offset)
    }

    /**
     * Returns the lazy-list item info of the cover at [lazyIndex], if it is currently visible.
     */
    internal fun visibleItemInfo(lazyIndex: Int): LazyListItemInfo? =
        lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == lazyIndex }

    /**
     * The scroll offset that places a cover in the center: half the viewport minus half a cover.
     * This equals -[Geometry.spacerWidth], but is read from the actual layout. Returns null when
     * nothing has been laid out yet.
     */
    private fun centeringOffset(): Int? {
        val layoutInfo = lazyListState.layoutInfo
        val coverWidth = layoutInfo.coverItems().firstOrNull()?.size ?: return null
        return -(layoutInfo.viewportSize.width / 2 - coverWidth / 2)
    }
}

// The lazy list contains a leading and a trailing spacer around the covers.
internal fun Int.toLazyIndex(): Int = this + 1
private fun Int.toCoverIndex(): Int = this - 1

private fun LazyListLayoutInfo.coverItems() =
    visibleItemsInfo.filter { it.index in 1..<totalItemsCount - 1 }
