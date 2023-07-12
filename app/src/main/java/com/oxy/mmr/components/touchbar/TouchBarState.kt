package com.oxy.mmr.components.touchbar

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun rememberTouchBarState(
    enabled: Boolean = true,
    initialX: Float = 0f,
    initialY: Float = 1f,
    initialBitmaps: List<Bitmap?> = emptyList(),
): TouchBarState = remember(enabled, initialX, initialY, initialBitmaps) {
    TouchBarState(
        enabled = enabled,
        initialX = initialX,
        initialY = initialY,
        initialBitmaps = initialBitmaps
    )
}

@Immutable
internal class TouchBarState(
    val enabled: Boolean,
    initialX: Float,
    initialY: Float,
    initialBitmaps: List<Bitmap?>
) {
    private var _x: Float by mutableStateOf(initialX)
    private var _y: Float by mutableStateOf(initialY)
    private var _isYFocus: Boolean by mutableStateOf(false)
    private var _bitmaps: List<Bitmap?> by mutableStateOf(initialBitmaps)

    fun notify(
        x: Float? = null,
        y: Float? = null,
        isYFocus: Boolean? = null,
        bitmaps: List<Bitmap?>? = null
    ) {
        x?.let { _x = it }
        y?.let { _y = it }
        isYFocus?.let { _isYFocus = it }
        bitmaps?.let { _bitmaps = it }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TouchBarState

        if (enabled != other.enabled) return false
        if (bitmaps != other.bitmaps) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (isYFocus != other.isYFocus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + bitmaps.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + isYFocus.hashCode()
        return result
    }

    override fun toString(): String {
        return "TouchBarState(enabled=$enabled, x=$x, y=$y, isYFocus=$isYFocus)"
    }

    val x: Float get() = _x
    val y: Float get() = _y
    val bitmaps: List<Bitmap?> get() = _bitmaps
    val isYFocus: Boolean get() = _isYFocus
}
