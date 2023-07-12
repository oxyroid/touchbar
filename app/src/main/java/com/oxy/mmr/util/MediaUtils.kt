package com.oxy.mmr.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.oxy.mmr.wrapper.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

object MediaUtils {
    fun loadThumbs(
        context: Context,
        uri: Uri?,
        totalCount: Int = 28,
        oneByOne: Boolean = true,
        // target scaled bitmap pixel length
        // param: first int = width, second int = height
        dstWidth: (Int, Int) -> Int = { it, _ -> it },
        dstHeight: (Int, Int) -> Int = { _, it -> it }
    ): Flow<Resource<List<Bitmap?>>> = channelFlow {
        send(Resource.Loading)
        if (uri == null) {
            send(Resource.Failure("Uri is null."))
            return@channelFlow
        }
        try {
            var bitmaps = emptyList<Bitmap?>()
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(context, uri)
                val duration = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toInt() ?: -1

                val deltaTime = duration / totalCount * 1000

                withContext(Dispatchers.IO) {
                    repeat(totalCount) { i ->
                        val bitmap = retriever.getFrameAtTime(
                            (deltaTime.toLong() * i),
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )
                        bitmaps = bitmaps + bitmap?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                dstWidth(it.width, it.height),
                                dstHeight(it.width, it.height),
                                true
                            )
                        }
                        bitmap?.recycle()
                        if (oneByOne) send(Resource.Success(bitmaps))
                    }
                }
                if (!oneByOne) send(Resource.Success(bitmaps))

            }
        } catch (e: Exception) {
            send(Resource.Failure(e.message.orEmpty()))
        }
    }

    suspend fun getDuration(
        context: Context,
        uri: Uri?
    ): Long = coroutineScope {
        MediaMetadataRetriever().use { retriever ->
            withContext(Dispatchers.IO) {
                retriever.setDataSource(context, uri)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                    ?: -1L
            }
        }
    }

    suspend fun recycleUseless(old: List<Bitmap>, new: List<Bitmap>) = coroutineScope {
        withContext(Dispatchers.IO) {
            old.forEach { bitmap ->
                if (bitmap !in new) bitmap.recycle()
            }
        }
    }

    suspend fun recycleNullableUseless(old: List<Bitmap?>, new: List<Bitmap?>) = coroutineScope {
        withContext(Dispatchers.IO) {
            old.forEach { bitmap ->
                if (bitmap !in new) {
                    bitmap?.recycle()
                }
            }
        }
    }
}