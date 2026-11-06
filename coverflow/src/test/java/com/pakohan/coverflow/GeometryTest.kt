package com.pakohan.coverflow

import androidx.compose.ui.unit.IntSize
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * With the default [CoverFlowParams] in a 1000x2000 px container:
 * - short edge = 1000, cover size = 1000 * .5 = 500, cover offset = 500 * .4 = 200
 * - container center = 500, spacer width = 500 - 200 / 2 = 400
 * - the default distance factor reaches its maximum at half a cover size (250 px) from the center
 */
class GeometryTest {
    private val geometry = Geometry(
        params = CoverFlowParams(),
        size = IntSize(1000, 2000),
    )

    @Test
    fun sizes_areRelativeToShortEdge() {
        assertEquals(500f, geometry.coverSize, DELTA)
        assertEquals(200, geometry.coverOffset)
        assertEquals(400, geometry.spacerWidth)
    }

    @Test
    fun sizes_useShortEdgeInLandscape() {
        val landscape = Geometry(CoverFlowParams(), IntSize(2000, 1000))
        assertEquals(500f, landscape.coverSize, DELTA)
        assertEquals(1000 - 100, landscape.spacerWidth)
    }

    @Test
    fun distanceToCenter_isZeroForCoverRightAfterSpacer() {
        // The first cover starts right after the spacer and is centered when not scrolled.
        assertEquals(0f, geometry.distanceToCenter(geometry.spacerWidth.toFloat()), DELTA)
        assertEquals(200f, geometry.distanceToCenter(600f), DELTA)
        assertEquals(-200f, geometry.distanceToCenter(200f), DELTA)
    }

    @Test
    fun centeredCover_hasNoEffects() {
        assertEquals(0f, geometry.rotation(0f), DELTA)
        assertEquals(1f, geometry.scale(0f), DELTA)
        assertEquals(0f, geometry.translationX(0f), DELTA)
    }

    @Test
    fun coverHalfwayToFullEffect_hasHalfTheEffects() {
        // 125 px = a quarter cover size -> factor .5
        assertEquals(-27.5f, geometry.rotation(125f), DELTA)
        assertEquals(.9f, geometry.scale(125f), DELTA)
        assertEquals(40f, geometry.translationX(125f), DELTA)
    }

    @Test
    fun coversFarAway_haveFullEffects_mirroredBySide() {
        for (distance in listOf(250f, 1000f)) {
            // right of the center: rotated towards the center, shifted to the right
            assertEquals(-55f, geometry.rotation(distance), DELTA)
            assertEquals(.8f, geometry.scale(distance), DELTA)
            assertEquals(80f, geometry.translationX(distance), DELTA)

            // left of the center: mirrored rotation and shift, same zoom
            assertEquals(55f, geometry.rotation(-distance), DELTA)
            assertEquals(.8f, geometry.scale(-distance), DELTA)
            assertEquals(-80f, geometry.translationX(-distance), DELTA)
        }
    }

    private companion object {
        const val DELTA = 1e-4f
    }
}
