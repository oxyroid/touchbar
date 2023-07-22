package com.oxy.touchbar

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.oxy.touchbar.locals.LocalInfos
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun TouchbarPanel(
    state: TouchbarState,
    modifier: Modifier = Modifier
) {
    val infos = LocalInfos.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(
                state.enabled,
                infos.enableZHandle
            ) {
                val area = infos.activeVerticalHandle / this.size.width
                var position: Position? = null
                detectDragGestures(
                    onDragStart = { offset ->
                        position = Position(
                            offset.x / this.size.width,
                            offset.y / this.size.height
                        )
                    },
                    onDragEnd = {
                        position = null
                        state.notify(
                            isXFocus = false,
                            isYFocus = false,
                            isZFocus = false
                        )
                    },
                    onDragCancel = {
                        position = null
                        state.notify(
                            isXFocus = false,
                            isYFocus = false,
                            isZFocus = false
                        )
                    },
                    onDrag = { change, dragAmount ->
                        if (!state.enabled) return@detectDragGestures
                        change.consume()
                        val delta = dragAmount.x / this.size.width
                        position?.let { touched ->
                            fun notifyX() {
                                val target = (delta + state.x).coerceIn(0f, state.y)
                                state.notify(
                                    x = target,
                                    z = max(target, state.z),
                                    isXFocus = true
                                )
                                position = touched + delta
                            }

                            fun notifyY() {
                                val target = (delta + state.y).coerceIn(state.x, 1f)
                                state.notify(
                                    y = target,
                                    z = min(state.z, target),
                                    isYFocus = true
                                )
                                position = touched + delta
                            }

                            fun notifyXY() {
                                val targetX = (delta + state.x).coerceIn(0f, state.y)
                                val targetY = (delta + state.y).coerceIn(state.x, 1f)
                                state.notify(
                                    x = targetX,
                                    y = targetY,
                                    z = if (infos.enableZHandle) state.z.coerceIn(targetX, targetY)
                                    else state.z,
                                    isXFocus = true,
                                    isYFocus = true
                                )
                                position = touched + delta
                            }

                            fun notifyZ() {
                                if (!infos.enableZHandle) return
                                state.notify(
                                    z = (delta + state.z).coerceIn(state.x, state.y),
                                    isZFocus = true
                                )
                                position = touched + delta
                            }

                            when {
                                state.notifyX -> notifyX()
                                state.notifyY -> notifyY()
                                infos.enableZHandle && state.notifyZ -> notifyZ()

                                infos.enableZHandle && touched.vertical in (1 - infos.bottomPaddingPresent..1f) &&
                                        abs(touched.horizontal - state.z) <= area -> notifyZ()

                                state.notifyX(area, touched.horizontal) -> notifyX()
                                state.notifyY(area, touched.horizontal) -> notifyY()
                                touched.horizontal in (state.x..state.y) -> {
                                    if ((delta < 0f) && (state.x + delta) > 0f) {
                                        notifyXY()
                                    } else if ((delta > 0f) && (state.y + delta) < 1f) {
                                        notifyXY()
                                    }
                                }
                            }
                        }
                    }
                )
            }
    )
}

private data class Position(
    val horizontal: Float,
    val vertical: Float
) {
    operator fun plus(horizontal: Float): Position {
        return this.copy(
            horizontal = this.horizontal + horizontal
        )
    }
}

private val TouchbarState.notifyX: Boolean get() = isXFocus && !isYFocus && !isZFocus

private val TouchbarState.notifyY: Boolean get() = !isXFocus && isYFocus && !isZFocus

private val TouchbarState.notifyZ: Boolean get() = !isXFocus && !isYFocus && isZFocus

private fun TouchbarState.notifyX(
    area: Float,
    current: Float
): Boolean = abs(current - x) <= area

private fun TouchbarState.notifyY(
    area: Float,
    current: Float
): Boolean = abs(current - y) <= area
