package com.pakohan.coverflow.coverflow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import kotlin.math.abs

@Composable
internal fun Cover(
    painter: Geometry,
    onClickHandler: () -> Unit = {},
    onSelectedHandler: () -> Unit,
    content: @Composable () -> Unit,
) {
    var horizontalPosition by remember {
        mutableStateOf<Float?>(null)
    }

    val distanceToCenter = painter.distanceToCenter(horizontalPosition ?: 0f)
    Box(modifier = Modifier
        .size(with(LocalDensity.current) { painter.coverOffset.toDp() })
        .zIndex(1f - abs(distanceToCenter))
        .onGloballyPositioned { coordinates ->
            horizontalPosition = coordinates.positionInParent().x
        }) {
        // this makes sure that the cover only gets painted after we got the proper horizontal position.
        if (horizontalPosition != null) {
            if (painter.isSelected(horizontalPosition ?: 0f)) {
                onSelectedHandler()
            }

            Surface(
                modifier = Modifier
                    .requiredSize(with(LocalDensity.current) { painter.coverSize.toDp() })
                    .graphicsLayer(
                        rotationY = painter.rotation(distanceToCenter),
                        translationX = painter.translationX(distanceToCenter),
                        scaleY = painter.scale(distanceToCenter),
                        scaleX = painter.scale(distanceToCenter),
                    ),
                onClick = onClickHandler
            ) {
                content()
            }
        }
    }
}
