package com.pakohan.coverflow

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @param onSelectHandler will be called with -1 as long as the scrolling takes place
 */
@Composable
fun rememberCoverFlowState(onSelectHandler: (Int) -> Unit = {}): CoverFlowState {
    val lazyListState: LazyListState = rememberLazyListState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    return remember {
        CoverFlowState(
            lazyListState,
            coroutineScope,
            onSelectHandler,
        )
    }
}

class CoverFlowState internal constructor(
    internal val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope,
    private val onSelectHandler: (Int) -> Unit = {},
) {
    internal var geometry: Geometry? = null
    var selectedIndex: Int = 0
        internal set(it) {
            field = it
            onSelectHandler(it)
        }

    fun scrollToItem(
        index: Int,
    ) {
        val geometryCopy = geometry
        if (geometryCopy != null) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(
                    index + 1,
                    -geometryCopy.spacerWidth
                )
            }
        }
    }
}
