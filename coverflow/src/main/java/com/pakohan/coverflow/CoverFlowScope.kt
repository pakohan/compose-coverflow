package com.pakohan.coverflow

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Receiver scope to add elements to the CoverFlow list.
 */
interface CoverFlowScope {
    /**
     * Add typed items. See [LazyListScope.items]
     *
     * @param items the data to show
     * @param onSelectHandler called with the item and its index whenever it becomes the centered
     * cover
     * @param key a stable, unique key for an item. Keys keep the scroll position and the state of
     * covers correct when the list changes. Must be saveable in a Bundle.
     * @param contentType the type of content of an item, used to reuse compositions efficiently
     * @param itemContent the content of a cover
     */
    fun <T> items(
        items: List<T>,
        onSelectHandler: (item: T, index: Int) -> Unit = { _: T, _: Int -> },
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable ((item: T) -> Unit),
    )

    /**
     * Add [count] items. See [LazyListScope.items]
     *
     * @param count the amount of items
     * @param onSelectHandler called with the index whenever an item becomes the centered cover
     * @param key a stable, unique key for an item. Must be saveable in a Bundle.
     * @param contentType the type of content of an item
     * @param itemContent the content of a cover
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
        key: ((item: T) -> Any)?,
        contentType: (item: T) -> Any?,
        itemContent: @Composable (item: T) -> Unit,
    ) = items(
        count = items.size,
        onSelectHandler = {
            onSelectHandler(
                items[it],
                it,
            )
        },
        key = key?.let { itemKey -> { index: Int -> itemKey(items[index]) } },
        contentType = { contentType(items[it]) },
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

            // derivedStateOf: this cover only recomposes when its own selection state flips, not
            // whenever any other cover gets selected.
            val isSelected by remember(coverFlowState, coverIndex) {
                derivedStateOf { coverFlowState.selectedIndex == coverIndex }
            }

            // Side effects (calling the handler) belong in an effect, never in the composable
            // body. snapshotFlow only emits when the value changes; filter makes sure the handler
            // is only called when this cover becomes selected.
            val currentOnSelectHandler by rememberUpdatedState(onSelectHandler)
            LaunchedEffect(coverFlowState, coverIndex) {
                snapshotFlow { isSelected }
                    .filter { it }
                    .collect { currentOnSelectHandler(localIndex) }
            }

            Cover(
                geometry = geometry,
                state = coverFlowState,
                lazyIndex = coverIndex.toLazyIndex(),
                selected = isSelected,
                onClickHandler = {
                    coroutineScope.launch { coverFlowState.animateScrollToItem(coverIndex) }
                },
            ) {
                itemContent(localIndex)
            }
        }
    }
}
