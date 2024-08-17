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

private data class P(
    val placeable: Placeable,
    val x: Int,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomLazyLayout(
    modifier: Modifier = Modifier,
    itemWidth: Int,
    state: LazyLayoutState = rememberLazyLayoutState(itemWidth),
    content: CustomLazyListScope.() -> Unit,
) {

    val customLazyListScopeState = rememberUpdatedState(content)
    state.apply(customLazyListScopeState.value)

    LazyLayout(
        modifier = modifier
            .clipToBounds()
            .lazyLayoutPointerInput(state),
        itemProvider = {
            state
        },
    ) {
        val range = state.measure(
            it.maxWidth,
        )

        var x = range.firstVisibleItemX

        val visibleItems = mutableListOf<P>()
        for (i in range.firstVisibleItemIndex..range.lastVisibleItemIndex) {
            val placeables = measure(
                i,
                Constraints(),
            )
            if (placeables.size != 1) {
                throw IllegalArgumentException("CustomLazyLayout only supports one composable per item, got ${placeables.size}")
            }

            visibleItems.add(
                P(
                    placeables[0],
                    x,
                ),
            )
            x += state.itemWidth
        }

        layout(
            it.maxWidth,
            it.maxHeight,
        ) {
            visibleItems.forEach { placeable ->
                placeable.placeable.placeRelative(
                    placeable.x,
                    0,
                )
            }
        }
    }
}

private fun Modifier.lazyLayoutPointerInput(state: LazyLayoutState): Modifier {
    return this.then(
        pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                state.onDrag(
                    dragAmount.x.toInt(),
                )
            }
        },
    )
}
