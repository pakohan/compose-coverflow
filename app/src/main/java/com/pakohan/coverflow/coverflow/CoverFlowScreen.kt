package com.pakohan.coverflow.coverflow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// This is for playing around with the parameters
@Composable
fun CoverFlowScreen(
    modifier: Modifier = Modifier,
    showSettings: Boolean,
) {
    var params by rememberSaveable { mutableStateOf(CoverFlowParams()) }

    Column(
        modifier = modifier
    ) {
        AnimatedVisibility(visible = showSettings) {
            CoverFlowSettings(params = params,
                              onParamsUpdate = { params = it })
        }
        CoverFlow(
            params = params,
            modifier = Modifier.background(Color.Black)
        ) {
            items(
                20
            ) {
                Surface(
                    Modifier.background(
                        Color.Blue
                    )
                ) {}
            }
        }
    }
}
