package com.oxy.mmr.wrapper

import android.content.res.Configuration
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset

internal interface Shared<T> {
    val data: T
    val offset: IntOffset
    val size: Size
    fun copy(
        data: T = this.data,
        offset: IntOffset = this.offset,
        size: Size = this.size
    ): Shared<T> = of(
        { data },
        { offset },
        { size }
    )

    companion object {
        fun <T> of(
            data: T,
            offset: IntOffset,
            size: Size
        ): Shared<T> = object : Shared<T> {
            override val data: T get() = data
            override val offset: IntOffset get() = offset
            override val size: Size get() = size
        }

        // lazy
        fun <T> of(
            data: () -> T,
            offset: () -> IntOffset,
            size: () -> Size
        ): Shared<T> {
            return object : Shared<T> {
                override val data: T get() = data()
                override val offset: IntOffset get() = offset()
                override val size: Size get() = size()
            }
        }
    }
}

internal fun <T> Configuration.shared(): Shared<T> = Shared.of(
    data = { error("Do not get configuration shared data!") },
    offset = { IntOffset.Zero },
    size = {
        Size(
            screenWidthDp.toFloat(),
            screenHeightDp.toFloat()
        )
    }
)

@Composable
internal fun <T> SharedHandler(
    initial: Shared<T>,
    target: Shared<T> = LocalConfiguration.current.shared(),
    intOffsetAnimationSpec: AnimationSpec<IntOffset> = intOffsetDefaultSpring,
    sizeAnimationSpec: AnimationSpec<Size> = sizeDefaultSpring,
    content: @Composable (Shared<T>) -> Unit
) {
    var actualOffset by remember(initial.offset) { mutableStateOf(initial.offset) }
    var actualSize by remember(initial.size) { mutableStateOf(initial.size) }
    val animatedOffset by animateIntOffsetAsState(actualOffset, intOffsetAnimationSpec)
    val animatedSize by animateSizeAsState(actualSize, sizeAnimationSpec)

    LaunchedEffect(Unit) {
        actualOffset = target.offset
        actualSize = target.size
    }
    content(
        initial.copy(
            offset = animatedOffset,
            size = animatedSize
        )
    )
}

private val intOffsetDefaultSpring = spring(visibilityThreshold = IntOffset.VisibilityThreshold)

private val sizeDefaultSpring = spring(visibilityThreshold = Size.VisibilityThreshold)
