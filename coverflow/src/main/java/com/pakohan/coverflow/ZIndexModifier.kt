package com.pakohan.coverflow

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlin.math.abs

@Stable
internal fun Modifier.calculatedZIndex(
    geometry: Geometry,
    onSelectedHandler: (Boolean) -> Unit,
): Modifier = composed(
    factory = {
        val floatState = remember {
            mutableFloatStateOf(0f)
        }
        this then CalculatedZIndexElement(
            geometry = geometry,
            onSelectedHandler = onSelectedHandler,
            floatState = floatState,
        )
    },
)

internal data class CalculatedZIndexElement(
    private val geometry: Geometry,
    private val onSelectedHandler: (Boolean) -> Unit,
    private val floatState: MutableFloatState,
) : ModifierNodeElement<CalculatedZIndexNode>() {
    override fun create() = CalculatedZIndexNode(
        geometry,
        onSelectedHandler,
        floatState,
    )

    override fun update(node: CalculatedZIndexNode) {
        node.geometry = geometry
        node.onSelectedHandler = onSelectedHandler
        node.xPositionInParent = floatState
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "zIndex"
        properties["geometry"] = geometry
    }
}

internal class CalculatedZIndexNode(
    var geometry: Geometry,
    var onSelectedHandler: (Boolean) -> Unit,
    var xPositionInParent: MutableFloatState,
) : LayoutModifierNode,
        GlobalPositionAwareModifierNode,
        Modifier.Node() {

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        xPositionInParent.floatValue = coordinates.positionInParent().x
        // we use geometry to check whether an element is selected. That is if it's in the center.
        onSelectedHandler(geometry.isSelected(coordinates.positionInParent().x))
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(
            placeable.width,
            placeable.height,
        ) {
            placeable.place(
                0,
                0,
                zIndex = 1f - abs(geometry.distanceToCenter(xPositionInParent.floatValue)),
            )
        }
    }
}
