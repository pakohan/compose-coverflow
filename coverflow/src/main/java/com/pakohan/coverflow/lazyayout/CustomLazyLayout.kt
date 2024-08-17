package com.pakohan.coverflow.lazyayout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints

private data class CustomPlaceable(
    val placeable: Placeable,
    val x: Int,
    val y: Int,
    val zIndex: Float,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomLazyLayout(
    modifier: Modifier = Modifier,
    state: LazyLayoutState = rememberLazyLayoutState(),
    content: CustomLazyListScope.() -> Unit,
) {

    rememberUpdatedState(content).also { state.apply(it.value) }

    LazyLayout(
        modifier = modifier
            .lazyLayoutPointerInput(state)
            .clipToBounds(),
        itemProvider = {
            state
        },
    ) {
        state.containerSize = Dimension(
            width = it.maxWidth,
            height = it.maxHeight,
        )

        val calculatedGeometry = state.calculatedGeometry

        var x = calculatedGeometry.firstVisibleItemX

        val visibleItems = mutableListOf<CustomPlaceable>()
        for (i in calculatedGeometry.firstVisibleItemIndex..calculatedGeometry.lastVisibleItemIndex) {
            val placeables = measure(
                i,
                Constraints(
                    maxWidth = calculatedGeometry.itemWidth,
                    maxHeight = calculatedGeometry.maxHeight,
                ),
            )
            if (placeables.size != 1) {
                throw IllegalArgumentException("CustomLazyLayout only supports one composable per item, got ${placeables.size}")
            }

            visibleItems.add(
                CustomPlaceable(
                    placeables[0],
                    x,
                    calculatedGeometry.y(placeables[0].height),
                    calculatedGeometry.zIndex(i),
                ),
            )
            x += calculatedGeometry.itemWidth
        }

        layout(
            it.maxWidth,
            it.maxHeight,
        ) {
            visibleItems.forEach { placeable ->
                placeable.placeable.placeRelative(
                    placeable.x,
                    placeable.y,
                    placeable.zIndex,
                )
            }
        }
    }
}

private fun Modifier.lazyLayoutPointerInput(state: LazyLayoutState): Modifier {
    return this.then(
        pointerInput(Unit) {
            detectDragGestures(onDrag = state::onDrag)
        },
    )
}
