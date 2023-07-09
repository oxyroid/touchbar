package com.oxy.mmr.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.oxy.mmr.wrapper.Shared

@Composable
internal fun VideoAlbum(
    bitmaps: List<ImageBitmap?>,
    onClick: (Shared<ImageBitmap>) -> Unit,
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
        itemsIndexed(bitmaps) { index, nullableBitmap ->
            VideoAlbumItem(nullableBitmap) { bitmap ->
                val actualIndex = index - state.firstVisibleItemIndex
                val info = state.layoutInfo.visibleItemsInfo[actualIndex]
                onClick(
                    Shared.of(
                        offset = info.offset,
                        size = with(info.size) {
                            Size(
                                width.toFloat() / density,
                                height.toFloat() / density
                            )
                        },
                        data = bitmap
                    )
                )
            }
        }
        items(bitmaps.calculatePlaceholderCount(cellsCount)) {
            VideoAlbumPlaceholder()
        }
    }
}

@Composable
internal fun VideoAlbumItem(
    bitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    onClick: (ImageBitmap) -> Unit,
) {
    if (bitmap != null) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(4.dp) then modifier
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        onClick(bitmap)
                    }
            )
        }
    } else {
        VideoAlbumPlaceholder()
    }
}

@Composable
internal fun VideoAlbumPlaceholder(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
            .aspectRatio(1f)
            .then(modifier),
        color = Color.Transparent
    ) {}
}

private fun List<*>.calculatePlaceholderCount(cellsCount: Int): Int =
    if (isEmpty()) 0 else (size % cellsCount) + cellsCount
