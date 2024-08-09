package com.pakohan.coverflow_app

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pakohan.coverflow.CoverFlow
import com.pakohan.coverflow.CoverFlowParams
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
    ) {
        AnimatedVisibility(visible = showSettings) {
            CoverFlowSettings(
                params = params,
                onParamsUpdate = { params = it },
            )
        }
        CoverFlow(
            modifier = Modifier.background(Color.Black),
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
                onSelectHandler = { item: String, index: Int ->
                    Log.d(
                        "CoverFlowScreen",
                        "onSelectHandler in scope: $index",
                    )
                },
                items = listOf(
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
                ),
            ) {
                Text(
                    text = it,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.background(Color.White),
                )
            }
        }
    }
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
