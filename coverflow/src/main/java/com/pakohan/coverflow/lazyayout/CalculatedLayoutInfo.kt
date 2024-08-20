package com.pakohan.coverflow.lazyayout

import kotlin.math.abs

data class CalculatedLayoutInfo(
    val scrollOffset: Int,
    val containerSize: Dimension,
    val layoutInfo: LayoutInfo,
    val itemCount: Int,
) {
    private val halfContainerHeight = containerSize.height / 2

    val itemWidth = layoutInfo.itemWidth(containerSize)

    private val halfWidth = itemWidth / 2

    private val spacerWidth = containerSize.width / 2 - halfWidth

    private val visibleSpacer = spacerWidth - scrollOffset

    val maxHeight: Int = halfContainerHeight + halfWidth

    val firstVisibleItemX = if (visibleSpacer < 0) {
        visibleSpacer % itemWidth
    } else {
        visibleSpacer
    }

    private val firstVisibleItemIndex = if (visibleSpacer < 0) {
        (-visibleSpacer) / itemWidth
    } else {
        0
    }

    private val firstFullVisibleItemX = if (firstVisibleItemX < 0) {
        itemWidth + firstVisibleItemX
    } else {
        firstVisibleItemX
    }

    private val spaceFromFirstFullVisibleItem = containerSize.width - firstFullVisibleItemX

    private val amountFullyVisibleItems = spaceFromFirstFullVisibleItem / itemWidth

    private val spaceLeftAfterFullyVisibleItems = spaceFromFirstFullVisibleItem % itemWidth

    private val lastItemFullyVisible = spaceLeftAfterFullyVisibleItems == 0

    private val selectedIndex = (scrollOffset + halfWidth) / itemWidth

    private val lastVisibleItemIndex: Int
        get() {
            var result = firstVisibleItemIndex
            result += amountFullyVisibleItems
            if (!lastItemFullyVisible) {
                result++
            }
            if (result > itemCount - 1) {
                result = itemCount - 1
            }
            return result
        }

    private fun itemsToCenter(index: Int) = -abs(selectedIndex - index)

    internal fun zIndex(index: Int) = itemsToCenter(index).toFloat()

    fun distanceToCenter(index: Int) = index * itemWidth - scrollOffset

    internal fun y(itemHeight: Int) = layoutInfo.itemY(
        containerSize,
        itemHeight,
    )

    internal val indexRange: IntRange = firstVisibleItemIndex..lastVisibleItemIndex
}
