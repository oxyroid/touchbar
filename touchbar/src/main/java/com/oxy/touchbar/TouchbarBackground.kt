package com.oxy.touchbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.oxy.touchbar.locals.LocalInfos

@Composable
internal fun TouchbarBackground(
    background: ImageBitmap?,
    modifier: Modifier = Modifier
) {
    val infos = LocalInfos.current
    val density = LocalDensity.current
    val radius = infos.backgroundRadius
    BoxWithConstraints {
        Canvas(
            modifier
                .padding(
                    bottom = with(density) {
                        if (!infos.enableZHandle) 0.dp
                        else constraints.maxHeight.toDp() * infos.bottomPaddingPresent
                    }
                )
                .clip(RoundedCornerShape(radius))
                .fillMaxSize()
        ) {
            drawRect(Color.Black)
            if (background != null) {
                drawImage(
                    background,
                    topLeft = Offset(
                        x = 0f,
                        y = (this.size.height - background.height) / 2
                    )
                )
            }
        }
    }

}
