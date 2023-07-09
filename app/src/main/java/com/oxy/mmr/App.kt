package com.oxy.mmr

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

private const val MIME_VIDEO = "video/*"

private data class SharedElement(
    val offset: IntOffset,
    val size: Size,
    val bitmap: ImageBitmap
)

@Composable
fun App(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var uri: Uri? by remember { mutableStateOf(null) }
    var resource: Resource<List<ImageBitmap?>> by remember { mutableStateOf(Resource.Loading) }
    var bitmaps: List<ImageBitmap?> by remember { mutableStateOf(emptyList()) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { newUri ->
        uri = newUri
    }
    LaunchedEffect(context, uri) {
        context.loadThumbs(uri).collectLatest { resource = it }
    }
    LaunchedEffect(resource) {
        // thread safe
        val safeResource = resource
        bitmaps = when (safeResource) {
            is Resource.Success -> safeResource.data
            else -> emptyList()
        }
    }

    var element: SharedElement? by remember { mutableStateOf(null) }
    Box(
        modifier = Modifier
            .graphicsLayer {
                if (element != null) {
                    renderEffect = BlurEffect(16f, 16f)
                }
            }
            .then(modifier)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            VideoFrameLoader(
                bitmaps = bitmaps,
                onClick = { newElement ->
                    element = newElement
                },
                modifier = Modifier.fillMaxSize()
            )
            if (resource !is Resource.Success) {
                Text(
                    text = resource.toString(),
                    style = MaterialTheme.typography.labelMedium
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

        val configuration = LocalConfiguration.current

        var actualOffset by remember(element?.offset) {
            mutableStateOf(element?.offset ?: IntOffset.Zero)
        }
        var actualSize by remember(element?.size) {
            mutableStateOf(
                element?.size ?: Size(
                    configuration.screenWidthDp.toFloat(),
                    configuration.screenHeightDp.toFloat()
                )
            )
        }

        if (element != null) {
            val animatedOffset by animateIntOffsetAsState(actualOffset, tween(4000))
            val animatedSize by animateSizeAsState(actualSize, tween(4000))
            LaunchedEffect(Unit) {
                actualOffset = IntOffset.Zero
                actualSize = Size(
                    configuration.screenWidthDp.toFloat(),
                    configuration.screenHeightDp.toFloat()
                )
            }
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .graphicsLayer {
                        translationX = animatedOffset.x.toFloat()
                        translationY = animatedOffset.y.toFloat()
                    }
                    .size(
                        width = animatedSize.width.dp,
                        height = animatedSize.height.dp
                    )
                    .clickable {
                        element = null
                    }
            ) {
                Image(
                    bitmap = element!!.bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun VideoFrameLoader(
    bitmaps: List<ImageBitmap?>,
    onClick: (SharedElement) -> Unit,
    modifier: Modifier = Modifier,
    cellsCount: Int = 3
) {
    val density = LocalDensity.current.density
    val state = rememberLazyGridState()
    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(cellsCount),
        contentPadding = PaddingValues(4.dp),
        modifier = modifier
    ) {
        itemsIndexed(bitmaps) { index, bitmap ->
            if (bitmap != null) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                val actualIndex = index - state.firstVisibleItemIndex
                                val info = state.layoutInfo.visibleItemsInfo[actualIndex]
                                onClick(
                                    SharedElement(
                                        offset = info.offset,
                                        size = with(info.size) {
                                            Size(
                                                width.toFloat() / density,
                                                height.toFloat() / density
                                            )
                                        },
                                        bitmap = bitmap
                                    )
                                )
                            }
                    )
                }
            } else {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize()
                        .aspectRatio(1f)
                ) {}
            }
        }
        items(bitmaps.calculatePlaceholderCount(cellsCount)) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
                    .aspectRatio(1f),
                color = Color.Transparent
            ) {}
        }
    }
}

private fun Context.loadThumbs(
    uri: Uri?,
    totalCount: Int = 28
): Flow<Resource<List<ImageBitmap?>>> = flow {
    emit(Resource.Loading)
    if (uri == null) {
        emit(Resource.Failure("Uri is null."))
        return@flow
    }
    try {
        val bitmaps = MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(this@loadThumbs, uri)
            val duration = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: -1

            // total frame times
            val deltaTime = duration / totalCount * 1000

            withContext(Dispatchers.IO) {
                List(totalCount) { i ->
                    retriever.getFrameAtTime(
                        (deltaTime.toLong() * i),
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                        ?.asImageBitmap()
                }
            }
        }
        emit(Resource.Success(bitmaps))
    } catch (e: Exception) {
        emit(Resource.Failure(e.message.orEmpty()))
    }
}

private fun List<*>.calculatePlaceholderCount(cellsCount: Int): Int =
    if (isEmpty()) 0 else (size % cellsCount) + cellsCount

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val msg: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}