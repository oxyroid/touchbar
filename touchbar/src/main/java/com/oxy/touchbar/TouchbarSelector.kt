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
import com.oxy.touchbar.locals.LocalInfos
import kotlin.math.roundToInt

@Composable
internal fun TouchbarSelector(
    state: TouchbarState,
    modifier: Modifier = Modifier,
) {
    val infos = LocalInfos.current
    val feedback = LocalHapticFeedback.current

    val handleRadius: Float = infos.handleRadius
    val verticalHandle: Float = infos.verticalHandle
    val activeVerticalHandle: Float = infos.activeVerticalHandle
    val handleInset: Float = infos.handleInset
    val activeHandleInset: Float = infos.activeHandleInset
    val horizontalHandle: Float = infos.horizontalHandle
    val edgeColor: Color = infos.edgeColor
    val activeEdgeColor: Color = infos.activeEdgeColor
    val indicatorColor: Color = infos.indicatorColor
    val activeIndicatorColor: Color = infos.activeIndicatorColor

    val color by animateColorAsState(
        if (state.enabled && (state.isXFocus || state.isYFocus)) activeEdgeColor
        else edgeColor,
        tween(400)
    )
    val zColor by animateColorAsState(
        if (state.enabled && state.isZFocus) activeIndicatorColor
        else indicatorColor,
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

    val zHandlePresent by animateFloatAsState(
        if (state.isZFocus) 0.85f else 0.65f
    )

    LaunchedEffect(state.isXFocus, state.isYFocus) {
        if (state.isXFocus || state.isYFocus) {
            feedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    LaunchedEffect((state.z * 100).roundToInt()) {
        if (state.isZFocus) feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val bottomPadding = when {
            infos.enableZHandle -> this.size.height * infos.bottomPaddingPresent
            else -> 0f
        }
        // x panel v
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width - xHandle / 2,
                y = 0f
            ),
            size = this.size.copy(
                width = xHandle,
                height = this.size.height - bottomPadding
            ),
            cornerRadius = CornerRadius(handleRadius)
        )
        // x panel v inner
        drawLine(
            color = Color.Transparent,
            start = Offset(
                x = state.x * this.size.width,
                y = (this.size.height - bottomPadding) * 0.25f
            ),
            end = Offset(
                x = state.x * this.size.width,
                y = (this.size.height - bottomPadding) * 0.75f
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
                width = yHandle,
                height = this.size.height - bottomPadding
            ),
            cornerRadius = CornerRadius(handleRadius)
        )
        // y panel v inner
        drawLine(
            color = Color.Transparent,
            start = Offset(
                x = state.y * this.size.width,
                y = (this.size.height - bottomPadding) * 0.25f
            ),
            end = Offset(
                x = state.y * this.size.width,
                y = (this.size.height - bottomPadding) * 0.75f
            ),
            cap = StrokeCap.Round,
            strokeWidth = yHandleInset,
            blendMode = BlendMode.Clear
        )
        // h1
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width,
                y = 0f
            ),
            size = this.size.copy(
                width = this.size.width * (state.y - state.x),
                height = horizontalHandle
            ),
            cornerRadius = CornerRadius(handleRadius)
        )
        // h2
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = state.x * this.size.width,
                y = this.size.height - horizontalHandle - bottomPadding
            ),
            size = this.size.copy(
                width = this.size.width * (state.y - state.x),
                height = horizontalHandle
            ),
            cornerRadius = CornerRadius(handleRadius)
        )

        if (infos.enableZHandle) {
            // z panel v
            drawCircle(
                color = zColor,
                radius = (bottomPadding / 2) * zHandlePresent,
                center = Offset(
                    x = state.z * this.size.width,
                    y = this.size.height * (1 - infos.bottomPaddingPresent / 2)
                )
            )
        }
    }
}
