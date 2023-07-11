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
    initialPx: Boolean = false,
    initialPy: Boolean = false,
    initialBitmaps: List<Bitmap?> = emptyList(),
): TouchBarState = remember(enabled, initialX, initialY, initialPx, initialPy, initialBitmaps) {
    TouchBarState(
        enabled = enabled,
        initialX = initialX,
        initialY = initialY,
        initialPx = initialPx,
        initialPy = initialPy,
        initialBitmaps = initialBitmaps
    )
}

@Immutable
internal class TouchBarState(
    val enabled: Boolean,
    initialX: Float,
    initialY: Float,
    initialPx: Boolean,
    initialPy: Boolean,
    initialBitmaps: List<Bitmap?>
) {
    private var _x: Float by mutableStateOf(initialX)
    private var _y: Float by mutableStateOf(initialY)
    private var _px: Boolean by mutableStateOf(initialPx)
    private var _py: Boolean by mutableStateOf(initialPy)
    private var _bitmaps: List<Bitmap?> by mutableStateOf(initialBitmaps)

    fun notify(
        x: Float? = null,
        y: Float? = null,
        px: Boolean? = null,
        py: Boolean? = null,
        bitmaps: List<Bitmap?>? = null
    ) {
        x?.let { _x = it }
        y?.let { _y = it }
        px?.let { _px = it }
        py?.let { _py = it }
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
        if (px != other.px) return false
        if (py != other.py) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + bitmaps.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + px.hashCode()
        result = 31 * result + py.hashCode()
        return result
    }

    override fun toString(): String {
        return "TouchBarState(enabled=$enabled, x=$x, y=$y, px=$px, py=$py)"
    }

    val x: Float get() = _x
    val y: Float get() = _y
    val px: Boolean get() = _px
    val py: Boolean get() = _py
    val bitmaps: List<Bitmap?> get() = _bitmaps

}
