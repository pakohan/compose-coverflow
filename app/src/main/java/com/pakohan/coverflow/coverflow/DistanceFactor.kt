package com.pakohan.coverflow.coverflow

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

interface DistanceFactor {
    fun calculateFactor(
        coverSize: Float,
        distance: Float,
    ): Float
}

@Parcelize
class OffsetLinearDistanceFactor(private val distanceBreakpointRatio: Float = .5f) : DistanceFactor,
        Parcelable {
    override fun calculateFactor(
        coverSize: Float,
        distance: Float,
    ): Float {
        return if (distance < -distanceBreakpoint(coverSize)) {
            -1f
        } else if (distance > distanceBreakpoint(coverSize)) {
            1f
        } else {
            distance / distanceBreakpoint(coverSize)
        }
    }

    @Stable
    private fun distanceBreakpoint(coverSize: Float): Float {
        return coverSize * distanceBreakpointRatio
    }
}
