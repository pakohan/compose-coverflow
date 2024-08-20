package com.pakohan.coverflow.lazyayout

interface CustomLazyListScope {
    fun items(
        amount: Int,
        itemContent: ItemFunc,
    )

    fun <T> items(
        items: List<T>,
        itemContent: ParameterItemFunc<T>,
    )
}
