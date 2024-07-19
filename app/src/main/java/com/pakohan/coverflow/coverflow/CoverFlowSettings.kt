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
        Text("height ratio: ${params.selectedElementHeightRatio}")
        Slider(
            value = params.selectedElementHeightRatio,
            onValueChange = { onParamsUpdate(params.copy(selectedElementHeightRatio = it)) },
            steps = 100
        )

        Text("angle: ${params.maxAngleDegrees}")
        Slider(
            value = params.maxAngleDegrees,
            onValueChange = { onParamsUpdate(params.copy(maxAngleDegrees = it)) },
            steps = 90,
            valueRange = 0f..90f
        )

        Text("zoom factor: ${params.selectedElementAdditionalScale}")
        Slider(
            value = params.selectedElementAdditionalScale,
            onValueChange = { onParamsUpdate(params.copy(selectedElementAdditionalScale = it)) },
            steps = 100,
            valueRange = 0f..3f
        )

        Text("offset factor: ${params.elementsOffsetHeightFactor}")
        Slider(
            value = params.elementsOffsetHeightFactor,
            onValueChange = { onParamsUpdate(params.copy(elementsOffsetHeightFactor = it)) },
            steps = 100
        )

        Text("breakpoint ratio: ${params.distanceBreakpointRatio}")
        Slider(
            value = params.distanceBreakpointRatio,
            onValueChange = { onParamsUpdate(params.copy(distanceBreakpointRatio = it)) },
            steps = 100,
            valueRange = .2f..5f
        )

        Text("translation ratio: ${params.horizontalTranslationRatio}")
        Slider(
            value = params.horizontalTranslationRatio,
            onValueChange = { onParamsUpdate(params.copy(horizontalTranslationRatio = it)) },
            steps = 100,
            valueRange = 0f..100f
        )
    }
}
