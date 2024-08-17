package com.pakohan.coverflow.lazyayout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberLazyLayoutState(itemWidth: Int) = remember { // remember to save the scrollOffset and items
    LazyLayoutState(itemWidth)
}

data class LazyLayoutItemContent(
    val index: Int,
    val itemContent: @Composable (Int) -> Unit,
)

interface CustomLazyListScope {
    fun items(
        amount: Int,
        itemContent: @Composable (Int) -> Unit,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
class LazyLayoutState(
    val itemWidth: Int,
) : LazyLayoutItemProvider,
        CustomLazyListScope {
    var scrollOffset = mutableIntStateOf(0)
    private val items = mutableListOf<LazyLayoutItemContent>()

    fun onDrag(offset: Int) {
        scrollOffset.intValue = (scrollOffset.intValue - offset).coerceAtLeast(0)
            .coerceAtMost(items.size * itemWidth - itemWidth)
    }

    internal fun measure(
        containerWidth: Int,
    ) = Geometry(
        scrollOffset.intValue,
        containerWidth,
        itemWidth,
        itemCount,
    )

    override val itemCount
        get() = items.size

    override fun items(
        amount: Int,
        itemContent: @Composable (Int) -> Unit,
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

    @Composable
    override fun Item(
        index: Int,
        key: Any,
    ) {
        val item = items.getOrNull(index)
        item?.itemContent?.invoke(
            index,
        )
    }
}

@Immutable
@Stable
internal data class Geometry(
    val scrollOffset: Int,
    val containerWidth: Int,
    val itemWidth: Int,
    val amountItems: Int,
) {
    private val halfWidth = itemWidth / 2
    private val spacerWidth = containerWidth / 2 - halfWidth
    private val visibleSpacer = spacerWidth - scrollOffset
    val firstVisibleItemX: Int
        get() = if (visibleSpacer < 0) { // overscroll
            visibleSpacer % itemWidth
        } else {
            visibleSpacer
        }
    val firstVisibleItemIndex: Int
        get() = if (visibleSpacer < 0) {
            (-visibleSpacer) / itemWidth
        } else {
            0
        }
    private val firstFullVisibleItemX: Int
        get() = if (firstVisibleItemX < 0) {
            itemWidth + firstVisibleItemX
        } else {
            firstVisibleItemX
        }
    private val spaceFromFirstFullVisibleItem = containerWidth - firstFullVisibleItemX
    private val amountFullyVisibleItems = spaceFromFirstFullVisibleItem / itemWidth
    private val spaceLeftAfterFullyVisibleItems = spaceFromFirstFullVisibleItem % itemWidth
    private val lastItemFullyVisible = spaceLeftAfterFullyVisibleItems == 0
    val lastVisibleItemIndex: Int
        get() {
            var result = firstVisibleItemIndex
            result += amountFullyVisibleItems
            if (!lastItemFullyVisible) {
                result++
            }
            if (result > amountItems - 1) {
                result = amountItems - 1
            }
            return result
        }

//    private val firstItemFullyVisible = firstVisibleItemX < 0
//    val amountVisibleItems: Int
//        get() {
//            var result = 0
//            if (!firstItemFullyVisible) {
//                result++
//            }
//            result += amountFullyVisibleItems
//            if (!lastItemFullyVisible) {
//                result++
//            }
//            return result
//        }
}
