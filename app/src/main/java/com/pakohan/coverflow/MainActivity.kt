package com.pakohan.coverflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pakohan.coverflow.CoverFlowScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var showSettings by remember { mutableStateOf(false) }

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
                    com.pakohan.coverflow.CoverFlowScreen(
                        Modifier.padding(innerPadding)
                            .fillMaxSize(),
                        showSettings = showSettings
                    )
                }
            }
        }
    }
}
