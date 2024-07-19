package com.pakohan.coverflow.coverflow

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize
import kotlin.math.abs
import kotlin.math.min

@Immutable
@Stable
class CoverPainter(
    private val params: CoverFlowParams = CoverFlowParams(),
    private val size: IntSize = IntSize.Zero,
) {
    @Stable
    val mainCoverSize
        get() = (min(
            size.width,
            size.height
        ) * params.selectedElementHeightRatio).toInt()

    @Stable
    val coverOffset
        get() = (mainCoverSize * params.elementsOffsetHeightFactor).toInt()

    @Stable
    val spacerWidth
        get() = containerCenter - coverOffset / 2

    @Stable
    private val containerCenter
        get() = size.width / 2

    @Stable
    private val distanceBreakpoint
        get() = coverOffset.toFloat() * params.distanceBreakpointRatio

    @Stable
    fun isSelected(horizontalPosition: Float): Boolean {
        return (calculateDistanceToCenter(horizontalPosition) * 100).toInt() == 0
    }

    @Stable
    fun calculateDistanceToCenter(horizontalPosition: Float): Float {
        return horizontalPosition + coverOffset * .5f - containerCenter
    }

    @Stable
    fun rotation(distanceToCenter: Float): Float {
        return calculateDistanceToCenterFactor(distanceToCenter) * params.maxAngleDegrees
    }

    @Stable
    fun zoom(distanceToCenter: Float): Float {
        return 1f + params.selectedElementAdditionalScale * (1 - abs(calculateDistanceToCenterFactor(distanceToCenter)))
    }

    @Stable
    fun translationX(distanceToCenter: Float): Float {
        return -calculateDistanceToCenterFactor(distanceToCenter) * coverOffset * params.horizontalTranslationRatio
    }

    @Stable
    private fun calculateDistanceToCenterFactor(
        relative: Float,
    ): Float {
        return if (relative < -distanceBreakpoint) {
            1f
        } else if (relative > distanceBreakpoint) {
            -1f
        } else {
            -relative / distanceBreakpoint
        }
    }
}

@Immutable
@Parcelize
data class CoverFlowParams(
    val maxAngleDegrees: Float = 55f,
    val selectedElementAdditionalScale: Float = .3f,
    val selectedElementHeightRatio: Float = .5f,
    val elementsOffsetHeightFactor: Float = .5f,
    val distanceBreakpointRatio: Float = 1f,
    val horizontalTranslationRatio: Float = .5f,
) : Parcelable

fun debugCoverFlowParams(): CoverFlowParams {
    return CoverFlowParams(
        maxAngleDegrees = 0f,
        selectedElementAdditionalScale = 0f,
        selectedElementHeightRatio = .5f,
        elementsOffsetHeightFactor = 1f,
        distanceBreakpointRatio = 1f,
        horizontalTranslationRatio = 0f,
    )
}
