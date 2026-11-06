package com.pakohan.coverflow

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlin.math.abs
import kotlin.math.min

/**
 * Params for configuring the coverflow composable.
 *
 * All relevant values are relativ to the dimensions of the CoverFlow composable to make it responsive.
 * It is based on the shorter of the two sides of the outer component.
 *
 * @param size is multiplied with that edge to get the cover size in the middle.
 * @param offset is multiplied with that edge to get the distance between two covers in the list.
 * @param distanceFactor calculates how strong the transformations are applied to covers depending
 * on their distance to the center.
 * @param angle is the maximum rotation angle.
 * @param shift is the horizontal shift of the items away from the center. Useful for giving the
 * center element more space.
 * @param zoom makes the side elements smaller.
 * @param mirror acitvates the mirror effect.
 */
@Immutable
@Parcelize
data class CoverFlowParams(
    val size: Float = .5f,
    val offset: Float = .4f,
    val distanceFactor: @RawValue DistanceFactor = OffsetLinearDistanceFactor(),
    val angle: Float = 55f,
    val shift: Float = .4f,
    val zoom: Float = .8f,
    val mirror: Boolean = true,
) : Parcelable

/**
 * Pure math: converts [CoverFlowParams] and the container size into pixel values.
 *
 * @Immutable tells the Compose compiler that instances never change after construction, so
 * composables taking a Geometry can be skipped when the same instance is passed again. (@Stable on
 * individual private getters and functions had no effect and was removed.)
 */
@Immutable
internal class Geometry(
    internal val params: CoverFlowParams = CoverFlowParams(),
    private val size: IntSize = IntSize.Zero,
) {
    private val shortEdge
        get() = min(
            size.width,
            size.height,
        )

    private val containerCenter
        get() = size.width / 2

    private val zoomDelta
        get() = 1 - params.zoom

    internal val coverSize
        get() = shortEdge * params.size

    internal val coverOffset
        get() = (coverSize * params.offset).toInt()

    internal val spacerWidth
        get() = containerCenter - coverOffset / 2

    internal fun distanceToCenter(horizontalPosition: Float): Float {
        return horizontalPosition + coverOffset * .5f - containerCenter
    }

    internal fun effectFactor(distanceToCenter: Float): Float {
        return params.distanceFactor.factor(
            distanceToCenter / coverSize,
        )
    }

    internal fun rotation(distanceToCenter: Float): Float {
        return -params.angle * effectFactor(distanceToCenter)
    }

    internal fun scale(distanceToCenter: Float): Float {
        return 1 - zoomDelta * abs(effectFactor(distanceToCenter))
    }

    internal fun translationX(distanceToCenter: Float): Float {
        return coverOffset * params.shift * effectFactor(distanceToCenter)
    }
}
