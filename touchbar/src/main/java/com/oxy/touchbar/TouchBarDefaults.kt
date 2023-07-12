package com.oxy.touchbar

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

object TouchBarDefaults {
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
