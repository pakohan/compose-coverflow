package com.pakohan.coverflow.lazyayout

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun rememberCenteredLazyRowState(centeredLazyRowLayoutInfo: CenteredLazyRowLayoutInfo = CoverFlowLayoutInfo()): CenteredLazyRowState {
    val density = LocalDensity.current
    return remember { // remember to save the scrollOffset and items
        CenteredLazyRowState(
            centeredLazyRowLayoutInfo,
            density,
        )
    }
}

typealias ItemFunc = @Composable CenteredLazyRowItemScope.(Int, Int) -> Unit
typealias ParameterItemFunc<T> = @Composable CenteredLazyRowItemScope.(Int, Int, T) -> Unit

@OptIn(ExperimentalFoundationApi::class)
class CenteredLazyRowState(
    private val centeredLazyRowLayoutInfo: CenteredLazyRowLayoutInfo,
    private val density: Density,
) {
    private var scrollOffset by mutableIntStateOf(0)
    private val items = mutableListOf<LazyLayoutItemContent>()
    internal var containerSize: Dimension = Dimension.Zero

    private data class LazyLayoutItemContent(
        val index: Int,
        val itemContent: ItemFunc,
    )

    val calculatedCenteredLazyRowLayoutInfo
        get() = CalculatedCenteredLazyRowLayoutInfo(
            scrollOffset = scrollOffset,
            containerSize = containerSize,
            centeredLazyRowLayoutInfo = centeredLazyRowLayoutInfo,
            itemCount = items.size,
        )

    private fun consumeScrollDelta(delta: Float): Float {
        var consumedDelta = delta
        if (consumedDelta > calculatedCenteredLazyRowLayoutInfo.remainingScrollOffset.toFloat()) {
            consumedDelta = calculatedCenteredLazyRowLayoutInfo.remainingScrollOffset.toFloat()
        }
        scrollOffset -= consumedDelta.toInt()

        Log.d(
            "consumeScrollDelta",
            "$delta -> $consumedDelta",
        )
        return consumedDelta
    }

    internal val scrollableState = ScrollableState(::consumeScrollDelta)

    internal val itemProvider = object : LazyLayoutItemProvider {
        override val itemCount
            get() = items.size

        @Composable
        override fun Item(
            index: Int,
            key: Any,
        ) {
            val item = items.getOrNull(index) ?: return
            val distance = calculatedCenteredLazyRowLayoutInfo.distanceToCenter(index)
            item.itemContent(
                CenteredLazyRowItemScopeImpl(distance),
                item.index,
                distance,
            )
        }
    }

    internal val lazyListScope = object : CenteredLazyRowScope {
        override fun items(
            amount: Int,
            itemContent: ItemFunc,
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
            itemContent: ParameterItemFunc<T>,
        ) = items(items.size) { index, distance ->
            itemContent(
                index,
                distance,
                items[index],
            )
        }
    }

    internal val snapLayoutInfoProvider = object : SnapLayoutInfoProvider {
        override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float = 0f
//        {
//            val decayAnimationSpec: DecayAnimationSpec<Float> = splineBasedDecay(density)
//            val offset = decayAnimationSpec.calculateTargetValue(
//                0f,
//                initialVelocity,
//            ).absoluteValue
//
//            val estimatedNumberOfItemsInDecay =
//                floor(offset.absoluteValue / calculatedCenteredLazyRowLayoutInfo.itemWidth)
//
//            val approachOffset =
//                estimatedNumberOfItemsInDecay * calculatedCenteredLazyRowLayoutInfo.itemWidth - calculatedCenteredLazyRowLayoutInfo.itemWidth
//            val finalDecayOffset = approachOffset.coerceAtLeast(0f)
//                .coerceAtMost(calculatedCenteredLazyRowLayoutInfo.maximumScrollOffset.toFloat())
//            return if (finalDecayOffset == 0f) {
//                finalDecayOffset
//            } else {
//                finalDecayOffset * initialVelocity.sign
//            }
//        }

        // positive numbers lead back to the beginning
        override fun calculateSnapOffset(velocity: Float): Float {
            val result = if (velocity < 0) { // scrolling to the right, elements moving to the left
                if (scrollOffset + calculatedCenteredLazyRowLayoutInfo.nextItemRightOffset.toFloat() > calculatedCenteredLazyRowLayoutInfo.maximumScrollOffset) {
                    calculatedCenteredLazyRowLayoutInfo.nextItemLeftOffset.toFloat()
                } else {
                    -calculatedCenteredLazyRowLayoutInfo.nextItemRightOffset.toFloat()
                }
            } else if (velocity > 0) {
                if (scrollOffset - calculatedCenteredLazyRowLayoutInfo.nextItemLeftOffset.toFloat() < 0) {
                    -calculatedCenteredLazyRowLayoutInfo.nextItemRightOffset.toFloat()
                } else {
                    calculatedCenteredLazyRowLayoutInfo.nextItemLeftOffset.toFloat()
                }
            } else {
                0f
            }

            Log.d(
                "calculateSnappingOffset",
                velocity.toString(),
            )
            return 10f
        }
    }
}
