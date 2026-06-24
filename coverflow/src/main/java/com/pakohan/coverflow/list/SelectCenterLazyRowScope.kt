package com.pakohan.coverflow.list

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

/**
 * Receiver scope to add elements to the CoverFlow list.
 */
interface SelectCenterLazyRowScope {
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

internal class SelectCenterLazyRowScopeImpl(
    private val lazyListScope: LazyListScope,
) : SelectCenterLazyRowScope {
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
        itemContent(it)
    }

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
}
