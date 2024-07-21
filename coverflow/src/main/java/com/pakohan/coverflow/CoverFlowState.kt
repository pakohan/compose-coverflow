package com.pakohan.coverflow

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememeberCoverFlowState(
    lazyListState: LazyListState = rememberLazyListState(),
): CoverFlowState {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    return remember {
        CoverFlowState(
            lazyListState,
            coroutineScope
        )
    }
}

class CoverFlowState(
    internal val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope,
) {
    internal var geometry: Geometry? = null
    var selectedIndex: Int = 0
        internal set

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
