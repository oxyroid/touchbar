package com.oxy.mmr.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.oxy.mmr.wrapper.Shared
import com.oxy.mmr.wrapper.SharedHandler

@Composable
internal fun VideoViewer(
    shared: Shared<ImageBitmap>,
    onClick: () -> Unit
) {
    SharedHandler(shared) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .graphicsLayer {
                    translationX = it.offset.x.toFloat()
                    translationY = it.offset.y.toFloat()
                }
                .size(
                    width = it.size.width.dp,
                    height = it.size.height.dp
                )
                .clickable {
                    onClick()
                }
        ) {
            Image(
                bitmap = shared.data,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

