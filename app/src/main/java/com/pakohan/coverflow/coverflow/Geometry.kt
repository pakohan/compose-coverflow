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
    internal val params: CoverFlowParams = CoverFlowParams(),
    private val size: IntSize = IntSize.Zero,
) {
    @Stable
    private val shortEdge
        get() = min(
            size.width,
            size.height
        )

    @Stable
    private val containerCenter
        get() = size.width / 2

    @Stable
    private val zoomDelta
        get() = 1 - params.zoom

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
    internal val spacerHeight
        get() = coverOffset / 2

    @Stable
    internal fun isSelected(horizontalPosition: Float): Boolean {
        return distanceToCenter(horizontalPosition).toInt() == 0
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
        return -params.angle * effectFactor(distanceToCenter)
    }

    @Stable
    internal fun scale(distanceToCenter: Float): Float {
        return 1 - zoomDelta * abs(effectFactor(distanceToCenter))
    }

    @Stable
    internal fun translationX(distanceToCenter: Float): Float {
        return coverOffset * params.horizontalShift * effectFactor(distanceToCenter)
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
    val offset: Float = .4f,
    val distanceFactor: @RawValue DistanceFactor = OffsetLinearDistanceFactor(),
    val angle: Float = 55f,
    val horizontalShift: Float = .4f,
    val zoom: Float = .8f,
    val mirror: Boolean = true,
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
        mirror = false
    )
}
