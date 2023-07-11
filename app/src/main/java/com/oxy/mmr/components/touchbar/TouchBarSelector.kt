package com.oxy.mmr.components.touchbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

@Composable
internal fun TouchBarSelector(
    state: TouchBarState,
    handlerRadius: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val color = if (state.enabled) TouchBarDefaults.ActiveEdgeColor
        else TouchBarDefaults.EdgeColor
        // x panel v
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width - TouchBarDefaults.PanelVerticalWidth / 2,
                y = 0f
            ),
            size = this.size.copy(
                width = TouchBarDefaults.PanelVerticalWidth
            ),
            cornerRadius = CornerRadius(handlerRadius)
        )
        // x panel v inner
        drawLine(
            color = Color.Transparent,
            start = Offset(
                x = state.x * this.size.width,
                y = this.size.height * 0.25f
            ),
            end = Offset(
                x = state.x * this.size.width,
                y = this.size.height * 0.75f
            ),
            cap = StrokeCap.Round,
            strokeWidth = 12f,
            blendMode = BlendMode.Clear
        )
        // y panel v
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.y * this.size.width - TouchBarDefaults.PanelVerticalWidth / 2,
                y = 0f
            ),
            size = this.size.copy(
                width = TouchBarDefaults.PanelVerticalWidth
            ),
            cornerRadius = CornerRadius(handlerRadius)
        )
        // y panel v inner
        drawLine(
            color = Color.Transparent,
            start = Offset(
                x = state.y * this.size.width,
                y = this.size.height * 0.25f
            ),
            end = Offset(
                x = state.y * this.size.width,
                y = this.size.height * 0.75f
            ),
            cap = StrokeCap.Round,
            strokeWidth = 12f,
            blendMode = BlendMode.Clear
        )
        // x1
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width,
                y = 0f
            ),
            size = this.size.copy(
                width = this.size.width * (state.y - state.x),
                height = TouchBarDefaults.PanelHorizontalWidth
            ),
            cornerRadius = CornerRadius(handlerRadius)
        )
        // x2
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width,
                y = this.size.height - TouchBarDefaults.PanelHorizontalWidth
            ),
            size = this.size.copy(
                width = this.size.width * (state.y - state.x),
                height = TouchBarDefaults.PanelHorizontalWidth
            ),
            cornerRadius = CornerRadius(handlerRadius)
        )
    }
}
