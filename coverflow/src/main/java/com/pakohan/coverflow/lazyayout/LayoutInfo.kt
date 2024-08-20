package com.pakohan.coverflow.lazyayout

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

interface LayoutInfo {
    fun itemWidth(containerSize: Dimension): Int

    fun itemY(
        containerSize: Dimension,
        itemHeight: Int,
    ): Int
}

class CoverFlowLayoutInfo(private val itemSizeFactor: Float = .5f) : LayoutInfo {
    override fun itemWidth(containerSize: Dimension): Int = (containerSize.shortEdge * itemSizeFactor).toInt()

    private fun halfWidth(containerSize: Dimension): Int {
        return itemWidth(containerSize) / 2
    }

    override fun itemY(
        containerSize: Dimension,
        itemHeight: Int,
    ) = containerSize.height / 2 + halfWidth(containerSize) - itemHeight
}

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
