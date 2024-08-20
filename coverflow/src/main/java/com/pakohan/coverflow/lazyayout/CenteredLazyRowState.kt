package com.pakohan.coverflow.lazyayout

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
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
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.sign

@Composable
fun rememberCenteredLazyRowState(layoutInfo: LayoutInfo = CoverFlowLayoutInfo()): CenteredLazyRowState {
    val density = LocalDensity.current
    return remember { // remember to save the scrollOffset and items
        CenteredLazyRowState(
            layoutInfo,
            density,
        )
    }
}

typealias ItemFunc = @Composable ItemScope.(Int) -> Unit
typealias ParameterItemFunc<T> = @Composable ItemScope.(Int, T) -> Unit

@OptIn(ExperimentalFoundationApi::class)
class CenteredLazyRowState(
    private val layoutInfo: LayoutInfo,
    private val density: Density,
) {
    private var scrollOffset by mutableIntStateOf(0)
    private val items = mutableListOf<LazyLayoutItemContent>()
    internal var containerSize: Dimension = Dimension.Zero

    private data class LazyLayoutItemContent(
        val index: Int,
        val itemContent: ItemFunc,
    )

    val calculatedLayoutInfo
        get() = CalculatedLayoutInfo(
            scrollOffset = scrollOffset,
            containerSize = containerSize,
            layoutInfo = layoutInfo,
            itemCount = items.size,
        )

    internal val itemProvider = object : LazyLayoutItemProvider {
        override val itemCount
            get() = items.size

        @Composable
        override fun Item(
            index: Int,
            key: Any,
        ) {
            val item = items.getOrNull(index) ?: return
            item.itemContent(
                ItemScopeImpl(calculatedLayoutInfo.distanceToCenter(index)),
                item.index,
            )
        }
    }

    internal val lazyListScope = object : CustomLazyListScope {
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
        ) = items(items.size) {
            itemContent(
                it,
                items[it],
            )
        }
    }

    private fun consumeScrollDelta(delta: Float): Float {
        val consumedDelta = floor(delta)
        scrollOffset -= consumedDelta.toInt()
        return consumedDelta
    }

    internal val scrollableState = ScrollableState(::consumeScrollDelta)

    internal val snapLayoutInfoProvider = object : SnapLayoutInfoProvider {
        override fun calculateApproachOffset(initialVelocity: Float): Float {
            val decayAnimationSpec: DecayAnimationSpec<Float> = splineBasedDecay(density)
            val offset = decayAnimationSpec.calculateTargetValue(
                0f,
                initialVelocity,
            ).absoluteValue

            val estimatedNumberOfItemsInDecay = floor(offset.absoluteValue / calculatedLayoutInfo.itemWidth)

            val approachOffset =
                estimatedNumberOfItemsInDecay * calculatedLayoutInfo.itemWidth - calculatedLayoutInfo.itemWidth
            val finalDecayOffset = approachOffset.coerceAtLeast(0f)
            return if (finalDecayOffset == 0f) {
                finalDecayOffset
            } else {
                finalDecayOffset * initialVelocity.sign
            }
        }

        override fun calculateSnappingOffset(currentVelocity: Float): Float {
            var lowerBoundOffset = Float.NEGATIVE_INFINITY
            var upperBoundOffset = Float.POSITIVE_INFINITY

            for (i in calculatedLayoutInfo.indexRange) {
                val distance = calculatedLayoutInfo.distanceToCenter(i)
                if (distance < 0 && distance > lowerBoundOffset) {
                    lowerBoundOffset = distance.toFloat()
                } else if (distance > 0 && distance < upperBoundOffset) {
                    upperBoundOffset = distance.toFloat()
                } else if (distance == 0) {
                    return 0f
                }
            }

            var result = if (currentVelocity < 0) { // scrolling to the right, elements moving to the left
                upperBoundOffset
            } else {
                lowerBoundOffset
            }
            if (result == Float.NEGATIVE_INFINITY) {
                result = upperBoundOffset
            }
            if (result == Float.POSITIVE_INFINITY) {
                result = lowerBoundOffset
            }

            return result
        }
    }
}
