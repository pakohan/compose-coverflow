package com.pakohan.coverflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pakohan.coverflow.coverflow.CoverFlow
import com.pakohan.coverflow.coverflow.CoverFlowParams
import com.pakohan.coverflow.coverflow.CoverFlowSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var params by rememberSaveable { mutableStateOf(CoverFlowParams()) }
            var showSettings by remember { mutableStateOf(false) }

            MaterialTheme {
                Scaffold(floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showSettings = !showSettings },
                    ) {
                        Icon(
                            Icons.Filled.Settings,
                            "Floating action button."
                        )
                    }
                }) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        AnimatedVisibility(visible = showSettings) {
                            CoverFlowSettings(params = params,
                                              onParamsUpdate = { params = it })
                        }
                        CoverFlow(
                            params = params,
                            modifier = Modifier.background(Color.Black)
                        ) {
                            items(20) {
                                Surface(
                                    modifier = Modifier.background(Color.Blue)
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }
}
