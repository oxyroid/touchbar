package com.oxy.touchbar

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.min

@Composable
internal fun TouchBarBackground(
    bitmaps: List<Bitmap?>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.fillMaxSize()) {
        var totalX = 0
        bitmaps.forEach { bitmap ->
            if (bitmap == null) {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(
                        x = totalX.toFloat(),
                        y = 0f
                    )
                )
            } else {
                drawImage(
                    bitmap.asImageBitmap(),
                    topLeft = Offset(
                        x = totalX.toFloat(),
                        y = 0f
                    )
                )
                totalX += min(bitmap.width, bitmap.height)
            }
        }
    }
}