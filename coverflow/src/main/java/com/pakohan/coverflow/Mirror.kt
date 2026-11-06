package com.pakohan.coverflow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@Composable
internal fun Mirror(
    modifier: Modifier = Modifier,
    coverSize: Float,
    content: @Composable () -> Unit,
) {
    val coverSizeDp = with(LocalDensity.current) { coverSize.toDp() }

    // The content is composed only once. Its drawing is recorded into this layer, and the mirror
    // draws the same layer a second time. Composing content() twice would duplicate its state and
    // side effects (e.g. loading an image twice).
    val contentLayer = rememberGraphicsLayer()

    Column(
        modifier = modifier
            .padding(top = coverSizeDp) // necessary to push the mirrored immage down and keep the real one centered
            .fillMaxWidth()
            .requiredHeight(2 * coverSizeDp), // original + mirror image
    ) {
        Box(
            propagateMinConstraints = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(coverSizeDp)
                .drawWithContent {
                    contentLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(contentLayer)
                },
        ) {
            content()
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverSizeDp)
                .graphicsLayer(
                    compositingStrategy = CompositingStrategy.Offscreen,
                    rotationX = 180f,
                )
                .clip(HalfSizeShape)
                .drawWithContent {
                    val colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Black,
                    )
                    drawContent()
                    drawRect(
                        topLeft = Offset(
                            0f,
                            this.size.height / 2,
                        ),
                        size = Size(
                            this.size.width,
                            this.size.height / 2,
                        ),
                        brush = Brush.verticalGradient(colors),
                        blendMode = BlendMode.DstIn,
                    )
                }
                .blur(
                    radiusX = 1.dp,
                    radiusY = 3.dp,
                )
                // draws the recorded content (where the second content() used to be composed)
                .drawBehind { drawLayer(contentLayer) },
        )
    }
}

private object HalfSizeShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = Outline.Rectangle(
        Rect(
            Offset(
                0f,
                size.height / 2,
            ),
            Size(
                size.width,
                size.height,
            ),
        ),
    )
}
