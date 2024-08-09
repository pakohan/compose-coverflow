package com.pakohan.coverflow

import org.junit.Assert.assertEquals
import org.junit.Test

class OffsetLinearDistanceFactorTest {
    @Test
    fun factor_fromHalfToOne_success() {
        val ldf = OffsetLinearDistanceFactor(
            .5f,
            1f,
        )
        val cases = mapOf(
            -1f to -1f,
            -.75f to -.5f,
            -.5f to -0f,
            0f to 0f,
            .5f to 0f,
            .75f to .5f,
            1f to 1f,
        )

        cases.forEach { (input, output) ->
            assertEquals(
                ldf.factor(input),
                output,
            )
        }
    }
}
