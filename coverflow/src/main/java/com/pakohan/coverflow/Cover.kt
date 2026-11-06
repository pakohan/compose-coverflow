package com.pakohan.coverflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.abs

/**
 * A single cover.
 *
 * The position-dependent effects are NOT computed during composition. Scrolling changes the
 * position every frame, so reading it in composition would recompose every visible cover on every
 * frame. Instead, the position is read from [CoverFlowState] inside the placement block (for the
 * z-index) and inside the [graphicsLayer] block (for rotation, translation, and scale). Compose
 * then only re-runs placement and drawing while scrolling, and the effects are applied in the
 * same frame as the scroll instead of one frame later.
 */
@Composable
internal fun Cover(
    geometry: Geometry,
    state: CoverFlowState,
    lazyIndex: Int,
    onClickHandler: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    // Distance of this cover's center to the container's center, or null if it isn't laid out.
    // Only call this from the layout or draw phase.
    fun distanceToCenter(): Float? = state.visibleItemInfo(lazyIndex)
        ?.let { geometry.distanceToCenter(it.offset.toFloat()) }

    // This box is used to space the elements horizontally and to give them the proper z index.
    Box(
        modifier = Modifier
            .width(with(LocalDensity.current) { geometry.coverOffset.toDp() })
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    // Covers closer to the center are drawn on top.
                    val distance = distanceToCenter() ?: 0f
                    placeable.place(0, 0, zIndex = 1f - abs(distance))
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // This Box applies the transformation effects: rotation, horizontal shift, and zoom
        Box(
            modifier = Modifier
                .requiredSize(with(LocalDensity.current) { geometry.coverSize.toDp() })
                .graphicsLayer {
                    val distance = distanceToCenter()
                    if (distance == null) {
                        // Not laid out yet, don't draw it with wrong transformations.
                        alpha = 0f
                        return@graphicsLayer
                    }
                    alpha = 1f
                    rotationY = geometry.rotation(distance)
                    translationX = geometry.translationX(distance)
                    scaleX = geometry.scale(distance)
                    scaleY = geometry.scale(distance)
                },
            propagateMinConstraints = true,
        ) {
            val clickModifier = Modifier.clickable(
                interactionSource = null,
                indication = null,
                onClick = onClickHandler,
            )
            if (geometry.params.mirror) {
                Mirror(
                    modifier = clickModifier,
                    coverSize = geometry.coverSize,
                ) {
                    content()
                }
            } else {
                Surface(modifier = clickModifier) {
                    content()
                }
            }
        }
    }
}
