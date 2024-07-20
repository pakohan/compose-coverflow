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
class CoverPainter(
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
    val coverSize
        get() = shortEdge * params.size

    @Stable
    val coverOffset
        get() = (coverSize * params.offset).toInt()

    @Stable
    val spacerWidth
        get() = containerCenter - coverOffset / 2

    @Stable
    private val containerCenter
        get() = size.width / 2

    @Stable
    private val zoomDelta
        get() = 1 - params.zoom

    @Stable
    fun isSelected(horizontalPosition: Float): Boolean {
        return (distanceToCenter(horizontalPosition) * 100).toInt() == 0
    }

    @Stable
    fun distanceToCenter(horizontalPosition: Float): Float {
        return horizontalPosition + coverOffset * .5f - containerCenter
    }

    @Stable
    fun effectFactor(distanceToCenter: Float): Float {
        return params.distanceFactor.calculateFactor(
            coverSize,
            distanceToCenter
        )
    }

    @Stable
    fun rotation(distanceToCenter: Float): Float {
        return -effectFactor(distanceToCenter) * params.angle
    }

    @Stable
    fun scale(distanceToCenter: Float): Float {
        return 1 - zoomDelta * abs(
            effectFactor(distanceToCenter)
        )
    }

    @Stable
    fun translationX(distanceToCenter: Float): Float {
        return effectFactor(distanceToCenter) * coverOffset * params.horizontalShift
    }
}

/**
 * params for configuring the coverflow composable.
 * all relevant values are relativ to the dimensions of the CoverFlow composable.
 * This makes sure scaling doesn't destroy how it looks like.
 * All calculations are derived in the following steps:
 *  1. Determine the shorter edge of the CoverFlow composable box.
 *  2. Multiply it with the coverSizeRatio. This gives us the size of the cover in focus.
 */
@Immutable
@Parcelize
data class CoverFlowParams(
    val size: Float = .5f,
    val offset: Float = .5f,
    val angle: Float = 55f,
    val horizontalShift: Float = .5f,
    val zoom: Float = .8f,
    val distanceFactor: @RawValue DistanceFactor = OffsetLinearDistanceFactor(),
) : Parcelable

fun debugCoverFlowParams(): CoverFlowParams {
    return CoverFlowParams(
        size = .5f,
        offset = 1f,
        angle = 0f,
        zoom = 1f,
        horizontalShift = 0f,
    )
}
