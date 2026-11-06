package com.pakohan.coverflow

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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

/**
 * CoverFlow implements 🍎 CoverFlow for Jetpack Compose.
 *
 * It uses [LazyRow] under the hood.
 *
 * @param modifier use it to change the background color
 * @param state used to observe and control the scrolling from the outside
 * @param params used to configure the design
 * @param content used to add list items in the [CoverFlowScope]
 */
@Composable
fun CoverFlow(
    modifier: Modifier = Modifier,
    state: CoverFlowState = rememberCoverFlowState(),
    params: CoverFlowParams = CoverFlowParams(),
    content: CoverFlowScope.() -> Unit,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    LazyRow(
        modifier = modifier
            .fillMaxSize()
            // onSizeChanged only fires when the size changes, unlike onGloballyPositioned, which
            // also fires whenever the component moves on screen.
            .onSizeChanged { size = it },
        verticalAlignment = Alignment.CenterVertically,
        state = state.lazyListState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state.lazyListState),
    ) {
        // Read size inside this lambda: LazyRow re-runs it as soon as size changes, possibly before
        // CoverFlow itself recomposes. A Geometry created outside would still be the stale one.
        if (size != IntSize.Zero) {
            val geometry = Geometry(params, size)
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
