package com.oxy.mmr

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oxy.mmr.components.VideoAlbum
import com.oxy.mmr.components.VideoViewer
import com.oxy.mmr.util.MediaUtils
import com.oxy.mmr.wrapper.Resource
import com.oxy.mmr.wrapper.Shared

private const val MIME_VIDEO = "video/*"

@Composable
fun App(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var uri: Uri? by remember { mutableStateOf(null) }
    var bitmaps: List<ImageBitmap?> by remember { mutableStateOf(emptyList()) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { newUri ->
        uri = newUri
    }
    val resource by produceState<Resource<List<ImageBitmap?>>>(
        Resource.Loading,
        uri
    ) {
        MediaUtils.loadThumbs(context, uri).collect {
            value = it
        }
    }

    LaunchedEffect(resource) {
        // thread safe
        val safeResource = resource
        bitmaps = when (safeResource) {
            is Resource.Success -> safeResource.data
            else -> emptyList()
        }
    }

    var element: Shared<ImageBitmap>? by remember { mutableStateOf(null) }
    Box(
        modifier = modifier
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
            if (resource !is Resource.Success) {
                Text(
                    text = resource.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
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
