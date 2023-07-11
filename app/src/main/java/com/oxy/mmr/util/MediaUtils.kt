package com.oxy.mmr.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.oxy.mmr.wrapper.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object MediaUtils {
    fun loadThumbs(
        context: Context,
        uri: Uri?,
        totalCount: Int = 28,
        oneByOne: Boolean = true
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
                val duration = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: -1

                val deltaTime = duration / totalCount * 1000

                withContext(Dispatchers.IO) {
                    repeat(totalCount) { i ->
                        val bitmap = retriever.getFrameAtTime(
                            (deltaTime.toLong() * i),
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )
                        bitmaps = bitmaps + bitmap
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

    suspend fun compress(bitmap: Bitmap): Bitmap = coroutineScope {
        withContext(Dispatchers.IO) {
            val output = ByteArrayOutputStream()
            output.use { innerOutput ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    0,
                    innerOutput
                )
            }
            ByteArrayInputStream(output.toByteArray()).use {
                BitmapFactory.decodeStream(it)
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