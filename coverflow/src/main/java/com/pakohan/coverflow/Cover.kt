package com.pakohan.coverflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity

@Composable
internal fun Cover(
    geometry: Geometry,
    onSelectedHandler: (Boolean) -> Unit,
    content: @Composable CoverContext.() -> Unit,
) {
    var horizontalPosition by remember { mutableStateOf<Float?>(null) }
    val distanceToCenter = geometry.distanceToCenter(horizontalPosition ?: 0f)

    // This box is used to space the elements horizontally and to give them the proper z index.
    Box(
        modifier = Modifier
            .width(with(LocalDensity.current) { geometry.coverOffset.toDp() })
            .onGloballyPositioned { coordinates ->
                horizontalPosition = coordinates.positionInParent().x
            }
            .calculatedZIndex(geometry),
        contentAlignment = Alignment.Center,
    ) {
        // this makes sure that the cover only gets painted after we got the proper horizontal position.
        if (horizontalPosition != null) {

            // we use geometry to check whether an element is selected. That is if it's in the
            // center.
            onSelectedHandler(geometry.isSelected(horizontalPosition ?: 0f))

            CoverContext(distanceToCenter).content()
        }
    }
}

internal data class CoverContext(val distanceToCenter: Float)

@Composable
internal fun InnerBox(
    geometry: Geometry,
    distanceToCenter: Float,
    onClickHandler: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .requiredSize(with(LocalDensity.current) { geometry.coverSize.toDp() })
            .graphicsLayer(
                rotationY = geometry.rotation(distanceToCenter),
                translationX = geometry.translationX(distanceToCenter),
                scaleY = geometry.scale(distanceToCenter),
                scaleX = geometry.scale(distanceToCenter),
            ),
        propagateMinConstraints = true,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        if (geometry.params.mirror) {
            Mirror(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClickHandler,
                ),
                coverSize = geometry.coverSize,
            ) {
                content()
            }
        } else {
            Surface(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClickHandler,
                ),
            ) {
                content()
            }
        }
    }
}
