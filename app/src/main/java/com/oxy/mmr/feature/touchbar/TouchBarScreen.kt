package com.oxy.mmr.feature.touchbar

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxy.mmr.components.TimeZone
import com.oxy.mmr.components.touchbar.TouchBar
import com.oxy.mmr.components.touchbar.rememberTouchBarState
import com.oxy.mmr.util.MediaUtils
import com.oxy.mmr.wrapper.Resource
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val MIME_VIDEO = "video/*"
private const val thumbCount = 28

@Composable
internal fun TouchBarScreen(
    modifier: Modifier = Modifier
) {
    var duration by remember { mutableStateOf(-1L) }
    val context = LocalContext.current
    var uri: Uri? by remember { mutableStateOf(null) }
    var bitmaps: List<Bitmap?> by remember { mutableStateOf(emptyList()) }

    val state = rememberTouchBarState(
        enabled = duration >= 0L
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { newUri ->
        uri = newUri
    }
    val resource by produceState<Resource<List<Bitmap?>>>(
        Resource.Loading,
        uri
    ) {
        MediaUtils.loadThumbs(
            context = context,
            uri = uri,
            totalCount = thumbCount
        ).collect {
            value = it
        }
    }

    var current: Int by remember { mutableStateOf(-1) }

    val bitmap by produceState<Bitmap?>(
        null,
        bitmaps,
        current
    ) {
        value = bitmaps.getOrNull(current)
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
        else MediaUtils.getDuration(context, uri)
    }

    LaunchedEffect(resource) {
        // thread safe
        val newBitmaps = when (val safeResource = resource) {
            is Resource.Success -> safeResource.data
            else -> emptyList()
        }

        MediaUtils.recycleNullableUseless(bitmaps, newBitmaps)
        bitmaps = newBitmaps
        state.notify(bitmaps = bitmaps)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp)
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            ClickableText(
                text = AnnotatedString("OPEN"),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = LocalContentColor.current
                ),
                onClick = {
                    launcher.launch(MIME_VIDEO)
                }
            )
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
                millisecond = remember(duration, state.x) {
                    if (duration == -1L) -1L
                    else (duration * state.x).roundToLong()
                }
            )
            TimeZone(
                millisecond = remember(duration, state.y) {
                    if (duration == -1L) -1L
                    else (duration * state.y).roundToLong()
                }
            )
        }

        TouchBar(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        LaunchedEffect(state.x) {
            val target = (state.x * thumbCount).roundToInt()
            if (current != target) current = target
        }
    }
}
