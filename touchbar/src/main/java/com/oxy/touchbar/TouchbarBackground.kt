package com.oxy.touchbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

@Composable
internal fun TouchbarBackground(
    background: ImageBitmap?,
    modifier: Modifier = Modifier,
    radius: Int = TouchbarDefaults.BackgroundRadiusPercent
) {
    Canvas(
        modifier
            .clip(RoundedCornerShape(radius))
            .fillMaxSize()
    ) {
        drawRect(Color.Black)
        if (background != null) {
            drawImage(
                background,
                topLeft = Offset(
                    x = 0f,
                    y = this.size.height / 2 - background.height / 2
                )
            )
        }
    }
}
