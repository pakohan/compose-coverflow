package com.pakohan.coverflow

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

/**
 * DistanceFactor returns how strong the three effects angle, horizontal translation, and zoom
 * get applied to each element in the view.
 */
interface DistanceFactor {
    /**
     * factor should return negative values for all elements left of the center and positive for all
     * elements right of the center.
     * * It is supposed to be 0 in the center.
     * * It can take values from -1 to 1.
     * * (`factor in -1f..1f`)
     * @param relativeDistance the center to the distance relative to focused covers size.
     *
     * this methods name has a great fun-factor
     */
    fun factor(
        relativeDistance: Float,
    ): Float
}

/**
 * DistanceFactor based on two values.
 * @param start determines from which distance the factor starts to increase.
 * @param end determines from which distance the factor is 1f.
 */
@Parcelize
class OffsetLinearDistanceFactor(
    private val start: Float = 0f,
    private val end: Float = .5f,
) : DistanceFactor,
        Parcelable {

    init {
        if (start < 0f || end < 0f) {
            throw IllegalArgumentException("start ($start) and end ($end) must not be negative")
        } else if (start > end) {
            throw IllegalArgumentException("start ($start) must be smaller than end ($end)")
        }
    }

    /**
     * * `relativeDistance < -end -> -1f`
     * * `relativeDistance in -end..-start -> -1f..0f`
     * * `relativeDistance in -start..start -> 0f`
     * * `relativeDistance in start..end -> 0f..1f`
     * * `relativeDistance > end -> 1f`
     * * `if abs(relativeDistance) in start..end` the factor grows linear.
     */
    override fun factor(
        relativeDistance: Float,
    ): Float {
        // it's easier to only work with positive values (aka distances that are right of the center.
        val absolute = abs(relativeDistance)
        var factor = if (absolute <= start) {
            0f
        } else if (absolute >= end) {
            1f
        } else { // division by 0 not possible, since we don't enter if start == end
            val intervalStep = absolute - start
            val delta = end - start
            intervalStep / delta
        }

        if (relativeDistance < 0) {
            factor = -factor
        }

        return factor
    }
}

/**
 * mainly for debugging purposes
 */
@Parcelize
class StaticDistanceFactor(private val factor: Float = 0f) : DistanceFactor,
        Parcelable {
    override fun factor(
        relativeDistance: Float,
    ): Float {
        return if (relativeDistance < 0) {
            -factor
        } else if (relativeDistance > 0) {
            factor
        } else {
            0f
        }
    }
}
