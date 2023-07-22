package com.oxy.touchbar

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.oxy.touchbar.locals.Infos
import com.oxy.touchbar.locals.LocalInfos

@Composable
fun Touchbar(
    modifier: Modifier = Modifier,
    state: TouchbarState = rememberTouchbarState(),
    height: Dp = TouchbarDefaults.HeightDp,
    backgroundRadius: Int = TouchbarDefaults.BackgroundRadiusPercent,
    handleRadius: Float = TouchbarDefaults.HandleRadius,
    verticalHandle: Float = TouchbarDefaults.VerticalHandle,
    activeVerticalHandle: Float = TouchbarDefaults.ActiveVerticalHandle,
    horizontalHandle: Float = TouchbarDefaults.HorizontalHandle,
    handleInset: Float = TouchbarDefaults.HandleInset,
    activeHandleInset: Float = TouchbarDefaults.ActiveHandleInset,
    edgeColor: Color = TouchbarDefaults.EdgeColor,
    activeEdgeColor: Color = TouchbarDefaults.ActiveEdgeColor,
    indicatorColor: Color = TouchbarDefaults.IndicatorColor,
    activeIndicatorColor: Color = TouchbarDefaults.ActiveIndicatorColor,
    // experimental
    enableZHandle: Boolean = false,
) {
    val infos = Infos(
        enableZHandle = enableZHandle,
        backgroundRadius = backgroundRadius,
        handleRadius = handleRadius,
        verticalHandle = verticalHandle,
        activeVerticalHandle = activeVerticalHandle,
        activeHandleInset = activeHandleInset,
        handleInset = handleInset,
        edgeColor = edgeColor,
        activeEdgeColor = activeEdgeColor,
        indicatorColor = indicatorColor,
        activeIndicatorColor = activeIndicatorColor,
        horizontalHandle = horizontalHandle
    )
    CompositionLocalProvider(
        LocalInfos provides infos
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
        ) {
            TouchbarBackground(state.background)
            TouchbarSelector(state)
            TouchbarPanel(state)
        }
    }
}
