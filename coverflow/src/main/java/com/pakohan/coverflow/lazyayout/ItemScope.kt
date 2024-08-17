package com.pakohan.coverflow.lazyayout

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer

interface ItemScope {
    fun Modifier.coverGraphicsLayer(block: GraphicsLayerScope.(Int) -> Unit): Modifier
}

class ItemScopeImpl(private val distanceToCenter: Int) : ItemScope {

    override fun Modifier.coverGraphicsLayer(block: GraphicsLayerScope.(Int) -> Unit): Modifier =
        this.graphicsLayer { block(distanceToCenter) }
}
