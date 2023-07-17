package com.oxy.touchbar

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

@Composable
fun Touchbar(
    modifier: Modifier = Modifier,
    state: TouchbarState = rememberTouchbarState(),
    height: Dp = TouchbarDefaults.HeightDp,
    backgroundRadius: Int = TouchbarDefaults.BackgroundRadiusPercent,
    handleRadius: Float = TouchbarDefaults.HandleRadius,
    verticalHandle: Float = TouchbarDefaults.VerticalHandle,
    activeVerticalHandle: Float = TouchbarDefaults.ActiveVerticalHandle,
    handleInset: Float = TouchbarDefaults.HandleInset,
    activeHandleInset: Float = TouchbarDefaults.ActiveHandleInset,
    edgeColor: Color = TouchbarDefaults.EdgeColor,
    activeEdgeColor: Color = TouchbarDefaults.ActiveEdgeColor
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val area = remember(
            activeVerticalHandle,
            constraints.maxWidth
        ) { activeVerticalHandle / constraints.maxWidth }
        TouchbarBackground(
            background = state.background,
            radius = backgroundRadius
        )
        TouchbarSelector(
            state = state,
            handleRadius = handleRadius,
            verticalHandle = verticalHandle,
            activeVerticalHandle = activeVerticalHandle,
            handleInset = handleInset,
            activeHandleInset = activeHandleInset,
            edgeColor = edgeColor,
            activeEdgeColor = activeEdgeColor
        )
        TouchbarPanel(
            modifier = Modifier
                .pointerInput(state.enabled, area) {
                    // 0f~1f
                    var touched: Float? = null
                    detectDragGestures(
                        onDragStart = { offset ->
                            touched = offset.x / this.size.width
                        },
                        onDragEnd = {
                            touched = null
                            state.notify(
                                isXFocus = false,
                                isYFocus = false
                            )
                        },
                        onDragCancel = {
                            touched = null
                            state.notify(
                                isXFocus = false,
                                isYFocus = false
                            )
                        },
                        onDrag = { change, dragAmount ->
                            if (!state.enabled) return@detectDragGestures
                            change.consume()
                            val delta = dragAmount.x / this.size.width
                            touched?.let { innerTouched ->
                                fun notifyX() {
                                    state.notify(
                                        x = (delta + state.x).coerceIn(0f, state.y)
                                    )
                                    touched = innerTouched + delta
                                }

                                fun notifyXFocus() {
                                    state.notify(
                                        isXFocus = true
                                    )
                                }

                                fun notifyY() {
                                    state.notify(
                                        y = (delta + state.y).coerceIn(state.x, 1f)
                                    )
                                    touched = innerTouched + delta
                                }

                                fun notifyYFocus() {
                                    state.notify(
                                        isYFocus = true
                                    )
                                }

                                when {
                                    state.isXFocus && !state.isYFocus -> {
                                        notifyX()
                                        notifyXFocus()
                                    }

                                    !state.isXFocus && state.isYFocus -> {
                                        notifyY()
                                        notifyYFocus()
                                    }

                                    abs(innerTouched - state.x) <= area -> {
                                        notifyX()
                                        notifyXFocus()
                                    }

                                    abs(innerTouched - state.y) <= area -> {
                                        notifyY()
                                        notifyYFocus()
                                    }

                                    innerTouched in (state.x..state.y) -> {
                                        if ((delta < 0f) && (state.x + delta) > 0f) {
                                            notifyX()
                                            notifyY()
                                        } else if ((delta > 0f) && (state.y + delta) < 1f) {
                                            notifyX()
                                            notifyY()
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
        )
    }
}
