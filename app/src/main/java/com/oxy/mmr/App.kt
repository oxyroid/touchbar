package com.oxy.mmr

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oxy.mmr.feature.album.AlbumScreen
import com.oxy.mmr.feature.touchbar.TouchBarScreen

sealed class Destination {
    object Home : Destination()
    object Album : Destination()
    object TouchBar : Destination()
}

@Composable
fun App(
    modifier: Modifier = Modifier
) {
    var destination: Destination by remember { mutableStateOf(Destination.Home) }

    Box(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { destination = Destination.Album },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Album")
            }
            Button(
                onClick = { destination = Destination.TouchBar },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "TouchBar")
            }
        }
        AnimatedVisibility(
            visible = destination == Destination.Album,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            AlbumScreen()
        }
        AnimatedVisibility(
            visible = destination == Destination.TouchBar,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            TouchBarScreen()
        }
    }

    BackHandler(destination != Destination.Home) {
        destination = Destination.Home
    }
}
