package com.pakohan.coverflow

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Receiver scope to add elements to the CoverFlow list.
 */
interface CoverFlowScope {
    /**
     * Add typed items. See [LazyListScope.items]
     */
    fun <T> items(
        items: List<T>,
        onSelectHandler: (item: T, index: Int) -> Unit = { _: T, _: Int -> },
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable ((item: T) -> Unit),
    )

    /**
     * Add typed items. See [LazyListScope.items]
     */
    fun items(
        count: Int,
        onSelectHandler: (index: Int) -> Unit = {},
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable ((index: Int) -> Unit),
    )
}

internal class CoverFlowScopeImpl(
    private val geometry: Geometry,
    private val lazyListScope: LazyListScope,
    private val coverFlowState: CoverFlowState,
) : CoverFlowScope {
    override fun <T> items(
        items: List<T>,
        onSelectHandler: (item: T, index: Int) -> Unit,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (item: T) -> Unit,
    ) = items(
        count = items.size,
        onSelectHandler = {
            onSelectHandler(
                items[it],
                it,
            )
        },
        key = key,
        contentType = contentType,
    ) {
        itemContent(items[it])
    }

    /**
     * Number of covers added by previous [items] calls. Needed to translate the index local to one
     * [items] call into the index within the whole cover flow.
     */
    private var coverCount = 0

    override fun items(
        count: Int,
        onSelectHandler: (index: Int) -> Unit,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit,
    ) {
        val firstCoverIndex = coverCount
        coverCount += count

        lazyListScope.items(
            count,
            key,
            contentType,
        ) { localIndex ->
            val coverIndex = firstCoverIndex + localIndex
            val coroutineScope = rememberCoroutineScope()

            // Side effects (calling the handler) belong in an effect, never in the composable
            // body. snapshotFlow turns the derived selectedIndex into a Flow, and
            // distinctUntilChanged + filter make sure the handler is only called once each time
            // this cover becomes selected.
            val currentOnSelectHandler by rememberUpdatedState(onSelectHandler)
            LaunchedEffect(coverFlowState, coverIndex) {
                snapshotFlow { coverFlowState.selectedIndex == coverIndex }
                    .distinctUntilChanged()
                    .filter { it }
                    .collect { currentOnSelectHandler(localIndex) }
            }

            Cover(
                geometry = geometry,
                state = coverFlowState,
                lazyIndex = coverIndex.toLazyIndex(),
                onClickHandler = {
                    coroutineScope.launch { coverFlowState.animateScrollToItem(coverIndex) }
                },
            ) {
                itemContent(localIndex)
            }
        }
    }
}
