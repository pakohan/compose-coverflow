package com.pakohan.coverflow.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectCenterLazyRow(
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: SelectCenterLazyRowScope.() -> Unit,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val spacerModifier = Modifier.width(with(LocalDensity.current) { (size.width / 2).toDp() })

    val state: LazyListState = rememberLazyListState()

    LazyRow(
        modifier = modifier.onGloballyPositioned {
            size = it.size
        },
        state = state,
        verticalAlignment = verticalAlignment,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
    ) {
        item {
            Spacer(modifier = spacerModifier)
        }

        SelectCenterLazyRowScopeImpl(this).content()

        item {
            Spacer(modifier = spacerModifier)
        }
    }
}
