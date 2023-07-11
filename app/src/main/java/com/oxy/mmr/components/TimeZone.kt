package com.oxy.mmr.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
internal fun TimeZone(
    millisecond: Long,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    color: Color = Color.Unspecified
) {
    Text(
        text = remember(millisecond) {
            millisecond.mss()
        },
        fontSize = fontSize,
        color = color,
        modifier = modifier
    )
}

private fun Long.mss(): String {
    if (this < 0L) return "--:--"
    var seconds = this / 1000
    var minutes = 0L
    while (seconds >= 60) {
        seconds -= 60
        minutes++
    }
    val formattedSeconds = minutes.toString().let {
        when (it.length) {
            1 -> "0$it"
            else -> it
        }
    }
    val formattedMinutes = seconds.toString().let {
        when (it.length) {
            1 -> "0$it"
            else -> it
        }
    }
    return "$formattedSeconds:$formattedMinutes"
}