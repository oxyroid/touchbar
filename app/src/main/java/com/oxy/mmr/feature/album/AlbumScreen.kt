package com.oxy.mmr.feature.album

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.oxy.mmr.components.VideoAlbum
import com.oxy.mmr.components.VideoViewer
import com.oxy.mmr.util.MediaUtils.loadThumbs
import com.oxy.mmr.util.MediaUtils.recycleNullableUseless
import com.oxy.mmr.wrapper.Shared
import kotlin.math.min
import kotlin.math.roundToInt

private const val MIME_VIDEO = "video/*"

@Composable
internal fun AlbumScreen(
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val thumbWidth = with(density) {
        configuration.screenWidthDp.dp.toPx().roundToInt() / 3
    }
    val context = LocalContext.current
    var uri: Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { newUri ->
        uri = newUri
    }
    val bitmaps by produceState(
        emptyList(),
        uri,
        thumbWidth
    ) {
        loadThumbs(
            context = context,
            uri = uri,
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

    DisposableEffect(Unit) {
        onDispose {
            bitmaps.forEach {
                it?.recycle()
            }
        }
    }

    var element: Shared<Bitmap>? by remember { mutableStateOf(null) }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .then(modifier)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            VideoAlbum(
                bitmaps = bitmaps,
                onClick = { newElement ->
                    element = newElement
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Button(
            onClick = {
                launcher.launch(MIME_VIDEO)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "PICK VIDEO"
            )
        }

        element?.let { innerShared ->
            VideoViewer(
                shared = innerShared,
                onClick = {
                    element = null
                }
            )
        }
    }
}