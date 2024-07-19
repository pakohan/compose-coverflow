package com.pakohan.coverflow.coverflow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoverFlow(
    modifier: Modifier = Modifier,
    params: CoverFlowParams = CoverFlowParams(),
    content: CoverFlowScope.() -> Unit,
) {
    val state = rememberLazyListState()
    var size by remember { mutableStateOf(IntSize.Zero) }
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            },
        verticalAlignment = Alignment.CenterVertically,
        state = state,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    ) {
        if (size != IntSize.Zero) {
            val painter = CoverPainter(
                params,
                size
            )
            val coverFlowScope = CoverFlowScope(
                painter = painter,
                lazyListScope = this,
            ) {
                coroutineScope.launch {
                    state.animateScrollToItem(
                        it + 1,
                        -painter.spacerWidth
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.width(with(LocalDensity.current) { (painter.spacerWidth).toDp() }))
            }

            coverFlowScope.apply(content)

            item {
                Spacer(modifier = Modifier.width(with(LocalDensity.current) { (painter.spacerWidth).toDp() }))
            }
        }
    }
}
