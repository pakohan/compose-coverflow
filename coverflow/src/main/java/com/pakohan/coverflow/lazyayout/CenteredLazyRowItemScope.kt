package com.pakohan.coverflow.lazyayout

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

interface CenteredLazyRowItemScope {
    fun Modifier.coverGraphicsLayer(block: GraphicsLayerScope.(Int) -> Unit): Modifier
    fun Modifier.mirrorGraphicsLayer(block: GraphicsLayerScope.(Int) -> Unit): Modifier
}

class CenteredLazyRowItemScopeImpl(private val distanceToCenter: Int) : CenteredLazyRowItemScope {
    override fun Modifier.coverGraphicsLayer(block: GraphicsLayerScope.(Int) -> Unit): Modifier =
        this.graphicsLayer { block(distanceToCenter) }

    override fun Modifier.mirrorGraphicsLayer(block: GraphicsLayerScope.(Int) -> Unit): Modifier = this.graphicsLayer {
        transformOrigin = TransformOrigin(
            .5f,
            -.5f,
        )
        block(distanceToCenter)
    }
}
