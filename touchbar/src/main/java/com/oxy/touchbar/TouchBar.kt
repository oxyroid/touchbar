package com.oxy.touchbar

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun TouchBar(
    modifier: Modifier = Modifier,
    state: TouchBarState = rememberTouchBarState(),
    backgroundRadiusPresent: Int = TouchBarDefaults.BackgroundRadiusPresent,
    // The percentage of the touchable area of
    // the left and right of the handle to the entire TouchBar
    @FloatRange(0.01, 0.49) area: Float = 0.1f,
) {
    val handlerRadius = 18f
    val feedback = LocalHapticFeedback.current
    val speederX = rememberSpeeder()
    val degreeX by remember {
        derivedStateOf {
            TouchBarDefaults.calculateDegree(speederX.speed)
        }
    }

    val speederY = rememberSpeeder()
    val degreeY by remember {
        derivedStateOf {
            TouchBarDefaults.calculateDegree(speederY.speed)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        TouchBarBackground(
            bitmaps = state.bitmaps,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(backgroundRadiusPresent))
                .background(Color.Black)
        )
        TouchBarSelector(
            state = state,
            handlerRadius = handlerRadius
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
                                    if (dragAmount.x > 0) speederX.increase()
                                    else if (dragAmount.x < 0) speederX.decrease()
                                    feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                    if (dragAmount.y > 0) speederY.increase()
                                    else if (dragAmount.y < 0) speederY.decrease()
                                    feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
