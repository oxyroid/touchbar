package com.oxy.mmr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.oxy.mmr.ui.theme.MediaMetadataRetrieverDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaMetadataRetrieverDemoTheme {
                Surface {
                    App(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
