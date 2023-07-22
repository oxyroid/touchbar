package com.oxy.touchbar.locals

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.oxy.touchbar.TouchbarDefaults

@Immutable
internal data class Infos(
    val enableZHandle: Boolean = false,
    val bottomPaddingPresent: Float = 0.25f,
    val backgroundRadius: Int = TouchbarDefaults.BackgroundRadiusPercent,
    val handleRadius: Float = TouchbarDefaults.HandleRadius,
    val verticalHandle: Float = TouchbarDefaults.VerticalHandle,
    val activeVerticalHandle: Float = TouchbarDefaults.ActiveVerticalHandle,
    val horizontalHandle: Float = TouchbarDefaults.HorizontalHandle,
    val handleInset: Float = TouchbarDefaults.HandleInset,
    val activeHandleInset: Float = TouchbarDefaults.ActiveHandleInset,
    val edgeColor: Color = TouchbarDefaults.EdgeColor,
    val activeEdgeColor: Color = TouchbarDefaults.ActiveEdgeColor,
    val indicatorColor: Color = TouchbarDefaults.IndicatorColor,
    val activeIndicatorColor: Color = TouchbarDefaults.ActiveIndicatorColor,
)

internal val LocalInfos = compositionLocalOf {
    Infos()
}