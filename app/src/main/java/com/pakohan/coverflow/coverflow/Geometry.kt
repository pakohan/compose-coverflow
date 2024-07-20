package com.pakohan.coverflow.coverflow

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlin.math.abs
import kotlin.math.min

@Immutable
@Stable
class Geometry(
    private val params: CoverFlowParams = CoverFlowParams(),
    private val size: IntSize = IntSize.Zero,
) {
    @Stable
    private val shortEdge
        get() = min(
            size.width,
            size.height
        )

    @Stable
    internal val coverSize
        get() = shortEdge * params.size

    @Stable
    internal val coverOffset
        get() = (coverSize * params.offset).toInt()

    @Stable
    internal val spacerWidth
        get() = containerCenter - coverOffset / 2

    @Stable
    private val containerCenter
        get() = size.width / 2

    @Stable
    private val zoomDelta
        get() = 1 - params.zoom

    @Stable
    internal fun isSelected(horizontalPosition: Float): Boolean {
        return (distanceToCenter(horizontalPosition) * 100).toInt() == 0
    }

    @Stable
    internal fun distanceToCenter(horizontalPosition: Float): Float {
        return horizontalPosition + coverOffset * .5f - containerCenter
    }

    @Stable
    internal fun effectFactor(distanceToCenter: Float): Float {
        return params.distanceFactor.factor(
            distanceToCenter / coverSize
        )
    }

    @Stable
    internal fun rotation(distanceToCenter: Float): Float {
        return -effectFactor(distanceToCenter) * params.angle
    }

    @Stable
    internal fun scale(distanceToCenter: Float): Float {
        return 1 - zoomDelta * abs(
            effectFactor(distanceToCenter)
        )
    }

    @Stable
    internal fun translationX(distanceToCenter: Float): Float {
        return effectFactor(distanceToCenter) * coverOffset * params.horizontalShift
    }
}

/**
 * params for configuring the coverflow composable.
 * all relevant values are relativ to the dimensions of the CoverFlow composable.
 * This makes sure scaling doesn't destroy how it looks like.
 * All calculations are derived in the following steps:
 *  1. Determine the shorter edge of the CoverFlow composable box.
 *  2. Use DistanceFactor to
 */
@Immutable
@Parcelize
data class CoverFlowParams(
    val size: Float = .5f,
    val offset: Float = .5f,
    val distanceFactor: @RawValue DistanceFactor = OffsetLinearDistanceFactor(),
    val angle: Float = 55f,
    val horizontalShift: Float = .5f,
    val zoom: Float = .8f,
) : Parcelable

/**
 * For debugging. Sets the Parameters to values so that we get a normal LazyList without any
 * CoverFlow effects.
 */
fun debugCoverFlowParams(): CoverFlowParams {
    return CoverFlowParams(
        size = .5f,
        offset = 1f,
        angle = 0f,
        horizontalShift = 0f,
        zoom = 1f,
    )
}