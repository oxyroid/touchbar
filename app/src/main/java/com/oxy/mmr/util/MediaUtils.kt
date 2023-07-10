package com.oxy.mmr.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.oxy.mmr.wrapper.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

object MediaUtils {
    fun loadThumbs(
        context: Context,
        uri: Uri?,
        totalCount: Int = 28
    ): Flow<Resource<List<ImageBitmap?>>> = channelFlow {
        send(Resource.Loading)
        if (uri == null) {
            send(Resource.Failure("Uri is null."))
            return@channelFlow
        }
        try {
            var bitmaps = emptyList<ImageBitmap?>()
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(context, uri)
                val duration = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: -1

                val deltaTime = duration / totalCount * 1000

                withContext(Dispatchers.IO) {
                    repeat(totalCount) { i ->
                        val bitmap = retriever.getFrameAtTime(
                            (deltaTime.toLong() * i),
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )?.asImageBitmap()
                        bitmaps = bitmaps + bitmap
                        send(Resource.Success(bitmaps))
                    }
                }
            }
        } catch (e: Exception) {
            send(Resource.Failure(e.message.orEmpty()))
        }
    }
}