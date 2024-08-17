package com.pakohan.coverflow.lazyayout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange

@Composable
fun rememberLazyLayoutState(geometry: Geometry = CoverFlowGeometry()) =
    remember { // remember to save the scrollOffset and items
        LazyLayoutState(geometry)
    }

data class LazyLayoutItemContent(
    val index: Int,
    val itemContent: @Composable ItemScope.(Int) -> Unit,
)

interface CustomLazyListScope {
    fun items(
        amount: Int,
        itemContent: @Composable ItemScope.(Int) -> Unit,
    )

    fun <T> items(
        items: List<T>,
        itemContent: @Composable ItemScope.(Int, T) -> Unit,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
class LazyLayoutState(
    private val geometry: Geometry,
) : LazyLayoutItemProvider,
        CustomLazyListScope {
    private var scrollOffset by mutableIntStateOf(0)

    private val items = mutableListOf<LazyLayoutItemContent>()

    internal var containerSize: Dimension = Dimension.Zero
        set(value) {
            if (field != value) field = value
        }

    val calculatedGeometry
        get() = CalculatedGeometry(
            scrollOffset = scrollOffset,
            containerSize = containerSize,
            geometry = geometry,
            itemCount = itemCount,
        )

    internal fun onDrag(
        change: PointerInputChange,
        dragAmount: Offset,
    ) {
        change.consume()
        scrollOffset = (scrollOffset - dragAmount.x.toInt()).coerceAtLeast(0)
            .coerceAtMost(items.size * calculatedGeometry.itemWidth - calculatedGeometry.itemWidth)
    }

    override val itemCount
        get() = items.size

    override fun items(
        amount: Int,
        itemContent: @Composable ItemScope.(Int) -> Unit,
    ) {
        for (i in 0..<amount) {
            items.add(
                LazyLayoutItemContent(
                    i,
                    itemContent,
                ),
            )
        }
    }

    override fun <T> items(
        items: List<T>,
        itemContent: @Composable ItemScope.(Int, T) -> Unit,
    ) = items(items.size) {
        itemContent(
            it,
            items[it],
        )
    }

    @Composable
    override fun Item(
        index: Int,
        key: Any,
    ) {
        val item = items.getOrNull(index) ?: return
        item.itemContent.invoke(
            ItemScopeImpl(calculatedGeometry.distanceToCenter(index)),
            item.index,
        )
    }
}
