package com.pakohan.coverflow.coverflow

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

class CoverFlowScope(
    internal val painter: Geometry,
    internal val lazyListScope: LazyListScope,
    internal val onClickHandler: (index: Int) -> Unit,
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
            onClickHandler = { onClickHandler(it) },
            onSelectedHandler = { onSelectHandler(items[it]) },
            painter = painter,
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
            onClickHandler = { onClickHandler(it) },
            onSelectedHandler = { onSelectHandler(it) },
            painter = painter,
        ) {
            itemContent(it)
        }
    }
}
