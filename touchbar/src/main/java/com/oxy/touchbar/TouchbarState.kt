package com.oxy.touchbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap

@Composable
fun rememberTouchbarState(
    enabled: Boolean = true,
    initialX: Float = 0f,
    initialY: Float = 1f,
    initialZ: Float = 0f
): TouchbarState {
    check(0f <= initialX) { "initialX should not less than 0" }
    check(initialX <= initialY) { "initialY should not less than initialX" }
    check(initialY <= 1) { "initialY should not higher than 1" }
    return remember(enabled, initialX, initialY, initialZ) {
        TouchbarState(
            enabled = enabled,
            initialX = initialX,
            initialY = initialY,
            initialZ = initialZ
        )
    }
}

@Immutable
class TouchbarState(
    val enabled: Boolean,
    initialX: Float,
    initialY: Float,
    initialZ: Float
) {
    private var _x: Float by mutableStateOf(initialX)
    private var _y: Float by mutableStateOf(initialY)
    private var _z: Float by mutableStateOf(initialZ)
    private var _isXFocus: Boolean by mutableStateOf(false)
    private var _isYFocus: Boolean by mutableStateOf(false)
    private var _isZFocus: Boolean by mutableStateOf(false)
    private var _background: ImageBitmap? by mutableStateOf(null)

    fun notify(
        x: Float? = null,
        y: Float? = null,
        z: Float? = null,
        isXFocus: Boolean? = null,
        isYFocus: Boolean? = null,
        isZFocus: Boolean? = null
    ) {
        x?.let { _x = it }
        y?.let { _y = it }
        z?.let { _z = it }
        isXFocus?.let { _isXFocus = it }
        isYFocus?.let { _isYFocus = it }
        isZFocus?.let { _isZFocus = it }
    }

    fun notifyBackground(bitmap: ImageBitmap?) {
        _background = bitmap
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TouchbarState

        if (enabled != other.enabled) return false
        if (background != other.background) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (isXFocus != other.isXFocus) return false
        if (isYFocus != other.isYFocus) return false
        if (isZFocus != other.isZFocus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + background.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + isXFocus.hashCode()
        result = 31 * result + isYFocus.hashCode()
        result = 31 * result + isZFocus.hashCode()
        return result
    }

    override fun toString(): String {
        return "TouchBarState(enabled=$enabled, x=$x, y=$y, z=$z, isXFocus=$isXFocus, isYFocus=$isYFocus, isZFocus=$isZFocus)"
    }

    val x: Float get() = _x
    val y: Float get() = _y
    val z: Float get() = _z
    val isXFocus: Boolean get() = _isXFocus
    val isYFocus: Boolean get() = _isYFocus
    val isZFocus: Boolean get() = _isZFocus
    val background: ImageBitmap? get() = _background
}
