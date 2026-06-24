package com.pakohan.coverflow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

/**
 * CoverFlow implements 🍎 CoverFlow for Jetpack Compose.
 *
 * It uses [LazyRow] under the hood.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoverFlow(
    /** use it to change the background color */
    modifier: Modifier = Modifier,
    /** used to make the scrolling accessible to the outside */
    state: CoverFlowState = rememberCoverFlowState(),
    /** used to configure the design */
    params: CoverFlowParams = CoverFlowParams(),
    /** used to add list items in the [CoverFlowScope] */
    content: CoverFlowScope.() -> Unit,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    LazyRow(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            },
        verticalAlignment = Alignment.CenterVertically,
        state = state.lazyListState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state.lazyListState),
    ) {
        if (size != IntSize.Zero) {
            val geometry = Geometry(
                params,
                size,
            )

            state.geometry = geometry

            val coverFlowScope = CoverFlowScopeImpl(
                geometry = geometry,
                lazyListScope = this,
                coverFlowState = state,
            )

            item {
                Spacer(modifier = Modifier.width(with(LocalDensity.current) { (geometry.spacerWidth).toDp() }))
            }
            
            coverFlowScope.apply(content)

            item {
                Spacer(modifier = Modifier.width(with(LocalDensity.current) { (geometry.spacerWidth).toDp() }))
            }
        }
    }
}
