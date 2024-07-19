package com.pakohan.coverflow.coverflow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    painter: CoverPainter,
    onClickHandler: () -> Unit = {},
    onSelectedHandler: () -> Unit,
    content: @Composable () -> Unit,
) {
    var horizontalPosition by remember {
        mutableFloatStateOf(0f)
    }
    val distanceToCenter = painter.calculateDistanceToCenter(horizontalPosition)

    Box(modifier = Modifier
        .size(with(LocalDensity.current) { painter.coverOffset.toDp() })
        .zIndex(1f - abs(distanceToCenter))
        .onGloballyPositioned { coordinates ->
            horizontalPosition = coordinates.positionInParent().x
        }) {

        if (painter.isSelected(horizontalPosition)) {
            onSelectedHandler()
        }

        Surface(
            modifier = Modifier
                .requiredSize(with(LocalDensity.current) { painter.mainCoverSize.toDp() })
                .graphicsLayer(
                    rotationY = painter.rotation(distanceToCenter),
                    scaleY = painter.zoom(distanceToCenter),
                    scaleX = painter.zoom(distanceToCenter),
                    translationX = painter.translationX(distanceToCenter),
                ),
            onClick = onClickHandler
        ) {
            content()
        }
    }
}
