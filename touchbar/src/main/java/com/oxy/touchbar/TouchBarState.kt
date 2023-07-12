package com.oxy.touchbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap

@Composable
fun rememberTouchBarState(
    enabled: Boolean = true,
    initialX: Float = 0f,
    initialY: Float = 1f
): TouchBarState = remember(enabled, initialX, initialY) {
    TouchBarState(
        enabled = enabled,
        initialX = initialX,
        initialY = initialY
    )
}

@Immutable
class TouchBarState(
    val enabled: Boolean,
    initialX: Float,
    initialY: Float,
) {
    private var _x: Float by mutableStateOf(initialX)
    private var _y: Float by mutableStateOf(initialY)
    private var _isXFocus: Boolean by mutableStateOf(false)
    private var _isYFocus: Boolean by mutableStateOf(false)
    private var _background: ImageBitmap? by mutableStateOf(null)

    fun notify(
        x: Float? = null,
        y: Float? = null,
        isXFocus: Boolean? = null,
        isYFocus: Boolean? = null
    ) {
        x?.let { _x = it }
        y?.let { _y = it }
        isXFocus?.let { _isXFocus = it }
        isYFocus?.let { _isYFocus = it }
    }

    fun notifyBackground(bitmap: ImageBitmap?) {
        _background = bitmap
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TouchBarState

        if (enabled != other.enabled) return false
        if (background != other.background) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (isXFocus != other.isXFocus) return false
        if (isYFocus != other.isYFocus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + background.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + isXFocus.hashCode()
        result = 31 * result + isYFocus.hashCode()
        return result
    }

    override fun toString(): String {
        return "TouchBarState(enabled=$enabled, x=$x, y=$y, isXFocus=$isXFocus, isYFocus=$isYFocus)"
    }

    val x: Float get() = _x
    val y: Float get() = _y
    val isXFocus: Boolean get() = _isXFocus
    val isYFocus: Boolean get() = _isYFocus
    val background: ImageBitmap? get() = _background
}
