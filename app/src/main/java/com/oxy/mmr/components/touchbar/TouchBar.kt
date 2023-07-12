@file:OptIn(ExperimentalFoundationApi::class)

package com.oxy.mmr.components.touchbar

import android.graphics.Bitmap
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.min

@Composable
internal fun TouchBar(
    modifier: Modifier = Modifier,
    state: TouchBarState = rememberTouchBarState(),
    backgroundRadiusPresent: Int = TouchBarDefaults.BackgroundRadiusPresent,
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
                .pointerInput(state.enabled) {
                    // 0f~1f
                    var touched: Float? = null
                    detectDragGestures(
                        onDragStart = { offset ->
                            touched = offset.x / this.size.width
                        },
                        onDragEnd = {
                            touched = null
                            state.notify(
                                isYFocus = false
                            )
                        },
                        onDragCancel = {
                            touched = null
                            state.notify(
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
                                        x = (delta + state.x).coerceIn(0f, state.y),
                                        isYFocus = false
                                    )
                                    touched = innerTouched + delta
                                    if (dragAmount.x > 0) speederX.increase()
                                    else if (dragAmount.x < 0) speederX.decrease()
                                    feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }

                                fun notifyY() {
                                    state.notify(
                                        y = (delta + state.y).coerceIn(state.x, 1f),
                                        isYFocus = true
                                    )
                                    touched = innerTouched + delta
                                    if (dragAmount.y > 0) speederY.increase()
                                    else if (dragAmount.y < 0) speederY.decrease()
                                    feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                when {
                                    abs(innerTouched - state.x) <= 0.1f -> {
                                        notifyX()
                                    }

                                    abs(innerTouched - state.y) <= 0.1f -> {
                                        notifyY()
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

@Composable
internal fun TouchBarPanel(modifier: Modifier) {
    Box(modifier.fillMaxSize())
}

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

internal object TouchBarDefaults {
    const val BackgroundRadiusPresent = 25
    val EdgeColor = Color.White
    val ActiveEdgeColor = Color(0xffffc773)
    const val PanelVerticalWidth = 48f
    const val PanelHorizontalWidth = 18f
    private const val MaxDegree = 15f
    fun calculateDegree(@FloatRange(-1.0, 1.0) speed: Float): Float {
        // linear
        return speed * MaxDegree
    }
}
