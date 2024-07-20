package com.pakohan.coverflow.coverflow

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CoverFlowSettings(
    modifier: Modifier = Modifier,
    params: CoverFlowParams,
    onParamsUpdate: (CoverFlowParams) -> Unit,
) {
    Column(modifier = modifier) {
        Text("Size: ${params.size}")
        Slider(
            value = params.size,
            onValueChange = { onParamsUpdate(params.copy(size = it)) },
            steps = 100
        )

        Text("Offset: ${params.offset}")
        Slider(
            value = params.offset,
            onValueChange = { onParamsUpdate(params.copy(offset = it)) },
            steps = 100
        )

        Text("Angle: ${params.angle}")
        Slider(
            value = params.angle,
            onValueChange = { onParamsUpdate(params.copy(angle = it)) },
            steps = 90,
            valueRange = 0f..90f
        )

        Text("Horizontal shift: ${params.horizontalShift}")
        Slider(
            value = params.horizontalShift,
            onValueChange = { onParamsUpdate(params.copy(horizontalShift = it)) },
            steps = 100,
            valueRange = 0f..1f
        )

        Text("Zoom: ${params.zoom}")
        Slider(
            value = params.zoom,
            onValueChange = { onParamsUpdate(params.copy(zoom = it)) },
            steps = 100,
            valueRange = 0f..1f
        )
    }
}
