package com.pakohan.coverflow.lazyayout

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.math.abs

@Stable
@Immutable
data class Dimension(
    val width: Int,
    val height: Int,
) {
    val shortEdge: Int = if (width < height) width else height

    companion object {
        val Zero = Dimension(
            0,
            0,
        )
    }
}

interface Geometry {
    fun itemWidth(containerSize: Dimension): Int

    fun itemY(
        containerSize: Dimension,
        itemHeight: Int,
    ): Int
}

class CoverFlowGeometry(private val itemSizeFactor: Float = .5f) : Geometry {
    override fun itemWidth(containerSize: Dimension): Int = (containerSize.shortEdge * itemSizeFactor).toInt()

    private fun halfWidth(containerSize: Dimension): Int {
        return itemWidth(containerSize) / 2
    }

    override fun itemY(
        containerSize: Dimension,
        itemHeight: Int,
    ) = containerSize.height / 2 + halfWidth(containerSize) - itemHeight
}

data class CalculatedGeometry(
    val scrollOffset: Int,
    val containerSize: Dimension,
    val geometry: Geometry,
    val itemCount: Int,
) {
    private val halfContainerHeight = containerSize.height / 2

    val itemWidth = geometry.itemWidth(containerSize)

    private val halfWidth = itemWidth / 2

    private val spacerWidth = containerSize.width / 2 - halfWidth

    private val visibleSpacer = spacerWidth - scrollOffset

    val maxHeight: Int = halfContainerHeight + halfWidth

    val firstVisibleItemX = if (visibleSpacer < 0) {
        visibleSpacer % itemWidth
    } else {
        visibleSpacer
    }

    val firstVisibleItemIndex = if (visibleSpacer < 0) {
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

    val lastVisibleItemIndex: Int
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

    internal fun y(itemHeight: Int) = geometry.itemY(
        containerSize,
        itemHeight,
    )
}
