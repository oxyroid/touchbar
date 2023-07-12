package com.oxy.touchbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
internal fun TouchBarSelector(
    state: TouchBarState,
    handleRadius: Float,
    modifier: Modifier = Modifier,
    verticalHandle: Float = TouchBarDefaults.VerticalHandle,
    activeVerticalHandle: Float = TouchBarDefaults.ActiveVerticalHandle,
    handleInset: Float = TouchBarDefaults.HandleInset,
    activeHandleInset: Float = TouchBarDefaults.ActiveHandleInset,
    edgeColor: Color = TouchBarDefaults.EdgeColor,
    activeEdgeColor: Color = TouchBarDefaults.ActiveEdgeColor
) {
    val feedback = LocalHapticFeedback.current
    val color by animateColorAsState(
        if (state.enabled && (state.isXFocus || state.isYFocus)) activeEdgeColor
        else edgeColor,
        tween(400)
    )
    val xHandleInset by animateFloatAsState(
        if (state.isXFocus) activeHandleInset else handleInset
    )
    val yHandleInset by animateFloatAsState(
        if (state.isYFocus) activeHandleInset else handleInset
    )

    val xHandle by animateFloatAsState(
        if (state.isXFocus) activeVerticalHandle else verticalHandle
    )
    val yHandle by animateFloatAsState(
        if (state.isYFocus) activeVerticalHandle else verticalHandle
    )
    LaunchedEffect(state.isXFocus, state.isYFocus) {
        if (state.isXFocus || state.isYFocus) {
            feedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        // x panel v
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width - xHandle / 2,
                y = 0f
            ),
            size = this.size.copy(
                width = xHandle
            ),
            cornerRadius = CornerRadius(handleRadius)
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
            strokeWidth = xHandleInset,
            blendMode = BlendMode.Clear
        )
        // y panel v
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.y * this.size.width - yHandle / 2,
                y = 0f
            ),
            size = this.size.copy(
                width = yHandle
            ),
            cornerRadius = CornerRadius(handleRadius)
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
            strokeWidth = yHandleInset,
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
                height = TouchBarDefaults.HorizontalHandle
            ),
            cornerRadius = CornerRadius(handleRadius)
        )
        // x2
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width,
                y = this.size.height - TouchBarDefaults.HorizontalHandle
            ),
            size = this.size.copy(
                width = this.size.width * (state.y - state.x),
                height = TouchBarDefaults.HorizontalHandle
            ),
            cornerRadius = CornerRadius(handleRadius)
        )
    }
}
