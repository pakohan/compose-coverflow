package com.pakohan.coverflow_app

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pakohan.coverflow.CoverFlow
import com.pakohan.coverflow.CoverFlowParams
import com.pakohan.coverflow.OffsetLinearDistanceFactor
import com.pakohan.coverflow.lazyayout.CenteredLazyRow
import com.pakohan.coverflow.lazyayout.rememberCenteredLazyRowState
import com.pakohan.coverflow.rememberCoverFlowState

// This is for playing around with the parameters
@Composable
fun CoverFlowScreen(
    modifier: Modifier = Modifier,
    showSettings: Boolean,
) {
    var params by rememberSaveable { mutableStateOf(CoverFlowParams()) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(visible = showSettings) {
            CoverFlowSettings(
                params = params,
                onParamsUpdate = { params = it },
            )
        }

        val items = listOf(
            "This is a simple Compose CoverFlow implementation",
            "Apple patented it, but the patent expired in 2024",
            "This component is based on LazyRow, which makes it efficient",
            "It's customizable, but comes with good standard options, so it's easy to use",
            "It also keeps its layout when scaled, since all configuration options are relative to the container size",
            "It measures the container and then uses the shorter edge of it",
            "The first param is the size factor: it's multiplied with the short edge to get the cover size",
            "All other Parameters are relative to the cover size",
            "Offset is how much space is between each element",
            "Then there are three effects being applied to the elements on the side: angle, horizontal shift, and zoom",
            "The effect is added gradually, depending on the distance to the center",
            "The start parameter tells when the effects start being applied, the end parameter tells from which distance they should be fully applied",
        )

        CenterIndicator()
        CoverFlow(
            modifier = Modifier
                .background(Color.Black)
                .weight(1f),
            state = rememberCoverFlowState(
                onSelectHandler = {
                    Log.d(
                        "CoverFlowScreen",
                        "onSelectHandler outside of scope: $it",
                    )
                },
            ),
            params = params,
        ) {
            items(
                onSelectHandler = { _: String, index: Int ->
                    Log.d(
                        "CoverFlowScreen",
                        "onSelectHandler in scope: $index",
                    )
                },
                items = items,
            ) {
                Text(
                    text = it,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.background(Color.White),
                )
            }
        }

        CenterIndicator()

        val state = rememberCenteredLazyRowState()

        val old = OffsetLinearDistanceFactor(
            200f,
            400f,
        )

        CenteredLazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black),
            state = state,
        ) {
            items(items) { _, item ->
                Text(
                    modifier = Modifier
                        .requiredSize(with(LocalDensity.current) { (state.calculatedCenteredLazyRowLayoutInfo.itemWidth * 1.1f).toDp() })
                        .coverGraphicsLayer {
                            rotationY = -55f * old.factor(it.toFloat())
                        }
                        .background(Color.White),

                    text = item,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier
                        .requiredSize(with(LocalDensity.current) { (state.calculatedCenteredLazyRowLayoutInfo.itemWidth * 1.1f).toDp() })
                        .mirrorGraphicsLayer {
                            rotationY = -55f * old.factor(it.toFloat())
                        }
                        .background(Color.White),

                    text = item.uppercase(),
                    textAlign = TextAlign.Center,
                )
            }
        }
        CenterIndicator()
    }
}

@Composable
fun CenterIndicator(height: Dp = 16.dp) = Row(modifier = Modifier.height(height)) {
    Spacer(modifier = Modifier.weight(1f))
    VerticalDivider()
    Spacer(modifier = Modifier.weight(1f))
}

/**
 * For debugging. Sets the Parameters to values so that we get a normal LazyList without any
 * CoverFlow effects.
 */
@Suppress("unused")
fun debugCoverFlowParams(): CoverFlowParams {
    return CoverFlowParams(
        size = .5f,
        offset = 1f,
        angle = 0f,
        shift = 0f,
        zoom = 1f,
        mirror = false,
    )
}
