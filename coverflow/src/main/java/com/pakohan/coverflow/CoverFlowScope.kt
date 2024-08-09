package com.pakohan.coverflow

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

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

    override fun items(
        count: Int,
        onSelectHandler: (index: Int) -> Unit,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit,
    ) = lazyListScope.items(
        count,
        key,
        contentType,
    ) {
        Cover(
            onClickHandler = { coverFlowState.scrollToItem(it) },
            onSelectedHandler = { isSelected: Boolean ->
                if (selectHandler(
                        it,
                        isSelected,
                    )
                ) {
                    onSelectHandler(it)
                }
            },
            geometry = geometry,
        ) {
            itemContent(it)
        }
    }

    private fun selectHandler(
        index: Int,
        isSelected: Boolean,
    ): Boolean {
        if (!isSelected && index == coverFlowState.selectedIndex) {
            coverFlowState.selectedIndex = -1
        } else if (isSelected && index != coverFlowState.selectedIndex) {
            coverFlowState.selectedIndex = index
            return true
        }

        return false
    }
}
