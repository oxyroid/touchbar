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
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun TouchBar(
    modifier: Modifier = Modifier,
    state: TouchBarState = rememberTouchBarState(),
    backgroundRadius: Int = TouchBarDefaults.BackgroundRadiusPercent,
    handleRadius: Float = TouchBarDefaults.HandleRadius,
    verticalHandle: Float = TouchBarDefaults.VerticalHandle,
    activeVerticalHandle: Float = TouchBarDefaults.ActiveVerticalHandle,
    handleInset: Float = TouchBarDefaults.HandleInset,
    activeHandleInset: Float = TouchBarDefaults.ActiveHandleInset,
    edgeColor: Color = TouchBarDefaults.EdgeColor,
    activeEdgeColor: Color = TouchBarDefaults.ActiveEdgeColor
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        val area = remember(constraints) { verticalHandle / constraints.maxWidth }
        TouchBarBackground(
            background = state.background,
            radius = backgroundRadius
        )
        TouchBarSelector(
            state = state,
            handleRadius = handleRadius,
            verticalHandle = verticalHandle,
            activeVerticalHandle = activeVerticalHandle,
            handleInset = handleInset,
            activeHandleInset = activeHandleInset,
            edgeColor = edgeColor,
            activeEdgeColor = activeEdgeColor
        )
        TouchBarPanel(
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
