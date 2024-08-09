package com.pakohan.coverflow_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pakohan.coverflow.CoverFlowParams
import com.pakohan.coverflow.OffsetLinearDistanceFactor
import kotlin.math.round

@Composable
fun CoverFlowSettings(
    modifier: Modifier = Modifier,
    params: CoverFlowParams,
    onParamsUpdate: (CoverFlowParams) -> Unit,
) {
    var distanceRange by remember {
        mutableStateOf(.0f..0.5f)
    }

    Column(modifier = modifier) {
        Text("Size: ${params.size.round(2)}")
        Slider(
            value = params.size,
            onValueChange = { onParamsUpdate(params.copy(size = it)) },
            steps = 100,
        )

        Text("Offset: ${params.offset.round(2)}")
        Slider(
            value = params.offset,
            onValueChange = { onParamsUpdate(params.copy(offset = it)) },
            steps = 100,
        )

        Text("Distance: $distanceRange")
        RangeSlider(
            value = distanceRange,
            onValueChange = {
                distanceRange = it
                onParamsUpdate(
                    params.copy(
                        distanceFactor = OffsetLinearDistanceFactor(
                            distanceRange.start,
                            distanceRange.endInclusive,
                        ),
                    ),
                )
            },
            steps = 100,
            valueRange = 0f..2f,
        )

        Text("Angle: ${params.angle.round(2)}")
        Slider(
            value = params.angle,
            onValueChange = { onParamsUpdate(params.copy(angle = it)) },
            steps = 90,
            valueRange = 0f..90f,
        )

        Text("Horizontal shift: ${params.shift.round(2)}")
        Slider(
            value = params.shift,
            onValueChange = { onParamsUpdate(params.copy(shift = it)) },
            steps = 100,
        )

        Text("Zoom: ${params.zoom.round(2)}")
        Slider(
            value = params.zoom,
            onValueChange = { onParamsUpdate(params.copy(zoom = it)) },
            steps = 100,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Mirror:")
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(
                checked = params.mirror,
                onCheckedChange = { onParamsUpdate(params.copy(mirror = it)) },
            )
        }
    }
}

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (round(this * multiplier) / multiplier).toFloat()
}
