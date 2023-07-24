package com.oxy.mmr.feature.touchbar

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxy.mmr.components.TimeZone
import com.oxy.mmr.util.MediaUtils.getDuration
import com.oxy.mmr.util.MediaUtils.loadThumbs
import com.oxy.mmr.util.MediaUtils.merge
import com.oxy.mmr.util.MediaUtils.recycleNullableUseless
import com.oxy.touchbar.Touchbar
import com.oxy.touchbar.TouchbarDefaults
import com.oxy.touchbar.rememberTouchbarState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val MIME_VIDEO = "video/*"
private const val thumbCount = 28

@Composable
internal fun TouchBarScreen(
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    var duration by remember { mutableStateOf(-1L) }
    val context = LocalContext.current
    var uri: Uri? by remember { mutableStateOf(null) }

    var enableZHandle by remember { mutableStateOf(false) }

    val touchBarState = rememberTouchbarState(
        enabled = duration >= 0L
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { newUri ->
        uri = newUri
    }

    val thumbWidth = with(density) {
        configuration.screenWidthDp.dp.toPx().roundToInt()
    }

    val bitmaps by produceState(
        emptyList(),
        uri,
        thumbWidth
    ) {
        loadThumbs(
            context = context,
            uri = uri,
            totalCount = thumbCount,
            dstWidth = { w, _ -> min(w, thumbWidth) },
            dstHeight = { w, h ->
                val aw = min(w, thumbWidth).toFloat()
                (aw / w * h).roundToInt()
            }
        ).collect { newBitmaps ->
            recycleNullableUseless(value, newBitmaps)
            value = newBitmaps
        }
    }

    LaunchedEffect(bitmaps) {
        if (bitmaps.size == thumbCount) {
            touchBarState.background?.asAndroidBitmap()?.recycle()
            val merged = merge(bitmaps, Orientation.Horizontal)
            touchBarState.notifyBackground(merged?.asImageBitmap())
        }
    }

    var currentX: Int by remember { mutableStateOf(-1) }
    var currentY: Int by remember { mutableStateOf(-1) }
    var currentZ: Int by remember { mutableStateOf(-1) }

    val bitmap by produceState<Bitmap?>(
        null,
        enableZHandle,
        bitmaps,
        currentX,
        currentY,
        currentZ,
        touchBarState.isXFocus,
        touchBarState.isYFocus
    ) {
        value = bitmaps.getOrNull(
            if (enableZHandle) {
                if (touchBarState.isYFocus && touchBarState.isXFocus) currentZ
                else if (touchBarState.isYFocus) currentY
                else if (touchBarState.isXFocus) currentX
                else currentZ
            } else {
                if (touchBarState.isYFocus) currentY
                else currentX
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            bitmaps.forEach {
                it?.recycle()
            }
        }
    }

    LaunchedEffect(uri) {
        duration = if (uri == null) -1
        else getDuration(context, uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                colors = ButtonDefaults.buttonColors(),
                onClick = {
                    launcher.launch(MIME_VIDEO)
                }
            ) {
                Text(
                    text = "OPEN",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = LocalContentColor.current
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            bitmap?.let {
                Image(
                    bitmap = remember(it) { it.asImageBitmap() },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            TimeZone(
                millisecond = remember(duration, touchBarState.x) {
                    if (duration == -1L) -1L
                    else (duration * touchBarState.x).roundToLong()
                }
            )
            TimeZone(
                millisecond = remember(duration, touchBarState.z) {
                    if (duration == -1L) -1L
                    else (duration * touchBarState.z).roundToLong()
                }
            )
            TimeZone(
                millisecond = remember(duration, touchBarState.y) {
                    if (duration == -1L) -1L
                    else (duration * touchBarState.y).roundToLong()
                }
            )
        }

        var playing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        DisposableEffect(enableZHandle, playing, duration) {
            val job = if (enableZHandle && playing) {
                scope.launch(Dispatchers.IO) {
                    val delta = 5f / duration
                    while (true) {
                        delay(5L)
                        val target = (touchBarState.z + delta).coerceIn(0f, 1f)
                        touchBarState.notify(
                            z = target
                        )
                        if (target == 1f) {
                            playing = false
                            touchBarState.notify(
                                z = 0f
                            )
                        }
                    }
                }
            } else null
            onDispose {
                job?.cancel()
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            AnimatedVisibility(
                visible = enableZHandle
            ) {
                Surface(
                    onClick = { playing = !playing },
                    color = Color.Transparent,
                    contentColor = TouchbarDefaults.EdgeColor,
                    shape = RoundedCornerShape(15),
                    modifier = Modifier
                        .height(TouchbarDefaults.HeightDp)
                        .aspectRatio(4 / 3f)
                ) {
                    Icon(
                        imageVector = if (playing) Icons.Rounded.Pause
                        else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Touchbar(
                state = touchBarState,
                enableZHandle = enableZHandle,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = enableZHandle,
                onCheckedChange = { enableZHandle = it }
            )
            Text(
                text = "Z-HANDLE",
                fontSize = 10.sp,
            )
        }

        LaunchedEffect(touchBarState.x) {
            val target = (touchBarState.x * thumbCount).roundToInt()
            if (currentX != target) currentX = target
        }
        LaunchedEffect(touchBarState.y) {
            val target = (touchBarState.y * thumbCount).roundToInt()
            if (currentY != target) currentY = target
        }
        LaunchedEffect(touchBarState.z) {
            val target = (touchBarState.z * thumbCount).roundToInt()
            if (currentZ != target) currentZ = target
        }
    }
}

