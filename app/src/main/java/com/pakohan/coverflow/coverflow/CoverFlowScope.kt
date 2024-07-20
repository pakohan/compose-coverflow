package com.pakohan.coverflow.coverflow

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

class CoverFlowScope(
    internal val geometry: Geometry,
    internal val lazyListScope: LazyListScope,
    internal val coverFlowState: CoverFlowState,
) {
    fun <T> items(
        items: List<T>,
        onSelectHandler: (item: T) -> Unit = {},
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyItemScope.(item: T) -> Unit,
    ) = lazyListScope.items(
        items.size,
        key,
        contentType
    ) {
        Cover(
            onClickHandler = { coverFlowState.scrollToItem(it) },
            onSelectedHandler = { isSelected: Boolean ->
                if (selectHandler(
                        it,
                        isSelected
                    )
                ) {
                    onSelectHandler(items[it])
                }
            },
            geometry = geometry,
        ) {
            itemContent(items[it])
        }
    }

    fun items(
        count: Int,
        onSelectHandler: (index: Int) -> Unit = {},
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    ) = lazyListScope.items(
        count,
        key,
        contentType
    ) {
        Cover(
            onClickHandler = { coverFlowState.scrollToItem(it) },
            onSelectedHandler = { isSelected: Boolean ->
                if (selectHandler(
                        it,
                        isSelected
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
