package com.pakohan.coverflow.lazyayout

import android.graphics.Point
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CenteredLazyRow(
    modifier: Modifier = Modifier,
    state: CenteredLazyRowState = rememberCenteredLazyRowState(),
    content: CustomLazyListScope.() -> Unit,
) {
    rememberUpdatedState(content).also { state.lazyListScope.apply(it.value) }

    LazyLayout(
        modifier = modifier.scrollable(
            state = state.scrollableState,
            orientation = Orientation.Horizontal,
            flingBehavior = rememberSnapFlingBehavior(state.snapLayoutInfoProvider),
        ),
        itemProvider = { state.itemProvider },
    ) {
        state.containerSize = Dimension(
            width = it.maxWidth,
            height = it.maxHeight,
        )

        val calculatedGeometry = state.calculatedLayoutInfo
        var x = calculatedGeometry.firstVisibleItemX
        val visibleItems = mutableListOf<RowElement>()

        for (i in calculatedGeometry.indexRange) {
            val placeables = measure(
                i,
                Constraints(
                    maxWidth = calculatedGeometry.itemWidth,
                    maxHeight = calculatedGeometry.maxHeight,
                ),
            )
            if (placeables.size !in 1..2) {
                throw IllegalArgumentException("CustomLazyLayout only supports one composable per item, got ${placeables.size}")
            }

            visibleItems.add(
                RowElement(
                    placeables[0],
                    if (placeables.size > 1) placeables[1] else null,
                    Point(
                        x,
                        calculatedGeometry.y(placeables[0].height),
                    ),
                    calculatedGeometry.zIndex(i),
                ),
            )
            x += calculatedGeometry.itemWidth
        }

        layout(
            it.maxWidth,
            it.maxHeight,
        ) {
            visibleItems.forEach { element ->
                element.placeable.placeRelative(
                    element.coordinates.x,
                    element.coordinates.y,
                    element.zIndex,
                )
                if (element.mirrorPlaceable != null) {
                    element.mirrorPlaceable.placeRelative(
                        element.coordinates.x,
                        element.coordinates.y + element.placeable.height,
                        element.zIndex,
                    )
                }
            }
        }
    }
}

private data class RowElement(
    val placeable: Placeable,
    val mirrorPlaceable: Placeable?,
    val coordinates: Point,
    val zIndex: Float,
)
